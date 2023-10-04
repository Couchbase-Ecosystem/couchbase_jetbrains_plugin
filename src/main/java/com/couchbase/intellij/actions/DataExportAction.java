package com.couchbase.intellij.actions;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tools.CBTools;
import com.couchbase.intellij.tools.dialog.ExportDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class DataExportAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ExportDialog dialog = new ExportDialog();
        dialog.show();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean isConnected = ActiveCluster.getInstance().get() != null;
        boolean isToolAvailable = CBTools.getTool(CBTools.Type.CB_EXPORT).isAvailable();

        boolean shouldEnable = isConnected && isToolAvailable;
        e.getPresentation().setEnabledAndVisible(shouldEnable);
    }
}
