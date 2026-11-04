package com.couchbase.intellij.tree.node;

public class NoResultsNodeDescriptor extends NodeDescriptor {

    public NoResultsNodeDescriptor() {
        this("No results");
    }

    public NoResultsNodeDescriptor(String text) {
        super(text, null);
    }
}
