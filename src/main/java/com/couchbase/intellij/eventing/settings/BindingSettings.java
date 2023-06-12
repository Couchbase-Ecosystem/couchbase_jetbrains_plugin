package com.couchbase.intellij.eventing.settings;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
// import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.couchbase.intellij.eventing.components.CustomComboBox;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;

public class BindingSettings {

    private Integer bindingTypeCount = 0;

    private JPanel bindingsPanel;
    private JPanel bindingTypePanel;
    private JPanel aliasPanel;
    private JPanel bucketPanel;

    private JBLabel bindingTypeLabel;
    private JBLabel bucketLabel;
    private JBLabel bucketInfoLabel;
    private JBLabel accessLabel;
    private JBLabel authenticationLabel;
    private JBLabel usernameLabel;
    private JBLabel passwordLabel;
    private JBLabel bearerKeyLabel;
    private JBLabel constantAliasNameLabel;
    private JBLabel constantValueLabel;

    private JComboBox<String> bucketComboBox;
    private JComboBox<String> scopeComboBox;
    private JComboBox<String> collectionComboBox;
    private JComboBox<String> accessComboBox;

    private JBTextField bucketAliasNameField;
    private JBTextField urlAliasNameField;
    private JBTextField urlField;
    private JBTextField usernameField;
    private JBTextField bearerKeyField;
    private JBTextField constantAliasNameField;
    private JBTextField constantValueField;

    // Constructor
    public BindingSettings() {
        // Binding Type panel
        bindingsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints bindingsPanelGbc = new GridBagConstraints();
        bindingsPanelGbc.insets = new Insets(5, 5, 5, 5);
        bindingsPanelGbc.anchor = GridBagConstraints.WEST;

        // Binding Type label
        bindingTypeLabel = new JBLabel("Binding Type");
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
    }

    private void addBindingType() {
        // Increment the binding type count
        bindingTypeCount++;

        // Create a new aliasPanel to hold the binding type components
        bindingTypePanel = new JPanel(new GridBagLayout());
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
        aliasPanel = new JPanel(new GridBagLayout());
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
                bucketAliasNameField = new JBTextField(20);
                bucketAliasNameField.setToolTipText("Enter the alias name.");
                bucketAliasNameField.getEmptyText().setText("Enter the alias name.");
                aliasGbc.gridx = 0;
                aliasGbc.gridy = 0;
                aliasGbc.gridwidth = 4;
                aliasPanel.add(bucketAliasNameField, aliasGbc);

                // Create the bucket label
                bucketLabel = new JBLabel("Bucket");
                bucketLabel.setFont(bucketLabel.getFont().deriveFont(Font.BOLD));
                // aliasGbc.gridx = 0;
                // aliasGbc.gridy = 1;
                // aliasGbc.gridwidth = 1;
                // aliasPanel.add(bucketLabel, aliasGbc);

                // Create the bucket info label
                bucketInfoLabel = new JBLabel("bucket.scope.collection");
                bucketInfoLabel.setForeground(Color.GRAY);
                // aliasGbc.gridx = 1;
                // aliasGbc.gridy = 1;
                // aliasPanel.add(bucketInfoLabel, aliasGbc);

                // Add both labels to a flow layout panel
                bucketPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                bucketPanel.add(bucketLabel);
                bucketPanel.add(bucketInfoLabel);
                aliasGbc.gridx = 0;
                aliasGbc.gridy = 1;
                aliasGbc.gridwidth = 3;
                aliasPanel.add(bucketPanel, aliasGbc);

                // Create the access label
                accessLabel = new JBLabel("Access");
                accessLabel.setFont(accessLabel.getFont().deriveFont(Font.BOLD));
                aliasGbc.gridx = 3;
                aliasGbc.gridy = 1;
                aliasGbc.gridwidth = 1;
                aliasPanel.add(accessLabel, aliasGbc);

                // Create the bucket dropdown menu
                bucketComboBox = new JComboBox<>();
                // TODO: Populate the bucketComboBox with available buckets
                aliasGbc.gridx = 0;
                aliasGbc.gridy = 2;
                aliasPanel.add(bucketComboBox, aliasGbc);

                // Create the scope dropdown menu
                scopeComboBox = new JComboBox<>();
                scopeComboBox.setEnabled(false);
                // TODO: Update the scopeComboBox when the selected bucket changes
                aliasGbc.gridx = 1;
                aliasGbc.gridy = 2;
                aliasPanel.add(scopeComboBox, aliasGbc);

                // Create the collection dropdown menu
                collectionComboBox = new JComboBox<>();
                collectionComboBox.setEnabled(false);
                // TODO: Update the collectionComboBox when the selected scope changes
                aliasGbc.gridx = 2;
                aliasGbc.gridy = 2;
                aliasPanel.add(collectionComboBox, aliasGbc);

                // Create access dropdown menu
                accessComboBox = new JComboBox<>();
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
                urlAliasNameField = new JBTextField(20);
                urlAliasNameField.setToolTipText("Enter the alias name.");
                urlAliasNameField.getEmptyText().setText("Enter the alias name.");
                aliasGbc.gridx = 0;
                aliasGbc.gridy = 0;
                aliasPanel.add(urlAliasNameField, aliasGbc);

                // Create the URL text field
                urlField = new JBTextField(20);
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
                authenticationLabel = new JBLabel("Authentication");
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
                usernameLabel = new JBLabel("Username");
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

                usernameField = new JBTextField(20);
                usernameField.setToolTipText(
                        "Enter the username for basic or digest authentication.");
                usernameField.getEmptyText().setText("Enter the username.");
                usernameField.setVisible(false);
                usernameField.setEnabled(false);
                bindingsGbc.gridx = 1;
                bindingsGbc.gridy = 3;
                aliasPanel.add(usernameField, bindingsGbc);

                // Create the password label and text field
                passwordLabel = new JBLabel("Password");
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
                bearerKeyLabel = new JBLabel("Bearer Key");
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

                bearerKeyField = new JBTextField(20);
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
                constantAliasNameLabel = new JBLabel("Alias Name:");
                aliasGbc.gridx = 0;
                aliasGbc.gridy = 0;
                aliasPanel.add(constantAliasNameLabel, aliasGbc);

                // Create the alias name text field
                constantAliasNameField = new JBTextField(20);
                constantAliasNameField.setToolTipText("Enter the alias name.");
                constantAliasNameField.getEmptyText().setText("Enter the alias name.");
                aliasGbc.gridx = 1;
                aliasGbc.gridy = 0;
                aliasPanel.add(constantAliasNameField, aliasGbc);

                // Create the value label
                constantValueLabel = new JBLabel("Value:");
                aliasGbc.gridx = 0;
                aliasGbc.gridy = 1;
                aliasPanel.add(constantValueLabel, aliasGbc);

                // Create the value text field
                constantValueField = new JBTextField(20);
                constantValueField.setToolTipText("Enter the constant value.");
                constantValueField.getEmptyText().setText("Enter the constant value.");
                aliasGbc.gridx = 1;
                aliasGbc.gridy = 1;
                aliasPanel.add(constantValueField, aliasGbc);
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

    // Getters and setters
    public JPanel getPanel() {
        return bindingsPanel;
    }

}