package com.couchbase.intellij.database.entity;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;

import java.util.Set;
import java.util.stream.Collectors;

public class CouchbaseDocumentFlavor implements CouchbaseClusterEntity {
    private CouchbaseCollection parent;
    private JsonObject properties;

    public CouchbaseDocumentFlavor(CouchbaseCollection parent, JsonObject properties) {
        this.parent = parent;
        this.properties = properties;
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
    public Cluster getCluster() {
        return parent.getCluster();
    }

    @Override
    public Set<CouchbaseField> getChildren() {
        return CouchbaseField.fromObject(this, null, properties);
    }
}
