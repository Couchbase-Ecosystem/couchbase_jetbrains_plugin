package com.couchbase.intellij.persistence;

import java.util.HashMap;
import java.util.Map;

public class Clusters {

    private Map<String, SavedCluster> map;

    private Map<String, Map<String, String>> inferCache;
    private Map<String, Map<String, Long>> inferCacheUpdateTimes;

    public Map<String, SavedCluster> getMap() {
        if (map == null) {
            map = new HashMap<>();
        }
        return map;
    }

    public void setMap(Map<String, SavedCluster> map) {
        this.map = map;
    }

    public Map<String, Map<String, String>> getInferCache() {
        if (inferCache == null) {
            inferCache = new HashMap<>();
        }
        return inferCache;
    }

    public void setInferCache(Map<String, Map<String, String>> inferCache) {
        this.inferCache = inferCache;
    }

    public Map<String, Map<String, Long>> getInferCacheUpdateTimes() {
        if (inferCacheUpdateTimes == null) {
            inferCacheUpdateTimes = new HashMap<>();
        }
        return inferCacheUpdateTimes;
    }

    public void setInferCacheUpdateTimes(Map<String, Map<String, Long>> inferCacheUpdateTimes) {
        this.inferCacheUpdateTimes = inferCacheUpdateTimes;
    }
}


