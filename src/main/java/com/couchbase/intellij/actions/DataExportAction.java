package com.couchbase.intellij.actions;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tools.CBTools;
import com.couchbase.intellij.tools.dialog.ExportDialog;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
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
        boolean connected = ActiveCluster.getInstance().get() != null;
        e.getPresentation().setVisible(connected);
        if (!connected) {
            e.getPresentation().setEnabled(false);
            return;
        }
        ToolAvailability.update(e.getPresentation(), CBTools.Type.CB_EXPORT, false, getTemplatePresentation().getDescription());
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
