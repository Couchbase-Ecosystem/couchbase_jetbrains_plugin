package com.couchbase.intellij.persistence;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.codec.RawJsonTranscoder;
import com.couchbase.client.java.kv.MutationResult;
import com.couchbase.client.java.kv.ReplaceOptions;
import com.couchbase.client.java.kv.InsertOptions;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

/** The single server write used by document saves, including direct VFS writes. */
public final class CouchbaseDocumentSaver {
    private static final ObjectMapper JSON = new ObjectMapper();
    private CouchbaseDocumentSaver() {}

    public static MutationResult save(Collection collection, String id, String content, Long expectedCas) throws IOException {
        if (JSON.readTree(content) == null) throw new IOException("The document must contain valid JSON.");
        if (expectedCas == null) {
            // Never overwrite a document created by someone else after the editor's existence check.
            return collection.insert(id, content, InsertOptions.insertOptions().transcoder(RawJsonTranscoder.INSTANCE));
        }
        return collection.replace(id, content, ReplaceOptions.replaceOptions().cas(expectedCas)
                .preserveExpiry(true).transcoder(RawJsonTranscoder.INSTANCE));
    }
}
