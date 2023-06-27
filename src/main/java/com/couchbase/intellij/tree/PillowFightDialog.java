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
import com.couchbase.intellij.tools.github.CloneDemoRepo;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.ui.JBColor;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
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
        JBLabel label = new JBLabel(hintText);
        JBPanel panel = new JBPanel(new BorderLayout());
        panel.add(label, BorderLayout.CENTER);

        balloonHint = factory.createBalloonBuilder(panel).createBalloon();

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

        //panel.add(new JLabel("Available Buckets: "), gbc);
        panel.add(createLabelWithBalloon("Available Buckets: ", "A bucket is the fundamental space for storing data in Couchbase Server"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(bucketComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        //panel.add(new JLabel("Durability: "), gbc);
        panel.add(createLabelWithBalloon("Durability: ", "Specify durability level for mutation operations"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(durabilityComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        //panel.add(new JLabel("Persist-to: "), gbc);
        panel.add(createLabelWithBalloon("Persist-to: ", "Wait until the item has been persisted to at least NUMNODES nodes' disk. If NUMNODES is 1 then wait until only the master node has persisted the item for this key. You may not specify a number greater than the number of nodes actually in the cluster. -1 is special value, which mean to use all available nodes."), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(persistToTextField, gbc);
        persistToTextField.getEmptyText().setText("Enter a number -1 or greater");

        gbc.gridx = 0;
        gbc.gridy = 3;
        //panel.add(new JLabel("Batch Size: "), gbc);
        panel.add(createLabelWithBalloon("Batch Size: ", "This controls how many commands are scheduled per cycles. To simulate one operation at a time, set this value to 1."), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(batchSizeTextField, gbc);
        batchSizeTextField.getEmptyText().setText("Enter a number 0 or greater");

        gbc.gridx = 0;
        gbc.gridy = 4;
        //panel.add(new JLabel("Number of Items: "), gbc);
        panel.add(createLabelWithBalloon("Number of Items: ", "Set the total number of items the workload will access within the cluster. This will also determine the working set size at the server and may affect disk latencies if set to a high number."), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(numberItemsTextField, gbc);
        numberItemsTextField.getEmptyText().setText("Enter a number 0 or greater");

        gbc.gridx = 0;
        gbc.gridy = 5;
        //panel.add(new JLabel("Key prefix: "), gbc);
        panel.add(createLabelWithBalloon("Key Prefix: ", "Set the prefix to prepend to all keys in the cluster. Useful if you do not wish the items to conflict with existing data."), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        panel.add(keyPrefixTextField, gbc);
        keyPrefixTextField.getEmptyText().setText("Enter a prefix to prepend");

        gbc.gridx = 0;
        gbc.gridy = 6;
        //panel.add(new JLabel("Number of Threads: "), gbc);
        panel.add(createLabelWithBalloon("Number of Threads: ", "Set the number of threads (and thus the number of client instances) to run concurrently. Each thread is assigned its own client object."), gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        panel.add(numberThreadsTextField, gbc);
        numberThreadsTextField.getEmptyText().setText("Enter a number 0 or greater");

        gbc.gridx = 0;
        gbc.gridy = 7;
        //panel.add(new JLabel("Percentage: "), gbc);
        panel.add(createLabelWithBalloon("Percentage", "The percentage of operations which should be mutations. A value of 100 means only mutations while a value of 0 means only retrievals."), gbc);

        gbc.gridx = 1;
        gbc.gridy = 7;
        panel.add(percentageTextField, gbc);
        percentageTextField.getEmptyText().setText("Enter a number 0 or greater");

        gbc.gridx = 0;
        gbc.gridy = 8;
        //panel.add(new JLabel("No Population: "), gbc);
        panel.add(createLabelWithBalloon("No Population: ", "By default cbc-pillowfight will load all the items (see --num-items) into the cluster and then begin performing the normal workload. Specifying this option bypasses this stage. Useful if the items have already been loaded in a previous run."), gbc);

        gbc.gridx = 1;
        gbc.gridy = 8;
        panel.add(noPopulationComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 9;
        //panel.add(new JLabel("Populate only: "), gbc);
        panel.add(createLabelWithBalloon("Populate Only: ", "Stop after population. Useful to populate buckets with large amounts of data."), gbc);

        gbc.gridx = 1;
        gbc.gridy = 9;
        panel.add(populateOnlyComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 10;
        //panel.add(new JLabel("Min Size: "), gbc);
        panel.add(createLabelWithBalloon("Min Size: ", "Specify the minimum size to be stored into the cluster. This is typically a range, in which case each value generated will be between Min Size and Max Size bytes."), gbc);

        gbc.gridx = 1;
        gbc.gridy = 10;
        panel.add(minSizeTextField, gbc);
        minSizeTextField.getEmptyText().setText("Enter a number 0 or greater");

        gbc.gridx = 0;
        gbc.gridy = 11;
        panel.add(new JLabel("Max Size: "), gbc);
        panel.add(createLabelWithBalloon("Max Size: ", "Specify the maximum size to be stored into the cluster. This is typically a range, in which case each value generated will be between Min Size and Max Size bytes."), gbc);

        gbc.gridx = 1;
        gbc.gridy = 11;
        panel.add(maxSizeTextField, gbc);
        maxSizeTextField.getEmptyText().setText("Enter a number 0 or greater");

        /*
        gbc.gridx = 0;
        gbc.gridy = 12;
        panel.add(new JLabel("Pause At End: "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 12;
        panel.add(pauseAtEndComboBox, gbc);
        */

        gbc.gridx = 0;
        gbc.gridy = 12;
        //panel.add(new JLabel("Number of Cycles: "), gbc);
        panel.add(createLabelWithBalloon("Number of Cycles: ", "Specify the number of times the workload should cycle. During each cycle an amount of --batch-size operations are executed. Setting this to -1 will cause the workload to run infinitely."), gbc);

        gbc.gridx = 1;
        gbc.gridy = 12;
        panel.add(numberCyclesTextField, gbc);
        numberCyclesTextField.getEmptyText().setText("Enter a number -1 or greater");

        gbc.gridx = 0;
        gbc.gridy = 13;
        //panel.add(new JLabel("Sequential: "), gbc);
        panel.add(createLabelWithBalloon("Sequential: ", "Specify that the access pattern should be done in a sequential manner. This is useful for bulk-loading many documents in a single server."), gbc);

        gbc.gridx = 1;
        gbc.gridy = 13;
        panel.add(sequentialComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 14;
        //panel.add(new JLabel("Start At: "), gbc);
        panel.add(createLabelWithBalloon("Start At: ", "This specifies the starting offset for the items. The items by default are generated with the key prefix (--key-prefix) up to the number of items (--num-items). The --start-at value will increase the lower limit of the items. This is useful to resume a previously cancelled load operation."), gbc);

        gbc.gridx = 1;
        gbc.gridy = 14;
        panel.add(startAtTextField, gbc);
        startAtTextField.getEmptyText().setText("Enter a number 0 or greater");

        gbc.gridx = 0;
        gbc.gridy = 15;
        //panel.add(new JLabel("Timings: "), gbc);
        panel.add(createLabelWithBalloon("Timings: ", "Enabled timing recorded. Timing histogram will be dumped to STDERR on SIGQUIT (CTRL-/). When specified second time, it will dump a histogram of command timings and latencies to the screen every second."), gbc);

        gbc.gridx = 1;
        gbc.gridy = 15;
        panel.add(timingsComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 16;
        //panel.add(new JLabel("Expiry: "), gbc);
        panel.add(createLabelWithBalloon("Expiry: ", "Set the expiration time on the document for SECONDS when performing each operation. Note that setting this too low may cause not-found errors to appear on the screen."), gbc);

        gbc.gridx = 1;
        gbc.gridy = 16;
        panel.add(expiryTextField, gbc);
        expiryTextField.getEmptyText().setText("Enter a number 0 or greater");

        gbc.gridx = 0;
        gbc.gridy = 17;
        //panel.add(new JLabel("Replicate To: "), gbc);
        panel.add(createLabelWithBalloon("Replicate To: ", "Wait until the item has been replicated to at least NREPLICAS replica nodes. The bucket must be configured with at least one replica, and at least NREPLICAS replica nodes must be online. -1 is special value, which mean to use all available replicas."), gbc);

        gbc.gridx = 1;
        gbc.gridy = 17;
        panel.add(replicateToTextField, gbc);
        replicateToTextField.getEmptyText().setText("Enter a number -1 or greater");

        gbc.gridx = 0;
        gbc.gridy = 18;
        //panel.add(new JLabel("Lock: "), gbc);
        panel.add(createLabelWithBalloon("Lock: ", "This will retrieve and lock an item before update, making it inaccessible for modification until the update completed, or TIME has passed."), gbc);

        gbc.gridx = 1;
        gbc.gridy = 18;
        panel.add(lockTextField, gbc);
        lockTextField.getEmptyText().setText("Enter a number 0 or greater");

        gbc.gridx = 0;
        gbc.gridy = 19;
        //panel.add(new JLabel("JSON: "), gbc);
        panel.add(createLabelWithBalloon("JSON: ", "Make pillowfight store document as JSON rather than binary. This will allow the documents to nominally be analyzed by other Couchbase services such as Query and MapReduce."), gbc);

        gbc.gridx = 1;
        gbc.gridy = 19;
        panel.add(jsonComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 20;
        //panel.add(new JLabel("NOOP: "), gbc);
        panel.add(createLabelWithBalloon("NOOP: ", "Use couchbase NOOP operations when running the workload. This mode ignores population, and all other document operations. Useful as the most lightweight workload."), gbc);

        gbc.gridx = 1;
        gbc.gridy = 20;
        panel.add(noopComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 21;
        //panel.add(new JLabel("Subdoc: "), gbc);
        panel.add(createLabelWithBalloon("Subdoc: ", "Use couchbase sub-document operations when running the workload. In this mode pillowfight will use Couchbase sub-document operations to perform gets and sets of data. This option must be used with --json"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 21;
        panel.add(subdocComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 22;
        //panel.add(new JLabel("Pathcount: "), gbc);
        panel.add(createLabelWithBalloon("Pathcount: ", "Specify the number of paths a single sub-document operation should contain. By default, each subdoc operation operates on only a single path within the document. You can specify multiple paths to atomically executed multiple subdoc operations within a single command."), gbc);

        gbc.gridx = 1;
        gbc.gridy = 22;
        panel.add(pathcountTextField, gbc);
        pathcountTextField.getEmptyText().setText("Enter a number 0 or greater");

        gbc.gridx = 0;
        gbc.gridy = 23;
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
        Runtime rt = Runtime.getRuntime();

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
        /*
        if (pauseAtEnd.equals("enable")) {
            pauseAtEnd = "--pause-at-end";
        } else {
            pauseAtEnd = "";
        }
        */
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

        String command = String.format("cbc-pillowfight -U %s/%s -u %s -P %s --durability %s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s", ActiveCluster.getInstance().getClusterURL(), selectedBucket, ActiveCluster.getInstance().getUsername(), ActiveCluster.getInstance().getPassword(), selectedDurability, selectedPersistToTextField, batchSizeTextField, numberItemsTextField, keyPrefixTextField, numberThreadsTextField, percentageTextField, noPopulation, populateOnly, minSizeTextField, maxSizeTextField, numberCyclesTextField, sequential, startAtTextField, timings, expiryTextField, replicateToTextField, lockTextField, json, noop, subdoc, pathcountTextField);
        //System.out.println(command);
        Process proc = rt.exec(command);
        //System.out.println(proc);

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
