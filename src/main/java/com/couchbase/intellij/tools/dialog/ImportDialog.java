
package com.couchbase.intellij.tools.dialog;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.DocumentEvent;

import org.jetbrains.annotations.NotNull;

import com.couchbase.intellij.database.ActiveCluster;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;

public class ImportDialog extends DialogWrapper {
    // Declare UI components here
    private TextFieldWithBrowseButton datasetField;

    private ComboBox bucketCombo;

    private JRadioButton defaultScopeAndCollectionRadio;
    private JRadioButton collectionRadio;
    private JRadioButton dynamicScopeAndCollectionRadio;

    private ComboBox scopeCombo;
    private ComboBox collectionCombo;

    private JBTextField scopeFieldField;
    private JBTextField collectionFieldField;

    private JRadioButton generateUUIDRadio;
    private JRadioButton useFieldValueRadio;
    private JRadioButton customExpressionRadio;

    private JBTextField fieldNameField;

    private JBTextField expressionField;

    private JBTextField skipFirstField;
    private JBCheckBox skipFirstCheck;

    private JBTextField importUptoField;
    private JBCheckBox importUptoCheck;

    private JBTextField ignoreFieldsField;
    private JBCheckBox ignoreFieldsCheck;

    private JBTextField threadsField;

    private JBCheckBox verboseCheck;

    // Declare additional components for navigation and summary
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel keyPreviewPanel;

    private JButton backButton;
    private JButton nextButton;

    private int currentPage;

    private JBLabel summaryLabel;

    public ImportDialog() {
        super(true);
        init();
        setTitle("Import Data");
        getWindow().setMinimumSize(new Dimension(600, 380));
        setResizable(true);
        setOKButtonText("Import");
    }

    @Override
    protected JComponent createCenterPanel() {

        // Initialize additional components for navigation and summary
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        backButton = new JButton("Back");
        nextButton = new JButton("Next");

        currentPage = 1;

        summaryLabel = new JBLabel();

        // Create and add UI components for each page here
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Page 1: Select dataset
        JPanel datasetPanel = new JPanel(new BorderLayout());
        datasetPanel.add(new TitledSeparator("Select Dataset"), BorderLayout.NORTH);

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
        datasetField.addBrowseFolderListener("Select Dataset", "", null,
                FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor());
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

        defaultScopeAndCollectionRadio = new JRadioButton("Default Scope and Collection");
        collectionRadio = new JRadioButton("Collection");
        dynamicScopeAndCollectionRadio = new JRadioButton("Dynamic Scope and Collection");

        ButtonGroup group = new ButtonGroup();
        group.add(defaultScopeAndCollectionRadio);
        group.add(collectionRadio);
        group.add(dynamicScopeAndCollectionRadio);

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
        targetFormPanel.add(new JBLabel("Scope:"), c);
        c.weightx = 0.7;
        c.gridx = 1;

        scopeCombo = new ComboBox();
        targetFormPanel.add(scopeCombo, c);

        c.gridy = 3;
        c.weightx = 0.3;
        c.gridx = 0;
        targetFormPanel.add(new JBLabel("Collection:"), c);
        c.weightx = 0.7;
        c.gridx = 1;

        collectionCombo = new ComboBox();
        targetFormPanel.add(collectionCombo, c);

        // Scope and collection fields
        c.gridy = 4;
        c.weightx = 0.3;
        c.gridx = 0;
        targetFormPanel.add(new JBLabel("Scope Field:"), c);

        c.weightx = 0.7;
        c.gridx = 1;

        scopeFieldField = new JBTextField();
        targetFormPanel.add(scopeFieldField, c);

        c.gridy = 5;
        c.weightx = 0.3;
        c.gridx = 0;
        targetFormPanel.add(new JBLabel("Collection Field:"), c);
        c.weightx = 0.7;
        c.gridx = 1;

        collectionFieldField = new JBTextField();
        targetFormPanel.add(collectionFieldField, c);

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
        keyFormPanel.add(new JBLabel("Key Options:"), c);
        c.weightx = 0.7;
        c.gridx = 1;

        generateUUIDRadio = new JRadioButton("Generate random UUID for each document");

        useFieldValueRadio = new JRadioButton("Use the value of a field as the key");
        customExpressionRadio = new JRadioButton("Generate key based on custom expression");

        ButtonGroup keyGroup = new ButtonGroup();
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
        keyFormPanel.add(new JBLabel("Field Name:"), c);
        c.weightx = 0.7;
        c.gridx = 1;

        fieldNameField = new JBTextField();
        keyFormPanel.add(fieldNameField, c);

        // Expression field
        c.gridy = 2;
        c.weightx = 0.3;
        c.gridx = 0;
        keyFormPanel.add(new JBLabel("Expression:"), c);

        c.weightx = 0.7;
        c.gridx = 1;

        expressionField = new JBTextField();
        keyFormPanel.add(expressionField, c);

        keyPreviewPanel = new JPanel();
        keyFormPanel.add(keyPreviewPanel, c);
        // In addListeners method, add listeners for relevant fields:
        fieldNameField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                updateKeyPreview();
            }
        });

        expressionField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                updateKeyPreview();
            }
        });

        keyPanel.add(keyFormPanel, BorderLayout.CENTER);

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
        advancedFormPanel.add(new JBLabel("Skip the first # Documents:"), c);
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
        advancedFormPanel.add(new JBLabel("Import up to # Documents:"), c);
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
        advancedFormPanel.add(new JBLabel("Ignore the fields:"), c);
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
        advancedFormPanel.add(new JBLabel("Threads:"), c);
        c.weightx = 0.7;
        c.gridx = 1;

        threadsField = new JBTextField();
        advancedFormPanel.add(threadsField, c);

        // Verbose log
        c.gridy = 4;
        c.weightx = 0.3;
        c.gridx = 0;
        advancedFormPanel.add(new JBLabel("Verbose Log:"), c);
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

        // Add pages to card panel
        cardPanel.add(datasetPanel, "1");
        cardPanel.add(targetPanel, "2");
        cardPanel.add(keyPanel, "3");
        cardPanel.add(advancedPanel, "4");
        cardPanel.add(summaryPanel, "5");

        mainPanel.add(cardPanel, BorderLayout.CENTER);

        // Add navigation buttons
        // JPanel buttonPanel = new JPanel();
        // backButton.setEnabled(false);
        // backButton.addActionListener(e -> previousPage());
        // buttonPanel.add(backButton);

        // nextButton.addActionListener(e -> nextPage());
        // buttonPanel.add(nextButton);

        // mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add listeners to update summary and show/hide fields
        addListeners();

        return mainPanel;
    }

    private void addListeners() {
        // Add listeners for Page 2 radio buttons
        defaultScopeAndCollectionRadio.addActionListener(e -> updateScopeAndCollectionFields());
        collectionRadio.addActionListener(e -> updateScopeAndCollectionFields());
        dynamicScopeAndCollectionRadio.addActionListener(e -> updateScopeAndCollectionFields());
    }

    private void updateScopeAndCollectionFields() {
        boolean collectionSelected = collectionRadio.isSelected();
        scopeCombo.setVisible(collectionSelected);
        collectionCombo.setVisible(collectionSelected);

        boolean dynamicSelected = dynamicScopeAndCollectionRadio.isSelected();
        scopeFieldField.setVisible(dynamicSelected);
        collectionFieldField.setVisible(dynamicSelected);
    }

    private void updateKeyPreview() {
        // Update key preview panel with preview content
        keyPreviewPanel.removeAll();

        if (useFieldValueRadio.isSelected()) {
            // Generate preview based on field name
            String fieldName = fieldNameField.getText();
            // Add preview content to keyPreviewPanel
        } else if (customExpressionRadio.isSelected()) {
            // Generate preview based on custom expression
            String expression = expressionField.getText();
            // Add preview content to keyPreviewPanel
        }

        keyPreviewPanel.revalidate();
        keyPreviewPanel.repaint();
    }

    private void updateSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("<html>");

        // Page 1: Dataset
        summary.append("<b>Dataset:</b> ");
        summary.append(datasetField.getText());
        summary.append("<br>");

        // Page 2: Bucket and target location
        summary.append("<b>Bucket:</b> ");
        summary.append(bucketCombo.getSelectedItem());
        summary.append("<br>");

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
        summary.append("<br>");

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
        summary.append("<br>");

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
        summary.append(threadsField.getText());
        summary.append("<br>");
        if (verboseCheck.isSelected()) {
            summary.append("- Verbose Log<br>");
        }

        summary.append("</html>");

        summaryLabel.setText(summary.toString());
    }

    private void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            cardLayout.show(cardPanel, Integer.toString(currentPage));
            backButton.setEnabled(currentPage > 1);
            nextButton.setText(currentPage == 5 ? "Import" : "Next");
        }
    }

    private void nextPage() {
        if (currentPage < 5) {
            currentPage++;
            cardLayout.show(cardPanel, Integer.toString(currentPage));
            backButton.setEnabled(true);
            nextButton.setText(currentPage == 5 ? "Import" : "Next");
        } else {
            doOKAction();
        }
    }

    @Override
    protected void doOKAction() {
        // Implement import functionality here
        super.doOKAction();
    }

    @Override
    protected Action @NotNull [] createActions() {
        Action cancelAction = getCancelAction();
        Action backAction = new DialogWrapperAction("Back") {
            @Override
            protected void doAction(ActionEvent e) {
                previousPage();
            }
        };
        Action nextAction = new DialogWrapperAction(currentPage == 5 ? "Import" : "Next") {
            @Override
            protected void doAction(ActionEvent e) {
                nextPage();
            }

        };

        return new Action[] { cancelAction, backAction, nextAction };
    }
}
