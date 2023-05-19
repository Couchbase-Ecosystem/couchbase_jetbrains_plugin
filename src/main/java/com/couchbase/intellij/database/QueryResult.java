package com.couchbase.intellij.database;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.result.JsonTableModel;
import com.couchbase.intellij.workbench.QueryResultToolWindowFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

import java.util.List;

public class QueryResult {

    private static JsonTableModel model;

    public static void init(JsonTableModel model) {
        QueryResult.model = model;
    }

    public static void show(List<JsonObject> result, Project project) {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = toolWindowManager.getToolWindow("Couchbase Result");
        if (toolWindow != null) {
            toolWindow.show();
            model.updateData(result);
            //resultToolWindow.tabs.select(resultToolWindow.queryResultTab, true);
        }
    }
}
