package com.couchbase.intellij.persistence.storage;

import com.couchbase.intellij.persistence.QueryHistory;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

@State(
        name = "QueryHistoryStorage",
        storages = {
                @Storage(
                        value = "query_history.xml"
                )
        }
)
public class QueryHistoryStorage implements PersistentStateComponent<QueryHistoryStorage.State> {

    private QueryHistoryStorage.State myState = new QueryHistoryStorage.State();

    public static class State {
        public QueryHistory history = null;

        public QueryHistory getHistory() {
            return history;
        }

        public void setHistory(QueryHistory history) {
            this.history = history;
        }
    }

    @Nullable
    @Override
    public QueryHistoryStorage.State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull QueryHistoryStorage.State state) {
        XmlSerializerUtil.copyBean(state, myState);
    }

    public void setValue(QueryHistory newValue) {
        myState.history = newValue;
    }

    public QueryHistory getValue() {
        if (myState.history == null) {
            myState.history = new QueryHistory();
            myState.history.setHistory(new ArrayList<>());
        }
        return myState.history;
    }

    public static QueryHistoryStorage getInstance() {
        return ApplicationManager.getApplication().getService(QueryHistoryStorage.class);
    }
}
