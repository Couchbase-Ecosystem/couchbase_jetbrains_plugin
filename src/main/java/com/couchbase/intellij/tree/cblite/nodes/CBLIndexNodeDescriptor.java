package com.couchbase.intellij.tree.cblite.nodes;

import com.couchbase.intellij.tree.node.NodeDescriptor;
import com.intellij.openapi.util.IconLoader;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CBLIndexNodeDescriptor extends NodeDescriptor {

    private final String scope;
    private final String collection;

    public CBLIndexNodeDescriptor(String name, String scope, String collection) {
        super(name, IconLoader.getIcon("/assets/icons/indexes.svg", CBLIndexNodeDescriptor.class));
        this.scope = scope;
        this.collection = collection;
    }

}
