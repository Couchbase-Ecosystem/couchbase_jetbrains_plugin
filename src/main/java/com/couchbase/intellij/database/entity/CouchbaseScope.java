package com.couchbase.intellij.database.entity;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.collection.ScopeSpec;

import java.util.Set;
import java.util.stream.Collectors;

public class CouchbaseScope implements CouchbaseClusterEntity {
    private ScopeSpec spec;
    private CouchbaseBucket parent;
    private volatile Set<CouchbaseCollection> collections;

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

    /**
     * Scopes with at most this many collections get every schema inferred as soon as the scope is read, so
     * completion is instant. Larger scopes (they may hold thousands of collections) infer each collection
     * lazily, on first use, instead of starting one INFER task per collection.
     */
    public static final int EAGER_INFER_LIMIT = 20;

    @Override
    public void updateSchema() {
        Set<CouchbaseCollection> fresh = spec.collections().stream()
                .map(collectionSpec -> new CouchbaseCollection(this, collectionSpec))
                .collect(Collectors.toSet());
        if (fresh.size() <= EAGER_INFER_LIMIT) {
            fresh.forEach(CouchbaseCollection::updateSchema);
        }
        collections = fresh;
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

    /** Metadata already loaded by the schema worker; never starts schema inference. */
    public Set<CouchbaseCollection> getCachedChildren() {
        Set<CouchbaseCollection> snapshot = collections;
        return snapshot == null ? Set.of() : snapshot;
    }
}
