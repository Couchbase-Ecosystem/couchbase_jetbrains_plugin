package com.couchbase.intellij.tree.iq;

import com.couchbase.intellij.tree.iq.settings.OpenAISettingsState;
import com.couchbase.intellij.tree.iq.ui.CouchbaseIQPanel;
import com.couchbase.intellij.tree.iq.ui.MessageComponent;
import com.obiscr.OpenAIProxy;
import okhttp3.Authenticator;
import okhttp3.Credentials;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.Proxy;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 *
 */
public abstract class AbstractHandler {

    protected X509TrustManager trustManager;

    public static Proxy getProxy() {
        Proxy proxy;
        OpenAISettingsState instance = OpenAISettingsState.getInstance();
        if (!instance.enableProxy) {
            return null;
        }
        switch (instance.proxyType) {
            case HTTP:
                proxy = new OpenAIProxy(instance.proxyHostname,
                        Integer.parseInt(instance.proxyPort),
                        Proxy.Type.HTTP).build();
                break;
            case SOCKS:
                proxy = new OpenAIProxy(instance.proxyHostname,
                        Integer.parseInt(instance.proxyPort),
                        Proxy.Type.SOCKS).build();
                break;
            case DIRECT:
                proxy = new OpenAIProxy(instance.proxyHostname,
                        Integer.parseInt(instance.proxyPort),
                        Proxy.Type.DIRECT).build();
                break;
            default:
                proxy = null;
                break;
        }
        return proxy;
    }

    public abstract  <T> T handle(CouchbaseIQPanel couchbaseIQPanel, MessageComponent component, String question);

    protected Authenticator getProxyAuth() {
        OpenAISettingsState state = OpenAISettingsState.getInstance();
        return (route, response) -> {
            String credential = Credentials.basic(state.proxyUsername, state.proxyPassword);
            return response.request().newBuilder()
                    .header("Proxy-Authorization", credential)
                    .build();
        };
    }
    public HostnameVerifier getHostNameVerifier() {
        return (hostname, session) -> true;
    }

    public SSLContext getSslContext() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, new TrustManager[] { getTrustAllManager() }, new java.security.SecureRandom());
        return sslContext;
    }

    public TrustManager getTrustAllManager() {
        if (trustManager != null) {
            return trustManager;
        }
        trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }
        };
        return trustManager;
    }
}
