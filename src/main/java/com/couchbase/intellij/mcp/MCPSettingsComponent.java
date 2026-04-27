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

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UI component for MCP Settings panel.
 * Provides controls for configuring MCP server behavior.
 */
public class MCPSettingsComponent {

    private final JPanel mainPanel;

    // UI Components
    private final JBCheckBox readOnlyModeCheckBox;
    private final JBCheckBox sendToIQCheckBox;
    private final ComboBox<String> autoStartComboBox;
    private final TextFieldWithBrowseButton exportsPathField;
    private final DefaultListModel<String> confirmationToolsModel;
    private final DefaultListModel<String> disabledToolsModel;
    private final JBList<String> confirmationToolsList;
    private final JBList<String> disabledToolsList;

    /**
     * Available tools that can be configured for confirmation or disabling.
     */
    private static final String[] AVAILABLE_TOOLS = {
            "execute_query",
            "get_document_by_id",
            "upsert_document_by_id",
            "insert_document_by_id",
            "replace_document_by_id",
            "delete_document_by_id",
            "list_buckets",
            "list_scopes",
            "list_collections",
            "list_indexes",
            "create_primary_index",
            "create_index",
            "drop_index",
            "get_index_status"
    };

    public MCPSettingsComponent() {
        // Initialize components
        readOnlyModeCheckBox = new JBCheckBox("Read-Only Mode (disables write operations)");
        sendToIQCheckBox = new JBCheckBox("Send usage data to Couchbase IQ (helps improve performance)");

        autoStartComboBox = new ComboBox<>(new String[]{
                "Prompt on connection",
                "Auto-start when connected",
                "Never auto-start"
        });

        exportsPathField = new TextFieldWithBrowseButton();
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        descriptor.setTitle("Select Exports Directory");
        descriptor.setDescription("Choose the directory where MCP will export data");
        exportsPathField.addBrowseFolderListener(null, descriptor);

        // Confirmation tools list
        confirmationToolsModel = new DefaultListModel<>();
        confirmationToolsList = new JBList<>(confirmationToolsModel);
        confirmationToolsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Disabled tools list
        disabledToolsModel = new DefaultListModel<>();
        disabledToolsList = new JBList<>(disabledToolsModel);
        disabledToolsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Build the main panel
        mainPanel = buildPanel();

        // Load current settings
        reset();
    }

    private JPanel buildPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(JBUI.Borders.empty(10));

        // Create sections
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));

        // Server Behavior Section
        JPanel behaviorSection = createSectionPanel("Server Behavior");
        behaviorSection.add(createLabeledComponent("Auto-start behavior:", autoStartComboBox));
        behaviorSection.add(Box.createVerticalStrut(10));
        behaviorSection.add(readOnlyModeCheckBox);
        settingsPanel.add(behaviorSection);
        settingsPanel.add(Box.createVerticalStrut(15));

        // Export Settings Section
        JPanel exportSection = createSectionPanel("Export Settings");
        exportSection.add(createLabeledComponent("Exports data path:", exportsPathField));
        settingsPanel.add(exportSection);
        settingsPanel.add(Box.createVerticalStrut(15));

        // Tools Configuration Section
        JPanel toolsSection = createSectionPanel("Tools Configuration");

        // Confirmation Required Tools
        JPanel confirmationPanel = new JPanel(new BorderLayout());
        confirmationPanel.setBorder(JBUI.Borders.emptyTop(5));
        JBLabel confirmLabel = new JBLabel("Confirmation Required Tools:");
        confirmLabel.setBorder(JBUI.Borders.emptyBottom(5));
        confirmationPanel.add(confirmLabel, BorderLayout.NORTH);
        confirmationPanel.add(createToolsListPanel(confirmationToolsList, confirmationToolsModel), BorderLayout.CENTER);
        toolsSection.add(confirmationPanel);

        toolsSection.add(Box.createVerticalStrut(10));

        // Disabled Tools
        JPanel disabledPanel = new JPanel(new BorderLayout());
        disabledPanel.setBorder(JBUI.Borders.emptyTop(5));
        JBLabel disabledLabel = new JBLabel("Disabled Tools:");
        disabledLabel.setBorder(JBUI.Borders.emptyBottom(5));
        disabledPanel.add(disabledLabel, BorderLayout.NORTH);
        disabledPanel.add(createToolsListPanel(disabledToolsList, disabledToolsModel), BorderLayout.CENTER);
        toolsSection.add(disabledPanel);

        settingsPanel.add(toolsSection);
        settingsPanel.add(Box.createVerticalStrut(15));

        // Analytics Section
        JPanel analyticsSection = createSectionPanel("Analytics");
        analyticsSection.add(sendToIQCheckBox);
        settingsPanel.add(analyticsSection);

        // Add info label at the bottom
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(JBUI.Borders.emptyTop(15));
        JBLabel infoLabel = new JBLabel(
                "<html><body style='color: gray; font-size: 11px;'>" +
                "Note: Credentials are retrieved from secure storage and passed to the MCP server " +
                "via environment variables. They are never persisted to disk or logged." +
                "</body></html>"
        );
        infoPanel.add(infoLabel, BorderLayout.CENTER);
        settingsPanel.add(infoPanel);

        panel.add(new JBScrollPane(settingsPanel), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSectionPanel(String title) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(title),
                JBUI.Borders.empty(5, 10)
        ));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        return section;
    }

    private JPanel createLabeledComponent(String label, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JBLabel jLabel = new JBLabel(label);
        jLabel.setPreferredSize(new Dimension(150, jLabel.getPreferredSize().height));
        panel.add(jLabel, BorderLayout.WEST);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createToolsListPanel(JBList<String> list, DefaultListModel<String> model) {
        JPanel panel = ToolbarDecorator.createDecorator(list)
                .setAddAction(button -> {
                    String[] availableTools = getAvailableToolsNotInList(model);
                    if (availableTools.length > 0) {
                        String selected = (String) JOptionPane.showInputDialog(
                                mainPanel,
                                "Select a tool to add:",
                                "Add Tool",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                availableTools,
                                availableTools[0]
                        );
                        if (selected != null && !selected.isEmpty()) {
                            model.addElement(selected);
                        }
                    } else {
                        JOptionPane.showMessageDialog(mainPanel,
                                "All available tools are already in the list.",
                                "No Tools Available",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                })
                .setRemoveAction(button -> {
                    int[] indices = list.getSelectedIndices();
                    for (int i = indices.length - 1; i >= 0; i--) {
                        model.remove(indices[i]);
                    }
                })
                .disableUpDownActions()
                .createPanel();
        panel.setPreferredSize(new Dimension(-1, 120));
        return panel;
    }

    private String[] getAvailableToolsNotInList(DefaultListModel<String> model) {
        List<String> available = new ArrayList<>();
        for (String tool : AVAILABLE_TOOLS) {
            if (!model.contains(tool)) {
                available.add(tool);
            }
        }
        return available.toArray(new String[0]);
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public boolean isModified() {
        MCPSettingsStorage.State state = MCPSettingsStorage.getInstance().getState();
        if (state == null) {
            return false;
        }

        return readOnlyModeCheckBox.isSelected() != state.isReadOnlyMode()
                || sendToIQCheckBox.isSelected() != state.isSendToIQ()
                || !getExportsPath().equals(state.getExportsPath())
                || getAutoStartBehaviorIndex() != getIndexForBehavior(state.getAutoStartBehavior())
                || !getListAsStrings(confirmationToolsModel).equals(state.getConfirmationRequiredTools())
                || !getListAsStrings(disabledToolsModel).equals(state.getDisabledTools());
    }

    public void apply() {
        MCPSettingsStorage.State state = MCPSettingsStorage.getInstance().getState();
        if (state == null) {
            return;
        }

        state.setReadOnlyMode(readOnlyModeCheckBox.isSelected());
        state.setSendToIQ(sendToIQCheckBox.isSelected());
        state.setExportsPath(getExportsPath());
        state.setAutoStartBehavior(getBehaviorForIndex(autoStartComboBox.getSelectedIndex()));
        state.setConfirmationRequiredTools(getListAsStrings(confirmationToolsModel));
        state.setDisabledTools(getListAsStrings(disabledToolsModel));

        // Notify MCP server manager to restart if running
        MCPServerManager manager = MCPServerManager.getInstance();
        if (manager != null) {
            manager.handleSettingsChanged();
        }
    }

    public void reset() {
        MCPSettingsStorage.State state = MCPSettingsStorage.getInstance().getState();
        if (state == null) {
            return;
        }

        readOnlyModeCheckBox.setSelected(state.isReadOnlyMode());
        sendToIQCheckBox.setSelected(state.isSendToIQ());
        exportsPathField.setText(state.getExportsPath());
        autoStartComboBox.setSelectedIndex(getIndexForBehavior(state.getAutoStartBehavior()));

        confirmationToolsModel.clear();
        for (String tool : state.getConfirmationRequiredTools()) {
            confirmationToolsModel.addElement(tool);
        }

        disabledToolsModel.clear();
        for (String tool : state.getDisabledTools()) {
            disabledToolsModel.addElement(tool);
        }
    }

    private String getExportsPath() {
        return exportsPathField.getText() != null ? exportsPathField.getText().trim() : "";
    }

    private int getAutoStartBehaviorIndex() {
        return autoStartComboBox.getSelectedIndex();
    }

    private int getIndexForBehavior(MCPSettingsStorage.AutoStartBehavior behavior) {
        switch (behavior) {
            case AUTO_START_ENABLED:
                return 1;
            case AUTO_START_DISABLED:
                return 2;
            case PROMPT:
            default:
                return 0;
        }
    }

    private MCPSettingsStorage.AutoStartBehavior getBehaviorForIndex(int index) {
        switch (index) {
            case 1:
                return MCPSettingsStorage.AutoStartBehavior.AUTO_START_ENABLED;
            case 2:
                return MCPSettingsStorage.AutoStartBehavior.AUTO_START_DISABLED;
            case 0:
            default:
                return MCPSettingsStorage.AutoStartBehavior.PROMPT;
        }
    }

    private List<String> getListAsStrings(DefaultListModel<String> model) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < model.size(); i++) {
            list.add(model.get(i));
        }
        return list;
    }
}

