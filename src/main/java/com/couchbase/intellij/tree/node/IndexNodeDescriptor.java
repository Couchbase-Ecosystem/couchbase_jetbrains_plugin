package com.couchbase.intellij.tree.node;

import com.intellij.json.JsonFileType;
import com.intellij.openapi.vfs.VirtualFile;

public class IndexNodeDescriptor extends NodeDescriptor {

    private final String bucket;
    private final String scope;
    private final String collection;
    private VirtualFile virtualFile;

    public IndexNodeDescriptor(String bucket, String scope, String collection, String name, VirtualFile virtualFile) {
        super(name, JsonFileType.INSTANCE.getIcon());
        this.virtualFile = virtualFile;
        this.bucket = bucket;
        this.scope = scope;
        this.collection = collection;
    }

    public VirtualFile getVirtualFile() {
        return virtualFile;
    }

    public void setVirtualFile(VirtualFile virtualFile) {
        this.virtualFile = virtualFile;
    }

    public String getBucket() {
        return bucket;
    }

    public String getScope() {
        return scope;
    }

    public String getCollection() {
        return collection;
    }
}
