package com.couchbase.intellij.workbench.error;

import com.couchbase.client.core.error.CouchbaseException;
import com.google.gson.Gson;

public class CouchbaseQueryErrorUtil {

    public static CouchbaseQueryResultError parseQueryError(CouchbaseException ex) {
        String json = ex.getMessage().substring(ex.getMessage().indexOf("{"));
        return new Gson().fromJson(json, CouchbaseQueryResultError.class);
    }
}
