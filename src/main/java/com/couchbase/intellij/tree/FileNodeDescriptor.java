package com.couchbase.intellij.tree;

import com.intellij.json.JsonFileType;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;

public class FileNodeDescriptor extends NodeDescriptor {

    private VirtualFile virtualFile;

    public FileNodeDescriptor(String name, VirtualFile virtualFile) {
        super(name, JsonFileType.INSTANCE.getIcon());
        this.virtualFile = virtualFile;
    }

    public VirtualFile getVirtualFile() {
        return virtualFile;
    }

    public void setVirtualFile(VirtualFile virtualFile) {
        this.virtualFile = virtualFile;
    }
}
