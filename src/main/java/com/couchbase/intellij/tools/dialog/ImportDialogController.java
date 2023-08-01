package com.couchbase.intellij.tools.dialog;

import static utils.ProcessUtils.printOutput;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.event.DocumentEvent;

import org.jetbrains.annotations.NotNull;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tools.CBTools;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.DocumentAdapter;

import utils.FileUtils;

public class ImportDialogController {

    protected static final String[] possibleScopeFields = { "cbms", "scope", "cbs" };
    protected static final String[] possibleCollectionFields = { "cbmc", "collection", "cbc" };
    protected static final String[] possibleKeyFields = { "cbmk", "cbmid", "key", "cbk" };

    protected static String targetScopeField;
    protected static String targetCollectionField;

    protected static void addListeners(ImportDialog importDialog) {
        importDialog.defaultScopeAndCollectionRadio
                .addActionListener(e -> updateScopeAndCollectionFields(importDialog));
        importDialog.collectionRadio.addActionListener(e -> updateScopeAndCollectionFields(importDialog));
        importDialog.dynamicScopeAndCollectionRadio
                .addActionListener(e -> updateScopeAndCollectionFields(importDialog));

        importDialog.generateUUIDRadio.addActionListener(e -> updateKeyFormFields(importDialog));
        importDialog.useFieldValueRadio.addActionListener(e -> {
            updateKeyFormFields(importDialog);
            updateIgnoreFieldsTextField(importDialog);
        });
        importDialog.customExpressionRadio.addActionListener(e -> updateKeyFormFields(importDialog));

        DocumentAdapter updateSummaryListener = new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                Log.debug("Document changed" + e);
                updateSummary(importDialog);
            }
        };

        importDialog.datasetField.getTextField().getDocument().addDocumentListener(updateSummaryListener);
        importDialog.defaultScopeAndCollectionRadio.addActionListener(e -> updateSummary(importDialog));
        importDialog.collectionRadio.addActionListener(e -> updateSummary(importDialog));
        importDialog.dynamicScopeAndCollectionRadio.addActionListener(e -> updateSummary(importDialog));

        importDialog.scopeCombo.addActionListener(e -> updateSummary(importDialog));
        importDialog.collectionCombo.addActionListener(e -> updateSummary(importDialog));

        importDialog.dynamicScopeFieldField.getDocument().addDocumentListener(updateSummaryListener);
        importDialog.dynamicCollectionFieldField.getDocument().addDocumentListener(updateSummaryListener);

        importDialog.generateUUIDRadio.addActionListener(e -> updateSummary(importDialog));
        importDialog.useFieldValueRadio.addActionListener(e -> updateSummary(importDialog));
        importDialog.customExpressionRadio.addActionListener(e -> updateSummary(importDialog));

        importDialog.fieldNameField.getDocument().addDocumentListener(updateSummaryListener);
        importDialog.customExpressionField.getDocument().addDocumentListener(updateSummaryListener);

        importDialog.skipFirstField.getDocument().addDocumentListener(updateSummaryListener);
        importDialog.importUptoField.getDocument().addDocumentListener(updateSummaryListener);
        importDialog.ignoreFieldsField.getDocument().addDocumentListener(updateSummaryListener);

        importDialog.skipFirstCheck.addActionListener(e -> updateSummary(importDialog));
        importDialog.importUptoCheck.addActionListener(e -> updateSummary(importDialog));
        importDialog.ignoreFieldsCheck.addActionListener(e -> updateSummary(importDialog));
        importDialog.threadsSpinner.addChangeListener(e -> updateSummary(importDialog));
        importDialog.verboseCheck.addActionListener(e -> updateSummary(importDialog));

    }

    protected static void updateScopeAndCollectionFields(ImportDialog importDialog) {
        boolean collectionSelected = importDialog.collectionRadio.isSelected();

        importDialog.scopeLabel.setVisible(collectionSelected);
        importDialog.scopeCombo.setVisible(collectionSelected);
        importDialog.scopeLabel.setEnabled(collectionSelected);
        importDialog.scopeCombo.setEnabled(collectionSelected);

        importDialog.collectionLabel.setVisible(collectionSelected);
        importDialog.collectionCombo.setVisible(collectionSelected);
        importDialog.collectionLabel.setEnabled(collectionSelected);
        importDialog.collectionCombo.setEnabled(collectionSelected);

        boolean dynamicSelected = importDialog.dynamicScopeAndCollectionRadio.isSelected();

        importDialog.scopeFieldLabel.setVisible(dynamicSelected);
        importDialog.dynamicScopeFieldField.setVisible(dynamicSelected);
        importDialog.scopeFieldLabel.setEnabled(dynamicSelected);
        importDialog.dynamicScopeFieldField.setEnabled(dynamicSelected);

        importDialog.collectionFieldLabel.setVisible(dynamicSelected);
        importDialog.dynamicCollectionFieldField.setVisible(dynamicSelected);
        importDialog.collectionFieldLabel.setEnabled(dynamicSelected);
        importDialog.dynamicCollectionFieldField.setEnabled(dynamicSelected);

        try {
            if (importDialog.defaultScopeAndCollectionRadio.isSelected()) {
                Log.debug("Default scope and collection Radio selected");

                targetScopeField = "_default";
                targetCollectionField = "_default";
            } else if (collectionSelected) {
                Log.debug("collection Radio selected");

                importDialog.scopeCombo.addActionListener(e -> {
                    Log.debug("Scope combo box changed"
                            + Optional.ofNullable(importDialog.scopeCombo.getSelectedItem()).map(Object::toString)
                                    .orElse("null"));
                    targetScopeField = Optional.ofNullable(importDialog.scopeCombo.getSelectedItem())
                            .map(Object::toString)
                            .orElse("_default");
                });
                importDialog.collectionCombo.addActionListener(e -> {
                    Log.debug("Collection combo box changed"
                            + Optional.ofNullable(importDialog.collectionCombo.getSelectedItem())
                                    .map(Object::toString).orElse("null"));
                    targetCollectionField = Optional
                            .ofNullable(importDialog.collectionCombo.getSelectedItem())
                            .map(Object::toString)
                            .orElse("_default");
                });

            } else if (dynamicSelected) {
                Log.debug("Dynamic scope and collection Radio selected");
                String[] sampleElementContentSplit = getSampleElementContentSplit(importDialog.datasetField.getText());

                for (String field : possibleScopeFields) {
                    for (String element : sampleElementContentSplit) {
                        if (element.contains(field)) {
                            importDialog.dynamicScopeFieldField.setText("%" + field + "%");
                            targetScopeField = element.substring(element.indexOf(":") + 1);
                            break;
                        }
                    }
                }
                for (String field : possibleCollectionFields) {
                    for (String element : sampleElementContentSplit) {
                        if (element.contains(field)) {
                            importDialog.dynamicCollectionFieldField.setText("%" + field + "%");
                            targetCollectionField = element.substring(element.indexOf(":") + 1);
                            break;
                        }
                    }
                }

                updateIgnoreFieldsTextField(importDialog);

                importDialog.dynamicScopeFieldField.getDocument().addDocumentListener(new DocumentAdapter() {
                    @Override
                    protected void textChanged(@NotNull DocumentEvent e) {
                        Log.debug("Scope field changed" + importDialog.dynamicScopeFieldField.getText());
                        for (String element : sampleElementContentSplit) {
                            if (element.contains(importDialog.dynamicScopeFieldField.getText())) {
                                targetScopeField = element.substring(element.indexOf(":") + 1);
                                break;
                            }
                        }
                        updateIgnoreFieldsTextField(importDialog);
                    }
                });

                importDialog.dynamicCollectionFieldField.getDocument().addDocumentListener(new DocumentAdapter() {
                    @Override
                    protected void textChanged(@NotNull DocumentEvent e) {
                        Log.debug("Collection field changed" + importDialog.dynamicCollectionFieldField.getText());
                        for (String element : sampleElementContentSplit) {
                            if (element.contains(importDialog.dynamicCollectionFieldField.getText())) {
                                targetCollectionField = element.substring(element.indexOf(":") + 1);
                                break;
                            }
                        }
                        updateIgnoreFieldsTextField(importDialog);
                    }
                });
            }
        } catch (Exception e) {
            Log.error("Exception occurred", e);
            e.printStackTrace();
        }

        updateSummary(importDialog);
    }

    protected static void updateKeyFormFields(ImportDialog importDialog) {
        boolean useFieldValueSelected = importDialog.useFieldValueRadio.isSelected();

        importDialog.fieldNameLabel.setVisible(useFieldValueSelected);
        importDialog.fieldNameField.setVisible(useFieldValueSelected);
        importDialog.fieldNameLabel.setEnabled(useFieldValueSelected);
        importDialog.fieldNameField.setEnabled(useFieldValueSelected);

        boolean customExpressionSelected = importDialog.customExpressionRadio.isSelected();

        importDialog.customExpressionLabel.setVisible(customExpressionSelected);
        importDialog.customExpressionField.setVisible(customExpressionSelected);
        importDialog.customExpressionLabel.setEnabled(customExpressionSelected);
        importDialog.customExpressionField.setEnabled(customExpressionSelected);

        boolean keyPreviewVisible = useFieldValueSelected || customExpressionSelected;

        importDialog.keyPreviewTitledSeparator.setVisible(keyPreviewVisible);
        importDialog.keyPreviewTitledSeparator.setEnabled(keyPreviewVisible);
        importDialog.keyPreviewArea.setVisible(keyPreviewVisible);
        importDialog.keyPreviewArea.setEnabled(keyPreviewVisible);

        try {
            Log.debug("Updating key form fields");

            if (useFieldValueSelected) {
                String[] sampleElementContentSplit = getSampleElementContentSplit(importDialog.datasetField.getText());

                for (String field : possibleKeyFields) {
                    for (String element : sampleElementContentSplit) {
                        if (element.contains(field)) {
                            importDialog.fieldNameField.setText(field);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.error("Exception occurred", e);
            e.printStackTrace();
        }

        updateSummary(importDialog);
    }

    protected static void updateKeyPreview(ImportDialog importDialog) {
        importDialog.keyPreviewArea.setText("");

        try {

            if (importDialog.useFieldValueRadio.isSelected()) {
                Log.debug("Use field value radio selected");
                String fieldName = importDialog.fieldNameField.getText();

                String fileContent = Files.readString(Paths.get(importDialog.datasetField.getText()));
                JsonArray jsonArray = JsonArray.fromJson(fileContent);

                StringBuilder previewContent = new StringBuilder();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jsonObject = jsonArray.getObject(i);
                    if (jsonObject.containsKey(fieldName)) {
                        previewContent.append(jsonObject.getString(fieldName)).append("\n");
                    }
                }

                importDialog.keyPreviewArea.setText(previewContent.toString());

                updateIgnoreFieldsTextField(importDialog);

            } else if (importDialog.customExpressionRadio.isSelected()) {
                Log.debug("Custom expression radio selected");

                String expression = importDialog.customExpressionField.getText();

                Pattern pattern = Pattern.compile("%(\\w+)%");
                Matcher matcher = pattern.matcher(expression);
                List<String> fieldNames = new ArrayList<>();
                while (matcher.find()) {
                    fieldNames.add(matcher.group(1));
                }

                String fileContent = Files.readString(Paths.get(importDialog.datasetField.getText()));
                JsonArray jsonArray = JsonArray.fromJson(fileContent);

                StringBuilder previewContent = new StringBuilder();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jsonObject = jsonArray.getObject(i);

                    String key = expression;
                    for (String fieldName : fieldNames) {
                        if (jsonObject.containsKey(fieldName)) {
                            key = key.replace("%" + fieldName + "%", jsonObject.getString(fieldName));
                        }
                    }

                    previewContent.append(key).append("\n");
                }

                importDialog.keyPreviewArea.setText(previewContent.toString());

            }

        } catch (Exception e) {
            Log.error("Exception occurred", e);
            e.printStackTrace();
        }
    }

    protected static void updateSummary(ImportDialog importDialog) {
        StringBuilder summary = new StringBuilder();
        summary.append("<html>");

        summary.append("<b>Dataset:</b> ");
        summary.append(importDialog.datasetField.getText());
        summary.append("<br><br>");

        summary.append("<b>Bucket:</b> ");
        summary.append(importDialog.bucketCombo.getSelectedItem());
        summary.append("<br><br>");

        summary.append("<b>Scope and Collection:</b> ");
        if (importDialog.defaultScopeAndCollectionRadio.isSelected()) {
            summary.append("Default Scope and Collection");
        } else if (importDialog.collectionRadio.isSelected()) {
            summary.append("Collection - Scope: ");
            summary.append(importDialog.scopeCombo.getSelectedItem());
            summary.append(", Collection: ");
            summary.append(importDialog.collectionCombo.getSelectedItem());
        } else if (importDialog.dynamicScopeAndCollectionRadio.isSelected()) {
            summary.append("Dynamic Scope and Collection - Scope Field: ");
            summary.append(importDialog.dynamicScopeFieldField.getText());
            summary.append(", Collection Field: ");
            summary.append(importDialog.dynamicCollectionFieldField.getText());
        }
        summary.append("<br><br>");

        summary.append("<b>Document Key:</b> ");
        if (importDialog.generateUUIDRadio.isSelected()) {
            summary.append("Generate random UUID for each document");
        } else if (importDialog.useFieldValueRadio.isSelected()) {
            summary.append("Use the value of a field as the key - Field Name: ");
            summary.append(importDialog.fieldNameField.getText());
        } else if (importDialog.customExpressionRadio.isSelected()) {
            summary.append("Generate key based on custom expression - Expression: ");
            summary.append(importDialog.customExpressionField.getText());
        }
        summary.append("<br><br>");

        summary.append("<b>Advanced Options:</b><br>");
        if (importDialog.skipFirstCheck.isSelected()) {
            summary.append("- Skip the first ");
            summary.append(importDialog.skipFirstField.getText());
            summary.append(" documents<br>");
        }
        if (importDialog.importUptoCheck.isSelected()) {
            summary.append("- Import up to ");
            summary.append(importDialog.importUptoField.getText());
            summary.append(" documents<br>");
        }
        if (importDialog.ignoreFieldsCheck.isSelected()) {
            summary.append("- Ignore the fields: ");
            summary.append(importDialog.ignoreFieldsField.getText());
            summary.append("<br>");
        }
        summary.append("- Threads: ");
        summary.append(importDialog.threadsSpinner.getValue());
        summary.append("<br>");
        if (importDialog.verboseCheck.isSelected()) {
            summary.append("- Verbose Log<br>");
        }

        summary.append("<br>");

        importDialog.summaryLabel.setText(summary.toString());
    }

    protected static void updateIgnoreFieldsTextField(ImportDialog importDialog) {
        String ignoreFieldsText = "";

        if (importDialog.dynamicScopeAndCollectionRadio.isSelected()) {
            String fieldNameText = importDialog.fieldNameField.getText();
            List<String> wordsWithPercentSymbols = new ArrayList<>();

            String scopeText = importDialog.dynamicScopeFieldField.getText();
            if (!scopeText.isEmpty()) {
                wordsWithPercentSymbols.addAll(extractWordsWithPercentSymbols(scopeText));
            }

            String collectionText = importDialog.dynamicCollectionFieldField.getText();
            if (!collectionText.isEmpty()) {
                wordsWithPercentSymbols.addAll(extractWordsWithPercentSymbols(collectionText));
            }

            ignoreFieldsText = fieldNameText + "," + String.join(",", wordsWithPercentSymbols);
        } else if (importDialog.collectionRadio.isSelected()) {
            ignoreFieldsText = importDialog.fieldNameField.getText();
        }

        ignoreFieldsText = ignoreFieldsText.replaceAll("^,+|,+$", "");

        importDialog.ignoreFieldsField.setText(ignoreFieldsText);
    }

    protected static List<String> extractWordsWithPercentSymbols(String text) {
        List<String> wordsWithPercentSymbols = new ArrayList<>();
        Matcher matcher = Pattern.compile("%(\\w+)%").matcher(text);
        while (matcher.find()) {
            wordsWithPercentSymbols.add(matcher.group(1));
        }
        return wordsWithPercentSymbols;
    }

    protected static String[] getSampleElementContentSplit(String datasetFieldText) throws IOException {
        String sampleElementContent = FileUtils.sampleElementFromJsonArrayFile(datasetFieldText);
        System.out.println("Sample element content in updating key form fields " + sampleElementContent);
        assert sampleElementContent != null;
        return sampleElementContent.split(",");
    }

    protected static void complexBucketImport(ImportDialog importDialog,
            String bucket, String filePath, Project project, String fileFormat) {

        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Importing data", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);
                indicator.setText("Importing data");
                indicator.setText2("Importing data from " + filePath + " to bucket " + bucket);

                try {
                    ProcessBuilder processBuilder = new ProcessBuilder(
                            CBTools.getTool(CBTools.Type.CB_IMPORT).getPath(),
                            fileFormat,
                            "--no-ssl-verify",
                            "--cluster", ActiveCluster.getInstance().getClusterURL(),
                            "--username", ActiveCluster.getInstance().getUsername(),
                            "--password", ActiveCluster.getInstance().getPassword(),
                            "--bucket", bucket,
                            "--dataset", "file://" + filePath);

                    if (fileFormat.equals("csv")) {
                        processBuilder.command().add("--field-separator");
                        processBuilder.command().add(",");
                        processBuilder.command().add("--infer-types");
                    }

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

                    if (importDialog.targetLocationGroup.getSelection() == importDialog.defaultScopeAndCollectionRadio
                            .getModel()) {
                        processBuilder.command().add("--scope-collection-exp");
                        processBuilder.command().add("_default._default");
                    } else if (importDialog.targetLocationGroup.getSelection() == importDialog.collectionRadio
                            .getModel()) {
                        processBuilder.command().add("--scope-collection-exp");
                        processBuilder.command()
                                .add(checkForDelimiters(
                                        targetScopeField + "." + targetCollectionField, '#'));
                    } else if (importDialog.targetLocationGroup
                            .getSelection() == importDialog.dynamicScopeAndCollectionRadio.getModel()) {
                        processBuilder.command().add("--scope-collection-exp");
                        processBuilder.command()
                                .add(checkForDelimiters(importDialog.dynamicScopeFieldField.getText() + "." +
                                        importDialog.dynamicCollectionFieldField.getText(), '#'));
                    }

                    if (importDialog.keyGroup.getSelection() == importDialog.generateUUIDRadio.getModel()) {
                        processBuilder.command().add("--generate-key");
                        processBuilder.command().add(checkForDelimiters("#UUID#", '#'));
                    } else if (importDialog.keyGroup.getSelection() == importDialog.useFieldValueRadio.getModel()) {
                        processBuilder.command().add("--generate-key");
                        String fieldName = importDialog.fieldNameField.getText();
                        processBuilder.command().add(checkForDelimiters("%" + fieldName + "%", '#'));
                    } else if (importDialog.keyGroup.getSelection() == importDialog.customExpressionRadio.getModel()) {
                        processBuilder.command().add("--generate-key");
                        String customExpression = importDialog.customExpressionField.getText();
                        processBuilder.command().add(checkForDelimiters(customExpression, '#'));
                    }

                    if (importDialog.skipFirstCheck.isSelected()) {
                        if (fileFormat.equals("json")) {
                            processBuilder.command().add("--skip-docs");
                        } else if (fileFormat.equals("csv")) {
                            processBuilder.command().add("--skip-rows");
                        }
                        processBuilder.command().add(importDialog.skipFirstField.getText());
                    }

                    if (importDialog.importUptoCheck.isSelected()) {
                        if (fileFormat.equals("json")) {
                            processBuilder.command().add("--limit-docs");
                        } else if (fileFormat.equals("csv")) {
                            processBuilder.command().add("--limit-rows");
                        }
                        processBuilder.command().add(importDialog.importUptoField.getText());
                    }

                    if (importDialog.ignoreFieldsCheck.isSelected()) {
                        processBuilder.command().add("--ignore-fields");
                        processBuilder.command()
                                .add(checkForDelimiters("'" + importDialog.ignoreFieldsField.getText() + "'", '#'));
                    }

                    processBuilder.command().add("--threads");
                    processBuilder.command().add(importDialog.threadsSpinner.getValue().toString());

                    if (importDialog.verboseCheck.isSelected()) {
                        processBuilder.command().add("--verbose");
                    }

                    Log.debug("Command: " + processBuilder.command());
                    System.out.println("Command: " + processBuilder.command());

                    Process process = processBuilder.start();
                    printOutput(process, "Output from CB_IMPORT: ");
                    int exitCode = process.waitFor();

                    if (exitCode == 0) {
                        Log.info("Data imported successfully");
                        ApplicationManager.getApplication().invokeLater(
                                () -> Messages.showInfoMessage("Data imported successfully", "Import Complete"));
                    } else {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                        StringBuilder errorMessage = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            errorMessage.append(line).append("\n");
                        }
                        reader.close();

                        Log.error("An error occurred while trying to import the data: " + errorMessage
                                + "with exit code " + exitCode);
                        ApplicationManager.getApplication().invokeLater(() -> Messages.showErrorDialog(
                                "An error occurred while trying to import the data:\n" + errorMessage
                                        + "\nwith exit code " + exitCode,
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

    public static String checkForDelimiters(String input, char c) {
        if (input.indexOf(c) != -1) {
            return "'" + input + "'";
        } else {
            return input;
        }
    }
}
