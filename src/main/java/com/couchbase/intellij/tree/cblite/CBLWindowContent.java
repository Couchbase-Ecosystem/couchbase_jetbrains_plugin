package com.couchbase.intellij.tree.cblite;

import com.couchbase.intellij.tree.CouchbaseWindowContent;
import com.couchbase.intellij.tree.cblite.nodes.CBLDatabaseNodeDescriptor;
import com.couchbase.intellij.tree.cblite.nodes.CBLFileNodeDescriptor;
import com.couchbase.intellij.tree.cblite.nodes.CBLLoadMoreNodeDescriptor;
import com.couchbase.intellij.tree.cblite.storage.CBLDatabaseStorage;
import com.couchbase.intellij.tree.cblite.storage.SavedCBLDatabase;
import com.couchbase.intellij.tree.cblite.sync.SyncGatewaySyncDialog;
import com.couchbase.intellij.tree.node.NodeDescriptor;
import com.couchbase.intellij.workbench.Log;
import com.couchbase.lite.*;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static utils.FileUtils.createFolder;

public class CBLWindowContent extends JPanel {

    private final Project project;

    private static int workbenchCounter = 0;

    private static Tree tree;

    private static DefaultTreeModel treeModel;


    private void initCbLite() throws Exception {
        String path = PathManager.getConfigPath() + File.separator + "couchbase-intellij-plugin";
        createFolder(path);
        String rootDir = path + File.separator + "cblite";
        createFolder(rootDir);
        String temp = rootDir + File.separator + "cblite_temp";
        createFolder(temp);
        CouchbaseLite.init(false, new File(rootDir), new File(temp));
    }

    public CBLWindowContent(Project project) throws Exception {
        this.project = project;
        initCbLite();

        //Database.log.setCustom(new CBLPluginLogger());


        Database.log.getConsole().setDomains(LogDomain.ALL_DOMAINS);
        Database.log.getConsole().setLevel(LogLevel.VERBOSE);

        setLayout(new BorderLayout());
        add(createTopToolbar(), BorderLayout.NORTH);

        treeModel = getTreeModel();
        tree = new Tree(treeModel);
        tree.setRootVisible(false);
        tree.setCellRenderer(new NodeDescriptorRenderer());
        add(new JScrollPane(tree), BorderLayout.CENTER);

        tree.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    mouseClicked(e);
                } else {
                    TreePath clickedPath = tree.getPathForLocation(e.getX(), e.getY());
                    if (clickedPath != null) {
                        DefaultMutableTreeNode clickedNode = (DefaultMutableTreeNode) clickedPath.getLastPathComponent();
                        if (SwingUtilities.isRightMouseButton(e)) {
                            CBLTreeRightClickListener.handle(tree, project, e, clickedNode);
                        } else if (clickedNode.getUserObject() instanceof CBLLoadMoreNodeDescriptor) {
                            CBLLoadMoreNodeDescriptor loadMore = (CBLLoadMoreNodeDescriptor) clickedNode.getUserObject();
                            CBLDataLoader.listDocuments((DefaultMutableTreeNode) clickedNode.getParent(), tree, loadMore.getNewOffset());
                        }
                    }
                }
            }
        });

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TreePath clickedPath = tree.getPathForLocation(e.getX(), e.getY());
                if (clickedPath != null) {
                    DefaultMutableTreeNode clickedNode = (DefaultMutableTreeNode) clickedPath.getLastPathComponent();
                    Object userObject = clickedNode.getUserObject();
                    if (e.getClickCount() == 2) {
                        if (userObject instanceof CBLFileNodeDescriptor) {
                            CBLFileNodeDescriptor descriptor = (CBLFileNodeDescriptor) userObject;

                            CBLDataLoader.loadDocument(project, descriptor, tree, false);
                            VirtualFile virtualFile = descriptor.getVirtualFile();
                            if (virtualFile != null) {
                                FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                                fileEditorManager.openFile(virtualFile, true);
                            } else {
                                Log.debug("virtual file is null");
                            }
                        }
                    }
                }
            }
        });

        tree.addTreeExpansionListener(new TreeExpansionListener() {
            @Override
            public void treeExpanded(TreeExpansionEvent event) {
                CBLTreeExpandListener.handle(tree, event);
            }

            @Override
            public void treeCollapsed(TreeExpansionEvent event) {
                // No action needed
            }
        });
    }

    public static void removeNullDbnames(List<SavedCBLDatabase> list) {
        Iterator<SavedCBLDatabase> iterator = list.iterator();
        while (iterator.hasNext()) {
            SavedCBLDatabase item = iterator.next();
            if (item.getId() == null) {
                iterator.remove();
            }
        }
    }

    public static DefaultTreeModel getTreeModel() {

        java.util.List<SavedCBLDatabase> savedDBs = null;
        if (CBLDatabaseStorage.getInstance().getValue() == null || CBLDatabaseStorage.getInstance().getValue().getSavedDatabases() == null) {
            savedDBs = new ArrayList<>();
        } else {
            savedDBs = CBLDatabaseStorage.getInstance().getValue().getSavedDatabases();
        }


        removeNullDbnames(savedDBs);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");


        Map<String, SavedCBLDatabase> map = savedDBs.stream()
                .collect(Collectors.toMap(SavedCBLDatabase::getId, e -> e));

        Map<String, SavedCBLDatabase> sortedClusters = new TreeMap<>(map);
        for (Map.Entry<String, SavedCBLDatabase> entry : sortedClusters.entrySet()) {
            DefaultMutableTreeNode adminLocal = new DefaultMutableTreeNode(new CBLDatabaseNodeDescriptor(entry.getValue(), false));
            root.add(adminLocal);
        }

        return new DefaultTreeModel(root);
    }


    private JPanel createTopToolbar() {
        JPanel toolBarPanel = new JPanel(new BorderLayout());

        AnAction createNewDatabase = new AnAction("Create New CBLite Database") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                CBLCreateDatabaseDialog dialog = new CBLCreateDatabaseDialog(project, null, tree);
                dialog.show();
            }
        };
        createNewDatabase.getTemplatePresentation().setIcon(AllIcons.General.Add);
        AnAction importDatabase = new AnAction("Import CBLite Database") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                ImportCBLDatabaseDialog dialog = new ImportCBLDatabaseDialog(project, null, tree);
                dialog.show();
            }
        };
        importDatabase.getTemplatePresentation().setIcon(IconLoader.getIcon("/assets/icons/open_database.svg", CBLWindowContent.class));

        AnAction newWorkbench = new AnAction("New SQL++ Lite Workbench") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                ApplicationManager.getApplication().runWriteAction(() -> {
                    try {
                        Project project = e.getProject();
                        workbenchCounter++;
                        String fileName = "workbench" + workbenchCounter + ".sqlppl";
                        VirtualFile virtualFile = new LightVirtualFile(fileName, FileTypeManager.getInstance().getFileTypeByExtension("sqlppl"), "");
                        // Open the file in the editor
                        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                        fileEditorManager.openFile(virtualFile, true);
                    } catch (Exception ex) {
                        Log.error(ex);
                        ex.printStackTrace();
                    }
                });
            }
        };

        newWorkbench.getTemplatePresentation().setIcon(IconLoader.getIcon("/assets/icons/new_query.svg", CouchbaseWindowContent.class));


        AnAction optimizeDatabase = new AnAction("Run Database Optimize") {

            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {

                if (ActiveCBLDatabase.getInstance().getDatabase() == null) {
                    SwingUtilities.invokeLater(() -> Messages.showInfoMessage("You need to connect to a database before running this task", "Couchbase Plugin"));
                    return;
                }
                new Task.ConditionalModal(project, String.format("Running Database Optimize for ",
                        ActiveCBLDatabase.getInstance().getDatabase().getName()), false, PerformInBackgroundOption.ALWAYS_BACKGROUND) {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        try {

                            ActiveCBLDatabase.getInstance().getDatabase().performMaintenance(MaintenanceType.OPTIMIZE);
                        } catch (Exception ex) {
                            Log.error(ex);
                        }
                    }
                }.queue();
            }
        };

        optimizeDatabase.getTemplatePresentation().setIcon(IconLoader.getIcon("/assets/icons/optimize.svg", CouchbaseWindowContent.class));


        AnAction fullOptimize = new AnAction("Run Database Full-Optimize") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {

                if (ActiveCBLDatabase.getInstance().getDatabase() == null) {
                    SwingUtilities.invokeLater(() -> Messages.showInfoMessage("You need to connect to a database before running this task", "Couchbase Plugin"));
                    return;
                }
                new Task.ConditionalModal(project, String.format("Running Database Full Optimize for ",
                        ActiveCBLDatabase.getInstance().getDatabase().getName()), false, PerformInBackgroundOption.ALWAYS_BACKGROUND) {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        try {
                            ActiveCBLDatabase.getInstance().getDatabase().performMaintenance(MaintenanceType.FULL_OPTIMIZE);
                        } catch (Exception ex) {
                            Log.error(ex);
                        }
                    }
                }.queue();
            }
        };

        fullOptimize.getTemplatePresentation().setIcon(IconLoader.getIcon("/assets/icons/full_optimize.svg", CouchbaseWindowContent.class));


        AnAction compact = new AnAction("Run Database Compact") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {

                if (ActiveCBLDatabase.getInstance().getDatabase() == null) {
                    SwingUtilities.invokeLater(() -> Messages.showInfoMessage("You need to connect to a database before running this task", "Couchbase Plugin"));
                    return;
                }
                new Task.ConditionalModal(project, String.format("Running Database Compact for ",
                        ActiveCBLDatabase.getInstance().getDatabase().getName()), false, PerformInBackgroundOption.ALWAYS_BACKGROUND) {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        try {
                            ActiveCBLDatabase.getInstance().getDatabase().performMaintenance(MaintenanceType.COMPACT);
                        } catch (Exception ex) {
                            Log.error(ex);
                        }
                    }
                }.queue();
            }
        };

        compact.getTemplatePresentation().setIcon(IconLoader.getIcon("/assets/icons/database_compact.svg", CouchbaseWindowContent.class));


        AnAction integrityCheck = new AnAction("Run Database Integrity Check") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                if (ActiveCBLDatabase.getInstance().getDatabase() == null) {
                    SwingUtilities.invokeLater(() -> Messages.showInfoMessage("You need to connect to a database before running this task", "Couchbase Plugin"));
                    return;
                }
                new Task.ConditionalModal(project, String.format("Running Database Integrity Check for ",
                        ActiveCBLDatabase.getInstance().getDatabase().getName()), false, PerformInBackgroundOption.ALWAYS_BACKGROUND) {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        try {
                            ActiveCBLDatabase.getInstance().getDatabase().performMaintenance(MaintenanceType.INTEGRITY_CHECK);
                        } catch (Exception ex) {
                            Log.error(ex);
                        }
                    }
                }.queue();
            }
        };

        integrityCheck.getTemplatePresentation().setIcon(IconLoader.getIcon("/assets/icons/integrity_check.svg", CouchbaseWindowContent.class));


        AnAction syncDialog = new AnAction("Sync Gateway Replication") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                if (ActiveCBLDatabase.getInstance().getDatabase() == null) {
                    SwingUtilities.invokeLater(() -> Messages.showInfoMessage("You need to connect to a database before running this task", "Couchbase Plugin"));
                    return;
                }

                try {
                    SyncGatewaySyncDialog dialog = new SyncGatewaySyncDialog(project, tree);
                    dialog.show();
                } catch (Exception ex) {
                    Log.error(ex);
                    ex.printStackTrace();
                }
            }
        };

        syncDialog.getTemplatePresentation().setIcon(IconLoader.getIcon("/assets/icons/sync.svg", CouchbaseWindowContent.class));

        DefaultActionGroup leftActionGroup = new DefaultActionGroup();
        leftActionGroup.add(createNewDatabase);
        leftActionGroup.add(importDatabase);
        leftActionGroup.addSeparator();
        leftActionGroup.add(newWorkbench);
        leftActionGroup.addSeparator();
        leftActionGroup.add(optimizeDatabase);
        leftActionGroup.add(fullOptimize);
        leftActionGroup.add(compact);
        leftActionGroup.add(integrityCheck);
        leftActionGroup.addSeparator();
        leftActionGroup.add(syncDialog);


        ActionToolbar leftActionToolbar = ActionManager.getInstance().createActionToolbar("Explorer", leftActionGroup, true);
        leftActionToolbar.setTargetComponent(this);

        toolBarPanel.add(leftActionToolbar.getComponent(), BorderLayout.NORTH);
        return toolBarPanel;
    }


    static class NodeDescriptorRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            if (value instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object userObject = node.getUserObject();

                if (userObject instanceof NodeDescriptor) {
                    NodeDescriptor descriptor = (NodeDescriptor) userObject;
                    setIcon(descriptor.getIcon());
                    setText(descriptor.getText());
                }
            }
            return this;
        }
    }
}
