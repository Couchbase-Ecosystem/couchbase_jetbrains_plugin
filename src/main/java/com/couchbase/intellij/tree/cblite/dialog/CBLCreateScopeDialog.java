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

import com.couchbase.intellij.tree.cblite.ActiveCBLDatabase;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;

public class CBLCreateScopeDialog extends DialogWrapper {
    private JPanel panel;
    private JBTextField textField;
    private JLabel errorLabel;
    private Tree tree;
    private Project project;
    private String scopeName;

    public CBLCreateScopeDialog(Project project, Tree tree) {
        super(true);
        this.project = project;
        this.tree = tree;
        init();
        setTitle("New Scope");
        setResizable(true);
        getPeer().getWindow().setMinimumSize(new Dimension(400, 100));
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

    @Override
    protected JComponent createCenterPanel() {
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.decode("#FF4444"));
        JLabel label = new JLabel("Scope Name:");
        textField = new JBTextField(30);

        panel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.WEST;
        gc.insets = JBUI.insets(5);

        gc.gridy = 0;
        gc.gridx = 0;
        gc.weightx = 0.4;
        formPanel.add(label, gc);

        gc.gridy = 0;
        gc.gridx = 1;
        gc.weightx = 0.6;
        formPanel.add(textField, gc);

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(errorLabel, BorderLayout.SOUTH);

        return panel;
    }

    @Override
    protected void doOKAction() {
        this.scopeName = textField.getText();
        String validationError = isValidScopeName(scopeName);
        if (validationError != null) {
            errorLabel.setText(validationError);
            textField.setBorder(new LineBorder(Color.decode("#FF4444")));
            return;
        }

        try {
            ActiveCBLDatabase.getInstance().getDatabase().createCollection("default", scopeName);
        } catch (Exception e) {
            errorLabel.setText("Error creating scope: " + e.getMessage());
            textField.setBorder(new LineBorder(Color.decode("#FF4444")));
            return;
        }

        super.doOKAction();
    }
}
