package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.DataLoader;
import com.couchbase.intellij.persistence.SavedCluster;
import com.couchbase.intellij.tree.node.ConnectionNodeDescriptor;
import com.couchbase.intellij.tree.node.LoadingNodeDescriptor;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.util.Enumeration;

public class TreeActionHandler {

    public static void connectToCluster(SavedCluster savedCluster, Tree tree, JPanel toolBarPanel) {
        tree.setPaintBusy(true);
        SwingUtilities.invokeLater(() -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
            Enumeration<TreeNode> children = selectedNode.children();
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

                if (toolBarPanel != null) {

                    if (ActiveCluster.getInstance().getColor() != null) {
                        SwingUtilities.invokeLater(() -> {
                            if (ActiveCluster.getInstance().getColor() != null) {
                                Border line = BorderFactory.createMatteBorder(0, 0, 1, 0, ActiveCluster.getInstance().getColor());
                                Border margin = BorderFactory.createEmptyBorder(0, 0, 1, 0); // Top, left, bottom, right margins
                                Border compound = BorderFactory.createCompoundBorder(margin, line);
                                toolBarPanel.setBorder(compound);
                                toolBarPanel.revalidate();
                            }
                        });
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            toolBarPanel.setBorder(JBUI.Borders.empty());
                            toolBarPanel.revalidate();
                        });
                    }
                }

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> Messages.showErrorDialog("Could not connect to the cluster. Please check your network connectivity, " +
                        " if the cluster is active or if the credentials are still valid.", "Couchbase Connection Error"));
                return;
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
