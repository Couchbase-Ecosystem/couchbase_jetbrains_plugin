package com.couchbase.intellij.tree.cblite;

import com.couchbase.intellij.tree.cblite.nodes.CBLCollectionNodeDescriptor;
import com.couchbase.intellij.tree.cblite.nodes.CBLIndexesNodeDescriptor;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.tree.DefaultMutableTreeNode;

public class CBLTreeExpandListener {

    public static void handle(Tree tree, TreeExpansionEvent event) {
        Object expandedNode = event.getPath().getLastPathComponent();
        if (expandedNode instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode expandedTreeNode = (DefaultMutableTreeNode) expandedNode;


            if (expandedTreeNode.getUserObject() instanceof CBLCollectionNodeDescriptor) {
                CBLDataLoader.listDocuments(expandedTreeNode, tree, 0);
            } else if (expandedTreeNode.getUserObject() instanceof CBLIndexesNodeDescriptor) {
                CBLDataLoader.listIndexes(expandedTreeNode, tree);
            }
        }
    }
}
