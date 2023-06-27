package com.couchbase.intellij.tree.node;

import com.intellij.openapi.util.IconLoader;

public class ScopeNodeDescriptor extends NodeDescriptor {

    private String connectionId;
    private String bucket;

    public ScopeNodeDescriptor(String name, String connectionId, String bucket) {
        super(name, IconLoader.getIcon("/assets/icons/collections.svg", ScopeNodeDescriptor.class));
        this.connectionId = connectionId;
        this.bucket = bucket;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }
}
