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

/**
 * Log identifiers for MCP operations.
 * These IDs help track and categorize MCP-related log entries.
 */
public final class MCPLogIds {

    private MCPLogIds() {
        // Utility class, prevent instantiation
    }

    public static final String SERVER_START_ERROR = "MCP_SERVER_START_ERROR";
    public static final String SERVER_STOP_ERROR = "MCP_SERVER_STOP_ERROR";
    public static final String CONNECTION_UPDATE_ERROR = "MCP_CONNECTION_UPDATE_ERROR";
    public static final String SETTINGS_ERROR = "MCP_SETTINGS_ERROR";
    public static final String COPILOT_DETECTION = "MCP_COPILOT_DETECTION";
}

