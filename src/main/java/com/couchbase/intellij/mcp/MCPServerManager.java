/*
 * Copyright 2011-2024 Couchbase, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.couchbase.intellij.mcp;

import com.couchbase.intellij.persistence.SavedCluster;
import com.couchbase.intellij.workbench.Log;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.util.SystemInfo;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main controller for MCP (Model Context Protocol) server integration.
 *
 * This manager writes the MCP configuration to the GitHub Copilot config file
 * at ~/.config/github-copilot/intellij/mcp.json so that Copilot can discover
 * and use the Couchbase MCP server.
 */
@Service(Service.Level.APP)
public final class MCPServerManager implements Disposable {

    private static final String MCP_SERVER_NAME = "couchbase";

    private static final List<String> READ_ONLY_DISABLED_TOOLS = Arrays.asList(
            "upsert_document_by_id",
            "insert_document_by_id",
            "replace_document_by_id",
            "delete_document_by_id"
    );

    private SavedCluster currentCluster;
    private String currentPassword;
    private boolean isConfigured = false;

    public static MCPServerManager getInstance() {
        return ApplicationManager.getApplication().getService(MCPServerManager.class);
    }

    private Path getMcpConfigPath() {
        String userHome = System.getProperty("user.home");
        if (SystemInfo.isWindows) {
            return Paths.get(userHome, "AppData", "Local", "github-copilot", "intellij", "mcp.json");
        } else {
            return Paths.get(userHome, ".config", "github-copilot", "intellij", "mcp.json");
        }
    }

    public void handleConnectionEstablished(SavedCluster cluster, String password) {
        Log.info("MCP: handleConnectionEstablished called for cluster: " + cluster.getName());

        if (password == null || password.isEmpty()) {
            Log.info("MCP: No password available, skipping");
            return;
        }

        MCPSettingsStorage.State settings = MCPSettingsStorage.getInstance().getState();
        if (settings == null) {
            Log.info("MCP: Settings is null, skipping");
            return;
        }

        Log.info("MCP: Auto-start behavior is: " + settings.getAutoStartBehavior());

        switch (settings.getAutoStartBehavior()) {
            case AUTO_START_ENABLED:
                Log.info("MCP: Auto-configuring server");
                configureServer(cluster, password);
                break;
            case AUTO_START_DISABLED:
                Log.info("MCP: Auto-start is disabled, not showing prompt");
                break;
            case PROMPT:
            default:
                Log.info("MCP: Showing prompt dialog");
                promptForMCPAutoStart(cluster, password);
                break;
        }
    }

    private void promptForMCPAutoStart(SavedCluster cluster, String password) {
        ApplicationManager.getApplication().invokeLater(() -> {
            MCPPromptDialog dialog = new MCPPromptDialog();
            boolean dialogResult = dialog.showAndGet();

            MCPSettingsStorage.AutoStartBehavior behavior = dialog.getSelectedBehavior();
            MCPSettingsStorage.State settings = MCPSettingsStorage.getInstance().getState();

            if (settings != null && behavior != MCPSettingsStorage.AutoStartBehavior.PROMPT) {
                settings.setAutoStartBehavior(behavior);
            }

            if (dialogResult || dialog.isStartOnce()) {
                if (behavior == MCPSettingsStorage.AutoStartBehavior.AUTO_START_ENABLED || dialog.isStartOnce()) {
                    configureServer(cluster, password);
                }
            }
        });
    }

    public void configureServer(SavedCluster cluster, String password) {
        if (isConfigured && currentCluster != null && currentCluster.getId().equals(cluster.getId())) {
            Log.info("MCP server is already configured for this cluster");
            return;
        }

        if (password == null || password.isEmpty()) {
            Log.warning("No password provided for MCP server, cannot configure");
            showErrorNotification("Cannot configure MCP server: No password available. Please reconnect.");
            return;
        }

        try {
            MCPSettingsStorage.State settings = MCPSettingsStorage.getInstance().getState();
            if (settings == null) {
                settings = new MCPSettingsStorage.State();
            }

            String mcpConfig = buildMcpConfig(cluster, password, settings);
            writeMcpConfig(mcpConfig);

            currentCluster = cluster;
            currentPassword = password;
            isConfigured = true;

            Log.info("Couchbase MCP server configured for cluster: " + cluster.getName());
            showSuccessNotification();

        } catch (Exception e) {
            Log.error("MCPLogId: " + MCPLogIds.SERVER_START_ERROR +
                    " - Error configuring MCP server: " + e.getMessage());
            showErrorNotification("Failed to configure Couchbase MCP server: " + e.getMessage());

            currentCluster = null;
            currentPassword = null;
            isConfigured = false;
        }
    }

    private String buildMcpConfig(SavedCluster cluster, String password, MCPSettingsStorage.State settings) {
        StringBuilder envVars = new StringBuilder();
        envVars.append("        \"CB_CONNECTION_STRING\": \"").append(escapeJson(cluster.getUrl())).append("\",\n");
        envVars.append("        \"CB_USERNAME\": \"").append(escapeJson(cluster.getUsername())).append("\",\n");
        envVars.append("        \"CB_PASSWORD\": \"").append(escapeJson(password)).append("\"");

        if (settings.isReadOnlyMode()) {
            envVars.append(",\n        \"CB_MCP_READ_ONLY_MODE\": \"true\"");
        }

        List<String> disabledTools = new ArrayList<>(settings.getDisabledTools());
        if (settings.isReadOnlyMode()) {
            for (String tool : READ_ONLY_DISABLED_TOOLS) {
                if (!disabledTools.contains(tool)) {
                    disabledTools.add(tool);
                }
            }
        }
        if (!disabledTools.isEmpty()) {
            envVars.append(",\n        \"CB_MCP_DISABLED_TOOLS\": \"").append(String.join(",", disabledTools)).append("\"");
        }

        if (settings.getConfirmationRequiredTools() != null && !settings.getConfirmationRequiredTools().isEmpty()) {
            envVars.append(",\n        \"CB_MCP_CONFIRMATION_REQUIRED_TOOLS\": \"")
                    .append(String.join(",", settings.getConfirmationRequiredTools())).append("\"");
        }

        if (settings.getExportsPath() != null && !settings.getExportsPath().isEmpty()) {
            envVars.append(",\n        \"CB_MCP_EXPORTS_PATH\": \"").append(escapeJson(settings.getExportsPath())).append("\"");
        }

        return "{\n" +
                "  \"servers\": {\n" +
                "    \"couchbase\": {\n" +
                "      \"command\": \"uvx\",\n" +
                "      \"args\": [\"couchbase-mcp-server\"],\n" +
                "      \"env\": {\n" +
                envVars.toString() + "\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private void writeMcpConfig(String config) throws IOException {
        Path configPath = getMcpConfigPath();
        Files.createDirectories(configPath.getParent());
        try (FileWriter writer = new FileWriter(configPath.toFile())) {
            writer.write(config);
        }
        Log.info("MCP config written to: " + configPath);
    }

    public void removeServerConfig() {
        try {
            Path configPath = getMcpConfigPath();
            if (Files.exists(configPath)) {
                String emptyConfig = "{\n  \"servers\": {}\n}";
                try (FileWriter writer = new FileWriter(configPath.toFile())) {
                    writer.write(emptyConfig);
                }
                Log.info("Couchbase MCP server configuration removed");
            }
        } catch (Exception e) {
            Log.error("Error removing MCP server configuration: " + e.getMessage());
        }
        currentCluster = null;
        currentPassword = null;
        isConfigured = false;
    }

    public void handleConnectionRemoved() {
        if (isConfigured) {
            Log.info("Connection removed, removing MCP server configuration");
            removeServerConfig();
        }
    }

    public void handleSettingsChanged() {
        if (isConfigured && currentCluster != null && currentPassword != null) {
            Log.info("MCP settings changed, reconfiguring server");
            configureServer(currentCluster, currentPassword);
        }
    }

    public boolean isRunning() {
        return isConfigured;
    }

    public SavedCluster getCurrentCluster() {
        return currentCluster;
    }

    private void showSuccessNotification() {
        Notification notification = new Notification(
                "Couchbase",
                "Couchbase MCP Server",
                "Couchbase MCP server configured successfully. You may need to restart Copilot or refresh tools.",
                NotificationType.INFORMATION
        );

        notification.addAction(new NotificationAction("MCP Settings") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                ShowSettingsUtil.getInstance().showSettingsDialog(
                        e.getProject(),
                        MCPSettingsConfigurable.class
                );
                notification.expire();
            }
        });

        Notifications.Bus.notify(notification);
    }

    private void showErrorNotification(String message) {
        Notifications.Bus.notify(new Notification(
                "Couchbase",
                "Couchbase MCP Server Error",
                message,
                NotificationType.ERROR
        ));
    }

    @Override
    public void dispose() {
        removeServerConfig();
    }
}

