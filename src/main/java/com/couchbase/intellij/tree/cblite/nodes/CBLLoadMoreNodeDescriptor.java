package com.couchbase.intellij.tree.cblite.nodes;

import com.couchbase.intellij.tree.node.NodeDescriptor;
import com.intellij.openapi.util.IconLoader;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CBLLoadMoreNodeDescriptor extends NodeDescriptor {

    private String scope;
    private String collection;
    private int newOffset;

    public CBLLoadMoreNodeDescriptor( String scope, String collection, int newOffset) {
        super("Load More", IconLoader.getIcon("/assets/icons/double-chevron-down.svg", CBLLoadMoreNodeDescriptor.class));
        this.scope = scope;
        this.collection = collection;
        this.newOffset = newOffset;
    }
}
