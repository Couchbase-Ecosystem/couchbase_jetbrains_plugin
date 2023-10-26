package com.couchbase.intellij.tree.cblite;

import com.couchbase.intellij.tree.cblite.nodes.CBLCollectionNodeDescriptor;
import com.couchbase.intellij.tree.cblite.nodes.CBLDatabaseNodeDescriptor;
import com.couchbase.intellij.workbench.Log;
import com.couchbase.lite.CouchbaseLiteException;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.MouseEvent;

public class CBLTreeRightClickListener {

    public static void handle(Tree tree, Project project, MouseEvent e, DefaultMutableTreeNode clickedNode) {
        Object userObject = clickedNode.getUserObject();
        int row = tree.getClosestRowForLocation(e.getX(), e.getY());
        tree.setSelectionRow(row);

        if (userObject instanceof CBLDatabaseNodeDescriptor) {
            handleDatabase(project, e, clickedNode, (CBLDatabaseNodeDescriptor) userObject, tree);
        } else if (userObject instanceof CBLCollectionNodeDescriptor) {
            handleCollection(project, e, clickedNode, (CBLCollectionNodeDescriptor) userObject, tree);
        }
    }

    private static void handleCollection(Project project, MouseEvent e, DefaultMutableTreeNode clickedNode, CBLCollectionNodeDescriptor userObject, Tree tree) {
        DefaultActionGroup actionGroup = new DefaultActionGroup();

        AnAction openDocument = new AnAction("Open Document") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                CBLOpenDocumentDialog dialog = new CBLOpenDocumentDialog(false, project, tree, userObject.getScope(), userObject.getText());
                dialog.show();
            }
        };
        actionGroup.add(openDocument);

        AnAction createDocument = new AnAction("Create Document") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                CBLOpenDocumentDialog dialog = new CBLOpenDocumentDialog(true, project, tree, userObject.getScope(), userObject.getText());
                dialog.show();
            }
        };
        actionGroup.add(createDocument);

        AnAction deleteDocument = new AnAction("Delete Document") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                //TODO
            }
        };
        actionGroup.add(deleteDocument);

        showPopup(e, tree, actionGroup);
    }

    private static void handleDatabase(Project project, MouseEvent e, DefaultMutableTreeNode clickedNode, CBLDatabaseNodeDescriptor userObject, Tree tree) {
        DefaultActionGroup actionGroup = new DefaultActionGroup();

        boolean isActive = userObject.getDatabase().getId().equals(ActiveCBLDatabase.getInstance().getDatabaseId());
        if (isActive) {
            AnAction menuItem = new AnAction("Disconnect") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {

                    try {
                        CBLTreeHandler.disconnectFromCluster(clickedNode, userObject, tree);
                    } catch (CouchbaseLiteException ex) {
                        Log.error(ex);
                        SwingUtilities.invokeLater(() -> Messages.showErrorDialog("Could not disconnect from the database.", "Couchbase Lite Connection Error"));
                    }
                }
            };
            actionGroup.add(menuItem);
        } else {
            AnAction menuItem = new AnAction("Connect") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    CBLTreeHandler.connectToDatabase(project, userObject.getDatabase(), tree);
                }
            };
            actionGroup.add(menuItem);
        }

        AnAction deleteConnection = new AnAction("Delete Connection") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                try {
                    CBLTreeHandler.deleteConnection(clickedNode, userObject, tree);
                } catch (CouchbaseLiteException ex) {
                    Log.error(ex);
                    SwingUtilities.invokeLater(() -> Messages.showErrorDialog("Could not delete the connection.", "Couchbase Lite Plugin Error"));
                }
            }
        };
        actionGroup.add(deleteConnection);
        showPopup(e, tree, actionGroup);
    }

    private static void showPopup(MouseEvent e, Tree tree, DefaultActionGroup actionGroup) {
        DataContext dataContext = DataManager.getInstance().getDataContext(tree);
        JBPopup popup = JBPopupFactory.getInstance().createActionGroupPopup(null, actionGroup, dataContext, JBPopupFactory.ActionSelectionAid.MNEMONICS, false);
        popup.show(new RelativePoint(e));
    }
}
