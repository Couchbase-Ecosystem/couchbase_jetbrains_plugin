package com.couchbase.intellij.tree.node;

import com.intellij.icons.AllIcons;
import lombok.Getter;
import lombok.Setter;

public class SearchNodeDescriptor extends CounterNodeDescriptor {

    @Setter
    @Getter
    private String bucket;

    @Getter
    private String scope;

    public SearchNodeDescriptor(String scope, String bucket) {
        super("Search", AllIcons.Actions.ShortcutFilter);
        this.bucket = bucket;
        this.scope = scope;
    }
}
