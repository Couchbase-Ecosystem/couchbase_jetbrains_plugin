package com.couchbase.intellij.tree;

public class SchemaDataNodeDescriptor extends NodeDescriptor {

    public SchemaDataNodeDescriptor() {
        super("SchemaData", null, ScopeNodeDescriptor.class,
                false, true));
    }
}
