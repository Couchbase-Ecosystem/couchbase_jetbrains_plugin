
package com.couchbase.intellij.tools.dialog;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;

import org.jetbrains.annotations.NotNull;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tools.CBImport;
import com.couchbase.intellij.tools.CBTools;
import com.couchbase.intellij.tree.NewEntityCreationDialog;
import com.couchbase.intellij.tree.NewEntityCreationDialog.EntityType;
import com.couchbase.intellij.workbench.Log;
import com.intellij.ide.ui.laf.darcula.ui.DarculaTextBorder;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;

import utils.FileUtils;
import utils.TemplateUtil;

public class ImportDialog extends DialogWrapper {

    protected final Project project;

    protected TextFieldWithBrowseButton datasetField;

    protected ComboBox<String> bucketCombo;
    protected ComboBox<String> scopeCombo;
    protected ComboBox<String> collectionCombo;

    protected JBRadioButton defaultScopeAndCollectionRadio;
    protected JBRadioButton collectionRadio;
    protected JBRadioButton dynamicScopeAndCollectionRadio;

    protected JBTextField dynamicScopeField;
    protected JBTextField dynamicCollectionField;

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
    protected JPanel mainPanel;
    protected JPanel universalErrorPanel;
    protected JPanel datasetPanel;
    protected JPanel datasetFormPanel;
    protected JPanel datasetLabelHelpPanel;
    protected JPanel targetPanel;
    protected JPanel targetFormPanel;
    protected JPanel bucketLabelHelpPanel;
    protected JPanel scopeAndCollectionLabelHelpPanel;
    protected JPanel radioPanel;
    protected JPanel scopeLabelHelpPanel;
    protected JPanel collectionLabelHelpPanel;
    protected JPanel scopeFieldLabelHelpPanel;
    protected JPanel collectionFieldLabelHelpPanel;
    protected JPanel keyPanel;
    protected JPanel keyFormPanel;
    protected JPanel keyOptionsLabelHelpPanel;
    protected JPanel keyRadioPanel;
    protected JPanel fieldNameLabelHelpPanel;
    protected JPanel customExpressionLabelHelpPanel;
    protected JPanel keyPreviewPanel;
    protected JPanel advancedPanel;
    protected JPanel advancedFormPanel;
    protected JPanel skipFirstLabelHelpPanel;
    protected JPanel importUptoLabelHelpPanel;
    protected JPanel ignoreFieldsLabelHelpPanel;
    protected JPanel threadsLabelHelpPanel;
    protected JPanel verboseLabelHelpPanel;
    protected JPanel summaryPanel;
    protected JPanel panel;

    protected JTextArea keyPreviewArea;

    protected JBLabel universalErrorLabel;
    protected JBLabel scopeLabel;
    protected JBLabel collectionLabel;
    protected JBLabel scopeFieldLabel;
    protected JBLabel collectionFieldLabel;
    protected JBLabel fieldNameLabel;
    protected JBLabel customExpressionLabel;
    protected JBLabel customExpressionInfoLabel;
    protected JBLabel skipFirstLabel;
    protected JBLabel importUptoLabel;
    protected JBLabel ignoreFieldsLabel;
    protected JBLabel threadsLabel;
    protected JBLabel verboseLabel;
    protected JBLabel summaryLabel;

    protected TitledSeparator keyPreviewTitledSeparator;

    protected JButton backButton;
    protected JButton nextButton;

    protected int currentPage = 1;

    protected final String[] possibleScopeFields = { "cbms", "scope", "cbs" };
    protected final String[] possibleCollectionFields = { "cbmc", "collection", "cbc" };
    protected final String[] possibleKeyFields = { "cbmk", "cbmid", "key", "cbk" };

    protected String targetScopeField;
    protected String targetCollectionField;
    protected String fileFormat;
    protected String detectedCouchbaseJsonFormat;

    protected List<ScopeSpec> scopeItems;
    protected ArrayList<String> cachedJsonDocs = new ArrayList<>();

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
        mainPanel = new JPanel(new BorderLayout());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(createDatasetPanel(), "1");
        cardPanel.add(createTargetPanel(), "2");
        cardPanel.add(createKeyPanel(), "3");
        cardPanel.add(createAdvancedPanel(), "4");
        cardPanel.add(createSummaryPanel(), "5");

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

    protected JPanel createDatasetPanel() {
        datasetPanel = new JPanel(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        GridBagConstraints d = new GridBagConstraints();
        d.fill = GridBagConstraints.HORIZONTAL;
        d.gridy = 0;
        d.weightx = 0.5;
        d.gridx = 0;
        d.insets = JBUI.insets(5);

        JPanel listHeadingPanel = TemplateUtil.getLabelWithHelp("Lists",
                "<html>[<br>" +
                        "{<br>" +
                        "\"key\": \"mykey1\",<br>" +
                        "\"value\": \"myvalue1\"<br>" +
                        "},<br>" +
                        "{\"key\": \"mykey2\", \"value\": \"myvalue2\"}<br>" +
                        "{\"key\": \"mykey3\", \"value\": \"myvalue3\"}<br>" +
                        "{\"key\": \"mykey4\", \"value\": \"myvalue4\"}<br>" +
                        "]</html>");
        JLabel listDefinitionLabel = new JLabel(
                "<html>The list format specifies a file which contains a JSON list where<br>each element in the list is a JSON document. The file may only contain a<br>single list, but the list may be specified over multiple lines. This format<br>is specified by setting the --format option to \"list\". Below is an example<br>of a file in list format.</html>");

        JPanel linesHeadingPanel = TemplateUtil.getLabelWithHelp("Lines",
                "<html>{\"key\": \"mykey1\", \"value\": \"myvalue1\"}<br>" +
                        "{\"key\": \"mykey2\", \"value\": \"myvalue2\"}<br>" +
                        "{\"key\": \"mykey3\", \"value\": \"myvalue3\"}<br>" +
                        "{\"key\": \"mykey4\", \"value\": \"myvalue4\"}<br>" +
                        "</html>");

        JLabel linesDefinitionLabel = new JLabel(
                "<html>The lines format specifies a file that contains one JSON document on<br>every line in the file. This format is specified by setting the --format<br>option to \"lines\". Below is an example of a file in lines format.</html>");

        contentPanel.add(listHeadingPanel, d);
        d.gridy++;
        contentPanel.add(listDefinitionLabel, d);
        d.gridy++;
        contentPanel.add(linesHeadingPanel, d);
        d.gridy++;
        contentPanel.add(linesDefinitionLabel, d);

        datasetFormPanel = new JPanel();
        datasetFormPanel.setBorder(JBUI.Borders.empty(0, 10));
        datasetFormPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = JBUI.insets(5);

        c.gridy = 0;
        c.weightx = 0.3;
        c.gridx = 0;
        datasetLabelHelpPanel = TemplateUtil.getLabelWithHelp("Select the Dataset:",
                "<html>Select the file containing the data to import. The file must be in either JSON or CSV format.</html>");
        datasetFormPanel.add(datasetLabelHelpPanel, c);
        c.weightx = 0.7;
        c.gridx = 1;

        datasetField = new TextFieldWithBrowseButton();
        datasetFormPanel.add(datasetField, c);

        d.gridy++;
        contentPanel.add(datasetFormPanel, d);
        datasetPanel.add(contentPanel, BorderLayout.NORTH);

        return datasetPanel;
    }

    protected JPanel createTargetPanel() {
        targetPanel = new JPanel(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(new TitledSeparator("Target Location"));

        targetFormPanel = new JPanel();
        targetFormPanel.setBorder(JBUI.Borders.empty(0, 10));
        targetFormPanel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridy = 0;
        c.weightx = 0.3;
        c.gridx = 0;
        bucketLabelHelpPanel = TemplateUtil.getLabelWithHelp("Bucket:",
                "<html>Select the bucket where you want to import the data.</html>");
        targetFormPanel.add(bucketLabelHelpPanel, c);
        c.weightx = 0.7;
        c.gridx = 1;

        Set<String> bucketSet = ActiveCluster.getInstance().get().buckets().getAllBuckets().keySet();
        String[] buckets = bucketSet.toArray(new String[0]);

        bucketCombo = new ComboBox<>(buckets);
        targetFormPanel.add(bucketCombo, c);

        c.gridy = 1;
        c.weightx = 0.3;
        c.gridx = 0;
        scopeAndCollectionLabelHelpPanel = TemplateUtil.getLabelWithHelp("Scope and Collection:",
                "<html>Select the scope and collection where you want to import the data. You can choose to import into the default scope and collection, a specific collection, or dynamically determine the scope and collection based on the data.</html>");
        targetFormPanel.add(scopeAndCollectionLabelHelpPanel, c);
        c.weightx = 0.7;
        c.gridx = 1;

        defaultScopeAndCollectionRadio = new JBRadioButton("Default scope and collection");
        collectionRadio = new JBRadioButton("Collection");
        dynamicScopeAndCollectionRadio = new JBRadioButton("Dynamic scope and collection");

        targetLocationGroup = new ButtonGroup();
        targetLocationGroup.add(defaultScopeAndCollectionRadio);
        targetLocationGroup.add(collectionRadio);
        targetLocationGroup.add(dynamicScopeAndCollectionRadio);

        defaultScopeAndCollectionRadio.setSelected(true);

        radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
        radioPanel.add(defaultScopeAndCollectionRadio);
        radioPanel.add(collectionRadio);
        radioPanel.add(dynamicScopeAndCollectionRadio);

        targetFormPanel.add(radioPanel, c);

        c.gridy = 2;
        c.weightx = 0.3;
        c.gridx = 0;
        scopeLabel = new JBLabel("Scope:");
        scopeLabelHelpPanel = TemplateUtil.getLabelWithHelp(scopeLabel,
                "<html>Select the scope where you want to import the data.</html>");
        targetFormPanel.add(scopeLabelHelpPanel, c);
        c.weightx = 0.7;
        c.gridx = 1;

        scopeItems = new ArrayList<>();
        scopeCombo = new ComboBox<>();

        targetFormPanel.add(scopeCombo, c);

        c.gridy = 3;
        c.weightx = 0.3;
        c.gridx = 0;
        collectionLabel = new JBLabel("Collection:");
        collectionLabelHelpPanel = TemplateUtil.getLabelWithHelp(collectionLabel,
                "<html>Select the collection where you want to import the data.</html>");
        targetFormPanel.add(collectionLabelHelpPanel, c);
        c.weightx = 0.7;
        c.gridx = 1;

        collectionCombo = new ComboBox<>();

        targetFormPanel.add(collectionCombo, c);

        c.gridy = 4;
        c.weightx = 0.3;
        c.gridx = 0;
        scopeFieldLabel = new JBLabel("Scope field:");
        scopeFieldLabelHelpPanel = TemplateUtil.getLabelWithHelp(scopeFieldLabel,
                "<html>Specify the field in the data that contains the scope name.</html>");
        targetFormPanel.add(scopeFieldLabelHelpPanel, c);
        c.weightx = 0.7;
        c.gridx = 1;

        dynamicScopeField = new JBTextField();
        targetFormPanel.add(dynamicScopeField, c);

        c.gridy = 5;
        c.weightx = 0.3;
        c.gridx = 0;
        collectionFieldLabel = new JBLabel("Collection field:");
        collectionFieldLabelHelpPanel = TemplateUtil.getLabelWithHelp(collectionFieldLabel,
                "<html>Specify the field in the data that contains the collection name.</html>");
        targetFormPanel.add(collectionFieldLabelHelpPanel, c);
        c.weightx = 0.7;
        c.gridx = 1;

        dynamicCollectionField = new JBTextField();
        targetFormPanel.add(dynamicCollectionField, c);

        scopeLabelHelpPanel.setVisible(false);
        collectionLabelHelpPanel.setVisible(false);
        scopeFieldLabelHelpPanel.setVisible(false);
        collectionFieldLabelHelpPanel.setVisible(false);

        scopeCombo.setVisible(false);
        collectionCombo.setVisible(false);
        dynamicScopeField.setVisible(false);
        dynamicCollectionField.setVisible(false);

        contentPanel.add(targetFormPanel);
        targetPanel.add(contentPanel, BorderLayout.NORTH);

        handleBucketComboBoxChange();
        return targetPanel;
    }

    protected JPanel createKeyPanel() {
        keyPanel = new JPanel(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(new TitledSeparator("Document Key"));

        keyFormPanel = new JPanel();
        keyFormPanel.setBorder(JBUI.Borders.empty(0, 10));
        keyFormPanel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridy = 0;
        c.weightx = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridx = 0;
        keyOptionsLabelHelpPanel = TemplateUtil.getLabelWithHelp("Key options:",
                "<html>Select how you want to generate the document key for each imported document. You can choose to generate a random UUID, use the value of a field in the data, or generate a custom expression.</html>");
        keyFormPanel.add(keyOptionsLabelHelpPanel, c);

        c.gridy = 1;
        c.weightx = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridx = 0;

        generateUUIDRadio = new JBRadioButton("Generate random UUID for each document");
        useFieldValueRadio = new JBRadioButton("Use the value of a field as the key");
        customExpressionRadio = new JBRadioButton("Generate key based on custom expression");

        keyGroup = new ButtonGroup();
        keyGroup.add(generateUUIDRadio);
        keyGroup.add(useFieldValueRadio);
        keyGroup.add(customExpressionRadio);

        generateUUIDRadio.setSelected(true);

        keyRadioPanel = new JPanel();
        keyRadioPanel.setLayout(new BoxLayout(keyRadioPanel, BoxLayout.Y_AXIS));
        keyRadioPanel.add(generateUUIDRadio);
        keyRadioPanel.add(useFieldValueRadio);
        keyRadioPanel.add(customExpressionRadio);

        keyFormPanel.add(keyRadioPanel, c);

        c.gridy = 2;
        c.weightx = 0.05;
        c.gridwidth = 1;
        c.gridx = 0;
        fieldNameLabel = new JBLabel("Field name:");
        fieldNameLabelHelpPanel = TemplateUtil.getLabelWithHelp(fieldNameLabel,
                "<html>Specify the field in the data that contains the value to use as the document key.</html>");
        keyFormPanel.add(fieldNameLabelHelpPanel, c);
        c.weightx = 0.7;
        c.gridx = 1;

        fieldNameField = new JBTextField();
        keyFormPanel.add(fieldNameField, c);

        c.gridy = 3;
        c.weightx = 0.05;
        c.gridx = 0;
        customExpressionLabel = new JBLabel("Custom expression:");
        customExpressionLabelHelpPanel = TemplateUtil.getLabelWithHelp(customExpressionLabel,
                "<html>Specify the custom expression to generate the document key. You can use the following variables: <ul><li><b>#UUID#</b> - Random UUID</li><li><b>%FIELDNAME%</b> - Value of the field specified above</li></ul></html>");
        keyFormPanel.add(customExpressionLabelHelpPanel, c);
        c.weightx = 0.7;
        c.gridx = 1;

        customExpressionField = new JBTextField();
        keyFormPanel.add(customExpressionField, c);

        c.gridy = 4;
        c.weightx = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridx = 0;

        customExpressionInfoLabel = new JBLabel(
                "<html>Note: If #UUID# tag is used, the UUID values in the preview area might not match the actual UUID values generated during import.</html>");
        customExpressionInfoLabel.setForeground(Color.decode("#FFA500"));
        customExpressionInfoLabel.setVisible(false);
        keyFormPanel.add(customExpressionInfoLabel, c);

        contentPanel.add(keyFormPanel);
        keyPanel.add(contentPanel, BorderLayout.NORTH);

        keyPreviewPanel = new JPanel(new BorderLayout());
        keyPreviewTitledSeparator = new TitledSeparator("Key Preview");
        keyPreviewTitledSeparator.setVisible(false);
        keyPreviewPanel.add(keyPreviewTitledSeparator, BorderLayout.NORTH);

        keyPreviewArea = new JTextArea();
        keyPreviewArea.setEditable(false);
        keyPreviewArea.setLineWrap(true);
        keyPreviewArea.setWrapStyleWord(true);

        keyPreviewArea.setMinimumSize(new Dimension(150, 100));
        keyPreviewArea.setPreferredSize(new Dimension(150, 100));

        keyPreviewPanel.add(keyPreviewArea, BorderLayout.CENTER);

        contentPanel.add(keyPreviewPanel);

        fieldNameLabelHelpPanel.setVisible(false);
        customExpressionLabelHelpPanel.setVisible(false);

        fieldNameField.setVisible(false);
        customExpressionField.setVisible(false);

        keyPreviewPanel.setVisible(false);

        return keyPanel;
    }

    protected JPanel createAdvancedPanel() {
        advancedPanel = new JPanel(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(new TitledSeparator("Advanced Options"));

        advancedFormPanel = new JPanel();
        advancedFormPanel.setBorder(JBUI.Borders.empty(0, 10));
        advancedFormPanel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridy = 0;
        c.weightx = 0.3;
        c.gridx = 0;
        skipFirstLabel = new JBLabel("Skip the first # Documents:");
        skipFirstLabelHelpPanel = TemplateUtil.getLabelWithHelp(skipFirstLabel,
                "<html>Specify the number of documents to skip from the beginning of the file.</html>");
        advancedFormPanel.add(skipFirstLabelHelpPanel, c);
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
        importUptoLabelHelpPanel = TemplateUtil.getLabelWithHelp(importUptoLabel,
                "<html>Specify the maximum number of documents to import.</html>");
        advancedFormPanel.add(importUptoLabelHelpPanel, c);
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
        ignoreFieldsLabelHelpPanel = TemplateUtil.getLabelWithHelp(ignoreFieldsLabel,
                "<html>Specify the fields in the data to ignore.</html>");
        advancedFormPanel.add(ignoreFieldsLabelHelpPanel, c);
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
        threadsLabelHelpPanel = TemplateUtil.getLabelWithHelp(threadsLabel,
                "<html>Specify the number of threads to use for importing the data.</html>");
        advancedFormPanel.add(threadsLabelHelpPanel, c);
        c.weightx = 0.7;
        c.gridx = 1;

        int maxThreads = Runtime.getRuntime().availableProcessors();
        threadsSpinner = new JSpinner(new SpinnerNumberModel(4, 1, maxThreads, 1));
        advancedFormPanel.add(threadsSpinner, c);

        c.gridy = 4;
        c.weightx = 0.3;
        c.gridx = 0;
        verboseLabel = new JBLabel("Verbose log:");
        verboseLabelHelpPanel = TemplateUtil.getLabelWithHelp(verboseLabel,
                "<html>Specify whether to enable verbose logging.</html>");
        advancedFormPanel.add(verboseLabelHelpPanel, c);
        c.weightx = 0.7;
        c.gridx = 1;

        skipFirstField.setEnabled(false);
        importUptoField.setEnabled(false);
        ignoreFieldsField.setEnabled(true);

        verboseCheck = new JBCheckBox();
        advancedFormPanel.add(verboseCheck, c);

        contentPanel.add(advancedFormPanel);
        advancedPanel.add(contentPanel, BorderLayout.NORTH);

        return advancedPanel;
    }

    protected JPanel createSummaryPanel() {
        summaryPanel = new JPanel(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(new TitledSeparator("Summary"));

        summaryLabel = new JBLabel();
        contentPanel.add(summaryLabel);

        summaryPanel.add(contentPanel, BorderLayout.NORTH);

        return summaryPanel;
    }

    protected void calculateSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("<html>");

        summary.append("<b>Dataset:</b> ");
        summary.append(datasetField.getText());
        summary.append("<br><br>");

        summary.append("<b>Bucket:</b> ");
        summary.append(bucketCombo.getSelectedItem());
        summary.append("<br><br>");

        summary.append("<b>Scope and Collection:</b> ");
        if (defaultScopeAndCollectionRadio.isSelected()) {
            summary.append("Default Scope and Collection");
        } else if (collectionRadio.isSelected()) {
            summary.append("Collection - Scope: ");
            summary.append(targetScopeField);
            summary.append(", Collection: ");
            summary.append(targetCollectionField);
        } else if (dynamicScopeAndCollectionRadio.isSelected()) {
            summary.append("Dynamic Scope and Collection - Scope Field: ");
            summary.append(dynamicScopeField.getText());
            summary.append(", Collection Field: ");
            summary.append(dynamicCollectionField.getText());
        }
        summary.append("<br><br>");

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

        summary.append("<b>Advanced Options:</b><br>");
        if (skipFirstCheck.isSelected()) {
            if (fileFormat.equals("json")) {
                summary.append("- Skip the first ");
                summary.append(skipFirstField.getText());
                summary.append(" documents<br>");
            } else if (fileFormat.equals("csv")) {
                summary.append("- Skip the first ");
                summary.append(skipFirstField.getText());
                summary.append(" rows<br>");
            }
        }
        if (importUptoCheck.isSelected()) {
            if (fileFormat.equals("json")) {
                summary.append("- Import up to ");
                summary.append(importUptoField.getText());
                summary.append(" documents<br>");
            } else if (fileFormat.equals("csv")) {
                summary.append("- Import up to ");
                summary.append(importUptoField.getText());
                summary.append(" rows<br>");
            }
        }
        if (ignoreFieldsCheck.isSelected()) {
            summary.append("- Ignore the fields: ");
            summary.append(ignoreFieldsField.getText());
            summary.append("<br>");
        }
        summary.append("- Threads: ");
        summary.append(threadsSpinner.getValue());
        summary.append("<br>");
        if (verboseCheck.isSelected()) {
            summary.append("- Verbose Log<br>");
        }

        summaryLabel.setText(summary.toString());
    }

    protected void addListeners() {

        // Page 1: Dataset
        datasetField.addBrowseFolderListener("Select the Dataset", "", null,
                new FileChooserDescriptor(true, false, false, false, false, false) {
                    @Override
                    public boolean isFileVisible(VirtualFile file, boolean showHiddenFiles) {
                        if (!file.isDirectory()) {
                            String extension = file.getExtension();
                            return "json".equalsIgnoreCase(extension) || "csv".equalsIgnoreCase(extension);
                        }
                        return true;
                    }
                });
        datasetField.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                Log.debug("Dataset field changed: " + datasetField.getText());
                String filePath = datasetField.getText();
                if (filePath.endsWith(".json")) {
                    fileFormat = "json";
                } else if (filePath.endsWith(".csv")) {
                    fileFormat = "csv";
                }
                validateAndEnableNextButton();
            }
        });

        // Page 2: Target Location
        ActionListener radioListener = e -> {
            updateScopeAndCollectionFields();
            validateAndEnableNextButton();
        };
        defaultScopeAndCollectionRadio.addActionListener(radioListener);
        collectionRadio.addActionListener(radioListener);
        dynamicScopeAndCollectionRadio.addActionListener(radioListener);

        DocumentAdapter documentAdapter = new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                validateAndEnableNextButton();
            }
        };
        dynamicScopeField.getDocument().addDocumentListener(documentAdapter);
        dynamicCollectionField.getDocument().addDocumentListener(documentAdapter);

        bucketCombo.addActionListener(e -> handleBucketComboBoxChange());
        scopeCombo.addActionListener(e -> handleScopeComboBoxChange());

        ActionListener comboListener = e -> validateAndEnableNextButton();
        bucketCombo.addActionListener(comboListener);
        scopeCombo.addActionListener(comboListener);
        collectionCombo.addActionListener(comboListener);

        scopeCombo.addActionListener(e -> {
            Log.debug("Scope combo box changed: "
                    + Optional.ofNullable(scopeCombo.getSelectedItem()).map(Object::toString)
                            .orElse(null));
            targetScopeField = Optional.ofNullable(scopeCombo.getSelectedItem())
                    .map(Object::toString)
                    .orElse("_default");
        });
        collectionCombo.addActionListener(e -> {
            Log.debug("Collection combo box changed: "
                    + Optional.ofNullable(collectionCombo.getSelectedItem())
                            .map(Object::toString).orElse(null));
            targetCollectionField = Optional
                    .ofNullable(collectionCombo.getSelectedItem())
                    .map(Object::toString)
                    .orElse("_default");
        });

        dynamicScopeField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                updateIgnoreFieldsTextField();
            }
        });

        dynamicCollectionField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                updateIgnoreFieldsTextField();
            }
        });

        // Page 3: Document Key
        ActionListener keyRadioListener = e -> {
            updateKeyFormFields();
            validateAndEnableNextButton();
        };
        generateUUIDRadio.addActionListener(keyRadioListener);
        useFieldValueRadio.addActionListener(e -> {
            updateKeyFormFields();
            updateIgnoreFieldsTextField();
            validateAndEnableNextButton();
        });
        customExpressionRadio.addActionListener(keyRadioListener);

        fieldNameField.getDocument().addDocumentListener(new DocumentAdapter() {

            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                Log.debug("Field name field changed: " + fieldNameField.getText());
                updateKeyPreview();
                validateAndEnableNextButton();
            }
        });

        customExpressionField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                Log.debug("Custom Expression field changed: " +
                        customExpressionField.getText());
                updateKeyPreview();
                validateAndEnableNextButton();
            }
        });

        // Page 4: Advanced Options
        ActionListener checkListener = e -> {
            enableFields();
            validateAndEnableNextButton();
        };
        skipFirstCheck.addActionListener(checkListener);
        skipFirstField.getDocument().addDocumentListener(documentAdapter);

        importUptoCheck.addActionListener(checkListener);
        importUptoField.getDocument().addDocumentListener(documentAdapter);

        ignoreFieldsCheck.addActionListener(e -> {
            enableFields();
            updateIgnoreFieldsTextField();
            validateAndEnableNextButton();
        });

        ignoreFieldsField.getDocument().addDocumentListener(documentAdapter);

    }

    private void enableFields() {
        skipFirstField.setEnabled(skipFirstCheck.isSelected());
        importUptoField.setEnabled(importUptoCheck.isSelected());
        ignoreFieldsField.setEnabled(ignoreFieldsCheck.isSelected());
    }

    protected void handleBucketComboBoxChange() {
        try {
            String selectedBucket = Optional.ofNullable(bucketCombo.getSelectedItem())
                    .map(Object::toString)
                    .orElse(null);
            Log.debug("Bucket combo box changed: " + selectedBucket);

            scopeItems.clear();
            scopeItems.addAll(ActiveCluster.getInstance().get().bucket(selectedBucket)
                    .collections().getAllScopes());

            Set<String> scopeSet = scopeItems.stream()
                    .map(ScopeSpec::name)
                    .collect(Collectors.toSet());
            scopeCombo.removeAllItems();
            for (String scopeItem : scopeSet) {
                scopeCombo.addItem(scopeItem);
            }

            if (scopeCombo.getItemCount() > 0) {
                scopeCombo.setSelectedIndex(0);
                targetScopeField = Objects.requireNonNull(scopeCombo.getSelectedItem()).toString();
            } else {
                int result = Messages.showYesNoDialog(project,
                        "The selected bucket " + selectedBucket
                                + " is empty. Would you like to create a new scoope in this bucket?",
                        "Empty Bucket", Messages.getQuestionIcon());

                if (result == Messages.YES && !ActiveCluster.getInstance().isReadOnlyMode()) {
                    NewEntityCreationDialog entityCreationDialog = new NewEntityCreationDialog(
                            project,
                            EntityType.SCOPE,
                            selectedBucket);
                    entityCreationDialog.show();

                    if (entityCreationDialog.isOK()) {
                        String scopeName = entityCreationDialog.getEntityName();
                        ActiveCluster.getInstance().get().bucket(selectedBucket).collections()
                                .createScope(scopeName);

                        scopeCombo.addItem(scopeName);
                        scopeCombo.setSelectedItem(scopeName);
                    }
                } else {
                    scopeCombo.setSelectedItem("_default");
                    collectionCombo.setSelectedItem("_default");
                }
            }

            handleScopeComboBoxChange();
        } catch (Exception ex) {
            Log.error("Exception occurred" + ex);
        }
    }

    protected void handleScopeComboBoxChange() {
        try {
            String selectedScope = Optional.ofNullable(scopeCombo.getSelectedItem())
                    .map(Object::toString)
                    .orElse(null);
            Log.debug("Scope combo box changed: " + selectedScope);

            if (selectedScope == null || selectedScope.isEmpty()) {
                scopeCombo.setSelectedItem("_default");
                collectionCombo.setSelectedItem("_default");
                return;
            }

            collectionCombo.removeAllItems();

            String[] collectionItems = scopeItems.stream()
                    .filter(scope -> scope.name().equals(selectedScope))
                    .flatMap(scope -> scope.collections().stream())
                    .map(CollectionSpec::name)
                    .distinct()
                    .toArray(String[]::new);

            for (String collectionItem : collectionItems) {
                collectionCombo.addItem(collectionItem);
            }

            if (collectionCombo.getItemCount() > 0) {
                collectionCombo.setSelectedIndex(0);
                targetCollectionField = Objects.requireNonNull(collectionCombo.getSelectedItem()).toString();
            } else {
                int result = Messages.showYesNoDialog(project,
                        "The selected scope " + selectedScope
                                + " is empty. Would you like to create a new collection in this scope?",
                        "Empty Scope", Messages.getQuestionIcon());

                if (result == Messages.YES && !ActiveCluster.getInstance().isReadOnlyMode()) {
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
                } else {
                    scopeCombo.setSelectedItem("_default");
                    collectionItems = scopeItems.stream()
                            .filter(scope -> scope.name().equals("_default"))
                            .flatMap(scope -> scope.collections().stream())
                            .map(CollectionSpec::name)
                            .distinct()
                            .toArray(String[]::new);

                    for (String collectionItem : collectionItems) {
                        collectionCombo.addItem(collectionItem);
                    }
                    collectionCombo.setSelectedIndex(0);
                    targetCollectionField = Objects.requireNonNull(collectionCombo.getSelectedItem()).toString();
                }
            }
        } catch (Exception ex) {
            Log.error("Exception Occurred " + ex);
        }
    }

    protected void validateAndEnableNextButton() {
        try {
            boolean isValid = true;
            List<String> errorMessages = new ArrayList<>();
            if (currentPage == 1) {
                String datasetText = datasetField.getText();
                boolean isValidDataset = !(datasetText.isEmpty()
                        || !(datasetText.endsWith(".json") || datasetText.endsWith(".csv")));
                highlightField(datasetField, isValidDataset);
                if (!isValidDataset) {
                    isValid = false;
                    Log.debug("Validation failed: Dataset field is empty or does not have a valid file extension");
                    errorMessages.add("Please select a valid file.");
                } else {
                    try {
                        detectedCouchbaseJsonFormat = FileUtils.detectDatasetFormat(datasetText);
                        if (detectedCouchbaseJsonFormat == null) {
                            highlightField(datasetField, false);
                            isValid = false;
                            Log.debug("Validation failed: Dataset file is not in a valid Couchbase JSON format");
                            errorMessages.add("Dataset file is not in a valid Couchbase JSON format.");
                        }
                    } catch (Exception ex) {
                        highlightField(datasetField, false);
                        isValid = false;
                        Log.debug("An error occurred while validating the dataset file: " + ex);
                        errorMessages.add("An error occurred while validating the dataset file.");
                    }
                }
            } else if (currentPage == 2) {

                if (dynamicScopeAndCollectionRadio.isSelected()) {
                    String dynamicScopeText = dynamicScopeField.getText();
                    String dynamicCollectionText = dynamicCollectionField.getText();
                    boolean isValidDynamicScope = !dynamicScopeText.isEmpty();
                    boolean isValidDynamicCollection = !dynamicCollectionText.isEmpty();
                    highlightField(dynamicScopeField, isValidDynamicScope);
                    highlightField(dynamicCollectionField, isValidDynamicCollection);
                    if (!isValidDynamicScope || !isValidDynamicCollection) {
                        isValid = false;
                        Log.debug("Validation failed: Dynamic scope and/or collection fields are empty");
                        errorMessages.add("Dynamic scope and/or collection fields are empty.");
                    }
                }

                String scopeFieldText = dynamicScopeField.getText();
                boolean isValidScope = FileUtils.checkFieldsInJson(scopeFieldText,
                        datasetField.getText());

                highlightField(dynamicScopeField, isValidScope);

                String collectionFieldText = dynamicCollectionField.getText();
                boolean isValidCollection = FileUtils.checkFieldsInJson(collectionFieldText,
                        datasetField.getText());
                highlightField(dynamicCollectionField, isValidCollection);

                if (!isValidScope || !isValidCollection) {
                    isValid = false;
                    Log.debug("Validation failed: Scope and/or Collection fields are not valid");
                    errorMessages.add("Scope and/or Collection fields are not valid.");
                }

                if (!defaultScopeAndCollectionRadio.isSelected() && !collectionRadio.isSelected()
                        && !dynamicScopeAndCollectionRadio.isSelected()) {
                    isValid = false;
                    Log.debug("Validation failed: No target location radio box selected");
                    errorMessages.add("No target location radio box selected.");
                } else if (collectionRadio.isSelected()
                        && (scopeCombo.getSelectedItem() == null || collectionCombo.getSelectedItem() == null)) {
                    isValid = false;
                    Log.debug("Validation failed: Scope and/or collection combo box not selected");
                    errorMessages.add("Scope and/or collection combo box not selected.");
                }

            } else if (currentPage == 3) {
                if (useFieldValueRadio.isSelected()) {
                    String fieldNameText = fieldNameField.getText();
                    boolean isValidFieldName = !fieldNameText.isEmpty();
                    highlightField(fieldNameField, isValidFieldName);
                    if (!isValidFieldName) {
                        isValid = false;
                        Log.debug("Validation failed: Field name field is empty");
                        errorMessages.add("Field name field is empty.");
                    }
                } else if (customExpressionRadio.isSelected()) {
                    String customExpressionText = customExpressionField.getText();
                    boolean isValidCustomExpression = !customExpressionText.isEmpty();
                    highlightField(customExpressionField, isValidCustomExpression);
                    if (!isValidCustomExpression) {
                        isValid = false;
                        Log.debug("Validation failed: Custom expression field is empty");
                        errorMessages.add("Custom expression field is empty.");
                    } else
                        customExpressionInfoLabel.setVisible(customExpressionText.contains("#UUID#")
                                || customExpressionText.contains("#MONO_INCR#"));
                }
                if (!generateUUIDRadio.isSelected() && !useFieldValueRadio.isSelected()
                        && !customExpressionRadio.isSelected()) {
                    isValid = false;
                    Log.debug("Validation failed: No key option radio box selected");
                    errorMessages.add("No key option radio box selected.");
                }

            } else if (currentPage == 4) {
                if (skipFirstCheck.isSelected()) {
                    String skipFirstText = skipFirstField.getText();

                    boolean isNonNegativeInteger = skipFirstText.matches("\\d+");

                    if (isNonNegativeInteger) {
                        highlightField(skipFirstField, true);
                    } else {
                        highlightField(skipFirstField, false);
                        isValid = false;
                        Log.debug("Validation failed: Skip first field does not contain a valid non-negative integer");
                        errorMessages.add("Skip first field does not contain a valid non-negative integer.");
                    }
                } else {
                    highlightField(skipFirstField, true);
                }

                if (importUptoCheck.isSelected()) {
                    String importUptoText = importUptoField.getText();

                    boolean isNonNegativeInteger = importUptoText.matches("\\d+");

                    if (isNonNegativeInteger) {
                        highlightField(importUptoField, true);
                    } else {
                        highlightField(importUptoField, false);
                        isValid = false;
                        Log.debug(
                                "Validation failed: Import up to field does not contain a valid non-negative integer");
                        errorMessages.add("Import up to field does not contain a valid non-negative integer.");
                    }
                } else {
                    highlightField(importUptoField, true);
                }

                if (ignoreFieldsCheck.isSelected()) {
                    String ignoreFieldsText = ignoreFieldsField.getText();
                    boolean isValidIgnoreFields = !ignoreFieldsText.isEmpty();
                    highlightField(ignoreFieldsField, isValidIgnoreFields);
                    if (!isValidIgnoreFields) {
                        isValid = false;
                        Log.debug("Validation failed: Ignore fields field is empty");
                        errorMessages.add("Ignore fields field is empty.");
                    }
                } else {
                    highlightField(ignoreFieldsField, true);
                }
            }

            String errorMessage = "<html>" + String.join("<br>", errorMessages) + "</html>";
            universalErrorLabel.setText(errorMessage);
            universalErrorLabel.setVisible(!errorMessages.isEmpty());

            nextButton.setEnabled(isValid);
        } catch (Exception ex) {
            Log.error("Exception occurred: ", ex);
        }

    }

    private void highlightField(JComponent field, boolean isValid) {
        if (!isValid) {
            field.setBorder(BorderFactory.createLineBorder(Color.decode("#FF4444")));
        } else {
            field.setBorder(new DarculaTextBorder());
        }
    }

    protected void updateScopeAndCollectionFields() {
        boolean collectionSelected = collectionRadio.isSelected();

        scopeLabelHelpPanel.setVisible(collectionSelected);
        scopeCombo.setVisible(collectionSelected);

        collectionLabelHelpPanel.setVisible(collectionSelected);
        collectionCombo.setVisible(collectionSelected);

        boolean dynamicSelected = dynamicScopeAndCollectionRadio.isSelected();

        scopeFieldLabelHelpPanel.setVisible(dynamicSelected);
        dynamicScopeField.setVisible(dynamicSelected);

        collectionFieldLabelHelpPanel.setVisible(dynamicSelected);
        dynamicCollectionField.setVisible(dynamicSelected);

        try {
            if (defaultScopeAndCollectionRadio.isSelected()) {
                Log.debug("Default scope and collection Radio selected");

                targetScopeField = "_default";
                targetCollectionField = "_default";
            } else if (collectionSelected) {
                Log.debug("collection Radio selected");

            } else if (dynamicSelected) {
                Log.debug("Dynamic scope and collection Radio selected");
                String[] sampleElementContentSplit = getSampleElementContentSplit(datasetField.getText());

                for (String field : possibleScopeFields) {
                    if (Arrays.stream(sampleElementContentSplit).anyMatch(element -> element.contains(field))) {
                        dynamicScopeField.setText("%" + field + "%");
                        break;
                    }
                }
                for (String field : possibleCollectionFields) {
                    if (Arrays.stream(sampleElementContentSplit).anyMatch(element -> element.contains(field))) {
                        dynamicCollectionField.setText("%" + field + "%");
                        break;
                    }
                }

                updateIgnoreFieldsTextField();

            }
        } catch (Exception ex) {
            Log.error("Exception occurred: ", ex);
        }
    }

    protected void updateKeyFormFields() {
        boolean useFieldValueSelected = useFieldValueRadio.isSelected();

        fieldNameLabelHelpPanel.setVisible(useFieldValueSelected);
        fieldNameField.setVisible(useFieldValueSelected);

        boolean customExpressionSelected = customExpressionRadio.isSelected();

        customExpressionLabelHelpPanel.setVisible(customExpressionSelected);
        customExpressionField.setVisible(customExpressionSelected);

        boolean keyPreviewVisible = useFieldValueSelected || customExpressionSelected;

        keyPreviewTitledSeparator.setVisible(keyPreviewVisible);
        keyPreviewPanel.setVisible(keyPreviewVisible);

        try {
            Log.debug("Updating key form fields: ");

            if (useFieldValueSelected) {
                String[] sampleElementContentSplit = getSampleElementContentSplit(datasetField.getText());

                for (String field : possibleKeyFields) {
                    if (Arrays.stream(sampleElementContentSplit).anyMatch(element -> element.contains(field))) {
                        fieldNameField.setText(field);
                        break;
                    }
                }

            }
        } catch (Exception ex) {
            Log.error("Exception occurred: ", ex);
        }

    }

    protected void updateKeyPreview() {
        keyPreviewArea.setText("");
        if (cachedJsonDocs.isEmpty()) {
            cachedJsonDocs = readJsonFile();
        }

        StringBuilder previewContent = new StringBuilder();
        int monoIncrValue = 1;

        if (useFieldValueRadio.isSelected()) {
            String fieldName = fieldNameField.getText();
            for (int i = 0; i < Math.min(cachedJsonDocs.size(), 6); i++) {
                JsonObject jsonObject = JsonObject.fromJson(cachedJsonDocs.get(i));
                if (jsonObject.containsKey(fieldName)) {
                    previewContent.append(jsonObject.getString(fieldName)).append("\n");
                }
            }
        } else if (customExpressionRadio.isSelected()) {
            String expression = customExpressionField.getText();
            Pattern pattern = Pattern.compile("%(\\w+)%");
            Matcher matcher = pattern.matcher(expression);
            List<String> fieldNames = new ArrayList<>();
            while (matcher.find()) {
                fieldNames.add(matcher.group(1));
            }

            for (int i = 0; i < Math.min(cachedJsonDocs.size(), 6); i++) {
                JsonObject jsonObject = JsonObject.fromJson(cachedJsonDocs.get(i));

                String key = expression;
                for (String fieldName : fieldNames) {
                    if (jsonObject.containsKey(fieldName)) {
                        key = key.replace("%" + fieldName + "%", jsonObject.getString(fieldName));
                    }
                }

                key = key.replace("#UUID#", UUID.randomUUID().toString());
                key = key.replace("#MONO_INCR#", Integer.toString(monoIncrValue));
                monoIncrValue++;

                previewContent.append(key).append("\n");
            }
        }

        keyPreviewArea.setText(previewContent.toString());
        updateIgnoreFieldsTextField();
    }

    protected ArrayList<String> readJsonFile() {
        ArrayList<String> cachedJsonDocs = new ArrayList<>();

        try {
            String datasetPath = datasetField.getText();
            try (BufferedReader br = new BufferedReader(new FileReader(datasetPath))) {
                int counter = 0;
                String documentFound = "";

                while (true) {
                    String nextLine = br.readLine();

                    if (counter == 0) {
                        if (!nextLine.trim().startsWith("[")) {
                            System.out.println("Not a json array");
                            break;
                        }

                        nextLine = nextLine.replace("[", "");
                    }

                    if (nextLine == null || counter > 2000 || cachedJsonDocs.size() >= 10) {
                        break;
                    } else {
                        counter++;

                        documentFound += nextLine.trim();
                        if (isValidBrackets(documentFound)) {
                            documentFound = cleanString(documentFound);
                            cachedJsonDocs.add(documentFound);
                            documentFound = "";
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.error("Exception occurred", ex);
        }

        return cachedJsonDocs;
    }

    protected String cleanString(String input) {
        int firstOpenBracket = input.indexOf('{');
        int lastCloseBracket = input.lastIndexOf('}');

        if (firstOpenBracket == -1 || lastCloseBracket == -1 || firstOpenBracket > lastCloseBracket) {
            return input;
        }

        return input.substring(firstOpenBracket, lastCloseBracket + 1);
    }

    protected boolean isValidBrackets(String s) {
        Stack<Character> stack = new Stack<>();

        if (!s.contains("{") || !s.contains("}")) {
            return false;
        }
        for (char c : s.toCharArray()) {
            if (c == '{') {
                stack.push(c);
            } else if (c == '}') {
                if (stack.isEmpty() || stack.pop() != '{') {
                    return false;
                }
            }
        }

        return stack.isEmpty();
    }

    protected void updateIgnoreFieldsTextField() {
        String ignoreFieldsText = "";

        if (dynamicScopeAndCollectionRadio.isSelected()) {
            String fieldNameText = fieldNameField.getText();
            List<String> wordsWithPercentSymbols = new ArrayList<>();

            String scopeText = dynamicScopeField.getText();
            if (!scopeText.isEmpty()) {
                wordsWithPercentSymbols.addAll(extractWordsWithPercentSymbols(scopeText));
            }

            String collectionText = dynamicCollectionField.getText();
            if (!collectionText.isEmpty()) {
                wordsWithPercentSymbols.addAll(extractWordsWithPercentSymbols(collectionText));
            }

            ignoreFieldsText = fieldNameText + "," + String.join(",", wordsWithPercentSymbols);
        } else if (collectionRadio.isSelected()) {
            ignoreFieldsText = fieldNameField.getText();
        }

        ignoreFieldsText = ignoreFieldsText.replaceAll("^,+|,+$", "");

        ignoreFieldsField.setText(ignoreFieldsText);
    }

    protected List<String> extractWordsWithPercentSymbols(String text) {
        List<String> wordsWithPercentSymbols = new ArrayList<>();
        Matcher matcher = Pattern.compile("%(\\w+)%").matcher(text);
        while (matcher.find()) {
            wordsWithPercentSymbols.add(matcher.group(1));
        }
        return wordsWithPercentSymbols;
    }

    protected String[] getSampleElementContentSplit(String datasetFieldText) {
        String sampleElementContent = FileUtils.sampleElementFromJsonArrayFile(datasetFieldText);
        System.out.println("Sample element content in updating key form fields " + sampleElementContent);
        assert sampleElementContent != null;
        return sampleElementContent.split(",");
    }

    @Override
    protected JComponent createSouthPanel() {
        panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> doCancelAction());
        panel.add(cancelButton);

        backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            previousPage();
            backButton.setEnabled(currentPage > 1);
            backButton.setVisible(currentPage > 1);
            nextButton.setText(currentPage == 5 ? "Import" : "Next");
            validateAndEnableNextButton();
        });
        backButton.setEnabled(currentPage > 1);
        backButton.setVisible(currentPage > 1);
        panel.add(backButton);

        nextButton = new JButton("Next");
        nextButton.addActionListener(e -> {
            if (currentPage == 5) {
                doOKAction();
            } else {
                nextPage();
                backButton.setEnabled(currentPage > 1);
                backButton.setVisible(currentPage > 1);
                nextButton.setText(currentPage == 5 ? "Import" : "Next");
                validateAndEnableNextButton();
            }
        });
        nextButton.setEnabled(false);
        panel.add(nextButton);

        return panel;
    }

    protected void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            cardLayout.show(cardPanel, Integer.toString(currentPage));
        }
    }

    protected void nextPage() {
        if (currentPage < 5) {
            currentPage++;
            cardLayout.show(cardPanel, Integer.toString(currentPage));
            if (currentPage == 5) {
                calculateSummary();
            }
        }
    }

    private class CBImportCommandBuilder {
        private final List<String> commandList = new ArrayList<>();

        public CBImportCommandBuilder(String fileFormat, String clusterURL, String username, String password) {
            commandList.add(CBTools.getTool(CBTools.Type.CB_IMPORT).getPath());
            commandList.add(fileFormat);
            commandList.add("--no-ssl-verify");
            commandList.add("--cluster");
            commandList.add(clusterURL);
            commandList.add("--username");
            commandList.add(username);
            commandList.add("--password");
            commandList.add(password);
        }

        public CBImportCommandBuilder setBucket(String bucket) {
            commandList.add("--bucket");
            commandList.add(bucket);
            return this;
        }

        public CBImportCommandBuilder setDataset(String filePath) {
            commandList.add("--dataset");
            commandList.add("file://" + filePath);
            return this;
        }

        public CBImportCommandBuilder setFormat(String format) {
            if (format != null) {
                commandList.add("--format");
                commandList.add(format);
            }
            return this;
        }

        public CBImportCommandBuilder setFieldSeparator(String separator) {
            if (separator != null) {
                commandList.add("--field-separator");
                commandList.add(separator);
            }
            return this;
        }

        public CBImportCommandBuilder setInferTypes(String fileFormat) {
            if (fileFormat.equals("csv")) {
                commandList.add("--infer-types");
            }
            return this;
        }

        public CBImportCommandBuilder setScopeCollectionExp(ButtonModel targetLocationGroupSelection, String defaultExp,
                String collectionExp, String dynamicExp) {
            if (targetLocationGroupSelection == defaultScopeAndCollectionRadio.getModel()) {
                commandList.add("--scope-collection-exp");
                commandList.add(checkForDelimiters(defaultExp, '#'));
            } else if (targetLocationGroupSelection == collectionRadio.getModel()) {
                commandList.add("--scope-collection-exp");
                commandList.add(checkForDelimiters(collectionExp, '#'));
            } else if (targetLocationGroupSelection == dynamicScopeAndCollectionRadio.getModel()) {
                commandList.add("--scope-collection-exp");
                commandList.add(checkForDelimiters(dynamicExp, '#'));
            }
            return this;
        }

        public CBImportCommandBuilder setGenerateKey(ButtonModel keyGroupSelection, String uuidKey,
                String fieldValueKey, String customExpressionKey) {
            if (keyGroupSelection == generateUUIDRadio.getModel()) {
                commandList.add("--generate-key");
                commandList.add(checkForDelimiters(uuidKey, '#'));
            } else if (keyGroupSelection == useFieldValueRadio.getModel()) {
                commandList.add("--generate-key");
                commandList.add(checkForDelimiters(fieldValueKey, '#'));
            } else if (keyGroupSelection == customExpressionRadio.getModel()) {
                commandList.add("--generate-key");
                commandList.add(checkForDelimiters(customExpressionKey, '#'));
            }
            return this;
        }

        public CBImportCommandBuilder setSkipDocsOrRows(String skipValue, String flag) {
            if (skipValue != null && !skipValue.isEmpty()) {
                if (flag.equals("json")) {
                    commandList.add("--skip-docs");
                } else if (flag.equals("csv")) {
                    commandList.add("--skip-rows");
                }
                commandList.add(skipValue);
            }
            return this;
        }

        public CBImportCommandBuilder setLimitDocsOrRows(String limitValue, String flag) {
            if (limitValue != null && !limitValue.isEmpty()) {
                if (flag.equals("json")) {
                    commandList.add("--limit-docs");
                } else if (flag.equals("csv")) {
                    commandList.add("--limit-rows");
                }
                commandList.add(limitValue);
            }
            return this;
        }

        public CBImportCommandBuilder setIgnoreFields(String fields) {
            if (fields != null && !fields.isEmpty()) {
                commandList.add("--ignore-fields");
                commandList.add(checkForDelimiters("'" + fields + "'", '#'));
            }
            return this;
        }

        public CBImportCommandBuilder setThreads(int threads) {
            commandList.add("--threads");
            commandList.add(Integer.toString(threads));
            return this;
        }

        public void setVerbose() {
            commandList.add("--verbose");
        }

        public CBImportCommandBuilder setGeneratorDelimiter(String delimiter) {
            if (delimiter != null) {
                commandList.add("--generator-delimiter");
                commandList.add(delimiter);
            }
            return this;
        }

        public List<String> constructCommand() {
            return commandList;
        }
    }

    @Override
    protected void doOKAction() {
        try {
            // Get values from fields
            String bucket = Objects.requireNonNull(bucketCombo.getSelectedItem()).toString();
            String filePath = datasetField.getText();
            ButtonModel targetLocationGroupSelection = targetLocationGroup.getSelection();
            ButtonModel keyGroupSelection = keyGroup.getSelection();
            String dynamicScopeFieldText = dynamicScopeField.getText();
            String dynamicCollectionFieldText = dynamicCollectionField.getText();
            String fieldNameText = fieldNameField.getText();
            String customExpressionText = customExpressionField.getText();
            boolean skipFirstCheckSelected = skipFirstCheck.isSelected();
            String skipFirstFieldText = skipFirstField.getText();
            boolean importUptoCheckSelected = importUptoCheck.isSelected();
            String importUptoFieldText = importUptoField.getText();
            boolean ignoreFieldsCheckSelected = ignoreFieldsCheck.isSelected();
            String ignoreFieldsFieldText = ignoreFieldsField.getText();
            int threadsSpinnerValue = (int) threadsSpinner.getValue();
            boolean verboseCheckSelected = verboseCheck.isSelected();

            // Get values from ActiveCluster
            String clusterURL = ActiveCluster.getInstance().getClusterURL();
            String username = ActiveCluster.getInstance().getUsername();
            String password = ActiveCluster.getInstance().getPassword();

            // Create command list using CBImportCommandBuilder
            CBImportCommandBuilder builder = new CBImportCommandBuilder(fileFormat, clusterURL, username, password)
                    .setBucket(bucket)
                    .setDataset(filePath)
                    .setFormat(fileFormat.equals("json") ? detectedCouchbaseJsonFormat : null)
                    .setFieldSeparator(fileFormat.equals("csv") ? "," : null)
                    .setInferTypes(fileFormat)
                    .setScopeCollectionExp(targetLocationGroupSelection, "_default._default",
                            targetScopeField + "." + targetCollectionField,
                            dynamicScopeFieldText + "." + dynamicCollectionFieldText)
                    .setGenerateKey(keyGroupSelection, "#UUID#", "%" + fieldNameText + "%", customExpressionText)
                    .setGeneratorDelimiter("#")
                    .setSkipDocsOrRows(skipFirstCheckSelected ? skipFirstFieldText : null, fileFormat)
                    .setLimitDocsOrRows(importUptoCheckSelected ? importUptoFieldText : null, fileFormat)
                    .setIgnoreFields(ignoreFieldsCheckSelected ? ignoreFieldsFieldText : null)
                    .setThreads(threadsSpinnerValue);

            if (verboseCheckSelected) {
                builder.setVerbose();
            }

            List<String> commandList = builder.constructCommand();

            // Run the import command
            CBImport.complexBucketImport(bucket, filePath, project, fileFormat, commandList);
            super.doOKAction();
        } catch (Exception ex) {
            Log.error("Exception Occurred " + ex);
        }
    }

    public static String checkForDelimiters(String input, char c) {
        if (input.indexOf(c) != -1) {
            return "'" + input + "'";
        } else {
            return input;
        }
    }

}
