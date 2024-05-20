package com.couchbase.intellij.searchworkbench.contributor;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CBSCtlCodeCompletionTest extends BasePlatformTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testCompletion() {
        String content = """
                        {
                            "ctl": {"<caret>"}
                        }
                """;
        List<String> completionResults = getCompletions(content);
        assertNotNull("No completions found", completionResults);

        for (String keyword : CtlCbsContributor.keys) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testDontSuggestKeywordAlreadyExists() {
        String content = """
                         {
                             "ctl": { "timeout": 10000,
                                            "<caret>"}
                             
                         }
                """;
        List<String> completionResults = getCompletions(content);

        assertNotNull("No completions found", completionResults);
        List<String> expected = List.of("consistency");
        for (String keyword : expected) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }


    public void testCompletionConsistency() {
        String content = """
                        {
                            "ctl": {
                                    "timeout": 10000,
                                    "consistency": {
                                        "<caret>"
                                    }
                                }
                        }
                """;
        List<String> completionResults = getCompletions(content);
        assertNotNull("No completions found", completionResults);

        for (String keyword : ConsistencyCbsContributor.keys) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testCompletionConsistencyExistingValues() {
        String content = """
                        {
                            "ctl": {
                                    "timeout": 10000,
                                    "consistency": {
                                        "level": "at_plus",
                                        "<caret>"
                                    }
                                }
                        }
                """;
        List<String> completionResults = getCompletions(content);
        assertNotNull("No completions found", completionResults);

        for (String keyword : Arrays.asList("vectors", "results")) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testSuggestLevelValues() {
        String content = """
                         {
                         "ctl": {
                                  "timeout": 10000,
                                  "consistency": {
                                      "vectors": {
                                          "searchIndexName": {
                                              "607/205096593892159": 2,
                                              "640/298739127912798": 4 
                                          }
                                      },
                                      "level": "<caret>",
                                      "results": "complete"
                                  }
                              }
                          }
                """;
        List<String> completionResults = getCompletions(content);

        assertNotNull("No completions found", completionResults);
        List<String> expected = Arrays.asList("at_plus", "not_bounded");
        for (String keyword : expected) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testSuggestResultsValues() {
        String content = """
                         {
                         "ctl": {
                                  "timeout": 10000,
                                  "consistency": {
                                      "vectors": {
                                          "searchIndexName": {
                                              "607/205096593892159": 2,
                                              "640/298739127912798": 4
                                          }
                                      },
                                      "results": "<caret>"
                                  }
                              }
                          }
                """;
        List<String> completionResults = getCompletions(content);

        assertNotNull("No completions found", completionResults);
        List<String> expected = Arrays.asList("complete");
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
