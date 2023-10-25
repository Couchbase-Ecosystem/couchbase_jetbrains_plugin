package com.couchbase.intellij.tree.cblite.nodes;

import com.couchbase.intellij.tree.node.NodeDescriptor;
import com.couchbase.intellij.tree.node.ScopeNodeDescriptor;
import com.intellij.openapi.util.IconLoader;
import lombok.Getter;

public class CBLCollectionNodeDescriptor extends NodeDescriptor {

    @Getter
    private String scope;
    public CBLCollectionNodeDescriptor(String text, String scope) {
        super(text, IconLoader.getIcon("/assets/icons/collection.svg", CBLScopeNodeDescriptor.class));
        this.scope = scope;
    }
}
