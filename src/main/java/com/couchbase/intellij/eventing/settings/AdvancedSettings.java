package com.couchbase.intellij.eventing.settings;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;

public class AdvancedSettings {

        private JPanel advancedSettingsPanel;
        private JPanel systemLogLevelPanel;
        private JPanel n1qlConsistencyPanel;
        private JPanel workersPanel;
        private JPanel languageCompatibilityPanel;
        private JPanel scriptTimeoutPanel;
        private JPanel timerContextMaxSizePanel;

        private JBLabel systemLogLevelLabel;
        private JBLabel systemLogLevelInfoLabel;
        private JBLabel applicationLogFileLabel;
        private JBLabel n1qlConsistencyLabel;
        private JBLabel n1qlConsistencyInfoLabel;
        private JBLabel workersLabel;
        private JBLabel workersInfoLabel;
        private JBLabel languageCompatibilityLabel;
        private JBLabel languageCompatibilityInfoLabel;
        private JBLabel scriptTimeoutLabel;
        private JBLabel scriptTimeoutInfoLabel;
        private JBLabel timerContextMaxSizeLabel;
        private JBLabel timerContextMaxSizeInfoLabel;

        private JComboBox<String> systemLogLevelComboBox;
        private JComboBox<String> n1qlConsistencyComboBox;
        private JComboBox<String> languageCompatibilityComboBox;

        private JBTextField workersField;
        private JBTextField scriptTimeoutField;
        private JBTextField timerContextMaxSizeField;

        public AdvancedSettings() {
                // Advanced Settings panel
                advancedSettingsPanel = new JPanel();
                advancedSettingsPanel.setLayout(new GridBagLayout());
                GridBagConstraints advancedSettingsPanelConstraints = new GridBagConstraints();
                advancedSettingsPanelConstraints.gridx = 0;
                advancedSettingsPanelConstraints.gridy = 0;

                advancedSettingsPanelConstraints.anchor = GridBagConstraints.NORTHWEST;
                advancedSettingsPanelConstraints.insets = new Insets(5, 5, 5, 5);
                advancedSettingsPanelConstraints.fill = GridBagConstraints.HORIZONTAL;

                // Add components to the Advanced Settings panel
                systemLogLevelLabel = new JBLabel("System Log Level");
                systemLogLevelLabel.setFont(systemLogLevelLabel.getFont().deriveFont(Font.BOLD));
                // advancedSettingsPanelConstraints.gridx = 0;
                // advancedSettingsPanelConstraints.gridy = 0;
                // advancedSettingsPanel.add(systemLogLevelLabel,
                // advancedSettingsPanelConstraints);

                systemLogLevelInfoLabel = new JBLabel("Granularity of system events being captured in the log");
                systemLogLevelInfoLabel.setForeground(JBColor.GRAY);
                // advancedSettingsPanelConstraints.gridx = 1;
                // advancedSettingsPanelConstraints.gridy = 0;
                // advancedSettingsPanel.add(systemLogLevelInfoLabel,
                // advancedSettingsPanelConstraints);

                // Add both the labels to a panel
                systemLogLevelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                systemLogLevelPanel.add(systemLogLevelLabel);
                systemLogLevelPanel.add(systemLogLevelInfoLabel);
                advancedSettingsPanelConstraints.gridx = 0;
                advancedSettingsPanelConstraints.gridy = 0;
                advancedSettingsPanel.add(systemLogLevelPanel, advancedSettingsPanelConstraints);

                systemLogLevelComboBox = new JComboBox<>();
                systemLogLevelComboBox.addItem("Info");
                systemLogLevelComboBox.addItem("Error");
                systemLogLevelComboBox.addItem("Warning");
                systemLogLevelComboBox.addItem("Debug");
                systemLogLevelComboBox.addItem("Trace");
                advancedSettingsPanelConstraints.gridx = 0;
                advancedSettingsPanelConstraints.gridy = 1;
                advancedSettingsPanel.add(systemLogLevelComboBox, advancedSettingsPanelConstraints);

                applicationLogFileLabel = new JBLabel(
                                "Application log file for this Function is at:");

                advancedSettingsPanelConstraints.gridx = 0;
                advancedSettingsPanelConstraints.gridy = 2;
                advancedSettingsPanel.add(applicationLogFileLabel, advancedSettingsPanelConstraints);

                n1qlConsistencyLabel = new JBLabel("N1QL Consistency");
                n1qlConsistencyLabel.setFont(n1qlConsistencyLabel.getFont().deriveFont(Font.BOLD));
                // advancedSettingsPanelConstraints.gridx = 0;
                // advancedSettingsPanelConstraints.gridy = 3;
                // advancedSettingsPanel.add(n1qlConsistencyLabel,
                // advancedSettingsPanelConstraints);

                n1qlConsistencyInfoLabel = new JBLabel("Consistency level of N1QL statements in the function");
                n1qlConsistencyInfoLabel.setForeground(JBColor.GRAY);
                // advancedSettingsPanelConstraints.gridx = 1;
                // advancedSettingsPanelConstraints.gridy = 3;
                // advancedSettingsPanel.add(n1qlConsistencyInfoLabel,
                // advancedSettingsPanelConstraints);

                // Add both the labels to a panel
                n1qlConsistencyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                n1qlConsistencyPanel.add(n1qlConsistencyLabel);
                n1qlConsistencyPanel.add(n1qlConsistencyInfoLabel);
                advancedSettingsPanelConstraints.gridx = 0;
                advancedSettingsPanelConstraints.gridy = 3;
                advancedSettingsPanel.add(n1qlConsistencyPanel, advancedSettingsPanelConstraints);

                n1qlConsistencyComboBox = new JComboBox<>();
                n1qlConsistencyComboBox.addItem("None");
                n1qlConsistencyComboBox.addItem("Request");
                advancedSettingsPanelConstraints.gridx = 0;
                advancedSettingsPanelConstraints.gridy = 4;

                advancedSettingsPanel.add(n1qlConsistencyComboBox, advancedSettingsPanelConstraints);

                workersLabel = new JBLabel("Workers");
                workersLabel.setFont(workersLabel.getFont().deriveFont(Font.BOLD));
                // advancedSettingsPanelConstraints.gridx = 0;
                // advancedSettingsPanelConstraints.gridy = 5;
                // advancedSettingsPanel.add(workersLabel, advancedSettingsPanelConstraints);

                workersInfoLabel = new JBLabel(
                                "Number of workers per node to process the events. If no value is specified, a default value of 1 worker is used.");
                workersInfoLabel.setForeground(JBColor.GRAY);
                // advancedSettingsPanelConstraints.gridx = 1;
                // advancedSettingsPanelConstraints.gridy = 5;
                // advancedSettingsPanel.add(workersInfoLabel,
                // advancedSettingsPanelConstraints);

                // Add both the labels to a panel
                workersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                workersPanel.add(workersLabel);
                workersPanel.add(workersInfoLabel);
                advancedSettingsPanelConstraints.gridx = 0;
                advancedSettingsPanelConstraints.gridy = 5;
                advancedSettingsPanel.add(workersPanel, advancedSettingsPanelConstraints);

                workersField = new JBTextField(20);
                workersField.setToolTipText("Enter the number of workers per node to process the events.");
                workersField.setText("1");
                advancedSettingsPanelConstraints.gridx = 0;
                advancedSettingsPanelConstraints.gridy = 6;
                advancedSettingsPanel.add(workersField, advancedSettingsPanelConstraints);

                languageCompatibilityLabel = new JBLabel("Language compatibility");

                languageCompatibilityLabel.setFont(languageCompatibilityLabel.getFont().deriveFont(Font.BOLD));
                // advancedSettingsPanelConstraints.gridx = 0;
                // advancedSettingsPanelConstraints.gridy = 7;
                // advancedSettingsPanel.add(languageCompatibilityLabel,
                // advancedSettingsPanelConstraints);

                languageCompatibilityInfoLabel = new JBLabel("Language compatibility of the function");
                languageCompatibilityInfoLabel.setForeground(JBColor.GRAY);
                // advancedSettingsPanelConstraints.gridx = 1;
                // advancedSettingsPanelConstraints.gridy = 7;
                // advancedSettingsPanel.add(languageCompatibilityInfoLabel,
                // advancedSettingsPanelConstraints);

                // Add both the labels to a panel
                languageCompatibilityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                languageCompatibilityPanel.add(languageCompatibilityLabel);
                languageCompatibilityPanel.add(languageCompatibilityInfoLabel);
                advancedSettingsPanelConstraints.gridx = 0;
                advancedSettingsPanelConstraints.gridy = 7;
                advancedSettingsPanel.add(languageCompatibilityPanel, advancedSettingsPanelConstraints);

                languageCompatibilityComboBox = new JComboBox<>();
                languageCompatibilityComboBox.addItem("6.0.0");
                languageCompatibilityComboBox.addItem("6.5.0");
                languageCompatibilityComboBox.addItem("6.6.2");
                advancedSettingsPanelConstraints.gridx = 0;
                advancedSettingsPanelConstraints.gridy = 8;
                advancedSettingsPanel.add(languageCompatibilityComboBox, advancedSettingsPanelConstraints);

                scriptTimeoutLabel = new JBLabel("Script Timeout");

                scriptTimeoutLabel.setFont(scriptTimeoutLabel.getFont().deriveFont(Font.BOLD));
                // advancedSettingsPanelConstraints.gridx = 0;
                // advancedSettingsPanelConstraints.gridy = 9;
                // advancedSettingsPanel.add(scriptTimeoutLabel,
                // advancedSettingsPanelConstraints);

                scriptTimeoutInfoLabel = new JBLabel(
                                "Time after which the Function's execution will be timed out");
                scriptTimeoutInfoLabel.setForeground(JBColor.GRAY);
                // advancedSettingsPanelConstraints.gridx = 1;
                // advancedSettingsPanelConstraints.gridy = 9;
                // advancedSettingsPanel.add(scriptTimeoutInfoLabel,
                // advancedSettingsPanelConstraints);

                // Add both the labels to a panel
                scriptTimeoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                scriptTimeoutPanel.add(scriptTimeoutLabel);
                scriptTimeoutPanel.add(scriptTimeoutInfoLabel);
                advancedSettingsPanelConstraints.gridx = 0;
                advancedSettingsPanelConstraints.gridy = 9;
                advancedSettingsPanel.add(scriptTimeoutPanel, advancedSettingsPanelConstraints);

                scriptTimeoutField = new JBTextField(20);
                scriptTimeoutField.setText("60");
                scriptTimeoutField.setToolTipText(
                                "Enter the time after which the Function's execution will be timed out.");
                advancedSettingsPanelConstraints.gridx = 0;
                advancedSettingsPanelConstraints.gridy = 10;
                advancedSettingsPanel.add(scriptTimeoutField, advancedSettingsPanelConstraints);

                timerContextMaxSizeLabel = new JBLabel("Timer Context Max Size");

                timerContextMaxSizeLabel.setFont(timerContextMaxSizeLabel.getFont().deriveFont(Font.BOLD));
                // advancedSettingsPanelConstraints.gridx = 0;
                // advancedSettingsPanelConstraints.gridy = 11;
                // advancedSettingsPanel.add(timerContextMaxSizeLabel,
                // advancedSettingsPanelConstraints);

                timerContextMaxSizeInfoLabel = new JBLabel(
                                "Maximum allowed value of the Timer Context Size in Bytes. Takes effect immediately.");
                timerContextMaxSizeInfoLabel.setForeground(JBColor.GRAY);
                // advancedSettingsPanelConstraints.gridx = 1;
                // advancedSettingsPanelConstraints.gridy = 11;
                // advancedSettingsPanel.add(timerContextMaxSizeInfoLabel,
                // advancedSettingsPanelConstraints);

                // Add both the labels to a panel
                timerContextMaxSizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                timerContextMaxSizePanel.add(timerContextMaxSizeLabel);
                timerContextMaxSizePanel.add(timerContextMaxSizeInfoLabel);
                advancedSettingsPanelConstraints.gridx = 0;
                advancedSettingsPanelConstraints.gridy = 11;
                advancedSettingsPanel.add(timerContextMaxSizePanel, advancedSettingsPanelConstraints);

                timerContextMaxSizeField = new JBTextField(20);
                timerContextMaxSizeField.setText("1024");
                timerContextMaxSizeField.setToolTipText(
                                "Enter the maximum allowed value of the Timer Context Size in Bytes.");
                advancedSettingsPanelConstraints.gridx = 0;
                advancedSettingsPanelConstraints.gridy = 12;
                advancedSettingsPanel.add(timerContextMaxSizeField, advancedSettingsPanelConstraints);
        }

        public JPanel getPanel() {
                return advancedSettingsPanel;
        }
}