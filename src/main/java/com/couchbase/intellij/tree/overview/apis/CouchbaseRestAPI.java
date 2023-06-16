package com.couchbase.intellij.tree.overview.apis;

import com.couchbase.intellij.database.ActiveCluster;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.List;

public class CouchbaseRestAPI {

    public static ServerOverview getOverview() throws Exception {

        String content = callEndpoint("/pools/nodes", ActiveCluster.getInstance().getClusterURL(),
                ActiveCluster.getInstance().isSSLEnabled(), ActiveCluster.getInstance().getUsername(), ActiveCluster.getInstance().getPassword());
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.fromJson(content, ServerOverview.class);
    }

    public static BucketOverview getBucketOverview(String bucket) throws Exception {

        String content = callEndpoint("/pools/default/buckets/" + bucket, ActiveCluster.getInstance().getClusterURL(),
                ActiveCluster.getInstance().isSSLEnabled(), ActiveCluster.getInstance().getUsername(), ActiveCluster.getInstance().getPassword());
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.fromJson(content, BucketOverview.class);
    }

    private static String callEndpoint(String endpoint, String cbURL, boolean isSSL, String username, String password) throws Exception {

        List<String> servers = NSLookup.getServerURL(cbURL);

        String userpass = username + ":" + password;
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));

        String urlContent = (isSSL ? "https://" : "http://") + servers.get(0) + ":" + (isSSL ? "18091" : "8091") + endpoint;
        URL url = new URL(urlContent);
        InputStream stream;

        if (isSSL) {
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[0];
                        }

                        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", basicAuth);
            stream = conn.getInputStream();
        } else {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", basicAuth);
            stream = conn.getInputStream();
        }


        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        return content.toString();
    }
}
