package com.couchbase.intellij.searchworkbench.contributor;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CBSGeometryCodeCompletionTest extends BasePlatformTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testCompletion() {
        String content = """
                        {
                            "query": {
                                "field": "geojson",
                                "geometry": {
                                    "<caret>"
                                }
                            }
                        }
                """;
        List<String> completionResults = getCompletions(content);
        assertNotNull("No completions found", completionResults);

        for (String keyword : GeometryCbsContributor.keys) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testCompletionRelation() {
        String content = """
                        {
                            "query": {
                                "field": "geojson",
                                "geometry": {
                                     "relation": "<caret>"
                                }
                            }
                        }
                """;
        List<String> completionResults = getCompletions(content);
        assertNotNull("No completions found", completionResults);

        for (String keyword : Arrays.asList("intersects", "intersects", "within")) {
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
