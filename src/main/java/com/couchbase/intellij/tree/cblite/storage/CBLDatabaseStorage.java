package com.couchbase.intellij.tree.cblite.storage;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "CBLiteDatabaseStorage",
        storages = {
                @Storage(
                        value = "cblite_databases.xml"
                )
        }
)
public class CBLDatabaseStorage implements PersistentStateComponent<CBLDatabaseStorage.State> {


    private final CBLDatabaseStorage.State myState = new CBLDatabaseStorage.State();

    public static CBLDatabaseStorage getInstance() {
        return ApplicationManager.getApplication().getService(CBLDatabaseStorage.class);
    }

    @Nullable
    @Override
    public CBLDatabaseStorage.State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull CBLDatabaseStorage.State state) {
        XmlSerializerUtil.copyBean(state, myState);
    }

    public CBLDatabases getValue() {
        if (myState.databases == null) {
            myState.databases = new CBLDatabases();
        }
        return myState.databases;
    }

    public void setValue(CBLDatabases newValue) {
        myState.databases = newValue;
    }

    public static class State {
        public CBLDatabases databases = null;

        public CBLDatabases getStoredDatabases() {
            return databases;
        }

        public void setStoredDatabases(CBLDatabases databases) {
            this.databases = databases;
        }
    }
}
