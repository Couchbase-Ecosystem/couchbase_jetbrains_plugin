package com.couchbase.intellij.workbench;


import com.couchbase.intellij.database.QueryResult;
import com.couchbase.intellij.result.JsonTableModel;
import com.couchbase.intellij.workbench.error.CouchbaseQueryResultError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.AnimatedIcon;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.table.JBTable;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueryResultToolWindowFactory implements ToolWindowFactory {
    public static QueryResultToolWindowFactory instance;
    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public QueryResultToolWindowFactory() {
        instance = this;
    }

    private static final String rttToolTip = "Round-Trip Time (RTT) is the total time taken to send a request and receive a response from the server";
    private static final String elapsedToolTip = "Elapsed is the time taken by the server to process the request";
    private static final String executionTooltip = "Execution is the time taken by the server to execute the query";
    private static final String docsTooltip = "Count of documents returned";
    private static final String docsSizeTooltip = "Total size of documents returned";
    private static JBTabs tabs;
    private static TabInfo queryResultTab;

    private JLabel statusIcon;

    private EditorEx editor;

    private List<JLabel> queryStatsList;
    private List<Map<String, Object>> cachedResults;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        tabs = new JBTabsImpl(project);
        JsonTableModel model = new JsonTableModel();
        QueryResult.init(model);
        JBTable table = new JBTable(model);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        JPanel resultStats = new JPanel();
        resultStats.setLayout(new BorderLayout());
        resultStats.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 0));

        statusIcon = new JLabel();
        resultStats.add(statusIcon, BorderLayout.WEST);

        GridBagLayout layout = new GridBagLayout();
        JPanel gridPanel = new JPanel(layout);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST; // Align elements to the left
        c.ipadx = 25; // Padding between columns


        queryStatsList = new ArrayList<>();
        String[] tooltips = {rttToolTip, elapsedToolTip, executionTooltip, docsTooltip, docsSizeTooltip};
        String[] headers = {"RTT", "ELAPSED", "EXECUTION", "DOCS", "SIZE"};

        for (int i = 0; i < headers.length; i++) {
            c.gridy = 0;
            c.gridx = 2 * i; // To make room for values
            JLabel title = new JLabel("<html><small style='font-weight:100'>" + headers[i] + "</small></html>");
            title.setToolTipText(tooltips[i]);
            gridPanel.add(title, c);

            c.gridy = 1;
            c.gridx = 2 * i; // Same x coordinate to align vertically
            JLabel valueLabel = new JLabel("<html><strong>-</strong></html>");
            valueLabel.setToolTipText(tooltips[i]);
            queryStatsList.add(valueLabel);
            gridPanel.add(valueLabel, c);
        }
        resultStats.add(gridPanel, BorderLayout.CENTER);
        topPanel.add(resultStats, BorderLayout.WEST);

        JButton exportButton = new JButton("Export");
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(new JMenuItem("CSV"));
        popupMenu.add(new JMenuItem("JSON"));
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popupMenu.show(exportButton, exportButton.getWidth() / 2, exportButton.getHeight() / 2);
            }
        });
        topPanel.add(exportButton, BorderLayout.EAST);
        Document document = EditorFactory.getInstance().createDocument("{\"No data to display\": \"Hit 'execute' in the query editor to run a statement.\"}");
        editor = (EditorEx) EditorFactory.getInstance().createEditor(document, project, JsonFileType.INSTANCE, false);
        EditorSettings editorSettings = editor.getSettings();
        editorSettings.setVirtualSpace(false);
        editorSettings.setLineMarkerAreaShown(false);
        editorSettings.setIndentGuidesShown(false);
        editorSettings.setFoldingOutlineShown(false);
        editorSettings.setAdditionalColumnsCount(3);
        editorSettings.setAdditionalLinesCount(8);
        editorSettings.setLineNumbersShown(true);

        JBTabs resultTabs = new JBTabsImpl(project);
        resultTabs.addTab(new TabInfo(editor.getComponent()).setText("JSON"));
        resultTabs.addTab(new TabInfo(new JBScrollPane(table)).setText("Table"));

        JPanel queryResultPanel = new JPanel();
        queryResultPanel.setLayout(new BorderLayout());
        queryResultPanel.add(topPanel, BorderLayout.NORTH);
        queryResultPanel.add(resultTabs.getComponent(), BorderLayout.CENTER);

        TabInfo outputTab = new TabInfo(new JPanel()).setText("Log");
        queryResultTab = new TabInfo(queryResultPanel).setText("Query Result");

        tabs.addTab(outputTab);
        tabs.addTab(queryResultTab);
        tabs.select(queryResultTab, true);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(tabs.getComponent(), "", false);
        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.addContent(content);
    }

    public void updateQueryStats(List<String> queryValues, List<Map<String, Object>> results, CouchbaseQueryResultError error) {
        if (results != null) {

            statusIcon.setIcon(IconLoader.findIcon("./assets/icons/check_mark_big.svg"));
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                    editor.getDocument().setText(gson.toJson(results));
                }
            });
            cachedResults = results;
            for (int i = 0; i < queryStatsList.size(); i++) {
                queryStatsList.get(i).setText(queryValues.get(i));
            }
        } else {
            cachedResults = null;
            statusIcon.setIcon(IconLoader.findIcon("./assets/icons/warning-circle-big.svg"));
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                    editor.getDocument().setText(gson.toJson(error.getErrors()));
                }
            });
        }
    }

    public void setStatusAsLoading() {
        for (JLabel label : queryStatsList) {
            label.setText("-");
        }
        statusIcon.setIcon(new AnimatedIcon.Big());

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                editor.getDocument().setText("{ \"status\": \"Executing Statement\"}");
            }
        });
    }
}