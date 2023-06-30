package com.couchbase.intellij.tree;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.bucket.BucketManager;
import com.couchbase.intellij.database.DataLoader;
import com.couchbase.intellij.persistence.SavedCluster;
import com.couchbase.intellij.tools.CBTools;
import com.couchbase.intellij.tools.github.CloneDemoRepo;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.ui.JBColor;
import com.intellij.ui.TitlePanel;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.PositionTracker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.intellij.database.ActiveCluster;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.ui.JBUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;
import com.intellij.ui.components.JBTextField;
import com.intellij.openapi.ui.popup.Balloon;

import static utils.ProcessUtils.printOutput;

public class PillowFightDialog extends DialogWrapper {
    private ComboBox<String> bucketComboBox;
    private ComboBox<String> durabilityComboBox;
    private JBTextField persistToTextField;
    private JBTextField batchSizeTextField;
    private JBTextField numberItemsTextField;
    private JBTextField keyPrefixTextField;
    private JBTextField numberThreadsTextField;
    private JBTextField percentageTextField;
    private ComboBox<String> noPopulationComboBox;
    private ComboBox<String> populateOnlyComboBox;
    private JBTextField minSizeTextField;
    private JBTextField maxSizeTextField;
    //private ComboBox<String> pauseAtEndComboBox;
    private JBTextField numberCyclesTextField;
    private ComboBox<String> sequentialComboBox;
    private JBTextField startAtTextField;
    private ComboBox<String> timingsComboBox;
    private JBTextField expiryTextField;
    private JBTextField replicateToTextField;
    private JBTextField lockTextField;
    private ComboBox<String> jsonComboBox;
    private ComboBox<String> noopComboBox;
    private ComboBox<String> subdocComboBox;
    private JBTextField pathcountTextField;
    private JLabel errorMessage;

    @Override
    protected Action[] createActions() {
        Action okAction = getOKAction();
        okAction.putValue(Action.NAME, "Start");
        return new Action[]{okAction, getCancelAction()};
    }
    protected PillowFightDialog(Project project) {
        super(project);

        setTitle("Pillow Fight");
        bucketComboBox = new ComboBox<>();

        try {
            Set<String> bucketNamesSet = ActiveCluster.getInstance().get().buckets().getAllBuckets().keySet();
            String[] bucketNamesArray = bucketNamesSet.toArray(new String[bucketNamesSet.size()]);
            for (String s : bucketNamesArray) {
                bucketComboBox.addItem(s);
            }
        } catch (Exception e) {
            Messages.showErrorDialog("Could not connect to the cluster. Please check your network connectivity, " +
                    " if the cluster is active or if the credentials are still valid.", "Couchbase Plugin Error");
            System.err.println(e);
        }

        durabilityComboBox = new ComboBox<>();
        durabilityComboBox.addItem("none");
        durabilityComboBox.addItem("majority");
        durabilityComboBox.addItem("majority_and_persist_to_active");
        durabilityComboBox.addItem("persist_to_majority");

        persistToTextField = new JBTextField();
        persistToTextField.getDocument().addDocumentListener(new DocumentListener() {
            final Color originalColor = persistToTextField.getForeground();

            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                validatePersistToTextField(originalColor);
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                validatePersistToTextField(originalColor);
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                validatePersistToTextField(originalColor);
            }
        });

        batchSizeTextField = new JBTextField();
        batchSizeTextField.getDocument().addDocumentListener(new DocumentListener() {
            final Color originalColor = batchSizeTextField.getForeground();

            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                validateBatchSizeTextField(originalColor);
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                validateBatchSizeTextField(originalColor);
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                validateBatchSizeTextField(originalColor);
            }
        });

        numberItemsTextField = new JBTextField();
        numberItemsTextField.getDocument().addDocumentListener(new DocumentListener() {
            final Color originalColor = numberItemsTextField.getForeground();

            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                validateNumberItemsTextField(originalColor);
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                validateNumberItemsTextField(originalColor);
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                validateNumberItemsTextField(originalColor);
            }
        });

        keyPrefixTextField = new JBTextField();

        numberThreadsTextField = new JBTextField();
        numberThreadsTextField.getDocument().addDocumentListener(new DocumentListener() {
            final Color originalColor = numberThreadsTextField.getForeground();

            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                validateNumberThreadsTextField(originalColor);
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                validateNumberThreadsTextField(originalColor);
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                validateNumberThreadsTextField(originalColor);
            }
        });

        percentageTextField = new JBTextField();
        percentageTextField.getDocument().addDocumentListener(new DocumentListener() {
            final Color originalColor = percentageTextField.getForeground();

            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                validatePercentageTextField(originalColor);
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                validatePercentageTextField(originalColor);
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                validatePercentageTextField(originalColor);
            }
        });

        noPopulationComboBox = new ComboBox<>();
        noPopulationComboBox.addItem("disable");
        noPopulationComboBox.addItem("enable");

        populateOnlyComboBox = new ComboBox<>();
        populateOnlyComboBox.addItem("disable");
        populateOnlyComboBox.addItem("enable");

        minSizeTextField = new JBTextField();
        minSizeTextField.getDocument().addDocumentListener(new DocumentListener() {
            final Color originalColor = minSizeTextField.getForeground();

            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                validateMinSizeTextField(originalColor);
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                validateMinSizeTextField(originalColor);
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                validateMinSizeTextField(originalColor);
            }
        });

        maxSizeTextField = new JBTextField();
        maxSizeTextField.getDocument().addDocumentListener(new DocumentListener() {
            final Color originalColor = maxSizeTextField.getForeground();

            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                validateMaxSizeTextField(originalColor);
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                validateMaxSizeTextField(originalColor);
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                validateMaxSizeTextField(originalColor);
            }
        });

        //--pause-at-end option removed
        /*
        pauseAtEndComboBox = new ComboBox<>();
        pauseAtEndComboBox.addItem("enable");
        pauseAtEndComboBox.addItem("disable");
         */

        numberCyclesTextField = new JBTextField();
        numberCyclesTextField.getDocument().addDocumentListener(new DocumentListener() {
            final Color originalColor = numberCyclesTextField.getForeground();

            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                validateNumberCyclesTextField(originalColor);
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                validateNumberCyclesTextField(originalColor);
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                validateNumberCyclesTextField(originalColor);
            }
        });

        sequentialComboBox = new ComboBox<>();
        sequentialComboBox.addItem("disable");
        sequentialComboBox.addItem("enable");

        startAtTextField = new JBTextField();
        startAtTextField.getDocument().addDocumentListener(new DocumentListener() {
            final Color originalColor = startAtTextField.getForeground();

            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                validateStartAtTextField(originalColor);
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                validateStartAtTextField(originalColor);
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                validateStartAtTextField(originalColor);
            }
        });

        timingsComboBox = new ComboBox<>();
        timingsComboBox.addItem("disable");
        timingsComboBox.addItem("enable");

        expiryTextField = new JBTextField();
        expiryTextField.getDocument().addDocumentListener(new DocumentListener() {
            final Color originalColor = expiryTextField.getForeground();

            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                validateExpiryTextField(originalColor);
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                validateExpiryTextField(originalColor);
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                validateExpiryTextField(originalColor);
            }
        });

        replicateToTextField = new JBTextField();
        replicateToTextField.getDocument().addDocumentListener(new DocumentListener() {
            final Color originalColor = replicateToTextField.getForeground();

            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                validateReplicateToTextField(originalColor);
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                validateReplicateToTextField(originalColor);
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                validateReplicateToTextField(originalColor);
            }
        });

        lockTextField = new JBTextField();
        lockTextField.getDocument().addDocumentListener(new DocumentListener() {
            final Color originalColor = lockTextField.getForeground();

            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                validateLockTextField(originalColor);
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                validateLockTextField(originalColor);
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                validateLockTextField(originalColor);
            }
        });

        jsonComboBox = new ComboBox<>();
        jsonComboBox.addItem("disable");
        jsonComboBox.addItem("enable");

        noopComboBox = new ComboBox<>();
        noopComboBox.addItem("disable");
        noopComboBox.addItem("enable");

        subdocComboBox = new ComboBox<>();
        subdocComboBox.addItem("disable");
        subdocComboBox.addItem("enable");

        pathcountTextField = new JBTextField();
        pathcountTextField.getDocument().addDocumentListener(new DocumentListener() {
            final Color originalColor = pathcountTextField.getForeground();

            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                validatePathcountTextField(originalColor);
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                validatePathcountTextField(originalColor);
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                validatePathcountTextField(originalColor);
            }
        });

        init();

            /*
        } catch (Exception e) {
            Messages.showErrorDialog("Could not connect to the cluster. Please check your network connectivity, " +
                    " if the cluster is active or if the credentials are still valid.", "Couchbase Plugin Error");
            System.err.println(e);
        }
             */

        /*
        try {
            Cluster cluster = ActiveCluster.getInstance().get();
            if (cluster==null) {
                System.err.println("Active cluster is established. Connecting now...");
                // TODO: Handle multiple saved clusters
                try {
                    Map<String, SavedCluster> savedClusters = DataLoader.getSavedClusters();
                    String savedKey = (String) savedClusters.keySet().toArray()[0];
                    ActiveCluster.getInstance().connect((SavedCluster) savedClusters.get(savedKey));
                    cluster = ActiveCluster.getInstance().get();
                    Messages.showErrorDialog("Could not connect to the cluster. Please check your network connectivity, " +
                            " if the cluster is active or if the credentials are still valid.", "Couchbase Plugin Error");
                } catch (Exception e) {
                    System.err.println("Error" + e);
                }
            }
            BucketManager manager = cluster.buckets();
            Set<String> bucketNamesSet = manager.getAllBuckets().keySet();
            String[] bucketNamesArray = bucketNamesSet.toArray(new String[bucketNamesSet.size()]);
            for (int i = 0; i < bucketNamesArray.length; i++) {
                bucketComboBox.addItem(bucketNamesArray[i]);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
         */
    }

    private void validatePersistToTextField(Color originalColor) {
        String input = persistToTextField.getText();
        try {
            if (input == null || input.trim().isEmpty()) {
                persistToTextField.setForeground(originalColor);
                setOKActionEnabled(true);
                return;
            }
            if (Integer.parseInt(input) < -1) {
                persistToTextField.setForeground(JBColor.RED);
                setOKActionEnabled(false);
            } else {
                persistToTextField.setForeground(originalColor);
                setOKActionEnabled(true);
            }
        } catch (NumberFormatException e) {
            persistToTextField.setForeground(JBColor.RED);
            setOKActionEnabled(false);
        }
    }

    private void validateBatchSizeTextField(Color originalColor) {
        String input = batchSizeTextField.getText();
        try {
            if (input == null || input.trim().isEmpty()) {
                batchSizeTextField.setForeground(originalColor);
                setOKActionEnabled(true);
                return;
            }
            if (Integer.parseInt(input) < 0) {
                batchSizeTextField.setForeground(JBColor.RED);
                setOKActionEnabled(false);
            } else {
                batchSizeTextField.setForeground(originalColor);
                setOKActionEnabled(true);
            }
        } catch (NumberFormatException e) {
            batchSizeTextField.setForeground(JBColor.RED);
            setOKActionEnabled(false);
        }
    }

    private void validateNumberItemsTextField(Color originalColor) {
        String input = numberItemsTextField.getText();
        try {
            if (input == null || input.trim().isEmpty()) {
                numberItemsTextField.setForeground(originalColor);
                setOKActionEnabled(true);
                return;
            }
            if (Integer.parseInt(input) < 0) {
                numberItemsTextField.setForeground(JBColor.RED);
                setOKActionEnabled(false);
            } else {
                numberItemsTextField.setForeground(originalColor);
                setOKActionEnabled(true);
            }
        } catch (NumberFormatException e) {
            numberItemsTextField.setForeground(JBColor.RED);
            setOKActionEnabled(false);
        }
    }

    private void validateNumberThreadsTextField(Color originalColor) {
        String input = numberThreadsTextField.getText();
        try {
            if (input == null || input.trim().isEmpty()) {
                numberThreadsTextField.setForeground(originalColor);
                setOKActionEnabled(true);
                return;
            }
            if (Integer.parseInt(input) < 0) {
                numberThreadsTextField.setForeground(JBColor.RED);
                setOKActionEnabled(false);
            } else {
                numberThreadsTextField.setForeground(originalColor);
                setOKActionEnabled(true);
            }
        } catch (NumberFormatException e) {
            numberThreadsTextField.setForeground(JBColor.RED);
            setOKActionEnabled(false);
        }
    }

    private void validatePercentageTextField(Color originalColor) {
        String input = percentageTextField.getText();
        try {
            if (input == null || input.trim().isEmpty()) {
                percentageTextField.setForeground(originalColor);
                setOKActionEnabled(true);
                return;
            }
            if (Integer.parseInt(input) < 0) {
                percentageTextField.setForeground(JBColor.RED);
                setOKActionEnabled(false);
            } else {
                percentageTextField.setForeground(originalColor);
                setOKActionEnabled(true);
            }
        } catch (NumberFormatException e) {
            percentageTextField.setForeground(JBColor.RED);
            setOKActionEnabled(false);
        }
    }

    private void validateMinSizeTextField(Color originalColor) {
        String input = minSizeTextField.getText();
        try {
            if (input == null || input.trim().isEmpty()) {
                minSizeTextField.setForeground(originalColor);
                setOKActionEnabled(true);
                return;
            }
            if (Integer.parseInt(input) < 0) {
                minSizeTextField.setForeground(JBColor.RED);
                setOKActionEnabled(false);
            } else {
                minSizeTextField.setForeground(originalColor);
                setOKActionEnabled(true);
            }
        } catch (NumberFormatException e) {
            minSizeTextField.setForeground(JBColor.RED);
            setOKActionEnabled(false);
        }
    }

    private void validateMaxSizeTextField(Color originalColor) {
        String input = maxSizeTextField.getText();
        try {
            if (input == null || input.trim().isEmpty()) {
                maxSizeTextField.setForeground(originalColor);
                setOKActionEnabled(true);
                return;
            }
            if (Integer.parseInt(input) < 0) {
                maxSizeTextField.setForeground(JBColor.RED);
                setOKActionEnabled(false);
            } else {
                maxSizeTextField.setForeground(originalColor);
                setOKActionEnabled(true);
            }
        } catch (NumberFormatException e) {
            maxSizeTextField.setForeground(JBColor.RED);
            setOKActionEnabled(false);
        }
    }

    private void validateNumberCyclesTextField(Color originalColor) {
        String input = numberCyclesTextField.getText();
        try {
            if (input == null || input.trim().isEmpty()) {
                numberCyclesTextField.setForeground(originalColor);
                setOKActionEnabled(true);
                return;
            }
            if (Integer.parseInt(input) < -1) {
                numberCyclesTextField.setForeground(JBColor.RED);
                setOKActionEnabled(false);
            } else {
                numberCyclesTextField.setForeground(originalColor);
                setOKActionEnabled(true);
            }
        } catch (NumberFormatException e) {
            numberCyclesTextField.setForeground(JBColor.RED);
            setOKActionEnabled(false);
        }
    }

    private void validateStartAtTextField(Color originalColor) {
        String input = startAtTextField.getText();
        try {
            if (input == null || input.trim().isEmpty()) {
                startAtTextField.setForeground(originalColor);
                setOKActionEnabled(true);
                return;
            }
            if (Integer.parseInt(input) < 0) {
                startAtTextField.setForeground(JBColor.RED);
                setOKActionEnabled(false);
            } else {
                startAtTextField.setForeground(originalColor);
                setOKActionEnabled(true);
            }
        } catch (NumberFormatException e) {
            startAtTextField.setForeground(JBColor.RED);
            setOKActionEnabled(false);
        }
    }

    private void validateExpiryTextField(Color originalColor) {
        String input = expiryTextField.getText();
        try {
            if (input == null || input.trim().isEmpty()) {
                expiryTextField.setForeground(originalColor);
                setOKActionEnabled(true);
                return;
            }
            if (Integer.parseInt(input) < 0) {
                expiryTextField.setForeground(JBColor.RED);
                setOKActionEnabled(false);
            } else {
                expiryTextField.setForeground(originalColor);
                setOKActionEnabled(true);
            }
        } catch (NumberFormatException e) {
            expiryTextField.setForeground(JBColor.RED);
            setOKActionEnabled(false);
        }
    }

    private void validateReplicateToTextField(Color originalColor) {
        String input = replicateToTextField.getText();
        try {
            if (input == null || input.trim().isEmpty()) {
                replicateToTextField.setForeground(originalColor);
                setOKActionEnabled(true);
                return;
            }
            if (Integer.parseInt(input) < -1) {
                replicateToTextField.setForeground(JBColor.RED);
                setOKActionEnabled(false);
            } else {
                replicateToTextField.setForeground(originalColor);
                setOKActionEnabled(true);
            }
        } catch (NumberFormatException e) {
            replicateToTextField.setForeground(JBColor.RED);
            setOKActionEnabled(false);
        }
    }

    private void validateLockTextField(Color originalColor) {
        String input = lockTextField.getText();
        try {
            if (input == null || input.trim().isEmpty()) {
                lockTextField.setForeground(originalColor);
                setOKActionEnabled(true);
                return;
            }
            if (Integer.parseInt(input) < 0) {
                lockTextField.setForeground(JBColor.RED);
                setOKActionEnabled(false);
            } else {
                lockTextField.setForeground(originalColor);
                setOKActionEnabled(true);
            }
        } catch (NumberFormatException e) {
            lockTextField.setForeground(JBColor.RED);
            setOKActionEnabled(false);
        }
    }

    private void validatePathcountTextField(Color originalColor) {
        String input = pathcountTextField.getText();
        try {
            if (input == null || input.trim().isEmpty()) {
                pathcountTextField.setForeground(originalColor);
                setOKActionEnabled(true);
                return;
            }
            if (Integer.parseInt(input) < 0) {
                pathcountTextField.setForeground(JBColor.RED);
                setOKActionEnabled(false);
            } else {
                pathcountTextField.setForeground(originalColor);
                setOKActionEnabled(true);
            }
        } catch (NumberFormatException e) {
            pathcountTextField.setForeground(JBColor.RED);
            setOKActionEnabled(false);
        }
    }
    private static JBPanel createLabelWithBalloon(String labelText, String hintText) {
        JBLabel label = new JBLabel(labelText);
        JBLabel questionMark = new JBLabel("?");
        questionMark.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                showBalloonHint(questionMark, hintText);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hideBalloonHint();
            }
        });

        JBPanel panel = new JBPanel(new BorderLayout());
        panel.add(label, BorderLayout.WEST);
        panel.add(questionMark, BorderLayout.EAST);

        return panel;
    }

    private static Balloon balloonHint;

    private static void showBalloonHint(JBLabel questionMark, String hintText) {
        JBPopupFactory factory = JBPopupFactory.getInstance();
        JTextArea textArea = new JTextArea(hintText);
        textArea.setBackground(UIManager.getColor("ToolTip.background"));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setColumns(40);
        textArea.setSize(textArea.getPreferredSize());

        balloonHint = factory.createBalloonBuilder(textArea).setFillColor(UIManager.getColor("ToolTip.background")).setAnimationCycle(0).createBalloon();
        balloonHint.show(factory.guessBestPopupLocation(questionMark), Balloon.Position.below);
    }

    private static void hideBalloonHint() {
        if (balloonHint != null) {
            balloonHint.hide();
            balloonHint = null;
        }
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = JBUI.insets(5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        //panel.add(new JLabel("Available Buckets: "), gbc);
        panel.add(createLabelWithBalloon("Available Buckets: ", "A bucket is the fundamental space for storing data in Couchbase Server"), gbc);

        gbc.gridx++;
        panel.add(bucketComboBox, gbc);

        gbc.gridx--;
        gbc.gridy++;
        //panel.add(new JLabel("Durability: "), gbc);
        panel.add(createLabelWithBalloon("Durability: ", "Specify durability level for mutation operations"), gbc);

        gbc.gridx++;
        panel.add(durabilityComboBox, gbc);

        gbc.gridx--;
        gbc.gridy++;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        JPanel collapsiblePanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbcCollapsable = new GridBagConstraints();
        gbcCollapsable.gridx = 0;
        gbcCollapsable.gridy = 0;
        gbcCollapsable.insets = JBUI.insets(5);
        gbcCollapsable.anchor = GridBagConstraints.WEST;
        gbcCollapsable.fill = GridBagConstraints.HORIZONTAL;
        gbcCollapsable.weightx = 1.0;
        gbcCollapsable.weighty = 1.0;

        collapsiblePanel.setBorder(BorderFactory.createTitledBorder("Advanced Options"));
        collapsiblePanel.setVisible(false);

        //panel.add(new JLabel("Persist-to: "), gbc);
        collapsiblePanel.add(createLabelWithBalloon("Persist-to: ", "Wait until the item has been persisted to at least NUMNODES nodes' disk. If NUMNODES is 1 then wait until only the master node has persisted the item for this key. You may not specify a number greater than the number of nodes actually in the cluster. -1 is special value, which mean to use all available nodes."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(persistToTextField, gbcCollapsable);
        persistToTextField.getEmptyText().setText("Enter a number -1 or greater");

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        //panel.add(new JLabel("Batch Size: "), gbc);
        collapsiblePanel.add(createLabelWithBalloon("Batch Size: ", "This controls how many commands are scheduled per cycles. To simulate one operation at a time, set this value to 1."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(batchSizeTextField, gbcCollapsable);
        batchSizeTextField.getEmptyText().setText("Enter a number 0 or greater");

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        //panel.add(new JLabel("Number of Items: "), gbc);
        collapsiblePanel.add(createLabelWithBalloon("Number of Items: ", "Set the total number of items the workload will access within the cluster. This will also determine the working set size at the server and may affect disk latencies if set to a high number."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(numberItemsTextField, gbcCollapsable);
        numberItemsTextField.getEmptyText().setText("Enter a number 0 or greater");

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        //panel.add(new JLabel("Key prefix: "), gbc);
        collapsiblePanel.add(createLabelWithBalloon("Key Prefix: ", "Set the prefix to prepend to all keys in the cluster. Useful if you do not wish the items to conflict with existing data."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(keyPrefixTextField, gbcCollapsable);
        keyPrefixTextField.getEmptyText().setText("Enter a prefix to prepend");

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        //panel.add(new JLabel("Number of Threads: "), gbc);
        collapsiblePanel.add(createLabelWithBalloon("Number of Threads: ", "Set the number of threads (and thus the number of client instances) to run concurrently. Each thread is assigned its own client object."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(numberThreadsTextField, gbcCollapsable);
        numberThreadsTextField.getEmptyText().setText("Enter a number 0 or greater");

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        //panel.add(new JLabel("Percentage: "), gbc);
        collapsiblePanel.add(createLabelWithBalloon("Percentage", "The percentage of operations which should be mutations. A value of 100 means only mutations while a value of 0 means only retrievals."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(percentageTextField, gbcCollapsable);
        percentageTextField.getEmptyText().setText("Enter a number 0 or greater");

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        //panel.add(new JLabel("No Population: "), gbc);
        collapsiblePanel.add(createLabelWithBalloon("No Population: ", "By default cbc-pillowfight will load all the items (see --num-items) into the cluster and then begin performing the normal workload. Specifying this option bypasses this stage. Useful if the items have already been loaded in a previous run."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(noPopulationComboBox, gbcCollapsable);

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        //panel.add(new JLabel("Populate only: "), gbc);
        collapsiblePanel.add(createLabelWithBalloon("Populate Only: ", "Stop after population. Useful to populate buckets with large amounts of data."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(populateOnlyComboBox, gbcCollapsable);

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        //panel.add(new JLabel("Min Size: "), gbc);
        collapsiblePanel.add(createLabelWithBalloon("Min Size: ", "Specify the minimum size to be stored into the cluster. This is typically a range, in which case each value generated will be between Min Size and Max Size bytes."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(minSizeTextField, gbcCollapsable);
        minSizeTextField.getEmptyText().setText("Enter a number 0 or greater");

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        collapsiblePanel.add(new JLabel("Max Size: "), gbcCollapsable);
        collapsiblePanel.add(createLabelWithBalloon("Max Size: ", "Specify the maximum size to be stored into the cluster. This is typically a range, in which case each value generated will be between Min Size and Max Size bytes."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(maxSizeTextField, gbcCollapsable);
        maxSizeTextField.getEmptyText().setText("Enter a number 0 or greater");

        /*
        gbc.gridx = 0;
        gbc.gridy = 12;
        panel.add(new JLabel("Pause At End: "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 12;
        panel.add(pauseAtEndComboBox, gbc);
        */

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        //panel.add(new JLabel("Number of Cycles: "), gbc);
        collapsiblePanel.add(createLabelWithBalloon("Number of Cycles: ", "Specify the number of times the workload should cycle. During each cycle an amount of --batch-size operations are executed. Setting this to -1 will cause the workload to run infinitely."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(numberCyclesTextField, gbcCollapsable);
        numberCyclesTextField.getEmptyText().setText("Enter a number -1 or greater");

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        //panel.add(new JLabel("Sequential: "), gbc);
        collapsiblePanel.add(createLabelWithBalloon("Sequential: ", "Specify that the access pattern should be done in a sequential manner. This is useful for bulk-loading many documents in a single server."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(sequentialComboBox, gbcCollapsable);

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        //panel.add(new JLabel("Start At: "), gbc);
        collapsiblePanel.add(createLabelWithBalloon("Start At: ", "This specifies the starting offset for the items. The items by default are generated with the key prefix (--key-prefix) up to the number of items (--num-items). The --start-at value will increase the lower limit of the items. This is useful to resume a previously cancelled load operation."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(startAtTextField, gbcCollapsable);
        startAtTextField.getEmptyText().setText("Enter a number 0 or greater");

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        //panel.add(new JLabel("Timings: "), gbc);
        collapsiblePanel.add(createLabelWithBalloon("Timings: ", "Enabled timing recorded. Timing histogram will be dumped to STDERR on SIGQUIT (CTRL-/). When specified second time, it will dump a histogram of command timings and latencies to the screen every second."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(timingsComboBox, gbcCollapsable);

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        //panel.add(new JLabel("Expiry: "), gbc);
        collapsiblePanel.add(createLabelWithBalloon("Expiry: ", "Set the expiration time on the document for SECONDS when performing each operation. Note that setting this too low may cause not-found errors to appear on the screen."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(expiryTextField, gbcCollapsable);
        expiryTextField.getEmptyText().setText("Enter a number 0 or greater");

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        //panel.add(new JLabel("Replicate To: "), gbc);
        collapsiblePanel.add(createLabelWithBalloon("Replicate To: ", "Wait until the item has been replicated to at least NREPLICAS replica nodes. The bucket must be configured with at least one replica, and at least NREPLICAS replica nodes must be online. -1 is special value, which mean to use all available replicas."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(replicateToTextField, gbcCollapsable);
        replicateToTextField.getEmptyText().setText("Enter a number -1 or greater");

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        //panel.add(new JLabel("Lock: "), gbc);
        collapsiblePanel.add(createLabelWithBalloon("Lock: ", "This will retrieve and lock an item before update, making it inaccessible for modification until the update completed, or TIME has passed."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(lockTextField, gbcCollapsable);
        lockTextField.getEmptyText().setText("Enter a number 0 or greater");

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        //panel.add(new JLabel("JSON: "), gbc);
        collapsiblePanel.add(createLabelWithBalloon("JSON: ", "Make pillowfight store document as JSON rather than binary. This will allow the documents to nominally be analyzed by other Couchbase services such as Query and MapReduce."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(jsonComboBox, gbcCollapsable);

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        //panel.add(new JLabel("NOOP: "), gbc);
        collapsiblePanel.add(createLabelWithBalloon("NOOP: ", "Use couchbase NOOP operations when running the workload. This mode ignores population, and all other document operations. Useful as the most lightweight workload."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(noopComboBox, gbcCollapsable);

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        //panel.add(new JLabel("Subdoc: "), gbc);
        collapsiblePanel.add(createLabelWithBalloon("Subdoc: ", "Use couchbase sub-document operations when running the workload. In this mode pillowfight will use Couchbase sub-document operations to perform gets and sets of data. This option must be used with --json"), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(subdocComboBox, gbcCollapsable);

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        //panel.add(new JLabel("Pathcount: "), gbc);
        collapsiblePanel.add(createLabelWithBalloon("Pathcount: ", "Specify the number of paths a single sub-document operation should contain. By default, each subdoc operation operates on only a single path within the document. You can specify multiple paths to atomically executed multiple subdoc operations within a single command."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(pathcountTextField, gbcCollapsable);
        pathcountTextField.getEmptyText().setText("Enter a number 0 or greater");

        JButton toggleButton = new JButton("Show Advanced Options");
        toggleButton.addActionListener(e -> {
            boolean isVisible = collapsiblePanel.isVisible();
            collapsiblePanel.setVisible(!isVisible);
            if (isVisible) {
                toggleButton.setText("Show Advanced Options");
            } else {
                toggleButton.setText("Hide Advanced Options");
            }
        });

        panel.add(toggleButton, gbc);
        gbc.gridy++;
        panel.add(collapsiblePanel, gbc);

        gbc.gridx--;
        gbc.gridy++;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        errorMessage = new JLabel("");
        errorMessage.setForeground(Color.decode("#FF4444"));
        panel.add(errorMessage, gbc);

        return panel;
    }

    @Override
    protected void doOKAction() {
        List<String> errors = new ArrayList<>();
        if (bucketComboBox.getItemCount() == 0) {
            errors.add("No available buckets. Please connect to a cluster.");
        }

        if (persistToTextField.getText() == null || persistToTextField.getText().trim().isEmpty()) {

        } else {
            try {
                if (Integer.parseInt(persistToTextField.getText()) < -1){
                    errors.add("Persist-to value must be -1 or greater");
                }
            } catch (NumberFormatException e){

            }
        }

        if (batchSizeTextField.getText() == null || batchSizeTextField.getText().trim().isEmpty()) {

        } else {
            try {
                if (Integer.parseInt(batchSizeTextField.getText()) < 0){
                    errors.add("Batch size value must be 0 or greater");
                }
            } catch (NumberFormatException e) {

            }
        }

        if (numberItemsTextField.getText() == null || numberItemsTextField.getText().trim().isEmpty()) {

        } else {
            try {
                if (Integer.parseInt(numberItemsTextField.getText()) < 0){
                    errors.add("Number of Items value must be 0 or greater");
                }
            } catch (NumberFormatException e) {

            }
        }

        if (numberThreadsTextField.getText() == null || numberThreadsTextField.getText().trim().isEmpty()) {

        } else {
            try {
                if (Integer.parseInt(numberThreadsTextField.getText()) < 0){
                    errors.add("Number of Threads value must be 0 or greater");
                }
            } catch (NumberFormatException e) {

            }
        }

        if (percentageTextField.getText() == null || percentageTextField.getText().trim().isEmpty()) {

        } else {
            try {
                if (Integer.parseInt(percentageTextField.getText()) < 0){
                    errors.add("Percentage value must be 0 or greater");
                }
            } catch (NumberFormatException e) {

            }
        }

        if (minSizeTextField.getText() == null || minSizeTextField.getText().trim().isEmpty()) {

        } else {
            try {
                if (Integer.parseInt(minSizeTextField.getText()) < 0){
                    errors.add("Min Size value must be 0 or greater");
                }
            } catch (NumberFormatException e) {

            }
        }

        if (maxSizeTextField.getText() == null || maxSizeTextField.getText().trim().isEmpty()) {

        } else {
            try {
                if (Integer.parseInt(maxSizeTextField.getText()) < 0) {
                    errors.add("Max Size value must be 0 or greater");
                }
            } catch (NumberFormatException e) {

            }
        }

        if (numberCyclesTextField.getText() == null || numberCyclesTextField.getText().trim().isEmpty()) {

        } else {
            try {
                if (Integer.parseInt(numberCyclesTextField.getText()) < -1) {
                    errors.add("Number of Cycles value must be -1 or greater");
                } else if (populateOnlyComboBox.getSelectedItem() == "enable") {
                    errors.add("Number of Cycles is incompatible with Populate Only");
                }
            } catch (NumberFormatException e) {

            }
        }

        if (startAtTextField.getText() == null || startAtTextField.getText().trim().isEmpty()) {

        } else {
            try {
                if (Integer.parseInt(startAtTextField.getText()) < 0) {
                    errors.add("Start At value must be 0 or greater");
                }
            } catch (NumberFormatException e) {

            }
        }

        if (expiryTextField.getText() == null || expiryTextField.getText().trim().isEmpty()) {

        } else {
            try {
                if (Integer.parseInt(expiryTextField.getText()) < 0) {
                    errors.add("Expiry value must be 0 or greater");
                }
            } catch (NumberFormatException e) {

            }
        }

        if (replicateToTextField.getText() == null || replicateToTextField.getText().trim().isEmpty()) {

        } else {
            try {
                if (Integer.parseInt(replicateToTextField.getText()) < -1) {
                    errors.add("Replicate To value must be -1 or greater");
                }
            } catch (NumberFormatException e) {

            }
        }

        if (lockTextField.getText() == null || lockTextField.getText().trim().isEmpty()) {

        } else {
            try {
                if (Integer.parseInt(lockTextField.getText()) < 0) {
                    errors.add("Lock value must be 0 or greater");
                }
            } catch (NumberFormatException e) {

            }
        }

        if (subdocComboBox.getItem() == "enable" && jsonComboBox.getItem() == "disable") {
            errors.add("Subdoc must be used with JSON");
        }

        if (pathcountTextField.getText() == null || pathcountTextField.getText().trim().isEmpty()) {

        } else {
            try {
                if (Integer.parseInt(pathcountTextField.getText()) < 0) {
                    errors.add("Pathcount must be 0 or greater");
                } else if (!pathcountTextField.getText().equals("") && jsonComboBox.getItem() == "disable") {
                    errors.add("Pathcount must be used with JSON");
                }
            } catch (NumberFormatException e) {

            }
        }

        if (errors.isEmpty()) {
            super.doOKAction();
            try {
                PillowFightCommand(String.valueOf(bucketComboBox.getSelectedItem()), String.valueOf(durabilityComboBox.getSelectedItem()), persistToTextField.getText(), batchSizeTextField.getText(), numberItemsTextField.getText(), keyPrefixTextField.getText(), numberThreadsTextField.getText(), percentageTextField.getText(), String.valueOf(noPopulationComboBox.getSelectedItem()), String.valueOf(populateOnlyComboBox.getSelectedItem()), minSizeTextField.getText(), maxSizeTextField.getText(), numberCyclesTextField.getText(), String.valueOf(sequentialComboBox.getSelectedItem()), startAtTextField.getText(), String.valueOf(timingsComboBox.getSelectedItem()), expiryTextField.getText(), replicateToTextField.getText(), lockTextField.getText(), String.valueOf(jsonComboBox.getSelectedItem()), String.valueOf(noopComboBox.getSelectedItem()), String.valueOf(subdocComboBox.getSelectedItem()), pathcountTextField.getText());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            errorMessage.setText("<html>" + errors.stream().collect(Collectors.joining("<br>")) + "</html>");
        }
    }

    public void PillowFightCommand(String selectedBucket, String selectedDurability, String selectedPersistToTextField, String batchSizeTextField, String numberItemsTextField, String keyPrefixTextField, String numberThreadsTextField, String percentageTextField, String noPopulation, String populateOnly, String minSizeTextField, String maxSizeTextField, String numberCyclesTextField, String sequential, String startAtTextField, String timings, String expiryTextField, String replicateToTextField, String lockTextField, String json, String noop, String subdoc, String pathcountTextField) throws IOException, InterruptedException {
        /*
        if (selectedPersistToTextField.isEmpty()) {
            selectedPersistToTextField = "";
        } else {
            selectedPersistToTextField = " --persist-to " + selectedPersistToTextField;
        }
        if (batchSizeTextField.isEmpty()) {
            batchSizeTextField = "";
        } else {
            batchSizeTextField = " --batch-size " + batchSizeTextField;
        }
        if (numberItemsTextField.isEmpty()) {
            numberItemsTextField = "";
        } else {
            numberItemsTextField = " --num-items " + numberItemsTextField;
        }
        if (keyPrefixTextField.isEmpty()) {
            keyPrefixTextField = "";
        } else {
            keyPrefixTextField = " --key-prefix " + keyPrefixTextField;
        }
        if (numberThreadsTextField.isEmpty()) {
            numberThreadsTextField = "";
        } else {
            numberThreadsTextField = " --num-threads " + numberThreadsTextField;
        }
        if (percentageTextField.isEmpty()) {
            percentageTextField = "";
        } else {
            percentageTextField = " --set-pct " + percentageTextField;
        }
        if (noPopulation.equals("enable")) {
            noPopulation = " --no-population";
        } else {
            noPopulation = "";
        }
        if (populateOnly.equals("enable")) {
            populateOnly = " --populate-only";
        } else {
            populateOnly = "";
        }
        if (minSizeTextField.isEmpty()) {
            minSizeTextField = "";
        } else {
            minSizeTextField = " --min-size " + minSizeTextField;
        }
        if (maxSizeTextField.isEmpty()) {
            maxSizeTextField = "";
        } else {
            maxSizeTextField = " --max-size " + maxSizeTextField;
        }
        if (pauseAtEnd.equals("enable")) {
            pauseAtEnd = "--pause-at-end";
        } else {
            pauseAtEnd = "";
        }
        if (sequential.equals("enable")) {
            sequential = " --sequential";
        } else {
            sequential = "";
        }
        if (startAtTextField.isEmpty()) {
            startAtTextField = "";
        } else {
            startAtTextField = " --start-at " + startAtTextField;
        }
        if (timings.equals("enable")) {
            timings = " --timings";
        } else {
            timings = "";
        }
        if (expiryTextField.isEmpty()) {
            expiryTextField = "";
        } else {
            expiryTextField = " --expiry " + expiryTextField;
        }
        if (replicateToTextField.isEmpty()) {
            replicateToTextField = "";
        } else {
            replicateToTextField = " --replicate-to " + replicateToTextField;
        }
        if (lockTextField.isEmpty()) {
            lockTextField = "";
        } else {
            lockTextField = " --lock " + lockTextField;
        }
        if (json.equals("enable")) {
            json = " --json";
        } else {
            json = "";
        }
        if (noop.equals("enable")) {
            noop = " --noop";
        } else {
            noop = "";
        }
        if (subdoc.equals("enable")) {
            subdoc = " --subdoc";
        } else {
            subdoc = "";
        }
        if (pathcountTextField == null || pathcountTextField.trim().isEmpty()) {
            pathcountTextField = "";
        } else {
            pathcountTextField = " --pathcount " + pathcountTextField;
        }
        if (numberCyclesTextField == null || numberCyclesTextField.trim().isEmpty()) {
            numberCyclesTextField = "";
        } else {
            numberCyclesTextField = " --num-cycles " + numberCyclesTextField;
        }

        String path = CBTools.getTool(CBTools.Type.CBC_PILLOW_FIGHT).getPath();
        System.out.println(path);
        String command = String.format(" -U %s/%s -u %s -P %s --durability %s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s", ActiveCluster.getInstance().getClusterURL(), selectedBucket, ActiveCluster.getInstance().getUsername(), ActiveCluster.getInstance().getPassword(), selectedDurability, selectedPersistToTextField, batchSizeTextField, numberItemsTextField, keyPrefixTextField, numberThreadsTextField, percentageTextField, noPopulation, populateOnly, minSizeTextField, maxSizeTextField, numberCyclesTextField, sequential, startAtTextField, timings, expiryTextField, replicateToTextField, lockTextField, json, noop, subdoc, pathcountTextField);
        System.out.println(command);
        ProcessBuilder processBuilder = new ProcessBuilder(path, command);
        Process proc = processBuilder.start();
        */
        List<String> command = new ArrayList<>();
        command.add(CBTools.getTool(CBTools.Type.CBC_PILLOW_FIGHT).getPath());
        command.add("-U");
        command.add(String.format("%s/%s", ActiveCluster.getInstance().getClusterURL(), selectedBucket));
        command.add("-u");
        command.add(ActiveCluster.getInstance().getUsername());
        command.add("-P");
        command.add(ActiveCluster.getInstance().getPassword());
        command.add("--durability");
        command.add(selectedDurability);

        if (!selectedPersistToTextField.trim().isEmpty()) {
            command.add("--persist-to");
            command.add(selectedPersistToTextField);
        }
        if (!batchSizeTextField.trim().isEmpty()) {
            command.add("--batch-size");
            command.add(batchSizeTextField);
        }
        if (!numberItemsTextField.trim().isEmpty()) {
            command.add("--num-items");
            command.add(numberItemsTextField);
        }
        if (!keyPrefixTextField.trim().isEmpty()) {
            command.add("--key-prefix");
            command.add(keyPrefixTextField);
        }
        if (!numberThreadsTextField.trim().isEmpty()) {
            command.add("--num-threads");
            command.add(numberThreadsTextField);
        }
        if (!percentageTextField.trim().isEmpty()) {
            command.add("--set-pct");
            command.add(percentageTextField);
        }
        if (noPopulation.equals("enable")) {
            command.add("--no-population");
        }
        if (populateOnly.equals("enable")) {
            command.add(" --populate-only");
        }
        if (!minSizeTextField.trim().isEmpty()) {
            command.add("--min-size");
            command.add(minSizeTextField);
        }
        if (!maxSizeTextField.trim().isEmpty()) {
            command.add("--max-size");
            command.add(maxSizeTextField);
        }
        if (sequential.equals("enable")) {
            command.add("--sequential");
        }
        if (!startAtTextField.trim().isEmpty()) {
            command.add("--start-at");
            command.add(startAtTextField);
        }
        if (timings.equals("enable")) {
            command.add("--timings");
        }
        if (!expiryTextField.trim().isEmpty()) {
            command.add("--expiry");
            command.add(expiryTextField);
        }
        if (!replicateToTextField.trim().isEmpty()) {
            command.add("--replicate-to");
            command.add(replicateToTextField);
        }
        if (!lockTextField.trim().isEmpty()) {
            command.add("--lock");
            command.add(lockTextField);
        }
        if (json.equals("enable")) {
            command.add("--json");
        }
        if (noop.equals("enable")) {
            command.add(" --noop");
        }
        if (subdoc.equals("enable")) {
            command.add("--subdoc");
        }
        if (!pathcountTextField.trim().isEmpty()) {
            command.add("--pathcount");
            command.add(pathcountTextField);
        }
        if (!numberCyclesTextField.trim().isEmpty()) {
            command.add("--num-cycles");
            command.add(numberCyclesTextField);
        }

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process proc = processBuilder.start();

        //System.out.println("Executed command: " + String.join(" ", processBuilder.command()));

        StopButtonDialog stopButtonDialog = new StopButtonDialog(proc);
        stopButtonDialog.setVisible(true);
    }

    public class StopButtonDialog extends JDialog {
        private JButton stopButton;
        private JTextArea opsPerSecLabel;

        public StopButtonDialog(Process proc) throws IOException {
            super();
            setModal(true);
            setTitle("Stop Pillow Fight");
            opsPerSecLabel = new JTextArea("OPS/SEC:           ");
            opsPerSecLabel.setEditable(false);
            opsPerSecLabel.setBorder(JBUI.Borders.empty(0, 100));
            stopButton = new JButton("Stop");

            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(opsPerSecLabel, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.insets = JBUI.insets(5);
            panel.add(stopButton, gbc);

            add(panel);
            pack();

            setLocationRelativeTo(null);

            stopButton.addActionListener(e -> {
                if (proc != null) {
                    /*
                    BufferedWriter stdIn = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));
                    String controlC = "\u0003";
                    try {
                        stdIn.write(controlC);
                        stdIn.flush();
                        stdIn.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    */
                    /*
                    String controlC = "\u0003";
                    try {
                        OutputStream outputStream = proc.getOutputStream();
                        outputStream.write(controlC.getBytes());
                        outputStream.flush();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                    */
                    proc.destroy();
                }
                dispose();
            });

            new Thread(() -> {
                try {
                    BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
                    String line;
                    while ((line = stdError.readLine()) != null) {
                        //System.out.println(line);
                        opsPerSecLabel.setText(line);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }).start();
        }
    }
}
