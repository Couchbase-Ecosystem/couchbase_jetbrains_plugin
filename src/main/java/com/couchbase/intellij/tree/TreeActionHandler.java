package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.DataLoader;
import com.couchbase.intellij.persistence.SavedCluster;
import com.couchbase.intellij.tree.node.ConnectionNodeDescriptor;
import com.couchbase.intellij.tree.node.LoadingNodeDescriptor;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.Consumer;
import com.intellij.util.ui.JBUI;
import utils.CBConfigUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.util.Enumeration;
import java.util.concurrent.CompletableFuture;

public class TreeActionHandler {

    public static void connectToCluster(Project project, SavedCluster savedCluster, Tree tree, JPanel toolBarPanel) {
        connectToCluster(project, savedCluster, tree, toolBarPanel, null, null);
    }

    public static void connectToCluster(Project project, SavedCluster savedCluster, Tree tree, JPanel toolBarPanel, Consumer<Exception> connectionListener, Runnable disconnectListener) {

        SwingUtilities.invokeLater(() -> {
            tree.setPaintBusy(true);
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

            if (newActiveNode == null) {
                newActiveNode = new DefaultMutableTreeNode(new ConnectionNodeDescriptor(savedCluster.getId(),
                        savedCluster, true));
            } else {
                ((ConnectionNodeDescriptor) newActiveNode.getUserObject()).setActive(true);
                newActiveNode.removeAllChildren();
            }

            final DefaultMutableTreeNode targetNode = newActiveNode;

            try {
                DefaultMutableTreeNode finalNewActiveNode = newActiveNode;
                ActiveCluster.getInstance().connect(savedCluster, err -> {
                    if (err != null) {
                        tree.setPaintBusy(false);
                        if (connectionListener != null) {
                            connectionListener.consume(err);
                        }
                        disconnectFromCluster(finalNewActiveNode, (ConnectionNodeDescriptor) finalNewActiveNode.getUserObject(), tree);
                        return;
                    }

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
                    CompletableFuture.runAsync(() -> {
                        try {


                            if (!CBConfigUtil.isSupported(ActiveCluster.getInstance().getVersion())) {
                                SwingUtilities.invokeLater(() -> Messages.showErrorDialog("<html>This plugin doesn't work with versions bellow Couchbase 7.0</html>", "Couchbase Plugin Error"));
                            }

                            if (!CBConfigUtil.hasQueryService(ActiveCluster.getInstance().getServices())) {
                                SwingUtilities.invokeLater(() -> Messages.showErrorDialog("<html>Your cluster doesn't have the Query Service. Some features might not work properly.</html>", "Couchbase Plugin Error"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.error("Could not call the RestAPI to get an overview of the service.", e);
                        }
                    });

                    finalNewActiveNode.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
                    selectedNode.add(finalNewActiveNode);

                    ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(selectedNode);
                    ((DefaultTreeModel) tree.getModel()).reload();

                    if (connectionListener != null) {
                        connectionListener.consume(null);
                    }
                    tree.setPaintBusy(false);
                }, () -> {
                    try {
                        if (disconnectListener != null) {
                            disconnectListener.run();
                        }
                    } finally {
                        disconnectFromCluster(targetNode, (ConnectionNodeDescriptor) targetNode.getUserObject(), tree);
                    }
                });

            } catch (Exception e) {
                tree.setPaintBusy(false);
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> Messages.showErrorDialog("Could not connect to the cluster. Please check your network connectivity, " +
                        " if the cluster is active or if the credentials are still valid.", "Couchbase Connection Error"));
                if (connectionListener != null) {
                    connectionListener.consume(e);
                }
                ((ConnectionNodeDescriptor) newActiveNode.getUserObject()).setActive(false);
                return;
            } finally {
                tree.setPaintBusy(false);
            }

            newActiveNode.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
            selectedNode.add(newActiveNode);

            ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(selectedNode);
            ((DefaultTreeModel) tree.getModel()).reload();

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
