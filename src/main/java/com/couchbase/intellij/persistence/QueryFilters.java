package com.couchbase.intellij.persistence;

import java.util.HashMap;
import java.util.Map;

public class QueryFilters {

    private Map<String, Map<String, Map<String, Map<String, String>>>> filters;

    public String getQueryFilter(String connectionId, String bucket, String scope, String collection) {

        if (filters == null || filters.size() == 0) {
            return null;
        }
        if (filters.containsKey(connectionId)
                && filters.get(connectionId).containsKey(bucket)
                && filters.get(connectionId).get(bucket).containsKey(scope)
                && filters.get(connectionId).get(bucket).get(scope).containsKey(collection)) {
            return filters.get(connectionId).get(bucket).get(scope).get(collection);
        }
        return null;
    }

    public void saveQueryFilter(String connectionId, String bucket, String scope, String collection, String filter) {

        if (filters == null) {
            filters = new HashMap<>();
        }
        if (!filters.containsKey(connectionId)) {
            Map<String, String> colMap = new HashMap<>();
            colMap.put(collection, filter);

            Map<String, Map<String, String>> scopeMap = new HashMap<>();
            scopeMap.put(scope, colMap);

            Map<String, Map<String, Map<String, String>>> bucketMap = new HashMap<>();
            bucketMap.put(bucket, scopeMap);

            filters.put(connectionId, bucketMap);
        } else if (!filters.get(connectionId).containsKey(bucket)) {
            Map<String, String> colMap = new HashMap<>();
            colMap.put(collection, filter);

            Map<String, Map<String, String>> scopeMap = new HashMap<>();
            scopeMap.put(scope, colMap);

            filters.get(connectionId).put(bucket, scopeMap);
        } else if (!filters.get(connectionId).get(bucket).containsKey(scope)) {
            Map<String, String> colMap = new HashMap<>();
            colMap.put(collection, filter);

            filters.get(connectionId).get(bucket).put(scope, colMap);
        } else {
            filters.get(connectionId).get(bucket).get(scope).put(collection, filter);
        }
    }

}
