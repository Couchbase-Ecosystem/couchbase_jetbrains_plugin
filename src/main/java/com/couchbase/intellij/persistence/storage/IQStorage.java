package com.couchbase.intellij.persistence.storage;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "IQStorage",
        storages = {
                @Storage(
                        value = "iq.xml"
                )
        }
)
public class IQStorage implements PersistentStateComponent<IQStorage.State> {

    private static final State myState = new State();

    public static IQStorage getInstance() {
        return ApplicationManager.getApplication().getService(IQStorage.class);
    }

    @Override
    public @Nullable IQStorage.State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        XmlSerializerUtil.copyBean(state, myState);
    }

    @Data
    public static class State {
        public String activeOrganization;
    }
}
