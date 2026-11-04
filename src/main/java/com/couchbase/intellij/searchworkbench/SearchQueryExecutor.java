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
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/** Runs a search (FTS) query on a worker thread and reports to the Couchbase Output window. */
public class SearchQueryExecutor {

    /** The REST call to the search service. */
    public interface FtsCall {
        CompletableFuture<String> run(boolean scoped, String bucket, String scope, String index, String query) throws Exception;
    }

    /** Where the outcome goes; the real one is the output tool window, tests record the calls. */
    public interface Output {
        void loading();

        void cancelled();

        void results(List<String> metrics, List<JsonObject> hits);

        void error(String elapsed, String message);
    }

    static final String INVALID_JSON = "The query is not a valid JSON";

    /** Adds the workbench's row limit as {@code size} when the query sets neither {@code size} nor {@code limit}. */
    static String withDefaultSize(String query, @Nullable Integer limit) {
        JsonObject queryJson = JsonObject.fromJson(query);
        if (limit != null && !queryJson.containsKey("size") && !queryJson.containsKey("limit")) {
            queryJson.put("size", limit);
        }
        return queryJson.toString();
    }

    /**
     * Runs the query on the calling thread, which must not be the EDT: it waits for the search service and polls
     * {@code cancel} meanwhile. {@code indexName} is either {@code index} or {@code bucket.scope.index}.
     *
     * @return false when nothing was sent or the query was cancelled.
     */
    public static boolean run(BlockingQueue<Boolean> cancel, String bucket, String indexName, String query,
                              @Nullable Integer limit, FtsCall fts, Output out) {
        String prepared;
        try {
            prepared = withDefaultSize(query.trim(), limit);
        } catch (Exception invalid) {
            out.error("0 MS", INVALID_JSON);
            return false;
        }
        out.loading();
        long start = System.currentTimeMillis();
        try {
            CompletableFuture<String> future;
            if (indexName.contains(".")) {
                String[] split = indexName.split("\\.");
                future = fts.run(true, bucket, split[1], split[2], prepared);
            } else {
                future = fts.run(false, bucket, "_default", indexName, prepared);
            }
            while (!future.isDone()) {
                Boolean stop = cancel.poll(50, TimeUnit.MILLISECONDS);
                if (stop != null) {
                    future.cancel(true);
                    out.cancelled();
                    return false;
                }
            }
            String result = future.get();
            String elapsed = (System.currentTimeMillis() - start) + " MS";
            JsonObject response = JsonObject.fromJson(result);
            if (response.containsKey("error")) {
                out.error(elapsed, response.getString("error"));
                return true;
            }
            List<String> metrics = new ArrayList<>();
            metrics.add(elapsed);
            // "took" is reported in nanoseconds.
            metrics.add(response.getLong("took") / 1_000_000 + " MS");
            metrics.add("-");
            metrics.add(null);
            metrics.add(String.valueOf(response.getLong("total_hits")));
            metrics.add(QueryResultUtil.getSizeText(result.length()));
            List<JsonObject> hits = new ArrayList<>();
            JsonArray array = response.getArray("hits");
            for (int i = 0; array != null && i < array.size(); i++) {
                hits.add(array.getObject(i));
            }
            out.results(metrics, hits);
            return true;
        } catch (Exception e) {
            out.error((System.currentTimeMillis() - start) + " MS", "An error occurred while executing the query: " + e.getMessage());
            return true;
        }
    }

    /** Entry point for the editor's background task. Validation problems are shown as dialogs on the EDT. */
    public static boolean executeQuery(BlockingQueue<Boolean> queue, String bucket, String indexName, String query, Project project) {
        if (query == null || query.trim().isEmpty()) {
            return false;
        }
        String problem = null;
        if (ActiveCluster.getInstance().get() == null) {
            problem = "There is no active connection to run this query";
        } else if (bucket == null || bucket.trim().isEmpty()) {
            problem = "Please select a bucket before running the query";
        } else if (indexName == null || indexName.trim().isEmpty()) {
            problem = "Please select a search index before running the query";
        } else if (!ActiveCluster.getInstance().get().buckets().getAllBuckets().containsKey(bucket)) {
            problem = "There is no bucket \"" + bucket + "\" in the current cluster";
        }
        if (problem != null) {
            String message = problem;
            ApplicationManager.getApplication().invokeLater(() ->
                    Messages.showMessageDialog(message, "Couchbase Plugin Error", Messages.getErrorIcon()));
            return false;
        }
        QueryResultToolWindowFactory window = getOutputWindow(project);
        return run(queue, bucket, indexName, query, ActiveCluster.getInstance().getQueryLimit(),
                CouchbaseRestAPI::callFTS, new Output() {
                    @Override
                    public void loading() {
                        window.setStatusAsLoading();
                    }

                    @Override
                    public void cancelled() {
                        window.setStatusAsCanceled();
                    }

                    @Override
                    public void results(List<String> metrics, List<JsonObject> hits) {
                        window.updateQueryStats(metrics, hits, null, null, false);
                    }

                    @Override
                    public void error(String elapsed, String message) {
                        Log.error(message);
                        window.updateQueryStats(Arrays.asList(elapsed, "-", "-", "-", "-", "-"), null,
                                CouchbaseQueryErrorUtil.parseQueryError(message), null, false);
                    }
                });
    }

    public static QueryResultToolWindowFactory getOutputWindow(Project project) {
        return QueryResultUtil.getOutputWindow(project);
    }
}
