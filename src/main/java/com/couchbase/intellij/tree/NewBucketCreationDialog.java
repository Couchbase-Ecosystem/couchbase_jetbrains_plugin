package com.couchbase.intellij.tree;

import com.couchbase.client.core.msg.kv.DurabilityLevel;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.bucket.*;
import com.couchbase.client.protostellar.admin.bucket.v1.EvictionMode;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tree.overview.apis.CouchbaseRestAPI;
import com.couchbase.intellij.tree.overview.apis.ServerOverview;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.jgoodies.forms.factories.Borders;
import org.jetbrains.annotations.Nullable;
import utils.Mouse;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class NewBucketCreationDialog extends DialogWrapper {
    private JTextField bucketName;
    private ButtonGroup bucketTypes;
    private ButtonGroup storageBackends;
    private JSpinner memQuota;
    private JCheckBox enableReplicas;
    private ComboBox<Integer> replicaNum;
    private JTextArea replicasErrorLabel;
    private JCheckBox replicateViewIndexes;
    private JCheckBox enableTTL;
    private JTextField bucketTTL;
    private ButtonGroup compressionMode;
    private ButtonGroup conflictResolutionType;
    private ButtonGroup ejectionMethod;
    private ButtonGroup bucketPriority;
    private ComboBox<String> minimumDurabilityLevel;
    private JCheckBox autoCompactionOverride;
    private JCheckBox compactionEnableOnPercentFragmentation;
    private JSpinner compactionOnPercentFragmentation;
    private JCheckBox compactionEnableOnSizeFragmentation;
    private JSpinner compactionOnSizeFragmentation;
    private JCheckBox enableViewCompactOnPercentageFragmentation;
    private JSpinner compactViewOnPercentFragmentation;
    private JCheckBox compactionViewEnableOnSizeFragmentation;
    private JSpinner compactionViewOnSizeFragmentation;
    private JCheckBox enableCompactTimeInterval;
    private JSpinner compactIntervalStartHour;
    private JSpinner compactIntervalStartMinute;
    private JSpinner compactIntervalEndHour;
    private JSpinner compactIntervalEndMinute;
    private JCheckBox abortCompactionsAtIntervalEnd;
    private JCheckBox compactInParallel;
    private JTextField metadataPurgeInterval;
    private JCheckBox enableFlush;
    private JLabel bucketNameError;

    private int totalMemQuota = 0;
    private int usedMemQuota = 0;
    private int nodeCount;
    private JLabel freeMemLabel;
    private JLabel thisBucketMemLabel;
    private JPanel freeMemBar;
    private JPanel thisBucketMemBar;

    @Override
    protected void doOKAction() {
        Cluster cluster = ActiveCluster.getInstance().getCluster();
        if (cluster == null) {
            Messages.showMessageDialog("There is no active connection to run this query", "Couchbase Plugin Error", Messages.getErrorIcon());
            this.doCancelAction();
        }

        BucketSettings bs = BucketSettings.create(bucketName.getText());
        bs.bucketType(BucketType.valueOf(bucketTypes.getSelection().getActionCommand()));
        bs.storageBackend(StorageBackend.of(storageBackends.getSelection().getActionCommand()));
        bs.ramQuotaMB(Long.valueOf(memQuota.getValue().toString()));
        if (enableReplicas.isSelected()) {
            bs.numReplicas((Integer) replicaNum.getSelectedItem());
            if (replicateViewIndexes.isSelected()) {
                bs.replicaIndexes(true);
            }
        }
        if (enableTTL.isSelected()) {
            bs.maxExpiry(Duration.of(Long.valueOf(bucketTTL.getText()), ChronoUnit.SECONDS));
        }
        bs.compressionMode(CompressionMode.valueOf(compressionMode.getSelection().getActionCommand()));
        bs.conflictResolutionType(ConflictResolutionType.valueOf(conflictResolutionType.getSelection().getActionCommand()));
        if (ejectionMethod.getSelection() != null) {
            bs.evictionPolicy(EvictionPolicyType.valueOf(ejectionMethod.getSelection().getActionCommand()));
        }
        bs.minimumDurabilityLevel(DurabilityLevel.values()[minimumDurabilityLevel.getSelectedIndex()]);
        bs.flushEnabled(enableFlush.isSelected());

        try {
            cluster.buckets().createBucket(bs);
        } catch (Exception e) {
            Messages.showErrorDialog(e.getMessage(), "Failed to create bucket");
        }

        super.doOKAction();
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        if (bucketName.getText().trim().isEmpty()) {
            return new ValidationInfo("bucket name cannot be empty", bucketName);
        }
        try {
            Integer quota = Integer.valueOf(memQuota.getValue().toString());
            if (quota < 100) {
                return new ValidationInfo("Invalid memory quota", memQuota);
            }
        } catch (Exception e) {
            return new ValidationInfo("Invalid memory quota", memQuota);
        }
        return super.doValidate();
    }

    protected NewBucketCreationDialog(@Nullable Project project) {
        super(project);

        init();
        setTitle("Create new bucket");
    }

    private void readClusterInformation() {
        Cluster cluster = ActiveCluster.getInstance().getCluster();
        if (cluster == null) {
            Messages.showMessageDialog("There is no active connection to run this query", "Couchbase Plugin Error", Messages.getErrorIcon());
            this.doCancelAction();
        }

        try {
            ServerOverview info = CouchbaseRestAPI.getOverview();
            nodeCount = info.getNodes().size();
            totalMemQuota = Math.toIntExact(info.getMemoryQuota());
            usedMemQuota = info.getBucketNames().stream().mapToInt(b -> {
                try {
                    return Math.toIntExact(CouchbaseRestAPI.getBucketOverview(b.getBucketName())
                            .getQuota().getRam() / 1024 / 1024);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).sum();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        readClusterInformation();
        JPanel mainFrame = new JPanel(new GridBagLayout());
        JScrollPane scrollPane = new JBScrollPane(mainFrame);
        scrollPane.setBorder(Borders.EMPTY_BORDER);
        GridBagConstraints gbc = new GridBagConstraints();
        Insets internalInsets = gbc.insets = JBUI.insets(5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 0;

        GridBagConstraints subGbc = new GridBagConstraints();
        subGbc.gridx = 0;
        subGbc.gridy = 0;
        subGbc.anchor = GridBagConstraints.NORTHWEST;
        Insets sectionInsets = subGbc.insets = JBUI.insets(0, 0, 2, 5);
        subGbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel bucketNamePanel = new JPanel(new GridBagLayout());
        mainFrame.add(bucketNamePanel, gbc);
        JLabel bucketNameLabel = new JLabel("Name");
        Font sectionLabelFont = new Font(
                bucketNameLabel.getFont().getName(),
                Font.BOLD,
                bucketNameLabel.getFont().getSize()
        );
        bucketNameLabel.setFont(sectionLabelFont);
        bucketNamePanel.add(bucketNameLabel, subGbc);

        bucketName = new JTextField(40);
        bucketName.getDocument().addDocumentListener(new NameChangeListener());
        subGbc.gridy++;
        subGbc.fill = GridBagConstraints.HORIZONTAL;
        subGbc.gridwidth = GridBagConstraints.REMAINDER;
        subGbc.insets = JBUI.emptyInsets();
        bucketNamePanel.add(bucketName, subGbc);

        subGbc.gridy++;
        subGbc.fill = GridBagConstraints.NONE;
        subGbc.gridwidth = 1;
        subGbc.insets = JBUI.insets(0, 10, 5, 5);
        bucketNameError = new JLabel("Bucket name cannot be empty");
        Font errorFont = new Font(
                bucketNameError.getFont().getName(),
                bucketNameError.getFont().getStyle(),
                (int) (bucketNameError.getFont().getSize() / 1.5)
        );
        Color errorColor = Color.decode("#FF4444");
        bucketNameError.setFont(errorFont);
        bucketNameError.setForeground(errorColor);
        bucketNameError.setVisible(false);
        bucketNamePanel.add(bucketNameError, subGbc);

        gbc.gridy++;
        JLabel bucketTypeLabel = new JLabel("Bucket type");
        bucketTypeLabel.setFont(sectionLabelFont);
        mainFrame.add(bucketTypeLabel, gbc);

        JPanel bucketTypePanel = new JPanel(new GridBagLayout());
        gbc.gridy++;
        mainFrame.add(bucketTypePanel, gbc);
        subGbc.gridx = 0;
        subGbc.gridy = 0;
        subGbc.insets = JBUI.insets(5);
        subGbc.fill = GridBagConstraints.NONE;
        subGbc.anchor = GridBagConstraints.WEST;
        bucketTypes = new ButtonGroup();
        JRadioButton bucketTypeCouchbase = new JRadioButton("Couchbase");
        bucketTypeCouchbase.setActionCommand(BucketType.COUCHBASE.toString());
        bucketTypeCouchbase.setSelected(true);
        bucketTypes.add(bucketTypeCouchbase);
        bucketTypePanel.add(bucketTypeCouchbase, subGbc);

        JRadioButton bucketTypesMemcached = new JRadioButton("Memcached");
        bucketTypesMemcached.setActionCommand(BucketType.MEMCACHED.toString());
        bucketTypes.add(bucketTypesMemcached);
        subGbc.gridx++;
        bucketTypePanel.add(bucketTypesMemcached, subGbc);

        JRadioButton bucketTypesEphemeral = new JRadioButton("Ephemeral");
        bucketTypesEphemeral.setActionCommand(BucketType.EPHEMERAL.toString());
        bucketTypes.add(bucketTypesEphemeral);
        subGbc.gridx++;
        bucketTypePanel.add(bucketTypesEphemeral, subGbc);

        gbc.insets = sectionInsets;
        gbc.gridy++;
        JLabel storageBackendLabel = new JLabel("Storage Backend");
        storageBackendLabel.setFont(sectionLabelFont);
        mainFrame.add(storageBackendLabel, gbc);
        storageBackends = new ButtonGroup();

        JPanel storageBackendsPanel = new JPanel(new GridBagLayout());
        gbc.insets = internalInsets;
        gbc.gridy++;
        mainFrame.add(storageBackendsPanel, gbc);
        JRadioButton storageBackendCouchStore = new JRadioButton("CouchStore");
        storageBackendCouchStore.setActionCommand(StorageBackend.COUCHSTORE.alias());
        storageBackendCouchStore.setSelected(true);
        storageBackends.add(storageBackendCouchStore);
        subGbc.gridy = 0;
        subGbc.gridx = 0;
        storageBackendsPanel.add(storageBackendCouchStore, subGbc);

        JRadioButton storageBackendMagma = new JRadioButton("Magma");
        storageBackendMagma.setActionCommand(StorageBackend.MAGMA.alias());
        storageBackends.add(storageBackendMagma);
        subGbc.gridx++;
        storageBackendsPanel.add(storageBackendMagma, subGbc);

        JLabel memoryQuotaLabel = new JLabel("Memory Quota");
        memoryQuotaLabel.setFont(sectionLabelFont);
        gbc.gridy++;
        gbc.insets = sectionInsets;
        mainFrame.add(memoryQuotaLabel, gbc);

        JPanel memQuotaLabels = new JPanel(new GridBagLayout());
        gbc.insets = internalInsets;
        gbc.gridy++;
        mainFrame.add(memQuotaLabels, gbc);

        JLabel memQuotaInfo = new JLabel("in MegaBytes per server node");
        memQuotaInfo.setForeground(JBColor.GRAY);
        Font tipFont = new Font(
                memQuotaInfo.getFont().getName(),
                memQuotaInfo.getFont().getStyle(),
                (int) (memQuotaInfo.getFont().getSize() / 1.5)
        );
        memQuotaInfo.setFont(tipFont);
        subGbc.gridx = 0;
        subGbc.gridy = 0;
        subGbc.anchor = GridBagConstraints.BASELINE;
        subGbc.insets = JBUI.insets(0, 5, 2, 5);
        memQuotaLabels.add(memQuotaInfo, subGbc);

        JPanel memQuotaPanel = new JPanel(new GridBagLayout());
        gbc.gridy++;
        gbc.insets = internalInsets;
        mainFrame.add(memQuotaPanel, gbc);

        memQuota = new JSpinner(new SpinnerNumberModel(100, 1, totalMemQuota - usedMemQuota, 100));
        memQuota.setValue(100);
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) memQuota.getEditor();
        editor.getTextField().setColumns(10);
        subGbc.gridx = 0;
        subGbc.gridy = 0;
        subGbc.insets = JBUI.emptyInsets();
        memQuotaPanel.add(memQuota, subGbc);
        subGbc.gridx++;
        JLabel memQuotaLabel = new JLabel("MiB");
        memQuotaLabel.setForeground(JBColor.GRAY);
        memQuotaPanel.add(memQuotaLabel, subGbc);

        GridBagLayout memStatusLayout = new GridBagLayout();
        JPanel memStatus = new JPanel(memStatusLayout);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy++;
        gbc.insets = JBUI.insets(5, 10);
        mainFrame.add(memStatus, gbc);

        JPanel usedMemBar = new JPanel();
        Color usedMemColor = new JBColor(new Color(142, 183, 230), new Color(142, 183, 230));
        usedMemBar.setBackground(usedMemColor);
        subGbc.gridx = 0;
        subGbc.gridy = 0;
        subGbc.fill = GridBagConstraints.HORIZONTAL;
        subGbc.weightx = (double) (usedMemQuota) / totalMemQuota;
        memStatus.add(usedMemBar, subGbc);

        thisBucketMemBar = new JPanel();
        Color thisBucketMemColor = new JBColor(new Color(179, 207, 239), new Color(179, 207, 239));
        thisBucketMemBar.setBackground(thisBucketMemColor);
        subGbc.weightx = (double) 100 / totalMemQuota;
        subGbc.gridx++;
        memStatus.add(thisBucketMemBar, subGbc);

        freeMemBar = new JPanel();
        Color freeMemColor = new JBColor(new Color(239, 235, 224), new Color(239, 235, 224));
        freeMemBar.setBackground(freeMemColor);
        subGbc.weightx = (double) (totalMemQuota - usedMemQuota - 100) / totalMemQuota;
        subGbc.gridx++;
        memStatus.add(freeMemBar, subGbc);

        subGbc.gridy = 0;
        subGbc.gridx = 0;
        subGbc.insets = internalInsets;
        subGbc.weightx = 1;
        JPanel memLegendPanel = new JPanel(new GridBagLayout());
        gbc.gridy++;
        mainFrame.add(memLegendPanel, gbc);

        JPanel usedMemLegend = new JPanel();
        usedMemLegend.setBackground(usedMemColor);
        memLegendPanel.add(usedMemLegend, subGbc);
        JLabel usedMemLabel = new JLabel(String.format("other buckets (%dMib)", usedMemQuota));
        subGbc.gridx++;
        subGbc.fill = GridBagConstraints.HORIZONTAL;
        subGbc.weightx = 1;
        memLegendPanel.add(usedMemLabel, subGbc);

        JPanel thisBucketMemLegend = new JPanel();
        thisBucketMemLegend.setBackground(thisBucketMemColor);
        subGbc.gridx++;
        memLegendPanel.add(thisBucketMemLegend, subGbc);
        thisBucketMemLabel = new JLabel(String.format("this bucket (%dMib)", 100));
        subGbc.gridx++;
        memLegendPanel.add(thisBucketMemLabel, subGbc);

        JPanel freeMemLegend = new JPanel();
        freeMemLegend.setBackground(freeMemColor);
        subGbc.gridx++;
        memLegendPanel.add(freeMemLegend, subGbc);
        freeMemLabel = new JLabel(String.format("available (%dMib)", totalMemQuota));
        subGbc.gridx++;
        memLegendPanel.add(freeMemLabel, subGbc);
        subGbc.fill = GridBagConstraints.NONE;

        memQuota.addChangeListener(changeEvent -> {
            GridBagConstraints tbc = memStatusLayout.getConstraints(thisBucketMemBar);
            int quota = Integer.valueOf(memQuota.getValue().toString());
            tbc.weightx = (double) quota / totalMemQuota;
            memStatusLayout.setConstraints(thisBucketMemBar, tbc);
            tbc = memStatusLayout.getConstraints(freeMemBar);
            tbc.weightx = (double) (totalMemQuota - usedMemQuota - quota) / totalMemQuota;
            memStatusLayout.setConstraints(freeMemBar, tbc);
            thisBucketMemLabel.setText(String.format("this bucket (%dMib)", quota));
            memStatus.invalidate();
            usedMemBar.invalidate();
            thisBucketMemBar.invalidate();
            freeMemBar.invalidate();
            this.pack();
        });

        final Icon closedIcon = IconLoader.getIcon("/assets/icons/triangle-right.svg", getClass().getClassLoader());
        final Icon openedIcon = IconLoader.getIcon("/assets/icons/triangle-down.svg", getClass().getClassLoader());
        JLabel optionsLabel = new JLabel("Advanced bucket settings", closedIcon, SwingConstants.CENTER);
        gbc.gridy++;
        gbc.insets = sectionInsets;
        gbc.fill = GridBagConstraints.NONE;
        mainFrame.add(optionsLabel, gbc);

        JPanel advancedOptionsPanel = new JPanel(new GridBagLayout());
        advancedOptionsPanel.setVisible(false);
        gbc.gridy++;
        gbc.insets = JBUI.insets(5, 15, 5, 5);
        mainFrame.add(advancedOptionsPanel, gbc);
        optionsLabel.addMouseListener(Mouse.click(mouseEvent -> {
            advancedOptionsPanel.setVisible(!advancedOptionsPanel.isVisible());
            if (advancedOptionsPanel.isVisible()) {
                optionsLabel.setIcon(openedIcon);
            } else {
                optionsLabel.setIcon(closedIcon);
            }
        }));

        JLabel replicasLabel = new JLabel("Replicas");
        replicasLabel.setFont(sectionLabelFont);
        gbc.insets = sectionInsets;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 0;
        advancedOptionsPanel.add(replicasLabel, gbc);

        enableReplicas = new JCheckBox("Enable");
        enableReplicas.setSelected(true);
        gbc.insets = internalInsets;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.BASELINE_LEADING;
        advancedOptionsPanel.add(enableReplicas, gbc);

        replicaNum = new ComboBox<Integer>(new Integer[]{1, 2, 3});
        gbc.gridx++;
        advancedOptionsPanel.add(replicaNum, gbc);

        JLabel replicaNumLabel = new JLabel("Number of replica (backup) copies");
        replicaNumLabel.setFont(tipFont);
        replicaNumLabel.setForeground(JBColor.GRAY);
        gbc.gridx++;
        advancedOptionsPanel.add(replicaNumLabel, gbc);

        JPanel replicasErrorPanel = new JPanel(new GridBagLayout());
        replicasErrorPanel.setVisible(nodeCount <= 1);
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        advancedOptionsPanel.add(replicasErrorPanel, gbc);
        replicasErrorLabel = new JTextArea("Warning: you do not have enough data servers or server groups to support this number of replicas.");
        replicasErrorLabel.setEditable(false);
        replicasErrorLabel.setFont(errorFont);
        replicasErrorLabel.setForeground(errorColor);
        replicasErrorLabel.setBackground(replicasErrorPanel.getBackground());
        replicasErrorLabel.setLineWrap(true);
        replicasErrorLabel.setWrapStyleWord(true);
        subGbc.gridx = 0;
        subGbc.gridy = 0;
        subGbc.fill = GridBagConstraints.HORIZONTAL;
        subGbc.anchor = GridBagConstraints.WEST;
        subGbc.insets = JBUI.emptyInsets();
        replicasErrorPanel.add(replicasErrorLabel, subGbc);

        replicaNum.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int num = (int) replicaNum.getSelectedItem();
                replicasErrorPanel.setVisible(num >= nodeCount);
            }
        });

        replicateViewIndexes = new JCheckBox("Replicate view indexes");
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy++;
        advancedOptionsPanel.add(replicateViewIndexes, gbc);

        enableReplicas.addChangeListener(changeEvent -> {
            boolean e = enableReplicas.isSelected();
            replicaNum.setEnabled(e);
            replicateViewIndexes.setEnabled(e);
        });

        JLabel bucketMaxTTL = new JLabel("Bucket Max Time-To-Live");
        bucketMaxTTL.setFont(sectionLabelFont);
        gbc.insets = sectionInsets;
        gbc.gridy++;
        advancedOptionsPanel.add(bucketMaxTTL, gbc);

        enableTTL = new JCheckBox("Enable");
        gbc.insets = internalInsets;
        gbc.gridy++;
        advancedOptionsPanel.add(enableTTL, gbc);

        bucketTTL = new JTextField("0", 5);
        bucketTTL.setEnabled(false);
        enableTTL.addChangeListener(changeEvent -> bucketTTL.setEnabled(enableTTL.isSelected()));
        gbc.insets = JBUI.emptyInsets();
        gbc.gridx++;
        advancedOptionsPanel.add(bucketTTL, gbc);

        JLabel bucketTTLLabel = new JLabel("seconds");
        bucketTTLLabel.setFont(tipFont);
        bucketTTLLabel.setForeground(JBColor.GRAY);
        gbc.gridx++;
        advancedOptionsPanel.add(bucketTTLLabel, gbc);

        JLabel compressionModeSectionLabel = new JLabel("Compression Mode");
        compressionModeSectionLabel.setFont(sectionLabelFont);
        gbc.insets = sectionInsets;
        gbc.gridx = 0;
        gbc.gridy++;
        advancedOptionsPanel.add(compressionModeSectionLabel);

        compressionMode = new ButtonGroup();
        JPanel compressionModesPanel = new JPanel(new GridBagLayout());
        gbc.gridy++;
        gbc.insets = internalInsets;
        advancedOptionsPanel.add(compressionModesPanel, gbc);

        JRadioButton compressionModeOff = new JRadioButton("Off");
        compressionModeOff.setActionCommand(CompressionMode.OFF.toString());
        compressionMode.add(compressionModeOff);
        subGbc.gridx = 0;
        subGbc.gridy = 0;
        subGbc.insets = internalInsets;
        compressionModesPanel.add(compressionModeOff, subGbc);

        JRadioButton compressionModePassive = new JRadioButton("Passive");
        compressionModePassive.setActionCommand(CompressionMode.PASSIVE.toString());
        compressionModePassive.setSelected(true);
        compressionMode.add(compressionModePassive);
        subGbc.gridx++;
        compressionModesPanel.add(compressionModePassive, subGbc);

        JRadioButton compressionModeActive = new JRadioButton("Active");
        compressionModeActive.setActionCommand(CompressionMode.ACTIVE.toString());
        compressionMode.add(compressionModeActive);
        subGbc.gridx++;
        compressionModesPanel.add(compressionModeActive, subGbc);

        JLabel conflictResolutionSectionLabel = new JLabel("Conflict Resolution");
        conflictResolutionSectionLabel.setFont(sectionLabelFont);
        gbc.insets = sectionInsets;
        gbc.gridy++;
        advancedOptionsPanel.add(conflictResolutionSectionLabel, gbc);

        JPanel confictResolutionPanel = new JPanel(new GridBagLayout());
        gbc.gridy++;
        gbc.insets = internalInsets;
        advancedOptionsPanel.add(confictResolutionPanel, gbc);

        conflictResolutionType = new ButtonGroup();
        JRadioButton conflictResolutionSequenceNo = new JRadioButton("Sequence number");
        conflictResolutionSequenceNo.setSelected(true);
        conflictResolutionSequenceNo.setActionCommand(ConflictResolutionType.SEQUENCE_NUMBER.toString());
        conflictResolutionType.add(conflictResolutionSequenceNo);
        subGbc.gridx = 0;
        subGbc.gridy = 0;
        confictResolutionPanel.add(conflictResolutionSequenceNo, subGbc);

        JRadioButton conflictResolutionTimestamp = new JRadioButton("Timestamp");
        conflictResolutionTimestamp.setActionCommand(ConflictResolutionType.TIMESTAMP.toString());
        conflictResolutionType.add(conflictResolutionTimestamp);
        subGbc.gridx++;
        confictResolutionPanel.add(conflictResolutionTimestamp, subGbc);

        JLabel ejectionMethodSectionLabel = new JLabel("Ejection Method");
        ejectionMethodSectionLabel.setFont(sectionLabelFont);
        gbc.gridy++;
        gbc.insets = sectionInsets;
        advancedOptionsPanel.add(ejectionMethodSectionLabel, gbc);

        JPanel ejectionMethodPanel = new JPanel(new GridBagLayout());
        gbc.insets = internalInsets;
        gbc.gridy++;
        advancedOptionsPanel.add(ejectionMethodPanel, gbc);

        ejectionMethod = new ButtonGroup();
        JRadioButton ejectionMethodValue = new JRadioButton("Value-only");
        ejectionMethodValue.setActionCommand(EvictionMode.EVICTION_MODE_VALUE_ONLY.toString());
        ejectionMethod.add(ejectionMethodValue);
        subGbc.gridx = 0;
        subGbc.gridy = 0;
        ejectionMethodPanel.add(ejectionMethodValue, subGbc);

        JRadioButton ejectionMethodFull = new JRadioButton("Full");
        ejectionMethodFull.setActionCommand(EvictionMode.EVICTION_MODE_FULL.toString());
        ejectionMethod.add(ejectionMethodFull);
        subGbc.gridx++;
        ejectionMethodPanel.add(ejectionMethodFull, subGbc);

        /* not supported
        JLabel bucketPrioritySectionLabel = new JLabel("Bucket Priority");
        bucketPrioritySectionLabel.setFont(sectionLabelFont);
        gbc.insets = sectionInsets;
        gbc.gridy++;
        advancedOptionsPanel.add(bucketPrioritySectionLabel, gbc);

        JPanel bucketPriorityPanel = new JPanel(new GridBagLayout());
        gbc.gridy++;
        gbc.insets = internalInsets;
        advancedOptionsPanel.add(bucketPriorityPanel, gbc);

        bucketPriority = new ButtonGroup();
        JRadioButton bucketPriorityDefault = new JRadioButton("Default");
        bucketPriority.add(bucketPriorityDefault);
        subGbc.gridx = 0;
        subGbc.gridy = 0;
        bucketPriorityPanel.add(bucketPriorityDefault, subGbc);

        JRadioButton bucketPriorityHigh = new JRadioButton("High");
        bucketPriority.add(bucketPriorityHigh);
        subGbc.gridx++;
        bucketPriorityPanel.add(bucketPriorityHigh, subGbc);
        */

        JLabel minimumDurabilitySectionLabel = new JLabel("Minimum Durability Level");
        minimumDurabilitySectionLabel.setFont(sectionLabelFont);
        gbc.insets = sectionInsets;
        gbc.gridy++;
        advancedOptionsPanel.add(minimumDurabilitySectionLabel, gbc);

        minimumDurabilityLevel = new ComboBox<String>(new String[]{
                "none", "majority", "majorityAndPersistActive", "persistToMajority"
        });
        gbc.insets = internalInsets;
        gbc.gridy++;
        advancedOptionsPanel.add(minimumDurabilityLevel, gbc);

        /* not supported
        JLabel autoCompactionSectionLabel = new JLabel("Auto-Compaction");
        autoCompactionSectionLabel.setFont(sectionLabelFont);
        gbc.insets = sectionInsets;
        gbc.gridy++;
        advancedOptionsPanel.add(autoCompactionSectionLabel, gbc);

        autoCompactionOverride = new JCheckBox("Override the default auto-compaction settings?");
        gbc.insets = internalInsets;
        gbc.gridy++;
        advancedOptionsPanel.add(autoCompactionOverride, gbc);

        JPanel autoCompactionPanel = new JPanel(new GridBagLayout());
        autoCompactionPanel.setVisible(false);
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        advancedOptionsPanel.add(autoCompactionPanel, gbc);
        autoCompactionOverride.addChangeListener(changeEvent -> {
            autoCompactionPanel.setVisible(autoCompactionOverride.isSelected());
        });


        JPanel compactionOptionsPanel = new JPanel(new GridBagLayout());
        subGbc.gridy++;
        subGbc.insets = internalInsets;
        autoCompactionPanel.add(compactionOptionsPanel, subGbc);
        GridBagConstraints subSubGbc = new GridBagConstraints();
        subSubGbc.gridx = 0;
        subSubGbc.gridy = 0;
        subSubGbc.anchor = GridBagConstraints.WEST;

        JLabel databaseFragmentationSectionLabel = new JLabel("Database Fragmentation");
        databaseFragmentationSectionLabel.setFont(sectionLabelFont);
        subSubGbc.insets = sectionInsets;
        compactionOptionsPanel.add(databaseFragmentationSectionLabel, subSubGbc);

        JTextArea databaseFragmentationTip = new JTextArea("Set the database fragmentation level to determine the point when compaction is triggered.");
        databaseFragmentationTip.setEditable(false);
        databaseFragmentationTip.setBackground(autoCompactionPanel.getBackground());
        databaseFragmentationTip.setLineWrap(true);
        databaseFragmentationTip.setWrapStyleWord(true);
        databaseFragmentationTip.setFont(tipFont);
        databaseFragmentationTip.setForeground(JBColor.GRAY);
        subSubGbc.gridy++;
        subSubGbc.weightx = 1;
        subSubGbc.fill = GridBagConstraints.HORIZONTAL;
        subSubGbc.gridwidth = GridBagConstraints.REMAINDER;
        subSubGbc.anchor = GridBagConstraints.WEST;
        compactionOptionsPanel.add(databaseFragmentationTip, subSubGbc);

        compactionEnableOnPercentFragmentation = new JCheckBox();
        subSubGbc.insets = internalInsets;
        subSubGbc.weightx = 2;
        subSubGbc.gridy++;
        subSubGbc.gridx = 0;
        subSubGbc.fill = GridBagConstraints.NONE;
        subSubGbc.gridwidth = 1;
        compactionEnableOnPercentFragmentation.setSelected(true);
        compactionOptionsPanel.add(compactionEnableOnPercentFragmentation, subSubGbc);

        compactionOnPercentFragmentation = new JSpinner(new SpinnerNumberModel(30, 0, 100, 1));
        editor = (JSpinner.DefaultEditor) compactionOnPercentFragmentation.getEditor();
        editor.getTextField().setColumns(10);
        subSubGbc.insets = JBUI.emptyInsets();
        subSubGbc.gridx++;
        subSubGbc.weightx = 0;
        compactionOptionsPanel.add(compactionOnPercentFragmentation, subSubGbc);
        compactionEnableOnPercentFragmentation.addChangeListener(changeEvent -> compactionOnPercentFragmentation.setEnabled(compactionEnableOnPercentFragmentation.isSelected()));

        JLabel compactionPercentLabel = new JLabel("%");
        compactionPercentLabel.setForeground(JBColor.GRAY);
        subSubGbc.gridx++;
        subSubGbc.weightx = 1;
        compactionOptionsPanel.add(compactionPercentLabel, subSubGbc);

        compactionEnableOnSizeFragmentation = new JCheckBox();
        subSubGbc.insets = internalInsets;
        subSubGbc.weightx = 2;
        subSubGbc.gridy++;
        subSubGbc.gridx = 0;
        compactionOptionsPanel.add(compactionEnableOnSizeFragmentation, subSubGbc);

        compactionOnSizeFragmentation = new JSpinner(new SpinnerNumberModel());
        editor = (JSpinner.DefaultEditor) compactionOnSizeFragmentation.getEditor();
        editor.getTextField().setColumns(10);
        subSubGbc.insets = JBUI.emptyInsets();
        subSubGbc.weightx = 1;
        subSubGbc.gridx++;
        compactionOptionsPanel.add(compactionOnSizeFragmentation, subSubGbc);
        compactionEnableOnSizeFragmentation.addChangeListener(changeEvent ->
                compactionOnSizeFragmentation.setEnabled(compactionEnableOnSizeFragmentation.isSelected()));

        JLabel compactionSizeLabel = new JLabel("MiB");
        compactionSizeLabel.setForeground(JBColor.GRAY);
        subSubGbc.gridx++;
        subSubGbc.weightx = 1;
        subSubGbc.fill = GridBagConstraints.NONE;
        compactionOptionsPanel.add(compactionSizeLabel, subSubGbc);

        JLabel viewFragmentationSectionLabel = new JLabel("View Fragmentation");
        viewFragmentationSectionLabel.setFont(sectionLabelFont);
        subSubGbc.insets = sectionInsets;
        subSubGbc.gridx = 0;
        subSubGbc.gridy++;
        compactionOptionsPanel.add(viewFragmentationSectionLabel, subSubGbc);

        enableViewCompactOnPercentageFragmentation = new JCheckBox();
        subSubGbc.gridx = 0;
        subSubGbc.gridy++;
        subSubGbc.weightx = 2;
        subSubGbc.insets = internalInsets;
        compactionOptionsPanel.add(enableViewCompactOnPercentageFragmentation, subSubGbc);

        compactViewOnPercentFragmentation = new JSpinner(new SpinnerNumberModel(30, 0, 100, 1));
        editor = (JSpinner.DefaultEditor) compactViewOnPercentFragmentation.getEditor();
        editor.getTextField().setColumns(10);
        subSubGbc.weightx = 1;
        subSubGbc.gridx++;
        subSubGbc.insets = JBUI.emptyInsets();
        compactionOptionsPanel.add(compactViewOnPercentFragmentation, subSubGbc);

        JLabel compactionViewPercentLabel = new JLabel("%");
        compactionViewPercentLabel.setForeground(JBColor.GRAY);
        subSubGbc.gridx++;
        subSubGbc.weightx = 1;
        compactionOptionsPanel.add(compactionViewPercentLabel, subSubGbc);

        compactionViewEnableOnSizeFragmentation = new JCheckBox();
        subSubGbc.insets = internalInsets;
        subSubGbc.weightx = 2;
        subSubGbc.gridy++;
        subSubGbc.gridx = 0;
        compactionOptionsPanel.add(compactionViewEnableOnSizeFragmentation, subSubGbc);

        compactionViewOnSizeFragmentation = new JSpinner(new SpinnerNumberModel());
        editor = (JSpinner.DefaultEditor) compactionViewOnSizeFragmentation.getEditor();
        editor.getTextField().setColumns(10);
        subSubGbc.insets = JBUI.emptyInsets();
        subSubGbc.weightx = 1;
        subSubGbc.gridx++;
        compactionOptionsPanel.add(compactionViewOnSizeFragmentation, subSubGbc);
        compactionEnableOnSizeFragmentation.addChangeListener(changeEvent ->
                compactionViewOnSizeFragmentation.setEnabled(compactionViewEnableOnSizeFragmentation.isSelected()));

        JLabel compactionViewSizeLabel = new JLabel("MiB");
        compactionViewSizeLabel.setForeground(JBColor.GRAY);
        subSubGbc.gridx++;
        subSubGbc.weightx = 1;
        subSubGbc.fill = GridBagConstraints.NONE;
        compactionOptionsPanel.add(compactionViewSizeLabel, subSubGbc);

        JLabel compactTimeSection = new JLabel("Time Interval");
        compactTimeSection.setFont(sectionLabelFont);
        subSubGbc.gridy++;
        subSubGbc.gridx = 0;
        subSubGbc.insets = sectionInsets;
        compactionOptionsPanel.add(compactTimeSection, subSubGbc);

        enableCompactTimeInterval = new JCheckBox("Set the time interval for when compaction is allowed to run");
        subSubGbc.gridy++;
        subSubGbc.gridwidth = GridBagConstraints.REMAINDER;
        subSubGbc.insets = internalInsets;
        compactionOptionsPanel.add(enableCompactTimeInterval, subSubGbc);

        subSubGbc.gridy++;
        compactionOptionsPanel.add(new JLabel("Start Time"), subSubGbc);

        JPanel timeStartPanel = new JPanel();
        subSubGbc.gridy++;
        subSubGbc.gridx = 0;
        compactionOptionsPanel.add(timeStartPanel, subSubGbc);
        compactIntervalStartHour = new JSpinner(new SpinnerNumberModel(0, 0, 23, 1));
        compactIntervalStartHour.setEnabled(false);
        timeStartPanel.add(compactIntervalStartHour);

        timeStartPanel.add(new JLabel(":"));

        compactIntervalStartMinute = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
        compactIntervalStartMinute.setEnabled(false);
        timeStartPanel.add(compactIntervalStartMinute);

        subSubGbc.gridy++;
        compactionOptionsPanel.add(new JLabel("End Time"), subSubGbc);

        JPanel timeEndPanel = new JPanel();
        subSubGbc.gridy++;
        subSubGbc.gridx = 0;
        compactionOptionsPanel.add(timeEndPanel, subSubGbc);
        compactIntervalEndHour = new JSpinner(new SpinnerNumberModel(0, 0, 23, 1));
        compactIntervalEndHour.setEnabled(false);
        timeEndPanel.add(compactIntervalEndHour);

        timeEndPanel.add(new JLabel(":"));

        compactIntervalEndMinute = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
        compactIntervalEndMinute.setEnabled(false);
        timeEndPanel.add(compactIntervalEndMinute);

        subSubGbc.gridy++;
        subSubGbc.fill = GridBagConstraints.HORIZONTAL;
        subGbc.gridwidth = GridBagConstraints.REMAINDER;
        abortCompactionsAtIntervalEnd = new JCheckBox("Abort compaction exceeding the set time interval");
        abortCompactionsAtIntervalEnd.setEnabled(false);
        compactionOptionsPanel.add(abortCompactionsAtIntervalEnd, subSubGbc);

        enableCompactTimeInterval.addChangeListener(changeEvent -> {
            boolean e = enableCompactTimeInterval.isSelected();
            compactIntervalStartHour.setEnabled(e);
            compactIntervalStartMinute.setEnabled(e);
            compactIntervalEndHour.setEnabled(e);
            compactIntervalEndMinute.setEnabled(e);
            abortCompactionsAtIntervalEnd.setEnabled(e);
        });

        compactInParallel = new JCheckBox("Compact buckets and views indexes in parallel");
        subSubGbc.gridy++;
        subGbc.gridwidth = GridBagConstraints.REMAINDER;
        compactionOptionsPanel.add(compactInParallel, subSubGbc);

        JTextArea gsiCompactNote = new JTextArea();
        gsiCompactNote.setEditable(false);
        gsiCompactNote.setWrapStyleWord(true);
        gsiCompactNote.setBackground(JBColor.LIGHT_GRAY);
        gsiCompactNote.setText("NOTE FOR GSI INDEXES :\nAuto-compaction settings are unnecessary for memory-optimized and plasma-based indexes.");
        gsiCompactNote.setLineWrap(true);
        gsiCompactNote.setBorder(BorderFactory.createLineBorder(JBColor.LIGHT_GRAY, 10));
        subSubGbc.fill = GridBagConstraints.HORIZONTAL;
        subSubGbc.gridy++;
        subSubGbc.gridwidth = GridBagConstraints.REMAINDER;
        subSubGbc.weightx = 1;
        subSubGbc.insets = JBUI.insets(10, 5);
        compactionOptionsPanel.add(gsiCompactNote, subSubGbc);

        JLabel metadataPurgeSection = new JLabel("Metadata Purge Interval");
        metadataPurgeSection.setFont(sectionLabelFont);
        subSubGbc.gridy++;
        subSubGbc.insets = sectionInsets;
        compactionOptionsPanel.add(metadataPurgeSection, subSubGbc);

        JPanel metadataPurgePanel = new JPanel();
        subSubGbc.fill = GridBagConstraints.NONE;
        subSubGbc.gridwidth = 1;
        subSubGbc.gridy++;
        subSubGbc.insets = internalInsets;
        compactionOptionsPanel.add(metadataPurgePanel, subSubGbc);
        metadataPurgeInterval = new JTextField("3", 4);
        metadataPurgePanel.add(metadataPurgeInterval);

        JLabel metadataPurgeUnit = new JLabel("days, min 0.04 (1 hour) max 60");
        metadataPurgeUnit.setFont(tipFont);
        metadataPurgeUnit.setForeground(JBColor.GRAY);
        metadataPurgePanel.add(metadataPurgeUnit);
        */

        JLabel flushSection = new JLabel("Flush");
        flushSection.setFont(sectionLabelFont);
        gbc.insets = sectionInsets;
        gbc.gridy++;
        advancedOptionsPanel.add(flushSection, gbc);

        enableFlush = new JCheckBox("Enable");
        gbc.insets = internalInsets;
        gbc.gridy++;
        advancedOptionsPanel.add(enableFlush, gbc);


        return scrollPane;
    }

    private class NameChangeListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent documentEvent) {
            bucketNameError.setVisible(bucketName.getText().trim().isEmpty());
        }

        @Override
        public void removeUpdate(DocumentEvent documentEvent) {
            bucketNameError.setVisible(bucketName.getText().trim().isEmpty());
        }

        @Override
        public void changedUpdate(DocumentEvent documentEvent) {
        }
    }
}
