package com.couchbase.intellij.persistence;

import com.couchbase.intellij.database.ActiveCluster;

import java.io.IOException;
import java.io.OutputStream;

public class CouchbaseDocumentOutputStream extends OutputStream {
    private final StringBuilder content = new StringBuilder();
    private final CouchbaseDocumentVirtualFile target;

    public CouchbaseDocumentOutputStream(CouchbaseDocumentVirtualFile target) {
        this.target = target;
    }
    @Override
    public void write(int b) throws IOException {
        content.append(b);
    }

    @Override
    public void close() throws IOException {
        if (!content.isEmpty()) {
            ActiveCluster.getInstance().getCluster()
                    .bucket(target.bucket())
                    .scope(target.scope())
                    .collection(target.collection())
                    .upsert(target.id(), content.toString());
        }
    }
}
