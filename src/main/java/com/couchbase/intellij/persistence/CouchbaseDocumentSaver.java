package com.couchbase.intellij.persistence;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.codec.RawJsonTranscoder;
import com.couchbase.client.java.kv.MutationResult;
import com.couchbase.client.java.kv.ReplaceOptions;
import com.couchbase.client.java.kv.InsertOptions;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;

/** The single server write used by document saves, including direct VFS writes. */
public final class CouchbaseDocumentSaver {
    // Editable documents are always JSON (others open read-only) and are written raw with the JSON
    // flag, so the content must be exactly one JSON value: no trailing tokens, never empty.
    private static final ObjectMapper JSON = new ObjectMapper().enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
    // A fixed "\n" keeps loaded documents LF on every OS, as the editor's save replay expects.
    private static final ObjectWriter PRETTY = JSON.writer(new DefaultPrettyPrinter()
            .withObjectIndenter(new DefaultIndenter("  ", "\n")));
    private CouchbaseDocumentSaver() {}

    public static MutationResult save(Collection collection, String id, String content, Long expectedCas) throws IOException {
        requireJson(content);
        if (expectedCas == null) {
            // Never overwrite a document created by someone else after the editor's existence check.
            return collection.insert(id, content, InsertOptions.insertOptions().transcoder(RawJsonTranscoder.INSTANCE));
        }
        return collection.replace(id, content, ReplaceOptions.replaceOptions().cas(expectedCas)
                .preserveExpiry(true).transcoder(RawJsonTranscoder.INSTANCE));
    }

    /** Accepts any single JSON value, including top-level arrays and scalars. */
    public static JsonNode requireJson(String content) throws IOException {
        JsonNode node;
        try {
            node = content == null ? null : JSON.readTree(content);
        } catch (JsonProcessingException invalid) {
            throw new IOException("The document must contain valid JSON. " + invalid.getOriginalMessage(), invalid);
        }
        if (node == null || node.isMissingNode()) throw new IOException("The document must contain valid JSON.");
        return node;
    }

    /** Whether two editor texts are the same JSON document, ignoring line separators and formatting. */
    public static boolean sameJson(String a, String b) {
        if (a == null || b == null) return false;
        if (a.equals(b) || normalizeLineSeparators(a).equals(normalizeLineSeparators(b))) return true;
        try {
            return requireJson(a).equals(requireJson(b));
        } catch (IOException invalid) {
            return false;
        }
    }

    /** Formats server content for the editor; content that is not exactly one JSON value is rejected. */
    public static byte[] prettyPrint(byte[] json) throws IOException {
        JsonNode node = JSON.readTree(json);
        if (node == null || node.isMissingNode()) throw new JsonParseException(null, "The document is empty.");
        return PRETTY.writeValueAsBytes(node);
    }

    private static String normalizeLineSeparators(String text) {
        return text.replace("\r\n", "\n").replace('\r', '\n');
    }
}
