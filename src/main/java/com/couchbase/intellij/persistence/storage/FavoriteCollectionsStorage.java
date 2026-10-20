package com.couchbase.intellij.persistence.storage;

import com.couchbase.intellij.persistence.FavoriteCollections;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "FavoriteCollectionsStorage",
        storages = {
                @Storage(
                        value = "favorite_collections.xml"
                )
        }
)
public class FavoriteCollectionsStorage implements PersistentStateComponent<FavoriteCollectionsStorage.State> {

    private final State myState = new State();

    public static FavoriteCollectionsStorage getInstance() {
        return ApplicationManager.getApplication().getService(FavoriteCollectionsStorage.class);
    }

    @Nullable
    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        XmlSerializerUtil.copyBean(state, myState);
    }

    public FavoriteCollections getValue() {
        if (myState.favoriteCollections == null) {
            myState.favoriteCollections = new FavoriteCollections();
        }
        return myState.favoriteCollections;
    }

    public static class State {
        public FavoriteCollections favoriteCollections = null;

        public FavoriteCollections getFavoriteCollections() {
            return favoriteCollections;
        }

        public void setFavoriteCollections(FavoriteCollections favoriteCollections) {
            this.favoriteCollections = favoriteCollections;
        }
    }
}
