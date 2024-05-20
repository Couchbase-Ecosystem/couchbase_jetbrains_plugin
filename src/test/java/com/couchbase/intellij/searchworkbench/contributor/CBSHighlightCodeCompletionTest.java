package com.couchbase.intellij.searchworkbench.contributor;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CBSHighlightCodeCompletionTest extends BasePlatformTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testCompletion() {
        String content = """
                        {
                            "highlight": {"<caret>"}
                        }
                """;
        List<String> completionResults = getCompletions(content);
        assertNotNull("No completions found", completionResults);

        for (String keyword : HighlightCbsContributor.keys) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testDontSuggestKeywordAlreadyExists() {
        String content = """
                         {
                             "highlight": { "fields": ["textField"],
                                            "<caret>"}
                             
                         }
                """;
        List<String> completionResults = getCompletions(content);

        assertNotNull("No completions found", completionResults);
        List<String> expected = List.of("style");
        for (String keyword : expected) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testSuggestStyleValues() {
        String content = """
                         {
                             "highlight": { "fields": ["textField"],
                                            "style": "<caret>"
                             }
                             
                         }
                """;
        List<String> completionResults = getCompletions(content);

        assertNotNull("No completions found", completionResults);
        List<String> expected = Arrays.asList("html", "ansi");
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
