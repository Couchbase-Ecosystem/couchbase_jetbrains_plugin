package com.couchbase.intellij.workbench.chart;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.tools.dialog.CollapsiblePanel;
import com.couchbase.intellij.workbench.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.util.ui.JBUI;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefDisplayHandler;
import org.cef.handler.CefMessageRouterHandlerAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    private JBCefBrowser browser;

    private List<JsonObject> results;

    private ItemListener latListener;
    private ItemListener lonListener;

    private JBTextField filterField;

    private MapBridge mapBridge;


    @Override
    public void render(Map<String, String> fields, List<JsonObject> results) {
        this.results = results;
        String oldLatValue = latBox.getSelectedItem() == null ? null : latBox.getSelectedItem().toString();
        String oldLonValue = lonBox.getSelectedItem() == null ? null : lonBox.getSelectedItem().toString();

        List<String> options = new ArrayList<>();
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            if (!"String".equals(entry.getValue()) && !"Unknown".equals(entry.getValue())) {
                options.add(entry.getKey());
            }
        }

        latBox.removeItemListener(latListener);
        lonBox.removeItemListener(lonListener);

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

        if (lonBox.getSelectedItem() == null && latBox.getSelectedItem() == null) {
            browser.loadHTML("");
        }
    }

    @Override
    public JPanel getFieldsPanel() {


        latListener = e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                renderChart();

            }
        };

        lonListener = e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                renderChart();

            }
        };

        latBox = new ComboBox<>();
        lonBox = new ComboBox<>();

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        topPanel.setBorder(null);
        topPanel.add(new JLabel("Lat"));
        topPanel.add(latBox);

        topPanel.add(new JLabel("Lon"));
        topPanel.add(lonBox);

        return topPanel;
    }


    public JPanel getMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        filterField = new JBTextField();
        mapBridge = new MapBridge(filterField);

        JPanel advancedContent = new JPanel();
        advancedContent.setBorder(JBUI.Borders.empty(5));
        advancedContent.setLayout(new BoxLayout(advancedContent, BoxLayout.LINE_AXIS));

        JLabel label = new JLabel("SQL++ Query Filter:");
        label.setBorder(JBUI.Borders.emptyRight(5));
        advancedContent.add(label);
        advancedContent.add(Box.createHorizontalGlue());


        filterField.setEditable(false);
        filterField.setMaximumSize(new Dimension(Integer.MAX_VALUE, filterField.getPreferredSize().height));
        filterField.setAlignmentY(Component.CENTER_ALIGNMENT);
        filterField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                filterField.selectAll();
            }
        });
        advancedContent.add(filterField);

        CollapsiblePanel advancedPanel = new CollapsiblePanel("Filter", advancedContent);
        advancedPanel.setBorder(JBUI.Borders.emptyTop(10));

        browser = new JBCefBrowser();

        panel.add(advancedPanel, BorderLayout.NORTH);
        panel.add(browser.getComponent(), BorderLayout.CENTER);
        return panel;
    }


    private void renderChart() {

        ObjectMapper mapper = new ObjectMapper();
        String circles = "[]";
        String polygons = "[]";

        try {
            circles = mapper.writeValueAsString(mapBridge.getCircles());
            polygons = mapper.writeValueAsString(mapBridge.getPolygons());
        } catch (Exception e) {
            Log.debug("An error occurred while parsing the list of shapes", e);
        }

        if (latBox.getSelectedItem() != null
                && lonBox.getSelectedItem() != null) {
            String template = loadResourceAsString("/chartTemplates/cmap.html");
            template = template.replace("JSON_DATA_TEMPLATE", JsonArray.from(results).toString())
                    .replace("GEO_CIRCLE_LIST", circles)
                    .replace("GEO_POLYGON_LIST", polygons)
                    .replace("GEO_LAT_TEMPLATE", latBox.getSelectedItem().toString())
                    .replace("GEO_LON_TEMPLATE", lonBox.getSelectedItem().toString());
            browser.loadHTML(template);
            CefMessageRouter msgRouter = CefMessageRouter.create(new CefMessageRouter.CefMessageRouterConfig("javaQuery", "javaCancel"));
            msgRouter.addHandler(new MessageRouterHandler(), true);
            browser.getJBCefClient().getCefClient().addMessageRouter(msgRouter);


            CefDisplayHandler displayHandler = new CustomDisplayHandler();
            browser.getJBCefClient().addDisplayHandler(displayHandler, browser.getCefBrowser());

        } else {
            browser.loadHTML("");
        }
    }

    private double[][] parseLatLngArray(JSONArray array) throws JSONException {
        double[][] latlng = new double[array.length()][];
        for (int i = 0; i < array.length(); i++) {
            JSONArray innerArray = array.getJSONArray(i);
            latlng[i] = new double[innerArray.length()];
            for (int j = 0; j < innerArray.length(); j++) {
                latlng[i][j] = innerArray.getDouble(j);
            }
        }
        return latlng;
    }


    private static String loadResourceAsString(String resourcePath) {
        try (InputStream inputStream = MapCbChart.class.getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException | NullPointerException e) {
            Log.error("An error occurred while loading the html for the map");
            return null;
        }
    }


    public class MessageRouterHandler extends CefMessageRouterHandlerAdapter {
        @Override
        public boolean onQuery(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {

            try {
                JSONObject jsonQuery = new JSONObject(request);
                String action = jsonQuery.getString("action");
                JSONObject data = jsonQuery.getJSONObject("data");

                switch (action) {
                    case "addCircle":
                        mapBridge.addCircle(
                                data.getString("id"),
                                data.getDouble("lat"),
                                data.getDouble("lon"),
                                data.getDouble("radius")
                        );
                        break;
                    case "addPolygon":
                        double[][] latlng = parseLatLngArray(data.getJSONArray("latlng"));
                        mapBridge.addPolygon(data.getString("id"), latlng);
                        break;
                    case "removeCircle":
                        mapBridge.removeCircle(data.getString("id"));
                        break;
                    case "removePolygon":
                        mapBridge.removePolygon(data.getString("id"));
                        break;
                    default:
                        break;
                }
            } catch (Exception ex) {
                Log.debug("An error occurred while parsing the response from the chart", ex);
            }

            callback.success(request);
            return true;
        }
    }
}
