package com.couchbase.intellij.database.entity;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.collection.ScopeSpec;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CouchbaseScope implements CouchbaseClusterEntity {
    private ScopeSpec spec;
    private CouchbaseBucket parent;

    public CouchbaseScope(CouchbaseBucket parent, ScopeSpec spec) {
        this.parent = parent;
        this.spec = spec;
    }
    @Override
    public String getName() {
        return spec.name();
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
    public Set<CouchbaseCollection> getChildren() {
        return spec.collections().stream()
                .map(collectionSpec -> new CouchbaseCollection(this, collectionSpec))
                .collect(Collectors.toSet());
    }
}
