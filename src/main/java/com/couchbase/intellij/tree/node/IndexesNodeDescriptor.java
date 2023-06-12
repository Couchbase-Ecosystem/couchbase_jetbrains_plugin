package com.couchbase.intellij.tree.node;

import com.intellij.openapi.util.IconLoader;

public class IndexesNodeDescriptor extends NodeDescriptor {

    public IndexesNodeDescriptor() {
        super("Indexes", IconLoader.findIcon("./assets/icons/indexes.svg", ScopeNodeDescriptor.class,
                false, true));
    }
}
