package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.DataLoader;
import com.couchbase.intellij.tree.node.*;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.Enumeration;

public class TreeExpandListener implements TreeExpansionListener {
    private final Tree tree;

    public TreeExpandListener(Tree tree) {
        this.tree = tree;
    }

    @Override
    public void treeExpanded(TreeExpansionEvent event) {
        handle(tree, event);
    }

    @Override
    public void treeCollapsed(TreeExpansionEvent event) {
        handleCollapse(tree, event);
    }

    public static void handleCollapse(Tree tree, TreeExpansionEvent event) {
        Object collapsed = event.getPath().getLastPathComponent();
        if (!(collapsed instanceof DefaultMutableTreeNode node)) {
            return;
        }
        if (node.getUserObject() instanceof CollectionsNodeDescriptor) {
            resetCollections(tree, node);
        } else if (node.getUserObject() instanceof ScopeNodeDescriptor) {
            for (int i = 0; i < node.getChildCount(); i++) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
                if (child.getUserObject() instanceof CollectionsNodeDescriptor) {
                    resetCollections(tree, child);
                }
            }
        }
    }

    private static void resetCollections(Tree tree, DefaultMutableTreeNode node) {
        TreeLoadTask.invalidate(node, tree);
        ((CollectionsNodeDescriptor) node.getUserObject()).resetForExpansion();
        node.removeAllChildren();
        node.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
        ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(node);
    }

    public static void handle(Tree tree, TreeExpansionEvent event) {
        Object expandedNode = event.getPath().getLastPathComponent();
        if (expandedNode instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode expandedTreeNode = (DefaultMutableTreeNode) expandedNode;

            if (expandedTreeNode.getUserObject() instanceof ConnectionNodeDescriptor) {
                DataLoader.listBuckets(expandedTreeNode, tree);
            } else if (expandedTreeNode.getUserObject() instanceof BucketNodeDescriptor) {
                DataLoader.listScopes(expandedTreeNode, tree);
            } else if (expandedTreeNode.getUserObject() instanceof CollectionsNodeDescriptor) {
                DataLoader.listCollections(expandedTreeNode, tree);
            } else if (expandedTreeNode.getUserObject() instanceof CollectionNodeDescriptor) {
                DataLoader.listDocuments(expandedTreeNode, tree, 0);
            } else if (expandedTreeNode.getUserObject() instanceof SchemaNodeDescriptor) {
                DataLoader.showSchema(expandedTreeNode, (DefaultTreeModel) tree.getModel(), tree);
            } else if (expandedTreeNode.getUserObject() instanceof TooltipNodeDescriptor) {
                // Do Nothing
            } else if (expandedTreeNode.getUserObject() instanceof ScopeNodeDescriptor) {
                Enumeration<?> children = expandedTreeNode.children();
                while (children.hasMoreElements()) {
                    DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.nextElement();
                    if (child.getUserObject() instanceof CollectionsNodeDescriptor) {
                        ApplicationManager.getApplication().invokeLater(() -> {
                            if (tree.isExpanded(event.getPath()) && child.getParent() == expandedTreeNode) {
                                TreePath path = new TreePath(child.getPath());
                                tree.expandPath(path);
                            }
                        });
                        break;
                    }
                }

            } else if (expandedTreeNode.getUserObject() instanceof IndexesNodeDescriptor) {
                DataLoader.listIndexes(expandedTreeNode, tree);
            } else if (expandedTreeNode.getUserObject() instanceof SearchNodeDescriptor) {
                DataLoader.listSearchIndexes(expandedTreeNode, tree);
            } else {
                Log.debug("Tree not implemented yet " + expandedTreeNode.getUserObject());
            }
        }
    }
}
