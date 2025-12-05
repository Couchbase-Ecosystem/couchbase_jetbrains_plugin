package com.couchbase.intellij.searchworkbench;

import com.couchbase.intellij.searchworkbench.validator.MatchAllNoneObjectValidator;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class CBSMatchAllNodeInspectionTest extends CodeInsightFixtureTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.enableInspections(CBSJsonKeyInspection.class); // Enable the inspection class
    }

    public void testValidMatchAll() {
        String json = """
                {
                     "query": {
                         "match_all": {}
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);

        assertFalse(hasErrors);
    }

    public void testInvalidMatchAll() {
        String json = """
                {
                     "query": {
                         "match_all": {
                            "field": "test"
                         }
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(MatchAllNoneObjectValidator.getNoAttributesAllowed("match_all"))));
    }


    public void testValidMatchNone() {
        String json = """
                {
                     "query": {
                         "match_none": {}
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);

        assertFalse(hasErrors);
    }


    public void testInvalidMatchnone() {
        String json = """
                {
                     "query": {
                         "match_none": {
                            "field": "test"
                         }
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(MatchAllNoneObjectValidator.getNoAttributesAllowed("match_none"))));
    }


    @NotNull
    private List<HighlightInfo> getHighlightInfos(String json) {
        myFixture.configureByText("test.cbs.json", json);
        List<HighlightInfo> highlights = myFixture.doHighlighting();
        return highlights.stream().filter(h -> h.getSeverity() == HighlightSeverity.ERROR).collect(Collectors.toList());
    }

}