package com.couchbase.intellij.tree.iq.ui;

import com.couchbase.intellij.tree.CouchbaseWindowFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

public class ToolWindowLocator {
    public static final String TOOL_WINDOW_ID = "Couchbase";

    private static ToolWindow locate(Project project) {
        return ToolWindowManager.getInstance(project).getToolWindow(TOOL_WINDOW_ID);
    }

    public static void ensureActivated(Project project) {
        var couchbase = locate(project);
        if (couchbase == null) {
            throw new AssertionError("Unable to find " + TOOL_WINDOW_ID + " Tool Window");
        }
        if (!couchbase.isActive()) {
            couchbase.activate(null);

            couchbase.getContentManager().setSelectedContent(CouchbaseWindowFactory.getContent(project, CouchbaseWindowFactory.IQ_CONTENT));
        }
    }
}
