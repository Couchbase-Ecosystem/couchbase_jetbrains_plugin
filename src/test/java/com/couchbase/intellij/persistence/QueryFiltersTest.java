package com.couchbase.intellij.persistence;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class QueryFiltersTest {

    @Test
    public void emptyOrNullFiltersReturnNull() {
        QueryFilters f = new QueryFilters();
        assertNull(f.getQueryFilter("c", "b", "s", "col"));
        f.setFilters(new HashMap<>());
        assertNull(f.getQueryFilter("c", "b", "s", "col"));
    }

    @Test
    public void saveThenGetRoundTrips() {
        QueryFilters f = new QueryFilters();
        f.saveQueryFilter("conn", "bucket", "scope", "col", "age > 1");
        assertEquals("age > 1", f.getQueryFilter("conn", "bucket", "scope", "col"));
    }

    @Test
    public void lookupIsKeyedByEveryLevel() {
        QueryFilters f = new QueryFilters();
        f.saveQueryFilter("conn", "bucket", "scope", "col", "x");
        assertNull(f.getQueryFilter("other", "bucket", "scope", "col"));
        assertNull(f.getQueryFilter("conn", "other", "scope", "col"));
        assertNull(f.getQueryFilter("conn", "bucket", "other", "col"));
        assertNull(f.getQueryFilter("conn", "bucket", "scope", "other"));
    }

    @Test
    public void siblingsAtEachLevelAreAddedWithoutLosingExisting() {
        QueryFilters f = new QueryFilters();
        f.saveQueryFilter("conn", "bucket", "scope", "col1", "f1");
        f.saveQueryFilter("conn", "bucket", "scope", "col2", "f2");
        f.saveQueryFilter("conn", "bucket", "scope2", "col1", "f3");
        f.saveQueryFilter("conn", "bucket2", "scope", "col1", "f4");
        f.saveQueryFilter("conn2", "bucket", "scope", "col1", "f5");
        assertEquals("f1", f.getQueryFilter("conn", "bucket", "scope", "col1"));
        assertEquals("f2", f.getQueryFilter("conn", "bucket", "scope", "col2"));
        assertEquals("f3", f.getQueryFilter("conn", "bucket", "scope2", "col1"));
        assertEquals("f4", f.getQueryFilter("conn", "bucket2", "scope", "col1"));
        assertEquals("f5", f.getQueryFilter("conn2", "bucket", "scope", "col1"));
    }

    @Test
    public void savingSameKeyOverwrites() {
        QueryFilters f = new QueryFilters();
        f.saveQueryFilter("conn", "bucket", "scope", "col", "old");
        f.saveQueryFilter("conn", "bucket", "scope", "col", "new");
        assertEquals("new", f.getQueryFilter("conn", "bucket", "scope", "col"));
    }

    @Test
    public void nullFilterIsStoredAndReadBackAsNull() {
        QueryFilters f = new QueryFilters();
        f.saveQueryFilter("conn", "bucket", "scope", "col", null);
        assertNull(f.getQueryFilter("conn", "bucket", "scope", "col"));
        assertTrue(f.getFilters().get("conn").get("bucket").get("scope").containsKey("col"));
    }

    @Test
    public void setFiltersReplacesBackingMap() {
        QueryFilters f = new QueryFilters();
        Map<String, Map<String, Map<String, Map<String, String>>>> data = new HashMap<>();
        f.setFilters(data);
        assertSame(data, f.getFilters());
    }
}
