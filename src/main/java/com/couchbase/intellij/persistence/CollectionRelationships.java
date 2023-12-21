package com.couchbase.intellij.persistence;

import lombok.Data;

import java.util.Map;

@Data
public class CollectionRelationships {
    /**
     * Cluster id -> bucket.scope.collection.field -> bucket.scope.collection.field
     */
    private Map<String, Map<String, String>> relationships;

}
