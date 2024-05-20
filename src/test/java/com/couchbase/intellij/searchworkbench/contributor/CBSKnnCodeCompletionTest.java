package com.couchbase.intellij.searchworkbench.contributor;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CBSKnnCodeCompletionTest extends BasePlatformTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testEmptyJsonCompletion() {
        String content = """
                        { "knn": { "<caret>"}  }
                """;
        List<String> completionResults = getCompletions(content);
        assertNotNull("No completions found", completionResults);

        for (String keyword : KnnCbsContributor.keys) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testDontSuggestKeywordAlreadyExists() {
        String content = """
                         {
                             "knn": {
                                "field": "vector_field",
                                "<caret>"
                             }
                             
                         }
                """;
        List<String> completionResults = getCompletions(content);

        assertNotNull("No completions found", completionResults);
        List<String> expected = Arrays.asList("k", "vector");
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
}
