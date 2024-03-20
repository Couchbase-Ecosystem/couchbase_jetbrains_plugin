package com.couchbase.intellij.workbench.chart;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.components.JBTextField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapBridge {

    private Map<String, Circle> circles;
    private Map<String, Polygon> polygons;

    private JBTextField filterField;

    public MapBridge(JBTextField filterField) {
        this.circles = new HashMap<>();
        this.polygons = new HashMap<>();
        this.filterField = filterField;
    }

    public void addCircle(String id, double lat, double lon, double radius) {
        this.circles.put(id, new Circle(radius, new Double[]{lat, lon}));
        updateFilter();
    }

    public void removeCircle(String id) {
        this.circles.remove(id);
        updateFilter();
    }


    public void addPolygon(String id, double[][] latlng) {
        this.polygons.put(id, new Polygon(latlng));
        updateFilter();
    }

    public void removePolygon(String id) {
        this.polygons.remove(id);
        updateFilter();
    }

    public List<Circle> getCircles() {
        return new ArrayList<>(circles.values());
    }

    public List<Polygon> getPolygons() {
        return new ArrayList<>(polygons.values());
    }

    public void updateFilter() {

        ApplicationManager.getApplication().invokeLater(() -> {
            List<String> filters = new ArrayList<>();

            for (Map.Entry<String, Circle> entry : circles.entrySet()) {

                filters.add("{ \"field\": \"<INDEXED_GEO_FIELD>\", \"location\": " +
                        "{ \"lat\": " + entry.getValue().getCenter()[0] + ", \"lon\": " + entry.getValue().getCenter()[1] + " }" +
                        ", \"distance\": \"" + (entry.getValue().getRadius() / 1000) + "km\" }");
            }

            for (Map.Entry<String, Polygon> entry : polygons.entrySet()) {

                filters.add("{ \"field\": \"<INDEXED_GEO_FIELD>\", \"polygon_points\": [" +
                        String.join(",", toFTSCoordinates(entry.getValue().getLatlngs())) +
                        "] }");
            }

            if (filters.isEmpty()) {
                filterField.setText("");
            } else if (filters.size() == 1) {
                filterField.setText("SEARCH( <YOUR_COLLECTION>, { \"query\": " + filters.get(0) + " })");
            } else {
                filterField.setText("SEARCH( <YOUR_COLLECTION>, { \"query\": {  \"disjuncts\": [" + String.join(",", filters) + "] } })");
            }
        });

    }

    public static List<String> toFTSCoordinates(double[][] array) {
        List<String> list = new ArrayList<>();

        for (double[] pair : array) {
            if (pair.length == 2) {
                String formattedPair = String.format("\"%f,%f\"", pair[0], pair[1]);
                list.add(formattedPair);
            }
        }

        return list;
    }
}
