package com.couchbase.intellij.tools.dialog;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tools.CBImport;
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
import com.intellij.ui.components.*;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import utils.FileUtils;
import utils.TemplateUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.couchbase.intellij.tools.dialog.CBImportCommandBuilder.CSV_FILE_FORMAT;
import static com.couchbase.intellij.tools.dialog.CBImportCommandBuilder.JSON_FILE_FORMAT;

public class ImportDialog extends DialogWrapper {

    protected final Project project;

    protected TextFieldWithBrowseButton datasetField;

    protected ComboBox<String> targetBucketCombo;
    protected ComboBox<String> targetScopeCombo;
    protected ComboBox<String> targetCollectionCombo;

    protected JBRadioButton defaultScopeAndCollectionRadio;
    protected JBRadioButton targetCollectionRadio;
    protected JBRadioButton dynamicScopeAndCollectionRadio;

    protected JBTextField dynamicScopeField;
    protected JBTextField dynamicCollectionField;

    protected JBRadioButton generateUUIDRadio;
    protected JBRadioButton useFieldValueRadio;
    protected JBRadioButton customExpressionRadio;

    protected JBTextField useFieldNameField;
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

    protected final String[] possibleScopeFields = {"cbms", "scope", "cbs", "type", "category"};
    protected final String[] possibleCollectionFields = {"cbmc", "collection", "cbc", "subtype", "subcategory"};
    protected final String[] possibleKeyFields = {"cbmid", "id", "uuid", "name", "cbmk", "key", "cbk"};

    protected String targetScopeField;
    protected String targetCollectionField;
    protected String fileFormat;
    protected String detectedCouchbaseJsonFormat;

    protected List<ScopeSpec> targetScopeItems;
    protected ArrayList<String> cachedJsonDocs = new ArrayList<>();
    protected Map<String, String[]> cachedCsvDocs = new HashMap<>();

    protected static final int PREVIEW_SIZE = 6;
    protected static final String UUID_FLAG = "#UUID#";
    protected static final String MONO_INCR_FLAG = "#MONO_INCR#";
    protected static final String WORDS_WITH_PERCENT_SYMBOLS_REGEX = "%(\\w+)%";
    protected static final String JSON_FILE_EXTENSION = ".json";
    protected static final String CSV_FILE_EXTENSION = ".csv";

    protected static final String LINE_BREAK = "<br><br>";
    protected static final String DEFAULT_TAG = "_default";
    protected static final String IMPORT = "Import";
    protected static final String NEXT = "Next";
    protected static final String BACK = "Back";
    protected static final String CANCEL = "Cancel";

    public ImportDialog(Project project) {
        super(true);
        this.project = project;
        init();
        setTitle("Import Data");
        getWindow().setMinimumSize(new Dimension(600, 400));
        setResizable(true);
        setOKButtonText(IMPORT);
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

        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints d = new GridBagConstraints();
        d.fill = GridBagConstraints.HORIZONTAL;
        d.gridy = 0;
        d.weightx = 0.5;
        d.gridx = 0;
        d.insets = JBUI.insets(5);

        JPanel listHeadingPanel = TemplateUtil.getLabelWithHelp(
                "Lists",
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
                "<html>The list format specifies a file which contains a JSON list where<br>" +
                        "each element in the list is a JSON document. The file may only contain a<br>" +
                        "single list, but the list may be specified over multiple lines. This format<br>" +
                        "is specified by setting the --format option to \"list\". Below is an example<br>" +
                        "of a file in list format.</html>");

        JPanel linesHeadingPanel = TemplateUtil.getLabelWithHelp(
                "Lines",
                "<html>{\"key\": \"mykey1\", \"value\": \"myvalue1\"}<br>" +
                        "{\"key\": \"mykey2\", \"value\": \"myvalue2\"}<br>" +
                        "{\"key\": \"mykey3\", \"value\": \"myvalue3\"}<br>" +
                        "{\"key\": \"mykey4\", \"value\": \"myvalue4\"}<br>" +
                        "</html>");
        JLabel linesDefinitionLabel = new JLabel(
                "<html>The lines format specifies a file that contains one JSON document on<br>" +
                        "every line in the file. This format is specified by setting the --format<br>" +
                        "option to \"lines\". Below is an example of a file in lines format.</html>");

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

        datasetLabelHelpPanel = TemplateUtil.getLabelWithHelp(
                "Select the Dataset:",
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

        targetBucketCombo = new ComboBox<>(buckets);
        targetFormPanel.add(targetBucketCombo, c);

        c.gridy = 1;
        c.weightx = 0.3;
        c.gridx = 0;
        scopeAndCollectionLabelHelpPanel = TemplateUtil.getLabelWithHelp("Scope and Collection:",
                "<html>Select the scope and collection where you want to import the data. You can choose to import into the default scope and collection, a specific collection, or dynamically determine the scope and collection based on the data.</html>");
        targetFormPanel.add(scopeAndCollectionLabelHelpPanel, c);
        c.weightx = 0.7;
        c.gridx = 1;

        defaultScopeAndCollectionRadio = new JBRadioButton("Into the Default Scope and Collection");
        targetCollectionRadio = new JBRadioButton("Into a specified Collection");
        dynamicScopeAndCollectionRadio = new JBRadioButton("Dynamic scope and collection");

        targetLocationGroup = new ButtonGroup();
        targetLocationGroup.add(defaultScopeAndCollectionRadio);
        targetLocationGroup.add(targetCollectionRadio);
        targetLocationGroup.add(dynamicScopeAndCollectionRadio);

        defaultScopeAndCollectionRadio.setSelected(true);

        radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
        radioPanel.add(defaultScopeAndCollectionRadio);
        radioPanel.add(targetCollectionRadio);
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

        targetScopeItems = new ArrayList<>();
        targetScopeCombo = new ComboBox<>();

        targetFormPanel.add(targetScopeCombo, c);

        c.gridy = 3;
        c.weightx = 0.3;
        c.gridx = 0;
        collectionLabel = new JBLabel("Collection:");
        collectionLabelHelpPanel = TemplateUtil.getLabelWithHelp(collectionLabel,
                "<html>Select the collection where you want to import the data.</html>");
        targetFormPanel.add(collectionLabelHelpPanel, c);
        c.weightx = 0.7;
        c.gridx = 1;

        targetCollectionCombo = new ComboBox<>();

        targetFormPanel.add(targetCollectionCombo, c);

        c.gridy = 4;
        c.weightx = 0.3;
        c.gridx = 0;
        scopeFieldLabel = new JBLabel("Scope field:");
        scopeFieldLabelHelpPanel = TemplateUtil.getLabelWithHelp(scopeFieldLabel,
                "<html>Specify the field in the data that contains the scope name.<br>" +
                        "To use information from the JSON document, specify the column name between % characters. " +
                        "For example, --scope-collection-exp %scope_field%.%collection_field%. " +
                        "Fields that contain a % character may be escaped using %%." +
                        "For more information about the accepted format, see the SCOPE/COLLECTION PARSER section.</html>");
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
                "<html>Specify the field in the data that contains the collection name.<br>" +
                        "To use information from the JSON document, specify the column name between % characters. " +
                        "For example, --scope-collection-exp %scope_field%.%collection_field%. " +
                        "Fields that contain a % character may be escaped using %%." +
                        "For more information about the accepted format, see the SCOPE/COLLECTION PARSER section.</html>");
        targetFormPanel.add(collectionFieldLabelHelpPanel, c);
        c.weightx = 0.7;
        c.gridx = 1;

        dynamicCollectionField = new JBTextField();
        targetFormPanel.add(dynamicCollectionField, c);

        scopeLabelHelpPanel.setVisible(false);
        collectionLabelHelpPanel.setVisible(false);
        scopeFieldLabelHelpPanel.setVisible(false);
        collectionFieldLabelHelpPanel.setVisible(false);

        targetScopeCombo.setVisible(false);
        targetCollectionCombo.setVisible(false);
        dynamicScopeField.setVisible(false);
        dynamicCollectionField.setVisible(false);

        contentPanel.add(targetFormPanel);
        targetPanel.add(contentPanel, BorderLayout.NORTH);

        handleTargetBucketComboBoxChange();
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

        useFieldNameField = new JBTextField();
        keyFormPanel.add(useFieldNameField, c);

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
        keyPreviewArea.setPreferredSize(new Dimension(150, 200));
        keyPreviewArea.setMaximumSize(new Dimension(150, 300));

        JBScrollPane keyPreviewAreaScrollPane = new JBScrollPane(keyPreviewArea);
        keyPreviewAreaScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        keyPreviewPanel.add(keyPreviewAreaScrollPane, BorderLayout.CENTER);

        contentPanel.add(keyPreviewPanel);

        fieldNameLabelHelpPanel.setVisible(false);
        customExpressionLabelHelpPanel.setVisible(false);

        useFieldNameField.setVisible(false);
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
        summary.append(LINE_BREAK);

        summary.append("<b>Bucket:</b> ");
        summary.append(targetBucketCombo.getSelectedItem());
        summary.append(LINE_BREAK);

        summary.append("<b>Scope and Collection:</b> ");
        if (defaultScopeAndCollectionRadio.isSelected()) {
            summary.append("Default Scope and Collection");
        } else if (targetCollectionRadio.isSelected()) {
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
        summary.append(LINE_BREAK);

        summary.append("<b>Document Key:</b> ");
        if (generateUUIDRadio.isSelected()) {
            summary.append("Generate random UUID for each document");
        } else if (useFieldValueRadio.isSelected()) {
            summary.append("Use the value of a field as the key - Field Name: ");
            summary.append(useFieldNameField.getText());
        } else if (customExpressionRadio.isSelected()) {
            summary.append("Generate key based on custom expression - Expression: ");
            summary.append(customExpressionField.getText());
        }
        summary.append(LINE_BREAK);

        summary.append("<b>Advanced Options:</b><br>");
        if (skipFirstCheck.isSelected()) {
            if (fileFormat.equals(JSON_FILE_FORMAT)) {
                summary.append("- Skip the first ");
                summary.append(skipFirstField.getText());
                summary.append(" documents<br>");
            } else if (fileFormat.equals(CSV_FILE_FORMAT)) {
                summary.append("- Skip the first ");
                summary.append(skipFirstField.getText());
                summary.append(" rows<br>");
            }
        }
        if (importUptoCheck.isSelected()) {
            if (fileFormat.equals(JSON_FILE_FORMAT)) {
                summary.append("- Import up to ");
                summary.append(importUptoField.getText());
                summary.append(" documents<br>");
            } else if (fileFormat.equals(CSV_FILE_FORMAT)) {
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

    private void resetForm() {

        // Reset target panel
        targetBucketCombo.setSelectedIndex(0);
        defaultScopeAndCollectionRadio.setSelected(true);
        targetScopeCombo.setSelectedItem(null);
        targetCollectionCombo.setSelectedItem(null);
        dynamicScopeField.setText("");
        dynamicCollectionField.setText("");
        scopeLabelHelpPanel.setVisible(false);
        collectionLabelHelpPanel.setVisible(false);
        scopeFieldLabelHelpPanel.setVisible(false);
        collectionFieldLabelHelpPanel.setVisible(false);
        targetScopeCombo.setVisible(false);
        targetCollectionCombo.setVisible(false);
        dynamicScopeField.setVisible(false);
        dynamicCollectionField.setVisible(false);
        handleTargetBucketComboBoxChange();

        // Reset key panel
        generateUUIDRadio.setSelected(true);
        useFieldValueRadio.setSelected(false);
        customExpressionRadio.setSelected(false);
        fieldNameLabelHelpPanel.setVisible(false);
        customExpressionLabelHelpPanel.setVisible(false);
        useFieldNameField.setVisible(false);
        customExpressionField.setVisible(false);
        keyPreviewPanel.setVisible(false);
        keyPreviewTitledSeparator.setVisible(false);
        customExpressionInfoLabel.setVisible(false);
        useFieldNameField.setText("");
        customExpressionField.setText("");

        // Reset advanced panel
        skipFirstCheck.setSelected(false);
        skipFirstField.setText("");
        importUptoCheck.setSelected(false);
        importUptoField.setText("");
        ignoreFieldsCheck.setSelected(false);
        ignoreFieldsField.setText("");
        threadsSpinner.setValue(4);
        verboseCheck.setSelected(false);

        // Reset summary panel
        summaryLabel.setText("");
    }

    protected void addListeners() {
        // Page 1: Dataset
        datasetField.addBrowseFolderListener("Select the Dataset", "", null,
                new FileChooserDescriptor(true, false, false, false, false, false) {
                    @Override
                    public boolean isFileVisible(VirtualFile file, boolean showHiddenFiles) {
                        if (!file.isDirectory()) {
                            String extension = file.getExtension();
                            return JSON_FILE_FORMAT.equalsIgnoreCase(extension)
                                    || CSV_FILE_FORMAT.equalsIgnoreCase(extension);
                        }
                        return true;
                    }
                });
        datasetField.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                Log.debug("Dataset field changed: " + datasetField.getText());
                String filePath = datasetField.getText();

                if (filePath.endsWith(JSON_FILE_EXTENSION)) {
                    fileFormat = JSON_FILE_FORMAT;
                } else if (filePath.endsWith(CSV_FILE_EXTENSION)) {
                    fileFormat = CSV_FILE_FORMAT;
                }

                validateAndEnableNextButton();
                updateTargetOrDyanmicOptions();
                cachedJsonDocs.clear();
                cachedCsvDocs.clear();
                resetForm();
            }
        });

        // Page 2: Target Location
        ActionListener radioListener = e -> {
            updateTargetOrDyanmicOptions();
            validateAndEnableNextButton();
        };
        defaultScopeAndCollectionRadio.addActionListener(radioListener);
        targetCollectionRadio.addActionListener(radioListener);
        dynamicScopeAndCollectionRadio.addActionListener(radioListener);

        DocumentAdapter documentAdapter = new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                validateAndEnableNextButton();
            }
        };
        dynamicScopeField.getDocument().addDocumentListener(documentAdapter);
        dynamicCollectionField.getDocument().addDocumentListener(documentAdapter);

        targetBucketCombo.addActionListener(e -> handleTargetBucketComboBoxChange());
        targetScopeCombo.addActionListener(e -> handleTargetScopeComboBoxChange());

        ActionListener comboListener = e -> validateAndEnableNextButton();
        targetBucketCombo.addActionListener(comboListener);
        targetScopeCombo.addActionListener(comboListener);
        targetCollectionCombo.addActionListener(comboListener);

        targetScopeCombo.addActionListener(e -> {
            Log.debug("Scope combo box changed: "
                    + Optional.ofNullable(targetScopeCombo.getSelectedItem()).map(Object::toString)
                    .orElse(null));
            targetScopeField = Optional.ofNullable(targetScopeCombo.getSelectedItem())
                    .map(Object::toString)
                    .orElse(DEFAULT_TAG);
        });
        targetCollectionCombo.addActionListener(e -> {
            Log.debug("Collection combo box changed: "
                    + Optional.ofNullable(targetCollectionCombo.getSelectedItem())
                    .map(Object::toString).orElse(null));
            targetCollectionField = Optional
                    .ofNullable(targetCollectionCombo.getSelectedItem())
                    .map(Object::toString)
                    .orElse(DEFAULT_TAG);
        });

        // Page 3: Document Key
        ActionListener keyRadioListener = e -> {
            updateKeyFormFields();
            validateAndEnableNextButton();
        };
        generateUUIDRadio.addActionListener(keyRadioListener);
        useFieldValueRadio.addActionListener(e -> {
            updateKeyFormFields();
            validateAndEnableNextButton();
        });
        customExpressionRadio.addActionListener(keyRadioListener);

        useFieldNameField.getDocument().addDocumentListener(new DocumentAdapter() {

            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                Log.debug("Field name field changed: " + useFieldNameField.getText());
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

        ignoreFieldsCheck.addActionListener(checkListener);
        ignoreFieldsField.getDocument().addDocumentListener(documentAdapter);

    }

    private void enableFields() {
        skipFirstField.setEnabled(skipFirstCheck.isSelected());
        importUptoField.setEnabled(importUptoCheck.isSelected());
        ignoreFieldsField.setEnabled(ignoreFieldsCheck.isSelected());
    }

    protected void handleTargetBucketComboBoxChange() {
        try {
            String selectedBucket = Optional.ofNullable(targetBucketCombo.getSelectedItem())
                    .map(Object::toString)
                    .orElse(null);
            Log.debug("Bucket combo box changed: " + selectedBucket);

            targetScopeItems.clear();
            targetScopeItems.addAll(ActiveCluster.getInstance().get().bucket(selectedBucket)
                    .collections().getAllScopes());

            Set<String> scopeSet = targetScopeItems.stream()
                    .map(ScopeSpec::name)
                    .collect(Collectors.toSet());
            targetScopeCombo.removeAllItems();
            for (String scopeItem : scopeSet) {
                targetScopeCombo.addItem(scopeItem);
            }

            if (targetScopeCombo.getItemCount() > 0) {
                targetScopeCombo.setSelectedIndex(0);
                targetScopeField = Objects.requireNonNull(targetScopeCombo.getSelectedItem()).toString();
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

                        targetScopeCombo.addItem(scopeName);
                        targetScopeCombo.setSelectedItem(scopeName);
                    }
                } else {
                    targetScopeCombo.setSelectedItem(DEFAULT_TAG);
                    targetCollectionCombo.setSelectedItem(DEFAULT_TAG);
                }
            }

            handleTargetScopeComboBoxChange();
        } catch (Exception ex) {
            Log.error("Exception occurred" + ex);
        }
    }

    protected void handleTargetScopeComboBoxChange() {
        try {
            String selectedScope = Optional.ofNullable(targetScopeCombo.getSelectedItem())
                    .map(Object::toString)
                    .orElse(null);
            Log.debug("Scope combo box changed: " + selectedScope);

            if (selectedScope == null || selectedScope.isEmpty()) {
                targetScopeCombo.setSelectedItem(DEFAULT_TAG);
                targetCollectionCombo.setSelectedItem(DEFAULT_TAG);
                return;
            }

            targetCollectionCombo.removeAllItems();

            Consumer<String> refreshCollectionCombo = scope -> {
                String[] collectionItems = targetScopeItems.stream()
                        .filter(s -> s.name().equals(scope))
                        .flatMap(s -> s.collections().stream())
                        .map(CollectionSpec::name)
                        .distinct()
                        .toArray(String[]::new);

                for (String collectionItem : collectionItems) {
                    targetCollectionCombo.addItem(collectionItem);
                }
            };

            refreshCollectionCombo.accept(selectedScope);

            if (targetCollectionCombo.getItemCount() > 0) {
                targetCollectionCombo.setSelectedIndex(0);
                targetCollectionField = Objects.requireNonNull(targetCollectionCombo.getSelectedItem()).toString();
            } else {
                int result = Messages.showYesNoDialog(project,
                        "The selected scope " + selectedScope
                                + " is empty. Would you like to create a new collection in this scope?",
                        "Empty Scope", Messages.getQuestionIcon());

                if (result == Messages.YES && !ActiveCluster.getInstance().isReadOnlyMode()) {
                    NewEntityCreationDialog entityCreationDialog = new NewEntityCreationDialog(
                            project,
                            EntityType.COLLECTION,
                            Objects.requireNonNull(targetBucketCombo.getSelectedItem()).toString(),
                            selectedScope);
                    entityCreationDialog.show();

                    if (entityCreationDialog.isOK()) {
                        String collectionName = entityCreationDialog.getEntityName();
                        ActiveCluster.getInstance().get()
                                .bucket(targetBucketCombo.getSelectedItem().toString()).collections()
                                .createCollection(CollectionSpec.create(collectionName, selectedScope));

                        targetCollectionCombo.addItem(collectionName);
                        targetCollectionCombo.setSelectedItem(collectionName);
                    }
                } else {
                    targetScopeCombo.setSelectedItem(DEFAULT_TAG);
                    refreshCollectionCombo.accept(DEFAULT_TAG);
                    targetCollectionCombo.setSelectedIndex(0);
                    targetCollectionField = Objects.requireNonNull(targetCollectionCombo.getSelectedItem()).toString();
                }
            }
        } catch (Exception ex) {
            Log.error("Exception Occurred " + ex);
        }
    }

    protected void updateTargetOrDyanmicOptions() {
        boolean targetCollectionSelected = targetCollectionRadio.isSelected();

        scopeLabelHelpPanel.setVisible(targetCollectionSelected);
        targetScopeCombo.setVisible(targetCollectionSelected);

        collectionLabelHelpPanel.setVisible(targetCollectionSelected);
        targetCollectionCombo.setVisible(targetCollectionSelected);

        boolean dynamicSelected = dynamicScopeAndCollectionRadio.isSelected();

        scopeFieldLabelHelpPanel.setVisible(dynamicSelected);
        dynamicScopeField.setVisible(dynamicSelected);

        collectionFieldLabelHelpPanel.setVisible(dynamicSelected);
        dynamicCollectionField.setVisible(dynamicSelected);

        try {
            if (defaultScopeAndCollectionRadio.isSelected()) {
                Log.debug("Default scope and collection Radio selected");

                targetScopeField = DEFAULT_TAG;
                targetCollectionField = DEFAULT_TAG;
            } else if (targetCollectionSelected) {
                Log.debug("collection Radio selected");

            } else if (dynamicSelected) {
                Log.debug("Dynamic scope and collection Radio selected");
                String[] sampleElementContentSplit = getSampleElementContentSplit(datasetField.getText());

                BiConsumer<String[], JTextField> setFirstMatch = (fields, textField) -> {
                    Optional<String> firstMatch = Arrays.stream(fields)
                            .filter(field -> {
                                if (fileFormat.equals(JSON_FILE_FORMAT)) {
                                    return Arrays.stream(sampleElementContentSplit)
                                            .anyMatch(element -> element.contains('"' + field + '"'));
                                } else if (fileFormat.equals(CSV_FILE_FORMAT)) {
                                    return Arrays.stream(sampleElementContentSplit)
                                            .anyMatch(element -> element.contains(field));
                                }
                                return false;
                            })
                            .findFirst();
                    firstMatch.ifPresent(field -> textField.setText("%" + field + "%"));
                };

                setFirstMatch.accept(possibleScopeFields, dynamicScopeField);
                setFirstMatch.accept(possibleCollectionFields, dynamicCollectionField);

            }
        } catch (Exception ex) {
            Log.error("Exception occurred: ", ex);
        }
    }

    protected void updateKeyFormFields() {
        boolean useFieldValueSelected = useFieldValueRadio.isSelected();

        fieldNameLabelHelpPanel.setVisible(useFieldValueSelected);
        useFieldNameField.setVisible(useFieldValueSelected);

        boolean customExpressionSelected = customExpressionRadio.isSelected();

        customExpressionLabelHelpPanel.setVisible(customExpressionSelected);
        customExpressionField.setVisible(customExpressionSelected);

        boolean keyPreviewVisible = useFieldValueSelected || customExpressionSelected;

        keyPreviewTitledSeparator.setVisible(keyPreviewVisible);
        keyPreviewPanel.setVisible(keyPreviewVisible);

        try {
            Log.debug("Updating key form fields: ");
            String[] sampleElementContentSplit = getSampleElementContentSplit(datasetField.getText());

            for (String field : possibleKeyFields) {
                if (Arrays.stream(sampleElementContentSplit).anyMatch(element -> {
                    if (fileFormat.equals(JSON_FILE_FORMAT)) {
                        return element.contains('"' + field + '"');
                    } else if (fileFormat.equals(CSV_FILE_FORMAT)) {
                        return element.contains(field);
                    }
                    return false;
                })) {
                    if (useFieldValueSelected) {
                        useFieldNameField.setText(field);
                        break;
                    } else if (customExpressionSelected) {
                        customExpressionField.setText("%" + field + "%");
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
        if ((fileFormat.equals(JSON_FILE_FORMAT) && cachedJsonDocs.isEmpty())
                || (fileFormat.equals(CSV_FILE_FORMAT) && cachedCsvDocs.isEmpty())) {
            readJsonOrCsvFile();
        }

        StringBuilder previewContent = new StringBuilder();
        int monoIncrValue = 1;

        if (useFieldValueRadio.isSelected()) {
            String fieldName = useFieldNameField.getText();
            if (fileFormat.equals(JSON_FILE_FORMAT)) {
                for (int i = 0; i < Math.min(cachedJsonDocs.size(), PREVIEW_SIZE); i++) {
                    JsonObject jsonObject = JsonObject.fromJson(cachedJsonDocs.get(i));
                    if (jsonObject.containsKey(fieldName)) {
                        previewContent.append(jsonObject.get(fieldName).toString()).append("\n");
                    }
                }
            } else if (fileFormat.equals(CSV_FILE_FORMAT) && (cachedCsvDocs.get(fieldName) != null)) {
                for (int i = 0; i < Math.min(cachedCsvDocs.size(), PREVIEW_SIZE); i++) {
                    previewContent.append(cachedCsvDocs.get(fieldName)[i]).append("\n");
                }

            }
        } else if (customExpressionRadio.isSelected()) {

            if (fileFormat.equals(JSON_FILE_FORMAT)) {
                String expression = customExpressionField.getText();
                Pattern pattern = Pattern.compile(WORDS_WITH_PERCENT_SYMBOLS_REGEX);
                Matcher matcher = pattern.matcher(expression);
                List<String> fieldNamesList = new ArrayList<>();
                while (matcher.find()) {
                    fieldNamesList.add(matcher.group(1));
                }
                for (int i = 0; i < Math.min(cachedJsonDocs.size(), PREVIEW_SIZE); i++) {
                    JsonObject jsonObject = JsonObject.fromJson(cachedJsonDocs.get(i));
                    String keyBuilder = expression;
                    for (String fieldName : fieldNamesList) {
                        if (jsonObject.containsKey(fieldName)) {
                            keyBuilder = keyBuilder.replace("%" + fieldName + "%",
                                    String.valueOf(jsonObject.get(fieldName)));
                        }
                    }

                    keyBuilder = keyBuilder.replace(UUID_FLAG, UUID.randomUUID().toString());
                    keyBuilder = keyBuilder.replace(MONO_INCR_FLAG, Integer.toString(monoIncrValue));
                    monoIncrValue++;

                    previewContent.append(keyBuilder).append("\n");
                }
            } else if (fileFormat.equals(CSV_FILE_FORMAT)) {
                String expression = customExpressionField.getText();
                Pattern pattern = Pattern.compile(WORDS_WITH_PERCENT_SYMBOLS_REGEX);
                Matcher matcher = pattern.matcher(expression);
                List<String> fieldNamesList = new ArrayList<>();
                while (matcher.find()) {
                    fieldNamesList.add(matcher.group(1));
                }

                for (int i = 0; i < Math.min(cachedCsvDocs.size(), PREVIEW_SIZE); i++) {
                    String keyBuilder = expression;
                    for (String fieldName : fieldNamesList) {
                        if (cachedCsvDocs.get(fieldName) != null) {
                            keyBuilder = keyBuilder.replace("%" + fieldName + "%",
                                    String.valueOf(cachedCsvDocs.get(fieldName)[i]));
                        }
                    }

                    keyBuilder = keyBuilder.replace(UUID_FLAG, UUID.randomUUID().toString());
                    keyBuilder = keyBuilder.replace(MONO_INCR_FLAG, Integer.toString(monoIncrValue));
                    monoIncrValue++;

                    previewContent.append(keyBuilder).append("\n");
                }

            }
        }

        keyPreviewArea.setText(previewContent.toString());

    }

    protected void readJsonOrCsvFile() {
        cachedJsonDocs = new ArrayList<>();
        cachedCsvDocs = new HashMap<>();

        try {
            String datasetPath = datasetField.getText();
            if (datasetPath.endsWith(JSON_FILE_EXTENSION)) {
                try (BufferedReader br = new BufferedReader(new FileReader(datasetPath))) {
                    int counter = 0;
                    String documentFound = "";

                    while (true) {
                        String nextLine = br.readLine();

                        if (counter == 0) {
                            if (!nextLine.trim().startsWith("[")) {
                                Log.debug("Not a json array");
                                break;
                            }

                            nextLine = nextLine.replace("[", "");
                        }

                        if (nextLine == null || counter > 2000 || cachedJsonDocs.size() >= PREVIEW_SIZE) {
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
            } else if (datasetPath.endsWith(CSV_FILE_EXTENSION)) {
                String[] headers = FileUtils.sampleElementFromCsvFile(datasetPath, 1);

                for (int lineNumber = 2; lineNumber < 2 + PREVIEW_SIZE; lineNumber++) {
                    String[] data = FileUtils.sampleElementFromCsvFile(datasetPath, lineNumber);
                    for (int headersIndex = 0; headersIndex < Objects.requireNonNull(headers).length; headersIndex++) {
                        if (!cachedCsvDocs.containsKey(headers[headersIndex])) {
                            cachedCsvDocs.put(headers[headersIndex], new String[PREVIEW_SIZE]);
                        }
                        cachedCsvDocs.get(headers[headersIndex])[lineNumber - 2] = Objects
                                .requireNonNull(data)[headersIndex];
                    }
                }

                for (Map.Entry<String, String[]> entry : cachedCsvDocs.entrySet()) {
                    String header = entry.getKey();
                    String[] data = entry.getValue();

                    Log.debug(header + " -> " + String.join(",", data));
                }
            }

        } catch (Exception ex) {
            Log.error("Exception occurred", ex);
        }

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
        Deque<Character> deque = new ArrayDeque<>();

        if (!s.contains("{") || !s.contains("}")) {
            return false;
        }

        for (char c : s.toCharArray()) {
            if (c == '{') {
                deque.push(c);
            } else if (c == '}' && (deque.isEmpty() || deque.pop() != '{')) {
                return false;
            }
        }

        return deque.isEmpty();
    }

    protected void updateIgnoreFieldsTextField() {
        String ignoreFieldsText = "";
        String useFieldNameFieldText = useFieldNameField.getText();
        List<String> wordsWithPercentSymbols = new ArrayList<>();

        // Fields from Page 2
        if (dynamicScopeAndCollectionRadio.isSelected()) {

            String scopeText = dynamicScopeField.getText();
            if (!scopeText.isEmpty()) {
                wordsWithPercentSymbols.addAll(extractWordsWithPercentSymbols(scopeText));
            }

            String collectionText = dynamicCollectionField.getText();
            if (!collectionText.isEmpty()) {
                wordsWithPercentSymbols.addAll(extractWordsWithPercentSymbols(collectionText));
            }

            ignoreFieldsText = String.join(",", wordsWithPercentSymbols);
        }

        // Fields fromv Page 3
        if (useFieldValueRadio.isSelected()) {
            if (!useFieldNameFieldText.isEmpty()) {
                ignoreFieldsText = ignoreFieldsText + "," + useFieldNameFieldText;
            }
        } else if (customExpressionRadio.isSelected()) {
            String customExpressionText = customExpressionField.getText();
            if (!customExpressionText.isEmpty()) {
                wordsWithPercentSymbols.addAll(extractWordsWithPercentSymbols(customExpressionText));
            }
            ignoreFieldsText = ignoreFieldsText + "," + String.join(",", wordsWithPercentSymbols);
        }

        if (fileFormat.equals(CSV_FILE_FORMAT)) {
            Set<String> headerSetForKeyGeneration = new HashSet<>();

            headerSetForKeyGeneration.addAll(extractWordsWithPercentSymbols(dynamicScopeField.getText()));
            headerSetForKeyGeneration
                    .addAll(extractWordsWithPercentSymbols(dynamicCollectionField.getText()));

            if (useFieldValueRadio.isSelected()) {
                headerSetForKeyGeneration
                        .add(useFieldNameField.getText());
            } else if (customExpressionRadio.isSelected()) {
                headerSetForKeyGeneration
                        .addAll(extractWordsWithPercentSymbols(customExpressionField.getText()));
            }

            String[] ignoreFieldsWordsArray = ignoreFieldsText.split(",");
            List<String> filteredWords = new ArrayList<>();

            for (String word : ignoreFieldsWordsArray) {
                if (!headerSetForKeyGeneration.contains(word)) {
                    filteredWords.add(word);
                }
            }

            ignoreFieldsText = String.join(",", filteredWords);
        }

        // Remove Leading and Trailing commas
        ignoreFieldsText = ignoreFieldsText.replaceAll("^(,)+|(,)+$", "");

        // In a comma-separated string, remove duplicate words
        String[] ignoreFieldsWordsArray = ignoreFieldsText.split(",");
        Set<String> uniqueWords = new HashSet<>(Arrays.asList(ignoreFieldsWordsArray));
        ignoreFieldsText = String.join(",", uniqueWords);

        if (!ignoreFieldsText.isEmpty()) {
            ignoreFieldsCheck.setSelected(true);
            ignoreFieldsField.setEnabled(true);
            ignoreFieldsField.setText(ignoreFieldsText);
        }
    }

    protected List<String> extractWordsWithPercentSymbols(String text) {
        List<String> wordsWithPercentSymbols = new ArrayList<>();
        Matcher matcher = Pattern.compile(WORDS_WITH_PERCENT_SYMBOLS_REGEX).matcher(text);
        while (matcher.find()) {
            wordsWithPercentSymbols.add(matcher.group(1));
        }
        return wordsWithPercentSymbols;
    }

    protected String[] getSampleElementContentSplit(String datasetFieldText) {
        String[] sampleElementContentSplit;

        if (JSON_FILE_FORMAT.equalsIgnoreCase(fileFormat)) {
            sampleElementContentSplit = Objects
                    .requireNonNull(FileUtils.sampleElementFromJsonArrayFile(datasetFieldText)).split(",");
        } else if (CSV_FILE_FORMAT.equalsIgnoreCase(fileFormat)) {
            sampleElementContentSplit = FileUtils.sampleElementFromCsvFile(datasetFieldText, 1);
        } else {
            throw new IllegalArgumentException("Unsupported file format: " + fileFormat);
        }

        Log.debug("sampleElementContentSplit: " + Arrays.toString(sampleElementContentSplit));

        return sampleElementContentSplit;
    }

    protected void validateAndEnableNextButton() {
        try {
            boolean isValid = true;
            List<String> errorMessages = new ArrayList<>();
            if (currentPage == 1) {
                try {
                    String datasetText = datasetField.getText();
                    boolean isValidDataset = !(datasetText.isEmpty()
                            || !(datasetText.endsWith(JSON_FILE_EXTENSION)
                            || datasetText.endsWith(CSV_FILE_EXTENSION)));
                    highlightField(datasetField, isValidDataset);
                    if (!isValidDataset) {
                        isValid = false;
                        Log.debug("Validation failed: Dataset field is empty or does not have a valid file extension");
                        errorMessages.add("Please select a valid file.");
                    } else {
                        if (fileFormat.equals(JSON_FILE_FORMAT)) {
                            detectedCouchbaseJsonFormat = FileUtils.detectDatasetFormat(datasetText);
                            if (detectedCouchbaseJsonFormat == null) {
                                highlightField(datasetField, false);
                                isValid = false;
                                Log.debug("Validation failed: Dataset file is not in a valid Couchbase JSON format");
                                errorMessages.add("Dataset file is not in a valid Couchbase JSON format.");
                            }
                        } else if (fileFormat.equals(CSV_FILE_FORMAT)) {
                            highlightField(datasetField, true);
                        }
                    }
                } catch (Exception ex) {
                    highlightField(datasetField, false);
                    isValid = false;
                    Log.debug("An error occurred while validating the dataset file: " + ex);
                    errorMessages.add("An error occurred while validating the dataset file.");
                }
            } else if (currentPage == 2) {

                if (targetCollectionRadio.isSelected()) {
                    if (targetScopeCombo.getSelectedItem() == null) {
                        isValid = false;
                        Log.debug("Validation failed: Scope combo box not selected");
                        errorMessages.add("Scope combo box not selected.");
                    }
                    if (targetCollectionCombo.getSelectedItem() == null) {
                        isValid = false;
                        Log.debug("Validation failed: Collection combo box not selected");
                        errorMessages.add("Collection combo box not selected.");
                    }
                } else if (dynamicScopeAndCollectionRadio.isSelected()) {
                    String dynamicScopeText = dynamicScopeField.getText();
                    String dynamicCollectionText = dynamicCollectionField.getText();
                    boolean isDynamicScopeEmpty = !dynamicScopeText.isEmpty();
                    boolean isDynamicCollectionEmpty = !dynamicCollectionText.isEmpty();
                    highlightField(dynamicScopeField, isDynamicScopeEmpty);
                    highlightField(dynamicCollectionField, isDynamicCollectionEmpty);
                    if (!isDynamicScopeEmpty || !isDynamicCollectionEmpty) {
                        isValid = false;
                        Log.debug("Validation failed: Dynamic scope and/or collection fields are empty");
                        errorMessages.add("Dynamic scope and/or collection fields are empty.");
                    }

                    if (fileFormat.equals(JSON_FILE_FORMAT) || fileFormat.equals(CSV_FILE_FORMAT)) {
                        String scopeFieldText = dynamicScopeField.getText();
                        boolean isValidDynamicScope = FileUtils.checkFields(
                                datasetField.getText(), scopeFieldText, fileFormat);

                        highlightField(dynamicScopeField, isValidDynamicScope);

                        String collectionFieldText = dynamicCollectionField.getText();
                        boolean isValidDynamicCollection = FileUtils.checkFields(
                                datasetField.getText(), collectionFieldText, fileFormat);

                        highlightField(dynamicCollectionField, isValidDynamicCollection);

                        if (!isValidDynamicScope || !isValidDynamicCollection) {
                            isValid = false;
                            Log.debug("Validation failed: Scope and/or Collection fields are not valid");
                            errorMessages.add("Scope and/or Collection fields are not valid.");
                        }
                    }

                    String regex = "^[\\w%\\-]+$";
                    if (!dynamicScopeText.matches(regex) || !dynamicCollectionText.matches(regex)) {
                        isValid = false;
                        Log.debug("Validation failed: Scope and/or Collection fields do not match the regex");
                        errorMessages.add("Scope and/or Collection fields do not match the regex.");
                    }
                } else if (!defaultScopeAndCollectionRadio.isSelected()
                        && !targetCollectionRadio.isSelected()
                        && !dynamicScopeAndCollectionRadio.isSelected()) {
                    isValid = false;
                    Log.debug("Validation failed: No target location radio box selected");
                    errorMessages.add("No target location radio box selected.");
                }
            } else if (currentPage == 3) {
                if (useFieldValueRadio.isSelected()) {
                    String fieldNameText = useFieldNameField.getText();
                    boolean isValidFieldName = !fieldNameText.isEmpty();
                    highlightField(useFieldNameField, isValidFieldName);
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
                        customExpressionInfoLabel.setVisible(customExpressionText.contains(UUID_FLAG)
                                || customExpressionText.contains(MONO_INCR_FLAG));
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
                    boolean isValidIgnoreFields = true;

                    // Check for emptiness
                    if (ignoreFieldsText.isEmpty()) {
                        isValidIgnoreFields = false;
                        errorMessages.add("Ignore fields field is empty.");
                    }

                    if (fileFormat.equals(CSV_FILE_FORMAT)) {
                        Set<String> headerSetForKeyGeneration = new HashSet<>();

                        headerSetForKeyGeneration.addAll(extractWordsWithPercentSymbols(dynamicScopeField.getText()));
                        headerSetForKeyGeneration
                                .addAll(extractWordsWithPercentSymbols(dynamicCollectionField.getText()));

                        if (useFieldValueRadio.isSelected()) {
                            headerSetForKeyGeneration
                                    .add(useFieldNameField.getText());
                        } else if (customExpressionRadio.isSelected()) {
                            headerSetForKeyGeneration
                                    .addAll(extractWordsWithPercentSymbols(customExpressionField.getText()));
                        }

                        for (String word : ignoreFieldsText.split(",")) {
                            if (headerSetForKeyGeneration.contains(word)) {
                                isValidIgnoreFields = false;
                                errorMessages.add(
                                        "Ignore fields cannot be headers used in dyanmic scope/collection field or key generation");
                                break;
                            }
                        }
                    }

                    highlightField(ignoreFieldsField, isValidIgnoreFields);

                    if (!isValidIgnoreFields) {
                        isValid = false;
                        Log.debug("Validation failed: Invalid ignore fields");
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

    @Override
    protected JComponent createSouthPanel() {
        panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = new JButton(CANCEL);
        cancelButton.addActionListener(e -> doCancelAction());
        panel.add(cancelButton);

        backButton = new JButton(BACK);
        backButton.addActionListener(e -> {
            previousPage();
            backButton.setEnabled(currentPage > 1);
            backButton.setVisible(currentPage > 1);
            nextButton.setText(currentPage == 5 ? IMPORT : NEXT);
            validateAndEnableNextButton();
        });
        backButton.setEnabled(currentPage > 1);
        backButton.setVisible(currentPage > 1);
        panel.add(backButton);

        nextButton = new JButton(NEXT);
        nextButton.addActionListener(e -> {
            if (currentPage == 5) {
                doOKAction();
            } else {
                nextPage();
                backButton.setEnabled(currentPage > 1);
                backButton.setVisible(currentPage > 1);
                nextButton.setText(currentPage == 5 ? IMPORT : NEXT);
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
            if (currentPage == 3) {
                updateKeyFormFields();
            } else if (currentPage == 4) {
                updateIgnoreFieldsTextField();
                String ignoreFieldsFieldText = ignoreFieldsField.getText();
                ignoreFieldsCheck.setSelected(!ignoreFieldsFieldText.isEmpty());
                ignoreFieldsField.setEnabled(!ignoreFieldsFieldText.isEmpty());
            } else if (currentPage == 5) {
                calculateSummary();
            }
        }
    }


    @Override
    protected void doOKAction() {
        try {
            String bucket = Objects.requireNonNull(targetBucketCombo.getSelectedItem()).toString();
            String filePath = datasetField.getText();
            ButtonModel targetLocationGroupSelection = targetLocationGroup.getSelection();
            ButtonModel keyGroupSelection = keyGroup.getSelection();
            String dynamicScopeFieldText = dynamicScopeField.getText();
            String dynamicCollectionFieldText = dynamicCollectionField.getText();
            String fieldNameText = useFieldNameField.getText();
            String customExpressionText = customExpressionField.getText();
            boolean skipFirstCheckSelected = skipFirstCheck.isSelected();
            String skipFirstFieldText = skipFirstField.getText();
            boolean importUptoCheckSelected = importUptoCheck.isSelected();
            String importUptoFieldText = importUptoField.getText();
            boolean ignoreFieldsCheckSelected = ignoreFieldsCheck.isSelected();
            String ignoreFieldsFieldText = ignoreFieldsField.getText();
            int threadsSpinnerValue = (int) threadsSpinner.getValue();
            boolean verboseCheckSelected = verboseCheck.isSelected();

            String clusterURL = ActiveCluster.getInstance().getClusterURL();
            String username = ActiveCluster.getInstance().getUsername();
            String password = ActiveCluster.getInstance().getPassword();

            CBImportCommandBuilder builder = new CBImportCommandBuilder(fileFormat, clusterURL, username, password)
                    .setBucket(bucket)
                    .setDataset(filePath)
                    .setFormat(fileFormat.equals(JSON_FILE_FORMAT) ? detectedCouchbaseJsonFormat : null)
                    .setFieldSeparator(fileFormat.equals(CSV_FILE_FORMAT) ? "," : null)
                    .setInferTypes(fileFormat);
                    .setScopeCollectionExp(targetLocationGroupSelection, "_default._default",
                            targetScopeField + "." + targetCollectionField,
                            dynamicScopeFieldText + "." + dynamicCollectionFieldText)
                    .setGenerateKey(keyGroupSelection, UUID_FLAG, "%" + fieldNameText + "%", customExpressionText)
                    .setGeneratorDelimiter("#")
                    .setSkipDocsOrRows(skipFirstCheckSelected ? skipFirstFieldText : null, fileFormat)
                    .setLimitDocsOrRows(importUptoCheckSelected ? importUptoFieldText : null, fileFormat)
                    .setIgnoreFields(ignoreFieldsCheckSelected ? ignoreFieldsFieldText : null)
                    .setThreads(threadsSpinnerValue);

            if (verboseCheckSelected) {
                builder.setVerbose();
            }

            List<String> commandList = builder.constructCommand();

            CBImport.complexBucketImport(bucket, filePath, project, fileFormat, commandList);
            super.doOKAction();
        } catch (Exception ex) {
            Log.error("Exception occurred in CBImport command builder: \n", ex);
        }
    }
}
