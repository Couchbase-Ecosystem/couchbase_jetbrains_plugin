package com.couchbase.intellij.database.entity;

import com.couchbase.client.java.Cluster;
import com.couchbase.intellij.persistence.SavedCluster;
import com.google.common.collect.Streams;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public interface CouchbaseClusterEntity {
    String getName();

    CouchbaseClusterEntity getParent();

    void updateSchema();

    Cluster getCluster();

    Set<? extends CouchbaseClusterEntity> getChildren();

    default CouchbaseClusterEntity getRoot() {
        CouchbaseClusterEntity parent = getParent();
        return parent == null ? this : parent;
    }

    default String path() {
        return getParent() == null ? getName() :
                String.format("%s.%s", getParent().getName(), getName());
    }

    default List<String> pathElements() {
        List<String> path = getParent().pathElements();
        path.add(getName());
        return path;
    }

    default SavedCluster getSavedCluster() {
        return getParent().getSavedCluster();
    }

    default Stream<? extends CouchbaseClusterEntity> getChild(String name) {
        if (getChildren() == null) {
            return Stream.empty();
        }
        Stream<? extends CouchbaseClusterEntity> childrenStream = getChildren().stream();
        if (this.getName() != null) {
            childrenStream = Streams.concat(childrenStream, Stream.of(this));
        }
        return childrenStream
                .flatMap(c -> c.getName() == null ? c.getChildren().stream() : Stream.of(c))
                .filter(e -> name.equalsIgnoreCase(e.getName()));
    }

    default Stream<? extends CouchbaseClusterEntity> navigate(List<String> path) {
        if (path.isEmpty()) {
            return Stream.of(this);
        }

        return getChild(path.get(0))
                .flatMap(c -> {
                    if (path.size() == 1) {
                        return Stream.of(c);
                    }
                    List<String> subPath = new ArrayList<>(path);
                    subPath.remove(0);
                    return c.navigate(subPath);
                });
    }
}
