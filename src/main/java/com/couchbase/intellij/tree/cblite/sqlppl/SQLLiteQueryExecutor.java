package com.couchbase.intellij.tree.cblite.sqlppl;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.tree.cblite.ActiveCBLDatabase;
import com.couchbase.intellij.workbench.QueryResultToolWindowFactory;
import com.couchbase.intellij.workbench.error.CouchbaseQueryError;
import com.couchbase.intellij.workbench.error.CouchbaseQueryResultError;
import com.couchbase.lite.Query;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SQLLiteQueryExecutor {

    private static ToolWindow toolWindow;
    private static QueryResultToolWindowFactory resultWindow;

    public static void runQuery(String query, int historyIndex, Project project) {

        if (ActiveCBLDatabase.getInstance().getDatabase() == null) {
            SwingUtilities.invokeLater(() -> Messages.showInfoMessage("There is no active Couchbase Lite connection to execute this query.", "Couchbase Plugin"));
            return;
        }

        long before = System.nanoTime();
        Query thisQuery =
                ActiveCBLDatabase.getInstance().getDatabase().createQuery(query);


        long past = System.nanoTime();
        double timing = ((double) past - before) / 1_000_000;

        String timingString = String.format("%.2f", timing);


        try (ResultSet rs = thisQuery.execute()) {
            List<Result> cbliteResult = rs.allResults();
            List<JsonObject> results = new ArrayList<>();
            for (Result item : cbliteResult) {
                results.add(JsonObject.fromJson(item.toJSON()));
            }

            getOutputWindow(project).updateQueryStats(Arrays.asList(timingString + " MS", "-", "-", "-", "-", "-"),
                    results, null, null, true);

        } catch (Exception e) {
            CouchbaseQueryError err = new CouchbaseQueryError();
            err.setMessage(e.getCause().getMessage());
            CouchbaseQueryResultError error = new CouchbaseQueryResultError();
            error.setErrors(List.of(err));

            getOutputWindow(project).updateQueryStats(Arrays.asList("0 MS", "-", "-", "-", "-", "-"),
                    null, error, null, false);
        }

    }


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
}
