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
        noPopulationComboBox.addItem("enable");
        noPopulationComboBox.addItem("disable");

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
            if (Integer.parseInt(input) < -1) {
                persistToTextField.setForeground(JBColor.RED);
                setOKActionEnabled(false);
            } else {
                persistToTextField.setForeground(originalColor);
                setOKActionEnabled(true);
            }
        } catch (IllegalArgumentException e) {
            persistToTextField.setForeground(JBColor.RED);
            setOKActionEnabled(false);
        }
    }

    private void validateBatchSizeTextField(Color originalColor) {
        String input = batchSizeTextField.getText();
        try {
            if (Integer.parseInt(input) < 0) {
                batchSizeTextField.setForeground(JBColor.RED);
                setOKActionEnabled(false);
            } else {
                batchSizeTextField.setForeground(originalColor);
                setOKActionEnabled(true);
            }
        } catch (IllegalArgumentException e) {
            batchSizeTextField.setForeground(JBColor.RED);
            setOKActionEnabled(false);
        }
    }

    private void validateNumberItemsTextField(Color originalColor) {
        String input = numberItemsTextField.getText();
        try {
            if (Integer.parseInt(input) < 0) {
                numberItemsTextField.setForeground(JBColor.RED);
                setOKActionEnabled(false);
            } else {
                numberItemsTextField.setForeground(originalColor);
                setOKActionEnabled(true);
            }
        } catch (IllegalArgumentException e) {
            numberItemsTextField.setForeground(JBColor.RED);
            setOKActionEnabled(false);
        }
    }

    private void validateNumberThreadsTextField(Color originalColor) {
        String input = numberThreadsTextField.getText();
        try {
            if (Integer.parseInt(input) < 0) {
                numberThreadsTextField.setForeground(JBColor.RED);
                setOKActionEnabled(false);
            } else {
                numberThreadsTextField.setForeground(originalColor);
                setOKActionEnabled(true);
            }
        } catch (IllegalArgumentException e) {
            numberThreadsTextField.setForeground(JBColor.RED);
            setOKActionEnabled(false);
        }
    }

    private void validatePercentageTextField(Color originalColor) {
        String input = percentageTextField.getText();
        try {
            if (Integer.parseInt(input) < 0) {
                percentageTextField.setForeground(JBColor.RED);
                setOKActionEnabled(false);
            } else {
                percentageTextField.setForeground(originalColor);
                setOKActionEnabled(true);
            }
        } catch (IllegalArgumentException e) {
            percentageTextField.setForeground(JBColor.RED);
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

        if (persistToTextField.getText().equals("")) {
            errors.add("Persist-to value is empty");
        } else if (Integer.parseInt(persistToTextField.getText()) < -1){
            errors.add("Persist-to value must be -1 or greater");
        }

        if (batchSizeTextField.getText().equals("")) {
            errors.add("Batch size value is empty");
        } else if (Integer.parseInt(batchSizeTextField.getText()) < 0){
            errors.add("Batch size value must be 0 or greater");
        }

        if (numberItemsTextField.getText().equals("")) {
            errors.add("Number of Items value is empty");
        } else if (Integer.parseInt(numberItemsTextField.getText()) < 0){
            errors.add("Number of Items value must be 0 or greater");
        }

        if (keyPrefixTextField.getText().equals("")) {
            errors.add("Key prefix value is empty");
        }

        if (numberThreadsTextField.getText().equals("")) {
            errors.add("Number of Threads value is empty");
        } else if (Integer.parseInt(numberThreadsTextField.getText()) < 0){
            errors.add("Number of Threads value must be 0 or greater");
        }

        if (percentageTextField.getText().equals("")) {
            errors.add("Percentage value is empty");
        } else if (Integer.parseInt(percentageTextField.getText()) < 0){
            errors.add("Percentage value must be 0 or greater");
        }

        if (errors.isEmpty()) {
            super.doOKAction();
            try {
                PillowFightCommand(String.valueOf(bucketComboBox.getSelectedItem()), String.valueOf(durabilityComboBox.getSelectedItem()), persistToTextField.getText());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            errorMessage.setText("<html>" + errors.stream().collect(Collectors.joining("<br>")) + "</html>");
        }
    }

    /*TODO: Add more options to command
     *      batch size
     *      number items
     *      key prefix
     *      number threads
     *      percentage
     */
    public void PillowFightCommand(String selectedBucket, String selectedDurability, String selectedPersistToTextField) throws IOException, InterruptedException {
        Runtime rt = Runtime.getRuntime();
        String command = String.format("cbc-pillowfight -U %s/%s -u %s -P %s --durability %s --persist-to %s", ActiveCluster.getInstance().getClusterURL(), selectedBucket, ActiveCluster.getInstance().getUsername(), ActiveCluster.getInstance().getPassword(), selectedDurability, selectedPersistToTextField);
        //System.out.println(command);
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
