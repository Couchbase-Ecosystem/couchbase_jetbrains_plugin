package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.DataLoader;
import com.couchbase.intellij.database.collections.FakeCollectionSource;
import com.couchbase.intellij.persistence.FavoriteCollections;
import com.couchbase.intellij.tree.node.*;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.UIUtil;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class TreeExpandListenerTest extends LightPlatformCodeInsightFixture4TestCase {
    static {
        com.couchbase.intellij.testutil.AllowedThreads.allowJdkAwtThreads();
    }

    private final FakeCollectionSource source = FakeCollectionSource.numbered(40);
    private final FavoriteCollections favorites = new FavoriteCollections();
    private final CollectionsNodeDescriptor descriptor = new CollectionsNodeDescriptor("scope", "bucket");
    private final DefaultMutableTreeNode collections = new DefaultMutableTreeNode(descriptor);
    private final DefaultMutableTreeNode scope = new DefaultMutableTreeNode(new ScopeNodeDescriptor("scope", "conn", "bucket"));

    private Tree tree() {
        scope.add(collections);
        collections.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        root.add(scope);
        Tree tree = new Tree(new DefaultTreeModel(root));
        tree.addTreeExpansionListener(new TreeExpandListener(tree));
        return tree;
    }

    private void populate(int count) {
        CollectionTreePopulator.populate(collections, descriptor, source, favorites, "conn", count, Map.of(), n -> null);
    }

    private List<String> names() {
        return IntStream.range(0, collections.getChildCount())
                .mapToObj(i -> ((DefaultMutableTreeNode) collections.getChildAt(i)).getUserObject())
                .filter(CollectionNodeDescriptor.class::isInstance)
                .map(Object::toString).toList();
    }

    @Test
    public void reopeningScopeDropsTemporarySelectionsAndExtraPagesButRetainsFavorites() {
        Tree tree = tree();
        try (MockedStatic<DataLoader> loader = Mockito.mockStatic(DataLoader.class)) {
            loader.when(() -> DataLoader.listCollections(collections, tree)).thenAnswer(call -> {
                populate(10);
                ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(collections);
                return null;
            });
            tree.expandPath(new TreePath(collections.getPath()));
            favorites.setFavorite("conn", "bucket", "scope", "col35", true);
            descriptor.addSearchAdded("col35");
            descriptor.addSearchAdded("col37");
            populate(30);
            assertTrue(names().containsAll(List.of("col29", "col35", "col37")));
            long inFlightLoad = descriptor.beginLoad();

            TreePath scopePath = new TreePath(scope.getPath());
            tree.collapsePath(scopePath);
            assertTrue(descriptor.getSearchAdded().isEmpty());
            assertEquals(0, descriptor.getPagedLoaded());
            assertFalse(descriptor.isCurrentLoad(inFlightLoad));
            assertEquals(1, collections.getChildCount());
            assertInstanceOf(((DefaultMutableTreeNode) collections.getFirstChild()).getUserObject(), LoadingNodeDescriptor.class);

            tree.expandPath(scopePath);
            UIUtil.dispatchAllInvocationEvents();
            assertEquals("col35", names().getFirst());
            assertEquals(11, names().size());
            assertTrue(names().contains("col09"));
            assertFalse(names().contains("col10"));
            assertFalse(names().contains("col37"));
            assertEquals(10, descriptor.getPagedLoaded());
        }
    }

    @Test
    public void collapsingTheCollectionsFolderAlsoForgetsSearchSelections() {
        Tree tree = tree();
        try (MockedStatic<DataLoader> loader = Mockito.mockStatic(DataLoader.class)) {
            loader.when(() -> DataLoader.listCollections(collections, tree)).thenAnswer(call -> {
                populate(10);
                return null;
            });
            TreePath path = new TreePath(collections.getPath());
            tree.expandPath(path);
            descriptor.addSearchAdded("col37");
            populate(20);
            tree.collapsePath(path);
            tree.expandPath(path);
            assertEquals(10, names().size());
            assertFalse(names().contains("col37"));
        }
    }
}
