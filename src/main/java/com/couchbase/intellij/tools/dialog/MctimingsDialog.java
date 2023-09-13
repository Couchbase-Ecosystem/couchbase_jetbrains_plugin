package com.couchbase.intellij.tools.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import org.jetbrains.annotations.Nullable;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tools.CBTools;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.ui.JBUI;

import utils.ProcessUtils;
import utils.TemplateUtil;

public class MctimingsDialog extends DialogWrapper {

    private final JComboCheckBox optionalParametersComboCheckBox = new JComboCheckBox();
    private GridBagConstraints c;
    private JComboBox<String> bucketComboBox;
    private JComboBox<String> outputFormatComboBox;
    private JPanel outputPanel;
    private JPanel dialogPanel;

    private static final String SUMMARY_OF_ALL_OPERATIONS = "Summary of All Operations";
    private static final String HISTOGRAM = "Histogram";
    private static final String JSON = "Json";
    private static final String JSON_PRETTY_PRINTED = "Json pretty printed";

    private JTextArea outputTextArea;

    public MctimingsDialog() {
        super(true);
        init();
        setTitle("Couchbase Mctimings Dialog");
        getWindow().setMinimumSize(new Dimension(800, 600));
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
        bucketComboBox = new JComboBox<>();
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
        outputFormatComboBox = new JComboBox<>();
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

        List<String> optionalParametersList = new ArrayList<>(Arrays.asList(optionalParameters));
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
        if (outputPanel != null) {
            outputPanel.removeAll();
            dialogPanel.remove(outputPanel);
        }

        String selectedOutputFormat = (String) outputFormatComboBox.getSelectedItem();
        if (Objects.equals(selectedOutputFormat, HISTOGRAM)) {
            outputPanel = new JPanel();
            outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.Y_AXIS));
            executeCommand();

        } else {
            outputTextArea = new JTextArea();

            outputTextArea.setEditable(false);
            outputTextArea.setLineWrap(true); // Enable line wrapping
            outputTextArea.setWrapStyleWord(true); // Wrap lines at word boundaries

            JScrollPane scrollPane = new JScrollPane(outputTextArea);
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

            outputPanel = new JPanel(new BorderLayout());
            executeCommand();
            outputPanel.add(scrollPane, BorderLayout.CENTER);
            outputPanel.revalidate();
            outputPanel.repaint();
            dialogPanel.add(outputPanel, c);
        }

        dialogPanel.revalidate();
        dialogPanel.repaint();

    }

    public void executeCommand() {
        List<String> command = new ArrayList<>();
        command.add(CBTools.getTool(CBTools.Type.MCTIMINGS).getPath());
        command.add("-h");
        command.add(ActiveCluster.getInstance().getClusterURL().replaceFirst("^couchbase://", ""));
        command.add("-p");
        command.add(ActiveCluster.getInstance().isSSLEnabled() ? "11207" : "11210");
        command.add("-u");
        command.add(ActiveCluster.getInstance().getUsername());
        command.add("-P");
        command.add(ActiveCluster.getInstance().getPassword());

        // Add the selected bucket to the command
        String selectedBucket = (String) bucketComboBox.getSelectedItem();
        if (!"All buckets".equals(selectedBucket)) {
            command.add("-b");
            command.add(selectedBucket);
        } else {
            command.add("-a");
        }

        // Add the selected output format to the command
        String selectedOutputFormat = (String) outputFormatComboBox.getSelectedItem();
        if (Objects.equals(selectedOutputFormat, HISTOGRAM)) {
            command.add("-v");
        } else if (Objects.equals(selectedOutputFormat, JSON)) {
            command.add("-j");
        } else if (Objects.equals(selectedOutputFormat, JSON_PRETTY_PRINTED)) {
            command.add("-j");
            command.add("-v");
        }

        // Add the selected optional parameters to the command
        List<String> selectedOptionalParameters = optionalParametersComboCheckBox.getSelectedItems();
        if (!selectedOptionalParameters.isEmpty()) {
            String parameters = String.join(" ", selectedOptionalParameters).toLowerCase();
            command.add(parameters);
        }

        Log.debug("Command is" + command);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        try {
            Process process = processBuilder.start();
            if (Objects.equals(selectedOutputFormat, HISTOGRAM)) {
                Map<String, Map<String, Map<String, String>>> histogramData = parseHistogram(process);
                generateHistograms(histogramData);
            } else
                ProcessUtils.printOutput(process, outputTextArea);
        } catch (IOException e) {
            Log.error("Exception Occurred: ", e);
        }
    }

    public static Map<String, Map<String, Map<String, String>>> parseHistogram(Process process) throws IOException {
        Map<String, Map<String, Map<String, String>>> histogramData = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            String currentBucket = "";
            String currentOperation = "";
            String bucketTag = "Bucket:";
            String operationTag = "The following data is collected for ";
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(bucketTag)) {
                    currentBucket = line.substring(bucketTag.length()).trim().replaceAll("[\"']", "");
                    histogramData.put(currentBucket, new HashMap<>());
                } else if (line.startsWith(operationTag)) {
                    currentOperation = line.substring(operationTag.length()).trim().replaceAll("[\"']", "");
                    histogramData.get(currentBucket).put(currentOperation, new HashMap<>());
                } else if (histogramData.containsKey(currentBucket)
                        && histogramData.get(currentBucket).containsKey(currentOperation)) {
                    String label;
                    String value;

                    int labelStartIndex = line.indexOf(" (");
                    int valueStartIndex = line.indexOf("%)");
                    int pipeIndex = line.indexOf("|");

                    if (labelStartIndex != -1 && valueStartIndex != -1 && pipeIndex != -1) {
                        label = line.substring(0, labelStartIndex).trim();
                        value = line.substring(valueStartIndex + 3, pipeIndex).trim();
                        histogramData.get(currentBucket).get(currentOperation).put(label, value);
                    }
                }
            }
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Log.error(line);
            }
        }

        return histogramData;
    }

    public void generateHistograms(Map<String, Map<String, Map<String, String>>> histogramData) {
        // For each bucket in the histogram data
        for (Map.Entry<String, Map<String, Map<String, String>>> bucketEntry : histogramData.entrySet()) {
            String bucket = bucketEntry.getKey();
            Map<String, Map<String, String>> operationsData = bucketEntry.getValue();

            // For each operation in the bucket
            for (Map.Entry<String, Map<String, String>> operationEntry : operationsData.entrySet()) {
                String operation = operationEntry.getKey();
                Map<String, String> operationData = operationEntry.getValue();

                // Get the labels and values for this operation
                int size = operationData.size();
                String[] labels = new String[size];
                int[] values = new int[size];
                int index = 0;

                for (Map.Entry<String, String> entry : operationData.entrySet()) {
                    labels[index] = entry.getKey();
                    values[index] = Integer.parseInt(entry.getValue());
                    index++;
                }

                // Generate a bar chart using the labels and values
                ChartGenerator chartGenerator = new ChartGenerator();
                JPanel chartPanel = chartGenerator.generateBarChart(bucket, operation, labels, values);

                // Set preferred size for the chart panel
                chartPanel.setPreferredSize(new Dimension(1600, 800));

                // Add the chart to the output panel
                outputPanel.add(chartPanel);
            }
        }

        // Create a JScrollPane containing the output panel
        JScrollPane scrollPane = new JScrollPane(outputPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Replace output panel with scroll pane in dialog panel
        dialogPanel.remove(outputPanel);
        dialogPanel.add(scrollPane, c);
    }

    public static class ChartGenerator {
        protected JPanel generateBarChart(String bucket, String operation, String[] labels, int[] values) {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            for (int i = 0; i < labels.length; i++) {
                dataset.addValue(values[i], "Occurrences", labels[i]);
            }

            JFreeChart barChart = ChartFactory.createBarChart(
                    "Operation Duration Histogram for " + bucket + "with operation " + operation,
                    "Operation Duration",
                    "Occurrences",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true, true, false);

            // Set dynamic range for Y-axis
            NumberAxis rangeAxis = (NumberAxis) barChart.getCategoryPlot().getRangeAxis();
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

            // Rotate X-axis labels
            CategoryAxis domainAxis = barChart.getCategoryPlot().getDomainAxis();
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

            ChartPanel chartPanel = new ChartPanel(barChart);
            chartPanel.setDisplayToolTips(true); // Enable tooltips
            chartPanel.setMouseWheelEnabled(true); // Enable zooming using mouse wheel
            chartPanel.setMouseZoomable(true); // Enable zooming using mouse drag

            return chartPanel;
        }
    }

}
