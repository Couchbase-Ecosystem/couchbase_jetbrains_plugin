package com.couchbase.intellij.tree.cblite;

import com.couchbase.intellij.tree.cblite.storage.SavedCBLDatabase;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import lombok.Getter;
import org.jetbrains.annotations.VisibleForTesting;

public class ActiveCBLDatabase {

    private static ActiveCBLDatabase activeDatabase = new ActiveCBLDatabase();

    @Getter
    private Database database;

    @Getter
    private SavedCBLDatabase savedDatabase;

    private ActiveCBLDatabase() {
    }

    public void connect(SavedCBLDatabase savedDatabase) throws CouchbaseLiteException {
        DatabaseConfiguration config = new DatabaseConfiguration();
        config.setDirectory(savedDatabase.getPath());
        database = new Database(savedDatabase.getName(), config);
        this.savedDatabase = savedDatabase;
    }

    public String getDatabaseId() {
        if(savedDatabase == null) {
            return null;
        }
        return savedDatabase.getId();
    }


    public void disconnect() throws CouchbaseLiteException {
        if(database != null) {
            database.close();
            database = null;
            savedDatabase = null;
        }
    }


    public static ActiveCBLDatabase getInstance() {
        return activeDatabase;
    }

    @VisibleForTesting
    public static void setInstance(ActiveCBLDatabase i) {
        activeDatabase = i;
    }
}
