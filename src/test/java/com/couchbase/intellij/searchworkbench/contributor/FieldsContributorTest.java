package com.couchbase.intellij.searchworkbench.contributor;

import com.couchbase.intellij.searchworkbench.idxparser.SearchIndexCache;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class FieldsContributorTest {

    private static final String STATIC_INDEX = "{\"params\":{\"mapping\":{"
            + "\"default_field\":\"_all\","
            + "\"index_dynamic\":false,"
            + "\"types\":{\"inventory.airline\":{\"properties\":{"
            + "\"name\":{\"type\":\"text\"},"
            + "\"geo\":{\"properties\":{\"lat\":{\"type\":\"number\"}}}"
            + "}}}"
            + "}}}";

    @Test
    public void unknownIndexReturnsEmptyMap() {
        try (MockedStatic<SearchIndexCache> cache = mockStatic(SearchIndexCache.class)) {
            cache.when(() -> SearchIndexCache.getIndex("travel", "idx")).thenReturn(null);
            assertTrue(FieldsContributor.getFieldNames("travel", "idx").isEmpty());
        }
    }

    @Test
    public void staticIndexReturnsFlattenedFieldsPlusDefaultField() {
        try (MockedStatic<SearchIndexCache> cache = mockStatic(SearchIndexCache.class)) {
            cache.when(() -> SearchIndexCache.getIndex("travel", "idx")).thenReturn(STATIC_INDEX);

            Map<String, String> fields = FieldsContributor.getFieldNames("travel", "idx");

            assertEquals("text", fields.get("name"));
            assertEquals("number", fields.get("geo.lat"));
            // default field is registered with a null type
            assertTrue(fields.containsKey("_all"));
            assertNull(fields.get("_all"));
            assertEquals(3, fields.size());
        }
    }

    @Test
    public void dynamicIndexWithNoDynamicallyIndexedCollectionsSkipsClusterLookups() {
        String dynamicIndex = "{\"params\":{\"mapping\":{"
                + "\"default_field\":\"_all\",\"index_dynamic\":true,"
                + "\"types\":{\"inventory.airline\":{\"dynamic\":false,\"properties\":{"
                + "\"name\":{\"type\":\"text\"}}}}"
                + "}}}";
        try (MockedStatic<SearchIndexCache> cache = mockStatic(SearchIndexCache.class)) {
            cache.when(() -> SearchIndexCache.getIndex("travel", "idx")).thenReturn(dynamicIndex);

            Map<String, String> fields = FieldsContributor.getFieldNames("travel", "idx");

            assertEquals("text", fields.get("name"));
            assertEquals(2, fields.size());
        }
    }

    @Test
    public void unparseableIndexReturnsEmptyMapInsteadOfThrowing() {
        try (MockedStatic<SearchIndexCache> cache = mockStatic(SearchIndexCache.class)) {
            cache.when(() -> SearchIndexCache.getIndex(anyString(), anyString())).thenReturn("{broken");
            assertTrue(FieldsContributor.getFieldNames("travel", "idx").isEmpty());
        }
    }

    @Test
    public void missingDefaultFieldRegistersNullKey() {
        // NOTE: possible bug: an index without default_field puts a null key into the field map
        String index = "{\"params\":{\"mapping\":{\"types\":{\"t\":{\"properties\":{"
                + "\"name\":{\"type\":\"text\"}}}}}}}";
        try (MockedStatic<SearchIndexCache> cache = mockStatic(SearchIndexCache.class)) {
            cache.when(() -> SearchIndexCache.getIndex("travel", "idx")).thenReturn(index);

            Map<String, String> fields = FieldsContributor.getFieldNames("travel", "idx");

            assertTrue(fields.containsKey(null));
            assertEquals("text", fields.get("name"));
        }
    }
}
