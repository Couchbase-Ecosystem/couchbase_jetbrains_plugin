package com.couchbase.intellij.workbench;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

import java.text.DecimalFormat;

public class QueryResultUtil {

    private static final DecimalFormat df = new DecimalFormat("#.00");

    /**
     * The Couchbase Output window of {@code project}. Nothing is cached statically: each project window owns
     * its tool window, and the reference dies with the project instead of keeping a closed one alive.
     */
    public static QueryResultToolWindowFactory getOutputWindow(Project project) {
        if (!ApplicationManager.getApplication().isDispatchThread()) {
            QueryResultToolWindowFactory ready = QueryResultToolWindowFactory.forProject(project);
            if (ready != null) return ready;
            QueryResultToolWindowFactory[] window = new QueryResultToolWindowFactory[1];
            ApplicationManager.getApplication().invokeAndWait(() -> window[0] = getOutputWindow(project));
            return window[0];
        }
        ToolWindow current = ToolWindowManager.getInstance(project).getToolWindow("Couchbase Output");
        if (current == null) throw new IllegalStateException("Couchbase Output tool window is unavailable.");
        current.getContentManager(); // Initialize the output components before a worker publishes results.
        current.show();
        QueryResultToolWindowFactory window = QueryResultToolWindowFactory.forProject(project);
        if (window == null) throw new IllegalStateException("Couchbase Output tool window is unavailable.");
        return window;
    }

    public static String getSizeText(long size) {
        if (size < 1024) {
            return size + " Bytes";
        } else {
            return df.format(size / 1024.0) + " KB";
        }
    }
}
