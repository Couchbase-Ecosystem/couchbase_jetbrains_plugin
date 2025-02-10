package com.couchbase.intellij.tree;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.env.ClusterEnvironment;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.persistence.Clusters;
import com.couchbase.intellij.persistence.CouchbaseDocumentVirtualFile;
import com.couchbase.intellij.persistence.SavedCluster;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiManager;

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


    private void queueReload() {
        SavedCluster cluster = ActiveCluster.getInstance().getSavedCluster();
        if (cluster != null && cluster.hasOption(Clusters.Options.LIVE_POLLING)) {
            FileDocumentManager documentManager = FileDocumentManager.getInstance();
            VirtualFile file = editor.getFile();
            if (file instanceof CouchbaseDocumentVirtualFile) {
                if (cluster.hasOption(Clusters.Options.LIVE_RELOAD)) {
                    documentManager.reloadFiles(file);
                } else {
                    CouchbaseDocumentVirtualFile cbfile = (CouchbaseDocumentVirtualFile) file;
                    if (!cbfile.isDesynchronized() && cbfile.checkUpdatedOnCluster()) {
                        String[] options = {"Replace document with server's version", "Keep current document"};
                        int result = Messages.showDialog(
                                String.format("Document `%s` was modified on the server. How would you like to proceed?", file.getName()), "Document Conflict"
                                , options, 0, Messages.getWarningIcon());

                        if (result == 0) {
                            documentManager.reloadFiles(file);
                        } else {
                            cbfile.setDesynchronized(true);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        queueReload();
    }

    @Override
    public void focusLost(FocusEvent e) {

    }
}
