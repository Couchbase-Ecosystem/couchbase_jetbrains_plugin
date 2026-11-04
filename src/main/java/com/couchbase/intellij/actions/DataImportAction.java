package com.couchbase.intellij.actions;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tools.CBTools;
import com.couchbase.intellij.tools.dialog.ImportDialog;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class DataImportAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ImportDialog importDialog = new ImportDialog(e.getProject());
        importDialog.show();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean connected = ActiveCluster.getInstance().get() != null;
        e.getPresentation().setVisible(connected);
        if (!connected) {
            e.getPresentation().setEnabled(false);
            return;
        }
        ToolAvailability.update(e.getPresentation(), CBTools.Type.CB_IMPORT, true, getTemplatePresentation().getDescription());
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
