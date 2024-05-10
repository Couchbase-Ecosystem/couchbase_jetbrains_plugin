package com.couchbase.intellij.searchworkbench;

import com.couchbase.intellij.searchworkbench.validator.QueryObjectValidator;
import com.couchbase.intellij.searchworkbench.validator.RootObjectValidator;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class CBSQueryInspectionTest extends JavaCodeInsightFixtureTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.enableInspections(CBSJsonKeyInspection.class); // Enable the inspection class
    }

    public void testInvalidTopLevelAttKey() {
        String json = """
                {
                    "invalidKey": ""
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(2, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(RootObjectValidator.getUnexpectedAttUnderRoot("invalidKey"))));
    }

    public void testInvalidTopLevelObjKey() {
        String json = """
                {
                    "invalidKey": {}
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(2, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(RootObjectValidator.getUnexpectedAttUnderRoot("invalidKey"))));
    }


    public void testValidQuery() {
        String json = """
                {
                    "query": {
                        "query": "description:pool name:pool^5"
                    }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        // Check for error-level highlights
        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);

        assertFalse(hasErrors); // Should have no errors for valid JSON
    }

    public void testInvalidQueryTypeTopLevel() {
        String json = """
                {
                     "query": "description:pool name:pool^5"
                    
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        // Check for error-level highlights
        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals("Expected JSON object for key: query")));
    }

    public void testInvalidQueryTypeNestedLevel() {
        String json = """
                {
                    "query": {
                        "query": []
                    }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        // Check for error-level highlights
        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals("Expected string for key: query")));
    }

    public void testInvalidNestedQueryAttribute() {
        String json = """
                {
                    "query": {
                        "vector": []
                    }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(2, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(QueryObjectValidator.getUnexpectedAttUnderQuery("vector"))));
    }


    public void testInvalidEmptyQuery() {
        String json = """
                {
                    "query": {}
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        // Check for error-level highlights
        assertSize(2, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals("'query' can't be empty")));
    }


    @NotNull
    private List<HighlightInfo> getHighlightInfos(String json) {
        myFixture.configureByText("test.cbs.json", json);
        List<HighlightInfo> highlights = myFixture.doHighlighting();
        return highlights.stream().filter(h -> h.getSeverity() == HighlightSeverity.ERROR).collect(Collectors.toList());
    }

}