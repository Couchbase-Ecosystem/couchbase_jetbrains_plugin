package com.couchbase.intellij.tree.cblite;

import com.couchbase.intellij.tree.cblite.nodes.CBLDatabaseNodeDescriptor;
import com.couchbase.intellij.tree.cblite.storage.CBLiteDatabaseStorage;
import com.couchbase.intellij.tree.cblite.storage.SavedCBLiteDatabase;
import com.couchbase.intellij.tree.node.*;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.actionSystem.*;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import com.couchbase.lite.CouchbaseLite;

public class CBLiteWindowContent extends JPanel {

    private Project project;

    private static Tree tree;

    private static DefaultTreeModel treeModel;

    public CBLiteWindowContent(Project project) {
        this.project = project;
        CouchbaseLite.init();
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
                            //TODO: Fix this for listing more documents
                        } else if (clickedNode.getUserObject() instanceof LoadMoreNodeDescriptor) {
//                            LoadMoreNodeDescriptor loadMore = (LoadMoreNodeDescriptor) clickedNode.getUserObject();
//                            DataLoader.listDocuments((DefaultMutableTreeNode) clickedNode.getParent(), tree, loadMore.getNewOffset());
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
                        System.out.println("==============double click not being properly handled yet");
                    }
                }
            }
        });
    }

    public static void removeNullDbnames(List<SavedCBLiteDatabase> list) {
        Iterator<SavedCBLiteDatabase> iterator = list.iterator();
        while (iterator.hasNext()) {
            SavedCBLiteDatabase item = iterator.next();
            if (item.getId() == null) {
                iterator.remove();
            }
        }
    }
    public static DefaultTreeModel getTreeModel() {

        java.util.List<SavedCBLiteDatabase> savedDBs = null;
        if (CBLiteDatabaseStorage.getInstance().getValue() == null || CBLiteDatabaseStorage.getInstance().getValue().getSavedDatabases() == null) {
            savedDBs = new ArrayList<>();
        } else {
            savedDBs = CBLiteDatabaseStorage.getInstance().getValue().getSavedDatabases();
        }


        removeNullDbnames(savedDBs);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");



        Map<String, SavedCBLiteDatabase> map = savedDBs.stream()
                .collect(Collectors.toMap(SavedCBLiteDatabase::getId, e->e));

        Map<String, SavedCBLiteDatabase> sortedClusters = new TreeMap<>(map);
        for (Map.Entry<String, SavedCBLiteDatabase> entry : sortedClusters.entrySet()) {
            DefaultMutableTreeNode adminLocal = new DefaultMutableTreeNode(new CBLDatabaseNodeDescriptor(entry.getValue(), false));
            root.add(adminLocal);
        }
//        return new DefaultTreeModel(root);
//
//
//        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
//
//        try {
//            DatabaseConfiguration config = new DatabaseConfiguration();
//            config.setDirectory("/Users/denisrosa/Downloads/pbwarehouses.cblite2");
//
//            Database database = new Database("db", config);
//
//            System.out.println("+++++++++++++++abrindo database scopes "+database.getScopes());
//            for (Scope scope : database.getScopes()) {
//                System.out.println("========="+scope.getName());
//            }
//
//            database.close();
//        }catch (Exception e ) {
//            e.printStackTrace();
//        }

        //TODO: Load from storage
//        DefaultMutableTreeNode activeDatabase = new DefaultMutableTreeNode(new CBLDatabaseNodeDescriptor("database1", true));
//        root.add(activeDatabase);
//
//        DefaultMutableTreeNode inactiveDatabase = new DefaultMutableTreeNode(new CBLDatabaseNodeDescriptor("database2", false));
//        root.add(inactiveDatabase);

        return new DefaultTreeModel(root);
    }


    private JPanel createTopToolbar() {
        JPanel toolBarPanel = new JPanel(new BorderLayout());

        AnAction createNewDatabase = new AnAction("Create New CBLite Database") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                // Add connection action code here
            }
        };
        createNewDatabase.getTemplatePresentation().setIcon(AllIcons.General.Add);
        AnAction importDatabase = new AnAction("Import CBLite Database") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                ImportCBLiteDatabaseDialog dialog = new ImportCBLiteDatabaseDialog(project, null, tree);
                dialog.show();
            }
        };
        importDatabase.getTemplatePresentation().setIcon(IconLoader.getIcon("/assets/icons/open_database.svg", CBLiteWindowContent.class));

        DefaultActionGroup leftActionGroup = new DefaultActionGroup();
        leftActionGroup.add(createNewDatabase);
        leftActionGroup.add(importDatabase);

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
