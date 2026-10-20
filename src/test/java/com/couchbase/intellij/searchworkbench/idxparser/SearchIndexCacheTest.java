package com.couchbase.intellij.searchworkbench.idxparser;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.search.SearchIndex;
import com.couchbase.client.java.manager.search.SearchIndexManager;
import com.couchbase.intellij.database.ActiveCluster;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SearchIndexCacheTest {

    private static final long TTL_MS = 1000L * 60 * 5;

    private ActiveCluster activeCluster;
    private Cluster cluster;
    private SearchIndexManager indexManager;
    private MockedStatic<ActiveCluster> activeClusterStatic;

    @Before
    public void setUp() throws Exception {
        resetCache();
        activeCluster = mock(ActiveCluster.class);
        cluster = mock(Cluster.class);
        indexManager = mock(SearchIndexManager.class);
        when(activeCluster.get()).thenReturn(cluster);
        when(cluster.searchIndexes()).thenReturn(indexManager);
        activeClusterStatic = mockStatic(ActiveCluster.class);
        activeClusterStatic.when(ActiveCluster::getInstance).thenReturn(activeCluster);
    }

    @After
    public void tearDown() throws Exception {
        activeClusterStatic.close();
        resetCache();
    }

    @Test
    public void returnsIndexJsonForBucketAndName() {
        stubIndexes(index("travel", "idx_hotels", "{\"name\":\"idx_hotels\"}"));

        assertEquals("{\"name\":\"idx_hotels\"}", SearchIndexCache.getIndex("travel", "idx_hotels"));
    }

    @Test
    public void indexesFromOtherBucketsAreExcluded() {
        stubIndexes(index("travel", "idx_hotels", "{\"b\":\"travel\"}"),
                index("other", "idx_other", "{\"b\":\"other\"}"));

        assertNull(SearchIndexCache.getIndex("travel", "idx_other"));
        assertEquals("{\"b\":\"other\"}", SearchIndexCache.getIndex("other", "idx_other"));
    }

    @Test
    public void secondLookupWithinTtlIsServedFromCache() {
        stubIndexes(index("travel", "idx_hotels", "{\"v\":1}"));

        SearchIndexCache.getIndex("travel", "idx_hotels");
        SearchIndexCache.getIndex("travel", "idx_hotels");

        verify(indexManager, times(1)).getAllIndexes();
    }

    @Test
    public void missingKeyForcesRefetchEveryCall() {
        stubIndexes(index("travel", "idx_hotels", "{\"v\":1}"));

        assertNull(SearchIndexCache.getIndex("travel", "missing"));
        assertNull(SearchIndexCache.getIndex("travel", "missing"));

        // NOTE: possible bug: negative results are not cached, so every miss hits the cluster
        verify(indexManager, times(2)).getAllIndexes();
    }

    @Test
    public void refetchesAfterTtlExpires() throws Exception {
        stubIndexes(index("travel", "idx_hotels", "{\"v\":1}"));
        SearchIndexCache.getIndex("travel", "idx_hotels");

        setLastUpdate(System.currentTimeMillis() - TTL_MS - 1000);
        SearchIndexCache.getIndex("travel", "idx_hotels");

        verify(indexManager, times(2)).getAllIndexes();
    }

    @Test
    public void emptyClusterReturnsNull() {
        stubIndexes();
        assertNull(SearchIndexCache.getIndex("travel", "idx_hotels"));
    }

    private void stubIndexes(SearchIndex... indexes) {
        when(indexManager.getAllIndexes()).thenReturn(List.of(indexes));
    }

    private static SearchIndex index(String bucket, String name, String json) {
        SearchIndex idx = mock(SearchIndex.class);
        when(idx.sourceName()).thenReturn(bucket);
        when(idx.name()).thenReturn(name);
        when(idx.toJson()).thenReturn(json);
        return idx;
    }

    private static void resetCache() throws Exception {
        setStatic("cache", new java.util.HashMap<String, String>());
        setLastUpdate(0L);
    }

    private static void setLastUpdate(long value) throws Exception {
        setStatic("lastUpdate", value);
    }

    private static void setStatic(String name, Object value) throws Exception {
        Field f = SearchIndexCache.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(null, value);
    }
}
