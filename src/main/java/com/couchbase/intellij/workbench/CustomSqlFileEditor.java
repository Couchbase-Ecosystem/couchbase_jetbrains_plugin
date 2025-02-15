package com.couchbase.intellij.workbench;


import com.couchbase.intellij.VirtualFileKeys;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.DataLoader;
import com.couchbase.intellij.database.QueryContext;
import com.couchbase.intellij.persistence.SavedCluster;
import com.couchbase.intellij.persistence.storage.ClustersStorage;
import com.couchbase.intellij.persistence.storage.QueryHistoryStorage;
import com.couchbase.intellij.tree.node.FileNodeDescriptor;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.fileEditor.impl.LoadTextUtil;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.RefreshQueue;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import generated.psi.Statement;
import org.intellij.sdk.language.SQLPPFormatter;
import org.intellij.sdk.language.psi.SqlppFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.couchbase.intellij.workbench.QueryExecutor.QueryType.*;

public class CustomSqlFileEditor implements FileEditor, TextEditor {
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

    private boolean isExecutingQuery = false;
    private AnAction executeAction;
    private AnAction cancelAction;

    private BlockingQueue<Boolean> queryExecutionChannel;
    private BlockingQueue<Boolean> scriptExecutionChannel;

    CustomSqlFileEditor(Project project, VirtualFile file) {
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

    public static void openDocument(Project project, String bucket, String scope, String collection, String id, @Nullable Tree tree) {
        String fileName = id + ".json";
        FileNodeDescriptor descriptor = new FileNodeDescriptor(fileName, bucket, scope, collection, id, FileNodeDescriptor.FileType.JSON, null);
        DataLoader.loadDocument(project, descriptor, tree);

        VirtualFile virtualFile = descriptor.getVirtualFile();
        if (virtualFile != null) {
            FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
            fileEditorManager.openFile(virtualFile, true);
        } else {
            Log.debug("virtual file is null");
        }
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

        Icon executeIcon = IconLoader.getIcon("/assets/icons/play.svg", CustomSqlFileEditor.class);
        Icon cancelIcon = IconLoader.getIcon("/assets/icons/cancel.svg", CustomSqlFileEditor.class);

        executeAction = new AnAction("Execute", "Execute the query statement in the editor", executeIcon) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                if (!isSameConnection()) {
                    return;
                }

                queryExecutionChannel = new LinkedBlockingQueue<>(1);
                scriptExecutionChannel = new LinkedBlockingQueue<>(1);

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
                            if (scriptExecutionChannel.peek() == null) {
                                try {
                                    scriptExecutionChannel.put(true);
                                } catch (InterruptedException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }

                            executeGroup.replaceAction(this, executeAction);
                            isExecutingQuery = false;
                        }
                    };

                    executeGroup.replaceAction(this, cancelAction);
                    isExecutingQuery = true;

                    List<String> statements = getStatements();

                    ProgressManager.getInstance().run(new Task.Backgroundable(project, "Running SQL++ query", true) {
                        @Override
                        public void run(@NotNull ProgressIndicator indicator) {

                            ApplicationManager.getApplication().invokeLater(() -> {
                                boolean query = false;
                                boolean script = false;
                                QueryContext context = ActiveCluster.getInstance().getQueryContext().getValue();
                                if (statements.size() == 0) {
                                    return;
                                } else if (statements.size() == 1) {
                                    query = QueryExecutor.executeQuery(queryExecutionChannel, NORMAL, context, statements.get(0), currentHistoryIndex, project);
                                } else {
                                    script = QueryExecutor.executeScript(scriptExecutionChannel, NORMAL, context, statements, currentHistoryIndex, project);

                                }

                                try {
                                    if (query || script) {
                                        int historySize = QueryHistoryStorage.getInstance().getValue().getHistory().size();
                                        currentHistoryIndex = historySize - 1;
                                        ApplicationManager.getApplication().invokeLater(() -> {
                                            historyLabel.setText("history (" + historySize + "/" + historySize + ")");
                                            historyLabel.revalidate();
                                        });
                                    }
                                } catch (Exception ex) {
                                    throw new RuntimeException(ex);
                                }

                                executeGroup.replaceAction(cancelAction, executeAction);
                                isExecutingQuery = false;
                            });
                        }
                    });
                }
            }
        };

        KeyStroke keyStroke;
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            keyStroke = KeyStroke.getKeyStroke("meta ENTER");
        } else {
            keyStroke = KeyStroke.getKeyStroke("control ENTER");
        }
        CustomShortcutSet shortcutSet = new CustomShortcutSet(keyStroke);
        executeAction.registerCustomShortcutSet(shortcutSet, queryEditor.getComponent());
        executeGroup.add(executeAction);

        executeGroup.addSeparator();

        Icon adviseIcon = IconLoader.getIcon("/assets/icons/advise.svg", CustomSqlFileEditor.class);
        executeGroup.add(new AnAction("Advise", "Get index recommendations about focused query", adviseIcon) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    if (!isSameConnection()) {
                        return;
                    }
                    String statement = getFocusedStatement();

                    QueryExecutor.executeQuery(queryExecutionChannel, ADVISE, ActiveCluster.getInstance().getQueryContext().getValue(), statement, -1, project);
                });
            }
        });

        Icon explainIcon = IconLoader.getIcon("/assets/icons/explain.svg", CustomSqlFileEditor.class);
        executeGroup.add(new AnAction("Explain", "Explains query phases for focused statement", explainIcon) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    if (!isSameConnection()) {
                        return;
                    }
                    String statement = getFocusedStatement();
                    QueryExecutor.executeQuery(queryExecutionChannel, EXPLAIN, ActiveCluster.getInstance().getQueryContext().getValue(), statement, -1, project);
                });
            }
        });


        executeGroup.addSeparator();


        Icon favoriteList = IconLoader.getIcon("/assets/icons/favorites-list.svg", CustomSqlFileEditor.class);
        executeGroup.add(new AnAction("Favorite List", "List of favorite queries", favoriteList) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                FavoriteQueryDialog dialog = new FavoriteQueryDialog(project, queryEditor.getDocument());
                dialog.show();
            }
        });

        executeGroup.addSeparator();
        Icon formatCode = IconLoader.getIcon("/assets/icons/format.svg", CustomSqlFileEditor.class);
        executeGroup.add(new AnAction("Format Code", "Formats a SQL++ code", formatCode) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                ApplicationManager.getApplication().runWriteAction(() -> queryEditor.getDocument().setText(SQLPPFormatter.format(queryEditor.getDocument().getText())));
            }
        });
        executeGroup.addSeparator();

        ActionToolbar executeToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, executeGroup, true);
        executeToolbar.setTargetComponent(queryEditor.getComponent());

        Icon leftIcon = IconLoader.getIcon("/assets/icons/chevron-left.svg", CustomSqlFileEditor.class);
        Icon rightIcon = IconLoader.getIcon("/assets/icons/chevron-right.svg", CustomSqlFileEditor.class);

        DefaultActionGroup prevActionGroup = new DefaultActionGroup();
        prevActionGroup.add(new AnAction("Previous History", "Previous history", leftIcon) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                if (currentHistoryIndex - 1 >= 0) {
                    currentHistoryIndex--;
                    ApplicationManager.getApplication().runWriteAction(() -> queryEditor.getDocument().setText(QueryHistoryStorage.getInstance().getValue().getHistory().get(currentHistoryIndex)));


                    ApplicationManager.getApplication().invokeLater(() -> {
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

                    ApplicationManager.getApplication().runWriteAction(() -> queryEditor.getDocument().setText(QueryHistoryStorage.getInstance().getValue().getHistory().get(currentHistoryIndex)));

                    ApplicationManager.getApplication().invokeLater(() -> {
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
        currentHistoryIndex = Math.max((historySize - 1), 0) + 1;
        historyLabel = new JLabel("history (" + (historySize + 1) + "/" + historySize + ")");
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

        favoriteActionGroup.add(new AnAction("Named Parameters", "Named parameters",
                IconLoader.getIcon("/assets/icons/named_param.svg", CustomSqlFileEditor.class)) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                NamedParameterDialog dialog = new NamedParameterDialog(project);
                dialog.show();
            }
        });

        favoriteActionGroup.add(new AnAction("Favorite Query", "Favorite query", IconLoader.getIcon("/assets/icons/star-empty.svg", CustomSqlFileEditor.class)) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                NewFavoriteCatalog dialog = new NewFavoriteCatalog(project, queryEditor.getDocument(), this, favoriteActionGroup, favToolbar);
                dialog.show();
            }
        });

        favoriteActionGroup.addSeparator();
        favoriteActionGroup.add(new AnAction("Query Options", "Query options", IconLoader.getIcon("/assets/icons/gear.svg", CustomSqlFileEditor.class)) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                if (ActiveCluster.getInstance().getCluster() != null) {
                    QueryOptionsDialog dialog = new QueryOptionsDialog(project);
                    dialog.show();
                } else {
                    Messages.showErrorDialog(
                            String.format("You can only change the query options when you are connected to a cluster"),
                            "Query Options Error"
                    );
                }
            }
        });

        favorite.add(favToolbar.getComponent(), BorderLayout.CENTER);

        topPanel = new JPanel(new BorderLayout());

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(historyPanel, BorderLayout.CENTER);
        rightPanel.add(favorite, BorderLayout.EAST);


        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(executeToolbar.getComponent(), BorderLayout.WEST);
        leftPanel.add(getQueryContextPanel(), BorderLayout.CENTER);

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

    private String normalizeStatement(Statement statement) {
        String text = statement.getText();
        return text;
    }

    private String getFocusedStatement() {
        SelectionModel selectionModel = queryEditor.textEditor.getEditor().getSelectionModel();

        if (selectionModel.hasSelection()) {
            return selectionModel.getSelectedText();
        }


        PsiFile psi = PsiManager.getInstance(project).findFile(getFile());
        PsiErrorElement err = PsiTreeUtil.findChildOfType(psi, PsiErrorElement.class);
        if (err != null) {
            return queryEditor.getDocument().getText();
        }
        return ReadAction.compute(() -> {
            PsiElement focused = psi.findElementAt(queryEditor.textEditor.getEditor().getCaretModel().getOffset());
            if (focused != null) {
                return normalizeStatement(PsiTreeUtil.getTopmostParentOfType(focused, Statement.class));
            }
            return null;
        });
    }

    private List<String> getStatements() {
        SelectionModel selectionModel = queryEditor.textEditor.getEditor().getSelectionModel();

        if (selectionModel.hasSelection()) {
            return Collections.singletonList(selectionModel.getSelectedText());
        }


        PsiFile psi = PsiManager.getInstance(project).findFile(getFile());
        PsiErrorElement err = PsiTreeUtil.findChildOfType(psi, PsiErrorElement.class);
        if (err != null) {
            return List.of(queryEditor.getDocument().getText());
        }
        return ReadAction.compute(() -> PsiTreeUtil.getChildrenOfAnyType(psi, Statement.class).stream()
                .map(this::normalizeStatement)
                .collect(Collectors.toList())
        );
    }

    private JPanel getQueryContextPanel() {
        JPanel contextPanel = new JPanel(new FlowLayout());

        DefaultActionGroup option1Group = new DefaultActionGroup("Set Query Context", true);
        option1Group.getTemplatePresentation().setIcon(IconLoader.getIcon("/assets/icons/query_context.svg", CustomSqlFileEditor.class));
        DefaultActionGroup option1Action = new DefaultActionGroup(option1Group) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                ActionManager.getInstance().createActionPopupMenu("Set Query Context", this).getComponent().show(e.getInputEvent().getComponent(), e.getInputEvent().getComponent().getWidth(), 0);
            }
        };

        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("QueryContext", option1Action, true);
        actionToolbar.setTargetComponent(contextPanel);
        JLabel contextLabel = new JLabel(NO_QUERY_CONTEXT_SELECTED);
        contextLabel.setFont(contextLabel.getFont().deriveFont(10.0f));

        Map<String, SavedCluster> clusters = ClustersStorage.getInstance().getValue().getMap();

        this.createContextSelector(option1Group, contextLabel);

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

    private void createContextSelector(DefaultActionGroup option1Group, JLabel contextLabel) {
        contextLabel.setText(NO_QUERY_CONTEXT_SELECTED);
        contextLabel.revalidate();

        if (ActiveCluster.getInstance().getColor() != null) {
            Border line = BorderFactory.createMatteBorder(0, 0, 1, 0, ActiveCluster.getInstance().getColor());
            Border margin = BorderFactory.createEmptyBorder(0, 0, 1, 0); // Top, left, bottom, right margins
            Border compound = BorderFactory.createCompoundBorder(margin, line);
            topPanel.setBorder(compound);
            topPanel.revalidate();
        } else {
            topPanel.setBorder(JBUI.Borders.empty());
            topPanel.revalidate();
        }

        option1Group.removeAll();
        AnAction clearContextAction = new AnAction("Clear Context") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                contextLabel.setText(NO_QUERY_CONTEXT_SELECTED);
                contextLabel.revalidate();
                setSelectedContext(null);
            }
        };

        option1Group.add(clearContextAction);
        option1Group.addSeparator("Buckets");


        ActiveCluster.subscribe(activeCluster -> {
            activeCluster.getQueryContext().subscribe(this, queryContext -> {
                ApplicationManager.getApplication().invokeLater(() -> {
                    if (queryContext.isPresent()) {
                        contextLabel.setText(String.format("%s > %s", queryContext.get().getBucket(), queryContext.get().getScope()));
                        contextLabel.revalidate();
                    } else {
                        contextLabel.setText(NO_QUERY_CONTEXT_SELECTED);
                        contextLabel.revalidate();
                    }
                    PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                    if (psiFile instanceof SqlppFile) {
                        ((SqlppFile) psiFile).setClusterContext(queryContext.orElse(null));
                    }
                });
                return true;
            });
            activeCluster.getChildren().stream()
                    .sorted(Comparator.comparing(b -> b.getName().toLowerCase()))
                    .forEach(bucket -> {
                        DefaultActionGroup bucketsGroup = new DefaultActionGroup(bucket.getName(), true);
                        bucketsGroup.addSeparator("Scopes");

                        bucket.getChildren().stream()
                                .sorted(Comparator.comparing(s -> s.getName().toLowerCase()))
                                .forEach(scope -> {

                                    AnAction scopeAction = new AnAction(scope.getName()) {
                                        @Override
                                        public void actionPerformed(@NotNull AnActionEvent e) {
                                            contextLabel.setText(bucket.getName() + " > " + scope.getName());
                                            contextLabel.revalidate();
                                            setSelectedContext(new QueryContext(bucket.getName(), scope.getName()));
                                        }
                                    };

                                    bucketsGroup.add(scopeAction);
                                });
                        option1Group.add(bucketsGroup);
                    });
        });
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

    private boolean isSameConnection() {
        if (ActiveCluster.getInstance().get() == null) {
            ApplicationManager.getApplication().invokeLater(() -> Messages.showErrorDialog("There is no active connection.", "Workbench Error"));
            return false;
        }
        return true;
    }

    /**
     * Does not update the UI
     * Used in testing only
     * * @param context
     */
    public void setSelectedContext(QueryContext context) {
        ActiveCluster.getInstance().setQueryContext(context);
        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
        if (psiFile instanceof SqlppFile) {
            ((SqlppFile) psiFile).setClusterContext(context);
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

    public interface FocusHandler extends BiConsumer<FocusEvent, Boolean> {

    }

    class EditorWrapper implements FocusListener {
        private final Editor viewer;
        private final TextEditor textEditor;
        private final List<FocusHandler> focusListeners = new ArrayList<>();

        public EditorWrapper(Editor viewer, TextEditor textEditor) {
            this.textEditor = textEditor;
            this.viewer = viewer;
            getContentComponent().addFocusListener(this);
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

        @Override
        public void focusGained(FocusEvent e) {

        }

        @Override
        public void focusLost(FocusEvent e) {

        }
    }
}