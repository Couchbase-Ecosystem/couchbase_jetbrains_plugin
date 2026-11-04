package com.couchbase.intellij.actions;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tools.CBTools;
import com.intellij.openapi.actionSystem.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** External tools have separate authentication contracts from the SDK and explorer. */
public abstract class PasswordConnectionAction extends AnAction {
    public static final String REQUIRES_PASSWORD = "This external tool requires a username/password connection.";

    private final @Nullable CBTools.Type tool;
    private final boolean requiresWrite;

    protected PasswordConnectionAction(String text) { this(text, null, false); }

    /** An action that runs {@code tool}; it stays listed but disabled until the tool is installed and usable. */
    protected PasswordConnectionAction(String text, @Nullable CBTools.Type tool, boolean requiresWrite) {
        super(text);
        this.tool = tool;
        this.requiresWrite = requiresWrite;
    }

    @Override public void update(@NotNull AnActionEvent event) {
        if (tool != null) {
            ToolAvailability.update(event.getPresentation(), tool, requiresWrite, getTemplatePresentation().getDescription());
            return;
        }
        boolean certificate = ActiveCluster.getInstance().usesCertificateAuthentication();
        event.getPresentation().setEnabled(!certificate);
        event.getPresentation().setDescription(certificate ? REQUIRES_PASSWORD : getTemplatePresentation().getDescription());
    }

    @Override public @NotNull ActionUpdateThread getActionUpdateThread() { return ActionUpdateThread.BGT; }
}
