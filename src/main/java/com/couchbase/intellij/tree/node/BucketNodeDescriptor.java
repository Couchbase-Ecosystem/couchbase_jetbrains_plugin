package com.couchbase.intellij.tree.node;

import com.intellij.openapi.util.IconLoader;

public class BucketNodeDescriptor extends NodeDescriptor {

    private String connectionId;

    public BucketNodeDescriptor(String name, String connectionId) {
        super(name, IconLoader.findIcon("assets/icons/bucket.svg", ConnectionNodeDescriptor.class,
                false, true));
        this.connectionId = connectionId;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }
}
