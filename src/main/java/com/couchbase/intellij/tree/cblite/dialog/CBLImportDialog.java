package com.couchbase.intellij.tree.cblite.dialog;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import org.jetbrains.annotations.NotNull;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.tree.cblite.ActiveCBLDatabase;
import com.couchbase.intellij.tree.docfilter.DocumentFilterDialog;
import com.couchbase.intellij.workbench.Log;
import com.couchbase.lite.Collection;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Scope;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;

import utils.ColorHelper;
import utils.FileUtils;
import utils.TemplateUtil;

public class CBLImportDialog extends DialogWrapper {
    private static final int CACHE_SIZE = 6;
    private final List<JsonObject> cache = new ArrayList<>();
    private final Project project;
    ComboBox<String> scopeComboBox;
    ComboBox<String> collectionComboBox;
    private JTextField keyField;
    private JTextArea keyPreviewArea;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private int currentPage = 1;
    private TextFieldWithBrowseButton datasetField;
    private JButton nextButton;
    private JButton prevButton;
    private JBLabel universalErrorLabel;

    public CBLImportDialog(Project project, Tree tree) {
        super(project, true);
        this.project = project;
        init();
        setTitle("CBLite Import");
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(createSourceTargetPanel(), "1");
        cardPanel.add(createKeyPanel(), "2");

        mainPanel.add(cardPanel, BorderLayout.CENTER);

        JPanel universalErrorPanel = new JPanel();
        universalErrorPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        universalErrorLabel = new JBLabel();
        universalErrorLabel.setForeground(Color.decode("#FF4444"));
        universalErrorLabel.setVisible(false);
        universalErrorPanel.add(universalErrorLabel);
        mainPanel.add(universalErrorPanel, BorderLayout.SOUTH);

        addListeners();
        return mainPanel;
    }

    protected JPanel createSourceTargetPanel() {

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = JBUI.insets(10, 0);

        JPanel datasetPanel = new JPanel(new BorderLayout());

        JPanel contentPanel = new JPanel(new GridBagLayout());

        String fontKeywordColor = ColorHelper.getKeywordColor();
        JLabel infoLabel = new JLabel();
        infoLabel.setIcon(IconLoader.getIcon("/assets/icons/information_big.svg", DocumentFilterDialog.class));
        infoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                String content = "<html><h2>JSON List</h2>\n" + "<div >" + "<pre>\n" + "The list format specifies a file which contains a JSON list where <br>each element in the list is a JSON document. The file may only <br>" + "contain a single list, but the list may be specified over multiple" + "<br>lines. Below is an example of a file in list format:</pre>\n" + "\n" + "<pre>\n" + "[\n" + "    { <span style='color:" + fontKeywordColor + "'>&quot;key&quot;</span>: &quot;mykey1&quot;, <span style='color:" + fontKeywordColor + "'>&quot;value&quot;</span>: &quot;myvalue1&quot;},\n" + "    { <span style='color:" + fontKeywordColor + "'>&quot;key&quot;</span>: &quot;mykey2&quot;, <span style='color:" + fontKeywordColor + "'>&quot;value&quot;</span>: &quot;myvalue2&quot;},\n" + "    { <span style='color:" + fontKeywordColor + "'>&quot;key&quot;</span>: &quot;mykey3&quot;, <span style='color:" + fontKeywordColor + "'>&quot;value&quot;</span>: &quot;myvalue3&quot;},\n" + "        \n" + "]\n" + "</pre>\n" + "\n" + "<h2>JSON Lines</h2>\n" + "\n" + "<pre>\n" + "The lines format specifies a file that contains one JSON document " + "<br>on every line in the file. Below is an example of a file in lines<br>format:</pre>\n" + "\n" + "<pre>\n" + "{ <span style='color:" + fontKeywordColor + "'>&quot;key&quot;</span>: &quot;mykey1&quot;, <span style='color:" + fontKeywordColor + "'>&quot;value&quot;</span>: &quot;myvalue1&quot;}<br/>" + "{ <span style='color:" + fontKeywordColor + "'>&quot;key&quot;</span>: &quot;mykey2&quot;, <span style='color:" + fontKeywordColor + "'>&quot;value&quot;</span>: &quot;myvalue2&quot;}<br/>" + "{ <span style='color:" + fontKeywordColor + "'>&quot;key&quot;</span>: &quot;mykey3&quot;, <span style='color:" + fontKeywordColor + "'>&quot;value&quot;</span>: &quot;myvalue3&quot;}" + "</pre>\n" + "</div>" + "</html>";

                TemplateUtil.showGotItTooltip(e.getComponent(), content);
            }
        });
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        infoPanel.add(infoLabel);

        c.gridy++;

        c.gridx = 1;
        contentPanel.add(infoPanel, c);

        JPanel datasetLabelHelpPanel = TemplateUtil.getLabelWithHelp("Select the Dataset:", "<html>Select the file containing the data to import. The file must be in either JSON or CSV format.</html>");
        c.gridy++;
        c.gridx = 0;
        c.weightx = 0.3;
        contentPanel.add(datasetLabelHelpPanel, c);

        datasetField = new TextFieldWithBrowseButton();
        c.gridx = 1;
        c.weightx = 0.7;
        contentPanel.add(datasetField, c);

        c.gridy++;
        c.gridx = 0;
        c.weightx = 1;
        c.gridwidth = 3;
        contentPanel.add(new TitledSeparator("Target"), c);

        scopeComboBox = new ComboBox<>();
        collectionComboBox = new ComboBox<>();

        c.gridy++;
        c.weightx = 0.3;
        c.gridx = 0;
        c.gridwidth = 1;
        contentPanel.add(new JLabel("Scope: "), c);
        c.weightx = 0.7;
        c.gridx = 1;
        c.gridwidth = 3;
        contentPanel.add(scopeComboBox, c);

        c.gridy++;
        c.weightx = 0.3;
        c.gridx = 0;
        c.gridwidth = 1;
        contentPanel.add(new JLabel("Collection: "), c);
        c.weightx = 0.7;
        c.gridx = 1;
        c.gridwidth = 3;
        contentPanel.add(collectionComboBox, c);

        datasetPanel.add(contentPanel, BorderLayout.NORTH);

        return datasetPanel;
    }

    protected JPanel createKeyPanel() {
        JPanel keyPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.insets = JBUI.insets(10);

        keyField = new JTextField(25);
        keyField.setMinimumSize(getInitialSize());
        keyPreviewArea = new JTextArea(10, 25);
        keyPreviewArea.setEditable(false);

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        keyPanel.add(new TitledSeparator("Key Naming"), c);

        c.gridy++;
        c.gridwidth = 1;

        JBLabel customExpressionLabel = new JBLabel("Custom expression:");
        JPanel customExpressionLabelHelpPanel = TemplateUtil.getLabelWithHelp(customExpressionLabel, "<html>Specify the custom expression to generate the document key. You can use the following variables: <ul><li><b>#UUID#</b> - Random UUID</li><li><b>%FIELDNAME%</b> - Value of the field specified above</li></ul></html>");
        keyPanel.add(customExpressionLabelHelpPanel, c);
        c.gridx = 1;
        keyPanel.add(keyField, c);

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 2;
        keyPanel.add(new JLabel("Key Preview:"), c);

        c.gridx = 0;
        c.gridy++;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        keyPanel.add(new JScrollPane(keyPreviewArea), c);

        return keyPanel;
    }

    @Override
    protected JComponent createSouthPanel() {
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        final int LAST_PAGE = 2;
        nextButton = new JButton("Next");
        nextButton.addActionListener(e -> {
            if (currentPage < LAST_PAGE) {
                nextPage();
                prevButton.setEnabled(true);
                nextButton.setText((currentPage == LAST_PAGE) ? "Import" : "Next");
            } else {
                doOKAction();
            }
        });

        prevButton = new JButton("Previous");
        prevButton.addActionListener(e -> {
            previousPage();
            prevButton.setEnabled(currentPage > 1);
            nextButton.setEnabled(true);
            nextButton.setText((currentPage == LAST_PAGE) ? "Import" : "Next");
        });

        prevButton.setEnabled(false);
        nextButton.setEnabled(false);

        southPanel.add(prevButton);
        southPanel.add(nextButton);

        return southPanel;
    }

    private void resetErrorMessages() {
        universalErrorLabel.setText("");
        universalErrorLabel.setVisible(false);
    }

    private void nextPage() {
        resetErrorMessages();
        currentPage++;
        cardLayout.next(cardPanel);
    }

    private void previousPage() {
        resetErrorMessages();
        currentPage--;
        cardLayout.previous(cardPanel);
    }

    private void addListeners() {

        datasetField.addBrowseFolderListener("Select a File", "Please select a JSON file", null, new FileChooserDescriptor(true, false, false, false, false, false) {
            @Override
            public boolean isFileSelectable(VirtualFile file) {
                return "json".equalsIgnoreCase(file.getExtension());
            }
        });

        datasetField.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                cache.clear();
                String filePath = datasetField.getText();
                if (filePath.isEmpty() || !Files.exists(Paths.get(filePath))) {
                    nextButton.setEnabled(false);
                    universalErrorLabel.setText("Please select a valid JSON file.");
                    universalErrorLabel.setVisible(true);
                } else {
                    String datasetText = datasetField.getText();
                    String detectedCouchbaseJsonFormat;
                    try {
                        detectedCouchbaseJsonFormat = FileUtils.detectDatasetFormat(datasetText);
                        if (detectedCouchbaseJsonFormat == null) {
                            nextButton.setEnabled(false);
                            universalErrorLabel.setText("The selected file is not in a recognized format. Please select a JSON file in either 'lines' or 'list' format.");
                            universalErrorLabel.setVisible(true);
                        } else {
                            nextButton.setEnabled(true);
                            universalErrorLabel.setVisible(false);
                        }

                    } catch (IOException e1) {
                        Log.error("Error while detecting dataset format", e1);
                    }
                }
            }
        });

        Database database = ActiveCBLDatabase.getInstance().getDatabase();

        try {
            for (Scope scope : database.getScopes()) {
                scopeComboBox.addItem(scope.getName());
            }

            scopeComboBox.addActionListener(e -> {
                String selectedScope = (String) scopeComboBox.getSelectedItem();
                collectionComboBox.removeAllItems();

                try {
                    for (Collection col : database.getCollections(selectedScope)) {
                        collectionComboBox.addItem(col.getName());
                    }
                } catch (CouchbaseLiteException e1) {
                    Log.error("Error while getting collections", e1);
                }
            });
        } catch (CouchbaseLiteException e1) {
            Log.error("Error while getting scopes", e1);
        }

        scopeComboBox.getActionListeners()[0].actionPerformed(null);
        keyField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                String filePath = datasetField.getText();
                String key = keyField.getText();
                int monoIncr = 0;
                if (!filePath.isEmpty() && key != null && !key.isEmpty()) {
                    try {
                        if (cache.isEmpty()) {
                            String content = new String(Files.readAllBytes(Paths.get(filePath)));
                            if (content.trim().startsWith("[")) {
                                JsonArray jsonArray = JsonArray.fromJson(content);
                                for (int i = 0; i < Math.min(CACHE_SIZE, jsonArray.size()); i++) {
                                    cache.add(jsonArray.getObject(i));
                                }
                            } else {
                                String[] lines = content.split("\n");
                                for (int i = 0; i < Math.min(CACHE_SIZE, lines.length); i++) {
                                    cache.add(JsonObject.fromJson(lines[i]));
                                }
                            }
                        }
                        StringBuilder projectedNames = new StringBuilder();
                        for (JsonObject jsonObject : cache) {
                            String[] keyParts = key.split("%");
                            for (int i = 0; i < keyParts.length; i++) {
                                if (i % 2 == 0) {

                                    if (keyParts[i].contains("#UUID#")) {
                                        keyParts[i] = keyParts[i].replace("#UUID#", UUID.randomUUID().toString());
                                    }

                                    if (keyParts[i].contains("#MONO_INCR#")) {
                                        keyParts[i] = keyParts[i].replace("#MONO_INCR#", String.valueOf(monoIncr++));
                                    }
                                    projectedNames.append(keyParts[i]);
                                } else {
                                    String keyPart = keyParts[i];
                                    if (jsonObject.containsKey(keyPart)) {
                                        projectedNames.append(jsonObject.getString(keyPart));
                                    }
                                }
                            }
                            projectedNames.append("\n");
                        }
                        String projectedNamesStr = projectedNames.toString().trim();
                        keyPreviewArea.setText(projectedNamesStr);
                        nextButton.setEnabled(!projectedNamesStr.isEmpty());
                    } catch (Exception ex) {
                        Log.error("Error while reading file", ex);
                    }
                } else {
                    keyPreviewArea.setText("");
                    nextButton.setEnabled(false);
                }
            }
        });

    }

    @Override
    protected void doOKAction() {

        String keyPattern = keyField.getText();

        if (keyPattern == null || keyPattern.trim().isEmpty()) {

            universalErrorLabel.setText("The key field cannot be empty.");
            universalErrorLabel.setVisible(true);
            return;
        }

        String filePath = datasetField.getText();

        String selectedScope = (String) scopeComboBox.getSelectedItem();
        String selectedCollection = (String) collectionComboBox.getSelectedItem();

        try {

            String content = new String(Files.readAllBytes(Paths.get(filePath)));

            JsonArray jsonArray = JsonArray.fromJson(content);

            Database database = ActiveCBLDatabase.getInstance().getDatabase();
            Scope scope = database.getScope(Objects.requireNonNull(selectedScope));
            Collection collection = Objects.requireNonNull(scope).getCollection(Objects.requireNonNull(selectedCollection));

            Set<String> docIds = new HashSet<>();

            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonObject = jsonArray.getObject(i);

                String docId = generateDocumentId(jsonObject);

                if (docIds.contains(docId)) {

                    universalErrorLabel.setText("The keys that are generated are not unique. Please check the key pattern.");
                    universalErrorLabel.setVisible(true);
                    return;
                }

                docIds.add(docId);
            }
            ProgressManager.getInstance().run(new Task.Backgroundable(project, "Importing data", false) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    try {

                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonObject jsonObject = jsonArray.getObject(i);

                            String docId = docIds.iterator().next();
                            docIds.remove(docId);

                            MutableDocument mutableDocument;
                            if (Objects.requireNonNull(collection).getDocument(docId) != null) {
                                mutableDocument = Objects.requireNonNull(collection.getDocument(docId)).toMutable();
                            } else {
                                mutableDocument = new MutableDocument(docId);
                            }

                            for (String key : jsonObject.getNames()) {
                                Object value = jsonObject.get(key);
                                if (value instanceof JsonArray arrayValue) {
                                    List<Object> list = new ArrayList<>();
                                    for (int j = 0; j < arrayValue.size(); j++) {
                                        list.add(arrayValue.get(j));
                                    }
                                    mutableDocument.setValue(key, list);
                                } else if (value instanceof JsonObject objectValue) {
                                    Map<String, Object> map = new HashMap<>();
                                    for (String innerKey : objectValue.getNames()) {
                                        map.put(innerKey, objectValue.get(innerKey));
                                    }
                                    mutableDocument.setValue(key, map);
                                } else {
                                    mutableDocument.setValue(key, value);
                                }
                            }

                            collection.save(mutableDocument);

                        }

                        ApplicationManager.getApplication().invokeLater(() -> {
                            Messages.showInfoMessage("Data imported successfully", "Success");
                            Log.info("Data imported successfully");
                        });
                    } catch (Exception e) {
                        ApplicationManager.getApplication().invokeLater(() -> {
                            Messages.showErrorDialog("Error while importing data: " + e.getMessage(), "Error");
                            Log.error("Error while importing data", e);
                        });
                    }
                }
            });

            super.doOKAction();
        } catch (Exception e) {
            Log.error("Error while importing data", e);
        } finally {
            cache.clear();
        }
    }

    private String generateDocumentId(JsonObject jsonObject) {
        try {
            String keyPattern = keyField.getText();
            String[] keyParts = keyPattern.split("%");
            StringBuilder docIdBuilder = new StringBuilder();
            int monoIncr = 0;

            for (int i = 0; i < keyParts.length; i++) {
                if (i % 2 == 0) {

                    if (keyParts[i].contains("#UUID#")) {
                        keyParts[i] = keyParts[i].replace("#UUID#", UUID.randomUUID().toString());
                    }

                    if (keyParts[i].contains("#MONO_INCR#")) {
                        keyParts[i] = keyParts[i].replace("#MONO_INCR#", String.valueOf(monoIncr++));
                    }
                    docIdBuilder.append(keyParts[i]);
                } else {
                    String keyPart = keyParts[i];
                    if (jsonObject.containsKey(keyPart)) {
                        docIdBuilder.append(jsonObject.getString(keyPart));
                    }
                }
            }

            return docIdBuilder.toString();
        } catch (Exception e) {
            Log.error("Error while generating document id", e);
            return null;
        } finally {
            cache.clear();
        }
    }

}
