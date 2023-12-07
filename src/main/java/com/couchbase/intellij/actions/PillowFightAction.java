package com.couchbase.intellij.actions;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tools.CBTools;
import com.couchbase.intellij.tools.PillowFightDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class PillowFightAction extends AnAction {

   boolean x = ActiveCluster.getInstance().isReadOnlyMode();
   boolean y = CBTools.getTool(CBTools.Type.CBC_PILLOW_FIGHT).isAvailable();
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
            PillowFightDialog dialog = new PillowFightDialog(e.getProject());
            dialog.show();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean isConnected = ActiveCluster.getInstance().get() != null;
        boolean isNotReadOnly = !ActiveCluster.getInstance().isReadOnlyMode();
        boolean isToolAvailable = CBTools.getTool(CBTools.Type.CBC_PILLOW_FIGHT).isAvailable();

        boolean shouldEnable = isConnected && isNotReadOnly && isToolAvailable;
        e.getPresentation().setEnabledAndVisible(shouldEnable);
    }
}