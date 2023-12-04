package com.couchbase.intellij.tree.node;

import utils.ColorHelper;

public class SchemaDataArrayNodeDescriptor extends TooltipNodeDescriptor {

    private String path;

    public SchemaDataArrayNodeDescriptor(String key, String value, String tooltip, String path) {
        super("<html><strong style='color: " + ColorHelper.getKeywordColor() + "'>" + key + ":</strong> " + value + "</html>", tooltip);
        this.path = path;
    }

    public SchemaDataArrayNodeDescriptor(String key, String path) {
        super("<html><strong>" + key + "</strong></html>", null);
        this.path = path;
    }
}
