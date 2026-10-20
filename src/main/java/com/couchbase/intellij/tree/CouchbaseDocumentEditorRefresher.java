package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.persistence.Clusters;
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
        long stamp = document == null ? -1 : document.getModificationStamp();
        checking = true;
        com.couchbase.intellij.database.ClusterUiTask.submit(file.getProject(), active, "Checking Couchbase document", connection -> {
            var result = connection.sdk().bucket(file.bucket()).scope(file.scope()).collection(file.collection()).get(file.id());
            var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return new RemoteDocument(result.cas(), mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(mapper.readTree(result.contentAsBytes())));
        }, remote -> {
            checking = false;
            if (!editor.isValid() || document != null && document.getModificationStamp() != stamp
                    || java.util.Objects.equals(Long.toString(remote.cas()), file.getUserData(com.couchbase.intellij.VirtualFileKeys.CAS))) return;
            boolean reload = cluster.hasOption(Clusters.Options.LIVE_RELOAD);
            if (!reload && !file.isDesynchronized()) {
                int choice = Messages.showDialog("Document `" + file.getName() + "` was modified on the server. How would you like to proceed?",
                        "Document Conflict", new String[]{"Replace document with server's version", "Keep current document"}, 0, Messages.getWarningIcon());
                reload = choice == 0;
                if (!reload) file.setDesynchronized(true);
            }
            if (reload) {
                file.acceptServerContent(remote.content(), remote.cas());
                manager.reloadFiles(file);
            }
        }, error -> { checking = false; Log.debug("Could not check the document for server changes", error); });
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
