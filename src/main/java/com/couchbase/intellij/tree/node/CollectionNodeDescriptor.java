package com.couchbase.intellij.tree.node;

import com.intellij.openapi.util.IconLoader;

public class CollectionNodeDescriptor extends NodeDescriptor {

    private String connectionId;
    private String bucket;
    private String scope;

    private String queryFilter;

    public CollectionNodeDescriptor(String name, String connectionId, String bucket, String scope, String queryFilter) {
        super(name, IconLoader.getIcon("/assets/icons/collection.svg", ScopeNodeDescriptor.class));
        this.connectionId = connectionId;
        this.bucket = bucket;
        this.scope = scope;
        this.queryFilter = queryFilter;
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

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getQueryFilter() {
        return queryFilter;
    }

    public void setQueryFilter(String queryFilter) {
        this.queryFilter = queryFilter;
    }
}
