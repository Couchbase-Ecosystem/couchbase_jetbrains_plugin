package com.couchbase.intellij.tree;

import com.couchbase.intellij.persistence.SavedCluster;
import com.couchbase.intellij.tools.dialog.CollapsiblePanel;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;

public class EditConnectionDialog extends DialogWrapper {

    private final SavedCluster savedCluster; // The cluster to edit
    private JBTextField connectionNameTextField;
    private JBTextField hostTextField;
    private JCheckBox enableSSLCheckBox;
    private JBTextField usernameTextField;
    private JPasswordField passwordField;

    private JPanel wrapperPanel;

    public EditConnectionDialog(SavedCluster savedCluster) {
        super(false);
        this.savedCluster = savedCluster;
        createUIComponents();

        init();
        setTitle("Edit Couchbase Connection");
        setResizable(true);
        getPeer().getWindow().setMinimumSize(new Dimension(800, 600));

        // Load the details of the savedCluster into the form fields
        loadSavedClusterDetails();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return wrapperPanel;
    }

    private void createUIComponents() {
        // Initialize the form fields
        connectionNameTextField = new JBTextField(30);
        hostTextField = new JBTextField(30);
        enableSSLCheckBox = new JCheckBox("Enable SSL");
        usernameTextField = new JBTextField(30);
        passwordField = new JPasswordField(30);

        // Prefill the form fields with the saved values
        connectionNameTextField.setText(savedCluster.getName());
        hostTextField.setText(savedCluster.getUrl());
        enableSSLCheckBox.setSelected(savedCluster.isSslEnable());
        usernameTextField.setText(savedCluster.getUsername());

        // Create panels for each group of related fields
        JPanel databasePanel = createDatabasePanel();
        JPanel credentialsPanel = createCredentialsPanel();
        CollapsiblePanel advancedPanel = createAdvancedPanel();

        // Main Panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(databasePanel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing between panels
        topPanel.add(credentialsPanel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing between panels

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(advancedPanel, BorderLayout.CENTER);
        mainPanel.setBorder(JBUI.Borders.emptyLeft(10));

        wrapperPanel = new JPanel(new BorderLayout());

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(mainPanel, BorderLayout.CENTER);

        wrapperPanel.add(rightPanel, BorderLayout.CENTER);
    }

    private JPanel createDatabasePanel() {
        JPanel databasePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = JBUI.insets(5);

        JLabel connectionNameLabel = new JLabel("Name:");
        JLabel hostLabel = new JLabel("Connection String:");
        enableSSLCheckBox = new JCheckBox("Enable SSL");

        hostTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                handleHostChange();
            }

            public void removeUpdate(DocumentEvent e) {
                handleHostChange();
            }

            public void insertUpdate(DocumentEvent e) {
                handleHostChange();
            }

            public void handleHostChange() {
                if (!hostTextField.getText().equals(savedCluster.getUrl())) {
                    // Enable the credentials panel
                    usernameTextField.setEnabled(true);
                    passwordField.setEnabled(true);
                } else {
                    // Disable the credentials panel
                    usernameTextField.setEnabled(false);
                    passwordField.setEnabled(false);
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        databasePanel.add(connectionNameLabel, gbc);

        gbc.gridx = 1;
        databasePanel.add(connectionNameTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        databasePanel.add(hostLabel, gbc);

        gbc.gridx = 1;
        databasePanel.add(hostTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        databasePanel.add(enableSSLCheckBox, gbc);

        return databasePanel;
    }

    private JPanel createCredentialsPanel() {
        JPanel credentialsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = JBUI.insets(5);

        JLabel usernameLabel = new JLabel("Username:");
        usernameTextField = new JBTextField(30);
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(30);

        // Initially disable the credentials panel
        usernameTextField.setEnabled(false);
        passwordField.setEnabled(false);

        gbc.gridx = 0;
        gbc.gridy = 0;
        credentialsPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        credentialsPanel.add(usernameTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        credentialsPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        credentialsPanel.add(passwordField, gbc);
        return credentialsPanel;
    }

    private CollapsiblePanel createAdvancedPanel() {
        // TODO: Implement this method to create the advanced panel
        JPanel content = new JPanel();
        String title = "Advanced Settings Pannel";
        return new CollapsiblePanel(title, content);
    }

    private void loadSavedClusterDetails() {
        // Load the details of the savedCluster into the form fields
        connectionNameTextField.setText(savedCluster.getName());
        hostTextField.setText(savedCluster.getUrl());
        enableSSLCheckBox.setSelected(savedCluster.isSslEnable());
        usernameTextField.setText(savedCluster.getUsername());
        passwordField.setText(""); // Passwords should not be pre-filled for security reasons
    }
}