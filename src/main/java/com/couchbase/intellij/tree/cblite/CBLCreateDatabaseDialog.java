package com.couchbase.intellij.tree.cblite;

import com.couchbase.intellij.tree.cblite.storage.CBLDatabaseStorage;
import com.couchbase.intellij.tree.cblite.storage.CBLDatabases;
import com.couchbase.intellij.tree.cblite.storage.SavedCBLDatabase;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;
import utils.TemplateUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CBLCreateDatabaseDialog extends DialogWrapper {

    private final DefaultMutableTreeNode clickedNode;
    private final Tree tree;
    private JTextField textField;

    private JTextField displayNameField;
    private JLabel errorLabel;
    private TextFieldWithBrowseButton databasePathField;
    private Project project;

    protected CBLCreateDatabaseDialog(Project project, DefaultMutableTreeNode
            clickedNode, Tree tree) {
        super(project);
        this.clickedNode = clickedNode;
        this.tree = tree;
        init();
        setTitle("New Couchbase Lite Database");
        getOKAction().putValue(Action.NAME, "Create Database");
        getPeer().getWindow().setMinimumSize(new Dimension(500, 200));
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(500, 200);
    }

    @Override
    @Nullable
    protected JComponent createCenterPanel() {
        JPanel main = new JPanel(new BorderLayout());
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.4;
        gbc.insets = JBUI.insets(5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel nameLabel = new JLabel("Database Name:");

        panel.add(TemplateUtil.createComponentWithBalloon(nameLabel, "The name of your database"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        textField = new JTextField();
        panel.add(textField, gbc);


        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.4;
        gbc.insets = JBUI.insets(5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel displayName = new JLabel("Display Name:");

        panel.add(TemplateUtil.createComponentWithBalloon(displayName, "The name of the database that will be displayed in the plugin"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        displayNameField = new JTextField();
        panel.add(displayNameField, gbc);


        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.4;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(TemplateUtil.createComponentWithBalloon(new JLabel("Database's Location:"), "Path to where the Couchbase Lite database is located"), gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.6;
        databasePathField = new TextFieldWithBrowseButton();
        FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, false, false, false, false)
                .withFileFilter(file -> true);
        databasePathField.addBrowseFolderListener(
                "Select the database location",
                "Choose a folder",
                project,
                descriptor
        );

        panel.add(databasePathField, gbc);

        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.decode("#FF4444"));
        main.add(errorLabel, BorderLayout.SOUTH);

        main.add(panel, BorderLayout.CENTER);

        return main;
    }

    @Override
    protected void doOKAction() {

        List<String> errors = new ArrayList<>();

        if (textField.getText().isBlank()) {
            errors.add("The database name can't be empty");
        }

        if (displayNameField.getText().isBlank()) {
            errors.add("The display name can't be empty");

        } else if (!isValidDisplayName(displayNameField.getText())) {
            errors.add("The informed Database display name already exists");
        }

        if (databasePathField.getText().isBlank()) {
            errors.add("Please select the folder where the database will be saved");
        }

        if (!errors.isEmpty()) {
            String errorMsg = String.join("<br>", errors);
            errorLabel.setText("<html>" + errorMsg + "</html>");
            return;
        }


        String fullPath = databasePathField.getText();
        SavedCBLDatabase database = CBLDataLoader.saveNewDatabase(displayNameField.getText(), textField.getText(), fullPath);
        CBLTreeHandler.connectToDatabase(project, database, tree);
        close(DialogWrapper.CANCEL_EXIT_CODE);
        super.doOKAction();
    }

    private boolean isValidDisplayName(String displayName) {

        CBLDatabases databases = CBLDatabaseStorage.getInstance().getValue();

        for (SavedCBLDatabase db : databases.getSavedDatabases()) {
            if (db.getId().equals(displayName)) {
                return false;
            }
        }
        return true;
    }

}
