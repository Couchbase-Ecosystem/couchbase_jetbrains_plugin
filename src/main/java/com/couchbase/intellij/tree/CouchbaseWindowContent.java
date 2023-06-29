package com.couchbase.intellij.tree;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.couchbase.intellij.database.DataLoader;
import com.couchbase.intellij.persistence.SavedCluster;
import com.couchbase.intellij.tree.node.CollectionNodeDescriptor;
import com.couchbase.intellij.tree.node.ConnectionNodeDescriptor;
import com.couchbase.intellij.tree.node.FileNodeDescriptor;
import com.couchbase.intellij.tree.node.IndexNodeDescriptor;
import com.couchbase.intellij.tree.node.LoadMoreNodeDescriptor;
import com.couchbase.intellij.tree.node.MissingIndexNodeDescriptor;
import com.couchbase.intellij.tree.node.NodeDescriptor;
import com.couchbase.intellij.tree.node.TooltipNodeDescriptor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.treeStructure.Tree;

public class CouchbaseWindowContent extends JPanel {

    private static DefaultTreeModel treeModel;
    private static Project project;

    private static JPanel toolBarPanel;

    public CouchbaseWindowContent(Project project) {
        CouchbaseWindowContent.project = project;
        setLayout(new BorderLayout());
        treeModel = getTreeModel(project);
        Tree tree = new Tree(treeModel);

        tree.setRootVisible(false);
        tree.setCellRenderer(new NodeDescriptorRenderer());

        // create the toolbar
        toolBarPanel = TreeToolBarBuilder.build(project, tree);
        add(toolBarPanel, BorderLayout.NORTH);
        add(new JScrollPane(tree), BorderLayout.CENTER);

        tree.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    mouseClicked(e);
                } else {
                    TreePath clickedPath = tree.getPathForLocation(e.getX(), e.getY());
                    if (clickedPath != null) {
                        DefaultMutableTreeNode clickedNode = (DefaultMutableTreeNode) clickedPath
                                .getLastPathComponent();
                        if (SwingUtilities.isRightMouseButton(e)) {
                            TreeRightClickListener.handle(tree, project, toolBarPanel, e, clickedNode);
                        } else if (clickedNode.getUserObject() instanceof LoadMoreNodeDescriptor) {
                            LoadMoreNodeDescriptor loadMore = (LoadMoreNodeDescriptor) clickedNode.getUserObject();
                            DataLoader.listDocuments((DefaultMutableTreeNode) clickedNode.getParent(), tree,
                                    loadMore.getNewOffset());
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
                        if (userObject instanceof FileNodeDescriptor) {
                            FileNodeDescriptor descriptor = (FileNodeDescriptor) userObject;
                            DataLoader.loadDocument(project, descriptor, tree, false);
                            VirtualFile virtualFile = descriptor.getVirtualFile();
                            if (virtualFile != null) {
                                FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                                fileEditorManager.openFile(virtualFile, true);

                            } else {
                                System.err.println("virtual file is null");
                            }
                        } else if (userObject instanceof IndexNodeDescriptor) {
                            IndexNodeDescriptor descriptor = (IndexNodeDescriptor) userObject;
                            VirtualFile virtualFile = descriptor.getVirtualFile();
                            if (virtualFile != null) {
                                OpenFileDescriptor fileDescriptor = new OpenFileDescriptor(project, virtualFile);
                                FileEditorManager.getInstance(project).openEditor(fileDescriptor, true);

                            } else {
                                System.err.println("virtual file is null");
                            }
                        } else if (userObject instanceof MissingIndexNodeDescriptor) {
                            MissingIndexNodeDescriptor node = (MissingIndexNodeDescriptor) userObject;
                            int result = Messages.showYesNoDialog(
                                    "<html>Are you sure that you would like to create a primary index on <strong>"
                                            + node.getBucket() + "." + node.getScope() + "." + node.getCollection()
                                            + "</strong>?<br><br>" +
                                            "<small>We don't recommend primary indexes in production environments.</small><br>"
                                            +
                                            "<small>This operation might take a while.</small></html>",
                                    "Create New Index", Messages.getQuestionIcon());
                            if (result == Messages.YES) {
                                DataLoader.createPrimaryIndex(node.getBucket(), node.getScope(), node.getCollection());
                                tree.collapsePath(clickedPath.getParentPath());
                            }
                        }
                    }

                }
            }
        });

        tree.addTreeExpansionListener(new TreeExpansionListener() {
            @Override
            public void treeExpanded(TreeExpansionEvent event) {
                TreeExpandListener.handle(tree, event);
            }

            @Override
            public void treeCollapsed(TreeExpansionEvent event) {
                // No action needed
            }
        });

    }

    public static DefaultTreeModel getTreeModel(Project project) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");

        Map<String, SavedCluster> sortedClusters = new TreeMap<>(DataLoader.getSavedClusters());
        for (Map.Entry<String, SavedCluster> entry : sortedClusters.entrySet()) {
            DefaultMutableTreeNode adminLocal = new DefaultMutableTreeNode(
                    new ConnectionNodeDescriptor(entry.getKey(), entry.getValue(), false));
            root.add(adminLocal);
        }
        return new DefaultTreeModel(root);
    }

    static class NodeDescriptorRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            if (value instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object userObject = node.getUserObject();

                if (userObject instanceof TooltipNodeDescriptor) {
                    TooltipNodeDescriptor descriptor = (TooltipNodeDescriptor) userObject;
                    setText(descriptor.getText());
                    setIcon(descriptor.getIcon());
                    if (descriptor.getTooltip() != null) {
                        setToolTipText(descriptor.getTooltip());
                    }
                } else if (userObject instanceof CollectionNodeDescriptor) {
                    CollectionNodeDescriptor descriptor = (CollectionNodeDescriptor) userObject;
                    setText(descriptor.getText());
                    if (descriptor.getQueryFilter() == null || descriptor.getQueryFilter().trim().isEmpty()) {
                        setIcon(descriptor.getIcon());
                    } else {
                        setIcon(IconLoader.getIcon("/assets/icons/filter.svg", CouchbaseWindowContent.class));
                    }
                } else if (userObject instanceof NodeDescriptor) {
                    NodeDescriptor descriptor = (NodeDescriptor) userObject;
                    setIcon(descriptor.getIcon());
                    setText(descriptor.getText());
                }
            }
            return this;
        }
    }

}