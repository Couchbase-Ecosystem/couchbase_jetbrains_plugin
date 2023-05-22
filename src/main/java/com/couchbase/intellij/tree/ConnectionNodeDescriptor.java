package com.couchbase.intellij.tree;

import com.intellij.openapi.util.IconLoader;

public class ConnectionNodeDescriptor extends NodeDescriptor {

    public ConnectionNodeDescriptor(String name, boolean isActive) {
        super(name, isActive ? IconLoader.findIcon("./assets/icons/couchbase-active.svg", ConnectionNodeDescriptor.class,
                false, true) : IconLoader.findIcon("./assets/icons/couchbase.svg",
                CouchbaseWindowContent.class, false, true));

    }

}
