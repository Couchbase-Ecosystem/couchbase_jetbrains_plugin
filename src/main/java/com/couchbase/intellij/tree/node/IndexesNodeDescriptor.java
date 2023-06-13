package com.couchbase.intellij.tree.node;

import com.intellij.openapi.util.IconLoader;

public class IndexesNodeDescriptor extends NodeDescriptor {

    private String bucket;
    private String scope;
    private String collection;

    public IndexesNodeDescriptor(String bucket, String scope, String collection) {
        super("Indexes", IconLoader.findIcon("./assets/icons/indexes.svg"));
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
