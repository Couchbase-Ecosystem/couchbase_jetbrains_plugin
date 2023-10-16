package com.couchbase.intellij.tools;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.workbench.Log;

import utils.ProcessUtils;

public class Mctimings {

    private static final String HISTOGRAM = "Histogram";
    private static final String JSON = "Json";
    private static final String JSON_PRETTY_PRINTED = "Json pretty printed";

    private final String selectedBucket;
    private final String selectedOutputFormat;
    private final List<String> selectedOptionalParameters;

    public Mctimings(String selectedBucket, String selectedOutputFormat, List<String> selectedOptionalParameters) {
        this.selectedBucket = selectedBucket;
        this.selectedOutputFormat = selectedOutputFormat;
        this.selectedOptionalParameters = selectedOptionalParameters;

    }

    public JPanel executeCommandAndReturnPanel() {
        JPanel outputPanel = new JPanel();
        outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.Y_AXIS));
        List<String> command = executeCommand();

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        try {
            Process process = processBuilder.start();
            Map<String, Map<String, Map<String, String>>> histogramData = parseHistogram(process);
            outputPanel = generateHistograms(histogramData);

        } catch (IOException e) {
            Log.error("Exception Occurred: ", e);
        }
        return outputPanel;
    }

    public String executeCommandAndReturnString() {
        String output = "";
        List<String> command = executeCommand();

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        try {
            Process process = processBuilder.start();
            output = ProcessUtils.returnOutput(process);
        } catch (IOException e) {
            Log.error("Exception Occurred: ", e);
        }
        return output;
    }

    public List<String> executeCommand() {
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
        if (!"All buckets".equals(selectedBucket)) {
            command.add("-b");
            command.add(selectedBucket);
        } else {
            command.add("-a");
        }

        // Add the selected output format to the command
        if (Objects.equals(selectedOutputFormat, HISTOGRAM)) {
            command.add("-v");
        } else if (Objects.equals(selectedOutputFormat, JSON)) {
            command.add("-j");
        } else if (Objects.equals(selectedOutputFormat, JSON_PRETTY_PRINTED)) {
            command.add("-j");
            command.add("-v");
        }

        // Add the selected optional parameters to the command
        if (!selectedOptionalParameters.isEmpty()) {
            for (String parameter : selectedOptionalParameters) {
                String parameterLowerCase = parameter.toLowerCase();
                command.add(parameterLowerCase);
            }
        }

        Log.debug("Command is" + command);
        return command;
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

    public JPanel generateHistograms(Map<String, Map<String, Map<String, String>>> histogramData) {
        JPanel outputPanel = new JPanel();
        outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.Y_AXIS));

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

        return outputPanel;

    }

    public static class ChartGenerator {
        protected JPanel generateBarChart(String bucket, String operation, String[] labels, int[] values) {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            for (int i = 0; i < labels.length; i++) {
                dataset.addValue(values[i], "Occurrences", labels[i]);
            }

            JFreeChart barChart = ChartFactory.createBarChart(
                    "Operation Duration Histogram for " + bucket + "with operation " + operation, "Operation Duration",
                    "Occurrences", dataset, PlotOrientation.VERTICAL, true, true, false);

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
