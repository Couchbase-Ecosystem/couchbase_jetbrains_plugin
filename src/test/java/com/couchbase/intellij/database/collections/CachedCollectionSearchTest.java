package com.couchbase.intellij.database.collections;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class CachedCollectionSearchTest {
    @Test
    public void openingThePickerAndBlankQueriesDoNotFetchNames() {
        FakeCollectionSource source = new FakeCollectionSource("airline", "airport", "hotel");
        CachedCollectionSearch search = new CachedCollectionSearch(source);
        assertTrue(search.search("", 20).isEmpty());
        assertTrue(search.search("  ", 20).isEmpty());
        assertEquals(0, source.pageCalls);
    }

    @Test
    public void typingEditingAndClearingReuseOneSnapshot() {
        FakeCollectionSource source = new FakeCollectionSource("airline", "airport", "hotel");
        CachedCollectionSearch search = new CachedCollectionSearch(source);
        assertEquals(List.of("airline", "airport"), search.search("air", 20));
        assertEquals(List.of("airline"), search.search("airl", 20));
        assertEquals(List.of("hotel"), search.search("  HOT  ", 20));
        assertEquals(List.of("airline", "airport", "hotel"), search.search("", 20));
        assertTrue(search.search("missing", 20).isEmpty());
        assertEquals(1, source.pageCalls);
        assertEquals(0, source.searchCalls);
    }

    @Test
    public void theSnapshotContainsNamesBeyondTheFirstPageAndResultLimit() {
        FakeCollectionSource source = FakeCollectionSource.numbered(50);
        CachedCollectionSearch search = new CachedCollectionSearch(source);
        assertEquals(20, search.search("col", 20).size());
        assertEquals(List.of("col49"), search.search("49", 20));
        assertEquals(1, source.pageCalls);
    }

    @Test
    public void anEmptyScopeIsAlsoCached() {
        FakeCollectionSource source = new FakeCollectionSource();
        CachedCollectionSearch search = new CachedCollectionSearch(source);
        assertTrue(search.search("air", 20).isEmpty());
        assertTrue(search.search("hotel", 20).isEmpty());
        assertEquals(1, source.pageCalls);
    }

    @Test
    public void reopeningThePickerGetsFreshNames() {
        FakeCollectionSource source = new FakeCollectionSource("airline");
        CachedCollectionSearch firstPicker = new CachedCollectionSearch(source);
        assertEquals(List.of("airline"), firstPicker.search("air", 20));
        source.names.add("airport");
        assertEquals(List.of("airline"), firstPicker.search("air", 20));
        assertEquals(List.of("airline", "airport"), new CachedCollectionSearch(source).search("air", 20));
        assertEquals(2, source.pageCalls);
    }

    @Test
    public void aFailedFetchCanBeRetriedInsteadOfCachingTheFailure() {
        FakeCollectionSource source = new FakeCollectionSource("hotel") {
            private boolean failed;

            @Override
            public List<String> page(int offset, int limit) {
                if (!failed) {
                    failed = true;
                    throw new IllegalStateException("Cluster unavailable");
                }
                return super.page(offset, limit);
            }
        };
        CachedCollectionSearch search = new CachedCollectionSearch(source);
        assertThrows(IllegalStateException.class, () -> search.search("hot", 20));
        assertEquals(List.of("hotel"), search.search("hot", 20));
    }
}
