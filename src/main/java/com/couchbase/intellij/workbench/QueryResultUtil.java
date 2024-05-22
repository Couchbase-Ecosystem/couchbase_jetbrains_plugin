package com.couchbase.intellij.workbench;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

import java.text.DecimalFormat;

public class QueryResultUtil {

    private static final DecimalFormat df = new DecimalFormat("#.00");
    private static ToolWindow toolWindow;
    private static QueryResultToolWindowFactory resultWindow;

    public static QueryResultToolWindowFactory getOutputWindow(Project project) {
        if (toolWindow == null) {
            ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
            toolWindow = toolWindowManager.getToolWindow("Couchbase Output");
        }
        ApplicationManager.getApplication().invokeLater(() -> {
                toolWindow.show();
        }, ModalityState.any());
        
        if (resultWindow == null) {
            resultWindow = QueryResultToolWindowFactory.instance;
        }
        return resultWindow;
    }

    public static String getSizeText(long size) {
        if (size < 1024) {
            return size + " Bytes";
        } else {
            return df.format(size / 1024.0) + " KB";
        }
    }
}
