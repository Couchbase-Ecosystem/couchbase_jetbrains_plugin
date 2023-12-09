package com.couchbase.intellij.workbench.chart;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.jcef.JBCefBrowser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapCbChart implements CbChart {

    protected ComboBox<String> latBox;
    protected ComboBox<String> lonBox;

    private String oldLatValue;
    private String oldLonValue;

    private JBCefBrowser browser;

    private List<JsonObject> results;

    private ItemListener latListener;
    private ItemListener lonListener;

    @Override
    public void render(Map<String, String> fields, List<JsonObject> results) {
        this.results = results;
        oldLatValue = latBox.getSelectedItem() == null ? null : latBox.getSelectedItem().toString();
        oldLonValue = lonBox.getSelectedItem() == null ? null : lonBox.getSelectedItem().toString();

        List<String> options = new ArrayList<>();
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            if (!"String".equals(entry.getValue()) && !"Unknown".equals(entry.getValue())) {
                options.add(entry.getKey());
            }
        }

        latBox.removeItemListener(latListener);
        latBox.removeItemListener(lonListener);

        latBox.removeAllItems();
        lonBox.removeAllItems();

        for (String option : options) {
            latBox.addItem(option);
            lonBox.addItem(option);
        }

        latBox.setSelectedItem(null);
        lonBox.setSelectedItem(null);

        latBox.addItemListener(latListener);
        lonBox.addItemListener(lonListener);

        if (options.contains(oldLatValue)) {
            latBox.setSelectedItem(oldLatValue);
        }

        if (options.contains(oldLonValue)) {
            lonBox.setSelectedItem(oldLonValue);
        }
    }

    @Override
    public JPanel getFieldsPanel() {


        latListener = e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (e.getItem() != null && lonBox.getSelectedItem() != null) {
                    renderChart();
                }
            }
        };

        lonListener = e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (e.getItem() != null && latBox.getSelectedItem() != null) {
                    renderChart();
                }
            }
        };

        latBox = new ComboBox<>();
        lonBox = new ComboBox<>();

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Lat"));
        topPanel.add(latBox);

        topPanel.add(new JLabel("Lon"));
        topPanel.add(lonBox);

        return topPanel;
    }

    public JPanel getMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        browser = new JBCefBrowser();
        panel.add(browser.getComponent(), BorderLayout.CENTER);
        return panel;
    }

    private void renderChart() {

        if (latBox.getSelectedItem() != null && lonBox.getSelectedItem() != null) {
            String template = loadResourceAsString("/chartTemplates/cmap.html");
            template = template.replace("JSON_DATA_TEMPLATE", JsonArray.from(results).toString())
                    .replace("GEO_LAT_TEMPLATE", latBox.getSelectedItem().toString())
                    .replace("GEO_LON_TEMPLATE", lonBox.getSelectedItem().toString());
            browser.loadHTML(template);
        } else {
            browser.loadHTML("");
        }
    }

    private static String loadResourceAsString(String resourcePath) {
        try (InputStream inputStream = MapCbChart.class.getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }
}
