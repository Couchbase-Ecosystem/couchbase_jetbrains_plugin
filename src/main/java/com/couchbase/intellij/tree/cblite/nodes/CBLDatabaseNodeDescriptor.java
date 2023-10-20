package com.couchbase.intellij.tree.cblite.nodes;

import com.couchbase.intellij.tree.node.ConnectionNodeDescriptor;
import com.couchbase.intellij.tree.node.NodeDescriptor;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public class CBLDatabaseNodeDescriptor extends NodeDescriptor {
    public CBLDatabaseNodeDescriptor(String text, boolean isActive) {
        super(text, isActive ? IconLoader.getIcon("/assets/icons/couchbase-active.svg", CBLDatabaseNodeDescriptor.class) : IconLoader.getIcon("/assets/icons/couchbase.svg", CBLDatabaseNodeDescriptor.class));
    }
}
