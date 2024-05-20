package com.couchbase.intellij.searchworkbench;

import com.couchbase.intellij.searchworkbench.validator.KnnObjectValidator;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class CBSKNNInspectionTest extends JavaCodeInsightFixtureTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.enableInspections(CBSJsonKeyInspection.class); // Enable the inspection class
    }

    public void testValidKNN() {
        String json = """
                {
                    "knn": [
                             {
                                 "k": 10,
                                 "field": "vector_field",
                                 "vector": [ 0.707106781186548, 0, 0.707106781186548 ]
                             }
                         ]
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(0, highlights);
    }

    public void testDoubleVal() {
        String json = """
                {
                    "knn": [
                             {
                                 "k": 10,
                                 "k": 10,
                                 "field": "vector_field",
                                 "field": "vector_field",
                                 "vector": [ 0.707106781186548, 0, 0.707106781186548 ],
                                 "vector": [ 0.707106781186548, 0, 0.707106781186548 ]
                             }
                         ]
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(3, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(KnnObjectValidator.singleKeyOccurrenceMessage("k"))));
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(KnnObjectValidator.singleKeyOccurrenceMessage("field"))));
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(KnnObjectValidator.singleKeyOccurrenceMessage("vector"))));
    }

    public void testInvalidField() {
        String json = """
                {
                    "knn": [
                             {
                                 "k": 10,
                                 "field": "vector_field",
                                 "vector": [ 0.707106781186548, 0, 0.707106781186548 ],
                                 "query": "test"
                             }
                         ]
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(KnnObjectValidator.getUnexpectedAttUnderKnn("query"))));
    }

    @NotNull
    private List<HighlightInfo> getHighlightInfos(String json) {
        myFixture.configureByText("test.cbs.json", json);
        List<HighlightInfo> highlights = myFixture.doHighlighting();
        return highlights.stream().filter(h -> h.getSeverity() == HighlightSeverity.ERROR).collect(Collectors.toList());
    }

}