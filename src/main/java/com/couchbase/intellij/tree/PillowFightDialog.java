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

    public void PillowFightCommand(String selectedBucket, String selectedDurability, String selectedPersistToTextField) throws IOException, InterruptedException {
        Runtime rt = Runtime.getRuntime();
        String command = String.format("cbc-pillowfight -U %s/%s -u %s -P %s --durability %s --persist-to %s", ActiveCluster.getInstance().getClusterURL(), selectedBucket, ActiveCluster.getInstance().getUsername(), ActiveCluster.getInstance().getPassword(), selectedDurability, selectedPersistToTextField);
        //System.out.println(command);
        Process proc = rt.exec(command);

        //BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        //BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

        //System.out.println(proc);

        StopButtonDialog stopButtonDialog = new StopButtonDialog(proc);
        stopButtonDialog.setVisible(true);

        /*
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (proc != null) {
                    proc.destroy(); // Stop the process if it is running
                }
            }
        });
         */

        /*
        Thread outputThread = new Thread(() -> {
            try {
                String line;
                while ((line = stdInput.readLine()) != null) {
                    if (line.contains("OPS/SEC:")) {
                        String opsSecValue = line.split("OPS/SEC:")[1].trim();
                        System.out.println("OPS/SEC: " + opsSecValue);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        */
    }

    public class StopButtonDialog extends JDialog {
        //TODO: Fix frame sizing of stop button to accomodate full title
        //TODO: Get OPS/SEC from proc PID and display above stop button
        private JButton stopButton;

        public StopButtonDialog(Process proc) {
            super();
            setTitle("Stop Pillow Fight");
            stopButton = new JButton("Stop");

            JPanel panel = new JPanel();
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
        }
    }
}
