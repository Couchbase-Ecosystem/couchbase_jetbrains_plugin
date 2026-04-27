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
 * Utility class to detect if GitHub Copilot is installed in the IDE.
 * The MCP server integration is only relevant when Copilot is available.
 */
public final class CopilotDetector {

    private CopilotDetector() {
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
     * Cached detection result to avoid repeated plugin lookups.
     */
    private static Boolean cachedResult = null;

    /**
     * Checks if any GitHub Copilot plugin is installed and enabled.
     *
     * @return true if Copilot is installed and enabled, false otherwise
     */
    public static boolean isCopilotInstalled() {
        if (cachedResult != null) {
            return cachedResult;
        }

        cachedResult = detectCopilot();
        Log.debug("MCPLogId: " + MCPLogIds.COPILOT_DETECTION +
                " - Copilot detection result: " + cachedResult);
        return cachedResult;
    }

    /**
     * Performs the actual detection of Copilot plugins.
     */
    private static boolean detectCopilot() {
        for (String pluginIdStr : COPILOT_PLUGIN_IDS) {
            try {
                PluginId pluginId = PluginId.getId(pluginIdStr);
                if (PluginManagerCore.isPluginInstalled(pluginId)) {
                    // Also check if the plugin is enabled
                    if (!PluginManagerCore.isDisabled(pluginId)) {
                        Log.info("GitHub Copilot detected: " + pluginIdStr);
                        return true;
                    }
                }
            } catch (Exception e) {
                Log.debug("Error checking for Copilot plugin " + pluginIdStr + ": " + e.getMessage());
            }
        }
        return false;
    }

    /**
     * Clears the cached detection result.
     * Useful if plugins may have been installed/uninstalled during the session.
     */
    public static void clearCache() {
        cachedResult = null;
    }
}

