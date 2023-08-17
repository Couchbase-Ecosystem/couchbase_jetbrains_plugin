package com.couchbase.intellij.tree.node;

import com.intellij.openapi.util.IconLoader;

public class MissingIndexFootNoteNodeDescriptor extends NodeDescriptor {

    private String bucket;
    private String scope;
    private String collection;

    public MissingIndexFootNoteNodeDescriptor(String bucket, String scope, String collection) {
        super("<html><small style='font-size: 80%'>You can't query or apply filters on this collection until an index is created.</small</html>",
                IconLoader.getIcon("/assets/icons/empty-icon.svg", MissingIndexFootNoteNodeDescriptor.class));
        this.bucket = bucket;
        this.scope = scope;
        this.collection = collection;
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
}

