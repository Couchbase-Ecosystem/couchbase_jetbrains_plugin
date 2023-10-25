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
public class CBLiteDatabaseStorage  implements PersistentStateComponent<CBLiteDatabaseStorage.State> {


    private final CBLiteDatabaseStorage.State myState = new CBLiteDatabaseStorage.State();

    public static CBLiteDatabaseStorage getInstance() {
        return ApplicationManager.getApplication().getService(CBLiteDatabaseStorage.class);
    }

    @Nullable
    @Override
    public CBLiteDatabaseStorage.State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull CBLiteDatabaseStorage.State state) {
        XmlSerializerUtil.copyBean(state, myState);
    }

    public CBLiteDatabases getValue() {
        if (myState.databases == null) {
            myState.databases = new CBLiteDatabases();
        }
        return myState.databases;
    }

    public void setValue(CBLiteDatabases newValue) {
        myState.databases = newValue;
    }

    public static class State {
        public CBLiteDatabases databases = null;

        public CBLiteDatabases getStoredDatabases() {
            return databases;
        }

        public void setStoredDatabases(CBLiteDatabases databases) {
            this.databases = databases;
        }
    }
}
