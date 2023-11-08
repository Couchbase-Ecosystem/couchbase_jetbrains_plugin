package com.couchbase.intellij.tree.cblite.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import com.couchbase.intellij.tree.cblite.ActiveCBLDatabase;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;

public class CBLCreateCollectionDialog extends DialogWrapper {
    private JBTextField scopeField;
    private JBTextField collectionField;
    private JLabel errorLabel;
    private static final Color ERROR_COLOR = Color.decode("#FF4444");

    public CBLCreateCollectionDialog() {
        super(true);

        init();
        setTitle("New Collection");
        setResizable(true);
        getPeer().getWindow().setMinimumSize(new Dimension(400, 100));
    }

    @Override
    protected JComponent createCenterPanel() {
        JLabel scopeLabel = new JLabel("Scope Name:");
        scopeField = new JBTextField(30);

        JLabel collectionLabel = new JLabel("Collection Name:");
        collectionField = new JBTextField(30);

        JPanel panel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.WEST;
        gc.insets = JBUI.insets(5);

        gc.gridy = 0;
        gc.gridx = 0;
        gc.weightx = 0.4;
        formPanel.add(scopeLabel, gc);

        gc.gridy = 0;
        gc.gridx = 1;
        gc.weightx = 0.6;
        formPanel.add(scopeField, gc);

        gc.gridy = 1;
        gc.gridx = 0;
        formPanel.add(collectionLabel, gc);

        gc.gridy = 1;
        gc.gridx = 1;
        formPanel.add(collectionField, gc);

        panel.add(formPanel, BorderLayout.NORTH);

        errorLabel = new JLabel("");
        errorLabel.setForeground(ERROR_COLOR);
        panel.add(errorLabel, BorderLayout.SOUTH);

        return panel;
    }

    @Override
    protected void doOKAction() {
        String collectionName = collectionField.getText();
        String scopeName = scopeField.getText();

        String collectionValidationError = isValidName(collectionName, "Collection");
        String scopeValidationError = isValidName(scopeName, "Scope");
        
        if (collectionValidationError != null) {
            errorLabel.setText(collectionValidationError);
            collectionField.setBorder(new LineBorder(ERROR_COLOR));
        } else {
            collectionField.setBorder(BorderFactory.createEmptyBorder());
        }

        if (scopeValidationError != null) {
            errorLabel.setText(errorLabel.getText() + " " + scopeValidationError);
            scopeField.setBorder(new LineBorder(ERROR_COLOR));
        } else {
            scopeField.setBorder(BorderFactory.createEmptyBorder());
        }

        if (collectionValidationError != null || scopeValidationError != null) {
            return;
        }

        try {
            ActiveCBLDatabase.getInstance().getDatabase().createCollection(collectionName, scopeName);
        } catch (Exception e) {
            errorLabel.setText("Error creating collection: " + e.getMessage());
            collectionField.setBorder(new LineBorder(ERROR_COLOR));
            return;
        }

        super.doOKAction();
    }

    public static String isValidName(String name, String type) {
        if (name == null || name.trim().isEmpty()) {
            return type + " name cannot be empty";
        }
        if (name.length() > 251) {
            return type + " name cannot be more than 251 characters";
        }
        String regex = "^[A-Za-z0-9_.-]+$";
        if (!name.matches(regex)) {
            return type + " name contains invalid characters";
        }
        if (name.startsWith("_") || name.startsWith("%")) {
            return type + " name cannot start with _ or %";
        }
        return null;
    }
}
