package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.persistence.Clusters;
import com.couchbase.intellij.persistence.CouchbaseDocumentSaveCoordinator;
import com.couchbase.intellij.persistence.CouchbaseDocumentSaver;
import com.couchbase.intellij.persistence.CouchbaseDocumentVirtualFile;
import com.couchbase.intellij.persistence.SavedCluster;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.ui.Messages;

import java.awt.event.*;

public class CouchbaseDocumentEditorRefresher implements FocusListener {
    private FileEditor editor;

    private CouchbaseDocumentEditorRefresher(FileEditor editor) {
        this.editor = editor;
    }

    public static void attach(FileEditor editor) {
        CouchbaseDocumentEditorRefresher refresher = new CouchbaseDocumentEditorRefresher(editor);
        editor.getPreferredFocusedComponent().addFocusListener(refresher);
    }


    private boolean checking;

    private void queueReload() {
        ActiveCluster active = ActiveCluster.getInstance();
        SavedCluster cluster = active.getSavedCluster();
        if (checking || cluster == null || !cluster.hasOption(Clusters.Options.LIVE_POLLING)
                || !(editor.getFile() instanceof CouchbaseDocumentVirtualFile file)
                || !java.util.Objects.equals(cluster.getId(), file.getUserData(com.couchbase.intellij.VirtualFileKeys.CONN_ID))) return;
        var manager = FileDocumentManager.getInstance();
        var document = manager.getCachedDocument(file);
        // The user's own save may already be on the server without its CAS acknowledged here.
        if (isSaving(document)) return;
        long stamp = document == null ? -1 : document.getModificationStamp();
        // A save that completes during this read moves the CAS; its result must not look like a server change.
        String cas = file.getUserData(com.couchbase.intellij.VirtualFileKeys.CAS);
        checking = true;
        com.couchbase.intellij.database.ClusterUiTask.submit(file.getProject(), active, "Checking Couchbase document", connection -> {
            var result = connection.sdk().bucket(file.bucket()).scope(file.scope()).collection(file.collection()).get(file.id());
            return new RemoteDocument(result.cas(), CouchbaseDocumentSaver.prettyPrint(result.contentAsBytes()));
        }, remote -> {
            checking = false;
            if (!editor.isValid() || isSaving(document) || document != null && document.getModificationStamp() != stamp
                    || !java.util.Objects.equals(cas, file.getUserData(com.couchbase.intellij.VirtualFileKeys.CAS))
                    || java.util.Objects.equals(Long.toString(remote.cas()), file.getUserData(com.couchbase.intellij.VirtualFileKeys.CAS))) return;
            boolean reload = cluster.hasOption(Clusters.Options.LIVE_RELOAD);
            if (!reload && !file.isDesynchronized()) {
                int choice = Messages.showDialog("Document `" + file.getName() + "` was modified on the server. How would you like to proceed?",
                        "Document Conflict", new String[]{"Replace document with server's version", "Keep current document"}, 0, Messages.getWarningIcon());
                // A save may have started or finished while the dialog was open.
                if (isSaving(document) || !java.util.Objects.equals(cas, file.getUserData(com.couchbase.intellij.VirtualFileKeys.CAS))) return;
                reload = choice == 0;
                if (!reload) file.setDesynchronized(true);
            }
            if (reload) {
                file.acceptServerContent(remote.content(), remote.cas());
                manager.reloadFiles(file);
            }
        }, error -> { checking = false; Log.debug("Could not check the document for server changes", error); });
    }

    private static boolean isSaving(com.intellij.openapi.editor.Document document) {
        return document != null && CouchbaseDocumentSaveCoordinator.isSaving(document);
    }

    private record RemoteDocument(long cas, byte[] content) {}

    @Override
    public void focusGained(FocusEvent e) {
        queueReload();
    }

    @Override
    public void focusLost(FocusEvent e) {

    }
}
