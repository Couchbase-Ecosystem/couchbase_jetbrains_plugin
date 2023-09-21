package com.couchbase.intellij.tools.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jetbrains.annotations.Nullable;

import com.couchbase.intellij.tools.CBStats;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.ui.DialogWrapper;

import utils.TemplateUtil;

public class CbstatsDialog extends DialogWrapper {

    private final String bucketName;
    private final String scopeName;
    private final String collectionName;
    private final String type;

    private static final String MEMORY_USED_BY_COLLECTION = "Memory Used By Collection";
    private static final String COLLECTION_DATA_SIZE = "Collection Data Size";
    private static final String NUMBER_OF_ITEMS = "Number Of Items";
    private static final String COLLECTION_NAME = "Collection Name";
    private static final String NUMBER_OF_DELETE_OPERATIONS = "Number Of Delete Operations";
    private static final String NUMBER_OF_GET_OPERATIONS = "Number Of Get Operations";
    private static final String NUMBER_OF_STORE_OPERATIONS = "Number Of Store Operations";
    private static final String SCOPE_NAME = "Scope Name";

    public CbstatsDialog(String bucket, String scope, String collection,String type) {
        super(true);
        this.bucketName = bucket;
        this.scopeName = scope;
        this.collectionName = collection;
        this.type = type;
        init();
        setTitle("Stats for " + type);
        setResizable(true);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());

        CBStats cbStats = new CBStats(bucketName, scopeName, collectionName,type);
        String output = "";
        try {
            output = cbStats.executeCommand();
        } catch (Exception ex) {
            Log.error(ex);
        }

        String[] lines = output.split("\n");
        String[] keys = new String[lines.length];
        String[] values = new String[lines.length];
        String[] helpTexts = new String[lines.length]; // array for help texts

        for (int i = 0; i < lines.length; i++) {
            int keyStartIndex = lines[i].indexOf(':', lines[i].indexOf(':') + 1) + 1;
            int valueStartIndex = lines[i].lastIndexOf(':') + 1;
            keys[i] = getFriendlyKeyName(lines[i].substring(keyStartIndex, valueStartIndex - 1).trim());
            values[i] = getFriendlyValue(lines[i].substring(valueStartIndex).trim(), keys[i]);
            helpTexts[i] = getHelpText(keys[i]); // get help text for each key
        }

        JPanel keyValuePanel = TemplateUtil.createKeyValuePanelWithHelp(keys, values, helpTexts, 1);

        dialogPanel.add(keyValuePanel, BorderLayout.CENTER);

        return dialogPanel;
    }

    private String getHelpText(String key) {
        switch (key) {
            case MEMORY_USED_BY_COLLECTION:
                return "This is the memory used by the collection. It is measured in bytes.";
            case COLLECTION_DATA_SIZE:
                return "This is the size of the data in the collection. It is also measured in bytes.";
            case NUMBER_OF_ITEMS:
                return "This represents the number of items in the collection. It doesn't have a unit as it's a count.";
            case COLLECTION_NAME:
                return "This is the name of the collection. It's a string and doesn't have a unit.";
            case NUMBER_OF_DELETE_OPERATIONS:
                return "This is the number of delete operations that have been performed on the collection. It doesn't have a unit as it's a count.";
            case NUMBER_OF_GET_OPERATIONS:
                return "This is the number of get operations that have been performed on the collection. It doesn't have a unit as it's a count.";
            case NUMBER_OF_STORE_OPERATIONS:
                return "This is the number of store operations that have been performed on the collection. It doesn't have a unit as it's a count.";
            case SCOPE_NAME:
                return "This is the name of the scope that contains the collection. It's a string and doesn't have a unit.";
            default:
                return "";
        }
    }

    private String getFriendlyKeyName(String key) {
        switch (key) {
            case "collections_mem_used":
                return MEMORY_USED_BY_COLLECTION;
            case "data_size":
                return COLLECTION_DATA_SIZE;
            case "items":
                return NUMBER_OF_ITEMS;
            case "name":
                return COLLECTION_NAME;
            case "ops_delete":
                return NUMBER_OF_DELETE_OPERATIONS;
            case "ops_get":
                return NUMBER_OF_GET_OPERATIONS;
            case "ops_store":
                return NUMBER_OF_STORE_OPERATIONS;
            case "scope_name":
                return SCOPE_NAME;
            default:
                return key;
        }
    }

    private String getFriendlyValue(String value, String key) {
        switch (key) {
            case MEMORY_USED_BY_COLLECTION:
            case COLLECTION_DATA_SIZE:
                long bytes = Long.parseLong(value);
                double sizeInMB = bytes / (1024.0 * 1024.0);
                if (sizeInMB >= 1024) {
                    double sizeInGB = sizeInMB / 1024.0;
                    return String.format("%.2f GB", sizeInGB);
                } else {
                    return String.format("%.2f MB", sizeInMB);
                }
            default:
                return value;
        }
    }
}
