package com.couchbase.intellij.persistence;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.*;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Key;
import java.util.Arrays;
import java.util.List;

/** A project must not dispose its documents while their server saves are pending. */
public final class CouchbaseDocumentCloseHandler implements ProjectCloseHandler {
    public static final Key<Boolean> CLOSE_AFTER_SAVE = Key.create("couchbase.close.after.save");

    private static List<Document> dirty(Project project) {
        var manager = FileDocumentManager.getInstance();
        return Arrays.stream(manager.getUnsavedDocuments()).filter(document ->
                manager.getFile(document) instanceof CouchbaseDocumentVirtualFile file && file.getProject() == project).toList();
    }

    @Override public boolean canClose(Project project) {
        List<Document> documents = dirty(project);
        if (documents.isEmpty()) return true;
        if (documents.stream().anyMatch(CouchbaseDocumentSaveCoordinator::isSaving)) {
            project.putUserData(CLOSE_AFTER_SAVE, true);
            return false;
        }
        int choice = Messages.showDialog(project, "Some Couchbase documents have unsaved changes.",
                "Unsaved Couchbase Documents", new String[]{"Save and Close", "Discard Changes and Close", "Cancel"},
                0, Messages.getWarningIcon());
        if (choice == 0) {
            project.putUserData(CLOSE_AFTER_SAVE, true);
            documents.forEach(document -> FileDocumentManager.getInstance().saveDocument(document));
            savesFinished(project);
            return false;
        }
        if (choice == 1) {
            // These VFS reads use the last server snapshot; discarding never performs I/O.
            documents.forEach(document -> FileDocumentManager.getInstance().reloadFromDisk(document, project));
            return dirty(project).isEmpty();
        }
        return false;
    }

    public static void savesFinished(Project project) {
        if (project.isDisposed() || !Boolean.TRUE.equals(project.getUserData(CLOSE_AFTER_SAVE))) return;
        List<Document> documents = dirty(project);
        if (documents.stream().anyMatch(CouchbaseDocumentSaveCoordinator::isSaving)) return;
        project.putUserData(CLOSE_AFTER_SAVE, null);
        if (documents.isEmpty()) {
            ApplicationManager.getApplication().invokeLater(() -> {
                if (!project.isDisposed()) ProjectManager.getInstance().closeAndDispose(project);
            });
        } else {
            com.intellij.notification.NotificationGroupManager.getInstance().getNotificationGroup("Couchbase document saves")
                    .createNotification("The project was kept open because some Couchbase documents could not be saved. Resolve the save errors or close again to discard local changes.",
                            com.intellij.notification.NotificationType.WARNING).notify(project);
        }
    }
}
