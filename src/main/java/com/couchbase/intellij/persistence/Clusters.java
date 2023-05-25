package com.couchbase.intellij.persistence;

import java.util.Map;

public class Clusters {

    private Map<String, SavedCluster> map;

    public Map<String, SavedCluster> getMap() {
        return map;
    }

    public void setMap(Map<String, SavedCluster> map) {
        this.map = map;
    }
}


