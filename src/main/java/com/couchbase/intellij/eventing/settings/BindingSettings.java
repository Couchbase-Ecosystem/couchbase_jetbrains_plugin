package com.couchbase.intellij.eventing.settings;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
// import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import com.couchbase.intellij.eventing.components.CustomComboBox;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;

public class BindingSettings {

    private Integer bindingTypeLineIndex = 0;

    private JPanel bindingsPanel;

    private JBLabel bindingTypeLabel;

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

    private Map<JPanel, JSeparator> separatorMap = new HashMap<>();
    private List<JPanel> bindingTypePanels = new ArrayList<>();

    // Keep track of all the aliasPanels
    private List<JPanel> aliasPanels = new ArrayList<>();

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

        // Create a new aliasPanel to hold the binding type components
        final JPanel bindingTypePanel = new JPanel(new GridBagLayout());
        GridBagConstraints bindingsTypeGbc = new GridBagConstraints();
        bindingsTypeGbc.insets = new Insets(5, 5, 5, 5);
        bindingsTypeGbc.anchor = GridBagConstraints.WEST;
        bindingsTypeGbc.fill = GridBagConstraints.HORIZONTAL;

        // Create the binding type dropdown menu
        CustomComboBox bindingTypeComboBox = new CustomComboBox();
        bindingTypeComboBox.setToolTipText("Type of binding for this function.");
        bindingTypeComboBox.addItem("Choose Binding Type");
        bindingTypeComboBox.addItem("Bucket Alias");
        bindingTypeComboBox.addItem("URL Alias");
        bindingTypeComboBox.addItem("Constant Alias");

        bindingsTypeGbc.gridx = 0;
        bindingsTypeGbc.gridy = 0;
        bindingTypePanel.add(bindingTypeComboBox, bindingsTypeGbc);

        // Create a delete button
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            // Remove the binding type panel and its associated separator
            JSeparator separator = separatorMap.get(bindingTypePanel);
            if (separator != null) {
                bindingsPanel.remove(separator);
                separatorMap.remove(bindingTypePanel);
            }
            bindingsPanel.remove(bindingTypePanel);

            // Remove the binding type panel from the bindingTypePanels list
            bindingTypePanels.remove(bindingTypePanel);

            // Rebuild the bindings panel
            rebuildBindingsPanel();

            // Repaint the bindings panel to show the removed binding type
            bindingsPanel.revalidate();
            bindingsPanel.repaint();
        });
        bindingsTypeGbc.gridx = 4; // because of the wide alias panel
        bindingsTypeGbc.gridy = 0;
        bindingsTypeGbc.anchor = GridBagConstraints.NORTHWEST;
        bindingTypePanel.add(deleteButton, bindingsTypeGbc);
        deleteButton.setVisible(false); // initially hide the delete button

        // Create a aliasPanel to hold the alias-specific components
        JPanel aliasPanel = new JPanel(new GridBagLayout());
        GridBagConstraints aliasGbc = new GridBagConstraints();
        aliasGbc.insets = new Insets(5, 5, 5, 5);
        aliasGbc.anchor = GridBagConstraints.WEST;
        aliasGbc.fill = GridBagConstraints.BOTH;

        bindingsTypeGbc.gridx = 1;
        bindingsTypeGbc.gridy = 0;
        bindingsTypeGbc.gridwidth = 1; // initially set to 1, will set to 3 when a binding type is selected
        bindingsTypeGbc.gridheight = 2;
        bindingTypePanel.add(aliasPanel, bindingsTypeGbc);

        // Add the aliasPanel to the list of aliasPanels
        aliasPanels.add(aliasPanel);

        // Set a custom renderer for the bindingTypeComboBox
        bindingTypeComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
                        cellHasFocus);

                if ("Choose Binding Type".equals(value)) {
                    label.setEnabled(false);
                    bindingsTypeGbc.gridwidth = 1; // set a smaller gridwidth value when nothing is selected
                    deleteButton.setVisible(false);
                    deleteButton.setEnabled(false);
                } else {
                    label.setEnabled(true);
                    bindingsTypeGbc.gridwidth = 3;
                    deleteButton.setVisible(true);
                    deleteButton.setEnabled(true);
                }

                return label;
            }
        });

        // Update the alias aliasPanel when the selected binding type changes
        bindingTypeComboBox.addItemListener(e -> {
            // Remove all components from the alias aliasPanel
            aliasPanel.removeAll();

            // Get the selected binding type
            String selectedBindingType = (String) bindingTypeComboBox.getSelectedItem();

            // Show or hide the delete button based on the selected item
            if (!selectedBindingType.equals("Choose Binding Type")) {
                deleteButton.setVisible(true); // show the delete button
                deleteButton.setEnabled(true); // enable the delete button
            } else {
                deleteButton.setVisible(false); // hide the delete button
                deleteButton.setEnabled(false); // disable the delete button
            }

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
                JBLabel bucketLabel = new JBLabel("Bucket");
                bucketLabel.setFont(bucketLabel.getFont().deriveFont(Font.BOLD));

                JBLabel bucketInfoLabel = new JBLabel("bucket.scope.collection");
                bucketInfoLabel.setForeground(Color.GRAY);

                JPanel bucketPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                bucketPanel.add(bucketLabel);
                bucketPanel.add(bucketInfoLabel);
                aliasGbc.gridx = 0;
                aliasGbc.gridy = 1;
                aliasGbc.gridwidth = 3;
                aliasPanel.add(bucketPanel, aliasGbc);

                JBLabel accessLabel = new JBLabel("Access");
                accessLabel.setFont(accessLabel.getFont().deriveFont(Font.BOLD));
                aliasGbc.gridx = 3;
                aliasGbc.gridy = 1;
                aliasGbc.gridwidth = 1;
                aliasPanel.add(accessLabel, aliasGbc);

                bucketComboBox = new JComboBox<>();
                aliasGbc.gridx = 0;
                aliasGbc.gridy = 2;
                aliasPanel.add(bucketComboBox, aliasGbc);

                scopeComboBox = new JComboBox<>();
                scopeComboBox.setEnabled(false);
                aliasGbc.gridx = 1;
                aliasGbc.gridy = 2;
                aliasPanel.add(scopeComboBox, aliasGbc);

                collectionComboBox = new JComboBox<>();
                collectionComboBox.setEnabled(false);
                aliasGbc.gridx = 2;
                aliasGbc.gridy = 2;
                aliasPanel.add(collectionComboBox, aliasGbc);

                accessComboBox = new JComboBox<>();
                accessComboBox.addItem("Read Only");
                accessComboBox.addItem("Read and Write");
                accessComboBox.setToolTipText("Select access level for this bucket.");
                accessComboBox.setEnabled(true);
                accessComboBox.setSelectedIndex(0);
                aliasGbc.gridx = 3;
                aliasGbc.gridy = 2;
                aliasPanel.add(accessComboBox, aliasGbc);

                aliasPanel.setPreferredSize(new Dimension(500, 120));

            } else if (selectedBindingType.equals("URL Alias")) {
                urlAliasNameField = new JBTextField(20);
                urlAliasNameField.setToolTipText("Enter the alias name.");
                urlAliasNameField.getEmptyText().setText("Enter the alias name.");
                aliasGbc.gridx = 0;
                aliasGbc.gridy = 0;
                aliasPanel.add(urlAliasNameField, aliasGbc);

                urlField = new JBTextField(20);
                urlField.setToolTipText("Enter the URL.");
                urlField.getEmptyText().setText("Enter the URL.");
                aliasGbc.gridx = 1;
                aliasGbc.gridy = 0;
                aliasPanel.add(urlField, aliasGbc);

                JBCheckBox allowCookiesCheckBox = new JBCheckBox("Allow cookies");
                allowCookiesCheckBox.setToolTipText("Allow cookies for this URL.");
                aliasGbc.gridx = 0;
                aliasGbc.gridy = 1;
                aliasPanel.add(allowCookiesCheckBox, aliasGbc);

                JBCheckBox validateSslCertificateCheckBox = new JBCheckBox("Validate SSL certificate");
                validateSslCertificateCheckBox.setToolTipText("Validate SSL certificate for this URL.");
                aliasGbc.gridx = 1;
                aliasGbc.gridy = 1;
                aliasPanel.add(validateSslCertificateCheckBox, aliasGbc);

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
                aliasGbc.gridx = 0;
                aliasGbc.gridy = 3;
                aliasPanel.add(usernameLabel, aliasGbc);

                usernameField = new JBTextField(20);
                usernameField.setToolTipText(
                        "Enter the username for basic or digest authentication.");
                usernameField.getEmptyText().setText("Enter the username.");
                usernameField.setVisible(false);
                usernameField.setEnabled(false);
                aliasGbc.gridx = 1;
                aliasGbc.gridy = 3;
                aliasPanel.add(usernameField, aliasGbc);

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
                aliasGbc.gridx = 0;
                aliasGbc.gridy = 4;
                aliasPanel.add(passwordLabel, aliasGbc);

                JBPasswordField passwordField = new JBPasswordField();
                passwordField.setToolTipText(
                        "Enter the password for basic or digest authentication.");
                passwordField.getEmptyText().setText("Enter the password.");
                passwordField.setVisible(false);
                passwordField.setEnabled(false);
                aliasGbc.gridx = 1;
                aliasGbc.gridy = 4;
                aliasPanel.add(passwordField, aliasGbc);

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
                aliasGbc.gridx = 0;
                aliasGbc.gridy = 3; // should be in the same row as usernameLabel
                aliasPanel.add(bearerKeyLabel, aliasGbc);

                bearerKeyField = new JBTextField(20);
                bearerKeyField.setToolTipText(
                        "Enter the bearer key for bearer token authentication.");
                bearerKeyField.getEmptyText().setText("Enter the bearer key.");
                bearerKeyField.setVisible(false);
                bearerKeyField.setEnabled(false);
                aliasGbc.gridx = 1;
                aliasGbc.gridy = 3; // should be in the same row as usernameField
                aliasPanel.add(bearerKeyField, aliasGbc);
                aliasPanel.setPreferredSize(new Dimension(500, 160));

                authComboBox.addActionListener(ee -> {
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
                        aliasPanel.setPreferredSize(new Dimension(500, 200));
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
                        aliasPanel.setPreferredSize(new Dimension(500, 180));
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
                        aliasPanel.setPreferredSize(new Dimension(500, 160));
                    }
                });
            } else if (selectedBindingType.equals("Constant Alias")) {
                JBLabel constantAliasNameLabel = new JBLabel("Alias Name:");
                aliasGbc.gridx = 0;
                aliasGbc.gridy = 0;
                aliasPanel.add(constantAliasNameLabel, aliasGbc);

                constantAliasNameField = new JBTextField(20);
                constantAliasNameField.setToolTipText("Enter the alias name.");
                constantAliasNameField.getEmptyText().setText("Enter the alias name.");
                aliasGbc.gridx = 1;
                aliasGbc.gridy = 0;
                aliasPanel.add(constantAliasNameField, aliasGbc);

                JBLabel constantValueLabel = new JBLabel("Value:");
                aliasGbc.gridx = 0;
                aliasGbc.gridy = 1;
                aliasPanel.add(constantValueLabel, aliasGbc);

                constantValueField = new JBTextField(20);
                constantValueField.setToolTipText("Enter the constant value.");
                constantValueField.getEmptyText().setText("Enter the constant value.");
                aliasGbc.gridx = 1;
                aliasGbc.gridy = 1;
                aliasPanel.add(constantValueField, aliasGbc);

                aliasPanel.setPreferredSize(new Dimension(500, 80));

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
        bindingsPanelGbc.gridy = ++bindingTypeLineIndex;
        bindingsPanelGbc.gridwidth = 3;
        bindingsPanel.add(bindingTypePanel, bindingsPanelGbc);

        // Add a separator to the bindings aliasPanel
        JSeparator separator = new JSeparator();
        separator.setForeground(Color.GRAY);
        bindingsPanelGbc.gridx = 0;
        bindingsPanelGbc.gridy = ++bindingTypeLineIndex;
        bindingsPanelGbc.gridwidth = 3;
        bindingsPanelGbc.fill = GridBagConstraints.HORIZONTAL;
        bindingsPanel.add(separator, bindingsPanelGbc);

        // Add the binding type panel to the bindingTypePanels list
        bindingTypePanels.add(bindingTypePanel);

        // Store a reference to the separator in the separatorMap
        separatorMap.put(bindingTypePanel, separator);

        // Repaint the bindings aliasPanel to show the new binding type
        bindingsPanel.revalidate();
        bindingsPanel.repaint();
    }

    private void removeBindingType() {
        // Get the number of binding types
        int bindingTypeLineIndex = bindingsPanel.getComponentCount();

        // Check if there are any binding types to remove
        if (bindingTypeLineIndex >= 2) {
            // Remove the last horizontal separator
            bindingsPanel.remove(bindingTypeLineIndex - 1);

            // Remove the last binding type aliasPanel
            bindingsPanel.remove(bindingTypeLineIndex - 2);

            // Repaint the bindings aliasPanel to show the removed binding type
            bindingsPanel.revalidate();
            bindingsPanel.repaint();
        }
    }

    private void rebuildBindingsPanel() {
        // Remove all components from the bindings panel except for the first row
        for (int i = bindingsPanel.getComponentCount() - 1; i >= 3; i--) {
            bindingsPanel.remove(i);
        }

        // Reset the binding type line index
        bindingTypeLineIndex = 0;

        // Add all remaining binding type panels to the bindings panel
        for (JPanel bindingTypePanel : bindingTypePanels) {
            GridBagConstraints bindingsPanelGbc = new GridBagConstraints();
            bindingsPanelGbc.insets = new Insets(5, 5, 5, 5);
            bindingsPanelGbc.anchor = GridBagConstraints.WEST;
            bindingsPanelGbc.gridx = 0;
            bindingsPanelGbc.gridy = ++bindingTypeLineIndex;
            bindingsPanelGbc.gridwidth = 3;
            bindingsPanel.add(bindingTypePanel, bindingsPanelGbc);

            JSeparator separator = separatorMap.get(bindingTypePanel);
            if (separator != null) {
                bindingsPanelGbc.gridx = 0;
                bindingsPanelGbc.gridy = ++bindingTypeLineIndex;
                bindingsPanelGbc.gridwidth = 3;
                bindingsPanelGbc.fill = GridBagConstraints.HORIZONTAL;
                bindingsPanel.add(separator, bindingsPanelGbc);
            }
        }
    }

    // Getters and setters
    public JPanel getPanel() {
        return bindingsPanel;
    }

}