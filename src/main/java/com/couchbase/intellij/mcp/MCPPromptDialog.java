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

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Dialog that prompts the user to choose MCP server auto-start behavior.
 * Shown when connecting to a Couchbase cluster for the first time with Copilot installed.
 */
public class MCPPromptDialog extends DialogWrapper {

    private MCPSettingsStorage.AutoStartBehavior selectedBehavior = MCPSettingsStorage.AutoStartBehavior.AUTO_START_DISABLED;
    private boolean startOnce = false;

    public MCPPromptDialog() {
        super(true);
        setTitle("Couchbase MCP Server");
        setOKButtonText("Auto-Start");
        setCancelButtonText("Never");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(450, 120));

        JBLabel messageLabel = new JBLabel(
                "<html><body style='width: 400px;'>" +
                "Would you like to automatically start the Couchbase MCP server for a streamlined experience?<br><br>" +
                "When started, the server will automatically connect to your active Couchbase instance." +
                "</body></html>"
        );
        messageLabel.setBorder(JBUI.Borders.empty(10));

        panel.add(messageLabel, BorderLayout.CENTER);

        return panel;
    }

    @Override
    protected Action[] createActions() {
        return new Action[]{
                new DialogWrapperAction("Auto-Start") {
                    @Override
                    protected void doAction(ActionEvent e) {
                        selectedBehavior = MCPSettingsStorage.AutoStartBehavior.AUTO_START_ENABLED;
                        startOnce = false;
                        close(OK_EXIT_CODE);
                    }
                },
                new DialogWrapperAction("Start Once") {
                    @Override
                    protected void doAction(ActionEvent e) {
                        // Keep behavior as PROMPT so we ask again next time
                        selectedBehavior = MCPSettingsStorage.AutoStartBehavior.PROMPT;
                        startOnce = true;
                        close(OK_EXIT_CODE);
                    }
                },
                new DialogWrapperAction("Never") {
                    @Override
                    protected void doAction(ActionEvent e) {
                        selectedBehavior = MCPSettingsStorage.AutoStartBehavior.AUTO_START_DISABLED;
                        startOnce = false;
                        close(CANCEL_EXIT_CODE);
                    }
                }
        };
    }

    /**
     * Gets the user's selected auto-start behavior.
     *
     * @return the selected behavior
     */
    public MCPSettingsStorage.AutoStartBehavior getSelectedBehavior() {
        return selectedBehavior;
    }

    /**
     * Checks if the user chose to start the server just this once.
     *
     * @return true if user selected "Start Once"
     */
    public boolean isStartOnce() {
        return startOnce;
    }
}

