package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tools.CBTools;
import com.couchbase.intellij.workbench.Log;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PillowFightRunner {
    public void runPillowFightCommand(String selectedBucket, String selectedDurability, String selectedPersistToTextField, String batchSizeTextField, String numberItemsTextField, String keyPrefixTextField, String numberThreadsTextField, String percentageTextField, String noPopulation, String populateOnly, String minSizeTextField, String maxSizeTextField, String numberCyclesTextField, String sequential, String startAtTextField, String timings, String expiryTextField, String replicateToTextField, String lockTextField, String json, String noop, String subdoc, String pathcountTextField) throws IOException, InterruptedException {
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
                    proc.destroy();
                }
                dispose();
            });

            new Thread(() -> {
                try {
                    BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
                    String line;
                    while ((line = stdError.readLine()) != null) {
                        opsPerSecLabel.setText(line);
                    }
                } catch (Exception e) {
                    Log.error(e);
                }
            }).start();
        }
    }
}
