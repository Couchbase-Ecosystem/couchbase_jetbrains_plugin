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
        getPeer().getWindow().setMinimumSize(new Dimension(360, 120));
    }

    @Override
    @Nullable
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.insets = JBUI.insets(3);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel nameLabel = new JLabel("Scope name");
        panel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        textField = new JTextField();
        panel.add(textField, gbc);

        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.decode("#FF4444"));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(panel, BorderLayout.NORTH);
        wrapper.add(errorLabel, BorderLayout.SOUTH);

        return wrapper;
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
