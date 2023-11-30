package com.couchbase.intellij.tree.cblite.nodes;

import com.couchbase.intellij.tree.node.FileNodeDescriptor;
import com.couchbase.intellij.tree.node.NodeDescriptor;
import com.intellij.icons.AllIcons;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.vfs.VirtualFile;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CBLFileNodeDescriptor extends NodeDescriptor {

    private VirtualFile virtualFile;
    private String scope;
    private String collection;
    private String id;

    public CBLFileNodeDescriptor(String name, String scope, String collection, String id, VirtualFile virtualFile) {
        super(name, JsonFileType.INSTANCE.getIcon());
        this.virtualFile = virtualFile;
        this.scope = scope;
        this.collection = collection;
        this.id = id;
    }
}
