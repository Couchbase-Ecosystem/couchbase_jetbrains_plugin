package com.couchbase.intellij.database.collections;

import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.intellij.persistence.FavoriteCollections;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class CollectionListingTest {

    private static final List<String> NONE = List.of();

    @Test
    public void firstPageHasTenAlphabeticalCollectionsAndMoreToLoad() {
        CollectionListing l = CollectionListing.build(FakeCollectionSource.numbered(25), NONE, NONE, 10);

        assertEquals(10, l.getNames().size());
        assertEquals("col00", l.getNames().get(0));
        assertEquals("col09", l.getNames().get(9));
        assertEquals(10, l.getPagedLoaded());
        assertEquals(25, l.getTotal());
        assertTrue(l.hasMore());
    }

    @Test
    public void loadingMoreExtendsTheSameSequence() {
        FakeCollectionSource src = FakeCollectionSource.numbered(25);
        CollectionListing second = CollectionListing.build(src, NONE, NONE, 20);
        assertEquals(20, second.getNames().size());
        assertTrue(second.hasMore());

        CollectionListing third = CollectionListing.build(src, NONE, NONE, 30);
        assertEquals(25, third.getNames().size());
        assertFalse(third.hasMore());
    }

    @Test
    public void scopeWithFewerThanOnePageHasNothingMore() {
        CollectionListing l = CollectionListing.build(new FakeCollectionSource("a", "b"), NONE, NONE, 10);
        assertEquals(List.of("a", "b"), l.getNames());
        assertFalse(l.hasMore());
    }

    @Test
    public void emptyScopeYieldsEmptyListing() {
        CollectionListing l = CollectionListing.build(new FakeCollectionSource(), NONE, NONE, 10);
        assertTrue(l.getNames().isEmpty());
        assertFalse(l.hasMore());
    }

    @Test
    public void favoritesComeFirstSortedAlphabeticallyEvenWhenFarDownTheList() {
        CollectionListing l = CollectionListing.build(FakeCollectionSource.numbered(30),
                List.of("col25", "col11", "col03"), NONE, 10);

        assertEquals(List.of("col03", "col11", "col25"), l.getNames().subList(0, 3));
        // col03 is also in the first page but must not be repeated
        assertEquals(l.getNames().size(), l.getNames().stream().distinct().count());
        assertEquals(12, l.getNames().size());
        assertEquals(10, l.getPagedLoaded());
    }

    @Test
    public void favoritesSortIgnoringCase() {
        CollectionListing l = CollectionListing.build(new FakeCollectionSource("beta", "Alpha", "gamma"),
                List.of("gamma", "beta", "Alpha"), NONE, 0);
        assertEquals(List.of("Alpha", "beta", "gamma"), l.getNames());
    }

    @Test
    public void favoritesThatNoLongerExistAreDropped() {
        CollectionListing l = CollectionListing.build(new FakeCollectionSource("a", "b"), List.of("deleted"), NONE, 10);
        assertEquals(List.of("a", "b"), l.getNames());
    }

    @Test
    public void searchedCollectionsAppearAfterFavoritesAndBeforeThePage() {
        CollectionListing l = CollectionListing.build(FakeCollectionSource.numbered(30),
                List.of("col20"), List.of("col28", "col27"), 3);

        assertEquals(List.of("col20", "col28", "col27", "col00", "col01", "col02"), l.getNames());
        assertEquals(3, l.getPagedLoaded());
        assertTrue(l.hasMore());
    }

    @Test
    public void searchedCollectionThatIsAlsoFavoriteIsShownOnce() {
        CollectionListing l = CollectionListing.build(FakeCollectionSource.numbered(30),
                List.of("col20"), List.of("col20"), 2);
        assertEquals(List.of("col20", "col00", "col01"), l.getNames());
    }

    @Test
    public void searchedCollectionThatWasDroppedIsIgnored() {
        CollectionListing l = CollectionListing.build(new FakeCollectionSource("a"), NONE, List.of("gone"), 10);
        assertEquals(List.of("a"), l.getNames());
    }

    // ---- sources ---------------------------------------------------------------------------------------------

    private static ManifestCollectionSource manifest(String... names) {
        java.util.Set<CollectionSpec> specs = new java.util.HashSet<>();
        for (String n : names) {
            specs.add(CollectionSpec.create(n, "s"));
        }
        ScopeSpec scope = ScopeSpec.create("s", specs);
        return new ManifestCollectionSource(() -> scope);
    }

    @Test
    public void manifestSourcePagesInAlphabeticalOrder() {
        ManifestCollectionSource src = manifest("c", "A", "b", "d");
        assertEquals(4, src.count());
        assertEquals(List.of("A", "b"), src.page(0, 2));
        assertEquals(List.of("c", "d"), src.page(2, 10));
        assertTrue(src.page(9, 2).isEmpty());
    }

    @Test
    public void manifestSourceSearchIsCaseInsensitiveContains() {
        ManifestCollectionSource src = manifest("airline", "Airport", "hotel", "route");
        assertEquals(List.of("airline", "Airport"), src.search("AIR", 10));
        assertEquals(List.of("airline"), src.search("air", 1));
        assertEquals(4, src.search("", 10).size());
        assertTrue(src.search("zzz", 10).isEmpty());
    }

    @Test
    public void manifestSourceReportsWhichNamesExist() {
        assertEquals(java.util.Set.of("a"), manifest("a", "b").existing(List.of("a", "x")));
    }

    @Test
    public void manifestIsFetchedOnlyOnce() {
        int[] calls = {0};
        ScopeSpec scope = ScopeSpec.create("s");
        ManifestCollectionSource src = new ManifestCollectionSource(() -> {
            calls[0]++;
            return scope;
        });
        src.count();
        src.page(0, 5);
        src.search("x", 5);
        assertEquals(1, calls[0]);
    }

    @Test
    public void fallbackSourceSwitchesPermanentlyWhenPreferredFails() {
        CollectionSource broken = new FakeCollectionSource() {
            @Override
            public int count() {
                throw new IllegalStateException("no query service");
            }
        };
        FallbackCollectionSource src = new FallbackCollectionSource(broken, new FakeCollectionSource("a", "b"));

        assertFalse(src.isUsingFallback());
        assertEquals(2, src.count());
        assertTrue(src.isUsingFallback());
        assertEquals(List.of("a", "b"), src.page(0, 10));
    }

    @Test
    public void fallbackSourceKeepsUsingPreferredWhenItWorks() {
        FakeCollectionSource preferred = new FakeCollectionSource("p");
        FallbackCollectionSource src = new FallbackCollectionSource(preferred, new FakeCollectionSource("f"));
        assertEquals(List.of("p"), src.page(0, 10));
        assertFalse(src.isUsingFallback());
    }

    // ---- favorites persistence model -------------------------------------------------------------------------

    @Test
    public void favoritesAreScopedPerConnectionBucketAndScope() {
        FavoriteCollections f = new FavoriteCollections();
        f.setFavorite("c1", "b", "s", "x", true);
        f.setFavorite("c1", "b", "s", "y", true);
        f.setFavorite("c2", "b", "s", "z", true);
        f.setFavorite("c1", "b", "other", "w", true);

        assertTrue(f.isFavorite("c1", "b", "s", "x"));
        assertFalse(f.isFavorite("c2", "b", "s", "x"));
        assertEquals(java.util.Set.of("x", "y"), new java.util.HashSet<>(f.getFavorites("c1", "b", "s")));
        assertEquals(List.of("z"), f.getFavorites("c2", "b", "s"));
    }

    @Test
    public void settingFavoriteTwiceKeepsOneEntryAndUnsetRemovesIt() {
        FavoriteCollections f = new FavoriteCollections();
        f.setFavorite("c", "b", "s", "x", true);
        f.setFavorite("c", "b", "s", "x", true);
        assertEquals(1, f.getKeys().size());
        f.setFavorite("c", "b", "s", "x", false);
        assertTrue(f.getKeys().isEmpty());
        f.setFavorite("c", "b", "s", "never", false);
        assertTrue(f.getKeys().isEmpty());
    }

    @Test
    public void similarScopeNamesDoNotLeakIntoEachOther() {
        FavoriteCollections f = new FavoriteCollections();
        f.setFavorite("c", "b", "sc", "x", true);
        assertTrue(f.getFavorites("c", "b", "s").isEmpty());
    }

    @Test
    public void nullKeyListFromXmlIsTreatedAsEmpty() {
        FavoriteCollections f = new FavoriteCollections();
        f.setKeys(null);
        assertTrue(f.getKeys().isEmpty());
        f.setFavorite("c", "b", "s", "x", true);
        assertTrue(f.isFavorite("c", "b", "s", "x"));
    }
}
