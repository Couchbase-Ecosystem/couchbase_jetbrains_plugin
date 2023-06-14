package com.couchbase.intellij.tree.node;

public class SchemaFlavorNodeDescriptor extends TooltipNodeDescriptor {

    public SchemaFlavorNodeDescriptor(String schemaText, String tooltip) {
        super("<html><strong>" + schemaText + "</strong></html>", tooltip);
    }
}
