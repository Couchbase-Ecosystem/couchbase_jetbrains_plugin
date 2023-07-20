package com.couchbase.intellij.database.entity;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;

import javax.naming.PartialResultException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CouchbaseField implements CouchbaseClusterEntity {
    private CouchbaseDocumentFlavor documentFlavor;
    private CouchbaseField parent;
    private String name;

    private JsonObject properties;
    private Set<CouchbaseField> children = new HashSet<>();

    public CouchbaseField(CouchbaseDocumentFlavor documentFlavor, CouchbaseField parent, String name, JsonObject properties) {
        this.documentFlavor = documentFlavor;
        this.parent = parent;
        this.name = name;
        this.properties = properties;
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
    public Cluster getCluster() {
        return documentFlavor.getCluster();
    }

    @Override
    public Set<CouchbaseField> getChildren() {
        return flattenArray(properties);
    }
    protected Set<CouchbaseField> flattenArray(JsonObject items) {
        String type = items.getString("type");
        if (type.equals("object")) {
            return CouchbaseField.fromObject(documentFlavor, this, items.getObject("properties"));
        } else if (type.equals("array")) {
            return flattenArray(items.getObject("items"));
        }

        return Collections.EMPTY_SET;
    }

    public static Set<CouchbaseField> fromObject(CouchbaseDocumentFlavor flavor, CouchbaseField parent, JsonObject object) {
        JsonObject fields = object.getObject("properties");
        return fields.getNames().stream()
                .map(field -> new CouchbaseField(flavor, parent, field, fields.getObject(field)))
                .collect(Collectors.toSet());
    }
}
