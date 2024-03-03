package com.couchbase.intellij.persistence;

import lombok.Data;

@Data
public class QueryPreferences {

    private int queryTimeout;
    private boolean saveHistory;
}
