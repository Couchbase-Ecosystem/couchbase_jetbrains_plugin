package com.couchbase.intellij.tools.dialog;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tools.CBTools;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import utils.ProcessUtils;
import utils.TemplateUtil;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;

public class MctimingsDialog extends DialogWrapper {

    private JComboBox<String> bucketComboBox;
    private JComboBox<String> outputFormatComboBox;
    private JPanel outputPanel;
    private JPanel dialogPanel;
    private GridBagConstraints c;
    private final JComboCheckBox optionalParametersComboCheckBox = new JComboCheckBox();

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
        outputFormatComboBox.addItem("Histogram");
        outputFormatComboBox.addItem("Json");
        outputFormatComboBox.addItem("Json pretty printed");
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
                "Flush", "GetQ", "No-op", "Version", "GetK", "GetKQ", "Append", "Prepend" };

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
        outputPanel = new JPanel(new BorderLayout());

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

    public static class ChartGenerator {
        protected JPanel generateBarChart(String[] labels, int[] values) {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            for (int i = 0; i < labels.length; i++) {
                dataset.addValue(values[i], "Occurrences", labels[i]);
            }

            JFreeChart barChart = ChartFactory.createBarChart(
                    "Bar Chart for Specified Period",
                    "Time Buckets",
                    "Occurrences",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true, true, false);

            // Set dynamic range for Y-axis
            NumberAxis rangeAxis = (NumberAxis) barChart.getCategoryPlot().getRangeAxis();
            rangeAxis.setRange(0, Arrays.stream(values).max().getAsInt());

            // Rotate X-axis labels
            CategoryAxis domainAxis = barChart.getCategoryPlot().getDomainAxis();
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

            ChartPanel chartPanel = new ChartPanel(barChart);
            chartPanel.setDisplayToolTips(true); // Enable tooltips

            return chartPanel;
        }
    }

    @Override
    protected void doOKAction() {
        dialogPanel.add(outputPanel, c);
        getWindow().setMinimumSize(new Dimension(1800, 1000));

        String selectedOutputFormat = (String) outputFormatComboBox.getSelectedItem();
        if (Objects.equals(selectedOutputFormat, "Histogram")) {
            // Generate a bar chart using the hardcoded values
            final String[] labels = { "[0.00 - 2.00]us", "[2.00 - 6.00]us", "[6.00 - 7.00]us", "[7.00 - 9.00]us",
                    "[9.00 - 11.00]us", "[11.00 - 13.00]us", "[13.00 - 15.00]us", "[15.00 - 16.00]us",
                    "[16.00 - 18.00]us", "[18.00 - 20.00]us", "[20.00 - 22.00]us", "[22.00 - 23.00]us",
                    "[23.00 - 25.00]us", "[25.00 - 26.00]us", "[26.00 - 28.00]us", "[28.00 - 30.00]us",
                    "[30.00 - 31.00]us", "[31.00 - 33.00]us", "[33.00 - 35.00]us", "[35.00 - 37.00]us",
                    "[37.00 - 39.00]us", "[39.00 - 39.00]us", "[39.00 - 41.00]us", "[41.00 - 41.00]us",
                    "[41.00 - 43.00]us", "[43.00 - 45.00]us", "[45.00 - 47.00]us", "[47.00 - 49.00]us",
                    "[49.00 - 51.00]us", "[51.00 - 53.00]us", "[53.00 - 57.00]us", "[57.00 - 57.00]us",
                    "[57.00 - 59.00]us", "[59.00 - 63.00]us", "[63.00 - 67.00]us", "[67.00 - 71.00]us",
                    "[71.00 - 75.00]us", "[75.00 - 75.00]us", "[75.00 - 79.00]us", "[79.00 - 83.00]us",
                    "[83.00 - 91.00]us", "[91.00 - 95.00]us", "[95.00 - 95.00]us", "[95.00 - 103.00]us",
                    "[103.00 - 111.00]us", "[111.00 - 123.00]us", "[123.00 - 127.00]us", "[127.00 - 135.00]us",
                    "[135.00 - 151.00]us", "[151.00 - 167.00]us", "[167.00 - 199.00]us", "[199.00 - 199.00]us",
                    "[199.00 - 215.00]us", "[215.00 - 215.00]us", "[215.00 - 231.00]us", "[231.00 - 287.00]us",
                    "[287.00 - 335.00]us", "[335.00 - 399.00]us", "[399.00 - 431.00]us", "[431.00 - 479.00]us",
                    "[479.00 - 479.00]us", "[479.00 - 543.00]us", "[543.00 - 831.00]us", "[831.00 - 831.00]us",
                    "[831.00 - 895.00]us", "[895.00 - 895.00]us", "[0.89 - 1.09]ms", "[1.09 - 1.09]ms",
                    "[1.09 - 1.09]ms", "[1.09 - 1.15]ms", "[1.15 - 1.15]ms", "[1.15 - 1.15]ms", "[1.15 - 1.15]ms",
                    "[1.15 - 1.15]ms", "[1.15 - 2.17]ms", "[2.17 - 2.17]ms", };

            final int[] values = { 3, 3909, 1596, 2727, 2698, 2013, 1664, 766, 1336, 1237, 1016, 512, 892, 394, 672,
                    645, 274, 453, 424, 310, 259, 0, 234, 0, 197, 134, 91, 96, 76, 60, 91, 0, 38, 56, 39, 36, 36, 0, 20,
                    11, 25, 13, 0, 14, 9, 10, 1, 6, 5, 5, 6, 0, 5, 0, 4, 1, 1, 2, 1, 2, 0, 1, 1, 0, 1, 0, 1, 0, 0, 1, 0,
                    0, 0, 0, 1, 0 };

            ChartGenerator chartGenerator = new ChartGenerator();
            JPanel chartPanel = chartGenerator.generateBarChart(labels, values);
            outputPanel.removeAll();
            outputPanel.add(chartPanel, BorderLayout.CENTER);
            outputPanel.revalidate();
            outputPanel.repaint();
        } else if (Objects.requireNonNull(selectedOutputFormat).startsWith("Json")) {
            // Generate an empty text area
            JTextArea outputTextArea = new JTextArea();

            outputTextArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(outputTextArea);
            outputPanel.removeAll();
            outputPanel.add(scrollPane, BorderLayout.CENTER);
            outputPanel.revalidate();
            outputPanel.repaint();

            // TODO: Impplement Text area
        }

    }

    public void executeCommand(JTextArea outputTextArea) {
        List<String> command = new ArrayList<>();
        command.add(CBTools.getTool(CBTools.Type.MCTIMINGS).getPath());

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
        if ("Json".equals(selectedOutputFormat)) {
            command.add("-o");
            command.add("json");
        } else if ("Json pretty printed".equals(selectedOutputFormat)) {
            command.add("-o");
            command.add("jsonpretty");
        }

        // Add the selected optional parameters to the command
        List<String> selectedOptionalParameters = optionalParametersComboCheckBox.getSelectedItems();
        for (String parameter : selectedOptionalParameters) {
            command.add("--" + parameter.toLowerCase());
        }

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        try {
            Process process = processBuilder.start();
            ProcessUtils.printOutput(process, outputTextArea);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
