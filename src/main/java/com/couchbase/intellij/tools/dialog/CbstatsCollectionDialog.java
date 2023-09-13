package com.couchbase.intellij.tools.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jetbrains.annotations.Nullable;

import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.ui.JBUI;

import utils.TemplateUtil;

public class CbstatsCollectionDialog extends DialogWrapper {

    private static final String NETWORK_CONNECTIVITY_ERROR_MESSAGE = "Could not connect to the cluster. Please check your network connectivity, if the cluster is active or if the credentials are still valid.";
    private static final String COUCHBASE_PLUGIN_ERROR_MESSAGE = "Couchbase Plugin Error";
    private static final String DEFAULT_TAG = "_default";
    private JPanel dialogPanel;
    private GridBagConstraints c;
    private JComboBox<String> bucketComboBox;
    private JComboBox<String> scopeComboBox;
    private JComboBox<String> collectionComboBox;

    public CbstatsCollectionDialog() {
        super(true);
        init();
        setTitle("Custom Dialog");
        getWindow().setMinimumSize(new Dimension(800, 600));
        setResizable(true);


    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        dialogPanel = new JPanel(new GridBagLayout());
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTH;

        bucketComboBox = new JComboBox<>();
        scopeComboBox = new JComboBox<>();
        collectionComboBox = new JComboBox<>();

        bucketComboBox.addActionListener(e -> handleBucketComboBoxChange());
        scopeComboBox.addActionListener(e -> handleScopeComboBoxChange());

        // Line 1: Bucket
        addLabelAndComponent("Bucket:", "Select the bucket", 0, bucketComboBox);

        // Line 2: Scope
        addLabelAndComponent("Scope:", "Select the scope", 1, scopeComboBox);

        // Line 3: Collection
        addLabelAndComponent("Collection:", "Select the collection", 2, collectionComboBox);

        // Line 4: Collection Statistics
        c.gridx = 0;
        c.gridy = 3;
        JPanel statsLabel = TemplateUtil.getLabelWithHelp("Collection Statistics:", "Collection statistics will be displayed here");
        dialogPanel.add(statsLabel, c);

        // Line 5: Output Text Preview Area
        c.gridx = 0;
        c.gridy = 4;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;

        JTextArea outputTextArea = new JTextArea();

        outputTextArea.setEditable(false);
        outputTextArea.setLineWrap(true); // Enable line wrapping
        outputTextArea.setWrapStyleWord(true); // Wrap lines at word boundaries

        JScrollPane scrollPane = new JScrollPane(outputTextArea);

        dialogPanel.add(scrollPane, c);

        return dialogPanel;
    }

    private void addLabelAndComponent(String labelText, String hint, int gridy, JComponent component) {
        c.gridx = 0;
        c.gridy = gridy;
        c.insets = JBUI.insets(5);

        JPanel label = TemplateUtil.getLabelWithHelp(labelText, hint);

        dialogPanel.add(label, c);

        c.gridx = 1;

        dialogPanel.add(component, c);
    }

    private void handleBucketComboBoxChange() {
        try {
            String selectedBucket = Optional.ofNullable(bucketComboBox.getSelectedItem()).map(Object::toString).orElse(null);

            Set<String> scopeNamesSet = ActiveCluster.getInstance().get().bucket(selectedBucket).collections().getAllScopes().stream().map(ScopeSpec::name).collect(Collectors.toSet());
            scopeComboBox.removeAllItems();
            for (String s : scopeNamesSet) {
                scopeComboBox.addItem(s);
            }

            if (scopeComboBox.getItemCount() > 0) {
                scopeComboBox.setSelectedIndex(0);
            } else {
                Messages.showErrorDialog("The selectd bucket is empty. Selecting default scope and collection", "Empty Bucket");
                scopeComboBox.setSelectedItem(DEFAULT_TAG);
                collectionComboBox.setSelectedItem(DEFAULT_TAG);

            }
            handleScopeComboBoxChange();
        } catch (Exception e) {
            Messages.showErrorDialog(NETWORK_CONNECTIVITY_ERROR_MESSAGE, COUCHBASE_PLUGIN_ERROR_MESSAGE);
            Log.error(e);
        }
    }

    private void handleScopeComboBoxChange() {
        try {
            String selectedBucket = Optional.ofNullable(bucketComboBox.getSelectedItem()).map(Object::toString).orElse(null);
            String selectedScope = Optional.ofNullable(scopeComboBox.getSelectedItem()).map(Object::toString).orElse(null);

            if (selectedScope == null || selectedScope.isEmpty()) {
                scopeComboBox.setSelectedItem(DEFAULT_TAG);
                collectionComboBox.setSelectedItem(DEFAULT_TAG);
                return;
            }

            Consumer<String> refreshCollectionCombo = scope -> {
                String[] collectionNamesArray = ActiveCluster.getInstance().get().bucket(selectedBucket).collections().getAllScopes().stream().filter(s -> s.name().equals(scope)).flatMap(s -> s.collections().stream()).map(CollectionSpec::name).distinct().toArray(String[]::new);
                collectionComboBox.removeAllItems();
                for (String s : collectionNamesArray) {
                    collectionComboBox.addItem(s);
                }
            };

            refreshCollectionCombo.accept(selectedScope);

            if (collectionComboBox.getItemCount() > 0) {
                collectionComboBox.setSelectedIndex(0);
            } else {
                Messages.showErrorDialog("The selectd scope is empty. Selecting default scope and collection", "Empty Bucket");
                scopeComboBox.setSelectedItem(DEFAULT_TAG);
                collectionComboBox.setSelectedItem(DEFAULT_TAG);
            }
        } catch (Exception e) {
            Messages.showErrorDialog(NETWORK_CONNECTIVITY_ERROR_MESSAGE, COUCHBASE_PLUGIN_ERROR_MESSAGE);
            Log.error(e);
        }
    }
}
