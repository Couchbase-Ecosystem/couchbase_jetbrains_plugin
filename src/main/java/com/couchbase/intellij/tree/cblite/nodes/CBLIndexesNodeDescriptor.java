package com.couchbase.intellij.tree.cblite.nodes;

import com.couchbase.intellij.tree.node.NodeDescriptor;
import com.intellij.openapi.util.IconLoader;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CBLIndexesNodeDescriptor extends NodeDescriptor {

    private final String scope;
    private final String collection;

    public CBLIndexesNodeDescriptor(String scope, String collection) {
        super("Indexes", IconLoader.getIcon("/assets/icons/indexes.svg", CBLIndexesNodeDescriptor.class));
        this.scope = scope;
        this.collection = collection;
    }

}
