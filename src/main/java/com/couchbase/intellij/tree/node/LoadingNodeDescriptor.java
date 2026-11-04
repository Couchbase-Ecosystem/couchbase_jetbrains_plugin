package com.couchbase.intellij.tree.node;

public class LoadingNodeDescriptor extends NodeDescriptor {

    public LoadingNodeDescriptor() {
        this("Loading…");
    }

    public LoadingNodeDescriptor(String text) {
        super(text, null);
    }
}
