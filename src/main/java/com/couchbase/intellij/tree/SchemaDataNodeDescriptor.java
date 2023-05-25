package com.couchbase.intellij.tree;

import com.intellij.openapi.util.IconLoader;

public class SchemaDataNodeDescriptor extends NodeDescriptor {

    public SchemaDataNodeDescriptor(String schemaText) {
        super("<html>" +
                schemaText.replace("\n", "<br/>") +
                "</html>"
                ,
                IconLoader.findIcon("./assets/icons/schema.svg", SchemaDataNodeDescriptor.class,
                false, true));
    }
}
