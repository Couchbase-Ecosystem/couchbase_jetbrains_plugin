package com.couchbase.intellij.persistence.storage;

import com.couchbase.intellij.persistence.CollectionRelationships;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

@State(
        name = "RelationshipStorage",
        storages = {
                @Storage(
                        value = "relationships.xml"
                )
        }
)
public class RelationshipStorage implements PersistentStateComponent<RelationshipStorage.State> {

    private final RelationshipStorage.State myState = new RelationshipStorage.State();

    public static RelationshipStorage getInstance() {
        return ApplicationManager.getApplication().getService(RelationshipStorage.class);
    }

    @Nullable
    @Override
    public RelationshipStorage.State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull RelationshipStorage.State state) {
        XmlSerializerUtil.copyBean(state, myState);
    }

    public CollectionRelationships getValue() {
        if (myState.relationships == null) {
            myState.relationships = new CollectionRelationships();
            myState.relationships.setRelationships(new HashMap<>());
        }
        return myState.relationships;
    }

    public void setValue(CollectionRelationships newValue) {
        myState.relationships = newValue;
    }

    @Data
    public static class State {
        public CollectionRelationships relationships;

    }
}