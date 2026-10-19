package com.couchbase.intellij.persistence;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.workbench.Log;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import com.couchbase.intellij.VirtualFileKeys;
import java.util.Objects;
import java.util.Arrays;

public class CouchbaseDocumentOutputStream extends ByteArrayOutputStream {

    private final boolean raw;
    private boolean closed;
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
        if (closed) return;
        closed = true;
        ActiveCluster active = ActiveCluster.getInstance();
        if (active == null || active.getCluster() == null) throw new IOException("No active cluster.");
        if (active.isReadOnlyMode() || !file.isWritable()) throw new IOException("This document is read-only.");
        if (!Objects.equals(active.getId(), file.getUserData(VirtualFileKeys.CONN_ID))) {
            throw new IOException("Reconnect to the document's original cluster before saving.");
        }
        String content = toString(StandardCharsets.UTF_8);
        String cas = file.getUserData(VirtualFileKeys.CAS);
        try {
            var collection = active.getCluster().bucket(file.bucket()).scope(file.scope()).collection(file.collection());
            var result = CouchbaseDocumentSaver.save(collection, file.id(), content, cas == null ? null : Long.valueOf(cas));
            file.putUserData(VirtualFileKeys.CAS, Long.toString(result.cas()));
            file.setContentHash(Arrays.hashCode(toByteArray()));
            file.setTimeStamp(System.currentTimeMillis());
            file.setSize(size());
            file.recordSavedContent(toByteArray());
            try {
                if (ActiveCluster.getInstance() == active && active.getSavedCluster() != null) {
                    com.couchbase.intellij.database.InferHelper.invalidateInferCacheIfOlder(file.bucket(), file.scope(), file.collection(), java.util.concurrent.TimeUnit.MINUTES.toMillis(1));
                }
            } catch (RuntimeException cacheFailure) {
                Log.debug("Document saved; could not invalidate the schema cache", cacheFailure);
            }
        } catch (com.couchbase.client.core.error.CasMismatchException | com.couchbase.client.core.error.DocumentExistsException conflict) {
            throw new IOException("The document changed on the server while saving. Your edits are kept; save again to review the conflict.", conflict);
        } catch (com.couchbase.client.core.error.DocumentNotFoundException deleted) {
            throw new IOException("The document was deleted on the server. Your edits are kept; save again to choose whether to recreate it.", deleted);
        } catch (RuntimeException failure) {
            throw new IOException("Could not save the Couchbase document. Your edits are kept.", failure);
        }
    }
}
