package com.couchbase.intellij.tree;

import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.intellij.database.ActiveCluster;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;



public class NewScopeCreationDialog extends DialogWrapper {

    private JTextField textField;
    private JLabel errorLabel;
    private String bucketName;
    private String scopeName;
    protected NewScopeCreationDialog(
            Project project,
            String bucketName) {
        super(project);
        this.bucketName = bucketName;


        init();
        setTitle("Create new Scope");
    }

    @Override
    @Nullable
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = JBUI.insets(5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel nameLabel = new JLabel("Name of the Scope");
        panel.add(nameLabel, gbc);

        gbc.gridy = 1;
        textField = new JTextField(20);
        panel.add(textField, gbc);

        gbc.gridy = 2;
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.decode("#FF4444"));
        panel.add(errorLabel, gbc);

        return panel;
    }

    @Override
    protected void doOKAction() {

        if (textField.getText().isEmpty()) {
            errorLabel.setText("Please enter a name for the Scope");
            return;
        }

        // Special characters are not allowed
        if (!textField.getText().matches("[a-zA-Z0-9_]+")) {
            errorLabel
                    .setText("Special characters are not allowed in scope name");
            return;
        }

        // Check if the name already exists
        scopeName = textField.getText();
        List<ScopeSpec> scopes = ActiveCluster.getInstance().get().bucket(bucketName).collections()
                .getAllScopes();
        for (ScopeSpec scope : scopes) {
            if (scope.name().equals(scopeName)) {
                // If it does, show error message
                errorLabel.setText("Scope with name " + scopeName + " already exists");
                return;
            }
        }

        super.doOKAction();
    }

    public String getEntityName() {
        return textField.getText();
    }
}
