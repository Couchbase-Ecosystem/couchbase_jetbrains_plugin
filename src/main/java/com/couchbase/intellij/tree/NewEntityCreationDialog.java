package com.couchbase.intellij.tree;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.couchbase.intellij.workbench.Log;
import org.jetbrains.annotations.Nullable;

import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tree.overview.apis.BucketQuota;
import com.couchbase.intellij.tree.overview.apis.CouchbaseRestAPI;
import com.couchbase.intellij.tree.overview.apis.ServerOverview;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.ui.JBUI;

import static utils.TemplateUtil.fmtByte;

public class NewEntityCreationDialog extends DialogWrapper {

    private JTextField textField;
    private JLabel errorLabel;
    private JLabel hddQuotaLabel;
    private JLabel ramQuotaLabel;
    private final EntityType entityType;

    private String bucketName;
    private String scopeName;
    private String collectionName;

    private boolean hddQuotaCheckPerformed = false;
    private boolean ramQuotaCheckPerformed = false;

    // ServerOverview serverOverview = new ServerOverview();
    private ServerOverview serverOverview;

    protected NewEntityCreationDialog(
            Project project,
            EntityType entityType,

            String... names) {
        super(project);
        this.entityType = entityType;

        // 3 varargs for bucket,scope, collection
        if (entityType == EntityType.BUCKET) {
            // do nothing
        } else if (entityType == EntityType.SCOPE) {
            bucketName = names[0];
        } else if (entityType == EntityType.COLLECTION) {
            bucketName = names[0];
            scopeName = names[1];
        }

        init();
        setTitle("Create new " + entityType.toString().toLowerCase());
    }

    @Override
    @Nullable
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = JBUI.insets(5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel nameLabel = new JLabel("Name of the " + entityType.toString().toLowerCase());
        panel.add(nameLabel, gbc);

        gbc.gridy = 1;
        textField = new JTextField(40);
        panel.add(textField, gbc);

        gbc.gridy = 2;
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.decode("#FF4444"));
        panel.add(errorLabel, gbc);

        gbc.gridy = 3;
        hddQuotaLabel = new JLabel("");
        hddQuotaLabel.setForeground(Color.decode("#FFA500"));
        panel.add(hddQuotaLabel, gbc);

        gbc.gridy = 4;
        ramQuotaLabel = new JLabel("");
        ramQuotaLabel.setForeground(Color.decode("#FFA500"));
        panel.add(ramQuotaLabel, gbc);

        return panel;
    }

    @Override
    protected void doOKAction() {
        try {

            if (textField.getText().isEmpty()) {
                errorLabel.setText("Please enter a name for the " + entityType.toString().toLowerCase());
                return;
            }

            String allowedCharacters = "[a-zA-Z0-9_.\\-%]+";
            if (!textField.getText().matches(allowedCharacters)) {
                errorLabel.setText("Invalid characters in " + entityType.toString().toLowerCase()
                        + ". Use: A-Z, a-z, 0-9, _, ., -, %");
                return;
            }

            if (!hddQuotaCheckPerformed) {

                serverOverview = CouchbaseRestAPI.getOverview();

                long totalHddQuota = serverOverview.getStorageTotals().getHdd().getQuotaTotal();
                long usedHddQuota = serverOverview.getStorageTotals().getHdd().getUsed();
                long availableHddQuota = totalHddQuota - usedHddQuota;

                hddQuotaLabel.setText("HDD Quota: " + fmtByte(availableHddQuota) + " units available");
                hddQuotaCheckPerformed = true;

                if (availableHddQuota <= 0) {

                    errorLabel.setText("Error: HDD usage exceeds quota by " + fmtByte(-availableHddQuota)
                            + " units. Adjust HDD quota or memory usage.");
                    return;
                }
            }

            if (entityType == EntityType.BUCKET) {
                if (!ramQuotaCheckPerformed) {

                    long totalRamQuota = serverOverview.getStorageTotals().getRam().getQuotaTotal();
                    long usedRamQuota = serverOverview.getStorageTotals().getRam().getQuotaUsed();
                    long availableRamQuota = totalRamQuota - usedRamQuota;

                    ramQuotaLabel.setText("RAM Quota: " + fmtByte(availableRamQuota) + " units available");
                    ramQuotaCheckPerformed = true;

                    BucketQuota bucketQuota = new BucketQuota();
                    Long bucketRamQuotaLong = bucketQuota.getRam();
                    long bucketRamQuota = 0L;
                    if (bucketRamQuotaLong != null) {
                        bucketRamQuota = bucketRamQuotaLong;
                    }

                    if ((bucketRamQuota > availableRamQuota)) {
                        errorLabel.setText(
                                "Error: The RAM quota for the bucket exceeds the available RAM by "
                                        + fmtByte(-availableRamQuota) + " quota for your cluster.");
                        return;
                    }
                }
                bucketName = textField.getText();
                Map<String, BucketSettings> bucketSettingsMap = ActiveCluster.getInstance().get().buckets()
                        .getAllBuckets();
                List<String> bucketNames = new ArrayList<>(bucketSettingsMap.keySet());
                if (bucketNames.contains(bucketName)) {
                    errorLabel.setText("Bucket with name " + bucketName + " already exists");
                    return;
                }
            } else if (entityType == EntityType.SCOPE) {
                scopeName = textField.getText();
                List<ScopeSpec> scopes = ActiveCluster.getInstance().get().bucket(bucketName).collections()
                        .getAllScopes();
                for (ScopeSpec scope : scopes) {
                    if (scope.name().equals(scopeName)) {
                        errorLabel.setText("Scope with name " + scopeName + " already exists");
                        return;
                    }
                }
            } else if (entityType == EntityType.COLLECTION) {
                collectionName = textField.getText();
                List<CollectionSpec> collections = ActiveCluster.getInstance().get().bucket(bucketName)
                        .collections().getAllScopes().stream()
                        .filter(scope -> scope.name().equals(scopeName))
                        .flatMap(scope -> scope.collections().stream())
                        .collect(Collectors.toList());

                for (CollectionSpec collection : collections) {
                    if (collection.name().equals(collectionName)) {
                        errorLabel.setText("Collection with name " + collectionName + " already exists");
                        return;
                    }
                }

            }

            super.doOKAction();
        } catch (Exception ex) {
            Log.error("Exception occurred during creation of " + entityType, ex);
        }
    }

    public String getEntityName() {
        return textField.getText();
    }

    public enum EntityType {
        BUCKET, SCOPE, COLLECTION
    }
}
