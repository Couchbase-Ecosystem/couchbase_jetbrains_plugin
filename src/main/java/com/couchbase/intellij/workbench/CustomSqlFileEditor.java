package com.couchbase.intellij.workbench;


import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.QueryResult;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.fileEditor.FileEditorStateLevel;
import com.intellij.openapi.fileEditor.impl.LoadTextUtil;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.util.List;

public class CustomSqlFileEditor implements FileEditor {
    private final Editor myEditor;
    private final VirtualFile file;
    private Project project;
    JPanel panel;

    CustomSqlFileEditor(Project project, VirtualFile file) {
        this.file = file;
        this.project = project;
        EditorFactory editorFactory = EditorFactory.getInstance();
        //Document document = editorFactory.createDocument(LoadTextUtil.loadText(file));
        //myEditor = editorFactory.createEditor(document, project, file, false);  // readOnly set to false

        //myEditor = TextEditorProvider.getInstance().createEditor(project, file);
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
        actionGroup.add(new AnAction("Button 1", "Button 1 description", icon) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {

                String editorText = myEditor.getDocument().getText();
                System.out.println("======text = "+editorText);

                final List<JsonObject> results = ActiveCluster.get().query(editorText).rowsAsObject();
                QueryResult.show(results, project);

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
        //return myEditor.getPreferredFocusedComponent();
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
       // TextEditorProvider.getInstance().disposeEditor(myEditor);
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


//
//
//import com.intellij.openapi.actionSystem.*;
//import com.intellij.openapi.editor.Document;
//import com.intellij.openapi.editor.Editor;
//import com.intellij.openapi.editor.EditorFactory;
//import com.intellij.openapi.fileEditor.FileEditor;
//import com.intellij.openapi.fileEditor.FileEditorLocation;
//import com.intellij.openapi.fileEditor.FileEditorState;
//import com.intellij.openapi.fileEditor.FileEditorStateLevel;
//import com.intellij.openapi.fileEditor.impl.LoadTextUtil;
//import com.intellij.openapi.project.Project;
//import com.intellij.openapi.util.Key;
//import com.intellij.openapi.vfs.VirtualFile;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import javax.swing.*;
//import java.awt.*;
//import java.beans.PropertyChangeListener;
//
//public class CustomSqlFileEditor implements FileEditor {
//    private final Editor myEditor;
//    private final VirtualFile file;
//    JPanel panel;
//
//    CustomSqlFileEditor(Project project, VirtualFile file) {
//        this.file = file;
//        EditorFactory editorFactory = EditorFactory.getInstance();
//        Document document = editorFactory.createDocument(LoadTextUtil.loadText(file));
//        myEditor = editorFactory.createEditor(document, project);
//        panel = new JPanel(new BorderLayout());
//    }
//
//    @NotNull
//    @Override
//    public VirtualFile getFile() {
//        return file;
//    }
//
//
//    @NotNull
//    @Override
//    public JComponent getComponent() {
//        DefaultActionGroup actionGroup = new DefaultActionGroup();
//
//        actionGroup.add(new AnAction("Button 1") {
//            @Override
//            public void actionPerformed(@NotNull AnActionEvent e) {
//                // Add your logic for button 1 click event
//            }
//        });
//        JComponent toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, actionGroup, true).getComponent();
//        panel.add(toolbar, BorderLayout.NORTH);
//        panel.add(myEditor.getComponent(), BorderLayout.CENTER);
//        return panel;
//    }
//
//    @Override
//    public JComponent getPreferredFocusedComponent() {
//        //return myEditor.getContentComponent();
//        return panel;
//    }
//
//    @NotNull
//    @Override
//    public String getName() {
//        return "Custom SQL File Editor";
//    }
//
//    @Override
//    public void setState(@NotNull FileEditorState state) {
//        // Do nothing
//    }
//
//    @Override
//    public void dispose() {
//        EditorFactory.getInstance().releaseEditor(myEditor);
//    }
//
//    @Override
//    public boolean isModified() {
//        return false;
//    }
//
//    @Override
//    public boolean isValid() {
//        return true;
//    }
//
//    @Override
//    public void selectNotify() {}
//
//    @Override
//    public void deselectNotify() {}
//
//    @Override
//    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {}
//
//    @Override
//    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {}
//
//    @Override
//    public @Nullable FileEditorLocation getCurrentLocation() {
//        return null;
//    }
//
//    @NotNull
//    @Override
//    public FileEditorState getState(@NotNull FileEditorStateLevel level) {
//        return new FileEditorState() {
//            @Override
//            public boolean canBeMergedWith(FileEditorState otherState, FileEditorStateLevel level) {
//                return false;
//            }
//        };
//    }
//
//    @Override
//    public <T> T getUserData(@NotNull Key<T> key) {
//        return null;
//    }
//
//    @Override
//    public <T> void putUserData(@NotNull Key<T> key, T value) {}
//}