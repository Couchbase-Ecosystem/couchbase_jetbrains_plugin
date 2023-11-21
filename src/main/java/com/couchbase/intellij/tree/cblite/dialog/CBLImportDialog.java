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
import java.util.List;

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
import com.couchbase.lite.Collection;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Scope;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;

import utils.FileUtils;
import utils.TemplateUtil;

public class CBLImportDialog extends DialogWrapper {
    private JTextField keyField;
    private JTextArea keyPreviewArea;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private int currentPage = 1;
    private TextFieldWithBrowseButton datasetField;

    private JButton nextButton;
    private JButton prevButton;

    private JPanel mainPanel;
    private JPanel southPanel;
    private JPanel datasetPanel;
    private JPanel datasetFormPanel;
    private JPanel datasetLabelHelpPanel;
    private JPanel universalErrorPanel;
    private JBLabel universalErrorLabel;

    ComboBox<String> scopeComboBox;
    ComboBox<String> collectionComboBox;

    private static final int CACHE_SIZE = 6;
    private List<JsonObject> cache = new ArrayList<>();

    public CBLImportDialog(Project project, Tree tree) {
        super(project, true);
        init();
        setTitle("CBLite Import");
    }

    @Override
    protected JComponent createCenterPanel() {
        mainPanel = new JPanel(new BorderLayout());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(createSourceTargetPanel(), "1");
        cardPanel.add(createKeyPanel(), "2");

        mainPanel.add(cardPanel, BorderLayout.CENTER);

        universalErrorPanel = new JPanel();
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
        c.insets = JBUI.insets(10);

        datasetPanel = new JPanel(new BorderLayout());

        JPanel contentPanel = new JPanel(new GridBagLayout());

        JLabel infoLabel = new JLabel();
        infoLabel.setIcon(IconLoader.getIcon("/assets/icons/information_big.svg", DocumentFilterDialog.class));
        infoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                String content = "<html><h2>List Format</h2><br>" +
                        "<p>The list format specifies a file which contains a JSON list where each element in the list is a JSON document. The file may only contain a single list, but the list may be specified over multiple lines. This format is specified by setting the --format option to \"list\". Below is an example of a file in list format.</p><br>"
                        +
                        "<p>[<br>" +
                        "{<br>" +
                        "\"key\": \"mykey1\",<br>" +
                        "\"value\": \"myvalue1\"<br>" +
                        "},<br>" +
                        "{\"key\": \"mykey2\", \"value\": \"myvalue2\"}<br>" +
                        "{\"key\": \"mykey3\", \"value\": \"myvalue3\"}<br>" +
                        "{\"key\": \"mykey4\", \"value\": \"myvalue4\"}<br>" +
                        "]</p><br>" +
                        "<h2>Lines Format</h2><br>" +
                        "<p>The lines format specifies a file that contains one JSON document on every line in the file. This format is specified by setting the --format option to \"lines\". Below is an example of a file in lines format.</p><br>"
                        +
                        "<p>{\"key\": \"mykey1\", \"value\": \"myvalue1\"}<br>" +
                        "{\"key\": \"mykey2\", \"value\": \"myvalue2\"}<br>" +
                        "{\"key\": \"mykey3\", \"value\": \"myvalue3\"}<br>" +
                        "{\"key\": \"mykey4\", \"value\": \"myvalue4\"}<br>" +
                        "</p></html>";
                DocumentFilterDialog.showGotItTooltip(e.getComponent(), content);
            }
        });

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        infoPanel.add(infoLabel);

        c.gridy++;

        c.gridx = 3;
        contentPanel.add(infoPanel, c);

        datasetFormPanel = new JPanel();
        datasetFormPanel.setBorder(JBUI.Borders.empty(0, 10));
        datasetFormPanel.setLayout(new GridBagLayout());

        datasetLabelHelpPanel = TemplateUtil.getLabelWithHelp(
                "Select the Dataset:",
                "<html>Select the file containing the data to import. The file must be in either JSON or CSV format.</html>");
        c.gridy++;
        c.weightx = 0.1;
        c.gridx = 0;
        datasetFormPanel.add(datasetLabelHelpPanel, c);

        datasetField = new TextFieldWithBrowseButton();
        c.weightx = 0.9;
        c.gridx = 1;
        datasetFormPanel.add(datasetField, c);

        c.gridy++;
        c.gridx = 0;
        c.gridwidth = 4;
        contentPanel.add(datasetFormPanel, c);

        c.gridy++;
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
        keyPreviewArea = new JTextArea(10, 25);
        keyPreviewArea.setEditable(false);

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        keyPanel.add(new TitledSeparator("Key Naming"), c);

        c.gridy++;
        c.gridwidth = 1;
        keyPanel.add(new JLabel("Custom Expression:"), c);

        c.gridx = 1;
        keyPanel.add(keyField, c);

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 2;
        keyPanel.add(new JLabel("Projected Names:"), c);

        c.gridx = 0;
        c.gridy++;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        keyPanel.add(new JScrollPane(keyPreviewArea), c);

        return keyPanel;
    }

    @Override
    protected JComponent createSouthPanel() {
        southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        final int LAST_PAGE = 2;
        nextButton = new JButton("Next");
        nextButton.addActionListener(e -> {
            if (currentPage < LAST_PAGE) {
                nextPage();
                prevButton.setEnabled(currentPage > 1);
                nextButton.setText((currentPage == LAST_PAGE) ? "Import" : "Next");
            } else {
                doOKAction();
            }
        });

        prevButton = new JButton("Previous");
        prevButton.addActionListener(e -> {
            previousPage();
            prevButton.setEnabled(currentPage > 1);
            nextButton.setText((currentPage == LAST_PAGE) ? "Import" : "Next");
        });

        prevButton.setEnabled(false);
        nextButton.setEnabled(false);

        southPanel.add(prevButton);
        southPanel.add(nextButton);

        return southPanel;
    }

    private void nextPage() {
        currentPage++;
        cardLayout.next(cardPanel);
    }

    private void previousPage() {
        currentPage--;
        cardLayout.previous(cardPanel);
    }

    private void addListeners() {

        datasetField.addBrowseFolderListener("Select a File", "Please select a JSON file", null,
                new FileChooserDescriptor(true, false, false, false, false, false) {
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
                    String detectedCouchbaseJsonFormat = null;
                    try {
                        detectedCouchbaseJsonFormat = FileUtils.detectDatasetFormat(datasetText);
                        if (detectedCouchbaseJsonFormat == null) {
                            nextButton.setEnabled(false);
                            universalErrorLabel.setText(
                                    "The selected file is not in a recognized format. Please select a JSON file in either 'lines' or 'list' format.");
                            universalErrorLabel.setVisible(true);
                        } else {
                            nextButton.setEnabled(true);
                            universalErrorLabel.setVisible(false);
                        }

                    } catch (IOException e1) {
                        e1.printStackTrace();
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
                    e1.printStackTrace();
                }
            });
        } catch (CouchbaseLiteException e1) {
            e1.printStackTrace();
        }

        scopeComboBox.getActionListeners()[0].actionPerformed(null);
        keyField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                String filePath = datasetField.getText();
                String key = keyField.getText();
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
                        keyPreviewArea.setText(projectedNames.toString());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

        });

    }

    @Override
    protected void doOKAction() {

    }
}
