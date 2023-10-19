package com.couchbase.intellij.workbench;

import com.couchbase.intellij.persistence.CouchbaseDocumentVirtualFile;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DocumentEditor implements FileEditor {
    private Project project;
    private CouchbaseDocumentVirtualFile file;

    private JTextArea editor;

    private final Map<Key<?>, Object> data = new HashMap<>();

    public DocumentEditor(Project project, CouchbaseDocumentVirtualFile file) {
        this.project = project;
        this.file = file;
        editor = new JTextArea();
    }

    @Override
    public @NotNull JComponent getComponent() {
        try {
            String content = new String(file.contentsToByteArray());
            editor.setText(content);
        } catch (IOException e) {
            editor.setText("Failed to load the document");
            editor.setEditable(false);
            e.printStackTrace();
        }
        return editor;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return editor;
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) @NotNull String getName() {
        return "Couchbase Document Editor";
    }

    @Override
    public void setState(@NotNull FileEditorState state) {
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean isValid() {
        return editor.isEditable();
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {

    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {

    }

    @Override
    public void dispose() {

    }

    @Override
    public <T> @Nullable T getUserData(@NotNull Key<T> key) {
        return (T) data.get(key);
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T value) {
        data.put(key, value);
    }
}
