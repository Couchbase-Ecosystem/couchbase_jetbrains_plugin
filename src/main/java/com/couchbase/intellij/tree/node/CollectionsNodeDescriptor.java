package com.couchbase.intellij.tree.node;

import com.intellij.openapi.util.IconLoader;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class CollectionsNodeDescriptor extends CounterNodeDescriptor {

    @Setter
    @Getter
    private String bucket;

    @Getter
    private String scope;

    /** Collections the user pulled into the list through search; kept across refreshes. */
    private final List<String> searchAdded = new ArrayList<>();

    /** How many collections of the alphabetical sequence are currently loaded. */
    @Setter
    @Getter
    private int pagedLoaded;

    public CollectionsNodeDescriptor(String scope, String bucket) {
        super("Collections", IconLoader.getIcon("/assets/icons/collections.svg", CollectionsNodeDescriptor.class));
        this.bucket = bucket;
        this.scope = scope;
    }

    public List<String> getSearchAdded() {
        return searchAdded;
    }

    public void addSearchAdded(String collection) {
        if (!searchAdded.contains(collection)) {
            searchAdded.add(collection);
        }
    }
}
