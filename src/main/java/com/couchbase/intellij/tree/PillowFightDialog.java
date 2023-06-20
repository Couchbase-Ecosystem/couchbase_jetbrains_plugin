package com.couchbase.intellij.tree;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.bucket.BucketManager;
import com.couchbase.intellij.database.DataLoader;
import com.couchbase.intellij.persistence.SavedCluster;
import com.couchbase.intellij.tools.github.CloneDemoRepo;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.Nullable;

import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.intellij.database.ActiveCluster;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.ui.JBUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;

public class PillowFightDialog extends DialogWrapper {
    private ComboBox<String> bucketComboBox;
    private ComboBox<String> durabilityComboBox;
    private JTextField persistToTextField;
    private JTextField batchSizeTextField;
    private JTextField numberItemsTextField;
    private JTextField keyPrefixTextField;
    private JTextField numberThreadsTextField;
    private JTextField percentageTextField;
    private ComboBox<String> noPopulationComboBox;
    private ComboBox<String> populateOnlyComboBox;
    private JTextField minSizeTextField;
    private JTextField maxSizeTextField;
    //private ComboBox<String> pauseAtEndComboBox;
    private JTextField numberCyclesTextField;
    private ComboBox<String> sequentialComboBox;
    private JTextField startAtTextField;
    private ComboBox<String> timingsComboBox;
    private JTextField expiryTextField;
    private JTextField replicateToTextField;
    private JTextField lockTextField;
    private ComboBox<String> jsonComboBox;
    private ComboBox<String> noopComboBox;
    private ComboBox<String> subdocComboBox;
    private JTextField pathcountTextField;
    private JLabel errorMessage;
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

        persistToTextField = new JTextField();
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

        batchSizeTextField = new JTextField();
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

        numberItemsTextField = new JTextField();
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

        keyPrefixTextField = new JTextField();

        numberThreadsTextField = new JTextField();
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

        percentageTextField = new JTextField();
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

        minSizeTextField = new JTextField();
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

        maxSizeTextField = new JTextField();
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

        numberCyclesTextField = new JTextField();
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

        startAtTextField = new JTextField();
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

        expiryTextField = new JTextField();
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

        replicateToTextField = new JTextField();
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

        lockTextField = new JTextField();
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

        pathcountTextField = new JTextField();
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

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = JBUI.insets(5);
        gbc.anchor = GridBagConstraints.WEST;

        panel.add(new JLabel("Available Buckets: "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(bucketComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Durability: "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(durabilityComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Persist-to (Enter a number -1 or greater): "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(persistToTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Batch Size (Enter a number 0 or greater): "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(batchSizeTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Number of Items (Enter a number 0 or greater): "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(numberItemsTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Key prefix: "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        panel.add(keyPrefixTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Number of Threads (Enter a number 0 or greater): "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        panel.add(numberThreadsTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(new JLabel("Percentage (Enter a number 0 or greater): "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 7;
        panel.add(percentageTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        panel.add(new JLabel("No population: "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 8;
        panel.add(noPopulationComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 9;
        panel.add(new JLabel("Populate only: "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 9;
        panel.add(populateOnlyComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 10;
        panel.add(new JLabel("Min Size (Enter a number 0 or greater): "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 10;
        panel.add(minSizeTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 11;
        panel.add(new JLabel("Max Size (Enter a number 0 or greater): "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 11;
        panel.add(maxSizeTextField, gbc);

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
        panel.add(new JLabel("Number of Cycles: "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 12;
        panel.add(numberCyclesTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 13;
        panel.add(new JLabel("Sequential: "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 13;
        panel.add(sequentialComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 14;
        panel.add(new JLabel("Start At (Enter a number 0 or greater): "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 14;
        panel.add(startAtTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 15;
        panel.add(new JLabel("Timings: "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 15;
        panel.add(timingsComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 16;
        panel.add(new JLabel("Expiry: "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 16;
        panel.add(expiryTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 17;
        panel.add(new JLabel("Replicate To: "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 17;
        panel.add(replicateToTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 18;
        panel.add(new JLabel("Lock: "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 18;
        panel.add(lockTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 19;
        panel.add(new JLabel("JSON: "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 19;
        panel.add(jsonComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 20;
        panel.add(new JLabel("NOOP: "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 20;
        panel.add(noopComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 21;
        panel.add(new JLabel("Subdoc: "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 21;
        panel.add(subdocComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 22;
        panel.add(new JLabel("Pathcount: "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 22;
        panel.add(pathcountTextField, gbc);

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
        System.out.println(noPopulation);

        if (selectedPersistToTextField.isEmpty()) {
            selectedPersistToTextField = "";
        } else {
            selectedPersistToTextField = "--persist-to " + selectedPersistToTextField;
        }
        if (batchSizeTextField.isEmpty()) {
            batchSizeTextField = "";
        } else {
            batchSizeTextField = "--batch-size " + batchSizeTextField;
        }
        if (numberItemsTextField.isEmpty()) {
            numberItemsTextField = "";
        } else {
            numberItemsTextField = "--num-items " + numberItemsTextField;
        }
        if (keyPrefixTextField.isEmpty()) {
            keyPrefixTextField = "";
        } else {
            keyPrefixTextField = "--key-prefix " + keyPrefixTextField;
        }
        if (numberThreadsTextField.isEmpty()) {
            numberThreadsTextField = "";
        } else {
            numberThreadsTextField = "--num-threads " + numberThreadsTextField;
        }
        if (percentageTextField.isEmpty()) {
            percentageTextField = "";
        } else {
            percentageTextField = "--set-pct " + percentageTextField;
        }
        if (noPopulation.equals("enable")) {
            noPopulation = "--no-population";
        } else {
            noPopulation = "";
        }
        if (populateOnly.equals("enable")) {
            populateOnly = "--populate-only";
        } else {
            populateOnly = "";
        }
        if (minSizeTextField.isEmpty()) {
            minSizeTextField = "";
        } else {
            minSizeTextField = "--min-size " + minSizeTextField;
        }
        if (maxSizeTextField.isEmpty()) {
            maxSizeTextField = "";
        } else {
            maxSizeTextField = "--max-size " + maxSizeTextField;
        }
        /*
        if (pauseAtEnd.equals("enable")) {
            pauseAtEnd = "--pause-at-end";
        } else {
            pauseAtEnd = "";
        }
        */
        if (sequential.equals("enable")) {
            sequential = "--sequential";
        } else {
            sequential = "";
        }
        if (startAtTextField.isEmpty()) {
            startAtTextField = "";
        } else {
            startAtTextField = "--start-at " + startAtTextField;
        }
        if (timings.equals("enable")) {
            timings = "--timings";
        } else {
            timings = "";
        }
        if (expiryTextField.isEmpty()) {
            expiryTextField = "";
        } else {
            expiryTextField = "--expiry " + expiryTextField;
        }
        if (replicateToTextField.isEmpty()) {
            replicateToTextField = "";
        } else {
            replicateToTextField = "--replicate-to " + replicateToTextField;
        }
        if (lockTextField.isEmpty()) {
            lockTextField = "";
        } else {
            lockTextField = "--lock " + lockTextField;
        }
        if (json.equals("enable")) {
            json = "--json";
        } else {
            json = "";
        }
        if (noop.equals("enable")) {
            noop = "--noop";
        } else {
            noop = "";
        }
        if (subdoc.equals("enable")) {
            subdoc = "--subdoc";
        } else {
            subdoc = "";
        }
        if (pathcountTextField == null || pathcountTextField.trim().isEmpty()) {
            pathcountTextField = "";
        } else {
            pathcountTextField = "--pathcount " + pathcountTextField;
        }
        if (numberCyclesTextField == null || numberCyclesTextField.trim().isEmpty()) {
            numberCyclesTextField = "";
        } else {
            numberCyclesTextField = "--num-cycles " + numberCyclesTextField;
        }

        String command = String.format("cbc-pillowfight -U %s/%s -u %s -P %s --durability %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s", ActiveCluster.getInstance().getClusterURL(), selectedBucket, ActiveCluster.getInstance().getUsername(), ActiveCluster.getInstance().getPassword(), selectedDurability, selectedPersistToTextField, batchSizeTextField, numberItemsTextField, keyPrefixTextField, numberThreadsTextField, percentageTextField, noPopulation, populateOnly, minSizeTextField, maxSizeTextField, numberCyclesTextField, sequential, startAtTextField, timings, expiryTextField, replicateToTextField, lockTextField, json, noop, subdoc, pathcountTextField);
        System.out.println(command);
        Process proc = rt.exec(command);
        //System.out.println(proc);

        StopButtonDialog stopButtonDialog = new StopButtonDialog(proc);
        stopButtonDialog.setVisible(true);
    }

    public class StopButtonDialog extends JDialog {
        //TODO: Fix frame sizing of stop button to accomodate full title\
        private JButton stopButton;
        private JTextArea opsPerSecLabel;

        public StopButtonDialog(Process proc) throws IOException {
            super();
            setModal(true);
            setTitle("Stop Pillow Fight");
            opsPerSecLabel = new JTextArea("OPS/SEC: ");
            opsPerSecLabel.setEditable(false);
            stopButton = new JButton("Stop");

            JPanel panel = new JPanel();
            panel.add(opsPerSecLabel);
            panel.add(stopButton);

            add(panel);
            pack();

            setLocationRelativeTo(null);

            stopButton.addActionListener(e -> {
                if (proc != null) {
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
