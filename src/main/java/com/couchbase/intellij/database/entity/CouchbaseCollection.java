package com.couchbase.intellij.database.entity;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.intellij.database.InferHelper;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CouchbaseCollection implements CouchbaseClusterEntity {
    private CollectionSpec spec;
    private CouchbaseScope parent;
    private Set<CouchbaseDocumentFlavor> children;

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
            new Task.ConditionalModal(null, "Running INFER for collection " + path, true, PerformInBackgroundOption.ALWAYS_BACKGROUND) {
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
            }.queue();
        }
    }

    private void loadSchema(JsonObject schema) {
        // children == null because it means that there is a valid model already
        // and it either loaded from a valid infer (in which case there is no sense to update it)
        // or from the cluster, which equates to the previous situation
        final String path = path();
        if (schema != null) {
            JsonArray content = schema.getArray("content");
            children = IntStream.range(0, content.size()).boxed()
                    .map(content::getObject)
                    .map(flavor -> new CouchbaseDocumentFlavor(this, flavor))
                    .peek(CouchbaseDocumentFlavor::updateSchema)
                    .collect(Collectors.toSet());
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
        if (children == null) {
            updateSchema();
        }
        return children;
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
            children.stream().map(CouchbaseDocumentFlavor::toJson)
                    .forEach(result::add);
        }
        return result;
    }
}
