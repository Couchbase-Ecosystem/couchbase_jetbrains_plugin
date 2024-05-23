package com.couchbase.intellij.persistence.storage;

import com.couchbase.intellij.persistence.FavoriteQueries;
import com.couchbase.intellij.persistence.NamedParams;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

@State(
        name = "NamedParamsStorage",
        storages = {
                @Storage(
                        value = "named_params.xml"
                )
        }
)
public class NamedParamsStorage implements PersistentStateComponent<NamedParamsStorage.State> {

    private final NamedParamsStorage.State myState = new NamedParamsStorage.State();

    public static NamedParamsStorage getInstance() {
        return ApplicationManager.getApplication().getService(NamedParamsStorage.class);
    }

    @Nullable
    @Override
    public NamedParamsStorage.State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull NamedParamsStorage.State state) {
        XmlSerializerUtil.copyBean(state, myState);
    }

    public NamedParams getValue() {
        if (myState.namedParams == null) {
            myState.namedParams = new NamedParams();
        }

        if(myState.namedParams.getParams() == null) {
            myState.namedParams.setParams(new HashMap<>());
        }
        return myState.namedParams;
    }

    public void setValue(NamedParams newValue) {
        myState.namedParams = newValue;
    }

    public static class State {
        public NamedParams namedParams = null;

        public NamedParams getNamedParams() {
            return namedParams;
        }

        public void setNamedParams(NamedParams namedParams) {
            this.namedParams = namedParams;
        }
    }
}