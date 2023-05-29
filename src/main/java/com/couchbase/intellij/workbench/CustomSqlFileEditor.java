package com.couchbase.intellij.workbench;


import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.fileEditor.FileEditorStateLevel;
import com.intellij.openapi.fileEditor.impl.LoadTextUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;

public class CustomSqlFileEditor implements FileEditor {
    private final Editor myEditor;
    private final VirtualFile file;
    private Project project;
    JPanel panel;

    CustomSqlFileEditor(Project project, VirtualFile file) {
        this.file = file;
        this.project = project;
        EditorFactory editorFactory = EditorFactory.getInstance();
        myEditor = EditorFactory.getInstance().createEditor(editorFactory.createDocument(LoadTextUtil.loadText(file)), project, file, false);
        panel = new JPanel(new BorderLayout());

    }

    @NotNull
    @Override
    public VirtualFile getFile() {
        return file;
    }


    @NotNull
    @Override
    public JComponent getComponent() {
        DefaultActionGroup actionGroup = new DefaultActionGroup();

        Icon icon = IconLoader.findIcon("./assets/icons/play.svg");
        actionGroup.add(new AnAction("Execute", "Execute the query statement in the editor", icon) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {

                String editorText = myEditor.getDocument().getText();
                QueryExecutor.executeQuery(editorText, project);
            }
        });
        actionGroup.addSeparator();
        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, actionGroup, true);
        toolbar.setTargetComponent(myEditor.getComponent());  // set target component
        panel.add(toolbar.getComponent(), BorderLayout.NORTH);
        panel.add(myEditor.getComponent(), BorderLayout.CENTER);
        myEditor.getContentComponent().requestFocusInWindow();
        return panel;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return myEditor.getComponent();
    }

    @NotNull
    @Override
    public String getName() {
        return "Custom SQL File Editor";
    }

    @Override
    public void setState(@NotNull FileEditorState state) {
        // Do nothing
    }

    @Override
    public void dispose() {
        EditorFactory.getInstance().releaseEditor(myEditor);
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void selectNotify() {
    }

    @Override
    public void deselectNotify() {
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {
    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {
    }

    @Override
    public @Nullable FileEditorLocation getCurrentLocation() {
        return null;
    }

    @NotNull
    @Override
    public FileEditorState getState(@NotNull FileEditorStateLevel level) {
        return new FileEditorState() {
            @Override
            public boolean canBeMergedWith(FileEditorState otherState, FileEditorStateLevel level) {
                return false;
            }
        };
    }

    @Override
    public <T> T getUserData(@NotNull Key<T> key) {
        return null;
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, T value) {
    }
}