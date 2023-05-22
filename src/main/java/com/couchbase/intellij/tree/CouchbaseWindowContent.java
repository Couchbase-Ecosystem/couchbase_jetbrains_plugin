package com.couchbase.intellij.tree;


import com.couchbase.intellij.DocumentFormatter;
import com.couchbase.intellij.database.DataLoader;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
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

public class CouchbaseWindowContent extends JPanel {

    private DefaultTreeModel treeModel;
    private Project project;

    public CouchbaseWindowContent(Project project) {
        this.project = project;
        setLayout(new BorderLayout());

        // create the toolbar
        JPanel toolBarPanel = new JPanel(new BorderLayout());
        AnAction newWorkbench = new AnAction() {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                ApplicationManager.getApplication().runWriteAction(() -> {
                    try {
                        Project project = e.getProject();

                        String fileName = "virtual.sqlpp";
                        VirtualFile virtualFile = new LightVirtualFile(fileName, FileTypeManager.getInstance().getFileTypeByExtension("sqlpp"), "");
                        if (virtualFile != null) {
                            // Open the file in the editor
                            FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                            fileEditorManager.openFile(virtualFile, true);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }
        };

        newWorkbench.getTemplatePresentation().setIcon(IconLoader.findIcon("./assets/icons/new_query.svg", CouchbaseWindowContent.class, false, true));
        newWorkbench.getTemplatePresentation().setDescription("Refresh");

        AnAction addConnectionAction = new AnAction() {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                // Add connection action code here
                DatabaseConnectionDialog dialog = new DatabaseConnectionDialog();
                dialog.show();
            }
        };
        addConnectionAction.getTemplatePresentation().setIcon(IconLoader.findIcon("./assets/icons/new_database.svg", CouchbaseWindowContent.class, false, true));
        addConnectionAction.getTemplatePresentation().setDescription("Add New Connection");

        AnAction importDataAction = new AnAction() {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                // Import data action code here
            }
        };
        importDataAction.getTemplatePresentation().setIcon(IconLoader.findIcon("./assets/icons/database_import.svg", CouchbaseWindowContent.class, false, true));
        importDataAction.getTemplatePresentation().setDescription("Import Data");

        AnAction ellipsisAction = new AnAction() {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                // Open menu code here
                JPopupMenu menu = new JPopupMenu();
                JMenuItem item1 = new JMenuItem("Test 1");
                JMenuItem item2 = new JMenuItem("Test 2");
                menu.add(item1);
                menu.add(item2);

                // Add action listeners to menu items
                item1.addActionListener(e1 -> {
                    // Code for "Test 1" here
                });

                item2.addActionListener(e2 -> {
                    // Code for "Test 2" here
                });

                // Display the menu
                Component component = e.getInputEvent().getComponent();
                menu.show(component, component.getWidth() / 2, component.getHeight() / 2);
            }
        };
        ellipsisAction.getTemplatePresentation().setIcon(IconLoader.findIcon("./assets/icons/ellipsis_horizontal.svg", CouchbaseWindowContent.class, false, true));
        ellipsisAction.getTemplatePresentation().setDescription("More Options");

        // add the actions to a DefaultActionGroup
        DefaultActionGroup leftActionGroup = new DefaultActionGroup();
        leftActionGroup.add(addConnectionAction);
        leftActionGroup.addSeparator();
        leftActionGroup.add(newWorkbench);
        leftActionGroup.addSeparator();
        leftActionGroup.add(importDataAction);

        DefaultActionGroup rightActionGroup = new DefaultActionGroup();
        rightActionGroup.add(ellipsisAction);


        // create an ActionToolbar and add it to the toolbar panel
        ActionToolbar leftActionToolbar = ActionManager.getInstance().createActionToolbar("Explorer", leftActionGroup, true);
        leftActionToolbar.setTargetComponent(this);
        toolBarPanel.add(leftActionToolbar.getComponent(), BorderLayout.WEST);

        ActionToolbar rightActionToolbar = ActionManager.getInstance().createActionToolbar("MoreOptions", rightActionGroup, true);
        rightActionToolbar.setTargetComponent(this);
        toolBarPanel.add(rightActionToolbar.getComponent(), BorderLayout.EAST);

        // add the toolbar panel to the main panel
        add(toolBarPanel, BorderLayout.NORTH);

        // create the tree

//        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
//        DefaultTreeModel treeModel = new DefaultTreeModel(root);
//        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode("Child");
//        root.add(childNode);

        treeModel = CouchbaseTreeModel.getTreeModel(project);
        Tree tree = new Tree(treeModel);
        tree.setRootVisible(false);
        tree.setCellRenderer(new NodeDescriptorRenderer());


        tree.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    mouseClicked(e);
                }
            }
        });

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    TreePath clickedPath = tree.getPathForLocation(e.getX(), e.getY());
                    if (clickedPath != null) {
                        DefaultMutableTreeNode clickedNode = (DefaultMutableTreeNode) clickedPath.getLastPathComponent();
                        Object userObject = clickedNode.getUserObject();
                        if (userObject instanceof FileNodeDescriptor) {
                            FileNodeDescriptor descriptor = (FileNodeDescriptor) userObject;
                            VirtualFile virtualFile = descriptor.getVirtualFile();
                            if (virtualFile != null) {

                                OpenFileDescriptor fileDescriptor = new OpenFileDescriptor(project, virtualFile);
                                FileEditorManager.getInstance(project).openEditor(fileDescriptor, true);
                                DocumentFormatter.formatFile(project, virtualFile);


                            } else {
                                System.out.println("virtual file is null===============");
                            }
                        }
                    }
                }
            }
        });


        tree.addTreeExpansionListener(new TreeExpansionListener() {
            @Override
            public void treeExpanded(TreeExpansionEvent event) {
                Object expandedNode = event.getPath().getLastPathComponent();
                if (expandedNode instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode expandedTreeNode = (DefaultMutableTreeNode) expandedNode;

                    if (expandedTreeNode.getUserObject() instanceof ConnectionNodeDescriptor) {
                        DataLoader.listBuckets(expandedTreeNode, treeModel, tree);
                    } else if (expandedTreeNode.getUserObject() instanceof BucketNodeDescriptor) {
                        DataLoader.listScopes(expandedTreeNode, treeModel, tree);
                    } else if (expandedTreeNode.getUserObject() instanceof ScopeNodeDescriptor) {
                        //Do Nothing
                    } else if (expandedTreeNode.getUserObject() instanceof CollectionsNodeDescriptor) {
                        DataLoader.listCollections(expandedTreeNode, treeModel, tree);
                    } else if (expandedTreeNode.getUserObject() instanceof CollectionNodeDescriptor) {
                        DataLoader.listDocuments(project, expandedTreeNode, treeModel, tree);
                    } else {
                        throw new UnsupportedOperationException("Not implemente yet");
                    }
                }
            }

            @Override
            public void treeCollapsed(TreeExpansionEvent event) {
                // No action needed
            }
        });
        //tree.setShowsRootHandles(true);


        // add the tree to the main panel
        add(new JScrollPane(tree), BorderLayout.CENTER);
    }


    class NodeDescriptorRenderer extends DefaultTreeCellRenderer {
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