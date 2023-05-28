package com.couchbase.intellij.tree.node;

import com.couchbase.intellij.tree.NodeDescriptor;
import com.intellij.openapi.util.IconLoader;

public class LoadMoreNodeDescriptor extends NodeDescriptor {

    private String bucket;
    private String scope;
    private String collection;
    private int newOffset;

    public LoadMoreNodeDescriptor(String bucket, String scope, String collection, int newOffset) {
        super("Load More", IconLoader.findIcon("./assets/icons/double-chevron-down.svg"));
        this.bucket = bucket;
        this.scope = scope;
        this.collection = collection;
        this.newOffset = newOffset;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public int getNewOffset() {
        return newOffset;
    }

    public void setNewOffset(int newOffset) {
        this.newOffset = newOffset;
    }
}
