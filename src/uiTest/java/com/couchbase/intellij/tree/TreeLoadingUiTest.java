package com.couchbase.intellij.tree;

import com.couchbase.client.java.*;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.client.java.manager.query.QueryIndex;
import com.couchbase.client.java.query.QueryResult;
import com.couchbase.intellij.database.*;
import com.couchbase.intellij.persistence.*;
import com.couchbase.intellij.tree.node.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.treeStructure.Tree;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.Test;
import org.mockito.Mockito;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class TreeLoadingUiTest extends DialogUiTestCase {
    private ActiveCluster previous, active;
    private Cluster sdk;
    private JFrame frame;
    private Tree tree;
    private DefaultMutableTreeNode parent;

    @Override protected void setUp() throws Exception {
        super.setUp(); previous = ActiveCluster.getInstance();
        active = Mockito.mock(ActiveCluster.class); sdk = Mockito.mock(Cluster.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(active.get()).thenReturn(sdk); Mockito.when(active.getId()).thenReturn("loading-ui");
        Mockito.when(active.getSavedCluster()).thenReturn(new SavedCluster()); ActiveCluster.setInstance(active);
    }
    private FrameFixture open(Object descriptor) {
        ApplicationManager.getApplication().invokeAndWait(() -> {
            var root = new DefaultMutableTreeNode("root"); var connection = new DefaultMutableTreeNode("connection"); root.add(connection);
            var bucket = new DefaultMutableTreeNode("bucket"); connection.add(bucket);
            var scope = new DefaultMutableTreeNode(new ScopeNodeDescriptor("scope", "loading-ui", "bucket")); bucket.add(scope);
            parent = new DefaultMutableTreeNode(descriptor); parent.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor())); scope.add(parent);
            tree = new CollectionExplorerTree(getProject(), new DefaultTreeModel(root)); tree.setRootVisible(false);
            tree.setCellRenderer(new CouchbaseWindowContent.NodeDescriptorRenderer());
            frame = new JFrame("Tree loading"); frame.setContentPane(new JScrollPane(tree)); frame.setSize(800, 600); frame.setVisible(true);
            tree.expandPath(new TreePath(parent.getPath()));
        });
        return new FrameFixture(robot, frame);
    }

    @Test public void collectionFailureHasAClickableRetryAndEmptySuccessHasNoLoadingRow() throws Exception {
        AtomicInteger attempts = new AtomicInteger(); ScopeSpec scope = ScopeSpec.create("scope", java.util.Set.of());
        Mockito.when(sdk.bucket("bucket").collections().getAllScopes()).thenAnswer(call -> {
            assertFalse("SDK listing runs outside the UI thread", SwingUtilities.isEventDispatchThread());
            if (attempts.incrementAndGet() == 1) throw new IllegalStateException("Connection interrupted");
            return List.of(scope);
        });
        open(new CollectionsNodeDescriptor("scope", "bucket"));
        GuiActionRunner.execute(() -> DataLoader.listCollections(parent, tree));
        await("A failed collection request offers Retry", () -> GuiActionRunner.execute(() -> has(LoadErrorNodeDescriptor.class)));
        clickRetry();
        await("Retry replaces failure with an explicit empty state", () -> GuiActionRunner.execute(() -> text("No collections")));
        assertEquals(2, attempts.get());
        assertFalse(GuiActionRunner.execute(() -> has(LoadingNodeDescriptor.class) || has(LoadErrorNodeDescriptor.class)));
    }

    @Test public void failedDocumentPageKeepsExistingDocumentsAndRetryLoadsTheSameOffset() throws Exception {
        Mockito.when(active.hasQueryService()).thenReturn(true);
        QueryIndex primary = Mockito.mock(QueryIndex.class); Mockito.when(primary.primary()).thenReturn(true);
        Mockito.when(sdk.bucket("bucket").scope("scope").collection("collection").queryIndexes().getAllIndexes()).thenReturn(List.of(primary));
        AtomicInteger requests = new AtomicInteger();
        Mockito.when(sdk.bucket("bucket").scope("scope").query(Mockito.anyString())).thenAnswer(call -> {
            assertFalse(SwingUtilities.isEventDispatchThread());
            int attempt = requests.incrementAndGet();
            if (attempt > 1) assertTrue(((String) call.getArgument(0)).contains("OFFSET 10"));
            if (attempt == 2) throw new IllegalStateException("Temporary failure");
            QueryResult result = Mockito.mock(QueryResult.class);
            Mockito.when(result.rowsAsObject()).thenReturn(IntStream.range(attempt == 1 ? 0 : 10, attempt == 1 ? 10 : 11)
                    .mapToObj(i -> JsonObject.create().put("cbFileNameId", "doc" + i).put("cbMetaType", "json")).toList());
            return result;
        });
        open(new CollectionNodeDescriptor("collection", "loading-ui", "bucket", "scope", null));
        GuiActionRunner.execute(() -> DataLoader.listDocuments(parent, tree, 0));
        await("First page is shown", () -> GuiActionRunner.execute(() -> documents() == 10));
        assertTrue(GuiActionRunner.execute(() -> has(SchemaNodeDescriptor.class)));
        GuiActionRunner.execute(() -> DataLoader.listDocuments(parent, tree, 10));
        await("Paging failure offers Retry", () -> GuiActionRunner.execute(() -> has(LoadErrorNodeDescriptor.class)));
        assertEquals(10, GuiActionRunner.execute(this::documents).intValue());
        clickRetry();
        await("Retry appends to the retained first page", () -> GuiActionRunner.execute(() -> documents() == 11));
        assertEquals(3, requests.get());
        assertTrue("Paging retains Schema", GuiActionRunner.execute(() -> has(SchemaNodeDescriptor.class)));
        assertFalse(GuiActionRunner.execute(() -> has(LoadingNodeDescriptor.class) || has(LoadErrorNodeDescriptor.class)));
    }

    @Test public void filteredDocumentEmptyStateExplainsThatThereAreNoMatches() throws Exception {
        Mockito.when(active.hasQueryService()).thenReturn(true);
        Mockito.when(active.getClusterURL()).thenThrow(new IllegalStateException("REST permission unavailable"));
        QueryIndex primary = Mockito.mock(QueryIndex.class); Mockito.when(primary.primary()).thenReturn(true);
        Mockito.when(sdk.bucket("bucket").scope("scope").collection("collection").queryIndexes().getAllIndexes()).thenReturn(List.of(primary));
        Mockito.when(sdk.bucket("bucket").scope("scope").query(Mockito.anyString()).rowsAsObject()).thenReturn(List.of());
        QueryFilter filter = new QueryFilter(); filter.setQuery("type = 'missing'");
        open(new CollectionNodeDescriptor("collection", "loading-ui", "bucket", "scope", filter));
        GuiActionRunner.execute(() -> DataLoader.listDocuments(parent, tree, 0));
        await("Filtered empty state is specific", () -> GuiActionRunner.execute(() -> text("No matching documents")));
        assertTrue("An unavailable emptiness check keeps Schema accessible", GuiActionRunner.execute(() -> has(SchemaNodeDescriptor.class)));
        GuiActionRunner.execute(() -> {
            ((CollectionNodeDescriptor) parent.getUserObject()).setQueryFilter(null);
            DataLoader.listDocuments(parent, tree, 0);
        });
        await("Unfiltered empty state is distinct", () -> GuiActionRunner.execute(() -> text("No documents")));
    }

    private void clickRetry() {
        Point point = GuiActionRunner.execute(() -> {
            for (int i = 0; i < parent.getChildCount(); i++) {
                var child = (DefaultMutableTreeNode) parent.getChildAt(i);
                if (child.getUserObject() instanceof LoadErrorNodeDescriptor) {
                    TreePath path = new TreePath(child.getPath()); tree.scrollPathToVisible(path);
                    Rectangle bounds = tree.getPathBounds(path); return new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
                }
            }
            throw new AssertionError("Retry row missing");
        });
        robot.click(tree, point);
    }
    private int documents() { return (int) java.util.Collections.list(parent.children()).stream().filter(node -> ((DefaultMutableTreeNode) node).getUserObject() instanceof FileNodeDescriptor).count(); }
    private boolean has(Class<?> type) { return java.util.Collections.list(parent.children()).stream().anyMatch(node -> type.isInstance(((DefaultMutableTreeNode) node).getUserObject())); }
    private boolean text(String expected) { return java.util.Collections.list(parent.children()).stream().anyMatch(node -> ((DefaultMutableTreeNode) node).getUserObject() instanceof NodeDescriptor descriptor && expected.equals(descriptor.getText())); }
    @Override protected void tearDown() throws Exception {
        ActiveCluster restore = previous;
        try {
            ApplicationManager.getApplication().invokeAndWait(() -> { if (frame != null) frame.dispose(); });
            super.tearDown();
        } finally { ActiveCluster.setInstance(restore); }
    }
}
