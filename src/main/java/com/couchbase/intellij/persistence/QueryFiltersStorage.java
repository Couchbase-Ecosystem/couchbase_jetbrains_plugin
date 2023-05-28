package com.couchbase.intellij.persistence;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "QueryFiltersStorage",
        storages = {
                @Storage(
                        value = "query_filters.xml"
                )
        }
)
public class QueryFiltersStorage implements PersistentStateComponent<QueryFiltersStorage.State> {

    private State myState = new State();

    public static class State {
        public QueryFilters queryFilters = null;

        public QueryFilters getQueryFilters() {
            return queryFilters;
        }

        public void setQueryFilters(QueryFilters queryFilters) {
            this.queryFilters = queryFilters;
        }
    }

    @Nullable
    @Override
    public QueryFiltersStorage.State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull QueryFiltersStorage.State state) {
        XmlSerializerUtil.copyBean(state, myState);
    }

    public void setValue(QueryFilters newValue) {
        myState.queryFilters = newValue;
    }

    public QueryFilters getValue() {
        if(myState.queryFilters == null){
            myState.queryFilters = new QueryFilters();
        }
        return myState.queryFilters;
    }

    public static QueryFiltersStorage getInstance() {
        return ApplicationManager.getApplication().getService(QueryFiltersStorage.class);
    }
}