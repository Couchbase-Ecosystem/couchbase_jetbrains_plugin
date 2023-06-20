package com.couchbase.intellij.tree.overview.apis;

import com.couchbase.client.core.deps.com.google.common.reflect.TypeToken;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CouchbaseRestAPI {


    public static Map<String, IndexStats> getIndexStats(String bucket, String scope, String collection, String indexName) throws Exception {

        String key = bucket;
        if ("_default".equals(scope) && "_default".equals(collection)) {
            key += ":" + indexName;
        } else {
            key += ":" + scope + ":" + collection + ":" + indexName;
        }

        List<String> calls = callAllEndpoints((ActiveCluster.getInstance().isSSLEnabled() ? "19102" : "9102") + "/api/v1/stats/" + bucket + "." + scope + "." + collection, ActiveCluster.getInstance().getClusterURL(),
                ActiveCluster.getInstance().isSSLEnabled(), ActiveCluster.getInstance().getUsername(), ActiveCluster.getInstance().getPassword());
        Gson gson = new GsonBuilder().create();

        Map<String, IndexStats> result = new HashMap<>();
        for (String jsonString : calls) {

            if (jsonString != null) {
                Map<String, IndexStats> map = gson.fromJson(jsonString, new TypeToken<Map<String, IndexStats>>() {
                }.getType());

                for (Map.Entry<String, IndexStats> entry : map.entrySet()) {
                    //the index might contain replicas which will look like "travel-sample:def_sourceairport (replica 1)"
                    if (key.equals(entry.getKey()) || entry.getKey().contains(key + " ")) {
                        result.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }

        return result;
    }

    public static ServerOverview getOverview() throws Exception {

        String content = callSingleEndpoint((ActiveCluster.getInstance().isSSLEnabled() ? "18091" : "8091") + "/pools/nodes", ActiveCluster.getInstance().getClusterURL(),
                ActiveCluster.getInstance().isSSLEnabled(), ActiveCluster.getInstance().getUsername(), ActiveCluster.getInstance().getPassword());
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.fromJson(content, ServerOverview.class);
    }

    public static BucketOverview getBucketOverview(String bucket) throws Exception {

        String content = callSingleEndpoint((ActiveCluster.getInstance().isSSLEnabled() ? "18091" : "8091") + "/pools/default/buckets/" + bucket, ActiveCluster.getInstance().getClusterURL(),
                ActiveCluster.getInstance().isSSLEnabled(), ActiveCluster.getInstance().getUsername(), ActiveCluster.getInstance().getPassword());
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.fromJson(content, BucketOverview.class);
    }

    private static String callSingleEndpoint(String endpoint, String cbURL, boolean isSSL, String username, String password) throws Exception {
        List<String> servers = NSLookup.getServerURL(cbURL);
        return callEndpoint(endpoint, servers.get(0), isSSL, username, password);
    }

    private static List<String> callAllEndpoints(String endpoint, String cbURL, boolean isSSL, String username, String password) throws Exception {
        List<String> servers = NSLookup.getServerURL(cbURL);
        List<CompletableFuture<String>> futureList = servers.parallelStream()
                .map(key -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return callEndpoint(endpoint, key, isSSL, username, password);
                    } catch (Exception e) {
                        return null;
                    }
                }))
                .collect(Collectors.toList());

        return futureList.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

    }

    private static String callEndpoint(String endpoint, String serverURL, boolean isSSL, String username, String password) throws Exception {

        String userpass = username + ":" + password;
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));

        String urlContent = (isSSL ? "https://" : "http://") + serverURL + ":" + endpoint;
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
