package com.couchbase.intellij.tree.node;

import com.couchbase.intellij.color.ColorHelper;
import com.couchbase.intellij.tree.TooltipNodeDescriptor;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.JBColor;

public class SchemaDataNodeDescriptor extends TooltipNodeDescriptor {

    public SchemaDataNodeDescriptor(String key, String value, String tooltip) {
        super("<html><strong style='color: "+ ColorHelper.getKeywordColor() +"'>"+key+":</strong> "+value+"</html>", tooltip);
    }
    public SchemaDataNodeDescriptor(String key) {
        super("<html><strong>"+key+"</strong></html>", null);
    }
}
