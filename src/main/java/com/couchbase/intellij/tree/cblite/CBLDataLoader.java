package com.couchbase.intellij.tree.cblite;

import com.couchbase.intellij.tree.cblite.nodes.CBLCollectionNodeDescriptor;
import com.couchbase.intellij.tree.cblite.nodes.CBLScopeNodeDescriptor;
import com.couchbase.intellij.tree.cblite.storage.CBLiteDatabaseStorage;
import com.couchbase.intellij.tree.cblite.storage.CBLiteDatabases;
import com.couchbase.intellij.tree.cblite.storage.CBLiteDuplicateNewDatabaseNameException;
import com.couchbase.intellij.tree.cblite.storage.SavedCBLiteDatabase;
import com.couchbase.lite.Collection;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Scope;

import javax.swing.tree.DefaultMutableTreeNode;
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


    public static void loadScopesAndCollections(DefaultMutableTreeNode parent) throws CouchbaseLiteException {

        Database database = ActiveCBLiteDatabase.getInstance().getDatabase();

        for (Scope scope : database.getScopes()) {
            DefaultMutableTreeNode scopeNode = new DefaultMutableTreeNode(new CBLScopeNodeDescriptor(scope.getName()));
            parent.add(scopeNode);

            for( Collection col: database.getCollections(scope.getName())) {
                DefaultMutableTreeNode colNode = new DefaultMutableTreeNode(
                        new CBLCollectionNodeDescriptor(col.getName(), scope.getName()));
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
