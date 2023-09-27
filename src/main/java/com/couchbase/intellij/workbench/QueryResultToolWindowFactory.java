package com.couchbase.intellij.workbench;


import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.workbench.error.CouchbaseQueryResultError;
import com.couchbase.intellij.workbench.explain.HtmlPanel;
import com.couchbase.intellij.workbench.result.JsonTableModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.ui.AnimatedIcon;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.table.JBTable;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class QueryResultToolWindowFactory implements ToolWindowFactory {
    private static final String rttToolTip = "Round-Trip Time (RTT) is the total time taken to send a request and receive a response from the server";
    private static final String elapsedToolTip = "Elapsed is the time taken by the server to process the request";
    private static final String executionTooltip = "Execution is the time taken by the server to execute the query";
    private static final String mutationsToolTip = "Count of documents mutated";
    private static final String docsTooltip = "Count of documents returned";
    private static final String docsSizeTooltip = "Total size of documents returned";
    public static QueryResultToolWindowFactory instance;
    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final String[] headers = {"RTT", "ELAPSED", "EXECUTION", "MUTATIONS", "DOCS", "SIZE"};
    private final String[] tooltips = {rttToolTip, elapsedToolTip, executionTooltip, mutationsToolTip, docsTooltip, docsSizeTooltip};
    private HtmlPanel htmlPanel;
    private JsonTableModel model;
    private JLabel statusIcon;
    private EditorEx editor;
    private List<JLabel> queryStatsList;
    private List<JLabel> queryLabelsList;
    private List<Map<String, Object>> cachedResults;
    private JPanel queryStatsPanel;
    private Project project;

    public QueryResultToolWindowFactory() {
        instance = this;
    }

    @NotNull
    private static String getEmptyExplain() {
        return "<html><body style=\" background: #3c3f41\"><span style='color:#ccc'>Nothing to show</span></body></html>";
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        this.project = project;
        JBTabs tabs = new JBTabsImpl(project);
        model = new JsonTableModel();
        JBTable table = new JBTable(model);
        table.setCellSelectionEnabled(true);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(true);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        JPanel resultStats = new JPanel();
        resultStats.setLayout(new BorderLayout());

        statusIcon = new JLabel();
        resultStats.add(statusIcon, BorderLayout.WEST);

        queryStatsPanel = new JPanel(new FlowLayout());

        queryStatsList = new ArrayList<>();
        queryLabelsList = new ArrayList<>();

        for (int i = 0; i < headers.length; i++) {

            JLabel title = new JLabel(getQueryStatHeader(headers[i]));
            title.setToolTipText(tooltips[i]);
            queryStatsPanel.add(title);
            queryLabelsList.add(title);

            JLabel valueLabel = new JLabel("<html><strong>-</strong></html>");
            valueLabel.setToolTipText(tooltips[i]);
            valueLabel.setBorder(JBUI.Borders.emptyRight(10));
            queryStatsPanel.add(valueLabel);
            queryStatsList.add(valueLabel);
        }

        queryStatsPanel.setVisible(false);
        resultStats.add(queryStatsPanel, BorderLayout.CENTER);
        resultStats.setBorder(JBUI.Borders.emptyLeft(10));

        topPanel.add(resultStats, BorderLayout.WEST);

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem jsonMenuItem = new JMenuItem("JSON");
        JMenuItem csvMenuItem = new JMenuItem("CSV");
        JMenuItem sqlppUpsertMenuItem = new JMenuItem("SQL++ UPSERT");
        JMenuItem sqlppInsertMenuItem = new JMenuItem("SQL++ INSERT");
        JMenuItem sqlppUpdateMenuItem = new JMenuItem("SQL++ UPDATE");
        popupMenu.add(csvMenuItem);
        popupMenu.add(jsonMenuItem);
        popupMenu.add(sqlppUpsertMenuItem);
        popupMenu.add(sqlppInsertMenuItem);
        popupMenu.add(sqlppUpdateMenuItem);

        csvMenuItem.addActionListener(actionEvent -> FileExporter.exportResultToCSV(project, model.tableModelToCSV()));
        jsonMenuItem.addActionListener(actionEvent -> FileExporter.exportResultToJson(project, gson.toJson(cachedResults)));
        addSQLPPMenuItemListener(sqlppUpsertMenuItem, "UPSERT", model::convertToSQLPPUpsert);
        addSQLPPMenuItemListener(sqlppInsertMenuItem, "INSERT", model::convertToSQLPPInsert);
        addSQLPPMenuItemListener(sqlppUpdateMenuItem, "UPDATE", model::convertToSQLPPUpdate);

        DefaultActionGroup executeGroup = new DefaultActionGroup();
        Icon executeIcon = IconLoader.getIcon("/assets/icons/export.svg", QueryResultToolWindowFactory.class);
        executeGroup.add(new AnAction("Export", "Export", executeIcon) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                Component component = e.getInputEvent().getComponent();
                popupMenu.show(component, -10, component.getHeight());
            }
        });

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("QueryResultToolbar", executeGroup, true);
        toolbar.getComponent().setBorder(JBUI.Borders.emptyRight(10));
        toolbar.setTargetComponent(topPanel);

        topPanel.add(toolbar.getComponent(), BorderLayout.EAST);


        VirtualFile virtualFile = new LightVirtualFile("query_result", FileTypeManager.getInstance().getFileTypeByExtension("json"),
                "{\n\"No data to display\": \"Hit 'execute' in the query editor to run a statement.\"\n}");
        Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
        editor = (EditorEx) EditorFactory.getInstance().createEditor(document, project, JsonFileType.INSTANCE, true);
        EditorSettings editorSettings = editor.getSettings();
        editorSettings.setLineNumbersShown(true);
        editorSettings.setFoldingOutlineShown(true);
        editorSettings.setAutoCodeFoldingEnabled(true);
        editorSettings.setIndentGuidesShown(true);


        htmlPanel = new HtmlPanel();
        htmlPanel.loadHTML(getEmptyExplain());

        JPanel explainPanel = new JPanel(new BorderLayout());
        explainPanel.add(htmlPanel, BorderLayout.CENTER);
        TabInfo explainTab = new TabInfo(explainPanel).setText("Explain");


        JBTabs resultTabs = new JBTabsImpl(project);
        resultTabs.addTab(new TabInfo(editor.getComponent()).setText("JSON"));
        resultTabs.addTab(new TabInfo(new JBScrollPane(table)).setText("Table"));
        resultTabs.addTab(explainTab);


        JPanel queryResultPanel = new JPanel();
        queryResultPanel.setLayout(new BorderLayout());
        queryResultPanel.add(topPanel, BorderLayout.NORTH);
        queryResultPanel.add(resultTabs.getComponent(), BorderLayout.CENTER);


        TabInfo outputTab = new TabInfo(getConsolePanel()).setText("Log");
        TabInfo queryResultTab = new TabInfo(queryResultPanel).setText("Query Result");


        tabs.addTab(outputTab);
        tabs.addTab(queryResultTab);
        tabs.select(queryResultTab, true);


        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(tabs.getComponent(), "", false);
        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.addContent(content);

        ActiveCluster.getInstance().registerNewConnectionListener(() -> {

            SwingUtilities.invokeLater(() -> {
                if (!ActiveCluster.getInstance().hasQueryService()) {
                    queryResultTab.setEnabled(false);
                    queryResultTab.revalidate();
                } else {
                    queryResultTab.setEnabled(true);
                    queryResultTab.revalidate();
                }
            });
        });
    }

    private void addSQLPPMenuItemListener(JMenuItem menuItem, String title, Supplier<String> sqlppSupplier) {
        menuItem.addActionListener(actionEvent -> {
            String sqlppContent = sqlppSupplier.get();
            if (sqlppContent.startsWith("The query result should")) {
                Messages.showErrorDialog(sqlppContent, "Error Exporting to SQL++ " + title);
            } else {
                FileExporter.exportResultToSQLPP(project, sqlppContent);
            }
        });
    }

    public JPanel getConsolePanel() {
        ConsoleView console = Log.getLogger();

        DefaultActionGroup actionGroup = new DefaultActionGroup();
        AnAction clearAction = new AnAction("Clear Log") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                console.clear();
            }
        };
        clearAction.getTemplatePresentation().setIcon(
                IconLoader.getIcon("/assets/icons/clear.svg", QueryResultToolWindowFactory.class));
        actionGroup.add(clearAction);

        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("ConsoleToolbar", actionGroup, true);
        actionToolbar.setLayoutPolicy(ActionToolbar.WRAP_LAYOUT_POLICY);
        JPanel actionPanel = new JPanel(new FlowLayout());
        actionPanel.add(actionToolbar.getComponent());

        actionToolbar.setTargetComponent(actionPanel);


        String[] options = {"Error", "Info", "Debug"};
        ComboBox<String> comboBox = new ComboBox<>(options);
        comboBox.setSelectedItem("Info");
        comboBox.addActionListener(e -> {
            String selectedValue = (String) comboBox.getSelectedItem();
            if ("Error".equals(selectedValue)) {
                Log.setLevel(1);
            } else if ("Info".equals(selectedValue)) {
                Log.setLevel(2);
            } else {
                Log.setLevel(3);
            }
        });

        JPanel comboboxPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        comboboxPanel.add(new JLabel("Log Level:"));
        comboboxPanel.add(comboBox);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(actionPanel, BorderLayout.LINE_END);
        panel.add(comboboxPanel, BorderLayout.CENTER);

        JPanel consolePanel = new JPanel(new BorderLayout());
        consolePanel.add(panel, BorderLayout.NORTH);
        consolePanel.add(console.getComponent(), BorderLayout.CENTER);

        return consolePanel;
    }

    private String getQueryStatHeader(String title) {
        return "<html><strong><small>" + title + ":</small></strong></html>";
    }

    private String getQueryStatResult(String title) {
        return "<html><small>" + title + "</small></html>";
    }

    public Map<String, Object> jsonObjectToMap(JsonObject jsonObject) {
        Map<String, Object> map = new HashMap<>();

        for (String key : jsonObject.getNames()) {
            Object value = jsonObject.get(key);
            if (value instanceof JsonObject) {
                map.put(key, jsonObjectToMap((JsonObject) value));
            } else if (value instanceof JsonArray) {
                map.put(key, jsonArrayToList((JsonArray) value));
            } else {
                map.put(key, value);
            }
        }

        return map;
    }

    public List<Object> jsonArrayToList(JsonArray jsonArray) {
        List<Object> list = new ArrayList<>();

        for (Object element : jsonArray) {
            if (element instanceof JsonObject) {
                list.add(jsonObjectToMap((JsonObject) element));
            } else if (element instanceof JsonArray) {
                list.add(jsonArrayToList((JsonArray) element));
            } else {
                list.add(element);
            }
        }

        return list;
    }

    public void updateQueryStats(List<String> queryValues, List<JsonObject> results, CouchbaseQueryResultError error, List<String> explain) {
        SwingUtilities.invokeLater(() -> {
            if (results != null && error == null || error.getErrors().isEmpty()) {

                List<Map<String, Object>> convertedResults = new ArrayList<>();
                for (JsonObject jsonObject : results) {
                    convertedResults.add(jsonObjectToMap(jsonObject));
                }

                queryStatsPanel.setVisible(true);
                cachedResults = convertedResults;

                statusIcon.setIcon(IconLoader.getIcon("/assets/icons/check_mark_big.svg", QueryResultToolWindowFactory.class));
                ApplicationManager.getApplication().runWriteAction(() -> {
                    editor.getDocument().setText(gson.toJson(convertedResults));
                });

                String content = (explain == null ? getEmptyExplain() : ExplainContent.getContent(explain));
                htmlPanel.loadHTML(content);

                queryStatsPanel.revalidate();

                for (int i = 0; i < queryStatsList.size(); i++) {
                    String v = queryValues.get(i);
                    if (v == null || v.trim().isEmpty() || "-".equals(v) || "0".equals(v)) {
                        queryLabelsList.get(i).setVisible(false);
                        queryStatsList.get(i).setVisible(false);
                    } else {
                        queryLabelsList.get(i).setVisible(true);
                        queryStatsList.get(i).setVisible(true);
                        queryStatsList.get(i).setText(getQueryStatResult(queryValues.get(i)));
                    }
                }

                model.updateData(convertedResults);

            } else {
                cachedResults = null;
                statusIcon.setIcon(IconLoader.getIcon("/assets/icons/warning-circle-big.svg", QueryResultToolWindowFactory.class));
                ApplicationManager.getApplication().runWriteAction(() -> editor.getDocument().setText(gson.toJson(error.getErrors())));
            }
        });
    }

    public void setStatusAsLoading() {
        SwingUtilities.invokeLater(() -> {
            for (JLabel label : queryStatsList) {
                label.setText("-");
            }
            statusIcon.setIcon(new AnimatedIcon.Default());

            ApplicationManager.getApplication().runWriteAction(() -> editor.getDocument().setText("{ \"status\": \"Executing Statement\"}"));
        });
    }
}