package com.couchbase.intellij.tools.dialog;

import com.couchbase.intellij.tools.CBTools;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

class CBImportCommandBuilder {

    protected static final String JSON_FILE_FORMAT = "json";
    protected static final String CSV_FILE_FORMAT = "csv";
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
        if (fileFormat.equals(CSV_FILE_FORMAT)) {
            commandList.add("--infer-types");
        }
        return this;
    }

    public CBImportCommandBuilder setScopeCollectionExp(ButtonModel targetLocationGroupSelection, String defaultExp,
                                                        String collectionExp, String dynamicExp) {
        String scopeCollectionExpFlag = "--scope-collection-exp";

        if (targetLocationGroupSelection == defaultScopeAndCollectionRadio.getModel()) {
            commandList.add(scopeCollectionExpFlag);
            commandList.add(defaultExp);
        } else if (targetLocationGroupSelection == targetCollectionRadio.getModel()) {
            commandList.add(scopeCollectionExpFlag);
            commandList.add(collectionExp);
        } else if (targetLocationGroupSelection == dynamicScopeAndCollectionRadio.getModel()) {
            commandList.add(scopeCollectionExpFlag);
            commandList.add(dynamicExp);
        }
        return this;
    }

    public CBImportCommandBuilder setGenerateKey(ButtonModel keyGroupSelection, String uuidKey,
                                                 String fieldValueKey, String customExpressionKey) {
        String generateKeyFlag = "--generate-key";

        if (keyGroupSelection == generateUUIDRadio.getModel()) {
            commandList.add(generateKeyFlag);
            commandList.add(uuidKey);
        } else if (keyGroupSelection == useFieldValueRadio.getModel()) {
            commandList.add(generateKeyFlag);
            commandList.add(fieldValueKey);
        } else if (keyGroupSelection == customExpressionRadio.getModel()) {
            commandList.add(generateKeyFlag);
            commandList.add(customExpressionKey);
        }
        return this;
    }

    public CBImportCommandBuilder setSkipDocsOrRows(String skipValue, String flag) {
        if (skipValue != null && !skipValue.isEmpty()) {
            if (flag.equals(JSON_FILE_FORMAT)) {
                commandList.add("--skip-docs");
            } else if (flag.equals(CSV_FILE_FORMAT)) {
                commandList.add("--skip-rows");
            }
            commandList.add(skipValue);
        }
        return this;
    }

    public CBImportCommandBuilder setLimitDocsOrRows(String limitValue, String flag) {
        if (limitValue != null && !limitValue.isEmpty()) {
            if (flag.equals(JSON_FILE_FORMAT)) {
                commandList.add("--limit-docs");
            } else if (flag.equals(CSV_FILE_FORMAT)) {
                commandList.add("--limit-rows");
            }
            commandList.add(limitValue);
        }
        return this;
    }

    public CBImportCommandBuilder setIgnoreFields(String fields) {
        if (fields != null && !fields.isEmpty()) {
            commandList.add("--ignore-fields");
            commandList.add(fields);
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