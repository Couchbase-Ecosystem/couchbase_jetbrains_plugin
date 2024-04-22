package com.couchbase.intellij.tree.node;

import com.intellij.json.JsonFileType;
import com.intellij.openapi.vfs.VirtualFile;
import lombok.Getter;

public class SearchIndexNodeDescriptor extends NodeDescriptor {

    @Getter
    private final String bucket;

    @Getter
    private final String scope;

    @Getter
    private final String indexName;
    private VirtualFile virtualFile;

    public SearchIndexNodeDescriptor(String indexName, String bucket, String scope, String name, VirtualFile virtualFile) {
        super(name, JsonFileType.INSTANCE.getIcon());
        this.virtualFile = virtualFile;
        this.bucket = bucket;
        this.scope = scope;
        this.indexName = indexName;
    }

    public VirtualFile getVirtualFile() {
        return virtualFile;
    }

    public void setVirtualFile(VirtualFile virtualFile) {
        this.virtualFile = virtualFile;
    }


}
