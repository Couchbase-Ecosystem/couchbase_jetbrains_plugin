package com.couchbase.intellij.workbench;

import com.couchbase.intellij.database.QueryResult;
import com.couchbase.intellij.result.JsonTableModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
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

public class QueryResultToolWindowFactory implements ToolWindowFactory {

    public static JBTabs tabs;
    public static TabInfo queryResultTab;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        tabs = new JBTabsImpl(project);
        JsonTableModel model = new JsonTableModel();
        QueryResult.init(model);
        JBTable table = new JBTable(model);

        JBScrollPane scrollPane = new JBScrollPane(table);

        TabInfo outputTab = new TabInfo(new JPanel()).setText("Log");
        queryResultTab = new TabInfo(scrollPane).setText("Query Result");

        tabs.addTab(outputTab);
        tabs.addTab(queryResultTab);
        tabs.select(queryResultTab, true);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(tabs.getComponent(), "", false);
        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.addContent(content);
    }
}