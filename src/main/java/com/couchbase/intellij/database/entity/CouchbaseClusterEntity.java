package com.couchbase.intellij.database.entity;

import com.couchbase.client.java.Cluster;

import java.util.Set;

public interface CouchbaseClusterEntity {
    String getName();

    CouchbaseClusterEntity getParent();

    void updateSchema();

    Cluster getCluster();

    Set<? extends CouchbaseClusterEntity> getChildren();

    default CouchbaseClusterEntity getRoot() {
        CouchbaseClusterEntity parent = getParent();
        return parent == null ? this : parent;
    }
}
