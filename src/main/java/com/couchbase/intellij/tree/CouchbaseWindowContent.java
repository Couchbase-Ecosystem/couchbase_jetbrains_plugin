package com.couchbase.intellij.tree;

import com.couchbase.intellij.DocumentFormatter;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.DataLoader;
import com.couchbase.intellij.persistence.SavedCluster;
import com.couchbase.intellij.tree.node.*;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.HyperlinkLabel;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
import java.net.URI;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

public class CouchbaseWindowContent extends JPanel {

    static Project project;
    private static DefaultTreeModel treeModel;
    private static JPanel toolBarPanel;

    private static Tree tree;


    public JPanel getTourPanel() {
        HyperlinkLabel hyperlinkLabel = new HyperlinkLabel("Watch a quick plugin tour >>");
        hyperlinkLabel.addHyperlinkListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://www.youtube.com/watch?v=RiHLyop_KUM&ab_channel=Couchbase"));
            } catch (Exception ex) {
                Log.error(ex);
            }
        });

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        panel.setBorder(new EmptyBorder(5, 0, 5, 0));
        panel.add(hyperlinkLabel);
        return panel;
    }

    public CouchbaseWindowContent(Project project) {
        CouchbaseWindowContent.project = project;
        setLayout(new BorderLayout());
        treeModel = getTreeModel(project);
        tree = new Tree(treeModel);

        tree.setRootVisible(false);
        tree.setCellRenderer(new NodeDescriptorRenderer());

        // create the toolbar
        final TreeToolBarBuilder toolBarBuilder = new TreeToolBarBuilder();
        toolBarPanel = toolBarBuilder.build(project, tree);
        add(toolBarPanel, BorderLayout.NORTH);
        add(new JScrollPane(tree), BorderLayout.CENTER);

        //only shows when the user doesn't have any connections yet
        if (DataLoader.getSavedClusters().isEmpty()) {
            add(getTourPanel(), BorderLayout.SOUTH);
        }

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
                            TreeRightClickListener.handle(tree, project, toolBarPanel, e, clickedNode);
                        } else if (clickedNode.getUserObject() instanceof LoadMoreNodeDescriptor) {
                            LoadMoreNodeDescriptor loadMore = (LoadMoreNodeDescriptor) clickedNode.getUserObject();
                            DataLoader.listDocuments((DefaultMutableTreeNode) clickedNode.getParent(), tree, loadMore.getNewOffset());
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
                            //always force to load the file from server on read only mode.
                            if (ActiveCluster.getInstance().isReadOnlyMode()) {
                                descriptor.setVirtualFile(null);
                            }

                            DataLoader.loadDocument(project, descriptor, tree);
                            VirtualFile virtualFile = descriptor.getVirtualFile();
                            if (virtualFile != null) {
                                FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                                FileEditor[] editor = fileEditorManager.openFile(virtualFile, true);
                                CouchbaseDocumentEditorRefresher.attach(editor[0]);
                            } else {
                                Log.debug("virtual file is null");
                            }
                        } else if (userObject instanceof IndexNodeDescriptor descriptor) {
                            VirtualFile virtualFile = descriptor.getVirtualFile();
                            if (virtualFile != null) {
                                OpenFileDescriptor fileDescriptor = new OpenFileDescriptor(project, virtualFile);
                                FileEditorManager.getInstance(project).openEditor(fileDescriptor, true);

                            } else {
                                Log.debug("virtual file is null");
                            }
                        } else if (userObject instanceof SearchIndexNodeDescriptor descriptor) {
                            VirtualFile virtualFile = descriptor.getVirtualFile();
                            if (virtualFile != null) {
                                DocumentFormatter.formatFile(project, virtualFile);
                                OpenFileDescriptor fileDescriptor = new OpenFileDescriptor(project, virtualFile);
                                FileEditorManager.getInstance(project).openEditor(fileDescriptor, true);

                            } else {
                                Log.debug("virtual file is null");
                            }
                        } else if (userObject instanceof MissingIndexNodeDescriptor) {

                            MissingIndexNodeDescriptor node = (MissingIndexNodeDescriptor) userObject;
                            createPrimaryIndex(node.getBucket(), node.getScope(), node.getCollection(), clickedPath);

                        } else if (userObject instanceof MissingIndexFootNoteNodeDescriptor) {
                            MissingIndexFootNoteNodeDescriptor node = (MissingIndexFootNoteNodeDescriptor) userObject;
                            createPrimaryIndex(node.getBucket(), node.getScope(), node.getCollection(), clickedPath);
                        } else if (userObject instanceof ConnectionNodeDescriptor) {
                            ConnectionNodeDescriptor node = (ConnectionNodeDescriptor) userObject;
                            if (!node.isActive()) {
                                TreeActionHandler.connectToCluster(project, node.getSavedCluster(), tree, toolBarPanel);
                            }
                        }
                    }

                }
            }

            private void createPrimaryIndex(String bucket, String scope, String collection, TreePath clickedPath) {
                if (ActiveCluster.getInstance().isReadOnlyMode()) {
                    Messages.showErrorDialog("You can't create indexes when your connection is on read-only mode", "Couchbase Plugin Error");
                } else {
                    int result = Messages.showYesNoDialog("<html>Are you sure that you would like to create a primary index on <strong>" + bucket + "." + scope + "." + collection + "</strong>?<br><br>" + "<small>We don't recommend primary indexes in production environments.</small><br>" + "<small>This operation might take a while.</small></html>", "Create New Index", Messages.getQuestionIcon());
                    if (result == Messages.YES) {
                        DataLoader.createPrimaryIndex(bucket, scope, collection);
                        tree.collapsePath(clickedPath.getParentPath());
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
        if (DataLoader.getSavedClusters() == null || DataLoader.getSavedClusters().isEmpty()) {
            Timer timer = new Timer(10000, e -> {
                ApplicationManager.getApplication().invokeLater(() -> {
                    try {
                        toolBarBuilder.showGotItTooltip();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            });
            timer.setRepeats(false);
            timer.start();
        }
    }


    public static DefaultTreeModel getTreeModel(Project project) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");

        Map<String, SavedCluster> sortedClusters = new TreeMap<>(DataLoader.getSavedClusters());
        for (Map.Entry<String, SavedCluster> entry : sortedClusters.entrySet()) {
            DefaultMutableTreeNode adminLocal = new DefaultMutableTreeNode(new ConnectionNodeDescriptor(entry.getKey(), entry.getValue(), false));
            root.add(adminLocal);
        }
        return new DefaultTreeModel(root);
    }

    public static Tree getTree() {
        return tree;
    }

    static class NodeDescriptorRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
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

                    Icon icon = descriptor.getIcon();
                    if (descriptor.getQueryFilter() != null) {
                        icon = IconLoader.getIcon("/assets/icons/filter.svg", CouchbaseWindowContent.class);
                    }

                    return new CounterPanel(icon, new JLabel(descriptor.getText()), descriptor.getCounterPanel());
                } else if (userObject instanceof ConnectionNodeDescriptor) {
                    ConnectionNodeDescriptor descriptor = (ConnectionNodeDescriptor) userObject;
                    if (descriptor.getText().equals(ActiveCluster.getInstance().getId()) && ActiveCluster.getInstance().isReadOnlyMode()) {
                        return new IconPanel(descriptor.getIcon(), IconLoader.getIcon("/assets/icons/eye.svg", CouchbaseWindowContent.class), new JLabel(descriptor.getText()));

                    } else {
                        setIcon(descriptor.getIcon());
                        setText(descriptor.getText());
                    }
                } else if (userObject instanceof CounterNodeDescriptor) {
                    CounterNodeDescriptor descriptor = (CounterNodeDescriptor) userObject;
                    return new CounterPanel(descriptor.getIcon(), new JLabel(descriptor.getText()), descriptor.getCounterPanel());

                } else if (userObject instanceof NodeDescriptor) {
                    NodeDescriptor descriptor = (NodeDescriptor) userObject;
                    setIcon(descriptor.getIcon());
                    setText(descriptor.getText());
                }
            }
            return this;
        }
    }

    static class IconPanel extends JPanel {

        public IconPanel(Icon icon1, Icon icon2, JLabel text) {
            setLayout(new FlowLayout(FlowLayout.LEFT, 0, 2));
            add(new JLabel(icon1));
            JLabel lbl2 = new JLabel(icon2);
            lbl2.setToolTipText("Cluster is on read-only mode");
            lbl2.setBorder(JBUI.Borders.empty(0, 3));
            add(lbl2);
            add(text);
        }
    }

    static class CounterPanel extends JPanel {
        public CounterPanel(Icon icon, JLabel text, JPanel counter) {
            setLayout(new FlowLayout(FlowLayout.LEFT, 0, 2));
            add(new JLabel(icon));
            text.setBorder(JBUI.Borders.empty(0, 3));
            setOpaque(true);
            add(text);
            add(counter);
        }
    }
}