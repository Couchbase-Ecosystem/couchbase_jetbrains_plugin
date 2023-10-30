package com.couchbase.intellij.tree.cblite;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.core.error.TimeoutException;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.intellij.VirtualFileKeys;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.entity.CouchbaseCollection;
import com.couchbase.intellij.persistence.CouchbaseDocumentVirtualFile;
import com.couchbase.intellij.tree.cblite.nodes.CBLCollectionNodeDescriptor;
import com.couchbase.intellij.tree.cblite.nodes.CBLFileNodeDescriptor;
import com.couchbase.intellij.tree.cblite.nodes.CBLLoadMoreNodeDescriptor;
import com.couchbase.intellij.tree.cblite.nodes.CBLScopeNodeDescriptor;
import com.couchbase.intellij.tree.cblite.storage.CBLDatabaseStorage;
import com.couchbase.intellij.tree.cblite.storage.CBLDatabases;
import com.couchbase.intellij.tree.cblite.storage.CBLDuplicateNewDatabaseNameException;
import com.couchbase.intellij.tree.cblite.storage.SavedCBLDatabase;
import com.couchbase.intellij.tree.node.*;
import com.couchbase.intellij.workbench.Log;
import com.couchbase.lite.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.UserBinaryFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.couchbase.intellij.VirtualFileKeys.READ_ONLY;

public class CBLDataLoader {


    public static SavedCBLDatabase saveNewDatabase(String id, String name, String path) {

        if (id == null) {
            throw new IllegalStateException("The database id can't be null");
        }

        if (name == null) {
            throw new IllegalStateException("The database name can't be null");
        }

        if (path == null) {
            throw new IllegalStateException("The path can't be null");
        }


        CBLDatabases databases = CBLDatabaseStorage.getInstance().getValue();

        for (SavedCBLDatabase db : databases.getSavedDatabases()) {
            if (db.equals(id)) {
                throw new CBLDuplicateNewDatabaseNameException();
            }
        }

        SavedCBLDatabase newdDb = new SavedCBLDatabase();
        newdDb.setId(id);
        newdDb.setName(name);
        newdDb.setPath(path);

        databases.getSavedDatabases().add(newdDb);

        return newdDb;
    }

    public static void listScopes(DefaultMutableTreeNode parent) throws CouchbaseLiteException {
        Database database = ActiveCBLDatabase.getInstance().getDatabase();
        for (Scope scope : database.getScopes()) {
            DefaultMutableTreeNode scopeNode = new DefaultMutableTreeNode(new CBLScopeNodeDescriptor(scope.getName()));
            parent.add(scopeNode);
        }
    }
    
    public static void listCollections(DefaultMutableTreeNode parent, String scopeName) throws CouchbaseLiteException {
        Database database = ActiveCBLDatabase.getInstance().getDatabase();
        for (Collection collection : database.getCollections(scopeName)) {
            DefaultMutableTreeNode collectionNode = new DefaultMutableTreeNode(
                new CBLCollectionNodeDescriptor(collection.getName(), scopeName));
            collectionNode.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
            parent.add(collectionNode);
        }
    }
    

    public static void listDocuments(DefaultMutableTreeNode parentNode, Tree tree, int newOffset) {
        Object userObject = parentNode.getUserObject();
        tree.setPaintBusy(true);

        if (userObject instanceof CBLCollectionNodeDescriptor) {

            try {
                CBLCollectionNodeDescriptor colNode = (CBLCollectionNodeDescriptor) parentNode.getUserObject();

                if (newOffset == 0) {
                    //removed loading node
                    parentNode.removeAllChildren();
                } else {
                    //removes "Load More" node
                    parentNode.remove(parentNode.getChildCount() - 1);
                }

                String query = "Select meta(couchbaseAlias).id as cbFileNameId  " +
                        "from `"+colNode.getScope()+"`.`" + colNode.getText() + "` as couchbaseAlias  order by meta(couchbaseAlias).id "
                        + (newOffset == 0 ? "" : " OFFSET " + newOffset) + " limit 10";

                Query thisQuery = ActiveCBLDatabase.getInstance().getDatabase().createQuery(query);
                List<Result> results = thisQuery.execute().allResults();

                if (!results.isEmpty()) {
                    for (Result result : results) {
                        String docId = result.getString("cbFileNameId");
                        String fileName = docId + ".json";

                        CBLFileNodeDescriptor node = new CBLFileNodeDescriptor(fileName, colNode.getScope(), colNode.getText(), docId, null);
                        DefaultMutableTreeNode jsonFileNode = new DefaultMutableTreeNode(node);
                        parentNode.add(jsonFileNode);
                    }

                    if (results.size() == 10) {
                        DefaultMutableTreeNode loadMoreNode = new DefaultMutableTreeNode(
                                new CBLLoadMoreNodeDescriptor( colNode.getScope(), colNode.getText(), newOffset + 10));
                        parentNode.add(loadMoreNode);
                    }
                } else if (newOffset == 0) {
                    parentNode.add(new DefaultMutableTreeNode(new NoResultsNodeDescriptor()));
                }
                ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);

            } catch (Exception e) {
                Log.error(e);
                e.printStackTrace();
            } finally {
                tree.setPaintBusy(false);
            }

        } else {
            throw new IllegalStateException("The expected parent was CBLCollectionNodeDescriptor but got something else");
        }

    }

    public static void loadScopesAndCollections(DefaultMutableTreeNode parent) throws CouchbaseLiteException {

        Database database = ActiveCBLDatabase.getInstance().getDatabase();

        for (Scope scope : database.getScopes()) {
            DefaultMutableTreeNode scopeNode = new DefaultMutableTreeNode(new CBLScopeNodeDescriptor(scope.getName()));
            parent.add(scopeNode);

            for( Collection col: database.getCollections(scope.getName())) {
                DefaultMutableTreeNode colNode = new DefaultMutableTreeNode(
                        new CBLCollectionNodeDescriptor(col.getName(), scope.getName()));
                colNode.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));

                scopeNode.add(colNode);
            }
        }
    }

    public static void deleteConnection(String id) {

        CBLDatabases databases = CBLDatabaseStorage.getInstance().getValue();

        databases.setSavedDatabases(databases.getSavedDatabases().stream()
                .filter(e-> !e.getId().equals(id))
                .collect(Collectors.toList()));
    }


    public static void loadDocument(Project project, CBLFileNodeDescriptor node, Tree tree, boolean isNew) {
        tree.setPaintBusy(true);

        if (node.getVirtualFile() != null) {
            return;
        }

        String cas = null;
        try {
            Document document = ActiveCBLDatabase.getInstance().getDatabase().getScope(node.getScope()).getCollection(node.getCollection()).getDocument(node.getId());

            if(!isNew || document != null) {
                cas = document.getRevisionID();
            }


        } catch (CouchbaseLiteException e) {
            Log.error("Could not load the document " + node.getId() + ".", e);
            SwingUtilities.invokeLater(() -> Messages.showInfoMessage("<html>Could not load the document <strong>" + node.getId() + "</strong>. Please check the log for more.</html>", "Couchbase Plugin Error"));
            return;
        } finally {
            tree.setPaintBusy(false);
        }

        final String docCass = cas;
        try {
            ApplicationManager.getApplication().runWriteAction(() -> {
                CBLDocumentVirtualFile virtualFile = new CBLDocumentVirtualFile(
                        project, JsonFileType.INSTANCE,  node.getScope(), node.getCollection(), node.getId()
                );

                virtualFile.putUserData(VirtualFileKeys.CBL_CON_ID, ActiveCBLDatabase.getInstance().getDatabaseId());
                virtualFile.putUserData(VirtualFileKeys.SCOPE, node.getScope());
                virtualFile.putUserData(VirtualFileKeys.COLLECTION, node.getCollection());
                virtualFile.putUserData(VirtualFileKeys.ID, node.getId());
                virtualFile.putUserData(VirtualFileKeys.CAS, docCass);
                node.setVirtualFile(virtualFile);
            });
        } catch (Exception e) {
            tree.setPaintBusy(false);
            Log.error("An error occurred while trying to load the file", e);
            SwingUtilities.invokeLater(() -> Messages.showInfoMessage("<html>Could not load the document <strong>" + node.getId() + "</strong>. Please check the log for more.</html>", "Couchbase Plugin Error"));
        }
    }
}
