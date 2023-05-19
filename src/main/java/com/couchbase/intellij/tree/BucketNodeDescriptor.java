package com.couchbase.intellij.tree;

import com.intellij.openapi.util.IconLoader;

public class BucketNodeDescriptor extends NodeDescriptor {

    public BucketNodeDescriptor(String name) {
        super(name, IconLoader.findIcon("./assets/icons/bucket.svg", ConnectionNodeDescriptor.class,
                false, true));
    }
}
