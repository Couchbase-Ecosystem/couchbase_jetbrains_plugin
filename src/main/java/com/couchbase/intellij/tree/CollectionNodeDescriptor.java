package com.couchbase.intellij.tree;

import com.intellij.openapi.util.IconLoader;

public class CollectionNodeDescriptor extends NodeDescriptor {

    public CollectionNodeDescriptor(String name) {
        super(name, IconLoader.findIcon("./assets/icons/collection.svg", ScopeNodeDescriptor.class,
                false, true));
    }
}
