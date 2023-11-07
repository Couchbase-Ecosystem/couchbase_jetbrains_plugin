package com.couchbase.intellij.tree.cblite;

import java.awt.event.MouseEvent;
import java.util.Objects;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jetbrains.annotations.NotNull;

import com.couchbase.intellij.tree.cblite.dialog.CBLAttachBlobDialog;
import com.couchbase.intellij.tree.cblite.dialog.CBLCreateCollectionDialog;
import com.couchbase.intellij.tree.cblite.nodes.CBLCollectionNodeDescriptor;
import com.couchbase.intellij.tree.cblite.nodes.CBLDatabaseNodeDescriptor;
import com.couchbase.intellij.tree.cblite.nodes.CBLFileNodeDescriptor;
import com.couchbase.intellij.tree.cblite.nodes.CBLIndexNodeDescriptor;
import com.couchbase.intellij.tree.cblite.nodes.CBLIndexesNodeDescriptor;
import com.couchbase.intellij.tree.cblite.nodes.CBLScopeNodeDescriptor;
import com.couchbase.intellij.workbench.Log;
import com.couchbase.lite.Collection;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Document;
import com.couchbase.lite.MaintenanceType;
import com.couchbase.lite.Meta;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.Scope;
import com.couchbase.lite.SelectResult;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
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
        } else if (userObject instanceof CBLIndexesNodeDescriptor) {
            handleIndexes(project, e, clickedNode, (CBLIndexesNodeDescriptor) userObject, tree);
        } else if (userObject instanceof CBLIndexNodeDescriptor) {
            handleIndex(project, e, clickedNode, (CBLIndexNodeDescriptor) userObject, tree);
        }
    }

    private static void handleIndex(Project project, MouseEvent e, DefaultMutableTreeNode clickedNode,
            CBLIndexNodeDescriptor userObject, Tree tree) {
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
                    Messages.showErrorDialog("Could not delete the index. Please check the logs for more.",
                            "Couchbase Plugin Error");
                }
            }
        };
        actionGroup.add(deleteIndex);

        showPopup(e, tree, actionGroup);
    }

    private static void handleIndexes(Project project, MouseEvent e, DefaultMutableTreeNode clickedNode,
            CBLIndexesNodeDescriptor userObject, Tree tree) {
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

            AnAction createCollection = new AnAction("Create a Collection") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    try {
                        CBLCreateCollectionDialog dialog = new CBLCreateCollectionDialog(project, tree);
                        dialog.show();
                        CBLDataLoader.loadScopesAndCollections(clickedNode);
                        ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(clickedNode);
                    } catch (Exception ex) {
                        Log.error(ex);
                        SwingUtilities
                                .invokeLater(() -> Messages.showErrorDialog("Could not create the collection.",
                                        "Couchbase Lite Plugin Error"));
                    }
                }
            };
            actionGroup.add(createCollection);

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

        AnAction deleteScope = new AnAction("Delete Scope") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                int result = Messages.showYesNoDialog("<html>Are you sure you want to delete the scope <strong>"
                        + userObject.getText() + "</strong>?</html>", "Delete Scope", Messages.getQuestionIcon());
                if (result != Messages.YES) {
                    return;
                }

                // Create a background task for deleting the scope
                Task.Backgroundable task = new Task.Backgroundable(project, "Deleting Scope", true) {
                    public void run(@NotNull ProgressIndicator indicator) {
                        try {
                            boolean anyDocumentHadBlob = false;
                            for (int i = 0; i < clickedNode.getChildCount(); i++) {
                                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) clickedNode.getChildAt(i);
                                CBLCollectionNodeDescriptor collectionNode = (CBLCollectionNodeDescriptor) childNode
                                        .getUserObject();
                                Collection collection = ActiveCBLDatabase.getInstance().getDatabase()
                                        .getScope(userObject.getText()).getCollection(collectionNode.getText());
                                Query query = QueryBuilder.select().from(DataSource.collection(collection));
                                ResultSet queryResults = query.execute();
                                for (Result queryResult : queryResults) {
                                    Document doc = collection.getDocument(queryResult.getString("_id"));
                                    if (documentHasBlob(doc)) {
                                        anyDocumentHadBlob = true;
                                        break;
                                    }
                                }
                                ActiveCBLDatabase.getInstance().getDatabase().deleteCollection(collectionNode.getText(),
                                        userObject.getText());
                            }
                            // After deleting the scope, perform maintenance with COMPACT type if any
                            // document had a blob
                            if (anyDocumentHadBlob) {
                                Log.debug("Performing maintenance with COMPACT type after deleting the scope "
                                        + userObject.getText());
                                ActiveCBLDatabase.getInstance().getDatabase()
                                        .performMaintenance(MaintenanceType.COMPACT);
                            }
                        } catch (Exception ex) {
                            Log.error(ex);
                            SwingUtilities.invokeLater(() -> Messages.showErrorDialog("Could not delete the scope.",
                                    "Couchbase Lite Plugin Error"));
                        }
                    }
                };

                // Run the background task
                ProgressManager.getInstance().run(task);
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
                    boolean anyDocumentHadBlob = false;
                    Collection collection = ActiveCBLDatabase.getInstance().getDatabase()
                            .getScope(userObject.getScope()).getCollection(userObject.getText());
                    Query query = QueryBuilder.select().from(DataSource.collection(collection));
                    ResultSet queryResults = query.execute();
                    for (Result queryResult : queryResults) {
                        Document doc = collection.getDocument(Objects.requireNonNull(queryResult.getString("_id")));
                        if (documentHasBlob(doc)) {
                            anyDocumentHadBlob = true;
                            break;
                        }
                    }
                    ActiveCBLDatabase.getInstance().getDatabase().deleteCollection(userObject.getText(),
                            userObject.getScope());
                    if (anyDocumentHadBlob) {
                        Log.debug("Performing maintenance with COMPACT type after deleting the collection "
                                + userObject.getText());
                        ActiveCBLDatabase.getInstance().getDatabase().performMaintenance(MaintenanceType.COMPACT);
                    }
                    DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) clickedNode.getParent();
                    Scope scope = ActiveCBLDatabase.getInstance().getDatabase().getScope(userObject.getScope());
                    ((DefaultTreeModel) tree.getModel()).removeNodeFromParent(clickedNode);

                    // If the scope has no collections after deleting the collection, remove the
                    // scope
                    if ((scope == null) || (scope.getCollections().isEmpty())) {
                        DefaultMutableTreeNode grandParentNode = (DefaultMutableTreeNode) parentNode
                                .getParent();
                        ((DefaultTreeModel) tree.getModel()).removeNodeFromParent(parentNode);
                        ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(grandParentNode);

                    } else {
                        parentNode.removeAllChildren();
                        CBLDataLoader.listCollections(parentNode, userObject.getScope());
                        ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
                    }
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

        AnAction attachBlob = new AnAction("Attach Blob") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                try {
                    Scope scope = ActiveCBLDatabase.getInstance().getDatabase().getScope(userObject.getScope());
                    Collection collection = scope.getCollection(userObject.getCollection());
                    Document document = collection.getDocument(userObject.getId());
                    CBLAttachBlobDialog dialog = new CBLAttachBlobDialog(project, scope, collection, document);
                    dialog.show();
                    if (dialog.showAndGet()) {
                        boolean hasBlob = documentHasBlob(document);
                        if (hasBlob) {
                            Log.debug("The document " + userObject.getId() + " already has a blob");
                            ActiveCBLDatabase.getInstance().getDatabase().performMaintenance(MaintenanceType.COMPACT);
                        }
                        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) clickedNode.getParent();
                        if (parentNode != null) {
                            parentNode.removeAllChildren();
                            CBLDataLoader.listDocuments(parentNode, tree, 0);
                            ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(clickedNode);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        actionGroup.add(attachBlob);

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

                    boolean hasBlob = documentHasBlob(document);
                    ActiveCBLDatabase.getInstance().getDatabase().getScope(userObject.getScope())
                            .getCollection(userObject.getCollection()).delete(document);

                    // If the document has a blob, perform maintenance with COMPACT type
                    if (hasBlob) {
                        Log.debug("Performing maintenance with COMPACT type after deleting the document "
                                + userObject.getId());
                        ActiveCBLDatabase.getInstance().getDatabase().performMaintenance(MaintenanceType.COMPACT);
                    }

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

    private static boolean documentHasBlob(Document document) {
        // Check if "_attachments" field exists and is a dictionary
        if (document.contains("_attachments") && document.getDictionary("_attachments") != null) {
            return true;
        }

        // Check all fields in the document for a dictionary with "@type" : "blob"
        for (String key : document.getKeys()) {
            if (document.getBlob(key) != null) {
                return true;
            }
        }

        return false;
    }

    private static void showPopup(MouseEvent e, Tree tree, DefaultActionGroup actionGroup) {
        DataContext dataContext = DataManager.getInstance().getDataContext(tree);
        JBPopup popup = JBPopupFactory.getInstance().createActionGroupPopup(null, actionGroup, dataContext,
                JBPopupFactory.ActionSelectionAid.MNEMONICS, false);
        popup.show(new RelativePoint(e));
    }
}
