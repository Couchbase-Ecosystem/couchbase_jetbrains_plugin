package com.couchbase.intellij.tree;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.query.CollectionQueryIndexManager;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.DataLoader;
import com.couchbase.intellij.tree.node.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.ui.TestDialog;
import com.intellij.openapi.ui.TestDialogManager;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.KeyEvent;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;

public class TreeKeyboardAndMenuTest extends LightPlatformCodeInsightFixture4TestCase {

    static {
        com.couchbase.intellij.testutil.AllowedThreads.allowJdkAwtThreads();
    }

    private ActiveCluster previous;
    private ActiveCluster active;
    private Cluster sdk;
    private TestDialog previousDialog;
    private DefaultMutableTreeNode root, bucket, collection, document, indexes, index;
    private CollectionExplorerTree tree;

    @Before
    public void setUpTree() {
        previous = ActiveCluster.getInstance();
        sdk = Mockito.mock(Cluster.class, Mockito.RETURNS_DEEP_STUBS);
        active = Mockito.mock(ActiveCluster.class);
        Mockito.when(active.get()).thenReturn(sdk);
        Mockito.when(active.getId()).thenReturn("conn");
        Mockito.when(active.hasQueryService()).thenReturn(true);
        com.couchbase.intellij.persistence.SavedCluster saved = new com.couchbase.intellij.persistence.SavedCluster();
        saved.setId("conn");
        Mockito.when(active.getSavedCluster()).thenReturn(saved); // Refresh invalidates the schema cache on it
        ActiveCluster.setInstance(active);

        root = new DefaultMutableTreeNode("root");
        bucket = new DefaultMutableTreeNode(new BucketNodeDescriptor("travel-sample", "conn"));
        collection = new DefaultMutableTreeNode(new CollectionNodeDescriptor("airline", "conn", "travel-sample", "inventory", null));
        document = new DefaultMutableTreeNode(new FileNodeDescriptor("airline_10", "travel-sample", "inventory", "airline", "airline_10",
                FileNodeDescriptor.FileType.JSON, null));
        indexes = new DefaultMutableTreeNode(new IndexesNodeDescriptor("travel-sample", "inventory", "airline"));
        index = new DefaultMutableTreeNode(new IndexNodeDescriptor("travel-sample", "inventory", "airline", "def_name.sqlpp",
                new LightVirtualFile("def_name.sqlpp", "CREATE INDEX def_name ON airline(name)")));
        root.add(bucket);
        bucket.add(collection);
        collection.add(document);
        collection.add(indexes);
        indexes.add(index);
        tree = new CollectionExplorerTree(getProject(), new DefaultTreeModel(root));
    }

    @After
    public void restore() {
        ActiveCluster.setInstance(previous);
        if (previousDialog != null) TestDialogManager.setTestDialog(previousDialog);
    }

    private static List<String> names(DefaultActionGroup menu) {
        return java.util.Arrays.stream(menu.getChildren((com.intellij.openapi.actionSystem.AnActionEvent) null))
                .map(a -> a.getTemplatePresentation().getText()).filter(java.util.Objects::nonNull).toList();
    }

    private DefaultActionGroup menuOf(DefaultMutableTreeNode node) {
        DefaultActionGroup menu = TreeRightClickListener.menuFor(tree, getProject(), null, null, node);
        assertNotNull(menu);
        return menu;
    }

    @Test
    public void bucketsOfferTheirOverview() {
        assertTrue(names(menuOf(bucket)).contains("Bucket Overview"));
    }

    @Test
    public void queryIndexesCanBeDroppedUnlessTheConnectionIsReadOnly() {
        List<String> names = names(menuOf(index));
        assertTrue(names.toString(), names.contains("Drop Index"));
        assertTrue(names.toString(), names.contains("Copy Definition"));

        Mockito.when(active.isReadOnlyMode()).thenReturn(true);
        assertFalse(names(menuOf(index)).contains("Drop Index"));
    }

    @Test
    public void thePrimaryIndexUsesItsOwnDropCall() {
        CollectionQueryIndexManager manager = Mockito.mock(CollectionQueryIndexManager.class);
        TreeRightClickListener.dropIndex(manager, "#primary");
        Mockito.verify(manager).dropPrimaryIndex();
        Mockito.verify(manager, Mockito.never()).dropIndex(anyString());

        TreeRightClickListener.dropIndex(manager, "def_name");
        Mockito.verify(manager).dropIndex("def_name");
    }

    @Test
    public void findMatchesTheFirstActionByPrefixAndSkipsSeparators() {
        assertNull(TreeNodeActions.find(null, "Delete"));
        AnAction delete = TreeNodeActions.find(menuOf(document), "Delete", "Drop");
        assertNotNull(delete);
        assertEquals("Delete Document", delete.getTemplatePresentation().getText());
        assertEquals("Refresh Documents", TreeNodeActions.find(menuOf(collection), "Refresh").getTemplatePresentation().getText());
        assertEquals("Drop Index", TreeNodeActions.find(menuOf(index), "Delete", "Drop").getTemplatePresentation().getText());
        assertNull(TreeNodeActions.find(menuOf(document), "Refresh"));
    }

    @Test
    public void enterLoadsTheNextPageOfDocuments() {
        DefaultMutableTreeNode more = new DefaultMutableTreeNode(new LoadMoreNodeDescriptor("travel-sample", "inventory", "airline", 20));
        collection.add(more);
        try (MockedStatic<DataLoader> loader = Mockito.mockStatic(DataLoader.class)) {
            assertTrue(tree.handleKey(KeyEvent.VK_ENTER, more));
            loader.verify(() -> DataLoader.listDocuments(collection, tree, 20));
        }
    }

    @Test
    public void enterLoadsMoreCollectionsAndIgnoresOtherNodes() {
        DefaultMutableTreeNode collections = new DefaultMutableTreeNode(new CollectionsNodeDescriptor("inventory", "travel-sample"));
        DefaultMutableTreeNode more = new DefaultMutableTreeNode(new LoadMoreCollectionsNodeDescriptor("travel-sample", "inventory", 20));
        collections.add(more);
        root.add(collections);
        try (MockedStatic<DataLoader> loader = Mockito.mockStatic(DataLoader.class)) {
            assertTrue(tree.handleKey(KeyEvent.VK_ENTER, more));
            loader.verify(() -> DataLoader.loadMoreCollections(collections, tree));
            assertFalse("a plain bucket node has nothing to open", tree.handleKey(KeyEvent.VK_ENTER, bucket));
        }
    }

    @Test
    public void deleteKeyRunsTheDeleteActionWhichAsksFirst() {
        previousDialog = TestDialogManager.setTestDialog(message -> 1); // Cancel
        tree.setSelectionPath(new TreePath(bucket.getPath()));
        assertTrue(tree.handleKey(KeyEvent.VK_DELETE, bucket));
        Mockito.verify(sdk, Mockito.never()).buckets();
        assertEquals("the node is still there after cancelling", 1, root.getChildCount());
    }

    @Test
    public void f5RefreshesAndUnknownKeysFallThrough() {
        try (MockedStatic<DataLoader> loader = Mockito.mockStatic(DataLoader.class)) {
            assertTrue(tree.handleKey(KeyEvent.VK_F5, collection));
            loader.verify(() -> DataLoader.listDocuments(collection, tree, 0));
        }
        assertFalse(tree.handleKey(KeyEvent.VK_A, collection));
    }
}
