package com.couchbase.intellij.persistence;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "ClustersStorage",
        storages = {
                @Storage(
                        value = "clusters.xml"
                )
        }
)
public class ClustersStorage implements PersistentStateComponent<ClustersStorage.State> {

    private State myState = new State();

    public static class State {
        public Clusters clusters = null;

        public Clusters getClusters() {
            return clusters;
        }

        public void setClusters(Clusters clusters) {
            this.clusters = clusters;
        }
    }

    @Nullable
    @Override
    public ClustersStorage.State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull ClustersStorage.State state) {
        XmlSerializerUtil.copyBean(state, myState);
    }

    public void setValue(Clusters newValue) {
        myState.clusters = newValue;
    }

    public Clusters getValue() {
        return myState.clusters;
    }

    public static ClustersStorage getInstance() {
        return ApplicationManager.getApplication().getService(ClustersStorage.class);
    }
}