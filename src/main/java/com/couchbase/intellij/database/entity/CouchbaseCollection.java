package com.couchbase.intellij.database.entity;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.intellij.database.InferHelper;
import com.couchbase.intellij.utils.Subscribable;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CouchbaseCollection implements CouchbaseClusterEntity {
    private CollectionSpec spec;
    private CouchbaseScope parent;
    private Subscribable<Set<CouchbaseDocumentFlavor>> children = new Subscribable<>();

    private AtomicBoolean updating = new AtomicBoolean(false);

    public CouchbaseCollection(CouchbaseScope parent, CollectionSpec spec) {
        this.parent = parent;
        this.spec = spec;
    }

    @Override
    public String getName() {
        return spec.name();
    }

    @Override
    public CouchbaseClusterEntity getParent() {
        return parent;
    }

    @Override
    public void updateSchema() {
        if (updating.get()) {
            return;
        }

        String path = this.path();
        if (getSavedCluster().isInferCacheValid(path) && children != null) {
            loadSchema(getSavedCluster().getInferCacheValue(path));
        } else {
            updating.set(true);

            ProgressManager.getInstance().run(new Task.Backgroundable(null, "Running INFER for collection " + path, true) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    try {
                        JsonObject schema = InferHelper.inferSchema(getName(), getParent().getName(), getParent().getParent().getName());
                        getSavedCluster().setInferCacheValue(path, schema);
                        Log.debug("Stored infer for " + path);
                        if (schema != null) {
                            loadSchema(schema);
                        } else {
                            Log.debug("Could not infer the schema for " + getName());
                        }
                    } finally {
                        updating.set(false);
                    }
                }
            });
        }
    }

    private void loadSchema(JsonObject schema) {
        // children == null because it means that there is a valid model already
        // and it either loaded from a valid infer (in which case there is no sense to update it)
        // or from the cluster, which equates to the previous situation
        final String path = path();
        if (schema != null) {
            JsonArray content = schema.getArray("content");
            children.set(IntStream.range(0, content.size()).boxed()
                    .map(content::getObject)
                    .map(flavor -> new CouchbaseDocumentFlavor(this, flavor))
                    .peek(CouchbaseDocumentFlavor::updateSchema)
                    .collect(Collectors.toSet()));
        } else {
            Log.debug("nada data for schemata " + path);
        }
    }

    @Override
    public Cluster getCluster() {
        return parent.getCluster();
    }

    @Override
    public Set<CouchbaseDocumentFlavor> getChildren() {
        if (!children.isPresent()) {
            updateSchema();
        }
        try {
            return children.get(1000);
        } catch (InterruptedException e) {
            return null;
        }
    }

    public JsonObject generateDocument() {
        return getChildren().stream().sorted(Comparator.comparingLong(flavor -> -flavor.getSampleSize()))
                .map(CouchbaseDocumentFlavor::generateDocument)
                .reduce((l, r) -> {
                    r.toMap().keySet().forEach(key -> {
                        if (!l.containsKey(key)) {
                            l.put(key, r.get(key));
                        }
                    });
                    return l;
                })
                .orElse(null);
    }

    public JsonArray toJson() {
        JsonArray result = JsonArray.create();
        if (children != null) {
            children.get().stream().flatMap(Collection::stream)
                    .map(CouchbaseDocumentFlavor::toJson)
                    .forEach(result::add);
        }
        return result;
    }


    public Set<String> getAllAttributeNames() {
        Set<String> attributeNames = new HashSet<>();

        // Check if children are not null
        this.children.get().stream().flatMap(Collection::stream)
                .forEach(flavor -> collectAttributeNames(flavor, attributeNames));

        return attributeNames;
    }

    /**
     * Recursively collects attribute names from a CouchbaseDocumentFlavor and its children.
     *
     * @param flavor         The document flavor to collect attribute names from.
     * @param attributeNames The set to collect attribute names into.
     */
    private void collectAttributeNames(CouchbaseDocumentFlavor flavor, Set<String> attributeNames) {
        // Assuming each flavor's properties are represented as fields within the document flavor
        Set<CouchbaseField> fields = flavor.getChildren();
        if (fields != null) {
            for (CouchbaseField field : fields) {
                attributeNames.add(field.getName());
                collectAttributeNamesFromFields(field.getChildren(), attributeNames);
            }
        }
    }

    /**
     * Recursively collects attribute names from nested fields.
     *
     * @param fields         The set of nested fields to collect attribute names from.
     * @param attributeNames The set to collect attribute names into.
     */
    private void collectAttributeNamesFromFields(Set<CouchbaseField> fields, Set<String> attributeNames) {
        for (CouchbaseField field : fields) {
            attributeNames.add(field.getName());
            if (field.hasChildren()) {
                collectAttributeNamesFromFields(field.getChildren(), attributeNames);
            }
        }
    }
}
