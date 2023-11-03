package com.couchbase.intellij.tree.cblite;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.tree.DefaultMutableTreeNode;

import com.couchbase.intellij.tree.cblite.nodes.CBLCollectionNodeDescriptor;
import com.couchbase.intellij.tree.cblite.nodes.CBLIndexesNodeDescriptor;
import com.intellij.ui.treeStructure.Tree;

public class CBLTreeExpandListener {

    public static void handle(Tree tree, TreeExpansionEvent event) {
        try {
            Object expandedNode = event.getPath().getLastPathComponent();
            if (expandedNode instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode expandedTreeNode = (DefaultMutableTreeNode) expandedNode;
                Object expandedTreeNodeUserObject = expandedTreeNode.getUserObject();

                if (expandedTreeNodeUserObject instanceof CBLCollectionNodeDescriptor) {
                    CBLDataLoader.listDocuments(expandedTreeNode, tree, 0);
                } else if (expandedTreeNodeUserObject instanceof CBLIndexesNodeDescriptor) {
                    CBLDataLoader.listIndexes(expandedTreeNode, tree);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
