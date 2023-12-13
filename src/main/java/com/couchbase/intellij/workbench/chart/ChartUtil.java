package com.couchbase.intellij.workbench.chart;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.workbench.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
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

}
