package com.couchbase.intellij.tree.cblite;

import com.couchbase.intellij.tree.cblite.storage.SavedCBLiteDatabase;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;

public class ImportCBLiteDatabaseDialog extends DialogWrapper {

    private final DefaultMutableTreeNode clickedNode;
    private final Tree tree;
    private JTextField textField;
    private JLabel errorLabel;
    private TextFieldWithBrowseButton databasePathField;
    private Project project;

    protected ImportCBLiteDatabaseDialog(Project project, DefaultMutableTreeNode
            clickedNode, Tree tree) {
        super(project);
        this.clickedNode = clickedNode;
        this.tree = tree;
        init();
        setTitle("Import Couchbase Lite Database");
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

        JLabel nameLabel = new JLabel("Database's Name:");
        panel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        textField = new JTextField();
        panel.add(textField, gbc);


        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Database's Folder"), gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.gridx = 1;
        gbc.gridy = 1;
        databasePathField = new TextFieldWithBrowseButton();
        databasePathField.addBrowseFolderListener(
                "Select the database folder",
                "Choose a .cblite2 or .cblite3 folder",
                project,
                new FileChooserDescriptor(false, true, false, false, false, false) {
                    @Override
                    public boolean isFileSelectable(VirtualFile file) {
                        String name = file.getName();
                        return name.endsWith(".cblite2") || name.endsWith(".cblite3");
                    }
                }
        );

        panel.add(databasePathField, gbc);

        gbc.gridy = 2;
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.decode("#FF4444"));
        panel.add(errorLabel, gbc);

        return panel;
    }

    @Override
    protected void doOKAction() {
        String fullPath = databasePathField.getText();
        Path path = Paths.get(fullPath);
        String lastFolderName = path.getFileName().toString();
        String parentPathString = path.getParent().toString();

        SavedCBLiteDatabase database = CBLDataLoader.saveNewDatabase(textField.getText(), lastFolderName, parentPathString );
        CBLTreeHandler.connectToDatabase(project, database, tree);

        close(DialogWrapper.CANCEL_EXIT_CODE);
        super.doOKAction();
    }

}
