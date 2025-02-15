package com.couchbase.intellij.workbench;


import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.workbench.chart.ChartsPanel;
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
import com.intellij.ui.tabs.JBTabsFactory;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.TabsListener;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
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
    private static final String[] QUERY_LIMITS = {"200", "500", "1000", "No Limit"};
    public static QueryResultToolWindowFactory instance;
    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static String latestExplain = null;
    private final String[] headers = {"RTT", "ELAPSED", "EXECUTION", "MUTATIONS", "DOCS", "SIZE"};
    private final String[] tooltips = {rttToolTip, elapsedToolTip, executionTooltip, mutationsToolTip, docsTooltip, docsSizeTooltip};
    private HtmlPanel htmlPanel;
    private JsonTableModel model;
    private JLabel statusIcon;
    private EditorEx editor;
    private List<JLabel> queryStatsList;
    private List<JLabel> queryLabelsList;
    private JPanel queryStatsPanel;
    private Project project;
    private JPopupMenu popupMenu;

    private ChartsPanel chartsPanel;


    private TabInfo explainTab;

    private TabInfo chartsTab;

    private JMenuItem jsonMenuItem;

    private JMenuItem csvMenuItem;

    private List<JsonObject> cachedResults;

    private CbShellEmulator emulator;


    public QueryResultToolWindowFactory() {
        instance = this;
    }

    @NotNull
    private static String getEmptyExplain() {
        return "<html><body style=\" background: #3c3f41\"><span style='color:#ccc'>Nothing to show</span></body></html>";
    }

    public static void removeAllActionListeners(JMenuItem menuItem) {
        ActionListener[] listeners = menuItem.getActionListeners();
        for (ActionListener listener : listeners) {
            menuItem.removeActionListener(listener);
        }
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        this.project = project;
        JBTabs tabs = JBTabsFactory.createTabs(project);
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

        popupMenu = new JPopupMenu();
        jsonMenuItem = new JMenuItem("JSON");
        csvMenuItem = new JMenuItem("CSV");

        popupMenu.add(csvMenuItem);
        popupMenu.add(jsonMenuItem);


        DefaultActionGroup executeGroup = new DefaultActionGroup();
        Icon executeIcon = IconLoader.getIcon("/assets/icons/export.svg", QueryResultToolWindowFactory.class);
        executeGroup.add(new AnAction("Export", "Export", executeIcon) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                Component component = e.getInputEvent().getComponent();
                popupMenu.show(component, -10, component.getHeight());
            }
        });

        ActionToolbar toolbar = ActionManager.getInstance()
                                             .createActionToolbar("QueryResultToolbar", executeGroup, true);
        toolbar.getComponent().setBorder(JBUI.Borders.emptyRight(10));
        toolbar.setTargetComponent(topPanel);

        JPanel rightPanel = new JPanel(new BorderLayout());
        JPanel limitPanel = new JPanel(new BorderLayout());
        limitPanel.add(new JLabel("Limit:"), BorderLayout.WEST);
        ComboBox queryLimitSelector = new ComboBox(QUERY_LIMITS);

        queryLimitSelector.addActionListener(e -> {
            Integer limit = null;
            if (queryLimitSelector.getSelectedIndex() < QUERY_LIMITS.length - 1) {
                limit = Integer.valueOf(QUERY_LIMITS[queryLimitSelector.getSelectedIndex()]);
            }
            ActiveCluster.getInstance().setQueryLimit(limit);
        });

        limitPanel.add(queryLimitSelector, BorderLayout.EAST);

        rightPanel.add(limitPanel, BorderLayout.WEST);
        rightPanel.add(toolbar.getComponent(), BorderLayout.EAST);
        topPanel.add(rightPanel, BorderLayout.EAST);


        VirtualFile virtualFile = new LightVirtualFile("query_result",
                                                       FileTypeManager.getInstance().getFileTypeByExtension("json"),
                                                       "{\n\"No data to display\": \"Hit 'execute' in the query editor to run a statement.\"\n}");
        Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
        editor = (EditorEx) EditorFactory.getInstance().createEditor(document, project, JsonFileType.INSTANCE, true);
        EditorSettings editorSettings = editor.getSettings();
        editorSettings.setLineNumbersShown(true);
        editorSettings.setFoldingOutlineShown(true);
        editorSettings.setAutoCodeFoldingEnabled(true);
        editorSettings.setIndentGuidesShown(true);


        latestExplain = getEmptyExplain();


        JBTabs resultTabs = JBTabsFactory.createTabs(project);
        resultTabs.addTab(new TabInfo(editor.getComponent()).setText("JSON"));
        resultTabs.addTab(new TabInfo(new JBScrollPane(table)).setText("Table"));

        try {
            htmlPanel = new HtmlPanel();
            htmlPanel.loadHTML(latestExplain);
            JPanel explainPanel = new JPanel(new BorderLayout());
            explainPanel.add(htmlPanel, BorderLayout.CENTER);
            explainTab = new TabInfo(explainPanel).setText("Explain");
            resultTabs.addTab(explainTab);
        } catch (Exception e) {
            Log.error(
                    "Failed to load the explain tab. Double check if the JRE that you are running your IDE has support for JCEF. https://plugins.jetbrains.com/docs/intellij/jcef.html#enabling-jcef");
        }

        JPanel charts = new JPanel(new BorderLayout());
        chartsPanel = new ChartsPanel();
        charts.add(chartsPanel.createChartPanel(() -> cachedResults), BorderLayout.CENTER);

        chartsTab = new TabInfo(charts).setText("Charts");
        resultTabs.addTab(chartsTab);

        //NOTE: This is an workaround as the explain tends to render awkwardly when it is not on focus
        resultTabs.addListener(new TabsListener() {
            @Override
            public void selectionChanged(TabInfo oldSelection, TabInfo newSelection) {
                if (explainTab != null && newSelection == explainTab) {
                    htmlPanel.loadHTML(latestExplain);
                } else if (chartsTab != null && newSelection == chartsTab) {
                    chartsPanel.updateChart(cachedResults);
                }
            }
        });


        JPanel queryResultPanel = new JPanel();
        queryResultPanel.setLayout(new BorderLayout());
        queryResultPanel.add(topPanel, BorderLayout.NORTH);
        queryResultPanel.add(resultTabs.getComponent(), BorderLayout.CENTER);


        TabInfo outputTab = new TabInfo(getConsolePanel()).setText("Log");
        TabInfo queryResultTab = new TabInfo(queryResultPanel).setText("Query Result");
        TabInfo cbShell = new TabInfo(getCbShellPanel()).setText("CB Shell");


        tabs.addTab(outputTab);
        tabs.addTab(queryResultTab);
        tabs.addTab(cbShell);
        tabs.select(queryResultTab, true);

        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(tabs.getComponent(), "", false);
        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.addContent(content);

        ActiveCluster.getInstance().registerNewConnectionListener(() -> {

            ApplicationManager.getApplication().invokeLater(() -> {
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
            if (sqlppContent != null) {
                if (sqlppContent.startsWith("The query result should")) {
                    Messages.showErrorDialog(sqlppContent, "Error Exporting to SQL++ " + title);
                } else {
                    FileExporter.exportResultToSQLPP(project, sqlppContent);
                }
            }
        });
    }


    public JPanel getCbShellPanel() {


        JPanel consolePanel = new JPanel(new BorderLayout());
//        consolePanel.add(panel, BorderLayout.NORTH);
//        consolePanel.add(console.getComponent(), BorderLayout.CENTER);

        emulator = new CbShellEmulator(project, consolePanel);

        return consolePanel;
    }

    public JPanel getConsolePanel() {
        ConsoleView console = Log.getView();

        DefaultActionGroup actionGroup = new DefaultActionGroup();
        AnAction clearAction = new AnAction("Clear Log") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                console.clear();
            }
        };
        clearAction.getTemplatePresentation()
                   .setIcon(IconLoader.getIcon("/assets/icons/clear.svg", QueryResultToolWindowFactory.class));
        actionGroup.add(clearAction);

        ActionToolbar actionToolbar = ActionManager.getInstance()
                                                   .createActionToolbar("ConsoleToolbar", actionGroup, true);
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

    public void updateQueryStats(List<String> queryValues, List<JsonObject> results, CouchbaseQueryResultError error, List<String> explain, boolean isQueryScript) {
        this.cachedResults = results;

        ApplicationManager.getApplication().invokeLater(() -> {
            if (results != null && error == null || error.getErrors().isEmpty()) {

                List<Map<String, Object>> convertedResults = new ArrayList<>();
                for (JsonObject jsonObject : results) {
                    convertedResults.add(jsonObjectToMap(jsonObject));
                }

                queryStatsPanel.setVisible(true);

                removeAllActionListeners(csvMenuItem);
                removeAllActionListeners(jsonMenuItem);
                csvMenuItem.addActionListener(
                        actionEvent -> FileExporter.exportResultToCSV(project, model.tableModelToCSV()));
                jsonMenuItem.addActionListener(
                        actionEvent -> FileExporter.exportResultToJson(project, gson.toJson(convertedResults)));


                statusIcon.setIcon(
                        IconLoader.getIcon("/assets/icons/check_mark_big.svg", QueryResultToolWindowFactory.class));
                ApplicationManager.getApplication().runWriteAction(() -> {
                    editor.getDocument().setText(gson.toJson(convertedResults));
                });

                latestExplain = (explain == null ? getEmptyExplain() : ExplainContent.getContent(explain));

                //IMPORTANT: Android Studio doesn't have JCEF support by default. So the explain tab might not be initialized on it.
                if (htmlPanel != null) {
                    htmlPanel.loadHTML(latestExplain);
                }

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

                try {
                    chartsPanel.updateChart(results);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!isQueryScript) {
                    boolean sqlppUpsertMenuItemExists = false;
                    boolean sqlppInsertMenuItemExists = false;
                    boolean sqlppUpdateMenuItemExists = false;

                    for (Component component : popupMenu.getComponents()) {
                        if (component instanceof JMenuItem) {
                            JMenuItem menuItem = (JMenuItem) component;
                            if (menuItem.getText().equals("SQL++ UPSERT")) {
                                sqlppUpsertMenuItemExists = true;
                            } else if (menuItem.getText().equals("SQL++ INSERT")) {
                                sqlppInsertMenuItemExists = true;
                            } else if (menuItem.getText().equals("SQL++ UPDATE")) {
                                sqlppUpdateMenuItemExists = true;
                            }
                        }
                    }

                    // If SQLPP items do not exist, add them after a divider
                    if (!sqlppUpsertMenuItemExists || !sqlppInsertMenuItemExists || !sqlppUpdateMenuItemExists) {
                        boolean dividerExists = false;
                        for (Component component : popupMenu.getComponents()) {
                            if (component instanceof JSeparator) {
                                dividerExists = true;
                                break;
                            }
                        }

                        if (!dividerExists) {
                            popupMenu.add(new JSeparator());
                        }

                        if (!sqlppUpsertMenuItemExists) {
                            JMenuItem sqlppUpsertMenuItem = new JMenuItem("SQL++ UPSERT");
                            popupMenu.add(sqlppUpsertMenuItem);
                            addSQLPPMenuItemListener(sqlppUpsertMenuItem, "UPSERT", model::convertToSQLPPUpsert);
                        }

                        if (!sqlppInsertMenuItemExists) {
                            JMenuItem sqlppInsertMenuItem = new JMenuItem("SQL++ INSERT");
                            popupMenu.add(sqlppInsertMenuItem);
                            addSQLPPMenuItemListener(sqlppInsertMenuItem, "INSERT", model::convertToSQLPPInsert);
                        }

                        if (!sqlppUpdateMenuItemExists) {
                            JMenuItem sqlppUpdateMenuItem = new JMenuItem("SQL++ UPDATE");
                            popupMenu.add(sqlppUpdateMenuItem);
                            addSQLPPMenuItemListener(sqlppUpdateMenuItem, "UPDATE", model::convertToSQLPPUpdate);
                        }
                    }
                } else {
                    for (Component component : popupMenu.getComponents()) {
                        if (component instanceof JMenuItem) {
                            JMenuItem menuItem = (JMenuItem) component;
                            if (menuItem.getText().equals("SQL++ UPSERT") || menuItem.getText()
                                                                                     .equals("SQL++ INSERT") || menuItem
                                    .getText().equals("SQL++ UPDATE")) {
                                popupMenu.remove(menuItem);
                            }

                        }
                        if (component instanceof JSeparator) {
                            popupMenu.remove(component);
                        }
                    }
                }

                popupMenu.revalidate();
                popupMenu.repaint();

            } else {
                cachedResults = null;
                statusIcon.setIcon(
                        IconLoader.getIcon("/assets/icons/warning-circle-big.svg", QueryResultToolWindowFactory.class));
                ApplicationManager.getApplication()
                                  .runWriteAction(() -> editor.getDocument().setText(gson.toJson(error.getErrors())));
            }
        });
    }

    public void setStatusAsLoading() {
        ApplicationManager.getApplication().invokeLater(() -> {
            for (JLabel label : queryStatsList) {
                label.setText("-");
            }
            statusIcon.setIcon(new AnimatedIcon.Default());

            ApplicationManager.getApplication().runWriteAction(
                    () -> editor.getDocument().setText("{ \"status\": \"Executing Statement\"}"));
        });
    }

    public void setStatusAsCanceled() {
        ApplicationManager.getApplication().invokeLater(() -> {
            for (JLabel label : queryStatsList) {
                label.setText("-");
            }
            statusIcon.setIcon(
                    IconLoader.getIcon("/assets/icons/warning-circle-big.svg", QueryResultToolWindowFactory.class));
            ApplicationManager.getApplication()
                              .runWriteAction(() -> editor.getDocument().setText("{ \"status\": \"Query cancelled\"}"));
        });
    }
}