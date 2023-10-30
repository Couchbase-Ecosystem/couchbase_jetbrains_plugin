package com.couchbase.intellij.tree.cblite;

import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jetbrains.annotations.NotNull;

import com.couchbase.intellij.tree.cblite.dialog.CBLCreateCollectionDialog;
import com.couchbase.intellij.tree.cblite.dialog.CBLCreateScopeDialog;
import com.couchbase.intellij.tree.cblite.nodes.CBLCollectionNodeDescriptor;
import com.couchbase.intellij.tree.cblite.nodes.CBLDatabaseNodeDescriptor;
import com.couchbase.intellij.tree.cblite.nodes.CBLFileNodeDescriptor;
import com.couchbase.intellij.tree.cblite.nodes.CBLScopeNodeDescriptor;
import com.couchbase.intellij.workbench.Log;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.treeStructure.Tree;

public class CBLTreeRightClickListener {

    public static void handle(Tree tree, Project project, MouseEvent e, DefaultMutableTreeNode clickedNode) {
        Object userObject = clickedNode.getUserObject();
        int row = tree.getClosestRowForLocation(e.getX(), e.getY());
        tree.setSelectionRow(row);

        if (userObject instanceof CBLDatabaseNodeDescriptor) {
            handleDatabase(project, e, clickedNode, (CBLDatabaseNodeDescriptor) userObject, tree);
        } else if (userObject instanceof CBLScopeNodeDescriptor) {
            handleScope(project, e, clickedNode, (CBLScopeNodeDescriptor) userObject, tree);
        } else if (userObject instanceof CBLCollectionNodeDescriptor) {
            handleCollection(project, e, clickedNode, (CBLCollectionNodeDescriptor) userObject, tree);
        } else if (userObject instanceof CBLFileNodeDescriptor) {
            handleDocument(project, e, clickedNode, (CBLFileNodeDescriptor) userObject, tree);
        }
    }

    private static void handleDatabase(Project project, MouseEvent e, DefaultMutableTreeNode clickedNode,
            CBLDatabaseNodeDescriptor userObject, Tree tree) {
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
                        SwingUtilities
                                .invokeLater(() -> Messages.showErrorDialog("Could not disconnect from the database.",
                                        "Couchbase Lite Connection Error"));
                    }
                }
            };
            actionGroup.add(menuItem);

            AnAction createScope = new AnAction("Create Scope") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    try {
                        CBLCreateScopeDialog dialog = new CBLCreateScopeDialog(project, tree);
                        dialog.show();
                        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) clickedNode.getParent();
                        if (parentNode != null) {
                            parentNode.removeAllChildren();
                        }
                        CBLDataLoader.loadScopesAndCollections(clickedNode);
                        ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(clickedNode);
                    } catch (Exception ex) {
                        Log.error(ex);
                        SwingUtilities.invokeLater(() -> Messages.showErrorDialog("Could not create the scope.","Couchbase Lite Plugin Error"));
                    }
                }
            };
            actionGroup.add(createScope);

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
                    SwingUtilities.invokeLater(() -> Messages.showErrorDialog("Could not delete the connection.",
                            "Couchbase Lite Plugin Error"));
                }
            }
        };
        actionGroup.add(deleteConnection);
        showPopup(e, tree, actionGroup);
    }

    private static void handleScope(Project project, MouseEvent e, DefaultMutableTreeNode clickedNode,
            CBLScopeNodeDescriptor userObject, Tree tree) {
        DefaultActionGroup actionGroup = new DefaultActionGroup();

        AnAction createCollection = new AnAction("Create Collection") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                try {
                    CBLCreateCollectionDialog dialog = new CBLCreateCollectionDialog(project, tree, userObject.getText());
                    dialog.show();
                    // remove all children nodes and list collections
                    clickedNode.removeAllChildren();
                    CBLDataLoader.listCollections(clickedNode, userObject.getText());
                    ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(clickedNode);
                } catch (Exception ex) {
                    Log.error(ex);
                    SwingUtilities.invokeLater(() -> Messages.showErrorDialog("Could not create the collection.",
                            "Couchbase Lite Plugin Error"));
                }
            }
        };
        actionGroup.add(createCollection);

        AnAction deleteScope = new AnAction("Delete Scope") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                int result = Messages.showYesNoDialog("<html>Are you sure you want to delete the scope <strong>"
                        + userObject.getText() + "</strong>?</html>", "Delete Scope", Messages.getQuestionIcon());
                if (result != Messages.YES) {
                    return;
                }

                try {
                    // TODO: delete scope
                    // ActiveCBLDatabase.getInstance().getDatabase().deleteScope(userObject.getText());
                    ((DefaultTreeModel) tree.getModel()).removeNodeFromParent(clickedNode);
                } catch (Exception ex) {
                    Log.error(ex);
                    SwingUtilities.invokeLater(() -> Messages.showErrorDialog("Could not delete the scope.",
                            "Couchbase Lite Plugin Error"));
                }
            }
        };
        actionGroup.add(deleteScope);
        showPopup(e, tree, actionGroup);
    }

    private static void handleCollection(Project project, MouseEvent e, DefaultMutableTreeNode clickedNode,
            CBLCollectionNodeDescriptor userObject, Tree tree) {
        DefaultActionGroup actionGroup = new DefaultActionGroup();

        AnAction openDocument = new AnAction("Open Document") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                CBLOpenDocumentDialog dialog = new CBLOpenDocumentDialog(false, project, tree, userObject.getScope(),
                        userObject.getText());
                dialog.show();
            }
        };
        actionGroup.add(openDocument);

        AnAction createDocument = new AnAction("Create Document") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                CBLOpenDocumentDialog dialog = new CBLOpenDocumentDialog(true, project, tree, userObject.getScope(),
                        userObject.getText());
                dialog.show();
            }
        };
        actionGroup.add(createDocument);

        AnAction deleteCollection = new AnAction("Delete Collection") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                int result = Messages.showYesNoDialog("<html>Are you sure you want to delete the collection <strong>"
                        + userObject.getText() + "</strong>?</html>", "Delete Collection", Messages.getQuestionIcon());
                if (result != Messages.YES) {
                    return;
                }

                try {
                    // TODO: delete collection
                    // ActiveCBLDatabase.getInstance().getDatabase().getScope(userObject.getScope()).deleteCollection(userObject.getText());
                    ((DefaultTreeModel) tree.getModel()).removeNodeFromParent(clickedNode);
                } catch (Exception ex) {
                    Log.error(ex);
                    SwingUtilities.invokeLater(() -> Messages.showErrorDialog("Could not delete the collection.",
                            "Couchbase Lite Plugin Error"));
                }
            }
        };
        actionGroup.add(deleteCollection);

        showPopup(e, tree, actionGroup);
    }

    private static void handleDocument(Project project, MouseEvent e, DefaultMutableTreeNode clickedNode,
            CBLFileNodeDescriptor userObject, Tree tree) {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        AnAction deleteDocument = new AnAction("Delete Document") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                int result = Messages.showYesNoDialog("<html>Are you sure you want to delete the document <strong>"
                        + userObject.getId() + "</strong>?</html>", "Delete Document", Messages.getQuestionIcon());
                if (result != Messages.YES) {
                    return;
                }

                try {
                    Document document = ActiveCBLDatabase.getInstance().getDatabase().getScope(userObject.getScope())
                            .getCollection(userObject.getCollection()).getDocument(userObject.getId());

                    ActiveCBLDatabase.getInstance().getDatabase().getScope(userObject.getScope())
                            .getCollection(userObject.getCollection()).delete(document);

                    if (userObject.getVirtualFile() != null) {
                        try {
                            FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                            fileEditorManager.closeFile(userObject.getVirtualFile());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Log.debug("Could not close the file", ex);
                        }
                    }

                    DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) clickedNode.getParent();
                    if (parentNode != null) {
                        ((DefaultTreeModel) tree.getModel()).removeNodeFromParent(clickedNode);
                    }
                    ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.error("An error occurred while trying to delete the document " + userObject.getId(), ex);
                    Messages.showErrorDialog("Could not delete the document. Please check the logs for more.",
                            "Couchbase Plugin Error");
                }
            }
        };
        actionGroup.add(deleteDocument);
        showPopup(e, tree, actionGroup);
    }

    private static void showPopup(MouseEvent e, Tree tree, DefaultActionGroup actionGroup) {
        DataContext dataContext = DataManager.getInstance().getDataContext(tree);
        JBPopup popup = JBPopupFactory.getInstance().createActionGroupPopup(null, actionGroup, dataContext,
                JBPopupFactory.ActionSelectionAid.MNEMONICS, false);
        popup.show(new RelativePoint(e));
    }
}
