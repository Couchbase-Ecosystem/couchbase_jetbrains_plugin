package com.couchbase.intellij.database.entity;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;

import java.util.Comparator;
import java.util.Set;

public class CouchbaseDocumentFlavor implements CouchbaseClusterEntity {
    private CouchbaseCollection parent;
    private JsonObject properties;
    private final long sampleSize;

    private Set<CouchbaseField> children;

    public CouchbaseDocumentFlavor(CouchbaseCollection parent, JsonObject properties) {
        this.parent = parent;
        this.properties = properties;
        children = CouchbaseField.fromObject(this, null, properties);
        Object docs = properties.get("#docs");
        if (docs instanceof JsonArray) {
            this.sampleSize = ((JsonArray) docs).toList().stream()
                    .filter(i -> i instanceof Integer)
                    .map(l -> (Integer) l)
                    .max(Comparator.naturalOrder())
                    .orElse(0);
        } else {
            this.sampleSize = properties.getLong("#docs");
        }
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public CouchbaseClusterEntity getParent() {
        return parent;
    }

    @Override
    public void updateSchema() {
        // noop
    }

    @Override
    public Cluster getCluster() {
        return parent.getCluster();
    }

    @Override
    public Set<CouchbaseField> getChildren() {
        return children;
    }

    public Long getSampleSize() {
        return sampleSize;
    }

    public JsonObject generateDocument() {
        JsonObject result = JsonObject.create();
        getChildren().forEach(field -> field.addZeroValue(result));
        return result;
    }

    public JsonObject toJson() {
        JsonObject result = JsonObject.create();
        if (children != null) {
            children.forEach(child -> result.put(child.getName(), child.toJson()));
        }
        return result;
    }
}
