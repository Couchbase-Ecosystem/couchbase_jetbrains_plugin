package com.couchbase.intellij.database.entity;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.collection.ScopeSpec;

import java.util.Set;
import java.util.stream.Collectors;

public class CouchbaseScope implements CouchbaseClusterEntity {
    private ScopeSpec spec;
    private CouchbaseBucket parent;
    private Set<CouchbaseCollection> collections;

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
    public void updateSchema() {
        collections = spec.collections().stream()
                .map(collectionSpec -> new CouchbaseCollection(this, collectionSpec))
                .peek(CouchbaseCollection::updateSchema)
                .collect(Collectors.toSet());
    }

    @Override
    public Cluster getCluster() {
        return parent.getCluster();
    }

    @Override
    public Set<CouchbaseCollection> getChildren() {
        if (collections == null) {
            updateSchema();
        }
        return collections;
    }
}
