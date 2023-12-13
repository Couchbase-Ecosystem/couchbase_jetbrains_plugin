package com.couchbase.intellij.workbench.chart;

import com.couchbase.client.java.json.JsonObject;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.util.ui.JBUI;
import utils.JsonObjectUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ChartsPanel {

    protected ComboBox<String> chartCombobox;

    private CbChart cbChart;
    private JPanel fieldsPanel;
    private JPanel bodyPanel;
    private JPanel fieldsChoice;
    private JPanel bodyChoice;


    public JPanel createChartPanel(Supplier<List<JsonObject>> result) {
        fieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        fieldsPanel.setBorder(null);
        bodyPanel = new JPanel(new BorderLayout());

        String[] chartOptions = {"Line", "Bar", "Pie", "Doughnut", "Map"};
        chartCombobox = new ComboBox<>(chartOptions);
        chartCombobox.addItemListener(e -> {

            if (e.getStateChange() == ItemEvent.SELECTED) {

                if (fieldsChoice != null) {
                    fieldsPanel.remove(fieldsChoice);
                }

                if (bodyChoice != null) {
                    bodyPanel.remove(bodyChoice);
                }

                if ("Map".equals(e.getItem().toString())) {
                    cbChart = new MapCbChart();
                } else if ("Pie".equals(e.getItem().toString())) {
                    cbChart = new PieDoughnutChart(PieDoughnutChart.Type.PIE);
                } else if ("Doughnut".equals(e.getItem().toString())) {
                    cbChart = new PieDoughnutChart(PieDoughnutChart.Type.DOUGHNUT);
                } else if ("Bar".equals(e.getItem().toString())) {
                    cbChart = new BarChart();
                }

                fieldsChoice = cbChart.getFieldsPanel();
                fieldsPanel.add(fieldsChoice);
                bodyChoice = cbChart.getMainPanel();
                bodyPanel.add(bodyChoice, BorderLayout.CENTER);
                fieldsPanel.revalidate();
                updateChart(result.get());
            }
        });

        chartCombobox.setSelectedItem(null);
        fieldsChoice = new JPanel();
        fieldsChoice.setBorder(JBUI.Borders.emptyTop(5));
        fieldsPanel.add(fieldsChoice);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topPanel.setBorder(null);
        JLabel typeLabel = new JLabel("Type");
        typeLabel.setBorder(JBUI.Borders.emptyLeft(5));
        topPanel.add(typeLabel);
        topPanel.add(chartCombobox);
        topPanel.add(fieldsPanel);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(bodyPanel, BorderLayout.CENTER);
        mainPanel.setBorder(JBUI.Borders.emptyTop(5));
        return mainPanel;
    }

    public void updateChart(List<JsonObject> results) {
        Map<String, String> fields = getFieldTypes(results);
        if (cbChart != null) {
            cbChart.render(fields, results);
        }
    }


    private Map<String, String> getFieldTypes(List<JsonObject> results) {

        if (results == null) {
            return new HashMap<>();
        }

        int counter = 0;
        Map<String, String> fields = new HashMap<>();
        for (JsonObject obj : results) {

            //we only analyze the up to the first 10 elements
            if (counter > 10) {
                break;
            }

            Map<String, String> map = JsonObjectUtil.generatePaths(obj, "", true);
            if (fields.isEmpty()) {
                fields = map;
            } else {

                for (Map.Entry<String, String> entry : fields.entrySet()) {
                    //contains key but the value type is different and the new value is not unknown
                    if (map.containsKey(entry.getKey())
                            && !map.get(entry.getKey()).equals(entry.getValue())
                            && !"Unknown".equals(map.get(entry.getKey()))) {

                        //if the current value is unknown, accept the new one
                        if ("Unknown".equals(entry.getValue())) {
                            entry.setValue(map.get(entry.getKey()));
                            //if the current value is string, just keep it as string
                        } else if (!"String".equals(entry.getValue())) {
                            entry.setValue(map.get(entry.getKey()));
                        }
                    }
                }

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    fields.putIfAbsent(entry.getKey(), entry.getValue());
                }
            }

            counter++;
        }

        return fields;
    }
}
