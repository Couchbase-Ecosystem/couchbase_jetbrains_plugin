package com.couchbase.intellij.tree.node;

import com.couchbase.intellij.tree.NodeDescriptor;
import com.intellij.openapi.util.IconLoader;


public class SchemaNodeDescriptor extends NodeDescriptor {
    public SchemaNodeDescriptor() {
        super("Schema", IconLoader.findIcon("./assets/icons/schema.svg", ScopeNodeDescriptor.class,
                false, true));
    }
}
