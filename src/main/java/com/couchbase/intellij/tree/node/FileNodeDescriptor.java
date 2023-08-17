package com.couchbase.intellij.tree.node;

import com.intellij.icons.AllIcons;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.vfs.VirtualFile;

public class FileNodeDescriptor extends NodeDescriptor {

    private VirtualFile virtualFile;
    private String bucket;
    private String scope;
    private String collection;
    private String id;

    private FileType type;

    public FileNodeDescriptor(String name, String bucket, String scope, String collection, String id, FileType type, VirtualFile virtualFile) {
        super(name, type==FileType.JSON? JsonFileType.INSTANCE.getIcon():AllIcons.FileTypes.Any_type);
        this.virtualFile = virtualFile;
        this.bucket = bucket;
        this.scope = scope;
        this.collection = collection;
        this.type = type;
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

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public static enum FileType {
        JSON,
        BINARY,
        UNKNOWN
    }
}
