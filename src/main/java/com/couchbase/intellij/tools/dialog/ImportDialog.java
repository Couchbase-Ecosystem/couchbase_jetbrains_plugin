
package com.couchbase.intellij.tools.dialog;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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

import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tree.NewEntityCreationDialog;
import com.couchbase.intellij.types.EntityType;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
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

public class ImportDialog extends DialogWrapper {

    protected final Project project;

    protected TextFieldWithBrowseButton datasetField;

    protected ComboBox<String> bucketCombo;
    protected ComboBox<String> scopeCombo;
    protected ComboBox<String> collectionCombo;

    protected JBRadioButton defaultScopeAndCollectionRadio;
    protected JBRadioButton collectionRadio;
    protected JBRadioButton dynamicScopeAndCollectionRadio;

    protected JBTextField dynamicScopeFieldField;
    protected JBTextField dynamicCollectionFieldField;

    protected JBRadioButton generateUUIDRadio;
    protected JBRadioButton useFieldValueRadio;
    protected JBRadioButton customExpressionRadio;

    protected JBTextField fieldNameField;
    protected JBTextField customExpressionField;

    protected JBTextField skipFirstField;
    protected JBCheckBox skipFirstCheck;

    protected JBTextField importUptoField;
    protected JBCheckBox importUptoCheck;

    protected JBTextField ignoreFieldsField;
    protected JBCheckBox ignoreFieldsCheck;

    protected JSpinner threadsSpinner;

    protected JBCheckBox verboseCheck;

    protected ButtonGroup targetLocationGroup;
    protected ButtonGroup keyGroup;

    protected CardLayout cardLayout;
    protected JPanel cardPanel;
    protected JTextArea keyPreviewArea;

    protected JBLabel scopeLabel;
    protected JBLabel collectionLabel;
    protected JBLabel scopeFieldLabel;
    protected JBLabel collectionFieldLabel;
    protected JBLabel keyLabel;
    protected JBLabel fieldNameLabel;
    protected JBLabel customExpressionLabel;
    protected JBLabel skipFirstLabel;
    protected JBLabel importUptoLabel;
    protected JBLabel ignoreFieldsLabel;
    protected JBLabel threadsLabel;
    protected JBLabel verboseLabel;

    protected JBLabel summaryLabel;

    protected TitledSeparator keyPreviewTitledSeparator;

    protected Action backAction;
    protected Action nextAction;
    protected Action cancelAction;

    protected int currentPage = 1;

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

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel datasetPanel = new JPanel(new BorderLayout());

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
        datasetField.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                Log.debug("Dataset field changed: " + datasetField.getText());
                setNextButtonOnDatasetSelection();
            }
        });
        datasetFormPanel.add(datasetField, c);

        datasetPanel.add(datasetFormPanel, BorderLayout.CENTER);

        JPanel targetPanel = new JPanel(new BorderLayout());
        targetPanel.add(new TitledSeparator("Target Location"), BorderLayout.NORTH);

        JPanel targetFormPanel = new JPanel();
        targetFormPanel.setBorder(JBUI.Borders.empty(0, 10));
        targetFormPanel.setLayout(new GridBagLayout());

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

        c.gridy = 2;
        c.weightx = 0.3;
        c.gridx = 0;
        scopeLabel = new JBLabel("Scope:");
        targetFormPanel.add(scopeLabel, c);
        c.weightx = 0.7;
        c.gridx = 1;

        List<ScopeSpec> scopes = new ArrayList<>();
        scopeCombo = new ComboBox<>();
        ActionListener bucketComboListener = (ActionEvent e) -> {
            try {
                Log.debug("Bucket combo box changed"
                        + Optional.ofNullable(bucketCombo.getSelectedItem()).map(Object::toString).orElse("null"));

                scopes.clear();
                scopes.addAll(ActiveCluster.getInstance().get().bucket(
                        bucketCombo.getSelectedItem().toString())
                        .collections().getAllScopes());

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

        ActionListener scopeComboListener = (ActionEvent e) -> {
            try {
                Log.debug("Scope combo box changed"
                        + Optional.ofNullable(scopeCombo.getSelectedItem()).map(Object::toString).orElse("null"));

                String selectedScope = (String) scopeCombo.getSelectedItem();
                if (selectedScope == null || selectedScope.isEmpty() || selectedScope.equals("_default")) {
                    scopeCombo.setSelectedItem("_default");
                    collectionCombo.setSelectedItem("_default");
                    return;
                }

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
                    int result = Messages.showYesNoDialog(project,
                            "The selected scope is empty. Would you like to create a new collection in this scope?",
                            "Empty Scope", Messages.getQuestionIcon());

                    if (result == Messages.YES) {
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

                                collectionCombo.addItem(collectionName);

                                collectionCombo.setSelectedItem(collectionName);
                            }
                        }
                    } else {
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

        scopeLabel.setVisible(false);
        collectionLabel.setVisible(false);
        scopeFieldLabel.setVisible(false);
        collectionFieldLabel.setVisible(false);

        scopeLabel.setEnabled(false);
        collectionLabel.setEnabled(false);
        scopeFieldLabel.setEnabled(false);
        collectionFieldLabel.setEnabled(false);

        scopeCombo.setVisible(false);
        collectionCombo.setVisible(false);
        dynamicScopeFieldField.setVisible(false);
        dynamicCollectionFieldField.setVisible(false);

        scopeCombo.setEnabled(false);
        collectionCombo.setEnabled(false);
        dynamicScopeFieldField.setEnabled(false);
        dynamicCollectionFieldField.setEnabled(false);

        targetPanel.add(targetFormPanel, BorderLayout.CENTER);

        JPanel keyPanel = new JPanel(new BorderLayout());
        keyPanel.add(new TitledSeparator("Document Key"), BorderLayout.NORTH);

        JPanel keyFormPanel = new JPanel();
        keyFormPanel.setBorder(JBUI.Borders.empty(0, 10));
        keyFormPanel.setLayout(new GridBagLayout());

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

        c.gridy = 1;
        c.weightx = 0.3;
        c.gridx = 0;
        fieldNameLabel = new JBLabel("Field name:");
        keyFormPanel.add(fieldNameLabel, c);
        c.weightx = 0.7;
        c.gridx = 1;

        fieldNameField = new JBTextField();
        keyFormPanel.add(fieldNameField, c);

        c.gridy = 2;
        c.weightx = 0.3;
        c.gridx = 0;
        customExpressionLabel = new JBLabel("Custom Expression:");
        keyFormPanel.add(customExpressionLabel, c);

        c.weightx = 0.7;
        c.gridx = 1;

        customExpressionField = new JBTextField();
        keyFormPanel.add(customExpressionField, c);

        fieldNameField.getDocument().addDocumentListener(new DocumentAdapter() {

            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                Log.debug("Field name field changed" + fieldNameField.getText());
                ImportDialogController.updateKeyPreview(ImportDialog.this);
            }
        });

        customExpressionField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                Log.debug("Custom Expression field changed" + customExpressionField.getText());
                ImportDialogController.updateKeyPreview(ImportDialog.this);
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

        fieldNameLabel.setVisible(false);
        customExpressionLabel.setVisible(false);
        fieldNameLabel.setEnabled(false);
        customExpressionLabel.setEnabled(false);

        fieldNameField.setVisible(false);
        customExpressionField.setVisible(false);
        fieldNameField.setEnabled(false);
        customExpressionField.setEnabled(false);

        keyPreviewArea.setVisible(false);
        keyPreviewArea.setEnabled(false);

        JPanel advancedPanel = new JPanel(new BorderLayout());
        advancedPanel.add(new TitledSeparator("Advanced Options"), BorderLayout.NORTH);

        JPanel advancedFormPanel = new JPanel();
        advancedFormPanel.setBorder(JBUI.Borders.empty(0, 10));
        advancedFormPanel.setLayout(new GridBagLayout());

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

        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.add(new TitledSeparator("Summary"), BorderLayout.NORTH);

        summaryLabel = new JBLabel();
        summaryPanel.add(summaryLabel, BorderLayout.CENTER);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(datasetPanel, "1");
        cardPanel.add(targetPanel, "2");
        cardPanel.add(keyPanel, "3");
        cardPanel.add(advancedPanel, "4");
        cardPanel.add(summaryPanel, "5");

        mainPanel.add(cardPanel, BorderLayout.CENTER);

        ImportDialogController.updateSummary(this);
        ImportDialogController.addListeners(this);
        return mainPanel;

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
            ImportDialogController.complexBucketImport(this,
                    Objects.requireNonNull(bucketCombo.getSelectedItem()).toString(),
                    datasetField.getText(),
                    project,
                    datasetField.getText().endsWith(".json") ? "json" : "csv");
            super.doOKAction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
