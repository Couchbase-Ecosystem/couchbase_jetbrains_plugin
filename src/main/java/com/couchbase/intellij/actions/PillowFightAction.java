package com.couchbase.intellij.actions;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tools.CBTools;
import com.couchbase.intellij.tools.PillowFightDialog;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class PillowFightAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PillowFightDialog dialog = new PillowFightDialog(e.getProject());
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
        ToolAvailability.update(e.getPresentation(), CBTools.Type.CBC_PILLOW_FIGHT, true, getTemplatePresentation().getDescription());
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
