package com.couchbase.intellij.persistence;

import lombok.Data;

import java.util.Map;

@Data
public class CollectionRelationships {
    private Map<String, Map<String, String>> relationships;

}
