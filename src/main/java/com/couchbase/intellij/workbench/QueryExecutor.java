package com.couchbase.intellij.workbench;

import com.couchbase.client.core.api.query.CoreQueryResult;
import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.*;
import com.couchbase.client.java.transactions.TransactionQueryResult;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.QueryContext;
import com.couchbase.intellij.persistence.QueryPreferences;
import com.couchbase.intellij.persistence.storage.QueryHistoryStorage;
import com.couchbase.intellij.workbench.error.CouchbaseQueryError;
import com.couchbase.intellij.workbench.error.CouchbaseQueryErrorUtil;
import com.couchbase.intellij.workbench.error.CouchbaseQueryResultError;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import reactor.core.publisher.Mono;

import javax.swing.*;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class QueryExecutor {

    private static final DecimalFormat df = new DecimalFormat("#.00");
    private static ToolWindow toolWindow;
    private static QueryResultToolWindowFactory resultWindow;
    private static boolean isQueryScript = false;

    private static QueryResultToolWindowFactory getOutputWindow(Project project) {
        if (toolWindow == null) {
            ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
            toolWindow = toolWindowManager.getToolWindow("Couchbase Output");
        }
        SwingUtilities.invokeLater(() -> ApplicationManager.getApplication().runWriteAction(() -> toolWindow.show()));

        if (resultWindow == null) {
            resultWindow = QueryResultToolWindowFactory.instance;
        }
        return resultWindow;
    }

    public static Boolean executeScript(BlockingQueue<Boolean> queue, QueryType type, QueryContext context, List<String> statements, int historyIndex, Project project) {
        Cluster cluster = ActiveCluster.getInstance().getCluster();
        if (statements == null || statements.isEmpty()) {
            return false;
        }
        if (ActiveCluster.getInstance().get() == null) {
            Messages.showMessageDialog("There is no active connection to run this query", "Couchbase Plugin Error", Messages.getErrorIcon());
            return false;
        }
        getOutputWindow(project).setStatusAsLoading();

        List<JsonObject> result = new ArrayList<>();
        List<QueryMetaData> metas = new ArrayList<>();
        Long start = System.currentTimeMillis();
        AtomicLong mutationCount = new AtomicLong();
        AtomicInteger resultCount = new AtomicInteger();
        AtomicLong resultSize = new AtomicLong();
        CouchbaseQueryResultError error = new CouchbaseQueryResultError();

        try {
            var future = cluster.reactive().transactions().run(tx -> {
                Mono<TransactionQueryResult> aggregatedResult = null;
                for (int i = 0; i < statements.size(); i++) {

                    String query = statements.get(i);

                    if (type != QueryType.NORMAL) {
                        query = String.format("%s %s", type.toString(), query);
                    }

                    JsonObject queryResult = JsonObject.create();
                    result.add(queryResult);
                    queryResult.put("_sequence_num", i);
                    queryResult.put("_sequence_query", query);

                    try {
                        Mono<TransactionQueryResult> transactionResult;

                        if (context == null || context.getBucket() == null) {
                            transactionResult = tx.query(query);
                        } else {
                            transactionResult = tx.query(cluster.bucket(context.getBucket()).scope(context.getScope()).reactive(), query);
                        }
                        if (aggregatedResult == null) {
                            aggregatedResult = transactionResult;
                        } else {
                            aggregatedResult = aggregatedResult.then(transactionResult);
                        }

                        aggregatedResult = aggregatedResult.flatMap((r) -> {
                            QueryMetaData meta = r.metaData();
                            metas.add(meta);
                            if (meta.status() == QueryStatus.SUCCESS) {
                                queryResult.put("_sequence_query_status", "success");
                                JsonArray rows = JsonArray.from(r.rowsAsObject());
                                if (rows.size() > 0) {
                                    queryResult.put("_sequence_result", rows);
                                    resultCount.addAndGet(rows.size());
                                    meta.metrics().ifPresent(queryMetrics -> {
                                        resultSize.addAndGet(queryMetrics.resultSize());
                                        mutationCount.addAndGet(queryMetrics.mutationCount());
                                    });
                                } else {
                                    meta.metrics().ifPresent(queryMetrics -> mutationCount.addAndGet(queryMetrics.mutationCount()));
                                }
                            } else {
                                queryResult.put("_sequence_query_status", meta.status().toString().toLowerCase());
                            }
                            return Mono.empty();
                        });

                    } catch (Throwable e) {
                        queryResult.put("_sequence_query_status", "error");
                        queryResult.put("_sequence_query_error", e.getMessage());
                        throw e;
                    }
                }

                return aggregatedResult;
            }).toFuture();

            do {
                if (queue.peek() != null) {
                    queue.poll();
                    future.cancel(true);
                    getOutputWindow(project).setStatusAsCanceled();
                }
            } while (!future.isDone());

            future.get();
        } catch (Exception e) {
            error.getErrors().add(new CouchbaseQueryError(0, e.getMessage(), false));
        }

        List<String> metricsList = new ArrayList<>();
        metricsList.add(System.currentTimeMillis() - start + " MS");
        metricsList.add("-");
        metricsList.add("-");
        metricsList.add(String.valueOf(mutationCount.get()));
        metricsList.add(String.valueOf(resultCount.get()));
        metricsList.add(getSizeText(resultSize.get()));
        List<String> timings;
        if (type == QueryType.EXPLAIN) {
            timings = result.stream()
                    .map(o -> o.getArray("_sequence_result"))
                    .filter(Objects::nonNull)
                    .map(a -> a.getObject(0))
                    .filter(Objects::nonNull)
                    .map(o -> o.get("plan"))
                    .filter(Objects::nonNull)
                    .map(o -> o.toString())
                    .collect(Collectors.toList());
        } else if (type == QueryType.ADVISE) {
            timings = metas.stream()
                    .filter(meta -> meta.profile().isPresent())
                    .map(meta -> meta.profile().get().get("executionTimings").toString())
                    .collect(Collectors.toList());
        } else {
            timings = null;
        }
        getOutputWindow(project).updateQueryStats(metricsList, result, error, timings, true);

        return error.getErrors().isEmpty();
    }

    public static Boolean executeQuery(BlockingQueue<Boolean> queue, QueryType type, QueryContext context, String query, int historyIndex, Project project) {
        return executeQuery(queue, type, context, query, historyIndex, project, 200);
    }

    public static Boolean executeQuery(BlockingQueue<Boolean> queue, QueryType type, QueryContext context, String query, int historyIndex, Project project, int limit) {
        if (query == null || query.trim().isEmpty()) {
            return false;
        }
        query = query.trim();
        if (query.endsWith(";")) {
            query = query.substring(0, query.length() - 1).trim();
        }
        if (ActiveCluster.getInstance().get() == null) {
            Messages.showMessageDialog("There is no active connection to run this query", "Couchbase Plugin Error", Messages.getErrorIcon());
            return false;
        }
        getOutputWindow(project).setStatusAsLoading();

        if (QueryType.EXPLAIN == type) {
            query = "EXPLAIN " + query;
        } else if (QueryType.ADVISE == type) {
            query = "ADVISE " + query;
        } else {
            if (ActiveCluster.getInstance().isReadOnlyMode() && SQLPPAnalyzer.isMutation(query)) {
                CouchbaseQueryError err = new CouchbaseQueryError();
                err.setMessage("Hey! You can not run mutations when your cluster is on read-only mode");
                CouchbaseQueryResultError error = new CouchbaseQueryResultError();
                error.setErrors(List.of(err));

                getOutputWindow(project).updateQueryStats(Arrays.asList("0 MS", "-", "-", "-", "-", "-"),
                        null, error, null, false);
                return false;
            }
        }

        final String origQuery = query;
        boolean autoLimited = false;
        if (!SQLPPAnalyzer.isLimited(project, query)) {
            Integer queryLimit = ActiveCluster.getInstance().getQueryLimit();
            if (queryLimit != null) {
                query = String.format("SELECT * FROM (%s) as d LIMIT %d", query, queryLimit);
                autoLimited = true;
            }
        }
        final String adjustedQuery = query;
        QueryPreferences pref = ActiveCluster.getInstance().getSavedCluster().getQueryPreferences();
        QueryOptions queryOptions = QueryOptions.queryOptions()
                .timeout(Duration.ofSeconds(pref.getQueryTimeout()))
                .profile(QueryProfile.TIMINGS).metrics(true);

        long start = 0;
        try {
            start = System.currentTimeMillis();
            CompletableFuture<QueryResult> futureResult;

            if (context != null && context.getBucket() != null && context.getScope() != null) {
                futureResult = ActiveCluster.getInstance().get().bucket(context.getBucket()).scope(context.getScope()).async().query(
                        adjustedQuery, queryOptions);
            } else {
                futureResult = ActiveCluster.getInstance().get().async().query(adjustedQuery, queryOptions);
            }

            while (!futureResult.isDone()) {
                if (queue.peek() != null) {
                    queue.poll();
                    futureResult.cancel(true);
                    getOutputWindow(project).setStatusAsCanceled();
                }
            }
            QueryResult result = futureResult.get();

            long end = System.currentTimeMillis();

            Optional<QueryMetrics> metrics = result.metaData().metrics();
            List<String> metricsList = new ArrayList<>();
            if (metrics.isPresent()) {
                metricsList.add(end - start + " MS");
                metricsList.add(metrics.get().elapsedTime().toMillis() + " MS");
                metricsList.add(metrics.get().executionTime().toMillis() + " MS");
                metricsList.add(String.valueOf(metrics.get().mutationCount()));
                metricsList.add(String.valueOf(metrics.get().resultCount()));
                metricsList.add(getSizeText(metrics.get().resultSize()));
            } else {
                metricsList.add("-");
                metricsList.add("-");
                metricsList.add("-");
                metricsList.add("-");
                metricsList.add("-");
                metricsList.add("-");
            }

            List<JsonObject> resultList;

            try {
                resultList = result.rowsAsObject();
            } catch (Exception ex) {
                if (ex.getMessage().startsWith("Deserialization of content into target class com.couchbase.client.java.json.JsonObject failed")) {
                    Field field = QueryResult.class.getDeclaredField("internal");
                    field.setAccessible(true);
                    CoreQueryResult internal = (CoreQueryResult) field.get(result);
                    List<JsonObject> objList = new ArrayList<>();
                    internal.rows().forEach(e -> {
                        JsonObject obj = JsonObject.create();
                        obj.put("content", JsonArray.fromJson(e.data()));
                        objList.add(obj);
                    });
                    resultList = objList;
                } else {
                    Log.error(ex);
                    throw ex;
                }
            }

            String timings;
            if (QueryType.EXPLAIN == type) {
                timings = result.rowsAsObject().get(0).get("plan").toString();
            } else if (result.metaData().profile().isPresent()) {
                JsonObject metadata = result.metaData().profile().get().getObject("executionTimings");
                if (autoLimited) {
                    metadata = cleanupAutoLimitedMetadata(metadata);
                }
                timings = metadata.toString();
            } else {
                timings = null;
            }

            getOutputWindow(project).updateQueryStats(metricsList, resultList, null, Collections.singletonList(timings), false);

        } catch (CouchbaseException e) {
            long end = System.currentTimeMillis();
            getOutputWindow(project).updateQueryStats(Arrays.asList((end - start) + " MS", "-", "-", "-", "-", "-"),
                    null, CouchbaseQueryErrorUtil.parseQueryError(e), null, false);
        } catch (ExecutionException e) {
            long end = System.currentTimeMillis();
            getOutputWindow(project).updateQueryStats(Arrays.asList((end - start) + " MS", "-", "-", "-", "-", "-"),
                    null, CouchbaseQueryErrorUtil.parseQueryError(e), null, false);
        } catch (Exception e) {
            Log.error(e);
            e.printStackTrace();
        }

        //if historyIndex is negative, doesn't add it to the history
        if (historyIndex >= 0 && pref.isSaveHistory()) {
            return updateQueryHistory(origQuery, historyIndex);
        } else {
            return false;
        }
    }

    private static JsonObject cleanupAutoLimitedMetadata(JsonObject metadata) {
        JsonArray items = metadata.getObject("~child").getArray("~children");
        if (items.size() > 4) {
            JsonArray newItems = JsonArray.create();
            for (int i = 0; i < items.size() - 4; i++) {
                newItems.add(items.getObject(i));
            }
            newItems.add(items.getObject(items.size() - 1));
            metadata.getObject("~child").put("~children", newItems);
        }
        return metadata;
    }

    private static String getSizeText(long size) {
        if (size < 1024) {
            return size + " Bytes";
        } else {
            return df.format(size / 1024.0) + " KB";
        }
    }

    private static boolean updateQueryHistory(String query, int currentIndex) {
        List<String> hist = QueryHistoryStorage.getInstance().getValue().getHistory();
        if (query == null) {
            return false;
        }
        //running am old query
        if (hist.size() >= currentIndex + 1) {
            String histQuery = hist.get(currentIndex);
            if (histQuery.equals(query.trim())) {
                return false;
            } else {
                addQueryToHistory(query, hist);
                return true;
            }
        } else {
            addQueryToHistory(query, hist);
            return true;
        }
    }

    private static void addQueryToHistory(String query, List<String> hist) {
        if (hist.size() < 100) {
            hist.add(query.trim());
        } else {
            hist.remove(0);
            hist.add(query.trim());
        }
    }

    public static void setIsQueryScript(boolean isQueryScript) {
        QueryExecutor.isQueryScript = isQueryScript;
    }

    public static boolean getIsQueryScript() {
        return isQueryScript;
    }

    public enum QueryType {
        NORMAL,
        EXPLAIN,
        ADVISE
    }
}
