package com.couchbase.intellij.actions;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tools.CBTools;
import com.couchbase.intellij.tools.dialog.ImportDialog;
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
        boolean isConnected = ActiveCluster.getInstance().get() != null;
        boolean isNotReadOnly = !ActiveCluster.getInstance().isReadOnlyMode();
        boolean isToolAvailable = CBTools.getTool(CBTools.Type.CB_IMPORT).isAvailable();

        boolean shouldEnable = isConnected && isNotReadOnly && isToolAvailable;
        e.getPresentation().setEnabledAndVisible(shouldEnable);
    }
}
