package com.couchbase.intellij.database.entity;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;

import java.util.*;
import java.util.stream.Collectors;

public class CouchbaseField implements CouchbaseClusterEntity {
    private CouchbaseDocumentFlavor documentFlavor;
    private CouchbaseField parent;
    private String name;

    private JsonObject properties;
    private Set<CouchbaseField> children = new HashSet<>();

    private List<String> types = new ArrayList<>();

    public CouchbaseField(CouchbaseDocumentFlavor documentFlavor, CouchbaseField parent, String name, JsonObject properties) {
        this.documentFlavor = documentFlavor;
        this.parent = parent;
        this.name = name;
        this.properties = properties;
        this.children = flattenArray(properties);
        Object type = properties.get("type");
        if (type instanceof JsonArray) {
            types = ((JsonArray) type).toList().stream()
                    .filter(Objects::nonNull)
                    .filter(t -> t instanceof String && !"null".equals(t))
                    .map(t -> (String) t)
                    .collect(Collectors.toList());
        } else if (type instanceof String) {
            this.types = List.of((String) type);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CouchbaseClusterEntity getParent() {
        return parent == null ? documentFlavor : parent;
    }

    @Override
    public void updateSchema() {
        // noop
    }

    @Override
    public Cluster getCluster() {
        return documentFlavor.getCluster();
    }

    @Override
    public Set<CouchbaseField> getChildren() {
        return children;
    }
    protected Set<CouchbaseField> flattenArray(JsonObject items) {
        Object typeObj = items.get("type");
        List types;
        if (typeObj instanceof JsonArray) {
            types = ((JsonArray) typeObj).toList();
        } else if (typeObj instanceof Collection) {
            types = new ArrayList((Collection) typeObj);
        } else {
            types = Arrays.asList((String) typeObj);
        }

        if (types.contains("object")) {
            return CouchbaseField.fromObject(documentFlavor, this, items);
        } else if (types.contains("array")) {
            Object itemsObj = items.get("items");
            if (itemsObj instanceof JsonArray) {
                return ((JsonArray) itemsObj).toList()
                        .stream()
                        .filter(i -> i instanceof HashMap)
                        .map(i -> (HashMap) i)
                        .sorted(Comparator.comparingInt(i -> {
                            if (i.containsKey("#docs")) {
                                return -(int) i.get("#docs");
                            } else {
                                return Integer.MIN_VALUE;
                            }
                        }))
                        .flatMap(i -> flattenArray(JsonObject.from(i)).stream())
                        .collect(Collectors.toSet());
            } else {
                return flattenArray((JsonObject) itemsObj);
            }
        }

        return Collections.EMPTY_SET;
    }

    public static Set<CouchbaseField> fromObject(CouchbaseDocumentFlavor flavor, CouchbaseField parent, JsonObject object) {
        JsonObject fields = object.getObject("properties");
        return fields == null ? Collections.EMPTY_SET : fields.getNames().stream()
                .map(field -> new CouchbaseField(flavor, parent, field, fields.getObject(field)))
                .collect(Collectors.toSet());
    }

    public void addZeroValue(JsonObject target) {
        if (types.size() == 0) {
            target.put(getName(), "");
        }
        String firstType = types.get(0);
        if ("string".equals(firstType)) {
            target.put(getName(), "");
        } else if ("number".equals(firstType)) {
            target.put(getName(), 0);
        } else if ("object".equals(firstType)) {
            JsonObject value = JsonObject.create();
            getChildren().forEach(child -> child.addZeroValue(value));
            target.put(getName(), value);
        } else if ("boolean".equals(firstType)) {
            target.put(getName(), false);
        } else if ("array".equals(firstType)) {
            JsonArray value = JsonArray.create();
            JsonObject sample = JsonObject.create();
            getChildren().forEach(child -> child.addZeroValue(sample));
            value.add(sample);
            target.put(getName(), value);
        } else {
            target.put(getName(), (String) null);
        }
    }
}
