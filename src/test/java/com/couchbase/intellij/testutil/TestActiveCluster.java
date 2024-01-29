package com.couchbase.intellij.testutil;

import com.couchbase.client.java.Cluster;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.entity.CouchbaseClusterEntity;

import java.util.List;
import java.util.stream.Stream;

public class TestActiveCluster extends ActiveCluster {

    private Cluster cluster;

    public TestActiveCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    @Override
    public Cluster get() {
        return cluster;
    }
}
