package com.couchbase.intellij.tree;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;

/**
 * The document last copied in the explorer tree, for "Paste Document" on a collection. Only a reference is kept:
 * the content is read fresh from the server when it is pasted, so the copy never goes stale.
 */
public final class DocumentClipboard {
    public record CopiedDocument(String connectionId, String bucket, String scope, String collection, String id) {}

    private static final AtomicReference<CopiedDocument> COPIED = new AtomicReference<>();

    private DocumentClipboard() {}

    /** Remembers the document for pasting and puts its ID on the system clipboard. */
    public static void copy(CopiedDocument document) {
        COPIED.set(document);
        TreeCopy.toClipboard(document.id());
    }

    public static @Nullable CopiedDocument get() {
        return COPIED.get();
    }
}
