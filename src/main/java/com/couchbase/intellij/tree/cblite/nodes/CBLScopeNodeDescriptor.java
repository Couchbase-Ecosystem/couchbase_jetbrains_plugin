package com.couchbase.intellij.tree.cblite.nodes;

import com.couchbase.intellij.tree.node.NodeDescriptor;
import com.intellij.openapi.util.IconLoader;

public class CBLScopeNodeDescriptor extends NodeDescriptor {

    public CBLScopeNodeDescriptor(String text) {
        super(text, IconLoader.getIcon("/assets/icons/scope.svg", CBLScopeNodeDescriptor.class));
    }
}
