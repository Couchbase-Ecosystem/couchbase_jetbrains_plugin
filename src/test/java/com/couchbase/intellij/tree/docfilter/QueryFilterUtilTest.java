package com.couchbase.intellij.tree.docfilter;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.persistence.QueryFilter;
import com.couchbase.intellij.persistence.storage.QueryFiltersStorage;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class QueryFilterUtilTest extends BasePlatformTestCase {

    static {
        com.couchbase.intellij.testutil.AllowedThreads.allowJdkAwtThreads();
    }

    private ActiveCluster previousCluster;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        previousCluster = ActiveCluster.getInstance();
        // A bare cluster has no saved connection, so getId() is null and every call uses the same storage key.
        ActiveCluster.setInstance(new ActiveCluster() {
        });
        QueryFiltersStorage.getInstance().loadState(new QueryFiltersStorage.State());
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            QueryFiltersStorage.getInstance().loadState(new QueryFiltersStorage.State());
            ActiveCluster.setInstance(previousCluster);
        } finally {
            super.tearDown();
        }
    }

    public void testMissingFilterReturnsNull() {
        assertNull(QueryFilterUtil.getQueryFilter("travel-sample", "inventory", "airport"));
    }

    public void testSaveThenGetRoundTripsJson() throws Exception {
        QueryFilter filter = new QueryFilter();
        filter.setQuery("type = 'airport'");
        filter.setDocumentStartKey("a");
        filter.setDocumentEndKey("z");
        filter.setOffset(40);

        QueryFilterUtil.saveQueryFilter("travel-sample", "inventory", "airport", filter);
        QueryFilter loaded = QueryFilterUtil.getQueryFilter("travel-sample", "inventory", "airport");

        assertNotNull(loaded);
        assertEquals("type = 'airport'", loaded.getQuery());
        assertEquals("a", loaded.getDocumentStartKey());
        assertEquals("z", loaded.getDocumentEndKey());
        assertEquals(40, loaded.getOffset());
    }

    public void testFiltersAreScopedPerCollection() throws Exception {
        QueryFilter filter = new QueryFilter();
        filter.setQuery("x");
        QueryFilterUtil.saveQueryFilter("travel-sample", "inventory", "airport", filter);

        assertNull(QueryFilterUtil.getQueryFilter("travel-sample", "inventory", "route"));
        assertNull(QueryFilterUtil.getQueryFilter("other", "inventory", "airport"));
    }

    public void testPlainTextStoredValueFallsBackToQueryField() throws Exception {
        // Legacy filters were stored as the raw query text, which is not valid QueryFilter JSON.
        QueryFiltersStorage.getInstance().getValue().saveQueryFilter(
                null, "travel-sample", "inventory", "airport", "SELECT * FROM airport");

        QueryFilter loaded = QueryFilterUtil.getQueryFilter("travel-sample", "inventory", "airport");

        assertNotNull(loaded);
        assertEquals("SELECT * FROM airport", loaded.getQuery());
        assertNull(loaded.getDocumentStartKey());
        assertEquals(0, loaded.getOffset());
    }

    public void testEmptyFilterSavesAsDefaults() throws Exception {
        QueryFilterUtil.saveQueryFilter("b", "s", "c", new QueryFilter());
        QueryFilter loaded = QueryFilterUtil.getQueryFilter("b", "s", "c");

        assertNotNull(loaded);
        assertNull(loaded.getQuery());
        assertEquals(0, loaded.getOffset());
    }
}
