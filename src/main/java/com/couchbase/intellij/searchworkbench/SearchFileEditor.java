package com.couchbase.intellij.searchworkbench;


import com.couchbase.intellij.database.ActiveCluster;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class SearchFileEditor implements FileEditor, TextEditor {
    private final VirtualFile file;
    private final Project project;
    private final Map<Key<?>, Object> data = new HashMap<>();
    JPanel panel;
    private JComponent component;
    private JPanel topPanel;

    private boolean isExecutingQuery = false;
    private AnAction executeAction;
    private AnAction cancelAction;

    private Runnable newConnectionListener;

    private BlockingQueue<Boolean> queryExecutionChannel;
    private ComboBox bucketCombo;

    private ComboBox idxCombo;

    private String selectedBucket;

    private String selectedIdx;

    private TextEditor queryEditor;

    SearchFileEditor(Project project, VirtualFile file, String selectedBucket, String selectedIdx) {
        this.file = file;
        this.project = project;
        this.selectedBucket = selectedBucket;
        this.selectedIdx = selectedIdx;
        this.queryEditor = (TextEditor) TextEditorProvider.getInstance().createEditor(project, file);
        this.panel = new JPanel(new BorderLayout());
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

        buildToolbar();
        panel.add(queryEditor.getComponent(), BorderLayout.CENTER);
        queryEditor.getComponent().requestFocusInWindow();
        component = panel;
    }

    private void buildToolbar() {
        DefaultActionGroup executeGroup = new DefaultActionGroup();

        Icon executeIcon = IconLoader.getIcon("/assets/icons/play.svg", SearchFileEditor.class);
        Icon cancelIcon = IconLoader.getIcon("/assets/icons/cancel.svg", SearchFileEditor.class);

        executeAction = new AnAction("Execute", "Execute the query statement in the editor", executeIcon) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                if (!isSameConnection()) {
                    return;
                }

                queryExecutionChannel = new LinkedBlockingQueue<>(1);
                if (!isExecutingQuery) {
                    isExecutingQuery = true;

                    cancelAction = new AnAction("Cancel", "Cancel query execution", cancelIcon) {
                        @Override
                        public void actionPerformed(@NotNull AnActionEvent e) {
                            if (queryExecutionChannel.peek() == null) {
                                try {
                                    queryExecutionChannel.put(true);
                                } catch (InterruptedException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                            executeGroup.replaceAction(this, executeAction);
                            isExecutingQuery = false;
                        }
                    };

                    executeAction.registerCustomShortcutSet(CommonShortcuts.CTRL_ENTER, queryEditor.getComponent());
                    executeGroup.replaceAction(this, cancelAction);
                    isExecutingQuery = true;

                    ProgressManager.getInstance().run(new Task.Backgroundable(project, "Running SQL++ query", true) {
                        @Override
                        public void run(@NotNull ProgressIndicator indicator) {

                            ApplicationManager.getApplication().invokeLater(() -> {
                                boolean query = SearchQueryExecutor.executeQuery(queryExecutionChannel,
                                        bucketCombo.getSelectedItem() == null ? null : bucketCombo.getSelectedItem().toString(),
                                        idxCombo.getSelectedItem() == null ? null : idxCombo.getSelectedItem().toString(),
                                        queryEditor.getEditor().getDocument().getText(),
                                        project);
                                executeGroup.replaceAction(cancelAction, executeAction);
                                isExecutingQuery = false;
                            });
                        }
                    });
                }
            }
        };

        executeGroup.add(executeAction);

        ActionToolbar executeToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, executeGroup, true);
        executeToolbar.setTargetComponent(queryEditor.getComponent());

        JPanel contextPanel = new JPanel(new FlowLayout());
        JLabel conLabel = new JLabel("Bucket:");
        conLabel.setFont(conLabel.getFont().deriveFont(10.0f));
        contextPanel.add(conLabel);

        bucketCombo = new ComboBox<>();
        ActiveCluster.getInstance().get().buckets().getAllBuckets().keySet().forEach(bucketCombo::addItem);
        bucketCombo.setFont(bucketCombo.getFont().deriveFont(10f));
        Dimension maxSize = new Dimension(250, 29);
        bucketCombo.setMaximumSize(maxSize);
        contextPanel.add(bucketCombo);

        JLabel idxLabel = new JLabel("Index:");
        idxLabel.setFont(conLabel.getFont().deriveFont(10.0f));
        contextPanel.add(idxLabel);

        idxCombo = new ComboBox<>();


        if (selectedBucket != null && selectedIdx != null) {
            bucketCombo.setSelectedItem(selectedBucket);
            getIndexByBucket(selectedBucket).forEach(idxCombo::addItem);
            idxCombo.setSelectedItem(selectedIdx);
            idxCombo.setEnabled(true);

        } else {
            bucketCombo.setSelectedItem(null);
            idxCombo.setSelectedItem(null);
            idxCombo.setEnabled(false);
        }

        idxCombo.setFont(bucketCombo.getFont().deriveFont(10f));

        Dimension initialSize = new Dimension(250, 29);

        idxCombo.setMaximumSize(maxSize);
        idxCombo.setPreferredSize(initialSize);
        contextPanel.add(idxCombo);

        contextPanel.revalidate();
        topPanel = new JPanel(new BorderLayout());

        ActionListener bucketComboListener = e -> {
            idxCombo.removeAllItems();

            if (bucketCombo.getSelectedItem() == null) {
                idxCombo.setEnabled(false);
                return;
            }
            String bucketId = (String) bucketCombo.getSelectedItem();

            getIndexByBucket(bucketId).forEach(idxCombo::addItem);
            idxCombo.setSelectedItem(null);
            idxCombo.setEnabled(true);
        };
        bucketCombo.addActionListener(bucketComboListener);


        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(executeToolbar.getComponent(), BorderLayout.WEST);
        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(contextPanel, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);


        newConnectionListener = () -> {
            bucketCombo.removeAllItems();
            bucketCombo.removeActionListener(bucketComboListener);
            ActiveCluster.getInstance().get().buckets().getAllBuckets().keySet().forEach(bucketCombo::addItem);
            bucketCombo.setSelectedItem(null);
            bucketCombo.addActionListener(bucketComboListener);

            idxCombo.removeAllItems();
            idxCombo.setEnabled(false);
            bucketCombo.revalidate();
            idxCombo.revalidate();
        };
        ActiveCluster.getInstance().registerNewConnectionListener(newConnectionListener);
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return queryEditor.getComponent();
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

        if (newConnectionListener != null) {
            ActiveCluster.getInstance().deregisterNewConnectionListener(newConnectionListener);
        }

        if (queryEditor != null && queryEditor.getEditor() != null) {
            EditorFactory.getInstance().releaseEditor(queryEditor.getEditor());
        }
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

    private boolean isSameConnection() {
        if (ActiveCluster.getInstance().get() == null) {
            ApplicationManager.getApplication().invokeLater(() -> Messages.showErrorDialog("There is no active connection.", "Workbench Error"));
            return false;
        }
        return true;
    }


    @Override
    public @NotNull Editor getEditor() {
        return queryEditor.getEditor();
    }

    @Override
    public boolean canNavigateTo(@NotNull Navigatable navigatable) {
        return false;
    }

    @Override
    public void navigateTo(@NotNull Navigatable navigatable) {

    }

    private List<String> getIndexByBucket(String bucketName) {
        return ActiveCluster.getInstance().get().searchIndexes()
                .getAllIndexes().stream()
                .filter(e -> bucketName.equals(e.sourceName()))
                .map(e -> e.name())
                .sorted()
                .collect(Collectors.toList());
    }

}