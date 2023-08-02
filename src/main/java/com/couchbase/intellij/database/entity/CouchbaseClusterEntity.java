package com.couchbase.intellij.database.entity;

import com.couchbase.client.java.Cluster;
import com.couchbase.intellij.persistence.SavedCluster;

import java.util.List;
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

    default String path() {
        return getParent() == null ? getName() :
                String.format("%s.%s", getParent().getName(), getName());
    }

    default List<String> pathElements() {
        List<String> path = getParent().pathElements();
        path.add(getName());
        return path;
    }

    default SavedCluster getSavedCluster() {
        return getParent().getSavedCluster();
    }
}
