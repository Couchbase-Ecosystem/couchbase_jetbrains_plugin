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

import com.couchbase.intellij.workbench.Log;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;

/**
 * Utility class to detect if AI assistants (GitHub Copilot, Gemini Code Assist) are installed in the IDE.
 * The MCP server integration is only relevant when these assistants are available.
 */
public final class AIAssistantDetector {

    private AIAssistantDetector() {
        // Utility class, prevent instantiation
    }

    /**
     * Known GitHub Copilot plugin IDs.
     */
    private static final String[] COPILOT_PLUGIN_IDS = {
            "com.github.copilot",        // Main Copilot plugin
            "com.github.copilot.chat"    // Copilot Chat plugin
    };

    /**
     * Known Google Gemini Code Assist plugin IDs.
     */
    private static final String[] GEMINI_PLUGIN_IDS = {
            "com.google.cloudcode.gemini",        // Gemini Code Assist main plugin
            "com.google.geminicodeassist",        // Alternative Gemini plugin ID
            "com.google.cloud.tools.intellij.gemini"  // Cloud Code Gemini integration
    };

    /**
     * Cached detection results to avoid repeated plugin lookups.
     */
    private static Boolean cachedCopilotResult = null;
    private static Boolean cachedGeminiResult = null;

    /**
     * Checks if any GitHub Copilot plugin is installed and enabled.
     *
     * @return true if Copilot is installed and enabled, false otherwise
     */
    public static boolean isCopilotInstalled() {
        if (cachedCopilotResult != null) {
            return cachedCopilotResult;
        }

        cachedCopilotResult = detectPlugin(COPILOT_PLUGIN_IDS, "Copilot");
        Log.debug("MCPLogId: " + MCPLogIds.COPILOT_DETECTION +
                " - Copilot detection result: " + cachedCopilotResult);
        return cachedCopilotResult;
    }

    /**
     * Checks if Google Gemini Code Assist plugin is installed and enabled.
     *
     * @return true if Gemini is installed and enabled, false otherwise
     */
    public static boolean isGeminiInstalled() {
        if (cachedGeminiResult != null) {
            return cachedGeminiResult;
        }

        cachedGeminiResult = detectPlugin(GEMINI_PLUGIN_IDS, "Gemini");
        Log.debug("MCPLogId: " + MCPLogIds.COPILOT_DETECTION +
                " - Gemini detection result: " + cachedGeminiResult);
        return cachedGeminiResult;
    }

    /**
     * Checks if any AI assistant (Copilot or Gemini) is installed.
     *
     * @return true if at least one AI assistant is available
     */
    public static boolean isAnyAIAssistantInstalled() {
        return isCopilotInstalled() || isGeminiInstalled();
    }

    /**
     * Performs the actual detection of plugins.
     */
    private static boolean detectPlugin(String[] pluginIds, String pluginName) {
        for (String pluginIdStr : pluginIds) {
            try {
                PluginId pluginId = PluginId.getId(pluginIdStr);
                if (PluginManagerCore.isPluginInstalled(pluginId)) {
                    // Also check if the plugin is enabled
                    if (!PluginManagerCore.isDisabled(pluginId)) {
                        Log.info(pluginName + " detected: " + pluginIdStr);
                        return true;
                    }
                }
            } catch (Exception e) {
                Log.debug("Error checking for " + pluginName + " plugin " + pluginIdStr + ": " + e.getMessage());
            }
        }
        return false;
    }

    /**
     * Clears the cached detection results.
     * Useful if plugins may have been installed/uninstalled during the session.
     */
    public static void clearCache() {
        cachedCopilotResult = null;
        cachedGeminiResult = null;
    }
}
