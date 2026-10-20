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
    public static final Key<String> LAST_ERROR = Key.create("couchbase.document.save.error");
    private CouchbaseDocumentSaveCoordinator() {}

    public static boolean maySave(Document document, CouchbaseDocumentVirtualFile file, boolean explicit) {
        Save pending = document.getUserData(PENDING);
        if (pending != null) {
            if (pending.replaying) return true;
            pending.again |= document.getModificationStamp() != pending.stamp;
            return false;
        }
        ActiveCluster active = ActiveCluster.getInstance();
        if (!canWrite(active, file)) return false;
        Save save = new Save(document, file, active, explicit);
        document.putUserData(PENDING, save);
        file.putUserData(LAST_ERROR, null);
        ApplicationManager.getApplication().executeOnPooledThread(() -> prepare(save));
        return false;
    }

    public static boolean isCompleting(CouchbaseDocumentVirtualFile file, String content) {
        return content.equals(file.getUserData(REPLAY));
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
            com.couchbase.client.java.json.JsonObject.fromJson(save.content);
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
                byte[] content = new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter()
                        .writeValueAsBytes(new com.fasterxml.jackson.databind.ObjectMapper().readTree(remote.contentAsBytes()));
                ui(save, () -> resolveConflict(save, remote.cas(), content));
            }
        } catch (Exception failure) { failed(save, failure); }
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
        } catch (Exception failure) { failed(save, failure); }
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
                FileDocumentManager.getInstance().saveDocument(save.document);
            }
        } finally {
            save.file.putUserData(REPLAY, null);
            finish(save);
        }
    }

    private static void failed(Save save, Exception failure) {
        ui(save, () -> {
            String message = failure instanceof CasMismatchException || failure instanceof DocumentExistsException
                    ? "The document changed again while saving. Your edits are kept; save again to review the conflict."
                    : "Could not save the document. Your edits are kept. " + Objects.toString(failure.getMessage(), "Try again.");
            save.file.putUserData(LAST_ERROR, message);
            finish(save);
            // Autosave should not interrupt typing with a modal error dialog.
            if (save.explicit && !save.file.getProject().isDisposed()) {
                com.intellij.notification.NotificationGroupManager.getInstance().getNotificationGroup("Couchbase document saves")
                        .createNotification(message, com.intellij.notification.NotificationType.WARNING).notify(save.file.getProject());
            }
        });
    }

    private static void finish(Save save) {
        if (save.document.getUserData(PENDING) == save) save.document.putUserData(PENDING, null);
        if (save.again && save.connection.isCurrent() && !save.file.getProject().isDisposed()
                && canWrite(ActiveCluster.getInstance(), save.file))
            FileDocumentManager.getInstance().saveDocument(save.document);
        CouchbaseDocumentCloseHandler.savesFinished(save.file.getProject());
    }

    private static void ui(Save save, Runnable callback) {
        ApplicationManager.getApplication().invokeLater(callback, save.modality);
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
