package com.couchbase.intellij.workbench;

import com.couchbase.client.core.api.query.CoreQueryResult;
import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryMetrics;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryResult;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.workbench.error.CouchbaseQueryErrorUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

import javax.swing.*;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.*;

public class QueryExecutor {

    public static final List<String> emptyStatsList = Arrays.asList("-", "-", "-", "-", "-");

    private static ToolWindow toolWindow;
    private static QueryResultToolWindowFactory resultWindow;

    private static DecimalFormat df = new DecimalFormat("#.00");

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


    public static void executeQuery(String query, Project project) {


        getOutputWindow(project).setStatusAsLoading();
        if (ActiveCluster.getInstance() == null) {
            //dlksdjflkjfsjdfksdf
        }
        SwingUtilities.invokeLater(() -> {
            try {
                long start = System.currentTimeMillis();
                QueryResult result = ActiveCluster.getInstance().get().query(query, QueryOptions.queryOptions().metrics(true));
                long end = System.currentTimeMillis();

                Optional<QueryMetrics> metrics = result.metaData().metrics();

                List<String> metricsList = new ArrayList<>();
                if (metrics.isPresent()) {
                    metricsList.add(end - start + " MS");
                    metricsList.add(metrics.get().elapsedTime().toMillis() + " MS");
                    metricsList.add(metrics.get().executionTime().toMillis() + " MS");
                    metricsList.add(String.valueOf(metrics.get().resultCount()));
                    metricsList.add(getSizeText(metrics.get().resultSize()));
                } else {
                    metricsList.add("-");
                    metricsList.add("-");
                    metricsList.add("-");
                    metricsList.add("-");
                    metricsList.add("-");
                }

                List<Map<String, Object>> resultList = null;

                try {
                    resultList = getResults(result.rowsAsObject());
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
                                }
                        );
                        resultList = getResults(objList);
                    } else {
                        throw ex;
                    }
                }

                getOutputWindow(project).updateQueryStats(metricsList, resultList, null);
            } catch (CouchbaseException e) {
                getOutputWindow(project).updateQueryStats(emptyStatsList, null, CouchbaseQueryErrorUtil.parseQueryError(e));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static List<Map<String, Object>> getResults(List<JsonObject> objects) {
        try {
            Field field = JsonObject.class.getDeclaredField("content");
            field.setAccessible(true);

            List<Map<String, Object>> result = new ArrayList<>();
            for (JsonObject obj : objects) {
                result.add((Map<String, Object>) field.get(obj));
            }
            return result;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private static String getSizeText(long size) {
        if (size < 1024) {
            return size + " Bytes";
        } else {
            return df.format(size / 1024.0) + " KB";
        }
    }
}
