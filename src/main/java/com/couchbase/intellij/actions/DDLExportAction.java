package com.couchbase.intellij.actions;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tools.PillowFightDialog;
import com.couchbase.intellij.tools.dialog.DDLExportDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class DDLExportAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        DDLExportDialog dialog = new DDLExportDialog();
        dialog.show();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean isConnected = ActiveCluster.getInstance().get() != null;
        e.getPresentation().setEnabledAndVisible(isConnected);
    }
}
