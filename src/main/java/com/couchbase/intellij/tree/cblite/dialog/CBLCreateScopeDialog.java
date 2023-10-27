package com.couchbase.intellij.tree.cblite.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;

public class CBLCreateScopeDialog extends DialogWrapper {
    private JPanel panel;
    private JBTextField textField;
    private JLabel errorLabel;

    public CBLCreateScopeDialog(Project project, Tree tree, String scopeName) {
        super(true);
        init();
        setTitle("New Scope");
        setResizable(true);
        getPeer().getWindow().setMinimumSize(new Dimension(400, 100));
    }

    public static boolean isValidScopeName(String scopeName) {
        if (scopeName == null || scopeName.trim().isEmpty()) {
            return false;
        }
        int maxScopeNameLength = 30;
        if (scopeName.length() > maxScopeNameLength) {
            return false;
        }
        String regex = "^[A-Za-z0-9_.-]+$";
        if (!scopeName.matches(regex)) {
            return false;
        }

        return true;
    }

    @Override
    protected JComponent createCenterPanel() {
        errorLabel = new JLabel("");
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
        if (!isValidScopeName(textField.getText())) {
            errorLabel.setText("Invalid scope name");
            return;
        }

        try {
            // ActiveCBLDatabase.getInstance().getDatabase().createScope(textField.getText());
        } catch (Exception e) {
            errorLabel.setText("Invalid scope name");
            return;
        }
        
        
        super.doOKAction();
    }
}
