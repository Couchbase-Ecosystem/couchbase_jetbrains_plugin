package com.couchbase.intellij.workbench.error;

import com.couchbase.client.core.error.CouchbaseException;
import com.google.gson.Gson;

import java.util.concurrent.ExecutionException;

public class CouchbaseQueryErrorUtil {

    public static CouchbaseQueryResultError parseQueryError(CouchbaseException ex) {
        String json = ex.getMessage().substring(ex.getMessage().indexOf("{"));
        return new Gson().fromJson(json, CouchbaseQueryResultError.class);
    }

    public static CouchbaseQueryResultError parseQueryError(ExecutionException ex) {
        String json = ex.getMessage().substring(ex.getMessage().indexOf("{"));
        return new Gson().fromJson(json, CouchbaseQueryResultError.class);
    }

    public static CouchbaseQueryResultError parseQueryError(String message) {
        String json = "{ \"errors\": [ {\n" +
                "    \"message\": \"" + message + "\"\n" +
                "  } ]}";
        return new Gson().fromJson(json, CouchbaseQueryResultError.class);
    }


}
