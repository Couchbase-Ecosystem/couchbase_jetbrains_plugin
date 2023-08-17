package com.couchbase.intellij.tree.overview.range;

import java.util.List;

public class RangeMetric {

    private List<String> nodes;

    private String bucket;
    private String collection;
    private String collection_id;
    private String instance;
    private String name;//"kv_collection_item_count"
    private String scope;
    private String scope_id;


    public List<String> getNodes() {
        return nodes;
    }

    public void setNodes(List<String> nodes) {
        this.nodes = nodes;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getCollection_id() {
        return collection_id;
    }

    public void setCollection_id(String collection_id) {
        this.collection_id = collection_id;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getScope_id() {
        return scope_id;
    }

    public void setScope_id(String scope_id) {
        this.scope_id = scope_id;
    }
}
