package com.couchbase.intellij.tree.cblite.nodes;

import com.couchbase.intellij.tree.node.NodeDescriptor;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.vfs.VirtualFile;
import lombok.Getter;
import lombok.Setter;


public class CBLEmptyNodeDescriptor extends NodeDescriptor {

    public CBLEmptyNodeDescriptor(String name) {
        super(name,null);
    }
}
