package com.couchbase.intellij.workbench.chart;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.ui.ComboBox;

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

public class ChartUtil {


    public static String loadResourceAsString(String resourcePath) {
        try (InputStream inputStream = MapCbChart.class.getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException | NullPointerException e) {
            Log.error("An error occurred while loading the html for the map");
            return null;
        }
    }


    public static JsonArray getAttributeValues(List<JsonObject> jsonObjects, String path) {
        JsonArray values = JsonArray.create();
        String[] pathParts = path.split("\\.");

        for (JsonObject jsonObject : jsonObjects) {
            Object currentElement = jsonObject;
            boolean pathCompleted = true;

            for (String part : pathParts) {
                if (!(currentElement instanceof JsonObject) || !((JsonObject) currentElement).containsKey(part)) {
                    pathCompleted = false;
                    break;
                }
                currentElement = ((JsonObject) currentElement).get(part);
            }

            if (pathCompleted) {
                values.add(currentElement);
            } else {
                values.addNull();
            }
        }
        return values;
    }


    public static List<String> filterOutAttributes(Map<String, String> fields, List<String> filterOutList) {
        List<String> valOptions = new ArrayList<>();
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            if (!filterOutList.contains(entry.getValue())) {
                valOptions.add(entry.getKey());
            }
        }
        return valOptions;
    }


    public static void addItemsWithoutChangeListener(ComboBox comboBox, List<String> values, ItemListener itemListener) {
        comboBox.removeItemListener(itemListener);
        comboBox.removeAllItems();
        values.forEach(e -> comboBox.addItem(e));
        comboBox.addItemListener(itemListener);
        comboBox.setSelectedItem(null);
    }

}
