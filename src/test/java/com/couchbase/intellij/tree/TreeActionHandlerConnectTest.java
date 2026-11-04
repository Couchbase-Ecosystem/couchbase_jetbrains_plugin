package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.persistence.SavedCluster;
import com.couchbase.intellij.tree.node.ConnectionNodeDescriptor;
import com.intellij.testFramework.PlatformTestUtil;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import com.intellij.ui.treeStructure.Tree;
import org.junit.After;
import org.junit.Test;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TreeActionHandlerConnectTest extends LightPlatformCodeInsightFixture4TestCase {

    static {
        com.couchbase.intellij.testutil.AllowedThreads.allowJdkAwtThreads();
    }

    /** Records which thread every structural change arrives on. */
    private static final class RecordingModel extends DefaultTreeModel {
        final List<Boolean> onEdt = new CopyOnWriteArrayList<>();

        RecordingModel(TreeNode root) {
            super(root);
        }

        @Override
        public void reload() {
            onEdt.add(SwingUtilities.isEventDispatchThread());
            super.reload();
        }

        @Override
        public void nodeStructureChanged(TreeNode node) {
            onEdt.add(SwingUtilities.isEventDispatchThread());
            super.nodeStructureChanged(node);
        }
    }

    private ActiveCluster previous;

    @After
    public void restoreCluster() {
        if (previous != null) {
            ActiveCluster.setInstance(previous);
        }
    }

    @Test
    public void connectionResultsReachTheTreeOnTheEdtEvenWhenTheClusterCallsBackFromAWorker() throws Exception {
        previous = ActiveCluster.getInstance();
        ActiveCluster active = mock(ActiveCluster.class);
        when(active.getVersion()).thenReturn("7.6.0");
        when(active.getServices()).thenReturn(List.of("kv", "n1ql"));
        doAnswer(call -> {
            Consumer<Throwable> listener = call.getArgument(1);
            // The real cluster reports from its schema worker, never from the EDT.
            new Thread(() -> listener.accept(null), "fake-cluster-worker").start();
            return null;
        }).when(active).connect(any(), any(), any());
        ActiveCluster.setInstance(active);

        SavedCluster saved = new SavedCluster();
        saved.setId("c1");
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        RecordingModel model = new RecordingModel(root);
        Tree tree = new Tree(model);
        CountDownLatch connected = new CountDownLatch(1);

        TreeActionHandler.connectToCluster(getProject(), saved, tree, null, error -> {
            assertNull(error);
            connected.countDown();
        }, null);

        long deadline = System.currentTimeMillis() + 10_000;
        while (connected.getCount() > 0 && System.currentTimeMillis() < deadline) {
            PlatformTestUtil.dispatchAllEventsInIdeEventQueue();
            Thread.sleep(10);
        }
        assertEquals("the connection listener must be told about the connection", 0, connected.getCount());
        assertFalse("the tree must have been reloaded", model.onEdt.isEmpty());
        assertTrue("every tree change must happen on the EDT: " + model.onEdt, model.onEdt.stream().allMatch(b -> b));
        assertEquals(1, root.getChildCount());
        ConnectionNodeDescriptor node = (ConnectionNodeDescriptor) ((DefaultMutableTreeNode) root.getFirstChild()).getUserObject();
        assertEquals("c1", node.getSavedCluster().getId());
        assertTrue(node.isActive());
    }
}
