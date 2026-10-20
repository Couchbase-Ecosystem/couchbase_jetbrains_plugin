package com.couchbase.intellij.database;

import com.couchbase.intellij.persistence.SavedCluster;
import com.sun.net.httpserver.HttpsServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import org.junit.*;
import javax.net.ssl.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.KeyStore;
import java.util.*;
import static org.junit.Assert.*;

public class ConnectionAuthenticationTest {
    private static Path directory;
    private static KeyStore store;

    @BeforeClass
    public static void certificates() throws Exception {
        directory = Files.createTempDirectory("cb-certificate-auth-");
        keytool("-genkeypair", "-alias", "client", "-keyalg", "RSA", "-dname", "CN=localhost", "-validity", "2",
                "-ext", "SAN=dns:localhost,ip:127.0.0.1", "-ext", "EKU=serverAuth,clientAuth", "-storetype", "PKCS12");
        keytool("-exportcert", "-alias", "client", "-rfc", "-file", directory.resolve("ca.pem").toString());
        store = KeyStore.getInstance(directory.resolve("client.p12").toFile(), "changeit".toCharArray());
    }

    private static void keytool(String... args) throws Exception {
        List<String> command = new ArrayList<>(List.of(Path.of(System.getProperty("java.home"), "bin", "keytool").toString()));
        command.addAll(List.of(args));
        command.addAll(List.of("-keystore", directory.resolve("client.p12").toString(), "-storepass", "changeit", "-noprompt"));
        Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(output, 0, process.waitFor());
    }

    private SavedCluster certificateSettings() {
        SavedCluster settings = new SavedCluster();
        settings.setUrl("couchbases://localhost");
        settings.setSslEnable(true);
        settings.setAuthenticationType("certificate");
        settings.setClientKeyStorePath(directory.resolve("client.p12").toString());
        settings.setServerCaCertificatePath(directory.resolve("ca.pem").toString());
        return settings;
    }

    @Test
    public void authenticatesHttpsWithTheCertificateAndSendsNoBasicCredentials() throws Exception {
        KeyManagerFactory keys = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keys.init(store, "changeit".toCharArray());
        TrustManagerFactory trust = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trust.init(store);
        SSLContext tls = SSLContext.getInstance("TLS");
        tls.init(keys.getKeyManagers(), trust.getTrustManagers(), null);
        HttpsServer server = HttpsServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.setHttpsConfigurator(new HttpsConfigurator(tls) {
            @Override public void configure(HttpsParameters parameters) {
                var options = tls.getDefaultSSLParameters(); options.setNeedClientAuth(true);
                parameters.setSSLParameters(options);
            }
        });
        server.createContext("/identity", exchange -> {
            var secure = (com.sun.net.httpserver.HttpsExchange) exchange;
            String identity = secure.getSSLSession().getPeerPrincipal().getName() + ":" + exchange.getRequestHeaders().getFirst("Authorization");
            byte[] body = identity.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, body.length);
            try (var out = exchange.getResponseBody()) { out.write(body); }
        });
        server.start();
        try {
            URL endpoint = URI.create("https://localhost:" + server.getAddress().getPort() + "/identity").toURL();
            var authentication = ConnectionAuthentication.load(certificateSettings(), "changeit");
            var connection = (HttpsURLConnection) endpoint.openConnection();
            authentication.configureRestConnection(connection);
            try (var in = connection.getInputStream()) {
                assertEquals("CN=localhost:null", new String(in.readAllBytes(), StandardCharsets.UTF_8));
            } finally { connection.disconnect(); }

            SavedCluster untrusted = certificateSettings(); untrusted.setServerCaCertificatePath(null);
            var rejected = (HttpsURLConnection) endpoint.openConnection();
            ConnectionAuthentication.load(untrusted, "changeit").configureRestConnection(rejected);
            try { assertThrows(SSLHandshakeException.class, rejected::getInputStream); }
            finally { rejected.disconnect(); }
        } finally { server.stop(0); }
    }

    @Test public void rejectsIncorrectPasswordAndInsecureCertificateConnections() {
        assertThrows(IllegalArgumentException.class, () -> ConnectionAuthentication.load(certificateSettings(), "wrong-password"));
        SavedCluster insecure = certificateSettings(); insecure.setSslEnable(false);
        assertThrows(IllegalArgumentException.class, () -> ConnectionAuthentication.load(insecure, "changeit"));
    }

    @Test public void supportsPasswordlessJavaKeystores() throws Exception {
        KeyStore jks = KeyStore.getInstance("JKS"); jks.load(null, null);
        jks.setKeyEntry("client", store.getKey("client", "changeit".toCharArray()), new char[0], store.getCertificateChain("client"));
        Path file = directory.resolve("passwordless.jks");
        try (var out = Files.newOutputStream(file)) { jks.store(out, new char[0]); }
        SavedCluster settings = certificateSettings(); settings.setClientKeyStorePath(file.toString());
        assertNotNull(ConnectionAuthentication.load(settings, "").clusterOptions());
    }

    @Test public void legacyConnectionsDefaultToPasswordAuthentication() throws Exception {
        SavedCluster settings = new SavedCluster(); settings.setUsername("user");
        assertFalse(settings.usesCertificateAuthentication());
        var server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            byte[] body = exchange.getRequestHeaders().getFirst("Authorization").getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, body.length);
            try (var out = exchange.getResponseBody()) { out.write(body); }
        });
        server.start();
        try {
            var connection = (HttpURLConnection) URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/").toURL().openConnection();
            ConnectionAuthentication.load(settings, "secret").configureRestConnection(connection);
            assertEquals(10000, connection.getConnectTimeout());
            try (var in = connection.getInputStream()) {
                assertEquals("Basic " + Base64.getEncoder().encodeToString("user:secret".getBytes(StandardCharsets.UTF_8)), new String(in.readAllBytes(), StandardCharsets.UTF_8));
            } finally { connection.disconnect(); }
        } finally { server.stop(0); }
    }

    @AfterClass public static void cleanup() throws Exception {
        if (directory != null) try (var files = Files.walk(directory)) {
            for (Path file : files.sorted(Comparator.reverseOrder()).toList()) Files.deleteIfExists(file);
        }
    }
}
