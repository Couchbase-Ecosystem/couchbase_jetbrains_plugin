package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tree.node.*;
import com.couchbase.client.java.Cluster;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import com.intellij.ui.treeStructure.Tree;
import org.junit.Test;
import org.mockito.Mockito;
import javax.swing.tree.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class TreeLoadTaskTest extends LightPlatformCodeInsightFixture4TestCase {
    static { com.couchbase.intellij.testutil.AllowedThreads.allowJdkAwtThreads(); }
    private ActiveCluster previous, active;
    private Tree tree;
    private DefaultMutableTreeNode parent;
    @Override protected boolean runInDispatchThread() { return false; }
    @Override protected void setUp() throws Exception {
        super.setUp(); previous = ActiveCluster.getInstance();
        active = Mockito.mock(ActiveCluster.class);
        Mockito.when(active.get()).thenReturn(Mockito.mock(Cluster.class));
        Mockito.when(active.getId()).thenReturn("tree-load-test"); ActiveCluster.setInstance(active);
        ApplicationManager.getApplication().invokeAndWait(() -> {
            var root = new DefaultMutableTreeNode("root"); parent = new DefaultMutableTreeNode("parent"); root.add(parent);
            tree = new Tree(new DefaultTreeModel(root));
        });
    }
    @Override protected void tearDown() throws Exception { ActiveCluster restore = previous; try { super.tearDown(); } finally { ActiveCluster.setInstance(restore); } }

    @Test public void failedPagingRetainsRowsAndOffersTheCapturedRetry() throws Exception {
        AtomicInteger retries = new AtomicInteger();
        AtomicReference<CompletableFuture<Void>> done = new AtomicReference<>();
        ApplicationManager.getApplication().invokeAndWait(() -> {
            parent.add(new DefaultMutableTreeNode(new CollectionNodeDescriptor("already loaded", active.getId(), "bucket", "scope", null)));
            done.set(TreeLoadTask.load(parent, tree, active, "collections", () -> true, () -> {
                assertFalse(ApplicationManager.getApplication().isDispatchThread());
                throw new IllegalStateException("Connection interrupted");
            }, unused -> fail("Failed data must not be published"), retries::incrementAndGet));
        });
        done.get().get(5, TimeUnit.SECONDS);
        ApplicationManager.getApplication().invokeAndWait(() -> {
            assertEquals(2, parent.getChildCount());
            assertTrue(((DefaultMutableTreeNode) parent.getFirstChild()).getUserObject() instanceof CollectionNodeDescriptor);
            var error = (LoadErrorNodeDescriptor) ((DefaultMutableTreeNode) parent.getLastChild()).getUserObject();
            assertEquals("Couldn't load collections — Retry", error.getText()); error.retry();
            assertEquals(1, retries.get());
        });
    }

    @Test public void invalidatedOrSupersededResultsCannotReplaceTheLatestListing() throws Exception {
        CountDownLatch release = new CountDownLatch(1), started = new CountDownLatch(1);
        AtomicReference<CompletableFuture<Void>> old = new AtomicReference<>(), latest = new AtomicReference<>();
        try {
            ApplicationManager.getApplication().invokeAndWait(() -> old.set(TreeLoadTask.load(parent, tree, active, "collections", () -> true,
                    () -> { started.countDown(); assertTrue(release.await(5, TimeUnit.SECONDS)); return "stale"; },
                    value -> fail("Stale results must be ignored"), () -> {})));
            assertTrue(started.await(5, TimeUnit.SECONDS));
            ApplicationManager.getApplication().invokeAndWait(() -> {
                TreeLoadTask.invalidate(parent, tree);
                latest.set(TreeLoadTask.load(parent, tree, active, "collections", () -> true, () -> "latest", value -> {
                    assertTrue(ApplicationManager.getApplication().isDispatchThread());
                    parent.removeAllChildren(); parent.add(new DefaultMutableTreeNode(value));
                }, () -> {}));
            });
            latest.get().get(5, TimeUnit.SECONDS);
            release.countDown(); old.get().get(5, TimeUnit.SECONDS);
            ApplicationManager.getApplication().invokeAndWait(() -> assertEquals("latest", ((DefaultMutableTreeNode) parent.getFirstChild()).getUserObject()));
        } finally { release.countDown(); }
    }

    @Test public void refreshRestoresSelectionAfterReplacingChildInstances() throws Exception {
        AtomicReference<CompletableFuture<Void>> done = new AtomicReference<>();
        ApplicationManager.getApplication().invokeAndWait(() -> {
            var original = new DefaultMutableTreeNode(new CollectionNodeDescriptor("selected", active.getId(), "bucket", "scope", null));
            parent.add(original); ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parent);
            tree.setSelectionPath(new TreePath(original.getPath()));
            done.set(TreeLoadTask.load(parent, tree, active, "collections", () -> true, () -> "selected", value -> {
                parent.removeAllChildren();
                parent.add(new DefaultMutableTreeNode(new CollectionNodeDescriptor(value, active.getId(), "bucket", "scope", null)));
            }, () -> {}));
        });
        done.get().get(5, TimeUnit.SECONDS);
        ApplicationManager.getApplication().invokeAndWait(() -> {
            assertNotNull(tree.getSelectionPath());
            assertEquals("selected", ((DefaultMutableTreeNode) tree.getLastSelectedPathComponent()).getUserObject().toString());
        });
    }
}
