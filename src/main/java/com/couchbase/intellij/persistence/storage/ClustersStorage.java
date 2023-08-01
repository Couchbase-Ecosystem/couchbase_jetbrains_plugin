package com.couchbase.intellij.persistence.storage;

import com.couchbase.intellij.database.InferHelper;
import com.couchbase.intellij.persistence.Clusters;
import com.couchbase.intellij.persistence.SavedCluster;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;

@State(
        name = "ClustersStorage",
        storages = {
                @Storage(
                        value = "clusters.xml"
                )
        }
)
public class ClustersStorage implements PersistentStateComponent<ClustersStorage.State> {

    private final State myState = new State();

    public static ClustersStorage getInstance() {
        return ApplicationManager.getApplication().getService(ClustersStorage.class);
    }

    @Nullable
    @Override
    public ClustersStorage.State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull ClustersStorage.State state) {
        XmlSerializerUtil.copyBean(state, myState);

        // remove stale cache
        myState.clusters.setInferCacheUpdateTimes(
                myState.clusters.getInferCacheUpdateTimes().entrySet().stream()
                        .filter(entry -> {
                            if (!myState.clusters.getMap().containsKey(entry.getKey())) {
                                InferHelper.log.info("removing caches for stale cluster " + entry.getKey());
                                myState.clusters.getInferCache().remove(entry.getKey());
                                return false;
                            }
                            return true;
                        })
                        .peek(utimes -> {
                            utimes.setValue(
                                    utimes.getValue().entrySet().stream()
                                            .filter(utime -> {
                                                SavedCluster savedCluster = myState.clusters.getMap().get(utimes.getKey());
                                                if (savedCluster != null) {
                                                    if (System.currentTimeMillis() - utime.getValue() < savedCluster.getInferCachePeriod()) {
                                                        return true;
                                                    }
                                                }
                                                savedCluster.getInferCacheValues().remove(utime.getKey());
                                                InferHelper.log.info("Removing stale cache for " + utime.getKey() + " on cluster " + utimes.getKey());
                                                return false;
                                            })
                                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                            );
                        })
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }

    public Clusters getValue() {
        return myState.clusters;
    }

    public void setValue(Clusters newValue) {
        myState.clusters = newValue;
    }

    public static class State {
        public Clusters clusters = null;

        public Clusters getClusters() {
            return clusters;
        }

        public void setClusters(Clusters clusters) {
            this.clusters = clusters;
        }
    }
}