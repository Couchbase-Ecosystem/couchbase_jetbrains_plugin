package com.couchbase.intellij.tree;

import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.intellij.database.ActiveCluster;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

enum EntityType {
    SCOPE, COLLECTION
}

public class NewEntityCreationDialog extends DialogWrapper {

    private final EntityType entityType;
    private JTextField textField;
    private JLabel errorLabel;
    private String bucketName;
    private String scopeName;
    private String collectionName;

    protected NewEntityCreationDialog(
            Project project,
            EntityType entityType,

            // 3 varargs for bucket, scope, collection
            String... names) {
        super(project);
        this.entityType = entityType;

        if (entityType == EntityType.SCOPE) {
            bucketName = names[0];
        } else if (entityType == EntityType.COLLECTION) {
            bucketName = names[0];
            scopeName = names[1];
        }

        init();
        setTitle("Create new " + entityType.toString().toLowerCase());
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

        JLabel nameLabel = new JLabel("Name of the " + entityType.toString().toLowerCase());
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
            errorLabel.setText("Please enter a name for the " + entityType.toString().toLowerCase());
            return;
        }

        // Special characters are not allowed
        if (!textField.getText().matches("[a-zA-Z0-9_]+")) {
            errorLabel
                    .setText("Special characters are not allowed in " + entityType.toString().toLowerCase()
                            + " creation");
            return;
        }

        // Check if the name already exists
        if (entityType == EntityType.SCOPE) {
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
        } else if (entityType == EntityType.COLLECTION) {
            collectionName = textField.getText();
            List<CollectionSpec> collections = ActiveCluster.getInstance().get().bucket(bucketName)
                    .collections().getAllScopes().stream()
                    .filter(scope -> scope.name().equals(scopeName))
                    .flatMap(scope -> scope.collections().stream())
                    .collect(Collectors.toList());

            for (CollectionSpec collection : collections) {
                if (collection.name().equals(collectionName)) {
                    // If it does, show error message
                    errorLabel.setText("Collection with name " + collectionName + " already exists");
                    return;
                }
            }

        }
        super.doOKAction();
    }

    public String getEntityName() {
        return textField.getText();
    }
}
