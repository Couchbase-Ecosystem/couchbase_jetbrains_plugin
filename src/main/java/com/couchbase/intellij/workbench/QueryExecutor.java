package com.couchbase.intellij.workbench;

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.QueryResult;
import com.couchbase.intellij.workbench.error.CouchbaseQueryErrorUtil;
import com.couchbase.intellij.workbench.error.CouchbaseQueryResultError;
import com.intellij.openapi.project.Project;

import java.util.List;

public class QueryExecutor {

    private void executeQuery(String query, Project project) {

        try {
            final List<JsonObject> results = ActiveCluster.getInstance().get().query(query).rowsAsObject();
            QueryResult.show(results, project);
        } catch (CouchbaseException e) {
            CouchbaseQueryResultError error = CouchbaseQueryErrorUtil.parseQueryError(e);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
