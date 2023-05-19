package com.couchbase.intellij.tree;

import com.intellij.openapi.util.IconLoader;

public class ScopeNodeDescriptor extends NodeDescriptor {

    public ScopeNodeDescriptor(String name) {
        super(name,  IconLoader.findIcon("./assets/icons/scope.svg", ScopeNodeDescriptor.class,
                false, true));
    }
}
