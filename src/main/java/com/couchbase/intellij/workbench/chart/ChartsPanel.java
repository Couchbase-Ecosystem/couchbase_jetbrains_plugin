package com.couchbase.intellij.workbench.chart;

import com.couchbase.client.java.json.JsonObject;
import com.intellij.openapi.ui.ComboBox;
import utils.JsonObjectUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartsPanel {

    protected ComboBox<String> chartCombobox;

    private CbChart cbChart;
    private JPanel fieldsPanel;
    private JPanel bodyPanel;
    private JPanel fieldsChoice;
    private JPanel bodyChoice;


    public JPanel createChartPanel() {
        fieldsPanel = new JPanel(new FlowLayout());
        bodyPanel = new JPanel(new BorderLayout());

        String[] chartOptions = {"Line", "Bar", "Pie", "Donut", "Map"};
        chartCombobox = new ComboBox<>(chartOptions);
        chartCombobox.addItemListener(e -> {

            if (e.getStateChange() == ItemEvent.SELECTED) {

                if (fieldsChoice != null) {
                    fieldsPanel.remove(fieldsChoice);
                }

                if (bodyChoice != null) {
                    bodyPanel.remove(bodyPanel);
                }

                if ("Map".equals(e.getItem().toString())) {
                    cbChart = new MapCbChart();
                    fieldsChoice = cbChart.getFieldsPanel();
                    fieldsPanel.add(fieldsChoice);
                    bodyChoice = cbChart.getMainPanel();
                    bodyPanel.add(bodyChoice);
                }
                fieldsPanel.revalidate();
            }
        });

        chartCombobox.setSelectedItem(null);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Type"));
        topPanel.add(chartCombobox);


        fieldsPanel.add(topPanel, BorderLayout.NORTH);
        return fieldsPanel;
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
