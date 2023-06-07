package com.couchbase.intellij.listener;

import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ToolWindowListener implements ToolWindowManagerListener {

    @Override
    public void toolWindowsRegistered(@NotNull List<String> ids, @NotNull ToolWindowManager toolWindowManager) {
        if (ids.contains("Couchbase")) {
            DependenciesDownloader dep = new DependenciesDownloader();
            try {
                dep.downloadDependencies();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ToolWindowManagerListener.super.toolWindowsRegistered(ids, toolWindowManager);
    }
}
