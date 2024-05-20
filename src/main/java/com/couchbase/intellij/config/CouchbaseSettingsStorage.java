package com.couchbase.intellij.config;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "CouchbaseSettings",
        storages = @Storage("CouchbaseSettings.xml")
)
public class CouchbaseSettingsStorage implements PersistentStateComponent<CouchbaseSettingsStorage.State> {


    private final CouchbaseSettingsStorage.State myState = new CouchbaseSettingsStorage.State();

    public static CouchbaseSettingsStorage getInstance() {
        return ApplicationManager.getApplication().getService(CouchbaseSettingsStorage.class);
    }

    @Nullable
    @Override
    public CouchbaseSettingsStorage.State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull CouchbaseSettingsStorage.State state) {
        XmlSerializerUtil.copyBean(state, myState);
    }

    @Data
    public static class State {
        public String openApiKey = null;
        public String googleGeminiKey = null;

        public String preferredProvider = null;
        public String preferredModel = null;
    }


}