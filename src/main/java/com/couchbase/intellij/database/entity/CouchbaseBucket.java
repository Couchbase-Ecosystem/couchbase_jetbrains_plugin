package com.couchbase.intellij.database.entity;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.intellij.database.ActiveCluster;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CouchbaseBucket implements CouchbaseClusterEntity {
    private String name;
    private ActiveCluster parent;

    public CouchbaseBucket(ActiveCluster cluster, String name) {
        this.parent = cluster;
        this.name = name;
    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public CouchbaseClusterEntity getParent() {
        return parent;
    }

    @Override
    public Cluster getCluster() {
        return parent.getCluster();
    }

    @Override
    public Set<CouchbaseScope> getChildren() {
        return getCluster().bucket(name).collections().getAllScopes().stream()
                .map(scopeSpec -> new CouchbaseScope(this, scopeSpec))
                .collect(Collectors.toSet());
    }
}
