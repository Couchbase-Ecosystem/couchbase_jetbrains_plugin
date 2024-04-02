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
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class LineChart implements CbChart {

    private ItemListener itemListener;

    protected ComboBox<String> xBox;
    protected ComboBox<String> yBox;

    private JBCefBrowser browser;

    private List<JsonObject> results;


    @Override
    public void render(Map<String, String> fields, List<JsonObject> results) {
        this.results = results;
        String oldLabelVal = xBox.getSelectedItem() == null ? null : xBox.getSelectedItem().toString();
        String oldValueVal = yBox.getSelectedItem() == null ? null : yBox.getSelectedItem().toString();


        List<String> valOptions = ChartUtil.filterOutAttributes(fields, Arrays.asList("String", "Unknown"));
        List<String> labelsOptions = ChartUtil.filterOutAttributes(fields, List.of("Unknown"));

        ChartUtil.addItemsWithoutChangeListener(xBox, labelsOptions, itemListener);
        ChartUtil.addItemsWithoutChangeListener(yBox, valOptions, itemListener);

        if (valOptions.contains(oldValueVal)) {
            yBox.setSelectedItem(oldValueVal);
        }

        if (labelsOptions.contains(oldLabelVal)) {
            xBox.setSelectedItem(oldLabelVal);
        }

        if (yBox.getSelectedItem() == null && xBox.getSelectedItem() == null) {
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

        xBox = new ComboBox<>();
        yBox = new ComboBox<>();

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        topPanel.setBorder(null);
        topPanel.add(new JLabel("X Axis:"));
        topPanel.add(xBox);

        topPanel.add(new JLabel("Y Axis:"));
        topPanel.add(yBox);

        String fontKeywordColor = ColorHelper.getKeywordColor();
        topPanel.add(ChartUtil.getInfoLabel("<html>" +
                "<h2>Line Chart</h2><br>" +
                "<strong>X Axis:</strong>&nbsp;String or Number<br>" +
                "<strong>Y Axis:</strong>&nbsp;Must be a Number<br><br>" +
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


        if (yBox.getSelectedItem() != null
                && xBox.getSelectedItem() != null) {


            JsonArray values = ChartUtil.getAttributeValues(results, yBox.getSelectedItem().toString());
            JsonArray labels = ChartUtil.getAttributeValues(results, xBox.getSelectedItem().toString());


            String template = ChartUtil.loadResourceAsString("/chartTemplates/lines.html");
            template = template.replace("JSON_DATA_TEMPLATE", JsonArray.from(results).toString())
                    .replace("DATA_LABELS", labels.toString())
                    .replaceAll("JS_LIB_PATH_", Matcher.quoteReplacement(CBFolders.getInstance().getJsDependenciesPath() + File.separator))
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
