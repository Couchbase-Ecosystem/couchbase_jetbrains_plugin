package com.couchbase.intellij.workbench;

import com.couchbase.client.core.api.query.CoreQueryResult;
import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryMetrics;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryProfile;
import com.couchbase.client.java.query.QueryResult;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.persistence.storage.QueryHistoryStorage;
import com.couchbase.intellij.workbench.error.CouchbaseQueryErrorUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

import javax.swing.*;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class QueryExecutor {

    private static ToolWindow toolWindow;
    private static QueryResultToolWindowFactory resultWindow;

    private static final DecimalFormat df = new DecimalFormat("#.00");

    private static QueryResultToolWindowFactory getOutputWindow(Project project) {
        if (toolWindow == null) {
            ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
            toolWindow = toolWindowManager.getToolWindow("Couchbase Output");
        }
        toolWindow.show();

        if (resultWindow == null) {
            resultWindow = QueryResultToolWindowFactory.instance;
        }
        return resultWindow;
    }


    public static boolean executeQuery(QueryType type, String query, String bucket, String scope, int historyIndex, Project project) {

        if (query == null || query.trim().isEmpty()) {
            return false;
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
        }

        final String adjustedQuery = query;

        SwingUtilities.invokeLater(() -> {
            long start = 0;
            try {
                start = System.currentTimeMillis();
                QueryResult result;

                if (bucket != null) {
                    result = ActiveCluster.getInstance().get().bucket(bucket).scope(scope).query(adjustedQuery,
                            QueryOptions.queryOptions().profile(QueryProfile.TIMINGS).metrics(true));
                } else {
                    result = ActiveCluster.getInstance().get().query(adjustedQuery, QueryOptions.queryOptions().metrics(true));
                }
                long end = System.currentTimeMillis();

                Optional<QueryMetrics> metrics = result.metaData().metrics();
                List<String> metricsList = new ArrayList<>();
                boolean isMutation = false;
                if (metrics.isPresent()) {
                    metricsList.add(end - start + " MS");
                    metricsList.add(metrics.get().elapsedTime().toMillis() + " MS");
                    metricsList.add(metrics.get().executionTime().toMillis() + " MS");
                    if (metrics.get().mutationCount() > 0) {
                        metricsList.add(String.valueOf(metrics.get().mutationCount()));
                        metricsList.add("");
                        isMutation = true;
                    } else {
                        metricsList.add(String.valueOf(metrics.get().resultCount()));
                        metricsList.add(getSizeText(metrics.get().resultSize()));
                    }

                } else {
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
                        throw ex;
                    }
                }


                String timings;
                if (QueryType.EXPLAIN == type) {
                    timings = result.rowsAsObject().get(0).get("plan").toString();
                } else {
                    timings = result.metaData().profile().isPresent() ? result.metaData().profile().get().get("executionTimings").toString() : null;
                }

                getOutputWindow(project).updateQueryStats(isMutation, metricsList, resultList, null, timings);
            } catch (CouchbaseException e) {
                long end = System.currentTimeMillis();
                getOutputWindow(project).updateQueryStats(false, Arrays.asList((end - start) + " MS", "-", "-", "-", "-"),
                        null, CouchbaseQueryErrorUtil.parseQueryError(e), null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //if historyIndex is negative, doesn't add it to the history
        if (historyIndex >= 0) {
            return updateQueryHistory(query, historyIndex);
        } else {
            return false;
        }


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

    public enum QueryType {
        NORMAL,
        EXPLAIN,
        ADVISE
    }
}
