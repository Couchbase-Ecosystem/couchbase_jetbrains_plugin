package com.couchbase.intellij.searchworkbench.actions;

import com.couchbase.intellij.DocumentFormatter;
import com.couchbase.intellij.tools.CBFolders;
import com.couchbase.intellij.workbench.Log;
import com.couchbase.intellij.workbench.chart.ChartUtil;
import com.couchbase.intellij.workbench.chart.CustomDisplayHandler;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.util.ui.JBUI;
import lombok.Data;
import lombok.Getter;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefDisplayHandler;
import org.cef.handler.CefMessageRouterHandlerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class CoordinatesDialog extends DialogWrapper {

    public static final String OPT_LINE = "Line";
    public static final String OPT_CIRCLE = "Circle";
    public static final String OPT_RECTANGLE = "Rectangle";
    public static final String OPT_POLYGON = "Polygon";
    public static final String OPT_POINT = "Point";
    public static final String OPT_MULTI_LINE = "MultiLine";
    public static final String OPT_MULTI_POINT = "MultiPoint";
    public static final String OPT_MULTI_POLYGON = "MultiPolygon";
    private final Editor editor;

    private JBLabel errorLabel;

    private ComboBox chartCombobox;

    private JBCefBrowser browser;

    private MessageRouterHandler messageRouterHandler;

    private final JsonFilterBlock block;

    public CoordinatesDialog(Editor editor, JsonFilterBlock block) {
        super(true);
        this.editor = editor;
        this.block = block;
        init();
        setTitle("Add Shape Coordinates");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        browser = new JBCefBrowser();
        String[] chartOptions = {OPT_LINE, OPT_CIRCLE, OPT_RECTANGLE, OPT_POLYGON, OPT_POINT, OPT_MULTI_LINE, OPT_MULTI_POINT, OPT_MULTI_POLYGON};
        chartCombobox = new ComboBox<>(chartOptions);
        chartCombobox.setSelectedItem(null);
        errorLabel = new JBLabel("");
        errorLabel.setForeground(Color.decode("#FF4444"));
        errorLabel.setBorder(JBUI.Borders.emptyTop(5));

        chartCombobox.addItemListener(e -> {

            if (e.getStateChange() == ItemEvent.SELECTED) {

                errorLabel.setText("");
                errorLabel.revalidate();

                String selected = e.getItem().toString();
                String shapeChoice = getChoice(selected);

                if (!shapeChoice.isBlank()) {

                    String template = ChartUtil.loadResourceAsString("/chartTemplates/coordinates.html");
                    template = template.replace("SHAPE_CHOICE", shapeChoice)
                            .replace("ALLOW_MULTIPLE", chartCombobox.getSelectedItem().toString().contains("Multi") ? "true" : "false")
                            .replaceAll("JS_LIB_PATH_", Matcher.quoteReplacement(CBFolders.getInstance().getJsDependenciesPath() + File.separator));
                    browser.loadHTML(template);
                    messageRouterHandler = new MessageRouterHandler();
                    CefMessageRouter msgRouter = CefMessageRouter.create(new CefMessageRouter.CefMessageRouterConfig("javaQuery", "javaCancel"));
                    msgRouter.addHandler(messageRouterHandler, true);
                    browser.getJBCefClient().getCefClient().addMessageRouter(msgRouter);
                    CefDisplayHandler displayHandler = new CustomDisplayHandler();
                    browser.getJBCefClient().addDisplayHandler(displayHandler, browser.getCefBrowser());
                    browser.getComponent().requestFocus();
                } else {
                    browser.loadHTML("");
                }
            }
        });

        if (block.getType() != null) {

            if (GeoQueryConstants.LINE_STRING.equals(block.getType())) {
                chartCombobox.setSelectedItem(OPT_LINE);
            } else if (GeoQueryConstants.RADIUS.equals(block.getType()) || GeoQueryConstants.CIRCLE.equals(block.getType())) {
                chartCombobox.setSelectedItem(OPT_CIRCLE);
            } else if (GeoQueryConstants.POLYGON.equals(block.getType())
                    || GeoQueryConstants.POLYGON_POINTS.equals(block.getType())) {
                chartCombobox.setSelectedItem(OPT_POLYGON);
            } else if (GeoQueryConstants.POINT.equals(block.getType())) {
                chartCombobox.setSelectedItem(OPT_POINT);
            } else if (GeoQueryConstants.MULTI_LINE_STRING.equals(block.getType())) {
                chartCombobox.setSelectedItem(OPT_MULTI_LINE);
            } else if (GeoQueryConstants.MULTI_POLYGON.equals(block.getType())) {
                chartCombobox.setSelectedItem(OPT_MULTI_POLYGON);
            } else if (GeoQueryConstants.MULTI_POINT.equals(block.getType())) {
                chartCombobox.setSelectedItem(OPT_MULTI_POINT);
            } else {
                chartCombobox.setSelectedItem(OPT_RECTANGLE);
            }
            chartCombobox.setEnabled(false);
        }


        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = JBUI.insets(5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Ensure the label fills the space to the left
        JLabel nameLabel = new JLabel("Select a shape:");
        panel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Ensure the combobox fills the space to the left
        panel.add(chartCombobox, gbc);


        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(browser.getComponent(), BorderLayout.CENTER);
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topPanel.add(panel);
        topPanel.setBorder(JBUI.Borders.emptyBottom(5));
        contentPane.add(topPanel, BorderLayout.NORTH);
        contentPane.add(errorLabel, BorderLayout.SOUTH);

        return contentPane;
    }

    @NotNull
    private static String getChoice(String selected) {
        String shapeChoice = "";

        if (OPT_LINE.equals(selected) || OPT_MULTI_LINE.equals(selected)) {
            shapeChoice = "polygon: false, polyline: true, rectangle: false, circle: false, marker: false, circlemarker: false";
        } else if (OPT_CIRCLE.equals(selected)) {
            shapeChoice = "polygon: false, polyline: false, rectangle: false, circle: true, marker: false, circlemarker: false";
        } else if (OPT_RECTANGLE.equals(selected)) {
            shapeChoice = "polygon: false, polyline: false, rectangle: true, circle: false, marker: false, circlemarker: false";
        } else if (OPT_POLYGON.equals(selected) || OPT_MULTI_POLYGON.equals(selected)) {
            shapeChoice = "polygon: true, polyline: false, rectangle: false, circle: false, marker: false, circlemarker: false";
        } else if (OPT_POINT.equals(selected) || OPT_MULTI_POINT.equals(selected)) {
            shapeChoice = "polygon: false, polyline: false, rectangle: false, circle: false, marker: false, circlemarker: true";
        }
        return shapeChoice;
    }


    @Override
    protected void doOKAction() {

        if (chartCombobox.getSelectedItem() == null) {
            errorLabel.setText("Please select a shape");
            errorLabel.revalidate();
            return;
        } else {

            if (messageRouterHandler.getShapes().isEmpty()) {
                errorLabel.setText("Please add the shape in the map");
                errorLabel.revalidate();
                return;
            } else {
                replaceContent();
            }
        }

        super.doOKAction();
    }

    private void replaceContent() {
        String content = "";
        if (GeoQueryConstants.CIRCLE.equals(block.getType()) || (block.getType() == null && OPT_CIRCLE.equals(chartCombobox.getSelectedItem()))) {
            content = "{\n " +
                    "\"type\": \"Circle\",\n" +
                    "\"coordinates\": [" + messageRouterHandler.getShapes().get(0).getLon() + ", " + messageRouterHandler.getShapes().get(0).getLat() + "],\n" +
                    "\"radius\": \"" + Math.round(Double.parseDouble(messageRouterHandler.getShapes().get(0).getRadius())) + "m\"" +
                    " \n}";

        } else if (GeoQueryConstants.LINE_STRING.equals(block.getType()) || (block.getType() == null && OPT_LINE.equals(chartCombobox.getSelectedItem()))) {
            content = "{\n " +
                    "\"type\": \"LineString\",\n" +
                    "\"coordinates\": " + messageRouterHandler.getShapes().get(0).getValue() +
                    " \n}";
        } else if (GeoQueryConstants.RADIUS.equals(block.getType())) {
            content = "{\n" +
                    "\"location\": {\n" +
                    "\"lon\": " + messageRouterHandler.getShapes().get(0).getLon() + ",\n" +
                    "\"lat\": " + messageRouterHandler.getShapes().get(0).getLat() + "\n" +
                    "},\n" +
                    "\"distance\": \"" + Math.round(Double.parseDouble(messageRouterHandler.getShapes().get(0).getRadius())) + "m\",\n" +
                    "\"field\":" +
                    " \n}";
        } else if (GeoQueryConstants.RECTANGLE.equals(block.getType())) {
            content = "{\n" +
                    "\"top_left\": " + messageRouterHandler.getShapes().get(0).getTopLeft() + ",\n" +
                    "\"bottom_right\": " + messageRouterHandler.getShapes().get(0).getBottomRight() + ",\n" +
                    "\"field\":" +
                    " \n}";
        } else if (GeoQueryConstants.POLYGON_POINTS.equals(block.getType())) {
            content = "{\n" +
                    "\"polygon_points\": " + convertCoordinates(messageRouterHandler.getShapes().get(0).getValue()) + ",\n" +
                    "\"field\":" +
                    " \n}";
        } else if (GeoQueryConstants.POINT.equals(block.getType()) || (block.getType() == null && OPT_POINT.equals(chartCombobox.getSelectedItem()))) {
            content = "{\n" +
                    "\"type\": \"Point\", \n" +
                    "\"coordinates\": [" + messageRouterHandler.getShapes().get(0).getLon() + "," + messageRouterHandler.getShapes().get(0).getLat() + "]" +
                    " \n}";
        } else if (GeoQueryConstants.POLYGON.equals(block.getType())
                || (block.getType() == null && OPT_POLYGON.equals(chartCombobox.getSelectedItem()))) {
            content = "{\n" +
                    "\"type\": \"Polygon\", \n" +
                    "\"coordinates\": " + messageRouterHandler.getShapes().get(0).getValue() +
                    " \n}";
        } else if (GeoQueryConstants.ENVELOPE.equals(block.getType())
                || (block.getType() == null && OPT_RECTANGLE.equals(chartCombobox.getSelectedItem()))) {
            content = "{\n" +
                    "\"type\": \"Envelope\", \n" +
                    "\"coordinates\": [" + messageRouterHandler.getShapes().get(0).getTopLeft() + ", " + messageRouterHandler.getShapes().get(0).getBottomRight() + "]" +
                    " \n}";
        } else if (GeoQueryConstants.MULTI_POINT.equals(block.getType()) || (block.getType() == null && OPT_MULTI_POINT.equals(chartCombobox.getSelectedItem()))) {
            content = "{\n" +
                    "\"type\": \"MultiPoint\", \n" +
                    "\"coordinates\": [" + messageRouterHandler.getShapes().stream()
                    .map(e -> "[" + e.getLon() + "," + e.getLat() + "]").collect(Collectors.joining(", ")) + "]" +
                    " \n}";
        } else if (GeoQueryConstants.MULTI_LINE_STRING.equals(block.getType()) || (block.getType() == null && OPT_MULTI_LINE.equals(chartCombobox.getSelectedItem()))) {
            content = "{\n" +
                    "\"type\": \"MultiLineString\", \n" +
                    "\"coordinates\": [" + messageRouterHandler.getShapes().stream()
                    .map(Shape::getValue).collect(Collectors.joining(", ")) + "]" +
                    " \n}";
        } else if (GeoQueryConstants.MULTI_POLYGON.equals(block.getType()) || (block.getType() == null && OPT_MULTI_POLYGON.equals(chartCombobox.getSelectedItem()))) {
            content = "{\n" +
                    "\"type\": \"MultiPolygon\", \n" +
                    "\"coordinates\": [" + messageRouterHandler.getShapes().stream()
                    .map(Shape::getValue).collect(Collectors.joining(", ")) + "]" +
                    " \n}";
        }

        final String replacement = content;

        CommandProcessor.getInstance().executeCommand(editor.getProject(), () -> {
            ApplicationManager.getApplication().runWriteAction(() -> {
                Document document = editor.getDocument();
                if (block.getType() != null) {
                    document.replaceString(block.getStart(), block.getEnd() + 1, replacement);
                } else {
                    document.insertString(block.getOffset(), replacement);
                }

                //NOTE: Format the document after the text change, somehow when we run it twice, it works
                DocumentFormatter.formatFile(editor.getProject(), editor.getVirtualFile());
                DocumentFormatter.formatFile(editor.getProject(), editor.getVirtualFile());
            });
        }, "Update Document Content", null);
    }


    @Data
    private class Shape {
        private String lat;
        private String lon;

        private String radius;

        private String value;

        private String topLeft;
        private String bottomRight;
    }

    @Getter
    public class MessageRouterHandler extends CefMessageRouterHandlerAdapter {

        private List<Shape> shapes = new ArrayList<>();

        @Override
        public boolean onQuery(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {

            try {
                JSONObject jsonQuery = new JSONObject(request);
                String action = jsonQuery.getString("action");
                JSONObject data = jsonQuery.getJSONObject("data");

                switch (action) {
                    case "addCircle":
                        Shape shape = new Shape();
                        shape.setLon(String.valueOf(data.getDouble("lon")));
                        shape.setLat(String.valueOf(data.getDouble("lat")));
                        shape.setRadius(String.valueOf(data.getDouble("radius")));
                        shapes.add(shape);
                        break;
                    case "addPolygon":
                        Shape polygon = new Shape();
                        double[][] latlng = parseLatLngArray(data.getJSONArray("latlng"));
                        polygon.setValue(convertToString(latlng));
                        shapes.add(polygon);
                        break;
                    case "addLine":
                        Shape line = new Shape();
                        double[][] lineValues = parseLatLngArray(data.getJSONArray("latlng"));
                        line.setValue(convertToString(lineValues));
                        shapes.add(line);
                        break;
                    case "addRectangle":
                        Shape rectangle = new Shape();
                        double[][] bounds = parseLatLngArray(data.getJSONArray("bounds"));
                        rectangle.setTopLeft(convertToString(bounds[0]));
                        rectangle.setBottomRight(convertToString(bounds[1]));
                        shapes.add(rectangle);
                        break;
                    case "addPoint":
                        Shape point = new Shape();
                        point.setLon(String.valueOf(data.getDouble("lon")));
                        point.setLat(String.valueOf(data.getDouble("lat")));
                        shapes.add(point);
                        break;
                    case "removeShape":
                        shapes = new ArrayList<>();
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

    public static String convertToString(double[][] array) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < array.length; i++) {
            sb.append("[");
            for (int j = 0; j < array[i].length; j++) {
                sb.append(array[i][j]);
                if (j < array[i].length - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
            if (i < array.length - 1) {
                sb.append(", ");
            }
        }

        sb.append("]");
        return sb.toString();
    }

    public static String convertToString(double[] array) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < array.length - 1) {
                sb.append(", ");
            }
        }

        sb.append("]");
        return sb.toString();
    }

    public static String convertCoordinates(String input) {
        String trimmedInput = input.substring(1, input.length() - 1);
        String[] coordinates = trimmedInput.split("\\], \\[");
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < coordinates.length; i++) {
            sb.append("\"");
            sb.append(coordinates[i].replace("[", "").replace("]", "").replace(", ", ","));
            sb.append("\"");
            if (i < coordinates.length - 1) {
                sb.append(",");
            }
        }
        sb.append("]");

        return sb.toString();
    }
}