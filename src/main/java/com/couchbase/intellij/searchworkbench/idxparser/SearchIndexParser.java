package com.couchbase.intellij.searchworkbench.idxparser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public class SearchIndexParser {
    public static Map<String, String> extractPropertiesMap(String index) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(index);
        Map<String, String> propertiesMap = new HashMap<>();
        JsonNode typesNode = rootNode.path("params").path("mapping").path("types");

        if (typesNode.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> typesFields = typesNode.fields();
            while (typesFields.hasNext()) {
                Map.Entry<String, JsonNode> typeField = typesFields.next();
                JsonNode propertiesNode = typeField.getValue().path("properties");
                extractProperties(propertiesNode, "", propertiesMap);
            }
        }
        return propertiesMap;
    }

    private static void extractProperties(JsonNode node, String parentKey, Map<String, String> propertiesMap) {
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String key = field.getKey();
            JsonNode value = field.getValue();

            if (value.has("properties")) {
                String newParentKey = parentKey.isEmpty() ? key : parentKey + "." + key;
                extractProperties(value.path("properties"), newParentKey, propertiesMap);
            } else if (value.has("fields")) {
                for (JsonNode fieldNode : value.path("fields")) {
                    String type = fieldNode.path("type").asText();
                    String fieldName = fieldNode.path("name").asText(key);
                    String finalKey = parentKey.isEmpty() ? fieldName : parentKey + "." + fieldName;
                    propertiesMap.put(finalKey, type);
                }
            } else {
                String type = value.path("type").asText();
                String finalKey = parentKey.isEmpty() ? key : parentKey + "." + key;
                propertiesMap.put(finalKey, type.isEmpty() ? "" : type);
            }
        }
    }

    public static boolean isIndexDynamic(String index) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(index);
        JsonNode indexDynamicNode = rootNode.path("params").path("mapping").path("index_dynamic");
        return indexDynamicNode.asBoolean(false);
    }

    public static boolean isCollectionDynamicallyIndexed(String index, String collection) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(index);
        JsonNode indexDynamicNode = rootNode.path("params").path("mapping").path("types").path(collection).path("dynamic");
        return indexDynamicNode.asBoolean(false);
    }


    public static String getDefaultField(String index) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(index);
        JsonNode indexDynamicNode = rootNode.path("params").path("mapping").path("default_field");
        return indexDynamicNode.textValue();
    }

    public static List<String> listCollections(String index) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(index);
        JsonNode typesNode = rootNode.path("params").path("mapping").path("types");
        Iterator<Map.Entry<String, JsonNode>> fields = typesNode.fields();
        List<String> types = new ArrayList<>();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            types.add(field.getKey());
        }
        return types;
    }

}
