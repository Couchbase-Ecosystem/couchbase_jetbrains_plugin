package com.couchbase.intellij.database;

import com.couchbase.client.java.Cluster;

public class ActiveCluster {


    private static Cluster cluster;
    private static String clusterId;

    public static Cluster get() {
        return cluster;
    }
    public static String id() {
        return clusterId;
    }

    public static void set(Cluster cluster, String clusterId) {
        ActiveCluster.cluster = cluster;
        ActiveCluster.clusterId = clusterId;
    }
}
