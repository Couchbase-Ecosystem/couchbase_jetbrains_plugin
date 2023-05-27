package com.couchbase.intellij.tree;

import com.intellij.openapi.util.IconLoader;

public class SchemaDataNodeDescriptor extends TooltipNodeDescriptor {

    public SchemaDataNodeDescriptor(String key, String value, String tooltip) {
        super("<html><strong>"+key+":</strong> "+value+"</html>", tooltip);
    }
    public SchemaDataNodeDescriptor(String key) {
        super("<html><strong>"+key+"</strong></html>", null);
    }
}
