
// This is the UI

// First line - heading
// Function Scope(Bold) bucket.scope(small greyed out) info icon(on hover displays-A bucket.scope combination used for identifying functions belonging to the same group.) 

// Error label for this(by default when nothing is selected)-> Please specify a scope to which the function belongs. User should have Eventing Manage Scope Functions permission on this scope

// Second line - two horizontal dropdown boxes 
// 1. Select from available buckets (or *) 2. Select from scopes available in that bucket (The first scope gets selected automatically when the bucket is selected)

// Third line - heading
// Listen to location (Bold) bucket.scope.collection (small greyed out) info icon(on hover displays-The function will listen to this resource for changes. Memcached buckets are not allowed.) 

// Error label for this(by default when nothing is selected)->  Please specify a source location for your function. User should have DCP Data Read permission on this keyspace

// Fourth line - three horizontal dropdown boxes 
// 1. Select from available buckets (or *) 2. Select from scopes available in that bucket (The first scope gets selected automatically when the bucket is selected) 3.  Select from collections available in that scope (The first collection gets selected automatically when the scope is selected)

// Fifth line - heading
// Eventing Storage (Bold) bucket.scope.collection (small greyed out) info icon(on hover displays-This resource is used to store system data and should not be used by other applications.) 

// Error label for this(by default when nothing is selected)->  Please specify a location to store Eventing data. User should have read/write permission on this keyspace

// Sixth line - three horizontal dropdown boxes 
// 1. Select from available buckets (or *) 2. Select from scopes available in that bucket (The first scope gets selected automatically when the bucket is selected) 3.  Select from collections available in that scope (The first collection gets selected automatically when the scope is selected)

// Seventh line - heading
// Function Name (Bold)

// Eighth line - full line text box
// Text box for Function Name 

// Error label depends on text box validation

// Ninth line - heading
// Deployment Feed Boundary(Bold) info icon(on hover The preferred Deployment time Feed Boundary for the function.)

// Tenth line - 1 drop down box with 2 options
// The dropdown box should contain “Everything” and “From now”

// Eleventh line - heading 
// Description(bold) optional(text in small, grayed out and round braces)

// Twelfth line - Text Area
// Text Area for big paragraph text writing (This is a text area not a text field)

// Thirteenth line - Openable Settings. 
// Settings(bold)
// There is a small right arrow before the word settings. On clicking the right menu. The form fill-up menu opens up
// {
//  First line - heading 
//  System Log Level(Bold) , small info icon(on hover shows: Granularity of system events being captured in the log)

//  Second line - dropdown menu
//  Dropdown menu contains: Info, Error, Warning, Debug and Trace

//  Info label - below dropdown menu
//  “Application log file for this Function is at:”

//  Third Line - heading
//  N1QL Consistency(bold) small info icon(on hover shows: Consistency level of N1QL statements in the function)

//  Fourth line - dropdown menu
//  Dropdown menu contains: None and Request

//  Fifth Line - heading
//  Workers(bold) small info icon(on hover shows: Number of workers per node to process the events. If no value is specified, a default value of 1 worker is used.)

//  Sixth Line - text field 
//  Text field can take only a natural number

//  Seventh line - heading 
//  Language compatibility (Bold) , small info icon(on hover shows: Language compatibility of the function)

//  Eighth line - dropdown menu
//  Dropdown menu contains: 6.0.0, 6.5.0, 6.6.2

//   Ninth Line - heading
//  Script Timeout(bold) in seconds (small greyed out text) small info icon(on hover shows: Time after which the Function's execution will be timed out)

//  Tenth Line - text field 
//  Text field can take only a natural number (default initial value: 60)

// Eleventh Line - heading
//  Timer Context Max Size (bold) small info icon(on hover shows: Maximum allowed value of the Timer Context Size in Bytes. Takes effect immediately.)

//  Twelfth Line - text field 
//  Text field can take only a natural number (default initial value: 1024) 
// }

// Fourteen Line - Openable Settings. 
// Binding Type (Bold left end of line) (Plus minus square buttons on the right end[plus adds a new binding type based on the dropdown menu,  minus removes the last binding type])
// Horizontal line separator.

// Here we can add any number of 3 types of binding types[from the dropdown menu(s)]. The dropdown menu contains:
// Binding type(default unelectable greyed out),  Bucket Alias, URL alias, constant alias.

// {
//  1. if it is bucket alias, then first line is bucket alias(the dropdown menu itself), and textfield with placeholder text of “alias name”
//  Second line contains heading Bucket(bold) bucket.scope.collection (small greyed out) Access(bold)
//  Third line contains 3 dropdown boxes for bucket scope collection respectively. And 4th dropdown for Access(2 options of “read only” and “read and write” )

//  2. If its URL alias,  then first line is URL alias(the dropdown menu itself), and two textfields containing placeholder texts of “Alias name..” and “URL...” Respectively.
//  The next line contains two checkboxes: 1. allow cookies 2. validate SSL certificate
//  The next line contains dropdown menu of: 1. no auth 2. basic 3. bearer4. digest
//       {
//      2a. if the user chooses, basic, the same line as dropdown menu should have  two textfields containing placeholder texts of “Username..” and “password…” Respectively.
//  2b. if the user chooses, bearer, the same line as dropdown menu should have 1 textfield containing placeholder texts of “bearer key..” 
//  2c. if the user chooses, digest, the same line as dropdown menu should have  two textfields containing placeholder texts of “Username..” and “password…” Respectively.
//  }

//       if it is constant alias, the first line is constant alias(the dropdown menu itself) and  two textfields containing placeholder texts of “Username..” and “password…” Respectively.
// }
package com.couchbase.intellij.tree;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;

public class FunctionDeploymentDialog extends DialogWrapper {
    private Project project;
    public Set<String> buckets;

    private JLabel errorLabel;

    private JComboBox<String> bucketComboBox;
    private JComboBox<String> scopeComboBox;
    private JComboBox<String> collectionComboBox;
    private JComboBox<String> deploymentFeedBoundaryComboBox;
    private JComboBox<String> systemLogsLevelComboBox;
    private JComboBox<String> n1qlConsistencyComboBox;
    private JComboBox<String> languageCompatibilityComboBox;

    private JTextField functionNameField;
    private JTextArea functionDescriptionArea;
    private JTextField workersField;
    private JTextField scriptTimeoutField;
    private JTextField timerContextMaxSizeField;

    private JPanel settingsPanel;
    private JPanel bindingsPanel;

    private boolean settingsPanelExpanded = false;

    public FunctionDeploymentDialog(@Nullable Project project) {
        super(project);
        this.project = project;
        this.buckets = null;
        setTitle("Deploy as a Couchbase Function");
        setSize(1000, 2000);
        setResizable(true);
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Function Scope label
        JLabel functionScopeLabel = new JLabel("Function Scope");
        functionScopeLabel.setFont(functionScopeLabel.getFont().deriveFont(Font.BOLD));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(functionScopeLabel, gbc);

        JLabel functionScopeInfoLabel = new JLabel("bucket.scope");
        functionScopeInfoLabel.setForeground(Color.GRAY);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(functionScopeInfoLabel, gbc);

        bucketComboBox = new JComboBox<>();
        bucketComboBox.addActionListener(e -> updateScopes());
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(bucketComboBox, gbc);

        scopeComboBox = new JComboBox<>();
        scopeComboBox.setEnabled(false);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(scopeComboBox, gbc);

        // Function Scope error label

        errorLabel = new JLabel(
                "Please specify a scope to which the function belongs. User should have Eventing Manage Scope Functions permission on this scope");
        errorLabel.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        panel.add(errorLabel, gbc);

        // Listen to location label
        JLabel listenToLocationLabel = new JLabel("Listen to location");
        listenToLocationLabel.setFont(listenToLocationLabel.getFont().deriveFont(Font.BOLD));
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(listenToLocationLabel, gbc);

        JLabel listenToLocationInfoLabel = new JLabel("bucket.scope.collection");
        listenToLocationInfoLabel.setForeground(Color.GRAY);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(listenToLocationInfoLabel, gbc);

        bucketComboBox = new JComboBox<>();
        bucketComboBox.addActionListener(e -> updateScopes());
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(bucketComboBox, gbc);

        scopeComboBox = new JComboBox<>();
        scopeComboBox.setEnabled(false);
        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(scopeComboBox, gbc);

        collectionComboBox = new JComboBox<>();
        collectionComboBox.setEnabled(false);
        gbc.gridx = 2;
        gbc.gridy = 4;
        panel.add(collectionComboBox, gbc);

        // Listen to location error label
        errorLabel = new JLabel(
                "Please specify a source location for your function. User should have DCP Data Read permission on this keyspace");
        errorLabel.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        panel.add(errorLabel, gbc);

        // Eventing Storage label
        JLabel eventingStorageLabel = new JLabel("Eventing Storage");
        eventingStorageLabel.setFont(eventingStorageLabel.getFont().deriveFont(Font.BOLD));
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(eventingStorageLabel, gbc);

        JLabel eventingStorageInfoLabel = new JLabel("bucket.scope.collection");
        eventingStorageInfoLabel.setForeground(Color.GRAY);
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.ipadx = 1;
        panel.add(eventingStorageInfoLabel, gbc);

        bucketComboBox = new JComboBox<>();
        bucketComboBox.addActionListener(e -> updateScopes());
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(bucketComboBox, gbc);

        scopeComboBox = new JComboBox<>();
        scopeComboBox.setEnabled(false);
        gbc.gridx = 1;
        gbc.gridy = 7;
        panel.add(scopeComboBox, gbc);

        collectionComboBox = new JComboBox<>();
        collectionComboBox.setEnabled(false);
        gbc.gridx = 2;
        gbc.gridy = 7;
        panel.add(collectionComboBox, gbc);

        // Eventing Storage error label
        errorLabel = new JLabel(
                "Please specify a location to store Eventing data. User should have read/write permission on this keyspace");
        errorLabel.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 3;
        panel.add(errorLabel, gbc);

        // Eventing Storage info label
        JLabel eventingStorageInfoLabel2 = new JLabel(
                "System data stored in this location will have the document ID prefixed with eventing. Using a persistent bucket type is recommended.");
        eventingStorageInfoLabel2.setForeground(Color.GRAY);
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 3;
        panel.add(eventingStorageInfoLabel2, gbc);

        // Function Name label and text field
        JLabel functionNameLabel = new JLabel("Function Name");
        functionNameLabel.setFont(functionNameLabel.getFont().deriveFont(Font.BOLD));
        gbc.gridx = 0;
        gbc.gridy = 10;
        panel.add(functionNameLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 11;
        functionNameField = new JTextField(20);
        panel.add(functionNameField, gbc);

        // Deployment Feed Boundary label and combo box
        // TODO: Add label and combo box for selecting deployment feed boundary
        JLabel deploymentFeedBoundaryLabel = new JLabel("Deployment Feed Boundary");
        deploymentFeedBoundaryLabel.setFont(deploymentFeedBoundaryLabel.getFont().deriveFont(Font.BOLD));
        gbc.gridx = 0;
        gbc.gridy = 12;
        panel.add(deploymentFeedBoundaryLabel, gbc);

        deploymentFeedBoundaryComboBox = new JComboBox<>();
        deploymentFeedBoundaryComboBox.addItem("Everything");
        deploymentFeedBoundaryComboBox.addItem("From Now");
        gbc.gridx = 0;
        gbc.gridy = 13;
        panel.add(deploymentFeedBoundaryComboBox, gbc);

        // Description label and text area
        // TODO: Add label and text area for entering description
        JLabel descriptionLabel = new JLabel("Description");
        descriptionLabel.setFont(descriptionLabel.getFont().deriveFont(Font.BOLD));
        gbc.gridx = 0;
        gbc.gridy = 14;
        panel.add(descriptionLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 15;
        functionDescriptionArea = new JTextArea(5, 100);
        panel.add(functionDescriptionArea, gbc);

        // Settings label and expandable panel
        // Add a toggle button for the settings panel
        JLabel settingsToggleButton = new JLabel("▶");
        settingsToggleButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Toggle the expanded state of the settings panel
                settingsPanelExpanded = !settingsPanelExpanded;

                // Show or hide the settings panel based on its expanded state
                if (settingsPanelExpanded) {
                    settingsToggleButton.setText("▼");
                    settingsPanel.setVisible(true);
                } else {
                    settingsToggleButton.setText("▶");
                    settingsPanel.setVisible(false);
                }
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 16;
        panel.add(settingsToggleButton, gbc);

        // TODO: Add label and expandable panel for settings
        JLabel settingsLabel = new JLabel("Settings");
        settingsLabel.setFont(settingsLabel.getFont().deriveFont(Font.BOLD));
        gbc.gridx = 1;
        gbc.gridy = 16;
        panel.add(settingsLabel, gbc);

        settingsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints settingsGbc = new GridBagConstraints();
        settingsGbc.insets = new Insets(5, 5, 5, 5);
        settingsGbc.anchor = GridBagConstraints.WEST;

        // System logs level label and combo box
        JLabel systemLogsLevelLabel = new JLabel("System Logs Level");
        systemLogsLevelLabel.setFont(systemLogsLevelLabel.getFont().deriveFont(Font.BOLD));
        settingsGbc.gridx = 0;
        settingsGbc.gridy = 0;
        settingsPanel.add(systemLogsLevelLabel, settingsGbc);

        systemLogsLevelComboBox = new JComboBox<>();
        systemLogsLevelComboBox.addItem("Error");
        systemLogsLevelComboBox.addItem("Warning");
        systemLogsLevelComboBox.addItem("Info");
        systemLogsLevelComboBox.addItem("Debug");

        settingsGbc.gridx = 0;
        settingsGbc.gridy = 1;
        settingsGbc.gridwidth = 2;
        settingsPanel.add(systemLogsLevelComboBox, settingsGbc);

        JLabel systemLogsLevelInfoLabel = new JLabel(
                "Application logs file for this function is at:");
        systemLogsLevelInfoLabel.setForeground(Color.GRAY);
        settingsGbc.gridx = 0;
        settingsGbc.gridy = 2;
        settingsGbc.gridwidth = 2;
        settingsPanel.add(systemLogsLevelInfoLabel, settingsGbc);

        // N1QL Consistency label and combo box
        JLabel n1QLConsistencyLabel = new JLabel("N1QL Consistency");
        n1QLConsistencyLabel.setFont(n1QLConsistencyLabel.getFont().deriveFont(Font.BOLD));
        settingsGbc.gridx = 0;
        settingsGbc.gridy = 3;
        settingsPanel.add(n1QLConsistencyLabel, settingsGbc);

        n1qlConsistencyComboBox = new JComboBox<>();
        n1qlConsistencyComboBox.addItem("Error");
        n1qlConsistencyComboBox.addItem("Warning");

        settingsGbc.gridx = 0;
        settingsGbc.gridy = 4;
        settingsGbc.gridwidth = 2;
        settingsPanel.add(n1qlConsistencyComboBox, settingsGbc);

        // Workers label and text field
        JLabel workersLabel = new JLabel("Workers");
        workersLabel.setFont(workersLabel.getFont().deriveFont(Font.BOLD));
        settingsGbc.gridx = 0;
        settingsGbc.gridy = 5;
        settingsPanel.add(workersLabel, settingsGbc);

        workersField = new JTextField(20);
        settingsGbc.gridx = 0;
        settingsGbc.gridy = 6;
        settingsGbc.gridwidth = 2;
        settingsPanel.add(workersField, settingsGbc);

        // Language Compatibility label and combo box
        JLabel languageCompatibilityLabel = new JLabel("Language Compatibility");
        languageCompatibilityLabel.setFont(languageCompatibilityLabel.getFont().deriveFont(Font.BOLD));
        settingsGbc.gridx = 0;
        settingsGbc.gridy = 7;
        settingsPanel.add(languageCompatibilityLabel, settingsGbc);

        languageCompatibilityComboBox = new JComboBox<>();
        languageCompatibilityComboBox.addItem("6.0.0");
        languageCompatibilityComboBox.addItem("6.5.0");
        languageCompatibilityComboBox.addItem("6.6.2");

        settingsGbc.gridx = 0;
        settingsGbc.gridy = 8;
        settingsGbc.gridwidth = 2;
        settingsPanel.add(languageCompatibilityComboBox, settingsGbc);

        // Script Timeout label and text field
        JLabel scriptTimeoutLabel = new JLabel("Script Timeout");
        scriptTimeoutLabel.setFont(scriptTimeoutLabel.getFont().deriveFont(Font.BOLD));
        settingsGbc.gridx = 0;
        settingsGbc.gridy = 9;
        settingsPanel.add(scriptTimeoutLabel, settingsGbc);

        JLabel scriptTimeoutInfoLabel = new JLabel("in seconds.");
        scriptTimeoutInfoLabel.setForeground(Color.GRAY);
        settingsGbc.gridx = 1;
        settingsGbc.gridy = 9;
        settingsPanel.add(scriptTimeoutInfoLabel, settingsGbc);

        scriptTimeoutField = new JTextField(20);
        settingsGbc.gridx = 0;
        settingsGbc.gridy = 10;
        settingsGbc.gridwidth = 2;
        settingsPanel.add(scriptTimeoutField, settingsGbc);

        // Timer Context Timeout label and text field
        JLabel timerContextMaxSizeLabel = new JLabel("Timer Context Max Size");
        timerContextMaxSizeLabel.setFont(timerContextMaxSizeLabel.getFont().deriveFont(Font.BOLD));
        settingsGbc.gridx = 0;
        settingsGbc.gridy = 11;
        settingsPanel.add(timerContextMaxSizeLabel, settingsGbc);

        timerContextMaxSizeField = new JTextField(20);
        settingsGbc.gridx = 0;
        settingsGbc.gridy = 12;
        settingsGbc.gridwidth = 2;
        settingsPanel.add(timerContextMaxSizeField, settingsGbc);

        gbc.gridx = 0;
        gbc.gridy = 17;
        panel.add(settingsPanel, gbc);

        // Binding Type label and expandable panel
        // TODO: Add label and expandable panel for binding type

        return new JScrollPane(panel);

    }

    private void updateScopes() {
        // Get selected bucket
        String selectedBucket = (String) bucketComboBox.getSelectedItem();

        // TODO: Get scopes for selected bucket and update scopeComboBox
        // You can use the Couchbase Java SDK to get the list of scopes for the selected
        // bucket
        // For example:
        // List<ScopeSpec> scopes =
        // cluster.bucket(selectedBucket).collections().getAllScopes();
        // Then you can update the scopeComboBox with the list of scopes

        // Enable scope combo box
        scopeComboBox.setEnabled(true);
    }

    public String getSelectedBucket() {
        return (String) bucketComboBox.getSelectedItem();
    }

    public String getSelectedScope() {
        return (String) scopeComboBox.getSelectedItem();
    }

    public String getFunctionName() {
        return functionNameField.getText();
    }
}
