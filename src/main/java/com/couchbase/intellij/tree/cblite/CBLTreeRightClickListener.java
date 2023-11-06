package com.couchbase.intellij.tree.cblite;

import com.couchbase.intellij.DocumentFormatter;
import com.couchbase.intellij.tree.cblite.nodes.*;
import com.couchbase.intellij.workbench.Log;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
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
        } else if (userObject instanceof CBLFileNodeDescriptor) {
            handleDocument(project, e, clickedNode, (CBLFileNodeDescriptor) userObject, tree);
        } else if (userObject instanceof CBLIndexesNodeDescriptor) {
            handleIndexes(project, e, clickedNode, (CBLIndexesNodeDescriptor) userObject, tree);
        } else if (userObject instanceof CBLIndexNodeDescriptor) {
            handleIndex(project, e, clickedNode, (CBLIndexNodeDescriptor) userObject, tree);
        }
    }

    private static void handleIndex(Project project, MouseEvent e, DefaultMutableTreeNode clickedNode, CBLIndexNodeDescriptor userObject, Tree tree) {
        DefaultActionGroup actionGroup = new DefaultActionGroup();

        AnAction deleteIndex = new AnAction("Delete Index") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {


                int result = Messages.showYesNoDialog(
                        "Are you sure that you want to delete the index \"" + userObject.getText() + "\"",
                        "Delete Index", Messages.getQuestionIcon());

                if (result != Messages.YES) {
                    return;
                }
                try {
                    CBLDataLoader.deleteIndex(userObject.getScope(), userObject.getCollection(), userObject.getText());
                    tree.collapsePath(new TreePath(((DefaultMutableTreeNode) clickedNode.getParent()).getPath()));
                    tree.expandPath(new TreePath(((DefaultMutableTreeNode) clickedNode.getParent()).getPath()));
                } catch (Exception ex) {
                    Log.error("Could not delete the index " + userObject.getText(), ex);
                    Messages.showErrorDialog("Could not delete the index. Please check the logs for more.", "Couchbase Plugin Error");
                }
            }
        };
        actionGroup.add(deleteIndex);

        showPopup(e, tree, actionGroup);
    }

    private static void handleIndexes(Project project, MouseEvent e, DefaultMutableTreeNode clickedNode, CBLIndexesNodeDescriptor userObject, Tree tree) {
        DefaultActionGroup actionGroup = new DefaultActionGroup();

        AnAction createIndex = new AnAction("Create Index") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                CBLCreateIndexDialog dialog = new CBLCreateIndexDialog(project, clickedNode, tree);
                dialog.show();
            }
        };
        actionGroup.add(createIndex);

        showPopup(e, tree, actionGroup);
    }

    private static void handleDocument(Project project, MouseEvent e, DefaultMutableTreeNode clickedNode, CBLFileNodeDescriptor userObject, Tree tree) {
        DefaultActionGroup actionGroup = new DefaultActionGroup();


        AnAction openDocument = new AnAction("View Metadata") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                String metadata = CBLDataLoader.getDocMetadata(userObject.getScope(), userObject.getCollection(), userObject.getId());

                if (metadata != null) {
                    VirtualFile virtualFile = new LightVirtualFile("(read-only) " + userObject.getId() + "_meta.json", FileTypeManager.getInstance().getFileTypeByExtension("json"), metadata);
                    DocumentFormatter.formatFile(project, virtualFile);
                    FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                    fileEditorManager.openFile(virtualFile, true);
                }
            }
        };
        actionGroup.add(openDocument);

        actionGroup.addSeparator();


        AnAction setDocumentExpiration = new AnAction("Set Document Expiration") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {

                DocumentExpirationDialog dialog = null;
                try {
                    dialog = new DocumentExpirationDialog(userObject.getScope(), userObject.getCollection(), userObject.getId());
                } catch (CouchbaseLiteException ex) {
                    Log.error("An error occurred while loading the document expiration dialog", ex);
                }
                dialog.show();
            }
        };
        actionGroup.add(setDocumentExpiration);


        AnAction deleteDocument = new AnAction("Delete Document") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                int result = Messages.showYesNoDialog("<html>Are you sure you want to delete the document <strong>" + userObject.getId() + "</strong>?</html>", "Delete Document", Messages.getQuestionIcon());
                if (result != Messages.YES) {
                    return;
                }

                try {
                    Document document = ActiveCBLDatabase.getInstance().getDatabase().
                            getScope(userObject.getScope())
                            .getCollection(userObject.getCollection()).getDocument(userObject.getId());

                    ActiveCBLDatabase.getInstance().getDatabase().
                            getScope(userObject.getScope())
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
                    Messages.showErrorDialog("Could not delete the document. Please check the logs for more.", "Couchbase Plugin Error");
                }
            }
        };
        actionGroup.add(deleteDocument);
        showPopup(e, tree, actionGroup);
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
