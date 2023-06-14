package com.couchbase.intellij.listener;

import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ToolWindowListener implements ToolWindowManagerListener {

    @Override
    public void toolWindowsRegistered(@NotNull List<String> ids, @NotNull ToolWindowManager toolWindowManager) {
        if (ids.contains("Couchbase")) {
            try {
                FileConfigInitializer.start();
                DependenciesDownloader dep = new DependenciesDownloader();
                dep.downloadDependencies();
            } catch (Exception e) {
                Log.error(e);
                e.printStackTrace();
            }
        }
        ToolWindowManagerListener.super.toolWindowsRegistered(ids, toolWindowManager);
    }
}
