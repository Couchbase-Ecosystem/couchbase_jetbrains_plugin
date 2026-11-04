package com.couchbase.intellij.tree;

import com.couchbase.client.core.error.DocumentExistsException;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.codec.RawJsonTranscoder;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.InsertOptions;
import com.couchbase.client.java.kv.UpsertOptions;
import com.couchbase.intellij.persistence.CouchbaseDocumentSaver;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Pastes a copied document into a collection. Insert first; when the ID is taken, the user chooses between
 * replacing the existing document, pasting under another ID (asked again if that one is taken too), or cancelling.
 */
public final class DocumentPaste {
    public enum Conflict { REPLACE, RENAME, CANCEL }

    /** The questions asked while pasting; the dialog version runs them on the EDT. */
    public interface Prompter {
        Conflict onConflict(String id, String target);

        /** The ID to paste under instead, or null to stop. */
        @Nullable String askNewId(String id);
    }

    /** The two writes a paste can need, so the loop can be tested without a cluster. */
    public interface Target {
        /** @return false when a document with that ID already exists. */
        boolean insert(String id, String content);

        void upsert(String id, String content);
    }

    private DocumentPaste() {}

    /** @return the ID the document was written under, or null when the user cancelled. */
    public static @Nullable String paste(Target target, String id, String content, String targetName, Prompter prompter) {
        String current = id;
        while (true) {
            if (target.insert(current, content)) {
                return current;
            }
            switch (prompter.onConflict(current, targetName)) {
                case REPLACE -> {
                    target.upsert(current, content);
                    return current;
                }
                case RENAME -> {
                    String renamed = prompter.askNewId(current);
                    if (renamed == null || renamed.isBlank()) {
                        return null;
                    }
                    current = renamed.trim();
                }
                case CANCEL -> {
                    return null;
                }
            }
        }
    }

    /** The source document's JSON, read fresh; non-JSON documents cannot be pasted. */
    public static String readJson(Collection source, String id) throws IOException {
        GetResult result = source.get(id);
        String content = new String(result.contentAsBytes(), StandardCharsets.UTF_8);
        try {
            CouchbaseDocumentSaver.requireJson(content);
        } catch (IOException notJson) {
            throw new IOException("Only JSON documents can be pasted; '" + id + "' is not valid JSON.", notJson);
        }
        return content;
    }

    /** Writes through the SDK with the JSON flag, like the editor's saves. */
    public static final class SdkTarget implements Target {
        private final Collection collection;

        public SdkTarget(Collection collection) {
            this.collection = collection;
        }

        @Override
        public boolean insert(String id, String content) {
            try {
                collection.insert(id, content, InsertOptions.insertOptions().transcoder(RawJsonTranscoder.INSTANCE));
                return true;
            } catch (DocumentExistsException exists) {
                return false;
            }
        }

        @Override
        public void upsert(String id, String content) {
            collection.upsert(id, content, UpsertOptions.upsertOptions().transcoder(RawJsonTranscoder.INSTANCE));
        }
    }

    /** Asks with modal dialogs; callable from the worker thread that does the writes. */
    public static final class DialogPrompter implements Prompter {
        private final Project project;

        public DialogPrompter(Project project) {
            this.project = project;
        }

        @Override
        public Conflict onConflict(String id, String target) {
            AtomicReference<Integer> choice = new AtomicReference<>(2);
            ApplicationManager.getApplication().invokeAndWait(() -> choice.set(Messages.showDialog(project,
                    "<html>A document with the ID <strong>" + id + "</strong> already exists in " + target
                            + ".<br>Replace it, or paste the copy under a different ID?</html>",
                    "Document Already Exists", new String[]{"Replace", "Change ID", "Cancel"}, 1, Messages.getWarningIcon())));
            return switch (choice.get()) {
                case 0 -> Conflict.REPLACE;
                case 1 -> Conflict.RENAME;
                default -> Conflict.CANCEL;
            };
        }

        @Override
        public @Nullable String askNewId(String id) {
            AtomicReference<String> entered = new AtomicReference<>();
            ApplicationManager.getApplication().invokeAndWait(() -> entered.set(Messages.showInputDialog(project,
                    "Paste the document under this ID:", "Paste Document", null, id, null)));
            return entered.get();
        }
    }
}
