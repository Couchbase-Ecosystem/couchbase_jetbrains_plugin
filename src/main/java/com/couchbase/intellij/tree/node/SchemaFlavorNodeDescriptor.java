package com.couchbase.intellij.tree.node;

import com.couchbase.intellij.tree.TooltipNodeDescriptor;

public class SchemaFlavorNodeDescriptor extends TooltipNodeDescriptor {

    public SchemaFlavorNodeDescriptor(String schemaText, String tooltip) {
        super( "<html><strong>"+schemaText+"</strong></html>", tooltip);
    }
}
