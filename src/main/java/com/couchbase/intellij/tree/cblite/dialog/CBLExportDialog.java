package com.couchbase.intellij.tree.cblite.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;
import java.util.Objects;

import javax.swing.*;

import com.couchbase.intellij.tools.dialog.JComboCheckBox;
import com.couchbase.intellij.tree.cblite.ActiveCBLDatabase;
import com.couchbase.lite.Collection;
import com.couchbase.lite.Database;
import com.couchbase.lite.Scope;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.treeStructure.Tree;

public class CBLExportDialog extends DialogWrapper {
    private Project project;
    private Tree tree;

    private TextFieldWithBrowseButton exportPathField;
    private JComboCheckBox scopeComboBox;
    private JComboCheckBox collectionComboBox;
    private JLabel errorLabel;

    public CBLExportDialog(Project project, Tree tree) {
        super(project);
        this.project = project;
        this.tree = tree;
        init();
        setTitle("Export");
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(new TitledSeparator("Target"), c);

        JLabel scopeLabel = new JLabel("Scope:");
        c.gridy++;
        c.gridx = 0;
        c.weightx = 0.0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        formPanel.add(scopeLabel, c);

        scopeComboBox = new JComboCheckBox();
        collectionComboBox = new JComboCheckBox();

        Database database = ActiveCBLDatabase.getInstance().getDatabase();
        try {
            for (Scope scopeName : database.getScopes()) {
                scopeComboBox.addItem(scopeName.getName());
            }

            scopeComboBox.setItemListener(e -> SwingUtilities.invokeLater(() -> {
                collectionComboBox.setEnabled(true);
                collectionComboBox.removeAllItems();
                List<String> selectedScopes = scopeComboBox.getSelectedItems();
                for (String selectedScope : selectedScopes) {
                    try {
                        for (Collection collectionName : Objects.requireNonNull(database.getScope(selectedScope))
                                .getCollections()) {
                            collectionComboBox.addItem(selectedScope + "." + collectionName.getName());
                        }
                    } catch (Exception exception) {
                        errorLabel.setText(exception.getMessage());
                    }
                }
                collectionComboBox.revalidate();
                collectionComboBox.repaint();
            }));

        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }


        scopeComboBox.setHint("Select one or more scopes");
        c.gridx = 1;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(scopeComboBox, c);

        JLabel collectionLabel = new JLabel("Collection:");
        c.gridx = 0;
        c.gridy++;
        c.weightx = 0.0;
        c.fill = GridBagConstraints.NONE;
        formPanel.add(collectionLabel, c);

        collectionComboBox.setEnabled(false);
        collectionComboBox.setHint("Select one or more collections");
        c.gridx = 1;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(collectionComboBox, c);

        JLabel scopeKeyLabel = new JLabel("Scope Key:");
        c.gridx = 0;
        c.gridy++;
        c.weightx = 0.0;
        c.fill = GridBagConstraints.NONE;
        formPanel.add(scopeKeyLabel, c);

        JTextField scopeKeyField = new JTextField();
        c.gridx = 1;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(scopeKeyField, c);

        JLabel collectionKeyLabel = new JLabel("Collection Key:");
        c.gridx = 0;
        c.gridy++;
        c.weightx = 0.0;
        c.fill = GridBagConstraints.NONE;
        formPanel.add(collectionKeyLabel, c);

        JTextField collectionKeyField = new JTextField();
        c.gridx = 1;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(collectionKeyField, c);

        JLabel outputFormatLabel = new JLabel("Output Format:");
        c.gridx = 0;
        c.gridy++;
        c.weightx = 0.0;
        c.fill = GridBagConstraints.NONE;
        formPanel.add(outputFormatLabel, c);

        JComboBox<String> outputFormatComboBox = new JComboBox<>(new String[] { "JSON Lines", "JSON List" });
        c.gridx = 1;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(outputFormatComboBox, c);

        JLabel destinationLabel = new JLabel("Destination Folder:");
        c.gridx = 0;
        c.gridy++;
        c.weightx = 0.0;
        c.fill = GridBagConstraints.NONE;
        formPanel.add(destinationLabel, c);

        TextFieldWithBrowseButton destinationField = new TextFieldWithBrowseButton();
        c.gridx = 1;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(destinationField, c);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel errorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.decode("#FF4444"));
        errorPanel.add(errorLabel);
        mainPanel.add(errorPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    @Override
    protected void doOKAction() {

        List<String> selectedScopes = scopeComboBox.getSelectedItems();
        List<String> selectedCollections = collectionComboBox.getSelectedItems();
        Database database = ActiveCBLDatabase.getInstance().getDatabase();

        try {

        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
            return;
        }
        super.doOKAction();
    }
}
