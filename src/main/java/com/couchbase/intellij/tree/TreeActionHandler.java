package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.DataLoader;
import com.couchbase.intellij.persistence.SavedCluster;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TreeActionHandler {

    public static void connectToCluster(SavedCluster savedCluster, Tree tree) {
        tree.setPaintBusy(true);
        SwingUtilities.invokeLater(() -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
            Enumeration children = selectedNode.children();
            DefaultMutableTreeNode newActiveNode = null;

            //Disconnect First and find the node that we want to connect to
            while (children.hasMoreElements()) {
                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) children.nextElement();
                if (childNode.getUserObject() instanceof ConnectionNodeDescriptor) {
                    ConnectionNodeDescriptor con = (ConnectionNodeDescriptor) childNode.getUserObject();
                    if (con.isActive()) {
                        disconnectFromCluster(childNode, con, tree);
                        break;
                    }
                    if (con.getSavedCluster().getId().equals(savedCluster.getId())) {
                        newActiveNode = childNode;
                    }
                }


            }

            try {
                ActiveCluster.getInstance().connect(savedCluster);
            } catch (Exception e) {
                Messages.showErrorDialog("Could not connect to the cluster. Please check your network connectivity, " +
                        " if the cluster is active or if the credentials are still valid.", "Couchbase Plugin Error");
            }

            if (newActiveNode == null) {
                newActiveNode = new DefaultMutableTreeNode(new ConnectionNodeDescriptor(savedCluster.getId(),
                        savedCluster, true));
            } else {
                ((ConnectionNodeDescriptor) newActiveNode.getUserObject()).setActive(true);
                newActiveNode.removeAllChildren();
            }

            newActiveNode.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
            selectedNode.add(newActiveNode);

            ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(selectedNode);
            ((DefaultTreeModel) tree.getModel()).reload();

            tree.setPaintBusy(false);
        });
    }

    public static void disconnectFromCluster(DefaultMutableTreeNode node, ConnectionNodeDescriptor con, Tree tree) {
        node.removeAllChildren();
        ((DefaultTreeModel) tree.getModel()).reload();
        if (con.isActive()) {
            con.setActive(false);
            ActiveCluster.getInstance().disconnect();
        }
    }

    public static void deleteConnection(DefaultMutableTreeNode node, ConnectionNodeDescriptor con, Tree tree) {

        if (ActiveCluster.getInstance().getId() != null
                && ActiveCluster.getInstance().getId().equals(con.getSavedCluster().getId())) {
            disconnectFromCluster(node, con, tree);
        }
        DataLoader.deleteSavedCluster(con.getSavedCluster());
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
        if (parentNode != null) {
            parentNode.remove(node);
            ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
        }
    }
}