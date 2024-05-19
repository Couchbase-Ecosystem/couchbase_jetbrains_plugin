package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tools.CBShell;
import com.couchbase.intellij.tree.examples.CardDialog;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.ui.GotItTooltip;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

public class TreeToolBarBuilder {

    private static int workbenchCounter = 0;

    private ActionToolbar leftActionToolbar;

    public JPanel build(Project project, Tree tree) {
        JPanel toolBarPanel = new JPanel(new BorderLayout());

        AnAction newWorkbench = new AnAction("New Query Workbench") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                ApplicationManager.getApplication().runWriteAction(() -> {
                    try {
                        Project project = e.getProject();
                        workbenchCounter++;
                        String fileName = "workbench" + workbenchCounter + ".sqlpp";
                        VirtualFile virtualFile = new LightVirtualFile(fileName, FileTypeManager.getInstance().getFileTypeByExtension("sqlpp"), "");
                        // Open the file in the editor
                        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                        fileEditorManager.openFile(virtualFile, true);
                    } catch (Exception ex) {
                        Log.error(ex);
                        ex.printStackTrace();
                    }
                });
            }

            @Override
            public void update(AnActionEvent e) {
                boolean shouldEnable = ActiveCluster.getInstance().hasQueryService();
                e.getPresentation().setEnabled(shouldEnable);
                e.getPresentation().setVisible(shouldEnable);
            }
        };

        newWorkbench.getTemplatePresentation().setIcon(IconLoader.getIcon("/assets/icons/new_query.svg", CouchbaseWindowContent.class));


        AnAction newSearchWorkbench = new AnAction("New Search Workbench") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                ApplicationManager.getApplication().runWriteAction(() -> {
                    try {
                        Project project = e.getProject();
                        TreeRightClickListener.searchWorkbenchCounter++;
                        String fileName = "search" + TreeRightClickListener.searchWorkbenchCounter + ".cbs.json";
                        String fileContent = """
                                {
                                  "query": {
                                        "query": "your_query_here"
                                  },
                                  "fields": ["*"]
                                }
                                        """;
                        VirtualFile virtualFile = new LightVirtualFile(fileName, FileTypeManager.getInstance().getFileTypeByExtension("cbs.json"), fileContent);
                        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                        fileEditorManager.openFile(virtualFile, true);
                    } catch (Exception ex) {
                        Log.error(ex);
                        ex.printStackTrace();
                    }
                });
            }

            @Override
            public void update(AnActionEvent e) {
                boolean shouldEnable = ActiveCluster.getInstance().hasSearchService();
                e.getPresentation().setEnabled(shouldEnable);
                e.getPresentation().setVisible(shouldEnable);
            }
        };

        newSearchWorkbench.getTemplatePresentation().setIcon(IconLoader.getIcon("/assets/icons/search_workbench2.svg", CouchbaseWindowContent.class));


        AnAction addConnectionAction = new AnAction("Add New Connection") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                // Add connection action code here
                NewConnectionDialog dialog = new NewConnectionDialog(project, tree, null, null);
                dialog.show();
            }
        };
        addConnectionAction.getTemplatePresentation().setIcon(IconLoader.getIcon("/assets/icons/new_database.svg", CouchbaseWindowContent.class));

        AnAction cbshellAction = new AnAction("Open New CB Shell") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {

                if (ActiveCluster.getInstance().get() != null) {
                    CBShell.openNewTerminal();
                } else {
                    Messages.showErrorDialog("You need to connect to a cluster first before running CB Shell", "Couchbase Plugin Error");
                }
            }
        };
        cbshellAction.getTemplatePresentation().setIcon(IconLoader.getIcon("/assets/icons/cbshell.svg", CouchbaseWindowContent.class));

        AnAction ellipsisAction = new AnAction("More Options") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                // Open menu code here
                JBPopupMenu menu = new JBPopupMenu();
                JBMenuItem item1 = new JBMenuItem("New Project from Template");
                menu.add(item1);

                item1.addActionListener(e1 -> {
                    CardDialog dialog = new CardDialog(project);
                    dialog.show();
                });

                JBMenuItem item2 = new JBMenuItem("Suggest Features / Report Bugs >>");
                item2.addActionListener(e1 -> {

                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        try {
                            Desktop.getDesktop().browse(new URI("https://github.com/couchbaselabs/couchbase_jetbrains_plugin/issues"));
                        } catch (IOException | URISyntaxException ex) {
                            Log.error("Could not open the project's home: https://github.com/couchbaselabs/couchbase_jetbrains_plugin/issues");
                        }
                    }
                });
                menu.add(item2);

                JBMenuItem pluginTour = new JBMenuItem("Watch the plugin's tour video");
                pluginTour.addActionListener(e1 -> {

                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        try {
                            Desktop.getDesktop().browse(new URI("https://www.youtube.com/watch?v=RiHLyop_KUM&ab_channel=Couchbase"));
                        } catch (IOException | URISyntaxException ex) {
                            Log.error("Could not open the plugin's tour video");
                        }
                    }
                });
                menu.add(pluginTour);


                Component component = e.getInputEvent().getComponent();
                menu.show(component, component.getWidth() / 2, component.getHeight() / 2);
            }
        };
        ellipsisAction.getTemplatePresentation().setIcon(IconLoader.getIcon("/assets/icons/ellipsis_horizontal.svg", CouchbaseWindowContent.class));
        ellipsisAction.getTemplatePresentation().setDescription("More options");

        DefaultActionGroup leftActionGroup = new DefaultActionGroup();
        leftActionGroup.add(addConnectionAction);
        leftActionGroup.addSeparator();

        leftActionGroup.add(newWorkbench);
        leftActionGroup.add(newSearchWorkbench);
        leftActionGroup.addSeparator();


// Disabling CBSHELL for now
//        if (OSUtil.isMacOS()) {
//            leftActionGroup.add(cbshellAction);
//        }

        DefaultActionGroup rightActionGroup = new DefaultActionGroup();
        rightActionGroup.add(ellipsisAction);

        leftActionToolbar = ActionManager.getInstance().createActionToolbar("Explorer", leftActionGroup, true);
        leftActionToolbar.setTargetComponent(toolBarPanel);
        toolBarPanel.add(leftActionToolbar.getComponent(), BorderLayout.WEST);

        ActionToolbar rightActionToolbar = ActionManager.getInstance().createActionToolbar("MoreOptions", rightActionGroup, true);
        rightActionToolbar.setTargetComponent(toolBarPanel);
        toolBarPanel.add(rightActionToolbar.getComponent(), BorderLayout.EAST);
        ActiveCluster.getInstance().registerNewConnectionListener(() -> {
            ApplicationManager.getApplication().invokeLater(() -> {
                leftActionToolbar.getComponent().revalidate();
                toolBarPanel.revalidate();
            });
        });

        return toolBarPanel;
    }

    public void showGotItTooltip() {
        GotItTooltip tooltip = new GotItTooltip(UUID.randomUUID().toString(), "Click here to connect to a new Couchbase Cluster", null);
        tooltip.show(leftActionToolbar.getComponent(), GotItTooltip.BOTTOM_MIDDLE);
    }
}
