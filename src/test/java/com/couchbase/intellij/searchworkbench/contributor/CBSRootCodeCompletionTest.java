package com.couchbase.intellij.searchworkbench.contributor;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class CBSRootCodeCompletionTest extends BasePlatformTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }


    public void testEmptyJsonCompletion() {
        String content = "{<caret>}";
        List<String> completionResults = getCompletions(content);

        assertNotNull("No completions found", completionResults);

        List<String> expected = CBSCodeCompletionContributor.topLevelKeywords.stream().map(e -> "\"" + e + "\"").collect(Collectors.toList());
        for (String keyword : expected) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testDontSuggestKeywordAlreadyExists() {
        String content = """
                         {
                             "query": {},
                             <caret>
                         }
                """;
        List<String> completionResults = getCompletions(content);

        assertNotNull("No completions found", completionResults);
        List<String> expected = CBSCodeCompletionContributor.topLevelKeywords.stream()
                .filter(e -> !e.equals("query"))
                .map(e -> "\"" + e + "\"").toList();
        for (String keyword : expected) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    @Nullable
    private List<String> getCompletions(String content) {
        myFixture.configureByText("test.cbs.json", content);
        myFixture.complete(CompletionType.BASIC);
        List<String> completionResults = myFixture.getLookupElementStrings();
        return completionResults;
    }

    public void testScoreCompletion() {
        String content = """
                {
                    "query": {
                        "query": "your_query_here"
                    },
                    "fields": ["*"],
                    "score": "<caret>"
                }
                """;

        List<String> completionResults = getCompletions(content);

        assertNotNull("No completions found", completionResults);
        assertTrue("Expected completion 'none' not found", completionResults.contains("none"));
    }
}
