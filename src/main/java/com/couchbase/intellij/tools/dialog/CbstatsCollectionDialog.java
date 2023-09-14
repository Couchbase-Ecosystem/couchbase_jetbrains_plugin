package com.couchbase.intellij.tools.dialog;

import com.couchbase.intellij.tools.CBStats;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class CbstatsCollectionDialog extends DialogWrapper {


    private final String bucketName;
    private String scopeName;
    private String collectionName;

    public CbstatsCollectionDialog(String bucket, String scope, String collection) {
        super(true);
        this.bucketName = bucket;
        this.scopeName = scope;
        this.collectionName = collection;
        init();
        setTitle("Cbstats Collection Dialog");
        getWindow().setMinimumSize(new Dimension(600, 400));
        setResizable(true);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        JPanel dialogPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH;

        // Output Text Preview Area
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;

        JTextArea outputTextArea = new JTextArea();

        outputTextArea.setEditable(false);
        outputTextArea.setLineWrap(true); // Enable line wrapping
        outputTextArea.setWrapStyleWord(true); // Wrap lines at word boundaries

        CBStats cbStats = new CBStats(bucketName,scopeName,collectionName);
        String output = "";
        try {
            output = cbStats.executeCommand();
        } catch (Exception e) {
            Log.error(e);
        }
        outputTextArea.setText(output);

        JScrollPane scrollPane = new JScrollPane(outputTextArea);

        dialogPanel.add(scrollPane, c);

        return dialogPanel;
    }

}
