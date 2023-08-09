package com.couchbase.intellij.database.entity;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;

import java.util.Set;

public class CouchbaseDocumentFlavor implements CouchbaseClusterEntity {
    private CouchbaseCollection parent;
    private JsonObject properties;

    private Set<CouchbaseField> children;

    public CouchbaseDocumentFlavor(CouchbaseCollection parent, JsonObject properties) {
        this.parent = parent;
        this.properties = properties;
        children = CouchbaseField.fromObject(this, null, properties);
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
}
