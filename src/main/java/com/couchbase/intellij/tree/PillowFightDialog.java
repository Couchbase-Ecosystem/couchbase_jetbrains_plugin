package com.couchbase.intellij.tree;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

import com.couchbase.intellij.tools.dialog.CollapsiblePanel;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.couchbase.intellij.database.ActiveCluster;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.ui.JBUI;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
    protected Action @NotNull [] createActions() {
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
            Log.error(e);
        }

        String[] disableEnableOptions = {"Disable", "Enable"};
        String[] durabilityOptions = {"none", "majority", "majority_and_persist_to_active", "persist_to_majority"};

        durabilityComboBox = new ComboBox<>(durabilityOptions);
        persistToTextField = createTextFieldWithValidation(-1);
        batchSizeTextField = createTextFieldWithValidation(0);
        numberItemsTextField = createTextFieldWithValidation(0);
        keyPrefixTextField = new JBTextField();
        numberThreadsTextField = createTextFieldWithValidation(0);
        percentageTextField = createTextFieldWithValidation(0);
        noPopulationComboBox = new ComboBox<>(disableEnableOptions);
        populateOnlyComboBox = new ComboBox<>(disableEnableOptions);
        minSizeTextField = createTextFieldWithValidation(0);
        maxSizeTextField = createTextFieldWithValidation(0);
        numberCyclesTextField = createTextFieldWithValidation(-1);
        sequentialComboBox = new ComboBox<>(disableEnableOptions);
        startAtTextField = createTextFieldWithValidation(0);
        timingsComboBox = new ComboBox<>(disableEnableOptions);
        expiryTextField = createTextFieldWithValidation(0);
        replicateToTextField = createTextFieldWithValidation(-1);
        lockTextField = createTextFieldWithValidation(0);
        jsonComboBox = new ComboBox<>(disableEnableOptions);
        noopComboBox = new ComboBox<>(disableEnableOptions);
        subdocComboBox = new ComboBox<>(disableEnableOptions);
        pathcountTextField = createTextFieldWithValidation(0);

        init();
    }

    private JBTextField createTextFieldWithValidation(int minValue) {
        JBTextField textField = new JBTextField();
        textField.getDocument().addDocumentListener(new DocumentListener() {
            final Color originalColor = textField.getForeground();

            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                validateTextField(textField, originalColor, minValue);
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                validateTextField(textField, originalColor, minValue);
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                validateTextField(textField, originalColor, minValue);
            }
        });
        return textField;
    }

    private void validateTextField(JBTextField textField, Color originalColor, int minValue) {
        String input = textField.getText();
        try {
            if (input == null || input.trim().isEmpty()) {
                textField.setForeground(originalColor);
                setOKActionEnabled(true);
                return;
            }
            if (Integer.parseInt(input) < minValue) {
                textField.setForeground(JBColor.RED);
                setOKActionEnabled(false);
            } else {
                textField.setForeground(originalColor);
                setOKActionEnabled(true);
            }
        } catch (NumberFormatException e) {
            textField.setForeground(JBColor.RED);
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
        gbc.weighty = 0.0;

        panel.add(createLabelWithBalloon("Available Buckets:                            ", "A bucket is the fundamental space for storing data in Couchbase Server"), gbc);

        gbc.gridx++;
        panel.add(bucketComboBox, gbc);

        gbc.gridx--;
        gbc.gridy++;
        panel.add(createLabelWithBalloon("Durability:                            ", "Specify durability level for mutation operations"), gbc);

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

        collapsiblePanel.setVisible(false);

        collapsiblePanel.add(createLabelWithBalloon("Persist-to: ", "Wait until the item has been persisted to at least NUMNODES nodes' disk. If NUMNODES is 1 then wait until only the master node has persisted the item for this key. You may not specify a number greater than the number of nodes actually in the cluster. -1 is special value, which mean to use all available nodes."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(persistToTextField, gbcCollapsable);
        persistToTextField.getEmptyText().setText("Enter a number -1 or greater");

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        collapsiblePanel.add(createLabelWithBalloon("Batch Size: ", "This controls how many commands are scheduled per cycles. To simulate one operation at a time, set this value to 1."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(batchSizeTextField, gbcCollapsable);
        batchSizeTextField.getEmptyText().setText("Enter a number 0 or greater");

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        collapsiblePanel.add(createLabelWithBalloon("Number of Items: ", "Set the total number of items the workload will access within the cluster. This will also determine the working set size at the server and may affect disk latencies if set to a high number."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(numberItemsTextField, gbcCollapsable);
        numberItemsTextField.getEmptyText().setText("Enter a number 0 or greater");

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        collapsiblePanel.add(createLabelWithBalloon("Key Prefix: ", "Set the prefix to prepend to all keys in the cluster. Useful if you do not wish the items to conflict with existing data."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(keyPrefixTextField, gbcCollapsable);
        keyPrefixTextField.getEmptyText().setText("Enter a prefix to prepend");

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        collapsiblePanel.add(createLabelWithBalloon("Number of Threads: ", "Set the number of threads (and thus the number of client instances) to run concurrently. Each thread is assigned its own client object."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(numberThreadsTextField, gbcCollapsable);
        numberThreadsTextField.getEmptyText().setText("Enter a number 0 or greater");

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        collapsiblePanel.add(createLabelWithBalloon("Percentage", "The percentage of operations which should be mutations. A value of 100 means only mutations while a value of 0 means only retrievals."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(percentageTextField, gbcCollapsable);
        percentageTextField.getEmptyText().setText("Enter a number 0 or greater");

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        collapsiblePanel.add(createLabelWithBalloon("No Population: ", "By default cbc-pillowfight will load all the items (see --num-items) into the cluster and then begin performing the normal workload. Specifying this option bypasses this stage. Useful if the items have already been loaded in a previous run."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(noPopulationComboBox, gbcCollapsable);

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        collapsiblePanel.add(createLabelWithBalloon("Populate Only: ", "Stop after population. Useful to populate buckets with large amounts of data."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(populateOnlyComboBox, gbcCollapsable);

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
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

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        collapsiblePanel.add(createLabelWithBalloon("Number of Cycles: ", "Specify the number of times the workload should cycle. During each cycle an amount of --batch-size operations are executed. Setting this to -1 will cause the workload to run infinitely."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(numberCyclesTextField, gbcCollapsable);
        numberCyclesTextField.getEmptyText().setText("Enter a number -1 or greater");

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        collapsiblePanel.add(createLabelWithBalloon("Sequential: ", "Specify that the access pattern should be done in a sequential manner. This is useful for bulk-loading many documents in a single server."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(sequentialComboBox, gbcCollapsable);

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        collapsiblePanel.add(createLabelWithBalloon("Start At: ", "This specifies the starting offset for the items. The items by default are generated with the key prefix (--key-prefix) up to the number of items (--num-items). The --start-at value will increase the lower limit of the items. This is useful to resume a previously cancelled load operation."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(startAtTextField, gbcCollapsable);
        startAtTextField.getEmptyText().setText("Enter a number 0 or greater");

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        collapsiblePanel.add(createLabelWithBalloon("Timings: ", "Enabled timing recorded. Timing histogram will be dumped to STDERR on SIGQUIT (CTRL-/). When specified second time, it will dump a histogram of command timings and latencies to the screen every second."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(timingsComboBox, gbcCollapsable);

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        collapsiblePanel.add(createLabelWithBalloon("Expiry: ", "Set the expiration time on the document for SECONDS when performing each operation. Note that setting this too low may cause not-found errors to appear on the screen."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(expiryTextField, gbcCollapsable);
        expiryTextField.getEmptyText().setText("Enter a number 0 or greater");

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        collapsiblePanel.add(createLabelWithBalloon("Replicate To: ", "Wait until the item has been replicated to at least NREPLICAS replica nodes. The bucket must be configured with at least one replica, and at least NREPLICAS replica nodes must be online. -1 is special value, which mean to use all available replicas."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(replicateToTextField, gbcCollapsable);
        replicateToTextField.getEmptyText().setText("Enter a number -1 or greater");

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        collapsiblePanel.add(createLabelWithBalloon("Lock: ", "This will retrieve and lock an item before update, making it inaccessible for modification until the update completed, or TIME has passed."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(lockTextField, gbcCollapsable);
        lockTextField.getEmptyText().setText("Enter a number 0 or greater");

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        collapsiblePanel.add(createLabelWithBalloon("JSON: ", "Make pillowfight store document as JSON rather than binary. This will allow the documents to nominally be analyzed by other Couchbase services such as Query and MapReduce."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(jsonComboBox, gbcCollapsable);

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        collapsiblePanel.add(createLabelWithBalloon("NOOP: ", "Use couchbase NOOP operations when running the workload. This mode ignores population, and all other document operations. Useful as the most lightweight workload."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(noopComboBox, gbcCollapsable);

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        collapsiblePanel.add(createLabelWithBalloon("Subdoc: ", "Use couchbase sub-document operations when running the workload. In this mode pillowfight will use Couchbase sub-document operations to perform gets and sets of data. This option must be used with --json"), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(subdocComboBox, gbcCollapsable);

        gbcCollapsable.gridx--;
        gbcCollapsable.gridy++;
        collapsiblePanel.add(createLabelWithBalloon("Pathcount: ", "Specify the number of paths a single sub-document operation should contain. By default, each subdoc operation operates on only a single path within the document. You can specify multiple paths to atomically executed multiple subdoc operations within a single command."), gbcCollapsable);

        gbcCollapsable.gridx++;
        collapsiblePanel.add(pathcountTextField, gbcCollapsable);
        pathcountTextField.getEmptyText().setText("Enter a number 0 or greater");

        gbc.gridy++;
        CollapsiblePanel collapsiblePanel1 = new CollapsiblePanel("Advanced Options", collapsiblePanel);
        panel.add(collapsiblePanel1, gbc);

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

        if (!numberCyclesTextField.getText().equals("") && populateOnlyComboBox.getSelectedItem() == "Enable") {
            errors.add("Number of Cycles is incompatible with Populate Only");
        }

        if (subdocComboBox.getSelectedItem() == "Enable" && jsonComboBox.getSelectedItem() == "Disable") {
            errors.add("Subdoc must be used with JSON");
        }

        if (!pathcountTextField.getText().equals("") && jsonComboBox.getSelectedItem() == "Disable") {
            errors.add("Pathcount must be used with JSON");
        }

        try {
            if (!lockTextField.getText().equals("") && Integer.parseInt(numberItemsTextField.getText()) < Integer.parseInt(batchSizeTextField.getText()) * Integer.parseInt(numberThreadsTextField.getText())) {
                errors.add("Number of Items cannot be smaller than Batch Size multiplied to Number of Threads when used with Lock");
            }
        } catch (Exception e) {
            Log.error(e);
        }

        if (errors.isEmpty()) {
            super.doOKAction();
            try {
                PillowFightRunner pillowFightRunner = new PillowFightRunner();
                pillowFightRunner.runPillowFightCommand(String.valueOf(bucketComboBox.getSelectedItem()), String.valueOf(durabilityComboBox.getSelectedItem()), persistToTextField.getText(), batchSizeTextField.getText(), numberItemsTextField.getText(), keyPrefixTextField.getText(), numberThreadsTextField.getText(), percentageTextField.getText(), String.valueOf(noPopulationComboBox.getSelectedItem()), String.valueOf(populateOnlyComboBox.getSelectedItem()), minSizeTextField.getText(), maxSizeTextField.getText(), numberCyclesTextField.getText(), String.valueOf(sequentialComboBox.getSelectedItem()), startAtTextField.getText(), String.valueOf(timingsComboBox.getSelectedItem()), expiryTextField.getText(), replicateToTextField.getText(), lockTextField.getText(), String.valueOf(jsonComboBox.getSelectedItem()), String.valueOf(noopComboBox.getSelectedItem()), String.valueOf(subdocComboBox.getSelectedItem()), pathcountTextField.getText());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            errorMessage.setText("<html>" + errors.stream().collect(Collectors.joining("<br>")) + "</html>");
        }
    }
}