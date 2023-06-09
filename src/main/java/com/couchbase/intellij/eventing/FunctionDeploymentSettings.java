package com.couchbase.intellij.eventing;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
// import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
// import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
// import javax.swing.JScrollPane;
// import javax.swing.JTextArea;
// import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.intellij.ui.JBColor;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;

public class FunctionDeploymentSettings extends JFrame {
        private JPanel mainPanel;
        private JPanel leftPanel;
        private JPanel rightPanel;
        private JPanel bottomPanel;
        private JPanel bindingsPanel;
        private JBLabel titleLabel;

        private JButton applyButton;
        private JButton cancelButton;

        private CardLayout cardLayout;
        private Map<String, Boolean> changedSettings;

        private Integer bindingTypeCount = 0;

        public FunctionDeploymentSettings() {
                // Set up the main frame
                setTitle("Project Structure");
                setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                setSize(1000, 800);
                setLocationRelativeTo(null);

                // Create and configure the main panel
                mainPanel = new JPanel();
                mainPanel.setLayout(new BorderLayout());
                // JScrollPane scrollPane = new JScrollPane(mainPanel);
                setContentPane(mainPanel);

                // Create and configure the left panel
                leftPanel = new JPanel();
                leftPanel.setLayout(new BorderLayout());
                leftPanel.setPreferredSize(new Dimension(400, 800));
                // leftPanel.setForeground(JBColor.BLACK);
                leftPanel.setBackground(Color.BLACK);

                // Add components to the left panel
                titleLabel = new JBLabel("Project Structure");
                titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
                titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                titleLabel.setBackground(Color.BLACK);
                leftPanel.add(titleLabel, BorderLayout.NORTH);

                String[] settings = { "General Settings", "Advanced Settings", "Binding Type" };
                JList<String> settingsList = new JList<>(settings);
                settingsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                settingsList.setSelectedIndex(0);

                settingsList.setCellRenderer(new DefaultListCellRenderer() {
                        @Override
                        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                        boolean isSelected, boolean cellHasFocus) {
                                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                                setHorizontalAlignment(SwingConstants.LEFT);
                                setForeground(Color.WHITE);
                                if (isSelected) {
                                        setBackground(new Color(98, 181, 229));
                                } else {
                                        setBackground(Color.BLACK);
                                }
                                return this;
                        }
                });

                settingsList.addListSelectionListener(e -> {
                        if (!e.getValueIsAdjusting()) {
                                String selectedValue = settingsList.getSelectedValue();
                                CardLayout cardLayout = (CardLayout) rightPanel.getLayout();
                                cardLayout.show(rightPanel, selectedValue);
                        }
                });

                JBScrollPane leftScrollPane = new JBScrollPane(settingsList);
                leftScrollPane.setBorder(BorderFactory.createEmptyBorder());
                leftScrollPane.setBackground(JBColor.BLACK);
                leftPanel.add(leftScrollPane, BorderLayout.CENTER);

                // Create and configure the right panel
                rightPanel = new JPanel();
                cardLayout = new CardLayout();
                rightPanel.setLayout(cardLayout);

                // Add components to the right panel
                JPanel generalSettingsPanel = new JPanel();
                generalSettingsPanel.setLayout(new GridBagLayout());
                generalSettingsPanel.setPreferredSize(new Dimension(600, 800));

                GridBagConstraints rightPanelConstraints = new GridBagConstraints();
                rightPanelConstraints.gridx = 0;
                rightPanelConstraints.gridy = 0;
                rightPanelConstraints.anchor = GridBagConstraints.NORTHWEST;
                rightPanelConstraints.insets = JBUI.insets(5);
                rightPanelConstraints.weightx = 1.0;
                rightPanelConstraints.weighty = 1.0;
                rightPanelConstraints.fill = GridBagConstraints.HORIZONTAL;

                // Function Scope label
                JBLabel functionScopeLabel = new JBLabel("Function scope");
                functionScopeLabel.setFont(functionScopeLabel.getFont().deriveFont(Font.BOLD));

                JBLabel functionScopeInfoLabel = new JBLabel("bucket.scope");
                functionScopeInfoLabel.setForeground(JBColor.GRAY);

                // Add both labels to a flow layout
                JPanel functionScopePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                functionScopePanel.add(functionScopeLabel);
                functionScopePanel.add(functionScopeInfoLabel);
                rightPanelConstraints.gridx = 0;
                rightPanelConstraints.gridy = 0;
                generalSettingsPanel.add(functionScopePanel, rightPanelConstraints);

                JComboBox<String> bucketComboBox1 = new JComboBox<>();
                // bucketComboBox1.addActionListener(e -> updateScopes());
                rightPanelConstraints.gridx = 0;
                rightPanelConstraints.gridy = 1;
                generalSettingsPanel.add(bucketComboBox1, rightPanelConstraints);

                JComboBox<String> scopeComboBox1 = new JComboBox<>();
                scopeComboBox1.setEnabled(false);
                rightPanelConstraints.gridx = 1;
                rightPanelConstraints.gridy = 1;
                generalSettingsPanel.add(scopeComboBox1, rightPanelConstraints);

                // Function Scope error label

                JBLabel helpLabel1 = new JBLabel(
                                "Please specify a scope to which the function belongs. User should have Eventing Manage Scope Functions permission on this scope");
                helpLabel1.setForeground(JBColor.GRAY);
                rightPanelConstraints.gridx = 0;
                rightPanelConstraints.gridy = 2;
                rightPanelConstraints.gridwidth = 3;
                generalSettingsPanel.add(helpLabel1, rightPanelConstraints);

                // Listen to location label
                JBLabel listenToLocationLabel = new JBLabel("Listen to location");

                listenToLocationLabel.setFont(listenToLocationLabel.getFont().deriveFont(Font.BOLD));
                // rightPanelConstraints.gridx = 0;
                // rightPanelConstraints.gridy = 3;
                // generalSettingsPanel.add(listenToLocationLabel, rightPanelConstraints);

                JBLabel listenToLocationInfoLabel = new JBLabel("bucket.scope.collection");
                listenToLocationInfoLabel.setForeground(JBColor.GRAY);
                // rightPanelConstraints.gridx = 1;
                // rightPanelConstraints.gridy = 3;
                // generalSettingsPanel.add(listenToLocationInfoLabel, rightPanelConstraints);

                // Add both labels to a flow layout
                JPanel listenToLocationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                listenToLocationPanel.add(listenToLocationLabel);
                listenToLocationPanel.add(listenToLocationInfoLabel);
                rightPanelConstraints.gridx = 0;
                rightPanelConstraints.gridy = 3;
                generalSettingsPanel.add(listenToLocationPanel, rightPanelConstraints);

                JComboBox<String> bucketComboBox2 = new JComboBox<>();
                // bucketComboBox2.addActionListener(e -> updateScopes());
                rightPanelConstraints.gridx = 0;
                rightPanelConstraints.gridy = 4;
                rightPanelConstraints.gridwidth = 1;
                generalSettingsPanel.add(bucketComboBox2, rightPanelConstraints);

                JComboBox<String> scopeComboBox2 = new JComboBox<>();
                scopeComboBox2.setEnabled(false);
                rightPanelConstraints.gridx = 1;
                rightPanelConstraints.gridy = 4;
                generalSettingsPanel.add(scopeComboBox2, rightPanelConstraints);

                JComboBox<String> collectionComboBox1 = new JComboBox<>();
                collectionComboBox1.setEnabled(false);
                rightPanelConstraints.gridx = 2;
                rightPanelConstraints.gridy = 4;
                generalSettingsPanel.add(collectionComboBox1, rightPanelConstraints);

                // Listen to location error label
                JBLabel helpLabel2 = new JBLabel(
                                "Please specify a source location for your function. User should have DCP Data Read permission on this keyspace");
                helpLabel2.setForeground(JBColor.GRAY);
                rightPanelConstraints.gridx = 0;
                rightPanelConstraints.gridy = 5;
                rightPanelConstraints.gridwidth = 3;

                generalSettingsPanel.add(helpLabel2, rightPanelConstraints);

                // Eventing Storage label
                JBLabel eventingStorageLabel = new JBLabel("Eventing Storage");
                eventingStorageLabel.setFont(eventingStorageLabel.getFont().deriveFont(Font.BOLD));
                // rightPanelConstraints.gridx = 0;
                // rightPanelConstraints.gridy = 6;
                // generalSettingsPanel.add(eventingStorageLabel, rightPanelConstraints);

                JBLabel eventingStorageInfoLabel = new JBLabel("bucket.scope.collection");
                eventingStorageInfoLabel.setForeground(JBColor.GRAY);
                // rightPanelConstraints.gridx = 1;
                // rightPanelConstraints.gridy = 6;
                // rightPanelConstraints.gridwidth = 2;
                // generalSettingsPanel.add(eventingStorageInfoLabel, rightPanelConstraints);

                // Add both labels to a flow layout
                JPanel eventingStoragePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                eventingStoragePanel.add(eventingStorageLabel);
                eventingStoragePanel.add(eventingStorageInfoLabel);
                rightPanelConstraints.gridx = 0;
                rightPanelConstraints.gridy = 6;
                generalSettingsPanel.add(eventingStoragePanel, rightPanelConstraints);

                JComboBox<String> bucketComboBox3 = new JComboBox<>();
                // bucketComboBox3.addActionListener(e -> updateScopes());
                rightPanelConstraints.gridx = 0;
                rightPanelConstraints.gridy = 7;
                rightPanelConstraints.gridwidth = 1;
                generalSettingsPanel.add(bucketComboBox3, rightPanelConstraints);

                JComboBox<String> scopeComboBox3 = new JComboBox<>();
                scopeComboBox3.setEnabled(false);
                rightPanelConstraints.gridx = 1;
                rightPanelConstraints.gridy = 7;
                generalSettingsPanel.add(scopeComboBox3, rightPanelConstraints);

                JComboBox<String> collectionComboBox2 = new JComboBox<>();
                collectionComboBox2.setEnabled(false);
                rightPanelConstraints.gridx = 2;
                rightPanelConstraints.gridy = 7;

                generalSettingsPanel.add(collectionComboBox2, rightPanelConstraints);

                // Eventing Storage help label
                JBLabel helpLabel3 = new JBLabel(
                                "Please specify a location to store Eventing data. User should have read/write permission on this keyspace");
                helpLabel3.setForeground(JBColor.GRAY);
                rightPanelConstraints.gridx = 0;
                rightPanelConstraints.gridy = 8;
                rightPanelConstraints.gridwidth = 3;
                generalSettingsPanel.add(helpLabel3, rightPanelConstraints);

                // Function Name label and text field
                JBLabel functionNameLabel = new JBLabel("Function Name");
                functionNameLabel.setFont(functionNameLabel.getFont().deriveFont(Font.BOLD));
                rightPanelConstraints.gridx = 0;
                rightPanelConstraints.gridy = 9;
                generalSettingsPanel.add(functionNameLabel, rightPanelConstraints);

                JBTextField functionNameField = new JBTextField(20);
                functionNameField.setToolTipText("Enter the name of the function.");
                rightPanelConstraints.gridx = 0;
                rightPanelConstraints.gridy = 10;
                generalSettingsPanel.add(functionNameField, rightPanelConstraints);

                // Deployment Feed Boundary label and combo box
                JBLabel deploymentFeedBoundaryLabel = new JBLabel("Deployment Feed Boundary");

                deploymentFeedBoundaryLabel.setFont(deploymentFeedBoundaryLabel.getFont().deriveFont(Font.BOLD));
                deploymentFeedBoundaryLabel.setToolTipText(
                                "The preferred Deployment time Feed Boundary for the function.");
                rightPanelConstraints.gridx = 0;
                rightPanelConstraints.gridy = 11;
                generalSettingsPanel.add(deploymentFeedBoundaryLabel, rightPanelConstraints);

                JComboBox<String> deploymentFeedBoundaryComboBox = new JComboBox<>();
                deploymentFeedBoundaryComboBox.addItem("Everything");
                deploymentFeedBoundaryComboBox.addItem("From now");
                deploymentFeedBoundaryComboBox.setToolTipText(
                                "The preferred Deployment time Feed Boundary for the function.");

                rightPanelConstraints.gridx = 0;
                rightPanelConstraints.gridy = 12;
                rightPanelConstraints.gridwidth = 3;
                generalSettingsPanel.add(deploymentFeedBoundaryComboBox, rightPanelConstraints);

                JBLabel warningLabel = new JBLabel("");
                warningLabel.setForeground(JBColor.ORANGE);
                rightPanelConstraints.gridx = 0;
                rightPanelConstraints.gridy = 13;
                generalSettingsPanel.add(warningLabel, rightPanelConstraints);

                deploymentFeedBoundaryComboBox.addActionListener(e -> {
                        if (deploymentFeedBoundaryComboBox.getSelectedItem().equals("From now")) {
                                warningLabel.setText(
                                                "Warning: Deploying with 'From now' will ignore all past mutations.");
                        } else {
                                warningLabel.setText("");
                        }
                });

                // Description label and text area
                JBLabel descriptionLabel = new JBLabel("Description");

                descriptionLabel.setFont(descriptionLabel.getFont().deriveFont(Font.BOLD));
                descriptionLabel.setToolTipText("Enter a description for the function (optional).");
                descriptionLabel.setDisplayedMnemonic('D');
                descriptionLabel.setDisplayedMnemonicIndex(0);
                descriptionLabel.setHorizontalAlignment(JBTextField.LEFT);
                rightPanelConstraints.gridx = 0;
                rightPanelConstraints.gridy = 14;
                generalSettingsPanel.add(descriptionLabel, rightPanelConstraints);

                JBTextArea descriptionTextArea = new JBTextArea(5, 20);
                descriptionTextArea.setLineWrap(true);
                descriptionTextArea.setWrapStyleWord(true);
                descriptionTextArea.setToolTipText("Enter a description for the function (optional).");
                descriptionLabel.setLabelFor(descriptionTextArea);
                JBScrollPane descriptionScrollPane = new JBScrollPane(descriptionTextArea);
                rightPanelConstraints.gridx = 0;
                rightPanelConstraints.gridy = 15;
                rightPanelConstraints.fill = GridBagConstraints.BOTH;
                generalSettingsPanel.add(descriptionScrollPane, rightPanelConstraints);

                // Advanced Settings panel
                JPanel advancedSettingsPanel = new JPanel();
                advancedSettingsPanel.setLayout(new GridBagLayout());
                GridBagConstraints advancedSettingsConstraints = new GridBagConstraints();
                advancedSettingsConstraints.gridx = 0;
                advancedSettingsConstraints.gridy = 0;

                advancedSettingsConstraints.anchor = GridBagConstraints.NORTHWEST;
                advancedSettingsConstraints.insets = new Insets(5, 5, 5, 5);
                advancedSettingsConstraints.fill = GridBagConstraints.HORIZONTAL;

                // Add components to the Advanced Settings panel
                JBLabel systemLogLevelLabel = new JBLabel("System Log Level");
                systemLogLevelLabel.setFont(systemLogLevelLabel.getFont().deriveFont(Font.BOLD));
                // advancedSettingsConstraints.gridx = 0;
                // advancedSettingsConstraints.gridy = 0;
                // advancedSettingsPanel.add(systemLogLevelLabel, advancedSettingsConstraints);

                JBLabel systemLogLevelInfoLabel = new JBLabel("Granularity of system events being captured in the log");
                systemLogLevelInfoLabel.setForeground(JBColor.GRAY);
                // advancedSettingsConstraints.gridx = 1;
                // advancedSettingsConstraints.gridy = 0;
                // advancedSettingsPanel.add(systemLogLevelInfoLabel,
                // advancedSettingsConstraints);

                // Add both the labels to a panel
                JPanel systemLogLevelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                systemLogLevelPanel.add(systemLogLevelLabel);
                systemLogLevelPanel.add(systemLogLevelInfoLabel);
                advancedSettingsConstraints.gridx = 0;
                advancedSettingsConstraints.gridy = 0;
                advancedSettingsPanel.add(systemLogLevelPanel, advancedSettingsConstraints);

                JComboBox<String> systemLogLevelComboBox = new JComboBox<>();
                systemLogLevelComboBox.addItem("Info");
                systemLogLevelComboBox.addItem("Error");
                systemLogLevelComboBox.addItem("Warning");
                systemLogLevelComboBox.addItem("Debug");
                systemLogLevelComboBox.addItem("Trace");
                advancedSettingsConstraints.gridx = 0;
                advancedSettingsConstraints.gridy = 1;
                advancedSettingsPanel.add(systemLogLevelComboBox, advancedSettingsConstraints);

                JBLabel applicationLogFileLabel = new JBLabel(
                                "Application log file for this Function is at:");

                advancedSettingsConstraints.gridx = 0;
                advancedSettingsConstraints.gridy = 2;
                advancedSettingsPanel.add(applicationLogFileLabel, advancedSettingsConstraints);

                JBLabel n1qlConsistencyLabel = new JBLabel("N1QL Consistency");
                n1qlConsistencyLabel.setFont(n1qlConsistencyLabel.getFont().deriveFont(Font.BOLD));
                // advancedSettingsConstraints.gridx = 0;
                // advancedSettingsConstraints.gridy = 3;
                // advancedSettingsPanel.add(n1qlConsistencyLabel, advancedSettingsConstraints);

                JBLabel n1qlConsistencyInfoLabel = new JBLabel("Consistency level of N1QL statements in the function");
                n1qlConsistencyInfoLabel.setForeground(JBColor.GRAY);
                // advancedSettingsConstraints.gridx = 1;
                // advancedSettingsConstraints.gridy = 3;
                // advancedSettingsPanel.add(n1qlConsistencyInfoLabel,
                // advancedSettingsConstraints);

                // Add both the labels to a panel
                JPanel n1qlConsistencyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                n1qlConsistencyPanel.add(n1qlConsistencyLabel);
                n1qlConsistencyPanel.add(n1qlConsistencyInfoLabel);
                advancedSettingsConstraints.gridx = 0;
                advancedSettingsConstraints.gridy = 3;
                advancedSettingsPanel.add(n1qlConsistencyPanel, advancedSettingsConstraints);

                JComboBox<String> n1qlConsistencyComboBox = new JComboBox<>();
                n1qlConsistencyComboBox.addItem("None");
                n1qlConsistencyComboBox.addItem("Request");
                advancedSettingsConstraints.gridx = 0;
                advancedSettingsConstraints.gridy = 4;

                advancedSettingsPanel.add(n1qlConsistencyComboBox, advancedSettingsConstraints);

                JBLabel workersLabel = new JBLabel("Workers");
                workersLabel.setFont(workersLabel.getFont().deriveFont(Font.BOLD));
                // advancedSettingsConstraints.gridx = 0;
                // advancedSettingsConstraints.gridy = 5;
                // advancedSettingsPanel.add(workersLabel, advancedSettingsConstraints);

                JBLabel workersInfoLabel = new JBLabel(
                                "Number of workers per node to process the events. If no value is specified, a default value of 1 worker is used.");
                workersInfoLabel.setForeground(JBColor.GRAY);
                // advancedSettingsConstraints.gridx = 1;
                // advancedSettingsConstraints.gridy = 5;
                // advancedSettingsPanel.add(workersInfoLabel, advancedSettingsConstraints);

                // Add both the labels to a panel
                JPanel workersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                workersPanel.add(workersLabel);
                workersPanel.add(workersInfoLabel);
                advancedSettingsConstraints.gridx = 0;
                advancedSettingsConstraints.gridy = 5;
                advancedSettingsPanel.add(workersPanel, advancedSettingsConstraints);

                JBTextField workersField = new JBTextField(20);
                workersField.setToolTipText("Enter the number of workers per node to process the events.");
                // advancedSettingsConstraints.gridx = 0;
                // advancedSettingsConstraints.gridy = 6;
                // advancedSettingsPanel.add(workersField, advancedSettingsConstraints);

                JBLabel languageCompatibilityLabel = new JBLabel("Language compatibility");

                languageCompatibilityLabel.setFont(languageCompatibilityLabel.getFont().deriveFont(Font.BOLD));
                // advancedSettingsConstraints.gridx = 0;
                // advancedSettingsConstraints.gridy = 7;
                // advancedSettingsPanel.add(languageCompatibilityLabel,
                // advancedSettingsConstraints);

                JBLabel languageCompatibilityInfoLabel = new JBLabel("Language compatibility of the function");
                languageCompatibilityInfoLabel.setForeground(JBColor.GRAY);
                // advancedSettingsConstraints.gridx = 1;
                // advancedSettingsConstraints.gridy = 7;
                // advancedSettingsPanel.add(languageCompatibilityInfoLabel,
                // advancedSettingsConstraints);

                // Add both the labels to a panel
                JPanel languageCompatibilityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                languageCompatibilityPanel.add(languageCompatibilityLabel);
                languageCompatibilityPanel.add(languageCompatibilityInfoLabel);
                advancedSettingsConstraints.gridx = 0;
                advancedSettingsConstraints.gridy = 7;
                advancedSettingsPanel.add(languageCompatibilityPanel, advancedSettingsConstraints);

                JComboBox<String> languageCompatibilityComboBox = new JComboBox<>();
                languageCompatibilityComboBox.addItem("6.0.0");
                languageCompatibilityComboBox.addItem("6.5.0");
                languageCompatibilityComboBox.addItem("6.6.2");
                advancedSettingsConstraints.gridx = 0;
                advancedSettingsConstraints.gridy = 8;
                advancedSettingsPanel.add(languageCompatibilityComboBox, advancedSettingsConstraints);

                JBLabel scriptTimeoutLabel = new JBLabel("Script Timeout");

                scriptTimeoutLabel.setFont(scriptTimeoutLabel.getFont().deriveFont(Font.BOLD));
                // advancedSettingsConstraints.gridx = 0;
                // advancedSettingsConstraints.gridy = 9;
                // advancedSettingsPanel.add(scriptTimeoutLabel, advancedSettingsConstraints);

                JBLabel scriptTimeoutInfoLabel = new JBLabel(
                                "Time after which the Function's execution will be timed out");
                scriptTimeoutInfoLabel.setForeground(JBColor.GRAY);
                // advancedSettingsConstraints.gridx = 1;
                // advancedSettingsConstraints.gridy = 9;
                // advancedSettingsPanel.add(scriptTimeoutInfoLabel,
                // advancedSettingsConstraints);

                // Add both the labels to a panel
                JPanel scriptTimeoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                scriptTimeoutPanel.add(scriptTimeoutLabel);
                scriptTimeoutPanel.add(scriptTimeoutInfoLabel);
                advancedSettingsConstraints.gridx = 0;
                advancedSettingsConstraints.gridy = 9;
                advancedSettingsPanel.add(scriptTimeoutPanel, advancedSettingsConstraints);

                JBTextField scriptTimeoutField = new JBTextField(20);
                scriptTimeoutField.setText("60");
                scriptTimeoutField.setToolTipText(
                                "Enter the time after which the Function's execution will be timed out.");
                advancedSettingsConstraints.gridx = 0;
                advancedSettingsConstraints.gridy = 10;
                advancedSettingsPanel.add(scriptTimeoutField, advancedSettingsConstraints);

                JBLabel timerContextMaxSizeLabel = new JBLabel("Timer Context Max Size");

                timerContextMaxSizeLabel.setFont(timerContextMaxSizeLabel.getFont().deriveFont(Font.BOLD));
                // advancedSettingsConstraints.gridx = 0;
                // advancedSettingsConstraints.gridy = 11;
                // advancedSettingsPanel.add(timerContextMaxSizeLabel,
                // advancedSettingsConstraints);

                JBLabel timerContextMaxSizeInfoLabel = new JBLabel(
                                "Maximum allowed value of the Timer Context Size in Bytes. Takes effect immediately.");
                timerContextMaxSizeInfoLabel.setForeground(JBColor.GRAY);
                // advancedSettingsConstraints.gridx = 1;
                // advancedSettingsConstraints.gridy = 11;
                // advancedSettingsPanel.add(timerContextMaxSizeInfoLabel,
                // advancedSettingsConstraints);

                // Add both the labels to a panel
                JPanel timerContextMaxSizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                timerContextMaxSizePanel.add(timerContextMaxSizeLabel);
                timerContextMaxSizePanel.add(timerContextMaxSizeInfoLabel);
                advancedSettingsConstraints.gridx = 0;
                advancedSettingsConstraints.gridy = 11;
                advancedSettingsPanel.add(timerContextMaxSizePanel, advancedSettingsConstraints);

                JBTextField timerContextMaxSizeField = new JBTextField(20);
                timerContextMaxSizeField.setText("1024");
                timerContextMaxSizeField.setToolTipText(
                                "Enter the maximum allowed value of the Timer Context Size in Bytes.");
                advancedSettingsConstraints.gridx = 0;
                advancedSettingsConstraints.gridy = 12;
                advancedSettingsPanel.add(timerContextMaxSizeField, advancedSettingsConstraints);

                // Binding Type panel
                bindingsPanel = new JPanel(new GridBagLayout());
                GridBagConstraints bindingsPanelGbc = new GridBagConstraints();
                bindingsPanelGbc.insets = new Insets(5, 5, 5, 5);
                bindingsPanelGbc.anchor = GridBagConstraints.WEST;

                // Binding Type label
                JBLabel bindingTypeLabel = new JBLabel("Binding Type");
                bindingTypeLabel.setFont(bindingTypeLabel.getFont().deriveFont(Font.BOLD));
                bindingsPanelGbc.gridx = 0;
                bindingsPanelGbc.gridy = 0;
                bindingsPanel.add(bindingTypeLabel, bindingsPanelGbc);

                // Plus button
                JButton plusButton = new JButton("+");
                plusButton.addActionListener(e -> addBindingType());
                bindingsPanelGbc.gridx = 1;
                bindingsPanelGbc.gridy = 0;
                bindingsPanel.add(plusButton, bindingsPanelGbc);

                // Minus button
                JButton minusButton = new JButton("-");
                minusButton.addActionListener(e -> removeBindingType());
                bindingsPanelGbc.gridx = 2;
                bindingsPanelGbc.gridy = 0;
                bindingsPanel.add(minusButton, bindingsPanelGbc);

                // Add the panels to the right panel
                rightPanel.add(generalSettingsPanel, "General Settings");
                rightPanel.add(advancedSettingsPanel, "Advanced Settings");
                rightPanel.add(new JBScrollPane(bindingsPanel), "Binding Type");

                // Create and configure the bottom panel
                bottomPanel = new JPanel();
                bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

                // Add components to the bottom panel
                applyButton = new JButton("Apply");
                applyButton.addActionListener(e -> {
                        // Save changes
                        dispose();
                });
                bottomPanel.add(applyButton);

                cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(e -> {
                        // Discard changes
                        dispose();
                });
                bottomPanel.add(cancelButton);

                // Add the panels to the main panel
                JBSplitter splitter = new JBSplitter(false);
                splitter.setFirstComponent(leftPanel);
                splitter.setSecondComponent(rightPanel);
                mainPanel.add(splitter, BorderLayout.CENTER);
                mainPanel.add(bottomPanel, BorderLayout.SOUTH);

                // Add a window listener to handle unsaved changes
                addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                                // Check for unsaved changes
                                if (changedSettings.values().stream().anyMatch(changed -> changed)) {
                                        int result = JOptionPane.showConfirmDialog(FunctionDeploymentSettings.this,
                                                        "You have unsaved changes. Do you want to save them before closing?",
                                                        "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION);
                                        if (result == JOptionPane.YES_OPTION) {
                                                // Save changes
                                                dispose();
                                        } else if (result == JOptionPane.NO_OPTION) {
                                                // Discard changes
                                                dispose();
                                        }
                                } else {
                                        dispose();
                                }
                        }
                });

                // Initialize the changed settings map
                changedSettings = new HashMap<>();
                changedSettings.put("General Settings", false);
                changedSettings.put("Advanced Settings", false);
                changedSettings.put("Binding Type", false);

                // Add document listeners to track changes
                DocumentListener documentListener = new DocumentListener() {
                        @Override
                        public void insertUpdate(DocumentEvent e) {
                                changedSettings.put("General Settings", true);
                        }

                        @Override
                        public void removeUpdate(DocumentEvent e) {
                                changedSettings.put("General Settings", true);
                        }

                        @Override
                        public void changedUpdate(DocumentEvent e) {
                                changedSettings.put("General Settings", true);
                        }
                };
                functionNameField.getDocument().addDocumentListener(documentListener);
                descriptionTextArea.getDocument().addDocumentListener(documentListener);

                ActionListener actionListener = e -> changedSettings.put("General Settings", true);
                deploymentFeedBoundaryComboBox.addActionListener(actionListener);

        }

        private void addBindingType() {
                // Increment the binding type count
                bindingTypeCount++;

                // Create a new aliasPanel to hold the binding type components
                JPanel bindingTypePanel = new JPanel(new GridBagLayout());
                GridBagConstraints bindingsGbc = new GridBagConstraints();
                bindingsGbc.insets = new Insets(5, 5, 5, 5);
                bindingsGbc.anchor = GridBagConstraints.WEST;
                bindingsGbc.fill = GridBagConstraints.HORIZONTAL;

                // Create the binding type dropdown menu
                CustomComboBox bindingTypeComboBox = new CustomComboBox();
                bindingTypeComboBox.setToolTipText("Type of binding for this function.");
                bindingTypeComboBox.addItem("Choose Binding Type");
                bindingTypeComboBox.addItem("Bucket Alias");
                bindingTypeComboBox.addItem("URL Alias");
                bindingTypeComboBox.addItem("Constant Alias");

                bindingsGbc.gridx = 0;
                bindingsGbc.gridy = 0;
                bindingTypePanel.add(bindingTypeComboBox, bindingsGbc);

                // Create a aliasPanel to hold the alias-specific components
                JPanel aliasPanel = new JPanel(new GridBagLayout());
                GridBagConstraints aliasGbc = new GridBagConstraints();
                aliasGbc.insets = new Insets(5, 5, 5, 5);
                aliasGbc.anchor = GridBagConstraints.WEST;
                aliasGbc.fill = GridBagConstraints.HORIZONTAL;
                bindingsGbc.gridx = 1;
                bindingsGbc.gridy = 0;
                bindingsGbc.gridwidth = 2;
                bindingTypePanel.add(aliasPanel, bindingsGbc);

                // Update the alias aliasPanel when the selected binding type changes
                bindingTypeComboBox.addItemListener(e -> {
                        // Remove all components from the alias aliasPanel
                        aliasPanel.removeAll();

                        // Get the selected binding type
                        String selectedBindingType = (String) bindingTypeComboBox.getSelectedItem();

                        if (selectedBindingType.equals("Bucket Alias")) {
                                // Create the alias name text field
                                JBTextField aliasNameField = new JBTextField(20);
                                aliasNameField.setToolTipText("Enter the alias name.");
                                aliasNameField.getEmptyText().setText("Enter the alias name.");
                                aliasGbc.gridx = 0;
                                aliasGbc.gridy = 0;
                                aliasGbc.gridwidth = 4;
                                aliasPanel.add(aliasNameField, aliasGbc);

                                // Create the bucket label
                                JBLabel bucketLabel = new JBLabel("Bucket");
                                bucketLabel.setFont(bucketLabel.getFont().deriveFont(Font.BOLD));
                                // aliasGbc.gridx = 0;
                                // aliasGbc.gridy = 1;
                                // aliasGbc.gridwidth = 1;
                                // aliasPanel.add(bucketLabel, aliasGbc);

                                // Create the bucket info label
                                JBLabel bucketInfoLabel = new JBLabel("bucket.scope.collection");
                                bucketInfoLabel.setForeground(Color.GRAY);
                                // aliasGbc.gridx = 1;
                                // aliasGbc.gridy = 1;
                                // aliasPanel.add(bucketInfoLabel, aliasGbc);

                                // Add both labels to a flow layout panel
                                JPanel bucketPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                                bucketPanel.add(bucketLabel);
                                bucketPanel.add(bucketInfoLabel);
                                aliasGbc.gridx = 0;
                                aliasGbc.gridy = 1;
                                aliasGbc.gridwidth = 3;
                                aliasPanel.add(bucketPanel, aliasGbc);

                                // Create the access label
                                JBLabel accessLabel = new JBLabel("Access");
                                accessLabel.setFont(accessLabel.getFont().deriveFont(Font.BOLD));
                                aliasGbc.gridx = 3;
                                aliasGbc.gridy = 1;
                                aliasGbc.gridwidth = 1;
                                aliasPanel.add(accessLabel, aliasGbc);

                                // Create the bucket dropdown menu
                                JComboBox<String> bucketComboBox = new JComboBox<>();
                                // TODO: Populate the bucketComboBox with available buckets
                                aliasGbc.gridx = 0;
                                aliasGbc.gridy = 2;
                                aliasPanel.add(bucketComboBox, aliasGbc);

                                // Create the scope dropdown menu
                                JComboBox<String> scopeComboBox = new JComboBox<>();
                                scopeComboBox.setEnabled(false);
                                // TODO: Update the scopeComboBox when the selected bucket changes
                                aliasGbc.gridx = 1;
                                aliasGbc.gridy = 2;
                                aliasPanel.add(scopeComboBox, aliasGbc);

                                // Create the collection dropdown menu
                                JComboBox<String> collectionComboBox = new JComboBox<>();
                                collectionComboBox.setEnabled(false);
                                // TODO: Update the collectionComboBox when the selected scope changes
                                aliasGbc.gridx = 2;
                                aliasGbc.gridy = 2;
                                aliasPanel.add(collectionComboBox, aliasGbc);

                                // Create access dropdown menu
                                JComboBox<String> accessComboBox = new JComboBox<>();
                                accessComboBox.addItem("Read Only");
                                accessComboBox.addItem("Read and Write");
                                accessComboBox.setToolTipText("Select access level for this bucket.");
                                accessComboBox.setEnabled(true);
                                accessComboBox.setSelectedIndex(0);
                                aliasGbc.gridx = 3;
                                aliasGbc.gridy = 2;
                                aliasPanel.add(accessComboBox, aliasGbc);

                        } else if (selectedBindingType.equals("URL Alias")) {
                                // Create the alias name text field
                                JBTextField aliasNameField = new JBTextField(20);
                                aliasNameField.setToolTipText("Enter the alias name.");
                                aliasNameField.getEmptyText().setText("Enter the alias name.");
                                aliasGbc.gridx = 0;
                                aliasGbc.gridy = 0;
                                aliasPanel.add(aliasNameField, aliasGbc);

                                // Create the URL text field
                                JBTextField urlField = new JBTextField(20);
                                urlField.setToolTipText("Enter the URL.");
                                urlField.getEmptyText().setText("Enter the URL.");
                                aliasGbc.gridx = 1;
                                aliasGbc.gridy = 0;
                                aliasPanel.add(urlField, aliasGbc);

                                // Create the allow cookies checkbox
                                JBCheckBox allowCookiesCheckBox = new JBCheckBox("Allow cookies");
                                allowCookiesCheckBox.setToolTipText("Allow cookies for this URL.");
                                aliasGbc.gridx = 0;
                                aliasGbc.gridy = 1;
                                aliasPanel.add(allowCookiesCheckBox, aliasGbc);

                                // Create the validate SSL certificate checkbox
                                JBCheckBox validateSslCertificateCheckBox = new JBCheckBox("Validate SSL certificate");
                                validateSslCertificateCheckBox.setToolTipText("Validate SSL certificate for this URL.");
                                aliasGbc.gridx = 1;
                                aliasGbc.gridy = 1;
                                aliasPanel.add(validateSslCertificateCheckBox, aliasGbc);

                                // Create the authentication label
                                JBLabel authenticationLabel = new JBLabel("Authentication");
                                authenticationLabel.setFont(authenticationLabel.getFont().deriveFont(Font.BOLD));
                                authenticationLabel.setToolTipText("Select authentication method for this URL.");
                                authenticationLabel.setDisplayedMnemonic('A');
                                authenticationLabel.setDisplayedMnemonicIndex(0);
                                authenticationLabel.setHorizontalAlignment(JBTextField.LEFT);
                                authenticationLabel.setVerticalAlignment(JBTextField.CENTER);
                                authenticationLabel.setHorizontalTextPosition(JBTextField.RIGHT);
                                authenticationLabel.setVerticalTextPosition(JBTextField.CENTER);
                                aliasGbc.gridx = 0;
                                aliasGbc.gridy = 2;
                                aliasPanel.add(authenticationLabel, aliasGbc);

                                // Create the auth dropdown menu
                                CustomComboBox authComboBox = new CustomComboBox();
                                authComboBox.addItem("No Auth");
                                authComboBox.addItem("Basic");
                                authComboBox.addItem("Bearer");
                                authComboBox.addItem("Digest");
                                authComboBox.setToolTipText("Select authentication method for this URL.");
                                authComboBox.setEnabled(true);

                                authComboBox.setSelectedIndex(0);
                                aliasGbc.gridx = 1;
                                aliasGbc.gridy = 2;
                                aliasPanel.add(authComboBox, aliasGbc);

                                // Create the username label and text field
                                JBLabel usernameLabel = new JBLabel("Username");
                                usernameLabel.setFont(usernameLabel.getFont().deriveFont(Font.BOLD));
                                usernameLabel.setToolTipText("Enter the username for basic or digest authentication.");
                                usernameLabel.setDisplayedMnemonic('U');
                                usernameLabel.setDisplayedMnemonicIndex(0);
                                usernameLabel.setHorizontalAlignment(JBTextField.LEFT);
                                usernameLabel.setVerticalAlignment(JBTextField.CENTER);
                                usernameLabel.setHorizontalTextPosition(JBTextField.RIGHT);
                                usernameLabel.setVerticalTextPosition(JBTextField.CENTER);
                                usernameLabel.setVisible(false);
                                usernameLabel.setEnabled(false);
                                bindingsGbc.gridx = 0;
                                bindingsGbc.gridy = 3;
                                aliasPanel.add(usernameLabel, bindingsGbc);

                                JBTextField usernameField = new JBTextField(20);
                                usernameField.setToolTipText(
                                                "Enter the username for basic or digest authentication.");
                                usernameField.getEmptyText().setText("Enter the username.");
                                usernameField.setVisible(false);
                                usernameField.setEnabled(false);
                                bindingsGbc.gridx = 1;
                                bindingsGbc.gridy = 3;
                                aliasPanel.add(usernameField, bindingsGbc);

                                // Create the password label and text field
                                JBLabel passwordLabel = new JBLabel("Password");
                                passwordLabel.setFont(passwordLabel.getFont().deriveFont(Font.BOLD));
                                passwordLabel.setToolTipText(
                                                "Enter the password for basic or digest authentication.");
                                passwordLabel.setDisplayedMnemonic('P');
                                passwordLabel.setDisplayedMnemonicIndex(0);
                                passwordLabel.setHorizontalAlignment(JBTextField.LEFT);
                                passwordLabel.setVerticalAlignment(JBTextField.CENTER);
                                passwordLabel.setHorizontalTextPosition(JBTextField.RIGHT);
                                passwordLabel.setVerticalTextPosition(JBTextField.CENTER);
                                passwordLabel.setVisible(false);
                                passwordLabel.setEnabled(false);
                                bindingsGbc.gridx = 0;
                                bindingsGbc.gridy = 4;
                                aliasPanel.add(passwordLabel, bindingsGbc);

                                JBPasswordField passwordField = new JBPasswordField();
                                passwordField.setToolTipText(
                                                "Enter the password for basic or digest authentication.");
                                passwordField.getEmptyText().setText("Enter the password.");
                                passwordField.setVisible(false);
                                passwordField.setEnabled(false);
                                bindingsGbc.gridx = 1;
                                bindingsGbc.gridy = 4;
                                aliasPanel.add(passwordField, bindingsGbc);

                                // Create the bearer key label and text field
                                JBLabel bearerKeyLabel = new JBLabel("Bearer Key");
                                bearerKeyLabel.setFont(bearerKeyLabel.getFont().deriveFont(Font.BOLD));
                                bearerKeyLabel.setToolTipText(
                                                "Enter the bearer key for bearer token authentication.");
                                bearerKeyLabel.setDisplayedMnemonic('B');
                                bearerKeyLabel.setDisplayedMnemonicIndex(0);
                                bearerKeyLabel.setHorizontalAlignment(JBTextField.LEFT);
                                bearerKeyLabel.setVerticalAlignment(JBTextField.CENTER);
                                bearerKeyLabel.setHorizontalTextPosition(JBTextField.RIGHT);
                                bearerKeyLabel.setVerticalTextPosition(JBTextField.CENTER);
                                bearerKeyLabel.setVisible(false);
                                bearerKeyLabel.setEnabled(false);
                                bindingsGbc.gridx = 0;
                                bindingsGbc.gridy = 5;
                                aliasPanel.add(bearerKeyLabel, bindingsGbc);

                                JBTextField bearerKeyField = new JBTextField(20);
                                bearerKeyField.setToolTipText(
                                                "Enter the bearer key for bearer token authentication.");
                                bearerKeyField.getEmptyText().setText("Enter the bearer key.");
                                bearerKeyField.setVisible(false);
                                bearerKeyField.setEnabled(false);
                                bindingsGbc.gridx = 1;
                                bindingsGbc.gridy = 5;
                                aliasPanel.add(bearerKeyField, bindingsGbc);

                                // Create the auth dropdown menu listener
                                authComboBox.addActionListener(ee -> {
                                        // Update the visibility of the authentication fields based on the selected
                                        String selectedAuth = (String) authComboBox.getSelectedItem();
                                        if (selectedAuth.equals("Basic") || selectedAuth.equals("Digest")) {
                                                usernameLabel.setVisible(true);
                                                usernameField.setVisible(true);
                                                usernameLabel.setEnabled(true);
                                                usernameField.setEnabled(true);

                                                passwordLabel.setVisible(true);
                                                passwordField.setVisible(true);
                                                passwordLabel.setEnabled(true);
                                                passwordField.setEnabled(true);

                                                bearerKeyLabel.setVisible(false);
                                                bearerKeyField.setVisible(false);
                                                bearerKeyLabel.setEnabled(false);
                                                bearerKeyField.setEnabled(false);
                                        } else if (selectedAuth.equals("Bearer")) {
                                                usernameLabel.setVisible(false);
                                                usernameField.setVisible(false);
                                                usernameLabel.setEnabled(false);
                                                usernameField.setEnabled(false);

                                                passwordLabel.setVisible(false);
                                                passwordField.setVisible(false);
                                                passwordLabel.setEnabled(false);
                                                passwordField.setEnabled(false);

                                                bearerKeyLabel.setVisible(true);
                                                bearerKeyField.setVisible(true);
                                                bearerKeyLabel.setEnabled(true);
                                                bearerKeyField.setEnabled(true);
                                        } else {
                                                usernameLabel.setVisible(false);
                                                usernameField.setVisible(false);
                                                usernameLabel.setEnabled(false);
                                                usernameField.setEnabled(false);

                                                passwordLabel.setVisible(false);
                                                passwordField.setVisible(false);
                                                passwordLabel.setEnabled(false);
                                                passwordField.setEnabled(false);

                                                bearerKeyLabel.setVisible(false);
                                                bearerKeyField.setVisible(false);
                                                bearerKeyLabel.setEnabled(false);
                                                bearerKeyField.setEnabled(false);
                                        }

                                });

                        } else if (selectedBindingType.equals("Constant Alias")) {
                                // Create the alias name label
                                JBLabel aliasNameLabel = new JBLabel("Alias Name:");
                                aliasGbc.gridx = 0;
                                aliasGbc.gridy = 0;
                                aliasPanel.add(aliasNameLabel, aliasGbc);

                                // Create the alias name text field
                                JBTextField aliasNameField = new JBTextField(20);
                                aliasNameField.setToolTipText("Enter the alias name.");
                                aliasNameField.getEmptyText().setText("Enter the alias name.");
                                aliasGbc.gridx = 1;
                                aliasGbc.gridy = 0;
                                aliasPanel.add(aliasNameField, aliasGbc);

                                // Create the value label
                                JBLabel valueLabel = new JBLabel("Value:");
                                aliasGbc.gridx = 0;
                                aliasGbc.gridy = 1;
                                aliasPanel.add(valueLabel, aliasGbc);

                                // Create the value text field
                                JBTextField valueField = new JBTextField(20);
                                valueField.setToolTipText("Enter the constant value.");
                                valueField.getEmptyText().setText("Enter the constant value.");
                                aliasGbc.gridx = 1;
                                aliasGbc.gridy = 1;
                                aliasPanel.add(valueField, aliasGbc);
                        }

                        // Repaint the alias aliasPanel to show the updated components
                        aliasPanel.revalidate();
                        aliasPanel.repaint();
                });
                bindingTypeComboBox.setSelectedIndex(0);

                // Add the binding type aliasPanel to the bindings aliasPanel
                GridBagConstraints bindingsPanelGbc = new GridBagConstraints();
                bindingsPanelGbc.insets = new Insets(5, 5, 5, 5);
                bindingsPanelGbc.anchor = GridBagConstraints.WEST;
                bindingsPanelGbc.gridx = 0;
                bindingsPanelGbc.gridy = bindingTypeCount;
                bindingsPanelGbc.gridwidth = 3;
                bindingsPanel.add(bindingTypePanel, bindingsPanelGbc);

                // Repaint the bindings aliasPanel to show the new binding type
                bindingsPanel.revalidate();
                bindingsPanel.repaint();
        }

        private void removeBindingType() {
                // Get the number of binding types
                int bindingTypeCount = bindingsPanel.getComponentCount();

                // Check if there are any binding types to remove
                if (bindingTypeCount > 0) {
                        // Remove the last binding type aliasPanel
                        bindingsPanel.remove(bindingTypeCount - 1);

                        // Repaint the bindings aliasPanel to show the removed binding type
                        bindingsPanel.revalidate();
                        bindingsPanel.repaint();
                }
        }
}