
package com.couchbase.intellij.tools.dialog;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;

import org.jetbrains.annotations.NotNull;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tools.CBTools;
import com.couchbase.intellij.tree.NewEntityCreationDialog;
import com.couchbase.intellij.types.EntityType;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;

import utils.FileUtils;

public class ImportDialog extends DialogWrapper {

    private final Project project;

    // Declare UI components here
    private TextFieldWithBrowseButton datasetField;

    private ComboBox<String> bucketCombo;
    private ComboBox<String> scopeCombo;
    private ComboBox<String> collectionCombo;

    private JBRadioButton defaultScopeAndCollectionRadio;
    private JBRadioButton collectionRadio;
    private JBRadioButton dynamicScopeAndCollectionRadio;

    private JBTextField dynamicScopeFieldField;
    private JBTextField dynamicCollectionFieldField;

    private JBRadioButton generateUUIDRadio;
    private JBRadioButton useFieldValueRadio;
    private JBRadioButton customExpressionRadio;

    private JBTextField fieldNameField;
    private JBTextField customExpressionField;

    private JBTextField skipFirstField;
    private JBCheckBox skipFirstCheck;

    private JBTextField importUptoField;
    private JBCheckBox importUptoCheck;

    private JBTextField ignoreFieldsField;
    private JBCheckBox ignoreFieldsCheck;

    private JSpinner threadsSpinner;

    private JBCheckBox verboseCheck;

    private ButtonGroup targetLocationGroup;
    private ButtonGroup keyGroup;

    // Declare additional components for navigation and summary
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JTextArea keyPreviewArea;

    // Declare labels for each field
    private JBLabel scopeLabel;
    private JBLabel collectionLabel;
    private JBLabel scopeFieldLabel;
    private JBLabel collectionFieldLabel;
    private JBLabel keyLabel;
    private JBLabel fieldNameLabel;
    private JBLabel customExpressionLabel;
    private JBLabel skipFirstLabel;
    private JBLabel importUptoLabel;
    private JBLabel ignoreFieldsLabel;
    private JBLabel threadsLabel;
    private JBLabel verboseLabel;

    // Declare label for summary
    private JBLabel summaryLabel;

    private TitledSeparator keyPreviewTitledSeparator;

    // Declare actions for back and next buttons
    private Action backAction;
    private Action nextAction;
    private Action cancelAction;

    private int currentPage = 1;

    private final String[] possibleScopeFields = { "cbms", "scope", "cbs" };
    private final String[] possibleCollectionFields = { "cbmc", "collection", "cbc" };
    private final String[] possibleKeyFields = { "cbmk", "cbmid", "key", "cbk" };

    private String targetScopeField;
    private String targetCollectionField;

    public ImportDialog(Project project) {
        super(true);
        this.project = project;
        init();
        setTitle("Import Data");
        getWindow().setMinimumSize(new Dimension(600, 400));
        setResizable(true);
        setOKButtonText("Import");
    }

    @Override
    protected JComponent createCenterPanel() {

        // Create and add UI components for each page here
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Page 1: Select dataset
        JPanel datasetPanel = new JPanel(new BorderLayout());
        // datasetPanel.add(new TitledSeparator("Dataset Selection"),
        // BorderLayout.NORTH);

        JPanel datasetFormPanel = new JPanel();
        datasetFormPanel.setBorder(JBUI.Borders.empty(0, 10));
        datasetFormPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridy = 0;
        c.weightx = 0.3;
        c.gridx = 0;
        datasetFormPanel.add(new JBLabel("Select the Dataset:"), c);
        c.weightx = 0.7;
        c.gridx = 1;

        datasetField = new TextFieldWithBrowseButton();
        datasetField.addBrowseFolderListener("Select the Dataset", "", null,
                FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor());
        // Add a listener for when the datasetField is changed
        datasetField.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                Log.debug("Dataset field changed: " + datasetField.getText());
                setNextButtonOnDatasetSelection();
            }
        });
        datasetFormPanel.add(datasetField, c);

        datasetPanel.add(datasetFormPanel, BorderLayout.CENTER);

        // Page 2: Select bucket and target location
        JPanel targetPanel = new JPanel(new BorderLayout());
        targetPanel.add(new TitledSeparator("Target Location"), BorderLayout.NORTH);

        JPanel targetFormPanel = new JPanel();
        targetFormPanel.setBorder(JBUI.Borders.empty(0, 10));
        targetFormPanel.setLayout(new GridBagLayout());

        // Bucket label and combobox
        c.gridy = 0;
        c.weightx = 0.3;
        c.gridx = 0;
        targetFormPanel.add(new JBLabel("Bucket:"), c);
        c.weightx = 0.7;
        c.gridx = 1;

        Set<String> bucketSet = ActiveCluster.getInstance().get().buckets().getAllBuckets().keySet();
        String[] buckets = bucketSet.toArray(new String[0]);

        bucketCombo = new ComboBox<>(buckets);
        targetFormPanel.add(bucketCombo, c);

        // Radio buttons for scope and collection options
        c.gridy = 1;
        c.weightx = 0.3;
        c.gridx = 0;
        targetFormPanel.add(new JBLabel("Scope and Collection:"), c);
        c.weightx = 0.7;
        c.gridx = 1;

        defaultScopeAndCollectionRadio = new JBRadioButton("Default scope and collection");
        collectionRadio = new JBRadioButton("Collection");
        dynamicScopeAndCollectionRadio = new JBRadioButton("Dynamic scope and collection");

        targetLocationGroup = new ButtonGroup();
        targetLocationGroup.add(defaultScopeAndCollectionRadio);
        targetLocationGroup.add(collectionRadio);
        targetLocationGroup.add(dynamicScopeAndCollectionRadio);

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
        radioPanel.add(defaultScopeAndCollectionRadio);
        radioPanel.add(collectionRadio);
        radioPanel.add(dynamicScopeAndCollectionRadio);

        targetFormPanel.add(radioPanel, c);

        // Scope and collection dropdowns
        c.gridy = 2;
        c.weightx = 0.3;
        c.gridx = 0;
        scopeLabel = new JBLabel("Scope:");
        targetFormPanel.add(scopeLabel, c);
        c.weightx = 0.7;
        c.gridx = 1;

        /// Set of scopes
        List<ScopeSpec> scopes = new ArrayList<>();
        scopeCombo = new ComboBox<>();
        // Add a listener for when the bucketCombo is changed
        ActionListener bucketComboListener = (ActionEvent e) -> {
            try {
                Log.debug("Bucket combo box changed"
                        + Optional.ofNullable(bucketCombo.getSelectedItem()).map(Object::toString).orElse("null"));

                // Get all scopes for selected bucket
                scopes.clear();
                scopes.addAll(ActiveCluster.getInstance().get().bucket(
                        bucketCombo.getSelectedItem().toString())
                        .collections().getAllScopes());

                // Update scope combo box items
                Set<String> scopeSet = scopes.stream()
                        .map(scope -> scope.name())
                        .collect(Collectors.toSet());
                String[] scopeItems = scopeSet.toArray(new String[0]);
                scopeCombo.removeAllItems();
                for (String scopeItem : scopeItems) {
                    scopeCombo.addItem(scopeItem);
                }
            } catch (Exception ex) {
                Log.error(ex);
                ex.printStackTrace();
            }
        };

        bucketCombo.addActionListener(bucketComboListener);

        targetFormPanel.add(scopeCombo, c);

        c.gridy = 3;
        c.weightx = 0.3;
        c.gridx = 0;
        collectionLabel = new JBLabel("Collection:");
        targetFormPanel.add(collectionLabel, c);
        c.weightx = 0.7;
        c.gridx = 1;

        collectionCombo = new ComboBox<>();

        // Add a listener for when the scopeCombo is changed
        ActionListener scopeComboListener = (ActionEvent e) -> {
            try {
                Log.debug("Scope combo box changed"
                        + Optional.ofNullable(scopeCombo.getSelectedItem()).map(Object::toString).orElse("null"));

                String selectedScope = (String) scopeCombo.getSelectedItem();
                if (selectedScope == null || selectedScope.isEmpty() || selectedScope.equals("_default")) {
                    // Set the selected scope to _default and the selected collection to _default
                    scopeCombo.setSelectedItem("_default");
                    collectionCombo.setSelectedItem("_default");
                    return;
                }

                // Set of collections from the selected scope
                String[] collectionItems = scopes.stream()
                        .filter(scope -> scope.name().equals(selectedScope))
                        .flatMap(scope -> scope.collections().stream())
                        .map(CollectionSpec::name)
                        .distinct()
                        .toArray(String[]::new);

                collectionCombo.removeAllItems();
                for (String collectionItem : collectionItems) {
                    collectionCombo.addItem(collectionItem);
                }

                if (collectionCombo.getItemCount() > 0) {
                    collectionCombo.setSelectedIndex(0);
                } else {
                    // If the selected scope has no collections, inform the user and ask if they
                    // want to create a new collection
                    int result = Messages.showYesNoDialog(project,
                            "The selected scope is empty. Would you like to create a new collection in this scope?",
                            "Empty Scope", Messages.getQuestionIcon());

                    if (result == Messages.YES) {
                        // If the user selects yes, create a new collection
                        if (!ActiveCluster.getInstance().isReadOnlyMode()) {
                            NewEntityCreationDialog entityCreationDialog = new NewEntityCreationDialog(
                                    project,
                                    EntityType.COLLECTION,
                                    Objects.requireNonNull(bucketCombo.getSelectedItem()).toString(),
                                    selectedScope);
                            entityCreationDialog.show();

                            if (entityCreationDialog.isOK()) {
                                String collectionName = entityCreationDialog.getEntityName();
                                ActiveCluster.getInstance().get()
                                        .bucket(bucketCombo.getSelectedItem().toString()).collections()
                                        .createCollection(CollectionSpec.create(collectionName, selectedScope));

                                // Add the new collection to the list of options in the collection combo box
                                collectionCombo.addItem(collectionName);

                                // Select the new collection in the combo box
                                collectionCombo.setSelectedItem(collectionName);
                            }
                        }
                    } else {
                        // If the user selects no, set the selected scope to _default and the selected
                        // collection to _default
                        scopeCombo.setSelectedItem("_default");
                        collectionCombo.setSelectedItem("_default");
                    }
                }
            } catch (Exception ex) {
                Log.error(ex);
                ex.printStackTrace();
            }
        };

        scopeCombo.addActionListener(scopeComboListener);
        targetFormPanel.add(collectionCombo, c);

        // Scope and collection fields
        c.gridy = 4;
        c.weightx = 0.3;
        c.gridx = 0;
        scopeFieldLabel = new JBLabel("Scope field:");
        targetFormPanel.add(scopeFieldLabel, c);

        c.weightx = 0.7;
        c.gridx = 1;

        dynamicScopeFieldField = new JBTextField();
        targetFormPanel.add(dynamicScopeFieldField, c);

        c.gridy = 5;
        c.weightx = 0.3;
        c.gridx = 0;
        collectionFieldLabel = new JBLabel("Collection field:");
        targetFormPanel.add(collectionFieldLabel, c);
        c.weightx = 0.7;
        c.gridx = 1;

        dynamicCollectionFieldField = new JBTextField();
        targetFormPanel.add(dynamicCollectionFieldField, c);

        // Set all labels to invisible and disabled by default
        scopeLabel.setVisible(false);
        collectionLabel.setVisible(false);
        scopeFieldLabel.setVisible(false);
        collectionFieldLabel.setVisible(false);

        scopeLabel.setEnabled(false);
        collectionLabel.setEnabled(false);
        scopeFieldLabel.setEnabled(false);
        collectionFieldLabel.setEnabled(false);

        // Set all fields to invisible and disabled by default
        scopeCombo.setVisible(false);
        collectionCombo.setVisible(false);
        dynamicScopeFieldField.setVisible(false);
        dynamicCollectionFieldField.setVisible(false);

        scopeCombo.setEnabled(false);
        collectionCombo.setEnabled(false);
        dynamicScopeFieldField.setEnabled(false);
        dynamicCollectionFieldField.setEnabled(false);

        targetPanel.add(targetFormPanel, BorderLayout.CENTER);

        // Page 3: Document key options
        JPanel keyPanel = new JPanel(new BorderLayout());
        keyPanel.add(new TitledSeparator("Document Key"), BorderLayout.NORTH);

        JPanel keyFormPanel = new JPanel();
        keyFormPanel.setBorder(JBUI.Borders.empty(0, 10));
        keyFormPanel.setLayout(new GridBagLayout());

        // Radio buttons for document key options
        c.gridy = 0;
        c.weightx = 0.3;
        c.gridx = 0;
        keyLabel = new JBLabel("Key options:");
        keyFormPanel.add(keyLabel, c);
        c.weightx = 0.7;
        c.gridx = 1;

        generateUUIDRadio = new JBRadioButton("Generate random UUID for each document");
        useFieldValueRadio = new JBRadioButton("Use the value of a field as the key");
        customExpressionRadio = new JBRadioButton("Generate key based on custom expression");

        keyGroup = new ButtonGroup();
        keyGroup.add(generateUUIDRadio);
        keyGroup.add(useFieldValueRadio);
        keyGroup.add(customExpressionRadio);

        JPanel keyRadioPanel = new JPanel();
        keyRadioPanel.setLayout(new BoxLayout(keyRadioPanel, BoxLayout.Y_AXIS));
        keyRadioPanel.add(generateUUIDRadio);
        keyRadioPanel.add(useFieldValueRadio);
        keyRadioPanel.add(customExpressionRadio);

        keyFormPanel.add(keyRadioPanel, c);

        // Field name field
        c.gridy = 1;
        c.weightx = 0.3;
        c.gridx = 0;
        fieldNameLabel = new JBLabel("Field name:");
        keyFormPanel.add(fieldNameLabel, c);
        c.weightx = 0.7;
        c.gridx = 1;

        fieldNameField = new JBTextField();
        keyFormPanel.add(fieldNameField, c);

        // Expression field
        c.gridy = 2;
        c.weightx = 0.3;
        c.gridx = 0;
        customExpressionLabel = new JBLabel("Custom Expression:");
        keyFormPanel.add(customExpressionLabel, c);

        c.weightx = 0.7;
        c.gridx = 1;

        customExpressionField = new JBTextField();
        keyFormPanel.add(customExpressionField, c);

        // In addListeners method, add listeners for relevant fields:
        fieldNameField.getDocument().addDocumentListener(new DocumentAdapter() {

            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                Log.debug("Field name field changed" + fieldNameField.getText());
                updateKeyPreview();
            }
        });

        customExpressionField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                Log.debug("Custom Expression field changed" + customExpressionField.getText());
                updateKeyPreview();
            }
        });

        keyPanel.add(keyFormPanel, BorderLayout.CENTER);

        JPanel keyPreviewPanel = new JPanel(new BorderLayout());
        keyPreviewTitledSeparator = new TitledSeparator("Key Preview");
        keyPreviewTitledSeparator.setVisible(false);
        keyPreviewTitledSeparator.setEnabled(false);
        keyPreviewPanel.add(keyPreviewTitledSeparator, BorderLayout.NORTH);

        keyPreviewArea = new JTextArea();
        keyPreviewArea.setEditable(false);
        keyPreviewArea.setLineWrap(true);
        keyPreviewArea.setWrapStyleWord(true);
        keyPreviewArea.setMinimumSize(new Dimension(150, 100));
        keyPreviewArea.setPreferredSize(new Dimension(150, 100));

        keyPreviewPanel.add(keyPreviewArea, BorderLayout.CENTER);

        keyPanel.add(keyPreviewPanel, BorderLayout.SOUTH);

        // Set all labels to invisible and disabled by default
        fieldNameLabel.setVisible(false);
        customExpressionLabel.setVisible(false);
        fieldNameLabel.setEnabled(false);
        customExpressionLabel.setEnabled(false);

        // Set all fields to invisible and disabled by default
        fieldNameField.setVisible(false);
        customExpressionField.setVisible(false);
        fieldNameField.setEnabled(false);
        customExpressionField.setEnabled(false);

        // Set the preview panel to invisible and disabled by default
        keyPreviewArea.setVisible(false);
        keyPreviewArea.setEnabled(false);

        // Page 4: Advanced options
        JPanel advancedPanel = new JPanel(new BorderLayout());
        advancedPanel.add(new TitledSeparator("Advanced Options"), BorderLayout.NORTH);

        JPanel advancedFormPanel = new JPanel();
        advancedFormPanel.setBorder(JBUI.Borders.empty(0, 10));
        advancedFormPanel.setLayout(new GridBagLayout());

        // Skip first documents
        c.gridy = 0;
        c.weightx = 0.3;
        c.gridx = 0;
        skipFirstLabel = new JBLabel("Skip the first # Documents:");
        advancedFormPanel.add(skipFirstLabel, c);
        c.weightx = 0.5;
        c.gridx = 1;

        skipFirstField = new JBTextField();
        advancedFormPanel.add(skipFirstField, c);

        c.weightx = 0.2;
        c.gridx = 2;
        skipFirstCheck = new JBCheckBox();
        advancedFormPanel.add(skipFirstCheck, c);

        // Import up to documents

        c.gridy = 1;
        c.weightx = 0.3;
        c.gridx = 0;
        importUptoLabel = new JBLabel("Import up to # Documents:");
        advancedFormPanel.add(importUptoLabel, c);
        c.weightx = 0.5;
        c.gridx = 1;

        importUptoField = new JBTextField();
        advancedFormPanel.add(importUptoField, c);

        c.weightx = 0.2;
        c.gridx = 2;
        importUptoCheck = new JBCheckBox();
        advancedFormPanel.add(importUptoCheck, c);

        // Ignore fields
        c.gridy = 2;
        c.weightx = 0.3;
        c.gridx = 0;
        ignoreFieldsLabel = new JBLabel("Ignore the fields:");
        advancedFormPanel.add(ignoreFieldsLabel, c);
        c.weightx = 0.5;
        c.gridx = 1;

        ignoreFieldsField = new JBTextField();
        advancedFormPanel.add(ignoreFieldsField, c);

        c.weightx = 0.2;
        c.gridx = 2;
        ignoreFieldsCheck = new JBCheckBox();

        advancedFormPanel.add(ignoreFieldsCheck, c);

        // Threads
        c.gridy = 3;
        c.weightx = 0.3;
        c.gridx = 0;
        threadsLabel = new JBLabel("Threads:");
        advancedFormPanel.add(threadsLabel, c);

        c.weightx = 0.7;
        c.gridx = 1;

        int maxThreads = Runtime.getRuntime().availableProcessors();
        threadsSpinner = new JSpinner(new SpinnerNumberModel(4, 1, maxThreads, 1));
        advancedFormPanel.add(threadsSpinner, c);

        // Verbose log
        c.gridy = 4;
        c.weightx = 0.3;
        c.gridx = 0;
        verboseLabel = new JBLabel("Verbose log:");
        advancedFormPanel.add(verboseLabel, c);
        c.weightx = 0.7;
        c.gridx = 1;

        verboseCheck = new JBCheckBox();
        advancedFormPanel.add(verboseCheck, c);

        advancedPanel.add(advancedFormPanel, BorderLayout.CENTER);

        // Page 5: Summary
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.add(new TitledSeparator("Summary"), BorderLayout.NORTH);

        summaryLabel = new JBLabel();
        summaryPanel.add(summaryLabel, BorderLayout.CENTER);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Add pages to card panel
        cardPanel.add(datasetPanel, "1");
        cardPanel.add(targetPanel, "2");
        cardPanel.add(keyPanel, "3");
        cardPanel.add(advancedPanel, "4");
        cardPanel.add(summaryPanel, "5");

        mainPanel.add(cardPanel, BorderLayout.CENTER);

        updateSummary();
        addListeners();
        return mainPanel;

    }

    private void addListeners() {
        // Add listeners for Page 2 radio buttons
        defaultScopeAndCollectionRadio.addActionListener(e -> updateScopeAndCollectionFields());
        collectionRadio.addActionListener(e -> updateScopeAndCollectionFields());
        dynamicScopeAndCollectionRadio.addActionListener(e -> updateScopeAndCollectionFields());

        // Add listeners for Page 3 radio buttons
        generateUUIDRadio.addActionListener(e -> updateKeyFormFields());
        useFieldValueRadio.addActionListener(e -> {
            updateKeyFormFields();
            updateIgnoreFieldsTextField();
        });
        customExpressionRadio.addActionListener(e -> updateKeyFormFields());

        DocumentAdapter updateSummaryListener = new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                Log.debug("Document changed" + e);
                updateSummary();
            }
        };

        // Add listeners for Page 1 fields
        datasetField.getTextField().getDocument().addDocumentListener(updateSummaryListener);
        // Add listeners for Page 2 fields
        defaultScopeAndCollectionRadio.addActionListener(e -> updateSummary());
        collectionRadio.addActionListener(e -> updateSummary());
        dynamicScopeAndCollectionRadio.addActionListener(e -> updateSummary());

        scopeCombo.addActionListener(e -> updateSummary());
        collectionCombo.addActionListener(e -> updateSummary());

        dynamicScopeFieldField.getDocument().addDocumentListener(updateSummaryListener);
        dynamicCollectionFieldField.getDocument().addDocumentListener(updateSummaryListener);

        // Add listeners for Page 3 fields
        generateUUIDRadio.addActionListener(e -> updateSummary());
        useFieldValueRadio.addActionListener(e -> updateSummary());
        customExpressionRadio.addActionListener(e -> updateSummary());

        fieldNameField.getDocument().addDocumentListener(updateSummaryListener);
        customExpressionField.getDocument().addDocumentListener(updateSummaryListener);

        // Add listeners for Page 4 fields
        skipFirstField.getDocument().addDocumentListener(updateSummaryListener);
        importUptoField.getDocument().addDocumentListener(updateSummaryListener);
        ignoreFieldsField.getDocument().addDocumentListener(updateSummaryListener);

        skipFirstCheck.addActionListener(e -> updateSummary());
        importUptoCheck.addActionListener(e -> updateSummary());
        ignoreFieldsCheck.addActionListener(e -> updateSummary());
        threadsSpinner.addChangeListener(e -> updateSummary());
        verboseCheck.addActionListener(e -> updateSummary());

    }

    protected void updateScopeAndCollectionFields() {
        boolean collectionSelected = collectionRadio.isSelected();

        scopeLabel.setVisible(collectionSelected);
        scopeCombo.setVisible(collectionSelected);
        scopeLabel.setEnabled(collectionSelected);
        scopeCombo.setEnabled(collectionSelected);

        collectionLabel.setVisible(collectionSelected);
        collectionCombo.setVisible(collectionSelected);
        collectionLabel.setEnabled(collectionSelected);
        collectionCombo.setEnabled(collectionSelected);

        boolean dynamicSelected = dynamicScopeAndCollectionRadio.isSelected();

        scopeFieldLabel.setVisible(dynamicSelected);
        dynamicScopeFieldField.setVisible(dynamicSelected);
        scopeFieldLabel.setEnabled(dynamicSelected);
        dynamicScopeFieldField.setEnabled(dynamicSelected);

        collectionFieldLabel.setVisible(dynamicSelected);
        dynamicCollectionFieldField.setVisible(dynamicSelected);
        collectionFieldLabel.setEnabled(dynamicSelected);
        dynamicCollectionFieldField.setEnabled(dynamicSelected);

        try {
            if (defaultScopeAndCollectionRadio.isSelected()) {
                Log.debug("Default scope and collection Radio selected");

                // Set the scope and collection fields to null
                targetScopeField = "_default";
                targetCollectionField = "_default";
            } else if (collectionSelected) {
                Log.debug("collection Radio selected");

                scopeCombo.addActionListener((ActionEvent e) -> {
                    Log.debug("Scope combo box changed"
                            + Optional.ofNullable(scopeCombo.getSelectedItem()).map(Object::toString).orElse("null"));
                    targetScopeField = Optional.ofNullable(scopeCombo.getSelectedItem()).map(Object::toString)
                            .orElse("_default");
                });
                collectionCombo.addActionListener((ActionEvent e) -> {
                    Log.debug("Collection combo box changed" + Optional.ofNullable(collectionCombo.getSelectedItem())
                            .map(Object::toString).orElse("null"));
                    targetCollectionField = Optional.ofNullable(collectionCombo.getSelectedItem()).map(Object::toString)
                            .orElse("_default");
                });

            } else if (dynamicSelected) {
                Log.debug("Dynamic scope and collection Radio selected");
                String[] sampleElementContentSplit = getSampleElementContentSplit(datasetField.getText());

                for (String field : possibleScopeFields) {
                    for (String element : sampleElementContentSplit) {
                        if (element.contains(field)) {
                            dynamicScopeFieldField.setText("%" + field + "%");
                            targetScopeField = element.substring(element.indexOf(":") + 1);
                            break;
                        }
                    }
                }
                for (String field : possibleCollectionFields) {
                    for (String element : sampleElementContentSplit) {
                        if (element.contains(field)) {
                            dynamicCollectionFieldField.setText("%" + field + "%");
                            targetCollectionField = element.substring(element.indexOf(":") + 1);
                            break;
                        }
                    }
                }

                updateIgnoreFieldsTextField();

                // Just in case the scope and collection fields are user-changed
                // then update all 4 fields with the latest values
                dynamicScopeFieldField.getDocument().addDocumentListener(new DocumentAdapter() {
                    @Override
                    protected void textChanged(@NotNull DocumentEvent e) {
                        Log.debug("Scope field changed" + dynamicScopeFieldField.getText());
                        for (String element : sampleElementContentSplit) {
                            if (element.contains(dynamicScopeFieldField.getText())) {
                                targetScopeField = element.substring(element.indexOf(":") + 1);
                                break;
                            }
                        }
                        updateIgnoreFieldsTextField();
                    }
                });

                dynamicCollectionFieldField.getDocument().addDocumentListener(new DocumentAdapter() {
                    @Override
                    protected void textChanged(@NotNull DocumentEvent e) {
                        Log.debug("Collection field changed" + dynamicCollectionFieldField.getText());
                        for (String element : sampleElementContentSplit) {
                            if (element.contains(dynamicCollectionFieldField.getText())) {
                                targetCollectionField = element.substring(element.indexOf(":") + 1);
                                break;
                            }
                        }
                        updateIgnoreFieldsTextField();
                    }
                });
            }
        } catch (Exception e) {
            Log.error("Exception occurred", e);
            e.printStackTrace();
        }

        updateSummary();
    }

    private void updateKeyFormFields() {
        boolean useFieldValueSelected = useFieldValueRadio.isSelected();

        fieldNameLabel.setVisible(useFieldValueSelected);
        fieldNameField.setVisible(useFieldValueSelected);
        fieldNameLabel.setEnabled(useFieldValueSelected);
        fieldNameField.setEnabled(useFieldValueSelected);

        boolean customExpressionSelected = customExpressionRadio.isSelected();

        customExpressionLabel.setVisible(customExpressionSelected);
        customExpressionField.setVisible(customExpressionSelected);
        customExpressionLabel.setEnabled(customExpressionSelected);
        customExpressionField.setEnabled(customExpressionSelected);

        boolean keyPreviewVisible = useFieldValueSelected || customExpressionSelected;

        keyPreviewTitledSeparator.setVisible(keyPreviewVisible);
        keyPreviewTitledSeparator.setEnabled(keyPreviewVisible);
        keyPreviewArea.setVisible(keyPreviewVisible);
        keyPreviewArea.setEnabled(keyPreviewVisible);

        try {
            Log.debug("Updating key form fields");

            if (useFieldValueSelected) {
                String[] sampleElementContentSplit = getSampleElementContentSplit(datasetField.getText());

                for (String field : possibleKeyFields) {
                    for (String element : sampleElementContentSplit) {
                        if (element.contains(field)) {
                            fieldNameField.setText(field);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.error("Exception occurred", e);
            e.printStackTrace();
        }

        updateSummary();
    }

    private void updateKeyPreview() {
        // Clear existing preview content
        keyPreviewArea.setText("");

        try {

            if (useFieldValueRadio.isSelected()) {
                Log.debug("Use field value radio selected");
                // Generate preview based on field name
                String fieldName = fieldNameField.getText();

                // Read file content and parse into a Couchbase JSON array
                String fileContent = Files.readString(Paths.get(datasetField.getText()));
                JsonArray jsonArray = JsonArray.fromJson(fileContent);

                // Generate preview content
                StringBuilder previewContent = new StringBuilder();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jsonObject = jsonArray.getObject(i);
                    if (jsonObject.containsKey(fieldName)) {
                        // previewContent.append("Preview:
                        // ").append(jsonObject.getString(fieldName)).append(".json\n");
                        previewContent.append(jsonObject.getString(fieldName)).append("\n");
                    }
                }

                // Set preview content in keyPreviewArea
                keyPreviewArea.setText(previewContent.toString());

                updateIgnoreFieldsTextField();

            } else if (customExpressionRadio.isSelected()) {
                Log.debug("Custom expression radio selected");

                // Generate preview based on custom expression
                String expression = customExpressionField.getText();

                // Extract field names from custom expression using regular expression
                Pattern pattern = Pattern.compile("%(\\w+)%");
                Matcher matcher = pattern.matcher(expression);
                List<String> fieldNames = new ArrayList<>();
                while (matcher.find()) {
                    fieldNames.add(matcher.group(1));
                }

                // Read file content and parse into a Couchbase JSON array
                String fileContent = Files.readString(Paths.get(datasetField.getText()));
                JsonArray jsonArray = JsonArray.fromJson(fileContent);

                // Generate preview content
                StringBuilder previewContent = new StringBuilder();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jsonObject = jsonArray.getObject(i);

                    // Replace field placeholders in custom expression with actual values
                    String key = expression;
                    for (String fieldName : fieldNames) {
                        if (jsonObject.containsKey(fieldName)) {
                            key = key.replace("%" + fieldName + "%", jsonObject.getString(fieldName));
                        }
                    }

                    // previewContent.append("Preview: ").append(key).append(".json\n");
                    previewContent.append(key).append("\n");
                }

                // Set preview content in keyPreviewArea
                keyPreviewArea.setText(previewContent.toString());

            }

        } catch (Exception e) {
            Log.error("Exception occurred", e);
            e.printStackTrace();
        }
    }

    private void updateSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("<html>");

        // Page 1: Dataset
        summary.append("<b>Dataset:</b> ");
        summary.append(datasetField.getText());
        summary.append("<br><br>");

        // Page 2: Bucket and target location
        summary.append("<b>Bucket:</b> ");
        summary.append(bucketCombo.getSelectedItem());
        summary.append("<br><br>");

        summary.append("<b>Scope and Collection:</b> ");
        if (defaultScopeAndCollectionRadio.isSelected()) {
            summary.append("Default Scope and Collection");
        } else if (collectionRadio.isSelected()) {
            summary.append("Collection - Scope: ");
            summary.append(scopeCombo.getSelectedItem());
            summary.append(", Collection: ");
            summary.append(collectionCombo.getSelectedItem());
        } else if (dynamicScopeAndCollectionRadio.isSelected()) {
            summary.append("Dynamic Scope and Collection - Scope Field: ");
            summary.append(dynamicScopeFieldField.getText());
            summary.append(", Collection Field: ");
            summary.append(dynamicCollectionFieldField.getText());
        }
        summary.append("<br><br>");

        // Page 3: Document key
        summary.append("<b>Document Key:</b> ");
        if (generateUUIDRadio.isSelected()) {
            summary.append("Generate random UUID for each document");
        } else if (useFieldValueRadio.isSelected()) {
            summary.append("Use the value of a field as the key - Field Name: ");
            summary.append(fieldNameField.getText());
        } else if (customExpressionRadio.isSelected()) {
            summary.append("Generate key based on custom expression - Expression: ");
            summary.append(customExpressionField.getText());
        }
        summary.append("<br><br>");

        // Page 4: Advanced options
        summary.append("<b>Advanced Options:</b><br>");
        if (skipFirstCheck.isSelected()) {
            summary.append("- Skip the first ");
            summary.append(skipFirstField.getText());
            summary.append(" documents<br>");
        }
        if (importUptoCheck.isSelected()) {
            summary.append("- Import up to ");
            summary.append(importUptoField.getText());
            summary.append(" documents<br>");
        }
        if (ignoreFieldsCheck.isSelected()) {
            summary.append("- Ignore the fields: ");
            summary.append(ignoreFieldsField.getText());
            summary.append("<br>");
        }
        summary.append("- Threads: ");
        // Use getValue() method of JSpinner to get its value
        summary.append(threadsSpinner.getValue());
        summary.append("<br>");
        if (verboseCheck.isSelected()) {
            summary.append("- Verbose Log<br>");
        }

        // Add additional line break at the end
        summary.append("<br>");

        summaryLabel.setText(summary.toString());
    }

    protected void updateIgnoreFieldsTextField() {
        String ignoreFieldsText = "";

        if (dynamicScopeAndCollectionRadio.isSelected()) {
            String fieldNameText = fieldNameField.getText();
            List<String> wordsWithPercentSymbols = new ArrayList<>();

            // Extract words with percent symbols from scope and add them to the list
            String scopeText = dynamicScopeFieldField.getText();
            if (!scopeText.isEmpty()) {
                wordsWithPercentSymbols.addAll(extractWordsWithPercentSymbols(scopeText));
            }

            // Extract words with percent symbols from collection and add them to the list
            String collectionText = dynamicCollectionFieldField.getText();
            if (!collectionText.isEmpty()) {
                wordsWithPercentSymbols.addAll(extractWordsWithPercentSymbols(collectionText));
            }

            // Combine all the words into a comma-separated string
            ignoreFieldsText = fieldNameText + "," + String.join(",", wordsWithPercentSymbols);
        } else if (collectionRadio.isSelected()) {
            // If collectionRadio is selected, just use the fieldNameText
            ignoreFieldsText = fieldNameField.getText();
        }

        // Remove any leading or trailing commas
        ignoreFieldsText = ignoreFieldsText.replaceAll("^,+|,+$", "");

        // Set the text in ignoreFieldsField
        ignoreFieldsField.setText(ignoreFieldsText);
    }

    private List<String> extractWordsWithPercentSymbols(String text) {
        List<String> wordsWithPercentSymbols = new ArrayList<>();
        Matcher matcher = Pattern.compile("%(\\w+)%").matcher(text);
        while (matcher.find()) {
            wordsWithPercentSymbols.add(matcher.group(1));
        }
        return wordsWithPercentSymbols;
    }

    protected String[] getSampleElementContentSplit(String datasetFieldText) throws IOException {
        String sampleElementContent = FileUtils.sampleElementFromJsonArrayFile(datasetFieldText);
        System.out.println("Sample element content in updating key form fields " + sampleElementContent);
        assert sampleElementContent != null;
        return sampleElementContent.split(",");
    }

    @Override
    protected Action @NotNull [] createActions() {
        cancelAction = getCancelAction();
        backAction = new DialogWrapperAction("Back") {
            @Override
            protected void doAction(ActionEvent e) {
                previousPage();
            }
        };
        nextAction = new DialogWrapperAction("Next") {
            @Override
            protected void doAction(ActionEvent e) {
                nextPage();
            }
        };

        backAction.setEnabled(false);
        nextAction.setEnabled(false);

        return new Action[] { cancelAction, backAction, nextAction };

    }

    protected void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            cardLayout.show(cardPanel, Integer.toString(currentPage));
            nextAction.putValue(Action.NAME, currentPage == 5 ? "Import" : "Next");
        }
        backAction.setEnabled(currentPage != 1);
    }

    protected void nextPage() {
        if (currentPage < 5) {
            currentPage++;
            cardLayout.show(cardPanel, Integer.toString(currentPage));
            nextAction.putValue(Action.NAME, currentPage == 5 ? "Import" : "Next");
        } else {
            doOKAction();
        }
        backAction.setEnabled(currentPage != 1);
    }

    protected void setNextButtonOnDatasetSelection() {
        nextAction.setEnabled(!datasetField.getText().isEmpty());
    }

    @Override
    protected void doOKAction() {
        try {
            complexBucketImport(
                    Objects.requireNonNull(bucketCombo.getSelectedItem()).toString(),
                    datasetField.getText(),
                    project,
                    datasetField.getText().endsWith(".json") ? "json" : "csv");
            super.doOKAction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void complexBucketImport(String bucket, String filePath, Project project, String fileFormat) {
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Importing data", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);
                indicator.setText("Importing data");
                indicator.setText2("Importing data from " + filePath + " to bucket " + bucket);

                try {
                    // Create process builder for CB_IMPORT tool
                    ProcessBuilder processBuilder = new ProcessBuilder(
                            CBTools.getTool(CBTools.Type.CB_IMPORT).getPath(),
                            fileFormat,
                            "--no-ssl-verify",
                            "-c", ActiveCluster.getInstance().getClusterURL(),
                            "-u", ActiveCluster.getInstance().getUsername(),
                            "-p", ActiveCluster.getInstance().getPassword(),
                            "-b", bucket,
                            "-d", "file://" + filePath);

                    // Add additional options for CSV format
                    if (fileFormat.equals("csv")) {
                        processBuilder.command().add("--field-separator");
                        processBuilder.command().add(",");
                        processBuilder.command().add("--infer-types");
                    }

                    // Check the first character of the file content and add the appropriate
                    // --format option
                    try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
                        int firstChar = reader.read();
                        if (firstChar == '{') {
                            processBuilder.command().add("--format");
                            processBuilder.command().add("lines");
                        } else if (firstChar == '[') {
                            processBuilder.command().add("--format");
                            processBuilder.command().add("list");
                        }
                    }

                    // Add scope and collection options based on selected target location
                    if (targetLocationGroup.getSelection() == defaultScopeAndCollectionRadio.getModel()) {
                        // Import data into default scope and collection
                        processBuilder.command().add("--scope-collection-exp");
                        processBuilder.command().add("_default._default");
                    } else if (targetLocationGroup.getSelection() == collectionRadio.getModel()) {
                        // Import data into selected scope and collection
                        processBuilder.command().add("--scope-collection-exp");
                        processBuilder.command().add(targetScopeField + "." + targetCollectionField);
                    } else if (targetLocationGroup.getSelection() == dynamicScopeAndCollectionRadio.getModel()) {
                        // Import data into dynamic scope and collection
                        processBuilder.command().add("--scope-collection-exp");
                        processBuilder.command().add(dynamicScopeFieldField.getText() + "."
                                + dynamicCollectionFieldField.getText());
                    }

                    // Add document key options based on selected key option
                    if (keyGroup.getSelection() == generateUUIDRadio.getModel()) {
                        // Generate random UUID for each document
                        processBuilder.command().add("-g");
                        processBuilder.command().add("#UUID#");
                    } else if (keyGroup.getSelection() == useFieldValueRadio.getModel()) {
                        // Use the value of a field as the key
                        processBuilder.command().add("-g");
                        processBuilder.command().add("%" + fieldNameField.getText() + "%");
                    } else if (keyGroup.getSelection() == customExpressionRadio.getModel()) {
                        // Generate key based on custom expression
                        processBuilder.command().add("-g");
                        processBuilder.command().add(customExpressionField.getText());
                    }

                    // Add advanced options
                    if (skipFirstCheck.isSelected()) {
                        processBuilder.command().add("--skip-rows");
                        processBuilder.command().add(skipFirstField.getText());
                    }

                    if (importUptoCheck.isSelected()) {
                        if (fileFormat.equals("json")) {
                            processBuilder.command().add("--limit-docs");
                        } else if (fileFormat.equals("csv")) {
                            processBuilder.command().add("--limit-rows");
                        }
                        processBuilder.command().add(importUptoField.getText());
                    }

                    if (ignoreFieldsCheck.isSelected()) {
                        processBuilder.command().add("--ignore-fields");
                        processBuilder.command().add(ignoreFieldsField.getText());
                    }

                    processBuilder.command().add("-t");
                    processBuilder.command().add(threadsSpinner.getValue().toString());

                    if (verboseCheck.isSelected()) {
                        processBuilder.command().add("-v ");
                    }

                    Log.debug("Command: " + processBuilder.command());
                    System.out.println("Command: " + processBuilder.command());

                    // Check if indexes should be created and create them if necessary
                    boolean createIndexes = shouldCreateIndexes();
                    if (createIndexes) {
                        createIndexes(bucket, targetScopeField, targetCollectionField);
                    }

                    // Execute CB_IMPORT tool using process builder
                    Process process = processBuilder.start();
                    int exitCode = process.waitFor();

                    if (exitCode == 0) {
                        Log.info("Data imported successfully");
                        ApplicationManager.getApplication().invokeLater(
                                () -> Messages.showInfoMessage("Data imported successfully", "Import Complete"));
                    } else {
                        // Read the error stream of the process to get more information about the error
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                        StringBuilder errorMessage = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            errorMessage.append(line).append("\n");
                        }
                        reader.close();

                        Log.error("An error occurred while trying to import the data: " + errorMessage);
                        ApplicationManager.getApplication().invokeLater(() -> Messages.showErrorDialog(
                                "An error occurred while trying to import the data:\n" + errorMessage,
                                "Import Error"));
                    }
                } catch (Exception e) {
                    Log.error("Exception occurred", e);
                    ApplicationManager.getApplication().invokeLater(() -> Messages
                            .showErrorDialog("An error occurred while trying to import the data", "Import Error"));
                }
            }
        });
    }

    protected boolean shouldCreateIndexes() {
        final boolean[] result = new boolean[1];
        ApplicationManager.getApplication().invokeLater(() -> result[0] = Messages.showYesNoDialog(
                "<html>This dataset contains indexes. Would you like to also create them?"
                        + "<br><small>If the indexes already exist in your environment, they won't be recreated.</small></html>",
                "CB Import", Messages.getQuestionIcon()) == Messages.YES);
        return result[0];
    }

    protected void createIndexes(String bucket, String scope, String collection) {
        try {
            // Read file content and parse into a Couchbase JSON array
            String fileContent = Files.readString(Paths.get(datasetField.getText()));
            JsonArray jsonArray = JsonArray.fromJson(fileContent);

            // Iterate over each element in the JSON array
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonObject = jsonArray.getObject(i);

                // Check if the element contains an index definition
                if (jsonObject.containsKey("indexDef")) {
                    JsonObject indexDef = jsonObject.getObject("indexDef");

                    // Extract index name and fields from index definition
                    String indexName = indexDef.getString("name");
                    JsonArray indexFields = indexDef.getArray("fields");

                    // Build CREATE INDEX statement
                    StringBuilder createIndexStatement = new StringBuilder();
                    createIndexStatement.append("CREATE INDEX ");
                    createIndexStatement.append(indexName);
                    createIndexStatement.append(" ON ");
                    createIndexStatement.append(bucket);
                    createIndexStatement.append(".");
                    createIndexStatement.append(scope);
                    createIndexStatement.append(".");
                    createIndexStatement.append(collection);
                    createIndexStatement.append("(");

                    for (int j = 0; j < indexFields.size(); j++) {
                        if (j > 0) {
                            createIndexStatement.append(", ");
                        }
                        createIndexStatement.append(indexFields.getString(j));
                    }

                    createIndexStatement.append(")");

                    // Execute CREATE INDEX statement
                    ActiveCluster.getInstance().get().query(createIndexStatement.toString());
                }
            }
        } catch (Exception e) {
            Log.error("Exception occurred", e);
            ApplicationManager.getApplication().invokeLater(() -> Messages
                    .showErrorDialog("An error occurred while trying to create indexes", "Create Indexes Error"));
        }
    }

}
