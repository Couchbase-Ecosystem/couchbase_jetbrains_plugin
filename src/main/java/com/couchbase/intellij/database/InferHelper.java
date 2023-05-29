package com.couchbase.intellij.database;

import com.couchbase.client.core.api.query.CoreQueryResult;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryResult;
import com.couchbase.intellij.tree.node.SchemaDataNodeDescriptor;
import com.couchbase.intellij.tree.node.SchemaFlavorNodeDescriptor;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InferHelper {

    public static JsonObject inferSchema(String collectionName, String scopeName, String bucketName) throws IOException, InterruptedException {
        try {
            String query = "INFER `" + bucketName + "`.`" + scopeName + "`.`" + collectionName + "` WITH {\"sample_size\": 2000}";
            QueryResult result = ActiveCluster.getInstance().get().query(query);

            try {
                result.rowsAsObject();
                throw new IllegalStateException("Infer didn't throw an Exception, this indicates that a bug in the SDK has been fixed");
            } catch (Exception ex) {
                if (ex.getMessage().startsWith("Deserialization of content into target class com.couchbase.client.java.json.JsonObject failed")) {
                    Field field = QueryResult.class.getDeclaredField("internal");
                    field.setAccessible(true);
                    CoreQueryResult internal = (CoreQueryResult) field.get(result);
                    List<JsonObject> objList = new ArrayList<>();
                    internal.rows().forEach(e -> {
                                JsonObject obj = JsonObject.create();
                                obj.put("content", JsonArray.fromJson(e.data()));
                                objList.add(obj);
                            }
                    );
                    return objList.get(0);
                } else {
                    throw ex;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not infer the schema of the collection " + e);
            return null;
        }
    }

    public static void extractArray(DefaultMutableTreeNode parentNode, JsonArray array) {
        for (int i = 0; i < array.size(); i++) {
            JsonObject inferSchemaRow = array.getObject(i);

            String tooltip = "#docs: " + inferSchemaRow.getNumber("#docs") + ", pattern: " + inferSchemaRow.getString("Flavor");
            SchemaFlavorNodeDescriptor sf = new SchemaFlavorNodeDescriptor("Pattern Found #" + (i + 1), tooltip);
            DefaultMutableTreeNode flavorNode = new DefaultMutableTreeNode(sf);
            parentNode.add(flavorNode);
            JsonObject properties = inferSchemaRow.getObject("properties");

            if (properties != null) {
                extractTypes(flavorNode, inferSchemaRow.getObject("properties"));

            } else {
                JsonArray samples = inferSchemaRow.getArray("samples");
                if (samples != null) {
                    String additionalTooltip = samples.toList().stream().map(e -> e.toString()).collect(Collectors.joining(","));
                    sf.setTooltip(sf.getTooltip() + ", samples: " + additionalTooltip);
                } else {
                    System.err.println("Infer reached an unexpected state");
                }
            }
        }
    }

    public static void extractTypes(DefaultMutableTreeNode parentNode, JsonObject properties) {
        for (String key : properties.getNames()) {
            JsonObject property = properties.getObject(key);
            String type = property.get("type").toString();
            //if it is an Object
            if (type.equals("object")) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new SchemaDataNodeDescriptor(key));
                extractTypes(childNode, property.getObject("properties"));
                parentNode.add(childNode);

            } else if (type.equals("array")) {
                try {
                    JsonObject items = property.getObject("items");
                    String itemTypeString = (String) items.get("type");
                    if (itemTypeString != null) {
                        DefaultMutableTreeNode childNode;
                        if (itemTypeString.equals("object")) {
                            //result.put(key, "array of " + extractTypes(items.getObject("properties")));
                            childNode = new DefaultMutableTreeNode(new SchemaDataNodeDescriptor(key, "array of objects", null));
                            extractTypes(childNode, items.getObject("properties"));
                        } else {
                            childNode = new DefaultMutableTreeNode(new SchemaDataNodeDescriptor(key, "array of " + itemTypeString + "s", null));
                        }
                        parentNode.add(childNode);
                    } else {
                        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new SchemaDataNodeDescriptor(key, type, null));
                        parentNode.add(childNode);
                    }
                } catch (ClassCastException cce) {
                    JsonArray array = property.getArray("items");
                    extractArray(parentNode, array);
                }
            } else {
                addLeaf(parentNode, key, property);
            }
        }
    }

    private static void addLeaf(DefaultMutableTreeNode parentNode, String key, JsonObject property) {
        String type = property.get("type").toString();
        boolean containsNull = false;
        if (type.contains("[")) {
            type = type.replace("[", "").replace("]", "").replace("\"", "").replace(",", " | ");

            if (type.contains("null")) {
                containsNull = true;
            }
        }

        String samples = null;
        JsonArray samplesArray = property.getArray("samples");
        if (samplesArray != null) {
            if (containsNull) {
                if (samplesArray.size() > 1) {
                    //ignoring the first array that will only contains null
                    samplesArray = samplesArray.getArray(1);
                } else {
                    samplesArray = samplesArray.getArray(0);
                }
            }
            samples = samplesArray.toList().stream().map(e -> e == null ? "null" : e.toString()).collect(Collectors.joining(" , "));

        }

        System.out.println(key + " - " + type + ", samples = " + samples);

        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new SchemaDataNodeDescriptor(key, type, samples));
        parentNode.add(childNode);
    }
}
