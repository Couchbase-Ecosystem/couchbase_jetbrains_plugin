package com.couchbase.intellij.tree;

import com.intellij.openapi.util.IconLoader;

public class CollectionsNodeDescriptor extends NodeDescriptor {

    public CollectionsNodeDescriptor() {
        super("Collections", IconLoader.findIcon("./assets/icons/collections.svg", ScopeNodeDescriptor.class,
                false, true));
    }
}
