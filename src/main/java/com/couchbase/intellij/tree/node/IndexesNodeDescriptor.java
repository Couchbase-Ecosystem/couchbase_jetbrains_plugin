package com.couchbase.intellij.tree.node;

import com.intellij.openapi.util.IconLoader;

public class IndexesNodeDescriptor extends NodeDescriptor {

    private final String bucket;
    private final String scope;
    private final String collection;

    public IndexesNodeDescriptor(String bucket, String scope, String collection) {
        super("Indexes", IconLoader.getIcon("/assets/icons/indexes.svg", IndexesNodeDescriptor.class));
        this.bucket = bucket;
        this.scope = scope;
        this.collection = collection;
    }

    public String getBucket() {
        return bucket;
    }

    public String getScope() {
        return scope;
    }

    public String getCollection() {
        return collection;
    }
}
