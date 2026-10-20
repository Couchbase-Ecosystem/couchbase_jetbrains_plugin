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

    /** Search selections for the current expansion; kept during paging and favorite changes. */
    private final List<String> searchAdded = new ArrayList<>();

    /** How many collections of the alphabetical sequence are currently loaded. */
    @Setter
    @Getter
    private int pagedLoaded;
    @Getter private int total = -1;
    @Getter private int shown;

    public void setListingCounts(int shown, int total) {
        this.shown = shown;
        this.total = total;
        setCounter(shown + " of " + total + " shown");
    }

    @Getter private long loadGeneration;

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

    /** Invalidates any background load and forgets temporary selections when this scope is closed. */
    public void resetForExpansion() {
        searchAdded.clear();
        pagedLoaded = 0;
        total = -1;
        shown = 0;
        setCounter("");
        loadGeneration++;
    }

    public long beginLoad() {
        return ++loadGeneration;
    }

    public boolean isCurrentLoad(long generation) {
        return loadGeneration == generation;
    }
}
