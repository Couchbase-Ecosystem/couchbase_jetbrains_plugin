package com.couchbase.intellij.searchworkbench.idxparser;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SearchIndexParserTest {

    @Test
    public void testVector() throws Exception {
        String fileContent = getFile("vector_index.json");
        Map<String, String> result = SearchIndexParser.extractPropertiesMap(fileContent);

        assertTrue(result.containsKey("embedding"));
        assertEquals(result.get("embedding"), "vector");
        assertTrue(result.containsKey("metadata"));
        assertTrue(result.containsKey("text"));
        assertEquals(result.get("text"), "text");
        assertTrue(SearchIndexParser.isIndexDynamic(fileContent));
        List<String> expected = List.of("_default._default");
        assertEquals(expected, SearchIndexParser.listCollections(fileContent));
        assertEquals("_all", SearchIndexParser.getDefaultField(fileContent));
        assertFalse(SearchIndexParser.isCollectionDynamicallyIndexed(fileContent, "_default._default"));
    }

    @Test
    public void testSearchableAs() throws Exception {
        String fileContent = getFile("searchable_as.json");
        Map<String, String> result = SearchIndexParser.extractPropertiesMap(fileContent);

        assertTrue(result.containsKey("someChild.namea"));
        assertEquals(result.get("someChild.namea"), "text");
        assertTrue(result.containsKey("country"));
        assertTrue(result.containsKey("type"));
        assertTrue(result.containsKey("name"));
        assertTrue(SearchIndexParser.isIndexDynamic(fileContent));
        List<String> expected = Arrays.asList("inventory.airline", "inventory.landmark");
        assertEquals(expected, SearchIndexParser.listCollections(fileContent));
        assertEquals("_all2", SearchIndexParser.getDefaultField(fileContent));
        assertTrue(SearchIndexParser.isCollectionDynamicallyIndexed(fileContent, "inventory.airline"));
        assertTrue(SearchIndexParser.isCollectionDynamicallyIndexed(fileContent, "inventory.landmark"));
    }


    @Test
    public void testSimpleDefinition() throws Exception {
        String fileContent = getFile("simple_definition.json");
        Map<String, String> result = SearchIndexParser.extractPropertiesMap(fileContent);

        assertTrue(result.containsKey("child.secondChild.country"));
        assertTrue(result.containsKey("country"));
        assertTrue(result.containsKey("name"));

        List<String> expected = Arrays.asList("airline");
        assertEquals(expected, SearchIndexParser.listCollections(fileContent));
    }


    private String getFile(String fileName) throws Exception {
        return new String(SearchIndexParserTest.class.getClassLoader().getResourceAsStream("search_idx/" + fileName).readAllBytes());
    }

}

