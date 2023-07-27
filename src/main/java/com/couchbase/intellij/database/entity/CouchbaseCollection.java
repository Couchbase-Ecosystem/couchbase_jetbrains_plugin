package com.couchbase.intellij.database.entity;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.intellij.database.InferHelper;
import com.intellij.openapi.application.ApplicationManager;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CouchbaseCollection implements CouchbaseClusterEntity {
    private CollectionSpec spec;
    private CouchbaseScope parent;
    private Set<CouchbaseDocumentFlavor> children;

    private AtomicBoolean updating = new AtomicBoolean(false);

    public CouchbaseCollection(CouchbaseScope parent, CollectionSpec spec) {
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
        if (!updating.get()) {
            updating.set(true);
            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                try {
                    JsonObject schema = InferHelper.inferSchema(getName(), getParent().getName(), getParent().getParent().getName());
                    if (schema != null) {
                        JsonArray content = schema.getArray("content");
                        children = IntStream.range(0, content.size()).boxed()
                                .map(content::getObject)
                                .map(flavor -> new CouchbaseDocumentFlavor(this, flavor))
                                .peek(CouchbaseDocumentFlavor::updateSchema)
                                .collect(Collectors.toSet());
                    } else {
                        System.err.println("Could not infer the schema for " + getName());
                    }
                } finally {
                    updating.set(false);
                }
            });
        }
    }

    @Override
    public Cluster getCluster() {
        return parent.getCluster();
    }

    @Override
    public Set<CouchbaseDocumentFlavor> getChildren() {
        if (children == null) {
            updateSchema();
        }
        return children;
    }
}
