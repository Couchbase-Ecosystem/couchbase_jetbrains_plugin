package com.couchbase.intellij.database;

import com.couchbase.client.core.env.CertificateAuthenticator;
import com.couchbase.client.core.env.PasswordAuthenticator;
import com.couchbase.client.java.ClusterOptions;
import com.couchbase.intellij.persistence.SavedCluster;

import javax.net.ssl.*;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;

/** One authentication configuration for both the SDK and the explorer's REST requests. */
public final class ConnectionAuthentication {
    private final SavedCluster settings;
    private final String secret;
    private final KeyManagerFactory keyManagers;
    private final SSLSocketFactory sslSocketFactory;

    private ConnectionAuthentication(SavedCluster settings, String secret, KeyManagerFactory keys,
                                     SSLSocketFactory sockets) {
        this.settings = settings;
        this.secret = secret;
        this.keyManagers = keys;
        this.sslSocketFactory = sockets;
    }

    public static ConnectionAuthentication load(SavedCluster settings, String secret) {
        if (!settings.usesCertificateAuthentication()) {
            return new ConnectionAuthentication(settings, secret, null, null);
        }
        if (!settings.isSslEnable() || !settings.getUrl().startsWith("couchbases://")) {
            throw new IllegalArgumentException("Client certificate authentication requires a secure couchbases:// connection.");
        }
        try {
            char[] password = secret == null ? new char[0] : secret.toCharArray();
            KeyStore store;
            try {
                store = KeyStore.getInstance(Path.of(settings.getClientKeyStorePath()).toFile(), password);
                int keys = 0;
                for (String alias : Collections.list(store.aliases())) {
                    if (store.isKeyEntry(alias)) {
                        keys++;
                        if (!(store.getKey(alias, password) instanceof java.security.PrivateKey)) {
                            throw new IllegalArgumentException("The keystore must contain a client certificate and its private key.");
                        }
                        var chain = store.getCertificateChain(alias);
                        if (chain == null || chain.length == 0) {
                            throw new IllegalArgumentException("The keystore has no client certificate chain.");
                        }
                        for (var certificate : chain) ((X509Certificate) certificate).checkValidity();
                    }
                }
                if (keys != 1) {
                    throw new IllegalArgumentException("Choose a keystore containing exactly one client private key and its certificate chain.");
                }
                KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                kmf.init(store, password);
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                if (settings.getServerCaCertificatePath() == null || settings.getServerCaCertificatePath().isBlank()) {
                    tmf.init((KeyStore) null);
                } else {
                    KeyStore trust = KeyStore.getInstance(KeyStore.getDefaultType());
                    trust.load(null, null);
                    try (InputStream in = Files.newInputStream(Path.of(settings.getServerCaCertificatePath()))) {
                        var certificates = CertificateFactory.getInstance("X.509").generateCertificates(in);
                        if (certificates.isEmpty()) throw new IllegalArgumentException("The server CA file contains no certificates.");
                        int index = 0;
                        for (var certificate : certificates) trust.setCertificateEntry("ca-" + index++, certificate);
                    }
                    tmf.init(trust);
                }
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
                return new ConnectionAuthentication(settings, secret, kmf, context.getSocketFactory());
            } finally {
                Arrays.fill(password, '\0');
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not open the client keystore or server CA. Check the files, keystore password, and certificate validity.", e);
        }
    }

    public ClusterOptions clusterOptions() {
        if (!settings.usesCertificateAuthentication()) {
            return settings.getLDAP()
                    ? ClusterOptions.clusterOptions(PasswordAuthenticator.ldapCompatible(settings.getUsername(), secret))
                    : ClusterOptions.clusterOptions(settings.getUsername(), secret);
        }
        return ClusterOptions.clusterOptions(CertificateAuthenticator.fromKeyManagerFactory(() -> keyManagers))
                .environment(env -> env.securityConfig(security -> {
                    security.enableTls(true);
                    if (settings.getServerCaCertificatePath() != null && !settings.getServerCaCertificatePath().isBlank()) {
                        security.trustCertificate(Path.of(settings.getServerCaCertificatePath()));
                    }
                }));
    }

    public void configureRestConnection(HttpURLConnection connection) {
        if (settings.usesCertificateAuthentication()) {
            if (!(connection instanceof HttpsURLConnection secure)) {
                throw new IllegalArgumentException("Client certificates require HTTPS.");
            }
            secure.setSSLSocketFactory(sslSocketFactory);
        } else {
            String credentials = settings.getUsername() + ":" + secret;
            connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder()
                    .encodeToString(credentials.getBytes(StandardCharsets.UTF_8)));
        }
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(30000);
    }
}
