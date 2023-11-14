package com.couchbase.intellij.tree.cblite.nodes;

import com.couchbase.intellij.tree.node.NodeDescriptor;
import com.couchbase.lite.Blob;
import com.intellij.openapi.util.IconLoader;

import lombok.Getter;

@Getter
public class CBLBlobNodeDescriptor extends NodeDescriptor {
    private final Blob blob;
    private final String contentType;
    private final String digest;
    private final long length;

    private final String blobName;
    private final String scope;
    private final String collection;
    private final String document;

    public CBLBlobNodeDescriptor(Blob blob, String blobName, String scope, String collection, String document) {
        super(blobName, IconLoader.getIcon("/assets/icons/blob.svg", CBLBlobNodeDescriptor.class));
        this.blob = blob;
        this.blobName = blobName;
        this.contentType = blob.getContentType();
        this.digest = blob.digest();
        this.length = blob.length();

        this.scope = scope;
        this.collection = collection;
        this.document = document;
    }
}
