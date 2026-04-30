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
import com.intellij.openapi.application.PathManager;
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
import java.util.List;

/**
 * Main controller for MCP (Model Context Protocol) server integration.
 *
 * This manager writes the MCP configuration to the appropriate config files
 * for both GitHub Copilot and Google Gemini Code Assist so that they can
 * discover and use the Couchbase MCP server.
 *
 * Copilot config: ~/.config/github-copilot/intellij/mcp.json (uses "servers" key)
 * Gemini config: ~/Library/Application Support/JetBrains/<IDE>/mcp.json (uses "mcpServers" key)
 */
@Service(Service.Level.APP)
public final class MCPServerManager implements Disposable {

    private SavedCluster currentCluster;
    private String currentPassword;
    private boolean isConfigured = false;
    private boolean copilotConfigured = false;
    private boolean geminiConfigured = false;

    public static MCPServerManager getInstance() {
        return ApplicationManager.getApplication().getService(MCPServerManager.class);
    }

    /**
     * Get the path to Copilot's mcp.json file.
     * Copilot uses ~/.config/github-copilot/intellij/mcp.json
     */
    private Path getCopilotMcpConfigPath() {
        String userHome = System.getProperty("user.home");
        if (SystemInfo.isWindows) {
            return Paths.get(userHome, "AppData", "Local", "github-copilot", "intellij", "mcp.json");
        } else {
            return Paths.get(userHome, ".config", "github-copilot", "intellij", "mcp.json");
        }
    }

    /**
     * Get the path to Gemini's mcp.json file.
     * Gemini uses the JetBrains config directory: ~/Library/Application Support/JetBrains/<IDE>/mcp.json
     */
    private Path getGeminiMcpConfigPath() {
        // Use PathManager to get the correct IDE config path
        String configPath = PathManager.getConfigPath();
        return Paths.get(configPath, "mcp.json");
    }

    /**
     * Get the path to the sampling.json file which controls auto-approve settings for Copilot.
     */
    private Path getSamplingConfigPath() {
        String userHome = System.getProperty("user.home");
        if (SystemInfo.isWindows) {
            return Paths.get(userHome, "AppData", "Local", "github-copilot", "intellij", "sampling.json");
        } else {
            return Paths.get(userHome, ".config", "github-copilot", "intellij", "sampling.json");
        }
    }

    public void handleConnectionEstablished(SavedCluster cluster, String password) {
        Log.info("MCP: handleConnectionEstablished called for cluster: " + cluster.getName());

        if (password == null || password.isEmpty()) {
            Log.info("MCP: No password available, skipping");
            return;
        }

        // Check if any AI assistant is installed
        boolean copilotInstalled = AIAssistantDetector.isCopilotInstalled();
        boolean geminiInstalled = AIAssistantDetector.isGeminiInstalled();

        if (!copilotInstalled && !geminiInstalled) {
            Log.info("MCP: No AI assistant (Copilot or Gemini) detected, skipping MCP setup");
            return;
        }

        Log.info("MCP: AI assistants detected - Copilot: " + copilotInstalled + ", Gemini: " + geminiInstalled);

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

            List<String> configuredAssistants = new ArrayList<>();

            // Configure Copilot if installed
            if (AIAssistantDetector.isCopilotInstalled()) {
                try {
                    String copilotConfig = buildMcpConfig(cluster, password, settings, false);
                    writeMcpConfig(copilotConfig, getCopilotMcpConfigPath());
                    updateSamplingConfig(settings);
                    copilotConfigured = true;
                    configuredAssistants.add("GitHub Copilot");
                    Log.info("Couchbase MCP server configured for Copilot");
                } catch (Exception e) {
                    Log.error("Error configuring MCP for Copilot: " + e.getMessage());
                }
            }

            // Configure Gemini if installed
            if (AIAssistantDetector.isGeminiInstalled()) {
                try {
                    String geminiConfig = buildMcpConfig(cluster, password, settings, true);
                    writeMcpConfig(geminiConfig, getGeminiMcpConfigPath());
                    geminiConfigured = true;
                    configuredAssistants.add("Gemini Code Assist");
                    Log.info("Couchbase MCP server configured for Gemini");
                } catch (Exception e) {
                    Log.error("Error configuring MCP for Gemini: " + e.getMessage());
                }
            }

            if (!configuredAssistants.isEmpty()) {
                currentCluster = cluster;
                currentPassword = password;
                isConfigured = true;
                Log.info("Couchbase MCP server configured for cluster: " + cluster.getName());
                showSuccessNotification(configuredAssistants);
            } else {
                showErrorNotification("Failed to configure Couchbase MCP server for any AI assistant.");
            }

        } catch (Exception e) {
            Log.error("MCPLogId: " + MCPLogIds.SERVER_START_ERROR +
                    " - Error configuring MCP server: " + e.getMessage());
            showErrorNotification("Failed to configure Couchbase MCP server: " + e.getMessage());

            currentCluster = null;
            currentPassword = null;
            isConfigured = false;
            copilotConfigured = false;
            geminiConfigured = false;
        }
    }

    /**
     * Update the sampling.json file to configure auto-approve settings for Couchbase MCP in Copilot.
     * If confirmation tools are empty, all tools are auto-approved (alwaysAllow: true).
     * If confirmation tools are specified, those tools require approval (alwaysAllow: false).
     */
    private void updateSamplingConfig(MCPSettingsStorage.State settings) {
        try {
            List<String> confirmationTools = settings.getConfirmationRequiredTools();
            String samplingConfig;

            if (confirmationTools == null || confirmationTools.isEmpty()) {
                // No confirmation tools specified - auto-approve all
                samplingConfig = "{\n" +
                        "  \"couchbase\": {\n" +
                        "    \"alwaysAllow\": true\n" +
                        "  }\n" +
                        "}";
                Log.info("MCP: Setting auto-approve for all Couchbase tools in Copilot");
            } else {
                // Some tools require confirmation - set alwaysAllow to false
                samplingConfig = "{\n" +
                        "  \"couchbase\": {\n" +
                        "    \"alwaysAllow\": false\n" +
                        "  }\n" +
                        "}";
                Log.info("MCP: Some tools require confirmation: " + confirmationTools);
            }

            writeSamplingConfig(samplingConfig);
        } catch (Exception e) {
            Log.error("Error updating sampling config: " + e.getMessage());
        }
    }

    private void writeSamplingConfig(String config) throws IOException {
        Path configPath = getSamplingConfigPath();
        Files.createDirectories(configPath.getParent());
        try (FileWriter writer = new FileWriter(configPath.toFile())) {
            writer.write(config);
        }
        Log.info("MCP sampling config written to: " + configPath);
    }

    /**
     * Build the MCP config JSON.
     *
     * @param cluster The cluster to connect to
     * @param password The password for the cluster
     * @param settings The MCP settings
     * @param useGeminiFormat If true, uses "mcpServers" key (Gemini format), otherwise uses "servers" key (Copilot format)
     * @return The MCP config JSON string
     */
    private String buildMcpConfig(SavedCluster cluster, String password, MCPSettingsStorage.State settings, boolean useGeminiFormat) {
        Log.info("MCP: Building config with readOnlyMode=" + settings.isReadOnlyMode() +
                 ", disabledTools=" + settings.getDisabledTools() +
                 ", confirmationTools=" + settings.getConfirmationRequiredTools() +
                 ", format=" + (useGeminiFormat ? "Gemini" : "Copilot"));

        StringBuilder envVars = new StringBuilder();
        envVars.append("        \"CB_CONNECTION_STRING\": \"").append(escapeJson(cluster.getUrl())).append("\",\n");
        envVars.append("        \"CB_USERNAME\": \"").append(escapeJson(cluster.getUsername())).append("\",\n");
        envVars.append("        \"CB_PASSWORD\": \"").append(escapeJson(password)).append("\"");

        // Only add CB_MCP_READ_ONLY_MODE if it's false (true is the default)
        if (!settings.isReadOnlyMode()) {
            envVars.append(",\n        \"CB_MCP_READ_ONLY_MODE\": \"false\"");
        }

        // Add disabled tools
        List<String> disabledTools = new ArrayList<>(settings.getDisabledTools());
        if (!disabledTools.isEmpty()) {
            envVars.append(",\n        \"CB_MCP_DISABLED_TOOLS\": \"").append(String.join(",", disabledTools)).append("\"");
            Log.info("MCP: Disabled tools: " + disabledTools);
        }

        // Add confirmation required tools - this is an environment variable for the MCP server
        List<String> confirmationTools = settings.getConfirmationRequiredTools();
        if (confirmationTools != null && !confirmationTools.isEmpty()) {
            envVars.append(",\n        \"CB_MCP_CONFIRMATION_REQUIRED_TOOLS\": \"")
                    .append(String.join(",", confirmationTools)).append("\"");
            Log.info("MCP: Confirmation required tools: " + confirmationTools);
        }

        if (settings.getExportsPath() != null && !settings.getExportsPath().isEmpty()) {
            envVars.append(",\n        \"CB_MCP_EXPORTS_PATH\": \"").append(escapeJson(settings.getExportsPath())).append("\"");
        }

        // Use appropriate key based on the AI assistant
        String serversKey = useGeminiFormat ? "mcpServers" : "servers";

        String config = "{\n" +
                "  \"" + serversKey + "\": {\n" +
                "    \"couchbase\": {\n" +
                "      \"command\": \"uvx\",\n" +
                "      \"args\": [\"couchbase-mcp-server\"],\n" +
                "      \"env\": {\n" +
                envVars + "\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        Log.info("MCP: Config built successfully for " + (useGeminiFormat ? "Gemini" : "Copilot"));
        return config;
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

    private void writeMcpConfig(String config, Path configPath) throws IOException {
        Files.createDirectories(configPath.getParent());
        try (FileWriter writer = new FileWriter(configPath.toFile())) {
            writer.write(config);
        }
        Log.info("MCP config written to: " + configPath);
    }

    public void removeServerConfig() {
        try {
            // Remove Copilot config
            if (copilotConfigured) {
                Path copilotConfigPath = getCopilotMcpConfigPath();
                if (Files.exists(copilotConfigPath)) {
                    String emptyConfig = "{\n  \"servers\": {}\n}";
                    try (FileWriter writer = new FileWriter(copilotConfigPath.toFile())) {
                        writer.write(emptyConfig);
                    }
                    Log.info("Couchbase MCP server configuration removed from Copilot");
                }
            }

            // Remove Gemini config
            if (geminiConfigured) {
                Path geminiConfigPath = getGeminiMcpConfigPath();
                if (Files.exists(geminiConfigPath)) {
                    String emptyConfig = "{\n  \"mcpServers\": {}\n}";
                    try (FileWriter writer = new FileWriter(geminiConfigPath.toFile())) {
                        writer.write(emptyConfig);
                    }
                    Log.info("Couchbase MCP server configuration removed from Gemini");
                }
            }
        } catch (Exception e) {
            Log.error("Error removing MCP server configuration: " + e.getMessage());
        }
        currentCluster = null;
        currentPassword = null;
        isConfigured = false;
        copilotConfigured = false;
        geminiConfigured = false;
    }

    public void handleConnectionRemoved() {
        if (isConfigured) {
            Log.info("Connection removed, removing MCP server configuration");
            removeServerConfig();
        }
    }

    public void handleSettingsChanged() {
        Log.info("MCP: handleSettingsChanged called, isConfigured=" + isConfigured);
        if (isConfigured && currentCluster != null && currentPassword != null) {
            Log.info("MCP: Settings changed, forcing reconfiguration for cluster: " + currentCluster.getName());

            try {
                MCPSettingsStorage.State settings = MCPSettingsStorage.getInstance().getState();
                if (settings == null) {
                    settings = new MCPSettingsStorage.State();
                }

                List<String> updatedAssistants = new ArrayList<>();

                // Update Copilot config if it was configured
                if (copilotConfigured) {
                    String copilotConfig = buildMcpConfig(currentCluster, currentPassword, settings, false);
                    writeMcpConfig(copilotConfig, getCopilotMcpConfigPath());
                    updateSamplingConfig(settings);
                    updatedAssistants.add("Copilot");
                }

                // Update Gemini config if it was configured
                if (geminiConfigured) {
                    String geminiConfig = buildMcpConfig(currentCluster, currentPassword, settings, true);
                    writeMcpConfig(geminiConfig, getGeminiMcpConfigPath());
                    updatedAssistants.add("Gemini");
                }

                Log.info("MCP: Settings updated successfully for: " + String.join(", ", updatedAssistants));
                showSettingsChangedNotification(updatedAssistants);

            } catch (Exception e) {
                Log.error("Error updating MCP settings: " + e.getMessage());
                showErrorNotification("Failed to update MCP server settings: " + e.getMessage());
            }
        } else {
            Log.info("MCP: Settings changed but server not configured or no current cluster/password");
        }
    }

    private void showSettingsChangedNotification(List<String> assistants) {
        String assistantList = String.join(" and ", assistants);
        Notifications.Bus.notify(new Notification(
                "Couchbase",
                "MCP Settings Updated",
                "MCP configuration updated for " + assistantList + ". To apply changes:\n" +
                "• Reconnect to the cluster, OR\n" +
                "• Restart the AI assistant chat panel",
                NotificationType.INFORMATION
        ));
    }

    public boolean isRunning() {
        return isConfigured;
    }

    public SavedCluster getCurrentCluster() {
        return currentCluster;
    }

    private void showSuccessNotification(List<String> assistants) {
        String assistantList = String.join(" and ", assistants);
        Notification notification = new Notification(
                "Couchbase",
                "Couchbase MCP Server",
                "Couchbase MCP server configured for " + assistantList + ". " +
                "You may need to restart the AI chat panel for changes to take effect.",
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

