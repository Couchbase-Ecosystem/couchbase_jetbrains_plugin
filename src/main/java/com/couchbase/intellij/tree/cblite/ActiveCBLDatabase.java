package com.couchbase.intellij.tree.cblite;

import com.couchbase.intellij.tree.cblite.storage.SavedCBLDatabase;
import com.couchbase.intellij.workbench.Log;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import lombok.Getter;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.HashMap;
import java.util.Map;

public class ActiveCBLDatabase {

    private static ActiveCBLDatabase activeDatabase = new ActiveCBLDatabase();

    private Map<String, Runnable> disconnectListeners = new HashMap<>();

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
        if (savedDatabase == null) {
            return null;
        }
        return savedDatabase.getId();
    }


    public void disconnect() throws CouchbaseLiteException {
        if (database != null) {
            database.close();
            database = null;
            savedDatabase = null;
        }

        for (Map.Entry<String, Runnable> run : disconnectListeners.entrySet()) {
            try {
                run.getValue().run();
            } catch (Exception e) {
                Log.debug("Error while calling disconnect listeners " + run.getKey() + e);
            }
        }
        disconnectListeners = new HashMap<>();
    }

    public void addDisconnectListener(String key, Runnable listener) {
        disconnectListeners.put(key, listener);
    }

    public void removeDisconnectListener(String key) {
        disconnectListeners.remove(key);
    }


    public static ActiveCBLDatabase getInstance() {
        return activeDatabase;
    }

    @VisibleForTesting
    public static void setInstance(ActiveCBLDatabase i) {
        activeDatabase = i;
    }
}
