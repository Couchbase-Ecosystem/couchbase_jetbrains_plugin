package com.couchbase.intellij.searchworkbench.idxparser;

import com.couchbase.client.java.manager.search.SearchIndex;
import com.couchbase.intellij.database.ActiveCluster;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SearchIndexCache {

    private static Map<String, String> cache = new HashMap<>();
    private static long lastUpdate = 0;
    private static final long ttl = 1000 * 60 * 20;

    public static String getIndex(String bucket, String key) {

        if (!cache.containsKey(bucket + "." + key) || (lastUpdate + ttl) < System.currentTimeMillis()) {
            cache = getIndexByBucket(bucket);
            lastUpdate = System.currentTimeMillis();
        }
        return cache.get(bucket + "." + key);
    }


    private static Map<String, String> getIndexByBucket(String bucketName) {
        return ActiveCluster.getInstance().get().searchIndexes()
                .getAllIndexes().stream()
                .filter(e -> bucketName.equals(e.sourceName()))
                .collect(Collectors.toMap(e -> e.sourceName() + "." + e.name(), SearchIndex::toJson));
    }

}
