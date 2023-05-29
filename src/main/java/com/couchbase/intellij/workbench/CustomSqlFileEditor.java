package com.couchbase.intellij.workbench;


import com.couchbase.intellij.persistence.storage.QueryHistoryStorage;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
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
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

public class CustomSqlFileEditor implements FileEditor {
    private final Editor myEditor;
    private final VirtualFile file;
    private Project project;
    private JLabel historyLabel;

    private JComponent component;

    private int currentHistoryIndex;
    JPanel panel;

    CustomSqlFileEditor(Project project, VirtualFile file) {
        this.file = file;
        this.project = project;
        EditorFactory editorFactory = EditorFactory.getInstance();
        myEditor = EditorFactory.getInstance().createEditor(editorFactory.createDocument(LoadTextUtil.loadText(file)), project, file, false);
        panel = new JPanel(new BorderLayout());
        QueryHistoryStorage.getInstance().getValue().setHistory(new ArrayList<>());
        init();

    }

    @NotNull
    @Override
    public VirtualFile getFile() {
        return file;
    }

    @NotNull
    @Override
    public JComponent getComponent() {
        return this.component;
    }

    public void init() {
        DefaultActionGroup executeGroup = new DefaultActionGroup();

        Icon executeIcon = IconLoader.findIcon("./assets/icons/play.svg");
        executeGroup.add(new AnAction("Execute", "Execute the query statement in the editor", executeIcon) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                String editorText = myEditor.getDocument().getText();
                if (QueryExecutor.executeQuery(editorText, currentHistoryIndex, project)) {
                    int historySize = QueryHistoryStorage.getInstance().getValue().getHistory().size();
                    currentHistoryIndex = historySize - 1;
                    SwingUtilities.invokeLater(() -> {
                        historyLabel.setText("history (" + historySize + "/" + historySize + ")");
                        historyLabel.revalidate();
                    });
                }
            }
        });

        ActionToolbar executeToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, executeGroup, true);
        executeToolbar.setTargetComponent(myEditor.getComponent());

        Icon leftIcon = IconLoader.findIcon("./assets/icons/chevron-left.svg");
        Icon rightIcon = IconLoader.findIcon("./assets/icons/chevron-right.svg");

        DefaultActionGroup prevActionGroup = new DefaultActionGroup();
        prevActionGroup.add(new AnAction("Previous history", "Previous history", leftIcon) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                if (currentHistoryIndex - 1 >= 0) {
                    currentHistoryIndex--;
                    ApplicationManager.getApplication().runWriteAction(new Runnable() {
                        @Override
                        public void run() {
                            myEditor.getDocument().setText(QueryHistoryStorage.getInstance().getValue().getHistory().get(currentHistoryIndex));
                        }
                    });


                    SwingUtilities.invokeLater(() -> {
                        System.out.println(currentHistoryIndex + 1);
                        historyLabel.setText("history (" + (currentHistoryIndex + 1) + "/" + QueryHistoryStorage.getInstance().getValue().getHistory().size() + ")");
                        historyLabel.revalidate();
                    });

                }
            }
        });

        DefaultActionGroup nextActionGroup = new DefaultActionGroup();
        nextActionGroup.add(new AnAction("Next history", "Next history", rightIcon) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                if (currentHistoryIndex + 1 < QueryHistoryStorage.getInstance().getValue().getHistory().size()) {
                    currentHistoryIndex++;

                    ApplicationManager.getApplication().runWriteAction(new Runnable() {
                        @Override
                        public void run() {
                            myEditor.getDocument().setText(QueryHistoryStorage.getInstance().getValue().getHistory().get(currentHistoryIndex));
                        }
                    });

                    SwingUtilities.invokeLater(() -> {
                        System.out.println(currentHistoryIndex + 1);
                        historyLabel.setText("history (" + (currentHistoryIndex + 1) + "/" + QueryHistoryStorage.getInstance().getValue().getHistory().size() + ")");
                        historyLabel.revalidate();
                    });
                }
            }
        });

        ActionToolbar prevToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, prevActionGroup, true);
        prevToolbar.setTargetComponent(panel);
        ActionToolbar nextToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, nextActionGroup, true);
        nextToolbar.setTargetComponent(panel);

        int historySize = QueryHistoryStorage.getInstance().getValue().getHistory().size();
        currentHistoryIndex = Math.max((historySize - 1), 0);
        historyLabel = new JLabel("history (" + historySize + "/" + historySize + ")");
        historyLabel.setFont(historyLabel.getFont().deriveFont(10.0f)); // set smaller font size
        historyLabel.setBorder(JBUI.Borders.emptyRight(12));

        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.add(prevToolbar.getComponent(), BorderLayout.WEST);
        historyPanel.add(historyLabel, BorderLayout.CENTER);
        historyPanel.add(nextToolbar.getComponent(), BorderLayout.EAST);

        JPanel favorite = new JPanel(new BorderLayout());
        DefaultActionGroup favoriteActionGroup = new DefaultActionGroup();
        ActionToolbar favToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, favoriteActionGroup, true);
        favToolbar.setTargetComponent(panel);
        favoriteActionGroup.add(new AnAction("Favorite Query", "Favorite query", IconLoader.findIcon("./assets/icons/star-empty.svg")) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                // handle next history navigation
            }
        });
        favorite.add(favToolbar.getComponent(), BorderLayout.CENTER);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(historyPanel, BorderLayout.CENTER);
        leftPanel.add(favorite, BorderLayout.EAST);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(executeToolbar.getComponent(), BorderLayout.WEST);
        topPanel.add(leftPanel, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(myEditor.getComponent(), BorderLayout.CENTER);
        myEditor.getContentComponent().requestFocusInWindow();
        component = panel;
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