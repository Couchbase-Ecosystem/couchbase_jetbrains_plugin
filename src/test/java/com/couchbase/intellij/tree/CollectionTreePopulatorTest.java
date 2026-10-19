package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.collections.CollectionListing;
import com.couchbase.intellij.database.collections.FakeCollectionSource;
import com.couchbase.intellij.persistence.FavoriteCollections;
import com.couchbase.intellij.tree.node.*;
import com.intellij.icons.AllIcons;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import org.junit.Test;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;

public class CollectionTreePopulatorTest extends LightPlatformCodeInsightFixture4TestCase {

    static {
        com.couchbase.intellij.testutil.AllowedThreads.allowJdkAwtThreads();
    }

    private final FavoriteCollections favorites = new FavoriteCollections();
    private final CollectionsNodeDescriptor desc = new CollectionsNodeDescriptor("s", "b");
    private final DefaultMutableTreeNode node = new DefaultMutableTreeNode(desc);

    private CollectionListing populate(FakeCollectionSource src, int pages) {
        return CollectionTreePopulator.populate(node, desc, src, favorites, "conn", pages, new HashMap<>(), n -> null);
    }

    private List<String> childNames() {
        List<String> out = new ArrayList<>();
        for (int i = 0; i < node.getChildCount(); i++) {
            out.add(((DefaultMutableTreeNode) node.getChildAt(i)).getUserObject().toString());
        }
        return out;
    }

    private Object lastChild() {
        return ((DefaultMutableTreeNode) node.getLastChild()).getUserObject();
    }

    @Test
    public void showsTenCollectionsAndALoadMoreRow() {
        populate(FakeCollectionSource.numbered(25), 10);

        assertEquals(11, node.getChildCount());
        assertTrue(lastChild() instanceof LoadMoreCollectionsNodeDescriptor);
        LoadMoreCollectionsNodeDescriptor more = (LoadMoreCollectionsNodeDescriptor) lastChild();
        assertEquals(20, more.getNewOffset());
        assertEquals("b", more.getBucket());
        assertEquals("s", more.getScope());
        assertEquals(10, desc.getPagedLoaded());
    }

    @Test
    public void loadMoreRowDisappearsOnceEverythingIsLoaded() {
        populate(FakeCollectionSource.numbered(25), 30);
        assertEquals(25, node.getChildCount());
        assertFalse(lastChild() instanceof LoadMoreNodeDescriptor);
    }

    @Test
    public void clickingLoadMoreTwiceWalksThroughAllPages() {
        FakeCollectionSource src = FakeCollectionSource.numbered(25);
        populate(src, 10);
        populate(src, ((LoadMoreCollectionsNodeDescriptor) lastChild()).getNewOffset());
        assertEquals(21, node.getChildCount());
        populate(src, ((LoadMoreCollectionsNodeDescriptor) lastChild()).getNewOffset());
        assertEquals(25, node.getChildCount());
    }

    @Test
    public void emptyScopeShowsNoResults() {
        populate(new FakeCollectionSource(), 10);
        assertEquals(1, node.getChildCount());
        assertTrue(lastChild() instanceof NoResultsNodeDescriptor);
    }

    @Test
    public void favoritesAreOnTopSortedAndMarkedWithAStar() {
        favorites.setFavorite("conn", "b", "s", "col22", true);
        favorites.setFavorite("conn", "b", "s", "col05", true);

        populate(FakeCollectionSource.numbered(30), 10);

        assertEquals(List.of("col05", "col22", "col00"), childNames().subList(0, 3));
        CollectionNodeDescriptor first = (CollectionNodeDescriptor) ((DefaultMutableTreeNode) node.getChildAt(0)).getUserObject();
        CollectionNodeDescriptor plain = (CollectionNodeDescriptor) ((DefaultMutableTreeNode) node.getChildAt(2)).getUserObject();
        assertTrue(first.isFavorite());
        assertSame(AllIcons.Nodes.Favorite, first.getIcon());
        assertFalse(plain.isFavorite());
        assertNotSame(AllIcons.Nodes.Favorite, plain.getIcon());
    }

    @Test
    public void favoriteOfAnotherConnectionIsIgnored() {
        favorites.setFavorite("other", "b", "s", "col22", true);
        populate(FakeCollectionSource.numbered(30), 10);
        assertEquals("col00", childNames().get(0));
    }

    @Test
    public void everyFavoriteIsShownEvenWhenThereAreMoreThanTen() {
        for (int i = 15; i < 30; i++) {
            favorites.setFavorite("conn", "b", "s", String.format("col%02d", i), true);
        }
        populate(FakeCollectionSource.numbered(40), 10);
        assertEquals(26, node.getChildCount()); // 15 favorites, 10 paged collections, Load More
        assertEquals("col15", childNames().get(0));
        assertEquals("col29", childNames().get(14));
        assertEquals("col00", childNames().get(15));
        assertEquals(10, desc.getPagedLoaded());
    }

    @Test
    public void unfavoritingACollectionOutsideThePageRemovesItFromTheList() {
        favorites.setFavorite("conn", "b", "s", "col22", true);
        populate(FakeCollectionSource.numbered(30), 10);
        assertEquals("col22", childNames().get(0));
        favorites.setFavorite("conn", "b", "s", "col22", false);
        populate(FakeCollectionSource.numbered(30), 10);
        assertFalse(childNames().contains("col22"));
        assertEquals("col00", childNames().get(0));
    }

    @Test
    public void pagingPastAFavoriteAndSearchSelectionNeverDuplicatesThem() {
        favorites.setFavorite("conn", "b", "s", "col12", true);
        desc.addSearchAdded("col12");
        desc.addSearchAdded("col18");
        populate(FakeCollectionSource.numbered(30), 10);
        populate(FakeCollectionSource.numbered(30), 20);
        assertEquals(21, node.getChildCount());
        assertEquals(childNames().size(), new HashSet<>(childNames()).size());
    }

    @Test
    public void unfavoritingRestoresNormalIcon() {
        CollectionNodeDescriptor d = new CollectionNodeDescriptor("x", "c", "b", "s", null);
        javax.swing.Icon normal = d.getIcon();
        d.setFavorite(true);
        assertSame(AllIcons.Nodes.Favorite, d.getIcon());
        d.setFavorite(false);
        assertSame(normal, d.getIcon());
    }

    @Test
    public void searchedCollectionIsAddedAndSurvivesReload() {
        FakeCollectionSource src = FakeCollectionSource.numbered(30);
        populate(src, 10);
        assertFalse(childNames().contains("col27"));

        desc.addSearchAdded("col27");
        desc.addSearchAdded("col27");
        populate(src, 10);

        assertEquals("col27", childNames().get(0));
        assertEquals(1, desc.getSearchAdded().size());
        populate(src, 10);
        assertEquals("col27", childNames().get(0));
    }

    @Test
    public void everyCollectionStartsWithAPlaceholderChildForLazyLoading() {
        populate(FakeCollectionSource.numbered(3), 10);
        DefaultMutableTreeNode col = (DefaultMutableTreeNode) node.getChildAt(0);
        assertEquals(1, col.getChildCount());
        assertTrue(((DefaultMutableTreeNode) col.getFirstChild()).getUserObject() instanceof LoadingNodeDescriptor);
    }

    @Test
    public void countersComeFromTheCountsMap() {
        Map<String, Integer> counts = Map.of("b.s.col00", 1500);
        CollectionTreePopulator.populate(node, desc, FakeCollectionSource.numbered(2), favorites, "conn", 10, counts, n -> null);
        CollectionNodeDescriptor d = (CollectionNodeDescriptor) ((DefaultMutableTreeNode) node.getChildAt(0)).getUserObject();
        assertEquals("(1.5k)", ((javax.swing.JLabel) d.getCounterPanel().getComponent(0)).getText());
        CollectionNodeDescriptor unknown = (CollectionNodeDescriptor) ((DefaultMutableTreeNode) node.getChildAt(1)).getUserObject();
        assertEquals("(?)", ((javax.swing.JLabel) unknown.getCounterPanel().getComponent(0)).getText());
    }
}
