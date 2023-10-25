package com.couchbase.intellij.tree.cblite;

import com.couchbase.intellij.database.DataLoader;
import com.couchbase.intellij.tree.cblite.nodes.CBLCollectionNodeDescriptor;
import com.couchbase.intellij.tree.node.*;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class CBLTreeExpandListener {

    public static void handle(Tree tree, TreeExpansionEvent event) {
        Object expandedNode = event.getPath().getLastPathComponent();
        if (expandedNode instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode expandedTreeNode = (DefaultMutableTreeNode) expandedNode;


            if (expandedTreeNode.getUserObject() instanceof CBLCollectionNodeDescriptor) {
                CBLDataLoader.listDocuments(expandedTreeNode, tree, 0);
            }
        }
    }
}
