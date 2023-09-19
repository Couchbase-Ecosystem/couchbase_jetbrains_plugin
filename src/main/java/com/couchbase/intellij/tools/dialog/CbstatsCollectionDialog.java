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

public class CbstatsCollectionDialog extends DialogWrapper {

    private final String bucketName;
    private final String scopeName;
    private final String collectionName;


    public CbstatsCollectionDialog(String bucket, String scope, String collection) {
        super(true);
        this.bucketName = bucket;
        this.scopeName = scope;
        this.collectionName = collection;
        init();
        setTitle("Stats for Collection");
        getWindow().setMinimumSize(new Dimension(350, 400));
        setResizable(true);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());

        CBStats cbStats = new CBStats(bucketName, scopeName, collectionName);
        String output = "";
        try {
            output = cbStats.executeCommand();
        } catch (Exception ex) {
            Log.error(ex);
        }

        String[] lines = output.split("\n");
        String[] keys = new String[lines.length];
        String[] values = new String[lines.length];

        for (int i = 0; i < lines.length; i++) {
            int keyStartIndex = lines[i].indexOf(':', lines[i].indexOf(':') + 1) + 1;
            int valueStartIndex = lines[i].lastIndexOf(':') + 1;
            keys[i] = getFriendlyKeyName(lines[i].substring(keyStartIndex, valueStartIndex - 1).trim());
            values[i] = getFriendlyValue(lines[i].substring(valueStartIndex).trim(), keys[i]);
        }

        JPanel keyValuePanel = TemplateUtil.createKeyValuePanel(keys, values, 1);

        dialogPanel.add(keyValuePanel, BorderLayout.CENTER);

        return dialogPanel;
    }

    private String getFriendlyKeyName(String key) {
        switch (key) {
            case "collections_mem_used":
                return "Memory Used By Collection";
            case "data_size":
                return "Collection Data Size";
            case "items":
                return "Number Of Items";
            case "name":
                return "Collection Name";
            case "ops_delete":
                return "Number Of Delete Operations";
            case "ops_get":
                return "Number Of Get Operations";
            case "ops_store":
                return "Number Of Store Operations";
            case "scope_name":
                return "Scope Name";
            default:
                return key;
        }
    }

    private String getFriendlyValue(String value, String key) {
        switch (key) {
            case "Memory Used By Collection":
            case "Collection Data Size":
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
