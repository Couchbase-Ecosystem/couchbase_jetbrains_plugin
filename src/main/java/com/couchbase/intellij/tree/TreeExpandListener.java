package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.DataLoader;
import com.couchbase.intellij.tree.node.*;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class TreeExpandListener {

    public static void handle(Tree tree, TreeExpansionEvent event) {
        Object expandedNode = event.getPath().getLastPathComponent();
        if (expandedNode instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode expandedTreeNode = (DefaultMutableTreeNode) expandedNode;

            if (expandedTreeNode.getUserObject() instanceof ConnectionNodeDescriptor) {
                DataLoader.listBuckets(expandedTreeNode, tree);
            } else if (expandedTreeNode.getUserObject() instanceof BucketNodeDescriptor) {
                DataLoader.listScopes(expandedTreeNode, tree);
            } else if (expandedTreeNode.getUserObject() instanceof ScopeNodeDescriptor) {
                DataLoader.listCollections(expandedTreeNode, tree);
            } else if (expandedTreeNode.getUserObject() instanceof CollectionNodeDescriptor) {
                DataLoader.listDocuments(expandedTreeNode, tree, 0);
            } else if (expandedTreeNode.getUserObject() instanceof SchemaNodeDescriptor) {
                DataLoader.showSchema(expandedTreeNode, (DefaultTreeModel) tree.getModel(), tree);
            } else if (expandedTreeNode.getUserObject() instanceof TooltipNodeDescriptor) {
                // Do Nothing
            } else if (expandedTreeNode.getUserObject() instanceof IndexesNodeDescriptor) {
                DataLoader.listIndexes(expandedTreeNode, tree);
            } else {
                throw new UnsupportedOperationException("Not implemented yet");
            }
        }
    }
}
