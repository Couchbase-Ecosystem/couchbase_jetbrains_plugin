package com.couchbase.intellij.tree.node;

import com.intellij.openapi.util.IconLoader;
import lombok.Getter;
import lombok.Setter;

public class CollectionsNodeDescriptor extends CounterNodeDescriptor {

    @Setter
    @Getter
    private String bucket;

    @Getter
    private String scope;

    public CollectionsNodeDescriptor(String scope, String bucket) {
        super("Collections", IconLoader.getIcon("/assets/icons/collections.svg", CollectionsNodeDescriptor.class));
        this.bucket = bucket;
        this.scope = scope;
    }
}
