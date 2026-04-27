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

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Persistent storage for MCP (Model Context Protocol) server settings.
 * Settings are stored in couchbase-mcp-settings.xml and persist across IDE sessions.
 *
 * Note: Credentials are NOT stored here - they are retrieved securely from PasswordStorage
 * and passed to the MCP server process via environment variables (in memory only).
 */
@State(
        name = "CouchbaseMCPSettings",
        storages = @Storage("couchbase-mcp-settings.xml")
)
public class MCPSettingsStorage implements PersistentStateComponent<MCPSettingsStorage.State> {

    /**
     * Defines the auto-start behavior for the MCP server.
     */
    public enum AutoStartBehavior {
        /**
         * Show a prompt asking the user whether to start the MCP server.
         */
        PROMPT,

        /**
         * Automatically start the MCP server when connected to a Couchbase cluster.
         */
        AUTO_START_ENABLED,

        /**
         * Never automatically start the MCP server.
         */
        AUTO_START_DISABLED
    }

    private final State myState = new State();

    public static MCPSettingsStorage getInstance() {
        return ApplicationManager.getApplication().getService(MCPSettingsStorage.class);
    }

    @Nullable
    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        XmlSerializerUtil.copyBean(state, myState);
    }

    /**
     * MCP Settings state class containing all configurable options.
     */
    public static class State {
        /**
         * Controls whether MCP server auto-starts when connected to a cluster.
         * Default is PROMPT to ask the user on first connection.
         */
        public AutoStartBehavior autoStartBehavior = AutoStartBehavior.PROMPT;

        /**
         * When true, write operations (upsert, insert, replace, delete) are disabled.
         * Default is true for safety.
         */
        public boolean readOnlyMode = true;

        /**
         * List of tools that require user confirmation before execution.
         */
        public List<String> confirmationRequiredTools = new ArrayList<>();

        /**
         * List of tools that are disabled and cannot be used.
         */
        public List<String> disabledTools = new ArrayList<>();

        /**
         * Path where MCP exports data (e.g., query results).
         */
        public String exportsPath = "";

        /**
         * When true, sends usage data to Couchbase IQ for analytics.
         */
        public boolean sendToIQ = false;

        // Getters and setters for proper serialization

        public AutoStartBehavior getAutoStartBehavior() {
            return autoStartBehavior;
        }

        public void setAutoStartBehavior(AutoStartBehavior autoStartBehavior) {
            this.autoStartBehavior = autoStartBehavior;
        }

        public boolean isReadOnlyMode() {
            return readOnlyMode;
        }

        public void setReadOnlyMode(boolean readOnlyMode) {
            this.readOnlyMode = readOnlyMode;
        }

        public List<String> getConfirmationRequiredTools() {
            return confirmationRequiredTools;
        }

        public void setConfirmationRequiredTools(List<String> confirmationRequiredTools) {
            this.confirmationRequiredTools = confirmationRequiredTools;
        }

        public List<String> getDisabledTools() {
            return disabledTools;
        }

        public void setDisabledTools(List<String> disabledTools) {
            this.disabledTools = disabledTools;
        }

        public String getExportsPath() {
            return exportsPath;
        }

        public void setExportsPath(String exportsPath) {
            this.exportsPath = exportsPath;
        }

        public boolean isSendToIQ() {
            return sendToIQ;
        }

        public void setSendToIQ(boolean sendToIQ) {
            this.sendToIQ = sendToIQ;
        }
    }
}

