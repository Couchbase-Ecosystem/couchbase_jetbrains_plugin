package com.couchbase.intellij.database;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class QueryContext {
    private String bucket;
    private String scope;

    public QueryContext(String bucket, String scope) {
        this.bucket = bucket;
        this.scope = scope;
    }

    public List<String> toList() {
        return Arrays.asList(bucket, scope);
    }
}
