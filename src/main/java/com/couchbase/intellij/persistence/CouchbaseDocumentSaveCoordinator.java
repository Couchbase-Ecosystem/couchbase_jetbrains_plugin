package com.couchbase.intellij.persistence;

import com.couchbase.client.core.error.*;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.intellij.VirtualFileKeys;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.ClusterUiTask;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Key;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/** Saves a captured editor version on a worker, then acknowledges it to the IDE on the EDT. */
public final class CouchbaseDocumentSaveCoordinator {
    private static final Key<Save> PENDING = Key.create("couchbase.pending.document.save");
    private static final Key<String> REPLAY = Key.create("couchbase.completed.document.save");
    private static final Key<Save> REPLAYING = Key.create("couchbase.replaying.document.save");
    private static final com.intellij.openapi.diagnostic.Logger LOG = com.intellij.openapi.diagnostic.Logger.getInstance(CouchbaseDocumentSaveCoordinator.class);
    public static final Key<String> LAST_ERROR = Key.create("couchbase.document.save.error");
    /** Epoch millis before which autosave must not retry a failed save; explicit saves ignore it. */
    private static final Key<Long> RETRY_AFTER = Key.create("couchbase.document.save.retry.after");
    public static final long AUTOSAVE_RETRY_DELAY_MS = 30_000;
    private CouchbaseDocumentSaveCoordinator() {}

    public static boolean maySave(Document document, CouchbaseDocumentVirtualFile file, boolean explicit) {
        Save pending = document.getUserData(PENDING);
        if (pending != null) {
            if (pending.replaying) return true;
            pending.again |= document.getModificationStamp() != pending.stamp;
            return false;
        }
        // A failed save leaves the document dirty, so autosave would otherwise hit an unreachable
        // server again on every tick. Explicit saves (Ctrl+S) always try.
        Long retryAfter = file.getUserData(RETRY_AFTER);
        if (!explicit && retryAfter != null && System.currentTimeMillis() < retryAfter) return false;
        start(document, file, explicit);
        return false;
    }

    private static void start(Document document, CouchbaseDocumentVirtualFile file, boolean explicit) {
        ActiveCluster active = ActiveCluster.getInstance();
        if (!canWrite(active, file)) return;
        Save save = new Save(document, file, active, explicit);
        document.putUserData(PENDING, save);
        file.putUserData(LAST_ERROR, null);
        file.putUserData(RETRY_AFTER, null);
        ApplicationManager.getApplication().executeOnPooledThread(() -> prepare(save));
    }

    /**
     * Whether the platform is writing the version that a background save already stored on the server.
     * The written text may use other line separators, or have been reformatted by a
     * beforeDocumentSaving listener, without being a different JSON document.
     */
    public static boolean isCompleting(CouchbaseDocumentVirtualFile file, String content) {
        return CouchbaseDocumentSaver.sameJson(file.getUserData(REPLAY), content);
    }

    /**
     * The platform is replaying a completed save, but a beforeDocumentSaving listener changed the
     * document into different JSON. The changed text is saved by a new background save instead.
     */
    public static boolean isReplayChanged(CouchbaseDocumentVirtualFile file) {
        Save replay = file.getUserData(REPLAYING);
        if (replay == null) return false;
        replay.changedOnReplay = true;
        return true;
    }

    public static boolean isSaving(Document document) { return document.getUserData(PENDING) != null; }

    private static boolean canWrite(ActiveCluster active, CouchbaseDocumentVirtualFile file) {
        return active != null && active.get() != null && !active.isReadOnlyMode()
                && !"true".equals(file.getUserData(VirtualFileKeys.READ_ONLY))
                && Objects.equals(active.getId(), file.getUserData(VirtualFileKeys.CONN_ID));
    }

    private static void prepare(Save save) {
        try {
            // Validation and both network operations are outside the platform locks.
            CouchbaseDocumentSaver.requireJson(save.content);
            requireWritable(save);
            GetResult remote;
            try { remote = save.collection().get(save.file.id()); }
            catch (DocumentNotFoundException missing) {
                if (save.cas == null) write(save, null);
                else ui(save, () -> resolveDeleted(save));
                return;
            }
            if (save.cas != null && Long.parseLong(save.cas) == remote.cas()) write(save, remote.cas());
            else {
                byte[] content = CouchbaseDocumentSaver.prettyPrint(remote.contentAsBytes());
                ui(save, () -> resolveConflict(save, remote.cas(), content));
            }
        } catch (Throwable failure) { failed(save, failure); }
    }

    private static void resolveDeleted(Save save) {
        if (!stillCurrent(save)) { finish(save); return; }
        int choice = Messages.showDialog(save.file.getProject(),
                "This document was deleted on the server. Recreate it with your current edits?",
                "Document Deleted", new String[]{"Recreate Document", "Cancel"}, 1, Messages.getWarningIcon());
        if (choice == 0) ApplicationManager.getApplication().executeOnPooledThread(() -> write(save, null));
        else finish(save);
    }

    private static void resolveConflict(Save save, long cas, byte[] serverContent) {
        if (!stillCurrent(save)) { finish(save); return; }
        int choice = Messages.showDialog(save.file.getProject(),
                "The document that you are trying to save has already been modified in the server. How would you like to proceed?",
                "Document Conflict", new String[]{"Overwrite server's version", "Replace mine with the latest version from server", "Cancel"},
                1, Messages.getWarningIcon());
        if (choice == 0) ApplicationManager.getApplication().executeOnPooledThread(() -> write(save, cas));
        else if (choice == 1) {
            if (!stillCurrent(save)) { finish(save); return; }
            ApplicationManager.getApplication().runWriteAction(() ->
                    save.document.setText(new String(serverContent, StandardCharsets.UTF_8)));
            acknowledge(save, serverContent, cas, true);
        } else finish(save);
    }

    private static boolean stillCurrent(Save save) {
        return !save.file.getProject().isDisposed() && canWrite(save.active, save.file)
                && save.connection.isCurrent() && save.document.getModificationStamp() == save.stamp;
    }

    private static void requireWritable(Save save) {
        save.connection.requireWritable();
        if (!canWrite(save.active, save.file)) throw new IllegalStateException("Reconnect to the document's original writable connection before saving.");
    }

    private static void write(Save save, Long cas) {
        try {
            requireWritable(save);
            var result = CouchbaseDocumentSaver.save(save.collection(), save.file.id(), save.content, cas);
            try {
                if (save.connection.isCurrent() && save.active.getSavedCluster() != null)
                    com.couchbase.intellij.database.InferHelper.invalidateInferCacheIfOlder(save.file.bucket(), save.file.scope(), save.file.collection(), java.util.concurrent.TimeUnit.MINUTES.toMillis(1));
            } catch (RuntimeException cacheFailure) {
                com.couchbase.intellij.workbench.Log.debug("Document saved; schema cache update failed", cacheFailure);
            }
            ui(save, () -> acknowledge(save, save.content.getBytes(StandardCharsets.UTF_8), result.cas(), false));
        } catch (Throwable failure) { failed(save, failure); }
    }

    private static void acknowledge(Save save, byte[] content, long cas, boolean reload) {
        try {
            if (save.file.getProject().isDisposed()) return;
            save.file.acceptServerContent(content, cas);
            if (reload || save.document.getModificationStamp() == save.stamp) {
                // The SDK already accepted this exact version. A normal platform save
                // can now clear its dirty flag without any server call in the VFS stream.
                save.replaying = true;
                save.file.putUserData(REPLAY, new String(content, StandardCharsets.UTF_8));
                save.file.putUserData(REPLAYING, save);
                // As-is: no trailing-space stripping or final newline, so the platform writes what the server has.
                FileDocumentManager.getInstance().saveDocumentAsIs(save.document);
            }
        } finally {
            save.file.putUserData(REPLAY, null);
            save.file.putUserData(REPLAYING, null);
            finish(save);
        }
    }

    private static void failed(Save save, Throwable failure) {
        // Errors must still release the save, or the document and project close stay blocked.
        ui(save, () -> {
            String message = failure instanceof CasMismatchException || failure instanceof DocumentExistsException
                    ? "The document changed again while saving. Your edits are kept; save again to review the conflict."
                    : "Could not save the document. Your edits are kept. " + Objects.toString(failure.getMessage(), "Try again.");
            save.file.putUserData(LAST_ERROR, message);
            save.file.putUserData(RETRY_AFTER, System.currentTimeMillis() + AUTOSAVE_RETRY_DELAY_MS);
            finish(save);
            // Autosave should not interrupt typing with a modal error dialog.
            if (save.explicit && !save.file.getProject().isDisposed()) {
                com.intellij.notification.NotificationGroupManager.getInstance().getNotificationGroup("Couchbase document saves")
                        .createNotification(message, com.intellij.notification.NotificationType.WARNING).notify(save.file.getProject());
            }
        });
        if (failure instanceof Error && !(failure instanceof com.intellij.openapi.diagnostic.ControlFlowException))
            LOG.error("Unexpected failure while saving a Couchbase document", failure);
    }

    private static void finish(Save save) {
        if (save.document.getUserData(PENDING) != save) return;
        save.document.putUserData(PENDING, null);
        boolean follow = save.connection.isCurrent() && !save.file.getProject().isDisposed()
                && canWrite(ActiveCluster.getInstance(), save.file);
        // The replay already cleared the dirty flag, so a normal save would be skipped.
        if (follow && save.changedOnReplay) start(save.document, save.file, save.explicit);
        else if (follow && save.again) FileDocumentManager.getInstance().saveDocument(save.document);
        CouchbaseDocumentCloseHandler.savesFinished(save.file.getProject());
    }

    private static void ui(Save save, Runnable callback) {
        ApplicationManager.getApplication().invokeLater(() -> {
            try {
                callback.run();
            } catch (Throwable failure) {
                // A dialog or platform call that fails must not leave the save pending forever.
                finish(save);
                throw failure;
            }
        }, save.modality);
    }

    private static final class Save {
        final Document document;
        final CouchbaseDocumentVirtualFile file;
        final ActiveCluster active;
        final ClusterUiTask.Connection connection;
        final String content;
        final String cas;
        final long stamp;
        final boolean explicit;
        final ModalityState modality = ModalityState.current();
        boolean replaying;
        boolean again;
        boolean changedOnReplay;
        Save(Document document, CouchbaseDocumentVirtualFile file, ActiveCluster active, boolean explicit) {
            this.document = document; this.file = file; this.active = active; this.explicit = explicit;
            connection = ClusterUiTask.capture(active);
            content = document.getText(); stamp = document.getModificationStamp(); cas = file.getUserData(VirtualFileKeys.CAS);
        }
        com.couchbase.client.java.Collection collection() {
            return connection.sdk().bucket(file.bucket()).scope(file.scope()).collection(file.collection());
        }
    }
}
