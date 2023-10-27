package com.couchbase.intellij.tree.cblite.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.couchbase.intellij.tree.cblite.ActiveCBLDatabase;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;

public class CBLCreateCollectionDialog extends DialogWrapper {
    private JPanel panel;
    private JBTextField textField;
    private JLabel errorLabel;
    private String scopeName;
    private Project project;
    private Tree tree;

    public CBLCreateCollectionDialog(Project project, Tree tree, String scopeName) {
        super(true);
        this.project = project;
        this.tree = tree;
        this.scopeName = scopeName;

        init();
        setTitle("New Collection");
        setResizable(true);
        getPeer().getWindow().setMinimumSize(new Dimension(400, 100));
    }

    public static boolean isValidCollectionName(String collectionName) {
        if (collectionName == null || collectionName.trim().isEmpty()) {
            return false;
        }
        int maxCollectionNameLength = 30;
        if (collectionName.length() > maxCollectionNameLength) {
            return false;
        }
        String regex = "^[A-Za-z0-9_.-]+$";
        if (!collectionName.matches(regex)) {
            return false;
        }

        return true;
    }

    @Override
    protected JComponent createCenterPanel() {
        errorLabel = new JLabel("");
        JLabel label = new JLabel("Collection Name:");
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
        String collectionName = textField.getText();

        if (!isValidCollectionName(collectionName)) {
            errorLabel.setText("Invalid collection name");
            return;
        }

        try {
            ActiveCBLDatabase.getInstance().getDatabase().createCollection(collectionName, scopeName);
        } catch (Exception e) {
            errorLabel.setText("Failed to create collection");
            return;
        }

        super.doOKAction();
    }
}