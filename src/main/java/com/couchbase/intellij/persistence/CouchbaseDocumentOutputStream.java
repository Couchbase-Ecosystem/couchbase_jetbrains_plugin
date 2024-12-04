package com.couchbase.intellij.persistence;

import com.couchbase.client.core.error.InvalidArgumentException;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.workbench.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

public class CouchbaseDocumentOutputStream extends OutputStream {

    private final boolean raw;
    private StringBuilder sb = new StringBuilder();
    private final CouchbaseDocumentVirtualFile file;

    public CouchbaseDocumentOutputStream(CouchbaseDocumentVirtualFile file, boolean raw) {
        this.file = file;
        this.raw = raw;
    }

    public CouchbaseDocumentOutputStream(CouchbaseDocumentVirtualFile file) {
        this(file, false);
    }

    @Override
    public void close() throws IOException {
        if (ActiveCluster.getInstance() != null) {
            Cluster cluster = ActiveCluster.getInstance().getCluster();
            try {
                JsonObject doc = JsonObject.fromJson(sb.toString());
                cluster.bucket(file.bucket()).scope(file.scope()).collection(file.collection()).upsert(file.id(), doc);
            } catch (InvalidArgumentException e) {
                cluster.bucket(file.bucket()).scope(file.scope()).collection(file.collection()).upsert(file.id(), sb.toString());
            }
            Log.debug("stored document " + file.id() + ": \n" + sb.toString() + "\\n");
            file.setContentHash(Arrays.hashCode(sb.toString().getBytes(Charset.defaultCharset())));
            file.setSize(sb.length());
        } else {
            throw new IOException("No active cluster");
        }
    }

    @Override
    public void write(int b) throws IOException {
        sb.append((char) b);
    }
}
