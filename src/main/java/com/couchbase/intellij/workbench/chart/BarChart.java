package com.couchbase.intellij.workbench.chart;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.tools.CBFolders;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.jcef.JBCefBrowser;
import org.cef.handler.CefDisplayHandler;
import utils.ColorHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BarChart implements CbChart {

    private ItemListener itemListener;

    protected ComboBox<String> labelsBox;
    protected ComboBox<String> valuesBox;

    private JBCefBrowser browser;

    private List<JsonObject> results;


    @Override
    public void render(Map<String, String> fields, List<JsonObject> results) {
        this.results = results;
        String oldLabelVal = labelsBox.getSelectedItem() == null ? null : labelsBox.getSelectedItem().toString();
        String oldValueVal = valuesBox.getSelectedItem() == null ? null : valuesBox.getSelectedItem().toString();


        List<String> valOptions = ChartUtil.filterOutAttributes(fields, Arrays.asList("String", "Unknown"));
        List<String> labelsOptions = ChartUtil.filterOutAttributes(fields, Arrays.asList("Unknown"));

        ChartUtil.addItemsWithoutChangeListener(labelsBox, labelsOptions, itemListener);
        ChartUtil.addItemsWithoutChangeListener(valuesBox, valOptions, itemListener);

        if (valOptions.contains(oldValueVal)) {
            valuesBox.setSelectedItem(oldValueVal);
        }

        if (labelsOptions.contains(oldLabelVal)) {
            labelsBox.setSelectedItem(oldLabelVal);
        }

        if (valuesBox.getSelectedItem() == null && labelsBox.getSelectedItem() == null) {
            browser.loadHTML("");
        }
    }

    @Override
    public JPanel getFieldsPanel() {
        itemListener = e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                renderChart();

            }
        };

        labelsBox = new ComboBox<>();
        valuesBox = new ComboBox<>();

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        topPanel.setBorder(null);
        topPanel.add(new JLabel("Labels:"));
        topPanel.add(labelsBox);

        topPanel.add(new JLabel("Values:"));
        topPanel.add(valuesBox);

        String fontKeywordColor = ColorHelper.getKeywordColor();
        topPanel.add(ChartUtil.getInfoLabel("<html>" +
                "<h2>Bar Chart</h2><br>" +
                "<strong>Labels:</strong>&nbsp;String or Number<br>" +
                "<strong>Values:</strong>&nbsp;Must be a Number<br><br>" +
                "<strong>Ex:</strong>" +
                "<pre>\n" +
                "[<br>" +
                "&nbsp;&nbsp;{ <span style='color:" + fontKeywordColor + "'>&quot;month&quot;</span>: &quot;Jan&quot;, <span style='color:" + fontKeywordColor + "'>&quot;value&quot;</span>: 10}<br/>" +
                "&nbsp;&nbsp;{ <span style='color:" + fontKeywordColor + "'>&quot;month&quot;</span>: &quot;Feb&quot;, <span style='color:" + fontKeywordColor + "'>&quot;value&quot;</span>: 20}<br/>" +
                "&nbsp;&nbsp;{ <span style='color:" + fontKeywordColor + "'>&quot;month&quot;</span>: &quot;Mar&quot;, <span style='color:" + fontKeywordColor + "'>&quot;value&quot;</span>: 30}<br/>" +
                "]" +
                "</pre>\n" +
                "</html>"));

        return topPanel;
    }

    @Override
    public JPanel getMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        browser = new JBCefBrowser();
        panel.add(browser.getComponent(), BorderLayout.CENTER);
        return panel;
    }


    private void renderChart() {


        if (valuesBox.getSelectedItem() != null
                && labelsBox.getSelectedItem() != null) {


            JsonArray values = ChartUtil.getAttributeValues(results, valuesBox.getSelectedItem().toString());
            JsonArray labels = ChartUtil.getAttributeValues(results, labelsBox.getSelectedItem().toString());


            String template = ChartUtil.loadResourceAsString("/chartTemplates/bar.html");
            template = template.replace("JSON_DATA_TEMPLATE", JsonArray.from(results).toString())
                    .replaceAll("JS_LIB_PATH", CBFolders.getInstance().getJsDependenciesPath())
                    .replace("DATA_LABELS", labels.toString())
                    .replace("DATA_VALUES", values.toString())
                    .replace("ISDARK", String.valueOf(ColorHelper.isDarkTheme()));
            browser.loadHTML(template);

            CefDisplayHandler displayHandler = new CustomDisplayHandler();
            browser.getJBCefClient().addDisplayHandler(displayHandler, browser.getCefBrowser());

        } else {
            browser.loadHTML("");
        }
    }

}
