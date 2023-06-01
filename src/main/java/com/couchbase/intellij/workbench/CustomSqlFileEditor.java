package com.couchbase.intellij.workbench;


import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.persistence.SavedCluster;
import com.couchbase.intellij.persistence.storage.ClustersStorage;
import com.couchbase.intellij.persistence.storage.QueryHistoryStorage;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.fileEditor.FileEditorStateLevel;
import com.intellij.openapi.fileEditor.impl.LoadTextUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.JBUI;
import org.intellij.sdk.language.SQLPPFormatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomSqlFileEditor implements FileEditor {
    public static final String NO_QUERY_CONTEXT_SELECTED = "No Query Context Selected";
    private final Editor queryEditor;
    private final VirtualFile file;
    private final Project project;
    private JLabel historyLabel;
    private JComponent component;

    private int currentHistoryIndex;

    private String selectedBucketContext;
    private String selectedScopeContext;
    private String cachedPreviousSelectedConnection;
    JPanel panel;

    CustomSqlFileEditor(Project project, VirtualFile file) {
        this.file = file;
        this.project = project;
        EditorFactory editorFactory = EditorFactory.getInstance();
        Document document = editorFactory.createDocument(LoadTextUtil.loadText(file));
        this.queryEditor = EditorFactory.getInstance().createEditor(document, project, file, false);
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
        DefaultActionGroup executeGroup = new DefaultActionGroup();

        Icon executeIcon = IconLoader.findIcon("./assets/icons/play.svg");
        executeGroup.add(new AnAction("Execute", "Execute the query statement in the editor", executeIcon) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                String editorText = queryEditor.getDocument().getText();
                if (QueryExecutor.executeQuery(editorText, selectedBucketContext, selectedScopeContext,
                        currentHistoryIndex, project)) {
                    int historySize = QueryHistoryStorage.getInstance().getValue().getHistory().size();
                    currentHistoryIndex = historySize - 1;
                    SwingUtilities.invokeLater(() -> {
                        historyLabel.setText("history (" + historySize + "/" + historySize + ")");
                        historyLabel.revalidate();
                    });
                }
            }
        });

        executeGroup.addSeparator();
        Icon favoriteList = IconLoader.findIcon("./assets/icons/favorites-list.svg");
        executeGroup.add(new AnAction("Favorite List", "List of favorite queries", favoriteList) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                FavoriteQueryDialog dialog = new FavoriteQueryDialog(queryEditor);
                dialog.show();
            }
        });

        executeGroup.addSeparator();
        Icon formatCode = IconLoader.findIcon("./assets/icons/format.svg");
        executeGroup.add(new AnAction("Format Code", "Formats a SQL++ code", formatCode) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                        queryEditor.getDocument().setText(SQLPPFormatter.format(queryEditor.getDocument().getText()));
                    }
                });
            }
        });
        executeGroup.addSeparator();

        ActionToolbar executeToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, executeGroup, true);
        executeToolbar.setTargetComponent(queryEditor.getComponent());

        Icon leftIcon = IconLoader.findIcon("./assets/icons/chevron-left.svg");
        Icon rightIcon = IconLoader.findIcon("./assets/icons/chevron-right.svg");

        DefaultActionGroup prevActionGroup = new DefaultActionGroup();
        prevActionGroup.add(new AnAction("Previous History", "Previous history", leftIcon) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                if (currentHistoryIndex - 1 >= 0) {
                    currentHistoryIndex--;
                    ApplicationManager.getApplication().runWriteAction(new Runnable() {
                        @Override
                        public void run() {
                            queryEditor.getDocument().setText(QueryHistoryStorage.getInstance().getValue().getHistory().get(currentHistoryIndex));
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
        nextActionGroup.add(new AnAction("Next History", "Next history", rightIcon) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                if (currentHistoryIndex + 1 < QueryHistoryStorage.getInstance().getValue().getHistory().size()) {
                    currentHistoryIndex++;

                    ApplicationManager.getApplication().runWriteAction(new Runnable() {
                        @Override
                        public void run() {
                            queryEditor.getDocument().setText(QueryHistoryStorage.getInstance().getValue().getHistory().get(currentHistoryIndex));
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
        historyLabel.setFont(historyLabel.getFont().deriveFont(10.0f));
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
                NewFavoriteCatalog dialog = new NewFavoriteCatalog(queryEditor, this, favoriteActionGroup, favToolbar);
                dialog.show();
            }
        });
        favorite.add(favToolbar.getComponent(), BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(historyPanel, BorderLayout.CENTER);
        rightPanel.add(favorite, BorderLayout.EAST);


        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(executeToolbar.getComponent(), BorderLayout.WEST);
        leftPanel.add(getQueryContextPanel(), BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(queryEditor.getComponent(), BorderLayout.CENTER);
        queryEditor.getContentComponent().requestFocusInWindow();
        component = panel;
    }


    private JPanel getQueryContextPanel() {
        JPanel contextPanel = new JPanel(new FlowLayout());
        JLabel conLabel = new JLabel("Connection:");
        conLabel.setFont(conLabel.getFont().deriveFont(10.0f));
        contextPanel.add(conLabel);

        DefaultActionGroup option1Group = new DefaultActionGroup("Set Query Context", true);
        option1Group.getTemplatePresentation().setIcon(IconLoader.findIcon("./assets/icons/query_context.svg"));
        DefaultActionGroup option1Action = new DefaultActionGroup(option1Group) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                ActionManager.getInstance().createActionPopupMenu("Set Query Context", this).getComponent().show(e.getInputEvent().getComponent(), e.getInputEvent().getComponent().getWidth(), 0);
            }
        };

        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("QueryContext", option1Action, true);
        actionToolbar.setTargetComponent(contextPanel);
        JLabel contextLabel = new JLabel(NO_QUERY_CONTEXT_SELECTED);
        contextLabel.setFont(conLabel.getFont().deriveFont(10.0f));

        Map<String, SavedCluster> clusters = ClustersStorage.getInstance().getValue().getMap();

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setFont(comboBox.getFont().deriveFont(10f));
        Dimension maxSize = new Dimension(200, 20);
        comboBox.setMaximumSize(maxSize);

        for (Map.Entry<String, SavedCluster> entry : clusters.entrySet()) {
            comboBox.addItem(entry.getValue().getId());
        }
        contextPanel.add(comboBox);
        comboBox.addActionListener(e -> {
            String selectedClusterId = (String) comboBox.getSelectedItem();

            if (selectedClusterId == null) {
                return;
            }

            option1Group.removeAll();
            AnAction clearContextAction = new AnAction("Clear Context") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    contextLabel.setText(NO_QUERY_CONTEXT_SELECTED);
                    contextLabel.revalidate();
                    selectedBucketContext = null;
                    selectedScopeContext = null;
                }
            };

            option1Group.add(clearContextAction);
            option1Group.addSeparator("Buckets");

            List<String> buckets = new ArrayList<>(ActiveCluster.getInstance().get().buckets().getAllBuckets().keySet());
            for (String bucket : buckets) {

                DefaultActionGroup bucketsGroup = new DefaultActionGroup(bucket, true);
                bucketsGroup.addSeparator("Scopes");

                List<ScopeSpec> scopes = ActiveCluster.getInstance().get().bucket(bucket).collections().getAllScopes();
                for (ScopeSpec spec : scopes) {
                    AnAction scopeAction = new AnAction(spec.name()) {
                        @Override
                        public void actionPerformed(@NotNull AnActionEvent e) {
                            contextLabel.setText(bucket + " > " + spec.name());
                            contextLabel.revalidate();
                            selectedBucketContext = bucket;
                            selectedScopeContext = spec.name();
                        }
                    };

                    bucketsGroup.add(scopeAction);
                }

                option1Group.add(bucketsGroup);
            }
        });

        comboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String item = (String) e.getItem();

                if (item == null) {
                    return;
                }
                if (ActiveCluster.getInstance().get() == null || !item.equals(ActiveCluster.getInstance().getId())) {
                    ApplicationManager.getApplication().invokeLater(() -> Messages.showErrorDialog("You can't select a cluster that you are not connected to", "Workbench Error"));

                    SwingUtilities.invokeLater(() -> comboBox
                            .setSelectedItem(cachedPreviousSelectedConnection));
                } else {
                    SwingUtilities.invokeLater(() -> {
                        contextLabel.setText(NO_QUERY_CONTEXT_SELECTED);
                        contextLabel.revalidate();
                    });
                    selectedBucketContext = null;
                    selectedScopeContext = null;
                    cachedPreviousSelectedConnection = e.getItem().toString();
                }
            }
        });

        if (ActiveCluster.getInstance().get() == null) {
            comboBox.setSelectedItem(null);
            cachedPreviousSelectedConnection = null;
        } else {
            comboBox.setSelectedItem(ActiveCluster.getInstance().getId());
            cachedPreviousSelectedConnection = ActiveCluster.getInstance().getId();
        }


        JPanel toolbarPanel = new JPanel(new BorderLayout());
        toolbarPanel.add(actionToolbar.getComponent(), BorderLayout.CENTER);
        toolbarPanel.setBorder(JBUI.Borders.emptyRight(-12)); // Adjust these values as per your requirement

        JPanel myPanel = new JPanel();
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.X_AXIS));
        myPanel.add(toolbarPanel);
        myPanel.add(Box.createRigidArea(new Dimension(0, 0))); // Adjust the value 5 as per your requirement
        myPanel.add(contextLabel);
        myPanel.setBorder(JBUI.Borders.emptyLeft(10));

        contextPanel.add(myPanel);

        return contextPanel;
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
        EditorFactory.getInstance().releaseEditor(queryEditor);
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