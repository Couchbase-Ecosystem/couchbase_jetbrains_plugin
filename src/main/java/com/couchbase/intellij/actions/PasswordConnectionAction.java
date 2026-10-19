package com.couchbase.intellij.actions;

import com.couchbase.intellij.database.ActiveCluster;
import com.intellij.openapi.actionSystem.*;
import org.jetbrains.annotations.NotNull;

/** External tools have separate authentication contracts from the SDK and explorer. */
public abstract class PasswordConnectionAction extends AnAction {
    public static final String REQUIRES_PASSWORD = "This external tool requires a username/password connection.";
    protected PasswordConnectionAction(String text) { super(text); }

    @Override public void update(@NotNull AnActionEvent event) {
        boolean certificate = ActiveCluster.getInstance().usesCertificateAuthentication();
        event.getPresentation().setEnabled(!certificate);
        event.getPresentation().setDescription(certificate ? REQUIRES_PASSWORD : getTemplatePresentation().getDescription());
    }

    @Override public @NotNull ActionUpdateThread getActionUpdateThread() { return ActionUpdateThread.EDT; }
}
