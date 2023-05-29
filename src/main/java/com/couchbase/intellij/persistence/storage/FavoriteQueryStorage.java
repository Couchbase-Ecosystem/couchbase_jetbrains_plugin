package com.couchbase.intellij.persistence.storage;

import com.couchbase.intellij.persistence.FavoriteQueries;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

@State(
        name = "FavoriteQueryStorage",
        storages = {
                @Storage(
                        value = "favorite_query.xml"
                )
        }
)
public class FavoriteQueryStorage implements PersistentStateComponent<FavoriteQueryStorage.State> {

    private FavoriteQueryStorage.State myState = new FavoriteQueryStorage.State();

    public static class State {
        public FavoriteQueries favQueries = null;

        public FavoriteQueries getFavQueries() {
            return favQueries;
        }

        public void setFavQueries(FavoriteQueries favQueries) {
            this.favQueries = favQueries;
        }
    }

    @Nullable
    @Override
    public FavoriteQueryStorage.State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull FavoriteQueryStorage.State state) {
        XmlSerializerUtil.copyBean(state, myState);
    }

    public void setValue(FavoriteQueries newValue) {
        myState.favQueries = newValue;
    }

    public FavoriteQueries getValue() {
        if (myState.favQueries == null) {
            myState.favQueries = new FavoriteQueries();
            myState.favQueries.setList(new ArrayList<>());
        }
        return myState.favQueries;
    }

    public static FavoriteQueryStorage getInstance() {
        return ApplicationManager.getApplication().getService(FavoriteQueryStorage.class);
    }
}