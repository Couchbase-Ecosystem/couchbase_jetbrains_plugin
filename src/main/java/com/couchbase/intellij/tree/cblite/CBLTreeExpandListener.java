package com.couchbase.intellij.tree.cblite;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.tree.DefaultMutableTreeNode;

import com.couchbase.intellij.tree.cblite.nodes.CBLCollectionNodeDescriptor;
import com.couchbase.intellij.tree.cblite.nodes.CBLDatabaseNodeDescriptor;
import com.couchbase.intellij.tree.cblite.nodes.CBLScopeNodeDescriptor;
import com.intellij.ui.treeStructure.Tree;

public class CBLTreeExpandListener {

    public static void handle(Tree tree, TreeExpansionEvent event) {
        try {
            Object expandedNode = event.getPath().getLastPathComponent();
            if (expandedNode instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode expandedTreeNode = (DefaultMutableTreeNode) expandedNode;
                Object userObject = expandedTreeNode.getUserObject();

                if (userObject instanceof CBLDatabaseNodeDescriptor) {
                    CBLDataLoader.listScopes(expandedTreeNode);
                } else if (userObject instanceof CBLScopeNodeDescriptor) {
                    CBLDataLoader.listCollections(expandedTreeNode, ((CBLScopeNodeDescriptor) userObject).getText());
                } else if (userObject instanceof CBLCollectionNodeDescriptor) {
                    CBLDataLoader.listDocuments(expandedTreeNode, tree, 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
