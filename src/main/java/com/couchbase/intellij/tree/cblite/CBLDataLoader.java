package com.couchbase.intellij.tree.cblite;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.tree.cblite.nodes.CBLCollectionNodeDescriptor;
import com.couchbase.intellij.tree.cblite.nodes.CBLFileNodeDescriptor;
import com.couchbase.intellij.tree.cblite.nodes.CBLLoadMoreNodeDescriptor;
import com.couchbase.intellij.tree.cblite.nodes.CBLScopeNodeDescriptor;
import com.couchbase.intellij.tree.cblite.storage.CBLiteDatabaseStorage;
import com.couchbase.intellij.tree.cblite.storage.CBLiteDatabases;
import com.couchbase.intellij.tree.cblite.storage.CBLiteDuplicateNewDatabaseNameException;
import com.couchbase.intellij.tree.cblite.storage.SavedCBLiteDatabase;
import com.couchbase.intellij.tree.node.*;
import com.couchbase.intellij.workbench.Log;
import com.couchbase.intellij.workbench.SQLPPQueryUtils;
import com.couchbase.lite.*;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.List;
import java.util.stream.Collectors;

public class CBLDataLoader {


    public static SavedCBLiteDatabase saveNewDatabase(String id, String name, String path) {

        if (id == null) {
            throw new IllegalStateException("The database id can't be null");
        }

        if (name == null) {
            throw new IllegalStateException("The database name can't be null");
        }

        if (path == null) {
            throw new IllegalStateException("The path can't be null");
        }


        CBLiteDatabases databases = CBLiteDatabaseStorage.getInstance().getValue();

        for (SavedCBLiteDatabase db : databases.getSavedDatabases()) {
            if (db.equals(id)) {
                throw new CBLiteDuplicateNewDatabaseNameException();
            }
        }

        SavedCBLiteDatabase newdDb = new SavedCBLiteDatabase();
        newdDb.setId(id);
        newdDb.setName(name);
        newdDb.setPath(path);

        databases.getSavedDatabases().add(newdDb);

        return newdDb;
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

                Query thisQuery = ActiveCBLiteDatabase.getInstance().getDatabase().createQuery(query);
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

        Database database = ActiveCBLiteDatabase.getInstance().getDatabase();

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

        CBLiteDatabases databases = CBLiteDatabaseStorage.getInstance().getValue();

        databases.setSavedDatabases(databases.getSavedDatabases().stream()
                .filter(e-> !e.getId().equals(id))
                .collect(Collectors.toList()));
    }

}
