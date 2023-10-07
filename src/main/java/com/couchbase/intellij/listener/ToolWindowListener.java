package com.couchbase.intellij.listener;

import com.couchbase.intellij.database.DataLoader;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static utils.FileUtils.createFolder;

public class ToolWindowListener implements ToolWindowManagerListener {

    @Override
    public void toolWindowsRegistered(@NotNull List<String> ids, @NotNull ToolWindowManager toolWindowManager) {
        if (ids.contains("Couchbase")) {
            try {
                String pluginConfigPath = PathManager.getConfigPath() + File.separator + "couchbase-intellij-plugin";
                createFolder(pluginConfigPath);
                DependenciesUtil.createVersioningFile(pluginConfigPath);

                CompletableFuture.runAsync(() -> DataLoader.cleanCache(ProjectManager.getInstance().getOpenProjects()[0], null));
                FileConfigInitializer.start(pluginConfigPath);

                DependenciesDownloader dep = new DependenciesDownloader();
                dep.downloadDependencies(pluginConfigPath);
            } catch (Exception e) {
                Log.error(e);
                e.printStackTrace();
            }
        }
        ToolWindowManagerListener.super.toolWindowsRegistered(ids, toolWindowManager);
    }
}
