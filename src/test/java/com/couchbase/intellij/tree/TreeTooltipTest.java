package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.persistence.SavedCluster;
import com.couchbase.intellij.tree.node.CollectionNodeDescriptor;
import com.couchbase.intellij.tree.node.ConnectionNodeDescriptor;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import com.intellij.ui.treeStructure.Tree;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.MouseEvent;

/** The tree asks the rendered component for its tooltip, so the indicator tooltips must sit on that component. */
public class TreeTooltipTest extends LightPlatformCodeInsightFixture4TestCase {

    static {
        com.couchbase.intellij.testutil.AllowedThreads.allowJdkAwtThreads();
    }

    private ActiveCluster previous;

    @After
    public void restore() {
        if (previous != null) ActiveCluster.setInstance(previous);
    }

    private static String tooltipOf(Object userObject) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(userObject);
        root.add(node);
        Tree tree = new Tree(new DefaultTreeModel(root));
        tree.setCellRenderer(new CouchbaseWindowContent.NodeDescriptorRenderer());
        Component rendered = tree.getCellRenderer().getTreeCellRendererComponent(tree, node, false, false, true, 1, false);
        assertTrue(rendered instanceof JComponent);
        MouseEvent hover = new MouseEvent(rendered, MouseEvent.MOUSE_MOVED, 0, 0, 2, 2, 0, false);
        return ((JComponent) rendered).getToolTipText(hover);
    }

    @Test
    public void favoriteCollectionsExplainTheirStar() {
        CollectionNodeDescriptor favorite = new CollectionNodeDescriptor("airline", "conn", "b", "s", null);
        favorite.setFavorite(true);
        assertEquals(CouchbaseWindowContent.FAVORITE_TOOLTIP, tooltipOf(favorite));

        CollectionNodeDescriptor plain = new CollectionNodeDescriptor("airport", "conn", "b", "s", null);
        assertNull(tooltipOf(plain));
    }

    @Test
    public void readOnlyConnectionsExplainTheirEye() {
        previous = ActiveCluster.getInstance();
        ActiveCluster active = Mockito.mock(ActiveCluster.class);
        Mockito.when(active.getId()).thenReturn("prod");
        Mockito.when(active.isReadOnlyMode()).thenReturn(true);
        ActiveCluster.setInstance(active);
        SavedCluster saved = new SavedCluster();
        saved.setId("prod");

        assertEquals(CouchbaseWindowContent.READ_ONLY_TOOLTIP, tooltipOf(new ConnectionNodeDescriptor("prod", saved, true)));
        assertNull("other connections have no eye", tooltipOf(new ConnectionNodeDescriptor("staging", new SavedCluster(), false)));
    }
}
