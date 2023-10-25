package com.couchbase.intellij.tree.cblite;

import com.couchbase.intellij.tree.cblite.nodes.CBLDatabaseNodeDescriptor;
import com.couchbase.intellij.tree.cblite.storage.SavedCBLiteDatabase;
import com.couchbase.intellij.workbench.Log;
import com.couchbase.lite.CouchbaseLiteException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.util.Enumeration;

public class CBLTreeHandler {

    public static void disconnectFromCluster(DefaultMutableTreeNode node, CBLDatabaseNodeDescriptor con, Tree tree) throws CouchbaseLiteException {
        node.removeAllChildren();
        ((DefaultTreeModel) tree.getModel()).reload();
        if (con.isActive()) {
            con.setActive(false);
            ActiveCBLiteDatabase.getInstance().disconnect();
        }

    }

    public static void connectToDatabase(Project project, SavedCBLiteDatabase savedDatabase, Tree tree) {
        tree.setPaintBusy(true);
        SwingUtilities.invokeLater(() -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
            Enumeration<TreeNode> children = selectedNode.children();
            DefaultMutableTreeNode newActiveNode = null;

            try {
                while (children.hasMoreElements()) {
                    DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) children.nextElement();
                    if (childNode.getUserObject() instanceof CBLDatabaseNodeDescriptor db) {
                        if (db.isActive()) {
                            disconnectFromCluster(childNode, db, tree);
                            break;
                        }
                        if (db.getDatabase().getId().equals(savedDatabase.getId())) {
                            newActiveNode = childNode;
                        }
                    }
                }

                if (newActiveNode == null) {
                    newActiveNode = new DefaultMutableTreeNode(new CBLDatabaseNodeDescriptor(savedDatabase, true));
                } else {
                    ((CBLDatabaseNodeDescriptor) newActiveNode.getUserObject()).setActive(true);
                    newActiveNode.removeAllChildren();
                }


                ActiveCBLiteDatabase.getInstance().connect(savedDatabase);
                ((CBLDatabaseNodeDescriptor) newActiveNode.getUserObject()).setActive(false);


                CBLDataLoader.loadScopesAndCollections(newActiveNode);
                //newActiveNode.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
                selectedNode.add(newActiveNode);

                ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(selectedNode);
                ((DefaultTreeModel) tree.getModel()).reload();

                tree.setPaintBusy(false);

            } catch (Exception e) {
                Log.error(e);
                SwingUtilities.invokeLater(() -> Messages.showErrorDialog("Could not connect to the specified database.", "Couchbase Connection Error"));
                ((CBLDatabaseNodeDescriptor) newActiveNode.getUserObject()).setActive(false);
                ((DefaultTreeModel) tree.getModel()).reload();
            } finally {
                tree.setPaintBusy(false);
            }

            ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(selectedNode);
            ((DefaultTreeModel) tree.getModel()).reload();

        });
    }


    public static void deleteConnection(DefaultMutableTreeNode node, CBLDatabaseNodeDescriptor con, Tree tree) throws CouchbaseLiteException {

        if (ActiveCBLiteDatabase.getInstance().getDatabase() != null
                && ActiveCBLiteDatabase.getInstance().getDatabaseId().equals(con.getDatabase().getId())) {
            disconnectFromCluster(node, con, tree);
        }
        CBLDataLoader.deleteConnection(con.getDatabase().getId());
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
        if (parentNode != null) {
            parentNode.remove(node);
            ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
        }
    }
}
