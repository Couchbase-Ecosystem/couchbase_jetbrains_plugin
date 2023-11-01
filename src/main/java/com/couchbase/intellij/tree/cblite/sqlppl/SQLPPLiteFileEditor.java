package com.couchbase.intellij.tree.cblite.sqlppl;


import com.couchbase.intellij.VirtualFileKeys;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.persistence.storage.QueryHistoryStorage;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.fileEditor.impl.LoadTextUtil;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.util.ui.JBUI;
import org.intellij.sdk.language.SQLPPFormatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

public class SQLPPLiteFileEditor implements FileEditor, TextEditor {
    public static final String NO_QUERY_CONTEXT_SELECTED = "No Query Context Selected";
    private final EditorWrapper queryEditor;
    private final VirtualFile file;
    private final Project project;
    private final Map<Key<?>, Object> data = new HashMap<>();
    JPanel panel;
    private JLabel historyLabel;
    private JComponent component;
    private int currentHistoryIndex;
    private JPanel topPanel;

    private AnAction executeAction;

    SQLPPLiteFileEditor(Project project, VirtualFile file) {
        this.file = file;
        this.project = project;
        EditorFactory editorFactory = EditorFactory.getInstance();
        Document document = editorFactory.createDocument(LoadTextUtil.loadText(file));

        boolean isViewer = false;
        if ("true".equals(file.getUserData(VirtualFileKeys.READ_ONLY))) {
            isViewer = true;
            this.queryEditor = new EditorWrapper(EditorFactory.getInstance().createEditor(document, project, file, isViewer), null);
        } else {
            this.queryEditor = new EditorWrapper(null, (TextEditor) TextEditorProvider.getInstance().createEditor(project, file)); //Edit
        }

        this.panel = new JPanel(new BorderLayout());
        init(isViewer);
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

    public void init(boolean isViewer) {

        if (!isViewer) {
            buildToolbar();
        }
        panel.add(queryEditor.getComponent(), BorderLayout.CENTER);
        queryEditor.getContentComponent().requestFocusInWindow();
        component = panel;

    }

    private void buildToolbar() {
        DefaultActionGroup executeGroup = new DefaultActionGroup();

        Icon executeIcon = IconLoader.getIcon("/assets/icons/play.svg", SQLPPLiteFileEditor.class);

        executeAction = new AnAction("Execute", "Execute the query statement in the editor", executeIcon) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                new Task.ConditionalModal(null, "Running SQL++ Lite query", true, PerformInBackgroundOption.ALWAYS_BACKGROUND) {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        SQLLiteQueryExecutor.runQuery(queryEditor.getDocument().getText(), 0, project);
                    }
                }.queue();
            }
        };

        executeGroup.add(executeAction);

        executeGroup.addSeparator();

        Icon formatCode = IconLoader.getIcon("/assets/icons/format.svg", SQLPPLiteFileEditor.class);
        executeGroup.add(new AnAction("Format Code", "Formats a SQL++ code", formatCode) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                ApplicationManager.getApplication().runWriteAction(() -> queryEditor.getDocument().setText(SQLPPFormatter.format(queryEditor.getDocument().getText())));
            }
        });

        ActionToolbar executeToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, executeGroup, true);
        executeToolbar.setTargetComponent(queryEditor.getComponent());

        Icon leftIcon = IconLoader.getIcon("/assets/icons/chevron-left.svg", SQLPPLiteFileEditor.class);
        Icon rightIcon = IconLoader.getIcon("/assets/icons/chevron-right.svg", SQLPPLiteFileEditor.class);

        DefaultActionGroup prevActionGroup = new DefaultActionGroup();
        prevActionGroup.add(new AnAction("Previous History", "Previous history", leftIcon) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {

            }
        });

        DefaultActionGroup nextActionGroup = new DefaultActionGroup();
        nextActionGroup.add(new AnAction("Next History", "Next history", rightIcon) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {

            }
        });

        ActionToolbar prevToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, prevActionGroup, true);
        prevToolbar.setTargetComponent(panel);
        ActionToolbar nextToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, nextActionGroup, true);
        nextToolbar.setTargetComponent(panel);

        int historySize = QueryHistoryStorage.getInstance().getValue().getHistory().size();
        currentHistoryIndex = Math.max((historySize - 1), 0) + 1;
        historyLabel = new JLabel("history (" + (historySize + 1) + "/" + historySize + ")");
        historyLabel.setFont(historyLabel.getFont().deriveFont(10.0f));
        historyLabel.setBorder(JBUI.Borders.emptyRight(12));

        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.add(prevToolbar.getComponent(), BorderLayout.WEST);
        historyPanel.add(historyLabel, BorderLayout.CENTER);
        historyPanel.add(nextToolbar.getComponent(), BorderLayout.EAST);


        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(historyPanel, BorderLayout.CENTER);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(executeToolbar.getComponent(), BorderLayout.WEST);

        topPanel = new JPanel(new BorderLayout());
        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);

        if (ActiveCluster.getInstance().getColor() != null) {
            Border line = BorderFactory.createMatteBorder(0, 0, 1, 0, ActiveCluster.getInstance().getColor());
            Border margin = BorderFactory.createEmptyBorder(0, 0, 1, 0);
            Border compound = BorderFactory.createCompoundBorder(margin, line);
            topPanel.setBorder(compound);
        }

        panel.add(topPanel, BorderLayout.NORTH);
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return queryEditor.getComponent();
    }

    @NotNull
    @Override
    public String getName() {
        return "Custom SQL++ Lite File Editor";
    }

    @Override
    public void setState(@NotNull FileEditorState state) {
        // Do nothing
    }

    @Override
    public void dispose() {
        queryEditor.release();
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
        return (otherState, level1) -> false;
    }

    @Nullable
    @Override
    public <T> T getUserData(@NotNull Key<T> key) {
        @SuppressWarnings("unchecked") T result = (T) data.get(key);

        return result;
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T value) {
        if (value == null) {
            data.remove(key);
        } else {
            data.put(key, value);
        }
    }


    @Override
    public @NotNull Editor getEditor() {
        if (queryEditor.textEditor != null) {
            return queryEditor.textEditor.getEditor();
        } else {
            return queryEditor.viewer;
        }
    }

    @Override
    public boolean canNavigateTo(@NotNull Navigatable navigatable) {
        return false;
    }

    @Override
    public void navigateTo(@NotNull Navigatable navigatable) {

    }

    static class EditorWrapper {
        private final Editor viewer;
        private final TextEditor textEditor;

        public EditorWrapper(Editor viewer, TextEditor textEditor) {
            this.textEditor = textEditor;
            this.viewer = viewer;
        }

        public JComponent getComponent() {
            return textEditor == null ? viewer.getComponent() : textEditor.getComponent();
        }

        public JComponent getContentComponent() {
            return textEditor == null ? viewer.getContentComponent() : textEditor.getEditor().getContentComponent();
        }

        public Document getDocument() {
            return textEditor == null ? viewer.getDocument() : textEditor.getEditor().getDocument();
        }

        public void release() {
            if (viewer != null) {
                EditorFactory.getInstance().releaseEditor(viewer);
            }
            if (textEditor != null && textEditor.getEditor() != null) {
                EditorFactory.getInstance().releaseEditor(textEditor.getEditor());
            }
        }
    }
}