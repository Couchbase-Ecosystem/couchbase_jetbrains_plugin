package com.couchbase.intellij.tree;

import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
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
        FileDocumentManager documentManager = FileDocumentManager.getInstance();
        if (!documentManager.isFileModified(editor.getFile())) {
            documentManager.reloadFiles(editor.getFile());
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
