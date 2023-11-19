package com.couchbase.intellij.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CBImportCommandBuilder {

    public static final String JSON_FILE_FORMAT = "json";
    public static final String CSV_FILE_FORMAT = "csv";
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

    public CBImportCommandBuilder setScopeCollectionExp(String colExp) {
        commandList.add("--scope-collection-exp");
        commandList.add(colExp);
        return this;
    }

    public CBImportCommandBuilder setGenerateKey(String key) {
        commandList.add("--generate-key");
        commandList.add(key);
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
        if (!fields.isEmpty()) {
            String filteredFields = Arrays.stream(fields.split(",")).map(e -> e.trim()).collect(Collectors.joining(","));
            commandList.add("--ignore-fields");
            commandList.add(filteredFields);
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

    public List<String> build() {
        return commandList;
    }
}