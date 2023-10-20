package com.couchbase.intellij.tree.cblite;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.DataLoader;
import com.couchbase.intellij.persistence.SavedCluster;
import com.couchbase.intellij.tree.CouchbaseWindowContent;
import com.couchbase.intellij.tree.cblite.nodes.CBLDatabaseNodeDescriptor;
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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.Map;
import java.util.TreeMap;

public class CBLiteWindowContent extends JPanel {

    private Project project;

    private static Tree tree;

    private static DefaultTreeModel treeModel;

    public CBLiteWindowContent(Project project) {
        this.project = project;
        setLayout(new BorderLayout());
        add(createTopToolbar(), BorderLayout.NORTH);

        treeModel = getTreeModel(project);
        tree = new Tree(treeModel);
        tree.setRootVisible(false);
        tree.setCellRenderer(new NodeDescriptorRenderer());
        add(new JScrollPane(tree), BorderLayout.CENTER);
    }


    public static DefaultTreeModel getTreeModel(Project project) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");

        //TODO: Load from storage
        DefaultMutableTreeNode activeDatabase = new DefaultMutableTreeNode(new CBLDatabaseNodeDescriptor("database1", true));
        root.add(activeDatabase);

        DefaultMutableTreeNode inactiveDatabase = new DefaultMutableTreeNode(new CBLDatabaseNodeDescriptor("database2", false));
        root.add(inactiveDatabase);

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
                // Add connection action code here
            }
        };
        importDatabase.getTemplatePresentation().setIcon(IconLoader.getIcon("/assets/icons/open_database.svg", CBLiteWindowContent.class));

        DefaultActionGroup leftActionGroup = new DefaultActionGroup();
        leftActionGroup.add(createNewDatabase);
        leftActionGroup.add(importDatabase);

        ActionToolbar leftActionToolbar = ActionManager.getInstance().createActionToolbar("Explorer", leftActionGroup, true);

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


                NodeDescriptor descriptor = (NodeDescriptor) userObject;
                setIcon(descriptor.getIcon());
                setText(descriptor.getText());

            }
            return this;
        }
    }
}
