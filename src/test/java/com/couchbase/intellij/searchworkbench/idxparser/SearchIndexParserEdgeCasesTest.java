package com.couchbase.intellij.searchworkbench.idxparser;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SearchIndexParserEdgeCasesTest {

    /** Wraps a "types" JSON object into a minimal index definition. */
    private static String withTypes(String typesJson) {
        return "{\"params\":{\"mapping\":{\"types\":" + typesJson + "}}}";
    }

    @Test
    public void missingTypesYieldsEmptyProperties() throws Exception {
        String index = "{\"params\":{\"mapping\":{\"default_field\":\"_all\"}}}";
        assertTrue(SearchIndexParser.extractPropertiesMap(index).isEmpty());
        assertEquals(Collections.emptyList(), SearchIndexParser.listCollections(index));
    }

    @Test
    public void nonObjectTypesIsIgnored() throws Exception {
        String index = withTypes("[\"a\",\"b\"]");
        assertTrue(SearchIndexParser.extractPropertiesMap(index).isEmpty());
        assertEquals(Collections.emptyList(), SearchIndexParser.listCollections(index));
    }

    @Test
    public void nestedPropertiesAreFlattenedWithDots() throws Exception {
        String index = withTypes("{\"t\":{\"properties\":{"
                + "\"a\":{\"properties\":{\"b\":{\"properties\":{\"c\":{\"type\":\"text\"}}}}}"
                + "}}}");
        Map<String, String> result = SearchIndexParser.extractPropertiesMap(index);
        assertEquals(Collections.singletonMap("a.b.c", "text"), result);
    }

    @Test
    public void multipleTypesAreMergedIntoOneMap() throws Exception {
        String index = withTypes("{"
                + "\"s1.c1\":{\"properties\":{\"name\":{\"type\":\"text\"}}},"
                + "\"s2.c2\":{\"properties\":{\"age\":{\"type\":\"number\"}}}"
                + "}");
        Map<String, String> result = SearchIndexParser.extractPropertiesMap(index);
        assertEquals(2, result.size());
        assertEquals("text", result.get("name"));
        assertEquals("number", result.get("age"));
        assertEquals(List.of("s1.c1", "s2.c2"), SearchIndexParser.listCollections(index));
    }

    @Test
    public void fieldsArrayUsesExplicitNameOrFallsBackToPropertyKey() throws Exception {
        String index = withTypes("{\"t\":{\"properties\":{"
                + "\"title\":{\"fields\":["
                + "{\"type\":\"text\",\"name\":\"titleAlt\"},"
                + "{\"type\":\"keyword\"}"
                + "]}"
                + "}}}");
        Map<String, String> result = SearchIndexParser.extractPropertiesMap(index);
        assertEquals("text", result.get("titleAlt"));
        // no explicit name: falls back to the property key
        assertEquals("keyword", result.get("title"));
        assertFalse(result.containsKey("title.titleAlt"));
    }

    @Test
    public void leafWithoutTypeIsRecordedWithEmptyString() throws Exception {
        String index = withTypes("{\"t\":{\"properties\":{"
                + "\"untyped\":{\"dynamic\":true}"
                + "}}}");
        Map<String, String> result = SearchIndexParser.extractPropertiesMap(index);
        assertTrue(result.containsKey("untyped"));
        assertEquals("", result.get("untyped"));
    }

    @Test
    public void indexDynamicDefaultsToFalseWhenMissing() throws Exception {
        assertFalse(SearchIndexParser.isIndexDynamic("{\"params\":{\"mapping\":{}}}"));
        assertTrue(SearchIndexParser.isIndexDynamic("{\"params\":{\"mapping\":{\"index_dynamic\":true}}}"));
        // NOTE: possible bug: string "true" is accepted as dynamic because Jackson's TextNode.asBoolean parses it
        assertTrue(SearchIndexParser.isIndexDynamic("{\"params\":{\"mapping\":{\"index_dynamic\":\"true\"}}}"));
    }

    @Test
    public void collectionDynamicFlagDefaultsToFalseForUnknownCollection() throws Exception {
        String index = withTypes("{\"s.c\":{\"dynamic\":true}}");
        assertTrue(SearchIndexParser.isCollectionDynamicallyIndexed(index, "s.c"));
        assertFalse(SearchIndexParser.isCollectionDynamicallyIndexed(index, "s.other"));
        assertFalse(SearchIndexParser.isCollectionDynamicallyIndexed("{}", "s.c"));
    }

    @Test
    public void defaultFieldIsNullWhenAbsent() throws Exception {
        assertNull(SearchIndexParser.getDefaultField("{\"params\":{\"mapping\":{}}}"));
        assertEquals("_all", SearchIndexParser.getDefaultField("{\"params\":{\"mapping\":{\"default_field\":\"_all\"}}}"));
    }

    @Test(expected = JsonProcessingException.class)
    public void invalidJsonThrows() throws Exception {
        SearchIndexParser.extractPropertiesMap("{not json");
    }
}
