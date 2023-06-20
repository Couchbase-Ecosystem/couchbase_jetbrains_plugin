package com.couchbase.intellij.tree.node;

import com.intellij.json.JsonFileType;
import com.intellij.openapi.vfs.VirtualFile;

public class FileNodeDescriptor extends NodeDescriptor {

    private VirtualFile virtualFile;
    private String bucket;
    private String scope;
    private String collection;
    private String id;

    public FileNodeDescriptor(String name, String bucket, String scope, String collection, String id, VirtualFile virtualFile) {
        super(name, JsonFileType.INSTANCE.getIcon());
        this.virtualFile = virtualFile;
        this.bucket = bucket;
        this.scope = scope;
        this.collection = collection;
        this.id = id;
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

    public String getId() {
        return id;
    }
}
