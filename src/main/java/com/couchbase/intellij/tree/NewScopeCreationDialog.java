package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.ActiveCluster;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;


public class NewScopeCreationDialog extends DialogWrapper {

    private JTextField textField;
    private JLabel errorLabel;
    private String bucketName;
    private String scopeName;
    private final Project project;
    private DialogOperation operation;

    protected NewScopeCreationDialog(
            Project project,
            String bucketName) {
        super(project);
        this.project = project;
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
        ActiveCluster active = ActiveCluster.getInstance();
        DialogOperation pending = new DialogOperation(); operation = pending;
        com.intellij.openapi.util.Disposer.register(getDisposable(), pending::cancel);
        setOKActionEnabled(false); textField.setEnabled(false); errorLabel.setText("Creating scope…");
        com.couchbase.intellij.database.ClusterUiTask.submit(project, active, "Creating Couchbase scope", connection -> {
            pending.checkCancelled(); connection.requireWritable();
            var manager = connection.sdk().bucket(bucketName).collections();
            if (manager.getAllScopes().stream().anyMatch(scope -> scope.name().equals(scopeName)))
                throw new IllegalArgumentException("Scope with name " + scopeName + " already exists");
            pending.beginMutation(connection, () -> getCancelAction().setEnabled(false));
            manager.createScope(scopeName);
            return scopeName;
        }, created -> { if (!isDisposed()) close(OK_EXIT_CODE); }, error -> {
            if (isDisposed()) return;
            operation = null; getCancelAction().setEnabled(true);
            setOKActionEnabled(true); textField.setEnabled(true); errorLabel.setText(error.getMessage());
        });
    }

    @Override public void doCancelAction() {
        if (operation == null || operation.cancel()) super.doCancelAction();
    }

    public String getEntityName() {
        return textField.getText();
    }
}
