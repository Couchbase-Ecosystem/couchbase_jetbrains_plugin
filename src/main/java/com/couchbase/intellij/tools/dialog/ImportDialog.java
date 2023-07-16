
package com.couchbase.intellij.tools.dialog;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;

import com.couchbase.client.java.manager.collection.CollectionSpec;
import org.jetbrains.annotations.NotNull;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tools.CBTools;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
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

    private Project project;

    // Declare UI components here
    private TextFieldWithBrowseButton datasetField;

    private JComboBox<String> bucketCombo;
    private JComboBox<String> scopeCombo;
    private JComboBox<String> collectionCombo;

    private JBRadioButton defaultScopeAndCollectionRadio;
    private JBRadioButton collectionRadio;
    private JBRadioButton dynamicScopeAndCollectionRadio;

    private JBTextField scopeFieldField;
    private JBTextField collectionFieldField;

    private JBRadioButton generateUUIDRadio;
    private JBRadioButton useFieldValueRadio;
    private JBRadioButton customExpressionRadio;

    private JBTextField fieldNameField;
    private JBTextField expressionField;

    private JBTextField skipFirstField;
    private JBCheckBox skipFirstCheck;

    private JBTextField importUptoField;
    private JBCheckBox importUptoCheck;

    private JBTextField ignoreFieldsField;
    private JBCheckBox ignoreFieldsCheck;

    private JSpinner threadsSpinner;

    private JBCheckBox verboseCheck;

    private ButtonGroup targetLocationgroup;
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
    private JBLabel expressionLabel;
    private JBLabel skipFirstLabel;
    private JBLabel importUptoLabel;
    private JBLabel ignoreFieldsLabel;
    private JBLabel threadsLabel;
    private JBLabel verboseLabel;

    // Declare label for summary
    private JBLabel summaryLabel;

    // Declare actions for back and next buttons
    private Action backAction;
    private Action nextAction;
    private Action cancelAction;

    private int currentPage = 1;

    private String[] possibleScopeFields = { "cbms", "scope", "cbs" };
    private String[] possibleCollectionFields = { "cbmc", "collection", "cbc" };
    private String[] possibleKeyFields = { "cbmk", "cbmid", "key", "cbk" };

    private String targetScopeField;
    private String targetCollectionField;

    public ImportDialog(Project project) {
        super(true);
        this.project = project;
        init();
        setTitle("Import Data");
        getWindow().setMinimumSize(new Dimension(600, 380));
        setResizable(true);
        setOKButtonText("Import");
    }

    @Override
    protected JComponent createCenterPanel() {

        // Create and add UI components for each page here
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Page 1: Select dataset
        JPanel datasetPanel = new JPanel(new BorderLayout());
        datasetPanel.add(new TitledSeparator("Select the Dataset"), BorderLayout.NORTH);

        JPanel datasetFormPanel = new JPanel();
        datasetFormPanel.setBorder(JBUI.Borders.empty(0, 10));
        datasetFormPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridy = 0;
        c.weightx = 0.3;
        c.gridx = 0;
        datasetFormPanel.add(new JBLabel("Dataset:"), c);
        c.weightx = 0.7;
        c.gridx = 1;

        datasetField = new TextFieldWithBrowseButton();
        datasetField.addBrowseFolderListener("Select the Dataset", "", null,
                FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor());
        // Add a listener for when the datasetField is changed
        datasetField.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                Log.debug("Dataset field changed" + datasetField.getText());
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

        bucketCombo = new JComboBox<>(buckets);
        targetFormPanel.add(bucketCombo, c);

        // Radio buttons for scope and collection options
        c.gridy = 1;
        c.weightx = 0.3;
        c.gridx = 0;
        targetFormPanel.add(new JBLabel("Scope and Collection:"), c);
        c.weightx = 0.7;
        c.gridx = 1;

        defaultScopeAndCollectionRadio = new JBRadioButton("Default Scope and Collection");
        collectionRadio = new JBRadioButton("Collection");
        dynamicScopeAndCollectionRadio = new JBRadioButton("Dynamic Scope and Collection");

        targetLocationgroup = new ButtonGroup();
        targetLocationgroup.add(defaultScopeAndCollectionRadio);
        targetLocationgroup.add(collectionRadio);
        targetLocationgroup.add(dynamicScopeAndCollectionRadio);

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
        scopeCombo = new JComboBox<>();
        ActionListener bucketComboListener = (ActionEvent e) -> {
            Log.debug("Bucket combo box changed");

            // Get all scopes for selected bucket
            scopes.clear();
            scopes.addAll(ActiveCluster.getInstance().get().bucket(bucketCombo.getSelectedItem().toString())
                    .collections()
                    .getAllScopes());

            // Update scope combo box items
            Set<String> scopeSet = scopes.stream()
                    .map(scope -> scope.name())
                    .collect(Collectors.toSet());
            String[] scopeItems = scopeSet.toArray(new String[0]);
            scopeCombo.removeAllItems();
            for (String scopeItem : scopeItems) {
                scopeCombo.addItem(scopeItem);
            }

            // Trigger action listener for scope combo box to update collection combo box
            // items
            scopeCombo.actionPerformed(new ActionEvent(scopeCombo, ActionEvent.ACTION_PERFORMED, null));
        };
        bucketCombo.addActionListener(bucketComboListener);

        // Trigger action listener for bucket combo box to update scope and collection
        // combo box items
        bucketComboListener.actionPerformed(new ActionEvent(bucketCombo, ActionEvent.ACTION_PERFORMED, null));

        targetFormPanel.add(scopeCombo, c);

        c.gridy = 3;
        c.weightx = 0.3;
        c.gridx = 0;
        collectionLabel = new JBLabel("Collection:");
        targetFormPanel.add(collectionLabel, c);
        c.weightx = 0.7;
        c.gridx = 1;

        collectionCombo = new JComboBox<>();
        // add a listener for when the scopeCombo is changed
        scopeCombo.addActionListener((ActionEvent e) -> {
            Log.debug("Scope combo box changed");

            // Set of collections from the selected scope

            String[] collectionItems = scopes.stream()
                    .filter(scope -> scope.name().equals(scopeCombo.getSelectedItem()))
                    .flatMap(scope -> scope.collections().stream())
                    .map(CollectionSpec::name).distinct().toArray(String[]::new);
            collectionCombo.removeAllItems();
            for (String collectionItem : collectionItems) {
                collectionCombo.addItem(collectionItem);
            }
        });
        targetFormPanel.add(collectionCombo, c);

        // Scope and collection fields
        c.gridy = 4;
        c.weightx = 0.3;
        c.gridx = 0;
        scopeFieldLabel = new JBLabel("Scope Field:");
        targetFormPanel.add(scopeFieldLabel, c);

        c.weightx = 0.7;
        c.gridx = 1;

        scopeFieldField = new JBTextField();
        targetFormPanel.add(scopeFieldField, c);

        c.gridy = 5;
        c.weightx = 0.3;
        c.gridx = 0;
        collectionFieldLabel = new JBLabel("Collection Field:");
        targetFormPanel.add(collectionFieldLabel, c);
        c.weightx = 0.7;
        c.gridx = 1;

        collectionFieldField = new JBTextField();
        targetFormPanel.add(collectionFieldField, c);

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
        scopeFieldField.setVisible(false);
        collectionFieldField.setVisible(false);

        scopeCombo.setEnabled(false);
        collectionCombo.setEnabled(false);
        scopeFieldField.setEnabled(false);
        collectionFieldField.setEnabled(false);

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
        keyLabel = new JBLabel("Key Options:");
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
        fieldNameLabel = new JBLabel("Field Name:");
        keyFormPanel.add(fieldNameLabel, c);
        c.weightx = 0.7;
        c.gridx = 1;

        fieldNameField = new JBTextField();
        keyFormPanel.add(fieldNameField, c);

        // Expression field
        c.gridy = 2;
        c.weightx = 0.3;
        c.gridx = 0;
        expressionLabel = new JBLabel("Expression:");
        keyFormPanel.add(expressionLabel, c);

        c.weightx = 0.7;
        c.gridx = 1;

        expressionField = new JBTextField();
        keyFormPanel.add(expressionField, c);

        // In addListeners method, add listeners for relevant fields:
        fieldNameField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                Log.debug("Field name field changed");
                updateKeyPreview();
            }
        });

        expressionField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                Log.debug("Expression field changed");
                updateKeyPreview();
            }
        });

        keyPanel.add(keyFormPanel, BorderLayout.CENTER);

        keyPreviewArea = new JTextArea();
        keyPreviewArea.setEditable(false);
        keyPreviewArea.setLineWrap(true);
        keyPreviewArea.setWrapStyleWord(true);
        keyPreviewArea.setMinimumSize(new Dimension(150, 100));
        keyPreviewArea.setPreferredSize(new Dimension(150, 100));
        keyPanel.add(keyPreviewArea, BorderLayout.SOUTH);

        // Set all labels to invisible and disabled by default
        fieldNameLabel.setVisible(false);
        expressionLabel.setVisible(false);
        fieldNameLabel.setEnabled(false);
        expressionLabel.setEnabled(false);

        // Set all fields to invisible and disabled by default
        fieldNameField.setVisible(false);
        expressionField.setVisible(false);
        fieldNameField.setEnabled(false);
        expressionField.setEnabled(false);

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

        // Replace threadsField with threadsSpinner
        threadsSpinner = new JSpinner(new SpinnerNumberModel(4, 1, null, 1));
        advancedFormPanel.add(threadsSpinner, c);

        // Verbose log
        c.gridy = 4;
        c.weightx = 0.3;
        c.gridx = 0;
        verboseLabel = new JBLabel("Verbose Log:");
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
        useFieldValueRadio.addActionListener(e -> updateKeyFormFields());
        customExpressionRadio.addActionListener(e -> updateKeyFormFields());

        DocumentAdapter updateSummaryListener = new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                Log.debug("Document changed");
                updateSummary();
            }
        };

        // Add listeners for Page 1 fields
        // Add listener for datasetField
        datasetField.getTextField().getDocument().addDocumentListener(updateSummaryListener);
        // Add listeners for Page 2 fields
        defaultScopeAndCollectionRadio.addActionListener(e -> updateSummary());
        collectionRadio.addActionListener(e -> updateSummary());
        dynamicScopeAndCollectionRadio.addActionListener(e -> updateSummary());

        scopeCombo.addActionListener(e -> updateSummary());
        collectionCombo.addActionListener(e -> updateSummary());

        scopeFieldField.getDocument().addDocumentListener(updateSummaryListener);
        collectionFieldField.getDocument().addDocumentListener(updateSummaryListener);

        // Add listeners for Page 3 fields
        generateUUIDRadio.addActionListener(e -> updateSummary());
        useFieldValueRadio.addActionListener(e -> updateSummary());
        customExpressionRadio.addActionListener(e -> updateSummary());

        fieldNameField.getDocument().addDocumentListener(updateSummaryListener);
        expressionField.getDocument().addDocumentListener(updateSummaryListener);

        // Add listeners for Page 4 fields
        skipFirstField.getDocument().addDocumentListener(updateSummaryListener);
        importUptoField.getDocument().addDocumentListener(updateSummaryListener);
        ignoreFieldsField.getDocument().addDocumentListener(updateSummaryListener);

        skipFirstCheck.addActionListener(e -> updateSummary());
        importUptoCheck.addActionListener(e -> updateSummary());
        ignoreFieldsCheck.addActionListener(e -> updateSummary());

        // Replace threadsField with threadsSpinner
        // threadsField.getDocument().addDocumentListener(updateSummaryListener);
        threadsSpinner.addChangeListener(e -> updateSummary());

        verboseCheck.addActionListener(e -> updateSummary());

    }

    private void updateScopeAndCollectionFields() {
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
        scopeFieldField.setVisible(dynamicSelected);
        scopeFieldLabel.setEnabled(dynamicSelected);
        scopeFieldField.setEnabled(dynamicSelected);

        collectionFieldLabel.setVisible(dynamicSelected);
        collectionFieldField.setVisible(dynamicSelected);
        collectionFieldLabel.setEnabled(dynamicSelected);
        collectionFieldField.setEnabled(dynamicSelected);

        try {
            if (defaultScopeAndCollectionRadio.isSelected()) {
                Log.debug("Default scope and collection selected");

                // Set the scope and collection fields to null
                targetScopeField = "_default";
                targetCollectionField = "_default";
            } else if (collectionSelected) {
                Log.debug("Collection selected");

                scopeCombo.addActionListener((ActionEvent e) -> {
                    Log.debug("Scope combo box changed");
                    targetScopeField = scopeCombo.getSelectedItem() != null ? scopeCombo.getSelectedItem().toString()
                            : "_default";
                });
                collectionCombo.addActionListener((ActionEvent e) -> {
                    Log.debug("Collection combo box changed");
                    targetCollectionField = collectionCombo.getSelectedItem() != null
                            ? collectionCombo.getSelectedItem().toString()
                            : "_default";
                });

            } else if (dynamicSelected && datasetField.getText() != null) {
                Log.debug("Dataset field not null");

                // Parse the first element of the dataset to get the field names
                String sampleElementContent = FileUtils.sampleElementFromJsonArrayFile(datasetField.getText());
                String[] sampleElementContentSplit = sampleElementContent.split(",");

                for (String field : possibleScopeFields) {
                    for (String element : sampleElementContentSplit) {
                        if (element.contains(field)) {
                            scopeFieldField.setText(field);
                            targetScopeField = element.substring(element.indexOf(":") + 1);
                            break;
                        }
                    }
                }
                for (String field : possibleCollectionFields) {
                    for (String element : sampleElementContentSplit) {
                        if (element.contains(field)) {
                            collectionFieldField.setText(field);
                            targetCollectionField = element.substring(element.indexOf(":") + 1);
                            break;
                        }
                    }
                }

                ignoreFieldsField.setText((scopeFieldField.getText() + "," + collectionFieldField.getText() + ","
                        + fieldNameField.getText()).replaceAll("^,*|,*$", ""));

                // Just in case the scope and collection fields are user-changed
                // then update all 4 fields with the latest values
                scopeFieldField.getDocument().addDocumentListener(new DocumentAdapter() {
                    @Override
                    protected void textChanged(DocumentEvent e) {
                        Log.debug("Scope field changed");
                        for (String element : sampleElementContentSplit) {
                            if (element.contains(scopeFieldField.getText())) {
                                targetScopeField = element.substring(element.indexOf(":") + 1);
                                break;
                            }
                        }

                        ignoreFieldsField
                                .setText((scopeFieldField.getText() + "," + collectionFieldField.getText() + ","
                                        + fieldNameField.getText()).replaceAll("^,*|,*$", ""));
                    }
                });

                collectionFieldField.getDocument().addDocumentListener(new DocumentAdapter() {
                    @Override
                    protected void textChanged(DocumentEvent e) {
                        Log.debug("Collection field changed");
                        for (String element : sampleElementContentSplit) {
                            if (element.contains(collectionFieldField.getText())) {
                                targetCollectionField = element.substring(element.indexOf(":") + 1);
                                break;
                            }
                        }
                        ignoreFieldsField
                                .setText((scopeFieldField.getText() + "," + collectionFieldField.getText() + ","
                                        + fieldNameField.getText()).replaceAll("^,*|,*$", ""));
                    }
                });
            }
        } catch (Exception e) {
            Log.debug("Exception occurred" + e.getMessage());
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

        expressionLabel.setVisible(customExpressionSelected);
        expressionField.setVisible(customExpressionSelected);
        expressionLabel.setEnabled(customExpressionSelected);
        expressionField.setEnabled(customExpressionSelected);

        boolean keyPreviewVisible = useFieldValueSelected || customExpressionSelected;

        // Replace keyPreviewPanel with keyPreviewArea
        // keyPreviewPanel.setVisible(keyPreviewVisible);
        // keyPreviewPanel.setEnabled(keyPreviewVisible);
        keyPreviewArea.setVisible(keyPreviewVisible);
        keyPreviewArea.setEnabled(keyPreviewVisible);

        try {
            Log.debug("Updating key form fields");

            if (useFieldValueSelected && datasetField.getText() != null) {
                // Parse the first element of the dataset to get the field names
                String sampleElementContent = FileUtils.sampleElementFromJsonArrayFile(datasetField.getText());
                String[] sampleElementContentSplit = sampleElementContent.split(",");

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
            Log.debug("Exception occurred" + e.getMessage());
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
                        previewContent.append("Preview: ").append(jsonObject.getString(fieldName)).append(".json\n");
                    }
                }

                // Set preview content in keyPreviewArea
                keyPreviewArea.setText(previewContent.toString());

                ignoreFieldsField.setText((scopeFieldField.getText() + "," + collectionFieldField.getText() + ","
                        + fieldNameField.getText()).replaceAll("^,*|,*$", ""));

            } else if (customExpressionRadio.isSelected()) {
                Log.debug("Custom expression radio selected");

                // Generate preview based on custom expression
                String expression = expressionField.getText();

                // Extract field names from custom expression using regular expression
                Pattern pattern = Pattern.compile("%(\\w+)-value%");
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
                            key = key.replace("%" + fieldName + "-value%", jsonObject.getString(fieldName));
                        }
                    }

                    previewContent.append("Preview: ").append(key).append(".json\n");
                }

                // Set preview content in keyPreviewArea
                keyPreviewArea.setText(previewContent.toString());

            }

        } catch (Exception e) {
            Log.debug("Exception occurred" + e.getMessage());
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
            summary.append(scopeFieldField.getText());
            summary.append(", Collection Field: ");
            summary.append(collectionFieldField.getText());
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
            summary.append(expressionField.getText());
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
        nextAction.setEnabled(datasetField.getText() != null && !datasetField.getText().isEmpty());
    }

    @Override
    protected void doOKAction() {
        try {
            complexBucketImport(bucketCombo.getSelectedItem().toString(), datasetField.getText(), project);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void complexBucketImport(String bucket, String filePath, Project project) {
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
                            "json",
                            "--no-ssl-verify",
                            "-c", ActiveCluster.getInstance().getClusterURL(),
                            "-u", ActiveCluster.getInstance().getUsername(),
                            "-p", ActiveCluster.getInstance().getPassword(),
                            "-b", bucket,
                            "--format", "list",
                            "-d", "file://" + filePath);

                    // Add scope and collection options based on selected target location
                    if (targetLocationgroup.getSelection() == defaultScopeAndCollectionRadio.getModel()) {
                        // Import data into default scope and collection
                        processBuilder.command().add("--scope-collection-exp");
                        processBuilder.command().add("_default._default");
                    } else if (targetLocationgroup.getSelection() == collectionRadio.getModel()) {
                        // Import data into selected scope and collection
                        processBuilder.command().add("--scope-collection-exp");
                        processBuilder.command().add(targetScopeField + "." + targetCollectionField);
                    } else if (targetLocationgroup.getSelection() == dynamicScopeAndCollectionRadio.getModel()) {
                        // Import data into dynamic scope and collection
                        // TODO: Add options for dynamic scope and collection
                    }

                    // Add document key options based on selected key option
                    if (keyGroup.getSelection() == generateUUIDRadio.getModel()) {
                        // Generate random UUID for each document
                        processBuilder.command().add("-g");
                        processBuilder.command().add("%uuid%");
                    } else if (keyGroup.getSelection() == useFieldValueRadio.getModel()) {
                        // Use the value of a field as the key
                        processBuilder.command().add("-g");
                        processBuilder.command().add("%" + fieldNameField.getText() + "%");
                    } else if (keyGroup.getSelection() == customExpressionRadio.getModel()) {
                        // Generate key based on custom expression
                        processBuilder.command().add("-g");
                        processBuilder.command().add(expressionField.getText());
                    }

                    // Add advanced options
                    if (skipFirstCheck.isSelected()) {
                        processBuilder.command().add("--skip-first");
                        processBuilder.command().add(skipFirstField.getText());
                    }
                    if (importUptoCheck.isSelected()) {
                        processBuilder.command().add("--limit-rows");
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
                        ApplicationManager.getApplication().invokeLater(() -> {
                            Messages.showInfoMessage("Data imported successfully", "Import Complete");
                        });
                    } else {
                        ApplicationManager.getApplication().invokeLater(() -> {
                            Messages.showErrorDialog("An error occurred while trying to import the data",
                                    "Import Error");
                        });
                    }
                } catch (Exception e) {
                    Log.error("Exception occurred" + e.getMessage());
                    ApplicationManager.getApplication().invokeLater(() -> {
                        Messages.showErrorDialog("An error occurred while trying to import the data", "Import Error");
                    });
                }
            }
        });
    }

    protected boolean shouldCreateIndexes() {
        int result = Messages.NO;
        try {
            result = Messages.showYesNoDialog(
                    "<html>This dataset contains indexes. Would you like to also create them?"
                            + "<br><small>If the indexes already exist in your environment, they won't be recreated.</small></html>",
                    "Simple Import", Messages.getQuestionIcon());

        } catch (Exception e) {
            Log.error("Exception occurred" + e.getMessage());
            ApplicationManager.getApplication().invokeLater(() -> {
                Messages.showErrorDialog("An error occurred while trying to create indexes", "Create Indexes Error");
            });
            e.printStackTrace();
        }
        return result == Messages.YES;
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
            Log.error("Exception occurred" + e.getMessage());
            ApplicationManager.getApplication().invokeLater(() -> {
                Messages.showErrorDialog("An error occurred while trying to create indexes", "Create Indexes Error");
            });
        }
    }

}
