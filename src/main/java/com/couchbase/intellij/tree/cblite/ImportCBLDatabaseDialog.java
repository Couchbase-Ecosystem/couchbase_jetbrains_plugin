package com.couchbase.intellij.tree.cblite;

import com.couchbase.intellij.tree.cblite.storage.SavedCBLDatabase;
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
import utils.TemplateUtil;

public class ImportCBLDatabaseDialog extends DialogWrapper {

    private final DefaultMutableTreeNode clickedNode;
    private final Tree tree;
    private JTextField textField;
    private JLabel errorLabel;
    private TextFieldWithBrowseButton databasePathField;
    private Project project;

    protected ImportCBLDatabaseDialog(Project project, DefaultMutableTreeNode
            clickedNode, Tree tree) {
        super(project);
        this.clickedNode = clickedNode;
        this.tree = tree;
        init();
        setTitle("Import Couchbase Lite Database");
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

        JLabel nameLabel = new JLabel("Database's Display Name:");

        panel.add(TemplateUtil.createComponentWithBalloon(nameLabel, "The name of the database that will be displayed in the plugin"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        textField = new JTextField();
        panel.add(textField, gbc);


        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.4;
        panel.add(TemplateUtil.createComponentWithBalloon(new JLabel("Database's Location:"),"Path to where the Couchbase Lite database is located"), gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.6;
        databasePathField = new TextFieldWithBrowseButton();
        databasePathField.addBrowseFolderListener(
                "Select the database location",
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

        panel.add(databasePathField,  gbc);

        gbc.gridy = 2;
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.decode("#FF4444"));
        panel.add(errorLabel, gbc);

        main.add(panel, BorderLayout.CENTER);

        return main;
    }

    @Override
    protected void doOKAction() {
        String fullPath = databasePathField.getText();
        Path path = Paths.get(fullPath);
        String lastFolderName = path.getFileName().toString();
        String parentPathString = path.getParent().toString();

        String dbname = lastFolderName.substring(0, lastFolderName.indexOf("."));

        SavedCBLDatabase database = CBLDataLoader.saveNewDatabase(textField.getText(), dbname, parentPathString );
        CBLTreeHandler.connectToDatabase(project, database, tree);

        close(DialogWrapper.CANCEL_EXIT_CODE);
        super.doOKAction();
    }

}
