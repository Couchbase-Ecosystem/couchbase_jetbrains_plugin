package com.couchbase.intellij.tree.node;

import lombok.Getter;
import utils.ColorHelper;

public class SchemaDataObjectNodeDescriptor extends TooltipNodeDescriptor {

    @Getter
    private String path;

    public SchemaDataObjectNodeDescriptor(String key, String value, String tooltip, String path) {
        super("<html><strong style='color: " + ColorHelper.getKeywordColor() + "'>" + key + ":</strong> " + value + "</html>", tooltip);
        this.path = path;
    }

    public SchemaDataObjectNodeDescriptor(String key, String path) {
        super("<html><strong>" + key + "</strong></html>", null);
        this.path = path;
    }
}
