package com.couchbase.intellij.persistence;

import lombok.Data;

//TODO: After a few releases, update queryfilters to use this object instead
@Data
public class QueryFilter {

    private String query;
    private String documentStartKey;
    private String documentEndKey;
    private int offset;
}
