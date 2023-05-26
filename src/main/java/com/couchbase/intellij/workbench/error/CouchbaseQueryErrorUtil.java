package com.couchbase.intellij.workbench.error;

import com.google.gson.Gson;

public class CouchbaseQueryErrorUtil {

    public static CouchbaseQueryResultError parseQueryError(Exception ex) {
        String json = ex.getMessage().substring(ex.getMessage().indexOf("{"));
        return new Gson().fromJson(json, CouchbaseQueryResultError.class);
    }
}
