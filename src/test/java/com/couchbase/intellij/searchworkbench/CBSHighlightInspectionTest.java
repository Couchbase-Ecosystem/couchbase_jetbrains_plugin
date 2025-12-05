package com.couchbase.intellij.searchworkbench;

import com.couchbase.intellij.searchworkbench.validator.HighlightObjectValidator;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

//TODO: highlight with vector sear only? -- Need to test
public class CBSHighlightInspectionTest extends CodeInsightFixtureTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.enableInspections(CBSJsonKeyInspection.class); // Enable the inspection class
    }

    public void testValidHighlight() {
        String json = """
                {
                    "query": {
                         "query": "description:pool name:pool^5"
                    },
                    "highlight": {
                    
                            "style": "html",
                            "fields": ["textField"]
                    
                    }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(0, highlights);
    }

    public void testValidEmptyHighlight() {
        String json = """
                {
                    "query": {
                         "query": "description:pool name:pool^5"
                    },
                    "highlight": {}
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(0, highlights);
    }

    public void testDoubleVal() {
        String json = """
                {
                    "query": {
                         "query": "description:pool name:pool^5"
                    },
                    "highlight": {
                    
                            "style": "html",
                            "style": "html",
                            "fields": ["textField"],
                            "fields": ["textField"]
                    
                    }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(2, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(HighlightObjectValidator.singleKeyOccurrenceMessage("style"))));
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(HighlightObjectValidator.singleKeyOccurrenceMessage("fields"))));
    }

    public void testInvalidField() {
        String json = """
                {
                    "query": {
                         "query": "description:pool name:pool^5"
                    },
                    "highlight": {
                            "style": "ansi",
                            "fielsd": ["textField"]
                    
                    }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(HighlightObjectValidator.getUnexpectedAttUnderHighlight("fielsd"))));
    }


    public void testInvalidAttType() {
        String json = """
                {
                    "query": {
                         "query": "description:pool name:pool^5"
                    },
                    "highlight": {
                    
                            "style": 2,
                            "fields": "textField"
                    
                    }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(2, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_STRING_FOR_KEY + "style")));
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_ARRAY_FOR_KEY + "fields")));
    }


    public void testInvalidStyleValue() {
        String json = """
                {
                    "query": {
                         "query": "description:pool name:pool^5"
                    },
                    "highlight": {
                    
                            "style": "ansiml",
                            "fields": ["textField"]
                    
                    }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(HighlightObjectValidator.getStyleValuesErrorMessage())));
    }

    @NotNull
    private List<HighlightInfo> getHighlightInfos(String json) {
        myFixture.configureByText("test.cbs.json", json);
        List<HighlightInfo> highlights = myFixture.doHighlighting();
        return highlights.stream().filter(h -> h.getSeverity() == HighlightSeverity.ERROR).collect(Collectors.toList());
    }

}