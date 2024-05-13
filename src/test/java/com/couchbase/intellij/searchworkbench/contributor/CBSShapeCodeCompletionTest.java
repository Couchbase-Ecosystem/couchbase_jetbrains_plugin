package com.couchbase.intellij.searchworkbench.contributor;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CBSShapeCodeCompletionTest extends BasePlatformTestCase {

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
                                    "shape": {
                                        "<caret>"
                                    },
                                    "relation": "intersects"
                                }
                            }
                        }
                """;
        List<String> completionResults = getCompletions(content);
        assertNotNull("No completions found", completionResults);

        for (String keyword : Arrays.asList("coordinates", "type", "radius", "geometries")) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testCompletionCircus() {
        String content = """
                        {
                            "query": {
                                "field": "geojson",
                                "geometry": {
                                    "shape": {
                                        "type": "Circle",
                                        "<caret>"
                                    },
                                    "relation": "intersects"
                                }
                            }
                        }
                """;
        List<String> completionResults = getCompletions(content);
        assertNotNull("No completions found", completionResults);

        assertSize(2, completionResults);
        for (String keyword : Arrays.asList("coordinates", "radius")) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testCompletionGeometryCollection() {
        String content = """
                        {
                            "query": {
                                "field": "geojson",
                                "geometry": {
                                    "shape": {
                                        "type": "GeometryCollection",
                                        "<caret>"
                                    },
                                    "relation": "intersects"
                                }
                            }
                        }
                """;
        List<String> completionResults = getCompletions(content);
        assertNotNull("No completions found", completionResults);

        assertSize(1, completionResults);
        for (String keyword : List.of("geometries")) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testCompletionPolygon() {
        String content = """
                        {
                            "query": {
                                "field": "geojson",
                                "geometry": {
                                    "shape": {
                                        "type": "Polygon",
                                        "<caret>"
                                    },
                                    "relation": "intersects"
                                }
                            }
                        }
                """;
        List<String> completionResults = getCompletions(content);
        assertNotNull("No completions found", completionResults);

        assertSize(1, completionResults);
        for (String keyword : List.of("coordinates")) {
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
