package utils;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;

import java.util.Map;
import java.util.TreeMap;

public class JsonObjectUtil {

    public static Map<String, String> generatePaths(JsonObject jsonObject, String prefix) {
        return generatePaths(jsonObject, prefix, false);
    }

    public static Map<String, String> generatePaths(JsonObject jsonObject, String prefix, boolean skipArrays) {
        Map<String, String> paths = new TreeMap<>();
        jsonObject.getNames().forEach(key -> {
            Object value = jsonObject.get(key);
            String currentPath = prefix.isEmpty() ? key : prefix + "." + key;
            if (value instanceof JsonObject) {
                paths.putAll(generatePaths((JsonObject) value, currentPath));
            } else if (value instanceof JsonArray) {
                if (!skipArrays) {
                    JsonArray array = (JsonArray) value;
                    if (!array.isEmpty()) {
                        Object firstElement = array.get(0);
                        if (firstElement instanceof JsonObject) {
                            paths.putAll(generatePaths((JsonObject) firstElement, currentPath + "[*]"));
                        } else {
                            String elementType = firstElement.getClass().getSimpleName() + "[]";
                            paths.put(currentPath + "[*]", elementType);
                        }
                    }
                }
            } else {
                paths.put(currentPath, value == null ? "Unknown" : value.getClass().getSimpleName());
            }
        });
        return paths;
    }
}
