package com.couchbase.intellij.tree.cblite.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.couchbase.intellij.tools.dialog.JComboCheckBox;
import com.couchbase.intellij.tree.cblite.ActiveCBLDatabase;
import com.couchbase.lite.Collection;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.Scope;
import com.couchbase.lite.SelectResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;

import utils.TemplateUtil;

public class CBLExportDialog extends DialogWrapper {

    private JComboCheckBox scopeComboBox;
    private JComboCheckBox collectionComboBox;
    private JLabel errorLabel;

    private TextFieldWithBrowseButton destinationField;
    private JComboBox<String> outputFormatComboBox;

    private JTextField scopeKeyField;
    private JTextField collectionKeyField;
    private JTextField documentKeyField;

    public CBLExportDialog(Project project, Tree tree) {
        super(project);
        init();
        setTitle("Export");
        getWindow().setMinimumSize(new Dimension(600, 380));
        setResizable(true);
        setOKButtonText("Export");
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.weightx = 0.7;
        c.fill = GridBagConstraints.BOTH;
        c.insets = JBUI.insets(5);
        formPanel.add(new TitledSeparator("Target"), c);

        JLabel scopeLabel = new JLabel("Scope:");
        c.gridy++;
        c.gridx = 0;
        c.weightx = 0.3;
        c.gridwidth = 1;
        formPanel.add(scopeLabel, c);

        scopeComboBox = new JComboCheckBox();
        collectionComboBox = new JComboCheckBox();

        Database database = ActiveCBLDatabase.getInstance().getDatabase();
        try {
            for (Scope scopeName : database.getScopes()) {
                scopeComboBox.addItem(scopeName.getName());
            }

            scopeComboBox.setItemListener(e -> SwingUtilities.invokeLater(() -> {

                collectionComboBox.removeAllItems();
                collectionComboBox.setEnabled(true);
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
            }));

        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }

        scopeComboBox.setHint("Select one or more scopes");
        scopeComboBox.setSelectedItem(null);
        c.gridx = 1;
        c.weightx = 0.7;
        formPanel.add(scopeComboBox, c);

        JLabel collectionLabel = new JLabel("Collection:");
        c.gridx = 0;
        c.gridy++;
        c.weightx = 0.3;
        formPanel.add(collectionLabel, c);

        collectionComboBox.setEnabled(false);
        collectionComboBox.setHint("Select one or more collections");
        c.gridx = 1;
        c.weightx = 0.7;
        formPanel.add(collectionComboBox, c);

        JLabel scopeKeyLabel = new JLabel("Scope Key:");
        JPanel scopeKeyPanel = TemplateUtil.getLabelWithHelp(scopeKeyLabel,
                "<html>This filed will be used to store the name of the scope the document came from. It will be created on each JSON document.</html>");
        c.gridx = 0;
        c.gridy++;
        c.weightx = 0.3;
        formPanel.add(scopeKeyPanel, c);

        scopeKeyField = new JTextField();
        scopeKeyField.setText("cbms");
        c.gridx = 1;
        c.weightx = 0.7;
        formPanel.add(scopeKeyField, c);

        JLabel collectionKeyLabel = new JLabel("Collection Key:");
        JPanel collectionKeyPanel = TemplateUtil.getLabelWithHelp(collectionKeyLabel,
                "<html>This filed will be used to store the name of the collection the document came from. It will be created on each JSON document.</html>");
        c.gridx = 0;
        c.gridy++;
        c.weightx = 0.3;
        formPanel.add(collectionKeyPanel, c);

        collectionKeyField = new JTextField();
        collectionKeyField.setText("cbmc");
        c.gridx = 1;
        c.weightx = 0.7;
        formPanel.add(collectionKeyField, c);

        JLabel documentKeyLabel = new JLabel("Document Key:");
        JPanel documentKeyPanel = TemplateUtil.getLabelWithHelp(documentKeyLabel,
                "<html>In Couchbase, the document's key is not "
                        + "part of the body of the document. But when you are exporting the dataset, it is recommended to also include "
                        + "the original keys. This property defines the name of the attribute in the final exported file that will contain the document's key.</html>");
        c.gridx = 0;
        c.gridy++;
        c.weightx = 0.3;
        formPanel.add(documentKeyPanel, c);

        documentKeyField = new JTextField();
        documentKeyField.setText("cbmid");
        c.gridx = 1;
        c.weightx = 0.7;
        formPanel.add(documentKeyField, c);

        JLabel outputFormatLabel = new JLabel("Output Format:");
        c.gridx = 0;
        c.gridy++;
        c.weightx = 0.3;
        formPanel.add(outputFormatLabel, c);

        outputFormatComboBox = new JComboBox<>(new String[] { "JSON Lines", "JSON List" });
        c.gridx = 1;
        c.weightx = 0.7;
        formPanel.add(outputFormatComboBox, c);

        JLabel destinationLabel = new JLabel("Destination Folder:");
        c.gridx = 0;
        c.gridy++;
        c.weightx = 0.3;
        formPanel.add(destinationLabel, c);

        destinationField = new TextFieldWithBrowseButton();
        destinationField.addBrowseFolderListener("Select Destination Folder", "Select Destination Folder", null,
                FileChooserDescriptorFactory.createSingleFolderDescriptor());
        destinationField.setText(System.getProperty("user.home"));
        c.gridx = 1;
        c.weightx = 0.7;
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

        List<String> selectedCollections = collectionComboBox.getSelectedItems();
        Database database = ActiveCBLDatabase.getInstance().getDatabase();

        String scopeKey = scopeKeyField.getText();
        String collectionKey = collectionKeyField.getText();
        String documentKey = documentKeyField.getText();

        try (FileWriter writer = new FileWriter(destinationField.getText() + "/export.json")) {
            String outputFormat = (String) outputFormatComboBox.getSelectedItem();
            boolean isListFormat = "JSON List".equals(outputFormat);

            if (isListFormat) {
                writer.write("[\n");
            }
            // Your existing code here

            for (String collectionPath : selectedCollections) {
                String[] parts = collectionPath.split("\\.");
                String scopeName = parts[0];
                String collectionName = parts[1];

                Collection collection = database.getScope(scopeName).getCollection(collectionName);

                Query query = QueryBuilder.select(SelectResult.all())
                        .from(DataSource.collection(Objects.requireNonNull(collection)));

                try (ResultSet resultSet = query.execute()) {
                    for (Result result : resultSet) {
                        String docId = result.getString("id");
                        Dictionary content = result.getDictionary(collectionName);

                        // Convert the content to a JSON string
                        String json = Objects.requireNonNull(content).toJSON();

                        // Convert the JSON string to a map
                        Map<String, Object> map = new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {
                        }.getType());

                        // Overwrite the scope, collection, and document key fields
                        map.put(scopeKey, scopeName);
                        map.put(collectionKey, collectionName);
                        map.put(documentKey, docId);

                        // Convert the map back to a JSON string
                        json = new Gson().toJson(map);

                        writer.write(json);

                        writer.write(isListFormat ? ",\n" : "\n");
                    }
                }
            }

            if (isListFormat) {
                writer.write("]\n");
            }

        } catch (IOException | CouchbaseLiteException e) {
            // Handle the exception
            errorLabel.setText(e.getMessage());
            return;
        }

        super.doOKAction();
    }

}
