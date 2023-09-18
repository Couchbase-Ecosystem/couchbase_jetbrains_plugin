package com.couchbase.intellij.tools.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import com.intellij.openapi.ui.ComboBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tools.Mctimings;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;

import utils.TemplateUtil;

public class MctimingsDialog extends DialogWrapper {

    private static final String SUMMARY_OF_ALL_OPERATIONS = "Summary of All Operations";
    private static final String HISTOGRAM = "Histogram";
    private static final String JSON = "Json";
    private static final String JSON_PRETTY_PRINTED = "Json pretty printed";
    private final JComboCheckBox optionalParametersComboCheckBox = new JComboCheckBox();
    private GridBagConstraints c;
    private JComboBox<String> bucketComboBox;
    private JComboBox<String> outputFormatComboBox;
    private JPanel outputPanel;
    private JPanel dialogPanel;
    private JBScrollPane histogramScrollPane;

    public MctimingsDialog() {
        super(true);
        init();
        setTitle("Couchbase Mctimings Dialog");
        getWindow().setMinimumSize(new Dimension(600, 400));
        setResizable(true);
        setOKButtonText("Display Timings");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        dialogPanel = new JPanel(new GridBagLayout());
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTH;

        // Line 1: Bucket
        c.gridx = 0;
        c.gridy = 0;
        c.insets = JBUI.insets(5);
        JPanel bucketLabel = TemplateUtil.getLabelWithHelp("Bucket:", "Select the bucket to display mctimings for");
        dialogPanel.add(bucketLabel, c);

        c.gridx = 1;
        c.gridy = 0;
        bucketComboBox = new ComboBox<>();
        Set<String> bucketSet = ActiveCluster.getInstance().get().buckets().getAllBuckets().keySet();
        String[] buckets = bucketSet.toArray(new String[0]);
        bucketComboBox.addItem("All buckets");
        for (String bucket : buckets) {
            bucketComboBox.addItem(bucket);
        }
        dialogPanel.add(bucketComboBox, c);

        // Line 2: Output format
        c.gridx = 0;
        c.gridy = 1;
        JPanel outputFormatLabel = TemplateUtil.getLabelWithHelp("Output format:",
                "Select the output format for mctimings");
        dialogPanel.add(outputFormatLabel, c);

        c.gridx = 1;
        c.gridy = 1;
        outputFormatComboBox = new ComboBox<>();
        outputFormatComboBox.addItem(SUMMARY_OF_ALL_OPERATIONS);
        outputFormatComboBox.addItem(HISTOGRAM);
        outputFormatComboBox.addItem(JSON);
        outputFormatComboBox.addItem(JSON_PRETTY_PRINTED);

        dialogPanel.add(outputFormatComboBox, c);

        // Line 3: Optional parameters
        c.gridx = 0;
        c.gridy = 2;
        JPanel optionalParametersLabel = TemplateUtil.getLabelWithHelp("Optional parameters:",
                "Select the optional parameters for mctimings");
        dialogPanel.add(optionalParametersLabel, c);

        c.gridx = 1;
        c.gridy = 2;

        List<String> optionalParametersList = getStrings();
        optionalParametersComboCheckBox.removeAllItems();
        optionalParametersComboCheckBox.setHint("Select some or none optional parameters");
        optionalParametersList.forEach(optionalParametersComboCheckBox::addItem);
        dialogPanel.add(optionalParametersComboCheckBox, c);

        // Line 4: Output
        c.gridx = 0;
        c.gridy = 3;
        JPanel outputLabel = TemplateUtil.getLabelWithHelp("Output:", "");
        dialogPanel.add(outputLabel, c);

        c.gridx = 0;
        c.gridy = 4;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;

        return dialogPanel;
    }

    @NotNull
    private static List<String> getStrings() {
        String[] optionalParameters = { "Get", "Set", "Add", "Replace", "Delete", "Increment", "Decrement", "Quit",
                "Flush", "GetQ", "No-op", "Version", "GetK", "GetKQ", "Append", "Prepend", "Stat", "SetQ", "AddQ",
                "ReplaceQ", "DeleteQ", "IncrementQ", "DecrementQ", "QuitQ", "FlushQ", "AppendQ", "PrependQ",
                "Verbosity", "Touch", "GAT", "GATQ", "HELO", "SASL list mechs", "SASL Auth", "SASL Step", "Ioctl get",
                "Ioctl set", "Config validate", "Config reload", "Audit put", "Audit config reload", "Shutdown", "RGet",
                "RSet", "RSetQ", "RAppend", "RAppendQ", "RPrepend", "RPrependQ", "RDelete", "RDeleteQ", "RIncr",
                "RIncrQ", "RDecr", "RDecrQ", "Set VBucket", "Get VBucket", "Del VBucket", "TAP Connect", "TAP Mutation",
                "TAP Delete", "TAP Flush", "TAP Opaque", "TAP VBucket Set", "TAP Checkout Start", "TAP Checkpoint End",
                "Get all vb seqnos", "Dcp Open", "Dcp add stream", "Dcp close stream", "Dcp stream req",
                "Dcp get failover log", "Dcp stream end", "Dcp snapshot marker", "Dcp mutation", "Dcp deletion",
                "Dcp expiration", "Dcp flush", "Dcp set vbucket state", "Dcp noop", "Dcp buffer acknowledgement",
                "Dcp control", "Dcp reserved4", "Stop persistence", "Start persistence", "Set param", "Get replica",
                "Create bucket", "Delete bucket", "List buckets", "Select bucket", "Assume role", "Observe seqno",
                "Observe", "Evict key", "Get locked", "Unlock key", "Last closed checkpoint", "Deregister tap client",
                "Reset replication chain", "Get meta", "Getq meta", "Set with meta", "Setq with meta", "Add with meta",
                "Addq with meta", "Snapshot vb states", "Vbucket batch count", "Del with meta", "Delq with meta",
                "Create checkpoint", "Notify vbucket update", "Enable traffic", "Disable traffic", "Change vb filter",
                "Checkpoint persistence", "Return meta", "Compact db", "Set cluster config", "Get cluster config",
                "Get random key", "Seqno persistence", "Get keys", "Set drift counter state", "Get adjusted time",
                "Subdoc get", "Subdoc exists", "Subdoc dict add", "Subdoc dict upsert", "Subdoc delete",
                "Subdoc replace", "Subdoc array push last", "Subdoc array push first", "Subdoc array insert",
                "Subdoc array add unique", "Subdoc counter", "Subdoc multi lookup", "Subdoc multi mutation", "Scrub",
                "Isasl refresh", "Ssl certs refresh", "Get cmd timer", "Set ctrl token", "Get ctrl token",
                "Init complete" };

        return new ArrayList<>(Arrays.asList(optionalParameters));
    }

    @Override
    protected JComponent createSouthPanel() {
        JButton displayTimingsButton;
        JButton cancelButton;
        JPanel southPanel;
        southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> doCancelAction());
        southPanel.add(cancelButton);
        displayTimingsButton = new JButton("Display Timings");
        displayTimingsButton.addActionListener(e -> doOKAction());
        southPanel.add(displayTimingsButton);

        return southPanel;
    }

    @Override
    protected void doOKAction() {
        getWindow().setMinimumSize(new Dimension(1600, 800));
        if (outputPanel != null)
            dialogPanel.remove(outputPanel);
        if (histogramScrollPane != null)
            dialogPanel.remove(histogramScrollPane);

        String selectedBucket = (String) bucketComboBox.getSelectedItem();
        String selectedOutputFormat = (String) outputFormatComboBox.getSelectedItem();
        List<String> selectedOptionalParameters = optionalParametersComboCheckBox.getSelectedItems();

        if (Objects.equals(selectedOutputFormat, HISTOGRAM)) {
            Mctimings mctimings = new Mctimings(selectedBucket, selectedOutputFormat, selectedOptionalParameters);
            outputPanel = mctimings.executeCommandAndReturnPanel();

            histogramScrollPane = new JBScrollPane(outputPanel);
            histogramScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            histogramScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

            dialogPanel.add(histogramScrollPane, c);

        } else {
            JTextArea outputTextArea = new JTextArea();

            outputTextArea.setEditable(false);
            outputTextArea.setLineWrap(true); // Enable line wrapping
            outputTextArea.setWrapStyleWord(true); // Wrap lines at word boundaries

            JBScrollPane outputTextAreaScrollPane = new JBScrollPane(outputTextArea);
            outputTextAreaScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

            outputPanel = new JPanel(new BorderLayout());
            Mctimings mctimings = new Mctimings(selectedBucket, selectedOutputFormat, selectedOptionalParameters);
            String output = mctimings.executeCommandAndReturnString();
            outputTextArea.setText(output);

            outputPanel.add(outputTextAreaScrollPane, BorderLayout.CENTER);
            dialogPanel.add(outputPanel, c);
        }

        dialogPanel.revalidate();
        dialogPanel.repaint();

    }

}
