package com.couchbase.intellij.actions;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tools.CBTool;
import com.couchbase.intellij.tools.CBTools;
import com.couchbase.intellij.tools.ToolStatus;
import com.intellij.openapi.actionSystem.Presentation;
import org.jetbrains.annotations.Nullable;

/**
 * Enablement of actions that run a bundled command-line tool. The tools are downloaded after the Couchbase tool
 * window registers, so a menu opened right after startup used to miss these entries. They now stay visible and
 * say why they are disabled.
 */
public final class ToolAvailability {
    public static final String READ_ONLY = "This connection is read-only.";

    private ToolAvailability() {}

    public static String name(CBTools.Type tool) {
        return switch (tool) {
            case SHELL -> "cbsh";
            case CB_IMPORT -> "cbimport";
            case CB_EXPORT -> "cbexport";
            case CBC_PILLOW_FIGHT -> "cbc-pillowfight";
            case MCTIMINGS -> "mctimings";
            case CBMIGRATE -> "cbmigrate";
        };
    }

    /** Why the tool cannot be used on the active connection right now, or null when it can. */
    public static @Nullable String unavailableReason(CBTools.Type tool, boolean requiresWrite) {
        ActiveCluster active = ActiveCluster.getInstance();
        if (active.usesCertificateAuthentication()) return PasswordConnectionAction.REQUIRES_PASSWORD;
        if (requiresWrite && active.isReadOnlyMode()) return READ_ONLY;
        CBTool status = CBTools.getTool(tool);
        if (status.getStatus() == ToolStatus.DOWNLOADING) {
            return name(tool) + " is still being downloaded. This option is enabled when the download completes.";
        }
        if (status.getStatus() != ToolStatus.AVAILABLE) {
            return name(tool) + " is not installed. It is downloaded when the Couchbase tool window opens; check the IDE log if it stays unavailable.";
        }
        return null;
    }

    /** Keeps the action visible and enables it only when the tool can run; the description carries the reason. */
    public static void update(Presentation presentation, CBTools.Type tool, boolean requiresWrite, String description) {
        String reason = unavailableReason(tool, requiresWrite);
        presentation.setEnabled(reason == null);
        presentation.setDescription(reason == null ? description : reason);
    }
}
