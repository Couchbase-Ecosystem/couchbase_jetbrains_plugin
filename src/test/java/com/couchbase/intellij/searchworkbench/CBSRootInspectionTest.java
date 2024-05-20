package com.couchbase.intellij.searchworkbench;

import com.couchbase.intellij.searchworkbench.validator.RootObjectValidator;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class CBSRootInspectionTest extends JavaCodeInsightFixtureTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.enableInspections(CBSJsonKeyInspection.class); // Enable the inspection class
    }

    public void testValidHybrid() {
        String json = """
                {
                    "query": {
                         "query": "description:pool name:pool^5"
                    },
                    "knn": [
                        {
                            "k": 10,
                            "field": "vector_field",
                            "vector": [ 0.707106781186548, 0, 0.707106781186548 ]
                        }
                    ],
                    "size": 10,
                    "from": 0,
                    "fields": ["textField"],
                    "explain": true,
                    "sort": [
                    ],
                    "includeLocations": false,
                    "score": "none",
                    "search_after": ["field1Value", "5", "10.033205341869529", "1234"],
                    "search_before": ["field1Value", "5", "10.033205341869529", "1234"],
                    "limit": 10,
                    "offset": 0,
                    "collections": ["collection1", "collection2"]
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(0, highlights);
    }


    public void testValidQuery() {
        String json = """
                {
                    "query": {
                         "query": "description:pool name:pool^5"
                    },
                    "size": 10,
                    "from": 0,
                    "fields": ["textField"],
                    "explain": true,
                    "sort": [
                    ],
                    "includeLocations": false,
                    "score": "none",
                    "search_after": ["field1Value", "5", "10.033205341869529", "1234"],
                    "search_before": ["field1Value", "5", "10.033205341869529", "1234"],
                    "limit": 10,
                    "offset": 0,
                    "collections": ["collection1", "collection2"]
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(0, highlights);
    }

    public void testValidKnn() {
        String json = """
                {
                    "knn": [
                        {
                            "k": 10,
                            "field": "vector_field",
                            "vector": [ 0.707106781186548, 0, 0.707106781186548 ]
                        }
                    ],
                    "size": 10,
                    "from": 0,
                    "fields": ["textField"],
                    "explain": true,
                    "sort": [
                    ],
                    "includeLocations": false,
                    "score": "none",
                    "search_after": ["field1Value", "5", "10.033205341869529", "1234"],
                    "search_before": ["field1Value", "5", "10.033205341869529", "1234"],
                    "limit": 10,
                    "offset": 0,
                    "collections": ["collection1", "collection2"]
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(0, highlights);
    }

    public void testMissingQueryAndKNN() {
        String json = """
                {
                    "size": 10,
                    "from": 0,
                    "fields": ["textField"],
                    "explain": true,
                    "sort": [
                    ],
                    "includeLocations": false,
                    "score": "none",
                    "search_after": ["field1Value", "5", "10.033205341869529", "1234"],
                    "search_before": ["field1Value", "5", "10.033205341869529", "1234"],
                    "limit": 10,
                    "offset": 0,
                    "collections": ["collection1", "collection2"]
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(RootObjectValidator.getQueryOrKnnReqMessage())));
    }


    public void testInvalidKey() {
        String json = """
                {
                    "query": {
                         "query": "description:pool name:pool^5"
                    },
                    "size": 10,
                    "from": 0,
                    "fields": ["textField"],
                    "explain": true,
                    "sort": [
                    ],
                    "includeLocations": false,
                    "score": "none",
                    "search_after": ["field1Value", "5", "10.033205341869529", "1234"],
                    "search_before": ["field1Value", "5", "10.033205341869529", "1234"],
                    "limit": 10,
                    "offsets": 0,
                    "collections": ["collection1", "collection2"]
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(RootObjectValidator.getUnexpectedAttUnderRoot("offsets"))));
    }


    public void testInvalidDupKey() {
        String json = """
                {
                    "query": {
                         "query": "description:pool name:pool^5"
                    },
                    "size": 10,
                    "from": 0,
                    "fields": ["textField"],
                    "explain": true,
                    "sort": [
                    ],
                    "includeLocations": false,
                    "score": "none",
                    "search_after": ["field1Value", "5", "10.033205341869529", "1234"],
                    "search_before": ["field1Value", "5", "10.033205341869529", "1234"],
                    "limit": 10,
                    "limit": 10,
                    "collections": ["collection1", "collection2"]
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(RootObjectValidator.getSingleOccurrenceErrorMessage("limit"))));
    }


    public void testInvalidTypes() {
        String json = """
                {
                    "query": "",
                    "knn": "",
                    "ctl": 2,
                    "size": "",
                    "limit": "",
                    "from": "",
                    "offset": {},
                    "highlight": [],
                    "fields": "textField",
                    "facets": [],
                    "explain": 1,
                    "sort": {},
                    "includeLocations": 1,
                    "score": true,
                    "search_after": {},
                    "search_before": ",
                    "collections": "collection1,collection2"
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_JSON_OBJECT_FOR_KEY + "query")));
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_JSON_OBJECT_FOR_KEY + "ctl")));
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_INTEGER_FOR_KEY + "size")));

        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_INTEGER_FOR_KEY + "limit")));

        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_ARRAY_FOR_KEY + "knn")));

        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_INTEGER_FOR_KEY + "from")));

        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_INTEGER_FOR_KEY + "offset")));

        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_JSON_OBJECT_FOR_KEY + "highlight")));

        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_ARRAY_FOR_KEY + "fields")));

        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_JSON_OBJECT_FOR_KEY + "facets")));

        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_BOOLEAN_FOR_KEY + "explain")));

        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_ARRAY_FOR_KEY + "sort")));

        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_BOOLEAN_FOR_KEY + "includeLocations")));

        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_STRING_FOR_KEY + "score")));

        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_ARRAY_FOR_KEY + "search_after")));

        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_ARRAY_FOR_KEY + "search_before")));

        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_ARRAY_FOR_KEY + "collections")));


    }

    @NotNull
    private List<HighlightInfo> getHighlightInfos(String json) {
        myFixture.configureByText("test.cbs.json", json);
        List<HighlightInfo> highlights = myFixture.doHighlighting();
        return highlights.stream().filter(h -> h.getSeverity() == HighlightSeverity.ERROR).collect(Collectors.toList());
    }

}