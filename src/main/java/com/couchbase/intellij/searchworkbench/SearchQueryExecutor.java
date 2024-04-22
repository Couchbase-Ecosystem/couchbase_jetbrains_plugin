package com.couchbase.intellij.searchworkbench;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tree.overview.apis.CouchbaseRestAPI;
import com.couchbase.intellij.workbench.Log;
import com.couchbase.intellij.workbench.QueryResultToolWindowFactory;
import com.couchbase.intellij.workbench.QueryResultUtil;
import com.couchbase.intellij.workbench.error.CouchbaseQueryErrorUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

public class SearchQueryExecutor {

    public static Boolean executeQuery(BlockingQueue<Boolean> queue, String bucket, String indexName, String query, Project project) {
        if (query == null || query.trim().isEmpty()) {
            return false;
        }
        query = query.trim();

        if (ActiveCluster.getInstance().get() == null) {
            Messages.showMessageDialog("There is no active connection to run this query", "Couchbase Plugin Error", Messages.getErrorIcon());
            return false;
        }

        if (bucket == null || bucket.trim().isEmpty()) {
            Messages.showMessageDialog("Please select a bucket before running the query", "Couchbase Plugin Error", Messages.getErrorIcon());
            return false;
        }

        if (indexName == null || indexName.trim().isEmpty()) {
            Messages.showMessageDialog("Please select a search index before running the query", "Couchbase Plugin Error", Messages.getErrorIcon());
            return false;
        }

        if (!ActiveCluster.getInstance().get().buckets().getAllBuckets().containsKey(bucket)) {
            Messages.showMessageDialog("There is no bucket \"" + bucket + "\" in the current cluster", "Couchbase Plugin Error", Messages.getErrorIcon());
            return false;
        }

        getOutputWindow(project).setStatusAsLoading();

        long start = 0;
        try {
            start = System.currentTimeMillis();
            CompletableFuture<String> futureResult = null;

            if (indexName.contains(".")) {
                String[] split = indexName.split("\\.");
                futureResult = CouchbaseRestAPI.callFTS(true, bucket, split[1], split[2], query);
            } else {
                futureResult = CouchbaseRestAPI.callFTS(false, bucket, "_default", indexName, query);
            }


            while (!futureResult.isDone()) {
                if (queue.peek() != null) {
                    queue.poll();
                    futureResult.cancel(true);
                    getOutputWindow(project).setStatusAsCanceled();
                }
            }
            String result = futureResult.get();

            long end = System.currentTimeMillis();

            JsonObject jsonObject = JsonObject.fromJson(result);
            List<String> metricsList = new ArrayList<>();
            if (!jsonObject.containsKey("error")) {
                metricsList.add(end - start + " MS");
                metricsList.add(jsonObject.getInt("took") / 1000 + " MS");
                metricsList.add("-");
                metricsList.add(null);
                metricsList.add(String.valueOf(jsonObject.getInt("total_hits")));
                metricsList.add(QueryResultUtil.getSizeText(result.length()));

                List<JsonObject> resultList = new ArrayList<>();
                JsonArray jsonArray = jsonObject.getArray("hits");
                for (int i = 0; i < jsonArray.size(); i++) {
                    resultList.add((JsonObject) jsonArray.get(i));
                }

                getOutputWindow(project).updateQueryStats(metricsList, resultList, null, null, false);

            } else {
                metricsList.add("-");
                metricsList.add("-");
                metricsList.add("-");
                metricsList.add("-");
                metricsList.add("-");
                metricsList.add("-");

                getOutputWindow(project).updateQueryStats(Arrays.asList((end - start) + " MS", "-", "-", "-", "-", "-"), null,
                        CouchbaseQueryErrorUtil.parseQueryError(jsonObject.getString("error")), null, false);

            }

        } catch (Exception e) {
            long end = System.currentTimeMillis();
            getOutputWindow(project).updateQueryStats(Arrays.asList((end - start) + " MS", "-", "-", "-", "-", "-"), null,
                    CouchbaseQueryErrorUtil.parseQueryError("An error occurred while executing the query: " + e.getMessage()), null, false);
            Log.error(e);
        }

        return true;
    }


    private static ToolWindow toolWindow;
    private static QueryResultToolWindowFactory resultWindow;

    public static QueryResultToolWindowFactory getOutputWindow(Project project) {
        if (toolWindow == null) {
            ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
            toolWindow = toolWindowManager.getToolWindow("Couchbase Output");
        }

        if (toolWindow != null) {
            ApplicationManager.getApplication().invokeLater(() -> {
                toolWindow.show();
            }, ModalityState.any());
        }

        if (resultWindow == null) {
            resultWindow = QueryResultToolWindowFactory.instance;
        }

        return resultWindow;
    }
}
