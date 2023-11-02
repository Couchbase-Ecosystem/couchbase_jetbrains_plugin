package com.couchbase.intellij.tree.cblite.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.BorderFactory;

import com.couchbase.intellij.tree.cblite.ActiveCBLDatabase;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;

public class CBLCreateScopedCollectionDialog extends DialogWrapper {
    private JPanel panel;
    private JBTextField scopeField;
    private JBTextField collectionField;
    private JLabel errorLabel;
    private Project project;
    private Tree tree;

    public CBLCreateScopedCollectionDialog(Project project, Tree tree) {
        super(true);
        this.project = project;
        this.tree = tree;

        init();
        setTitle("New Scoped Collection");
        setResizable(true);
        getPeer().getWindow().setMinimumSize(new Dimension(400, 100));
    }

    // ...

    @Override
    protected JComponent createCenterPanel() {
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.decode("#FF4444"));
        JLabel scopeLabel = new JLabel("Scope Name:");
        scopeField = new JBTextField(30);

        JLabel collectionLabel = new JLabel("Collection Name:");
        collectionField = new JBTextField(30);

        panel = new JPanel(new BorderLayout());
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
        panel.add(errorLabel, BorderLayout.SOUTH);

        return panel;
    }

    @Override
    protected void doOKAction() {
        String collectionName = collectionField.getText();
        String scopeName = scopeField.getText();

        String collectionValidationError = isValidCollectionName(collectionName);
        String scopeValidationError = isValidScopeName(scopeName);

        if (collectionValidationError != null) {
            errorLabel.setText(collectionValidationError);
            collectionField.setBorder(new LineBorder(Color.decode("#FF4444")));
        } else {
            collectionField.setBorder(BorderFactory.createEmptyBorder());
        }

        if (scopeValidationError != null) {
            errorLabel.setText(errorLabel.getText() + " " + scopeValidationError);
            scopeField.setBorder(new LineBorder(Color.decode("#FF4444")));
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
            collectionField.setBorder(new LineBorder(Color.decode("#FF4444")));
            return;
        }

        super.doOKAction();
    }

    public static String isValidCollectionName(String collectionName) {
        if (collectionName == null || collectionName.trim().isEmpty()) {
            return "Collection name cannot be empty";
        }
        int maxCollectionNameLength = 30;
        if (collectionName.length() > maxCollectionNameLength) {
            return "Collection name cannot be more than 30 characters";
        }
        String regex = "^[A-Za-z0-9_.-]+$";
        if (!collectionName.matches(regex)) {
            return "Collection name contains invalid characters";
        }
        return null;
    }

    public static String isValidScopeName(String scopeName) {
        if (scopeName == null || scopeName.trim().isEmpty()) {
            return "Scope name cannot be empty";
        }
        if (scopeName.length() > 30) {
            return "Scope name cannot be more than 30 characters";
        }
        String regex = "^[A-Za-z0-9_.-]+$";
        if (!scopeName.matches(regex)) {
            return "Scope name contains invalid characters";
        }
        return null;
    }
}
