package com.couchbase.intellij.listener;

import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

import static utils.FileUtils.createFolder;

public class ToolWindowListener implements ToolWindowManagerListener {

    @Override
    public void toolWindowsRegistered(@NotNull List<String> ids, @NotNull ToolWindowManager toolWindowManager) {
        if (ids.contains("Couchbase")) {
            try {
                String pluginConfigPath = PathManager.getConfigPath() + File.separator + "couchbase-intellij-plugin";
                createFolder(pluginConfigPath);
                DependenciesUtil.createVersioningFile(pluginConfigPath);

                FileConfigInitializer.start(pluginConfigPath);

                DependenciesDownloader dep = new DependenciesDownloader();
                dep.downloadDependencies(pluginConfigPath);
            } catch (Exception e) {
                Log.error(e);
                e.printStackTrace();
            }
        }
        // End-to-end smoke test hook (tools/e2e-ide-smoke.py): open the window as a click on its stripe button would.
        if (ids.contains("Couchbase") && Boolean.getBoolean("couchbase.e2e")) {
            com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater(() -> {
                com.intellij.openapi.wm.ToolWindow window = toolWindowManager.getToolWindow("Couchbase");
                if (window != null) window.activate(null);
            });
        }
        ToolWindowManagerListener.super.toolWindowsRegistered(ids, toolWindowManager);
    }
}
