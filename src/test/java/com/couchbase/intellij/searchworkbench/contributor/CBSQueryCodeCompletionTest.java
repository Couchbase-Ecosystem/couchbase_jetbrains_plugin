package com.couchbase.intellij.searchworkbench.contributor;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CBSQueryCodeCompletionTest extends BasePlatformTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testQueryCompletion() {
        String content = """
                        {
                            "query": {
                                "<caret>"
                            }
                        }
                """;
        List<String> completionResults = getCompletions(content);
        assertNotNull("No completions found", completionResults);

        for (String keyword : QueryCbsContributor.allQueryKeys) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testQueryCompletionConjuncts() {
        String content = """
                        {
                            "query": {
                                "conjuncts":[
                                    {
                                        "<caret>"
                                    }
                                ]
                            }
                        }
                """;
        List<String> completionResults = getCompletions(content);
        assertNotNull("No completions found", completionResults);

        for (String keyword : QueryCbsContributor.allQueryKeys) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testQueryCompletionDisjuncts() {
        String content = """
                        {
                            "query": {
                                "disjuncts":[
                                    {
                                        "<caret>"
                                    }
                                ]
                            }
                        }
                """;
        List<String> completionResults = getCompletions(content);
        assertNotNull("No completions found", completionResults);

        for (String keyword : QueryCbsContributor.allQueryKeys) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testQueryCompletionDisjunctsNoRecommendation() {
        String content = """
                        {
                            "query": {
                                "disjuncts":[
                                    
                                        "<caret>"
                                    
                                ]
                            }
                        }
                """;
        List<String> completionResults = getCompletions(content);
        assertSize(0, completionResults);
    }

    public void testQueryCompletionConjunctsNoRecommendation() {
        String content = """
                        {
                            "query": {
                                "conjuncts":[
                                        "<caret>"
                                ]
                            }
                        }
                """;
        List<String> completionResults = getCompletions(content);
        assertSize(0, completionResults);
    }

    public void testQueryCompletionEmpty() {
        String content = """
                        {
                            "query": {
                                "query": "",
                                <caret>
                            }
                        }
                """;
        List<String> completionResults = getCompletions(content);
        assertSize(0, completionResults);
    }

    public void testMatch() {
        String content = """
                        {
                            "query": {
                                "match": "best great",
                                "<caret>"
                            }
                        }
                """;
        List<String> completionResults = getCompletions(content);
        for (String keyword : Arrays.asList("field", "analyzer", "operator", "boost", "fuzziness", "prefix_length")) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testOperator() {
        String content = """
                        {
                            "query": {
                                "operator": "or"
                                "<caret>"
                            }
                        }
                """;
        List<String> completionResults = getCompletions(content);
        for (String keyword : Arrays.asList("field", "analyzer", "boost", "fuzziness", "prefix_length")) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testBool() {
        String content = """
                        {
                            "query": {
                                "bool": true
                                "<caret>"
                            }
                        }
                """;
        List<String> completionResults = getCompletions(content);
        for (String keyword : List.of("field", "boost")) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testPrefix() {
        String content = """
                        {
                            "query": {
                                "prefix": ""
                                "<caret>"
                            }
                        }
                """;
        List<String> completionResults = getCompletions(content);
        for (String keyword : List.of("field", "boost")) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testRegex() {
        String content = """
                        {
                            "query": {
                                "regexp": ""
                                "<caret>"
                            }
                        }
                """;
        List<String> completionResults = getCompletions(content);
        for (String keyword : List.of("field", "boost")) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testTerm() {
        String content = """
                        {
                            "query": {
                                "term": ""
                                "<caret>"
                            }
                        }
                """;
        List<String> completionResults = getCompletions(content);
        for (String keyword : List.of("field", "boost", "fuzziness")) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testTerms() {
        String content = """
                        {
                            "query": {
                                "terms": []
                                "<caret>"
                            }
                        }
                """;
        List<String> completionResults = getCompletions(content);
        for (String keyword : List.of("field", "boost")) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testWildcard() {
        String content = """
                        {
                            "query": {
                                "wildcard": "*"
                                "<caret>"
                            }
                        }
                """;
        List<String> completionResults = getCompletions(content);
        for (String keyword : List.of("field", "boost")) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }


    public void testNumericMin() {
        String content = """
                        {
                            "query": {
                                "min": 3,
                                "<caret>"
                            }
                        }
                """;
        List<String> completionResults = getCompletions(content);
        for (String keyword : List.of("max", "inclusive_min", "inclusive_max", "field", "boost")) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testNumericMinMax() {
        String content = """
                        {
                            "query": {
                                "min": 3,
                                "min": 10,
                                "<caret>"
                            }
                        }
                """;
        List<String> completionResults = getCompletions(content);
        for (String keyword : List.of("inclusive_min", "inclusive_max", "field", "boost")) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }


    public void testDateStart() {
        String content = """
                        {
                            "query": {
                                "start": "2001-10-09T10:20:30-08:00",
                                "<caret>"
                            }
                        }
                """;
        List<String> completionResults = getCompletions(content);
        for (String keyword : List.of("end", "inclusive_start", "inclusive_end", "field", "boost")) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testDateStartInclusiveEnd() {
        String content = """
                        {
                            "query": {
                                "start": "2001-10-09T10:20:30-08:00",
                                "inclusive_end": false,
                                "<caret>"
                            }
                        }
                """;
        List<String> completionResults = getCompletions(content);
        for (String keyword : List.of("end", "inclusive_start", "field", "boost")) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testDateInclusiveEnd() {
        String content = """
                        {
                            "query": {
                                "inclusive_start": false,
                                "<caret>"
                            }
                        }
                """;
        List<String> completionResults = getCompletions(content);
        for (String keyword : List.of("start", "end", "inclusive_end", "field", "boost")) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }


    public void testDistance() {
        String content = """
                        {
                            "query": {
                               "location": {
                                 "lon": -2.235143,
                                 "lat": 53.482358
                               },
                                "<caret>"
                           }
                        }
                """;
        List<String> completionResults = getCompletions(content);
        for (String keyword : List.of("distance", "field", "boost")) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testRectangle() {
        String content = """
                        {
                            "query": {
                                  "top_left": [-2.235143, 53.482358],
                                  "<caret>"
                              }
                        }
                """;
        List<String> completionResults = getCompletions(content);
        for (String keyword : List.of("bottom_right", "field", "boost")) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testPolygon() {
        String content = """
                        {
                            "query": {
                                  "polygon_points": [
                                     "37.79393211306212,-122.44234633404847",
                                     "37.77995881733997,-122.43977141339417",
                                     "37.788031092020155,-122.42925715405579",
                                     "37.79026946582319,-122.41149020154114",
                                     "37.79571192027403,-122.40735054016113",
                                     "37.79393211306212,-122.44234633404847"
                                   ],
                                  "<caret>"
                              }
                        }
                """;
        List<String> completionResults = getCompletions(content);
        for (String keyword : List.of("field", "boost")) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }


    public void testMust() {
        String content = """
                        {
                             "query":{
                                 "must":{
                                     "<caret>"
                                 }
                             }
                         }
                """;
        List<String> completionResults = getCompletions(content);
        assertNotNull("No completions found", completionResults);

        for (String keyword : List.of("conjuncts")) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testMustNot() {
        String content = """
                        {
                             "query":{
                                 "must_not":{
                                     "<caret>"
                                 }
                             }
                         }
                """;
        List<String> completionResults = getCompletions(content);
        assertNotNull("No completions found", completionResults);

        for (String keyword : List.of("disjuncts")) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testShould() {
        String content = """
                        {
                             "query":{
                                 "disjuncts":{
                                     "<caret>"
                                 }
                             }
                         }
                """;
        List<String> completionResults = getCompletions(content);
        assertNotNull("No completions found", completionResults);

        for (String keyword : List.of("disjuncts")) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }


    public void testTopLeft() {
        String content = """
                       {
                            "query": {
                                "top_left": {"<caret>"},
                                "bottom_right": {
                                  "lon": 28.955043,
                                  "lat": 40.991862
                                },
                                "field": "geo"
                            }
                          }
                """;
        List<String> completionResults = getCompletions(content);
        assertNotNull("No completions found", completionResults);

        for (String keyword : LocationCbsContributor.keys) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testBottomRight() {
        String content = """
                       {
                            "query": {
                                "top_left":  [-2.235143, 53.482358],
                                "bottom_right": {
                                  "lon": 28.955043,
                                  "<caret>"
                                },
                                "field": "geo"
                            }
                          }
                """;
        List<String> completionResults = getCompletions(content);
        assertNotNull("No completions found", completionResults);

        for (String keyword : List.of("lat")) {
            assertTrue("Expected completion '" + keyword + "' not found", completionResults.contains(keyword));
        }
    }

    public void testLocation() {
        String content = """
                       {
                            "query": {
                                   "location": {
                                     "<caret>"
                                   },
                                     "distance": "100mi",
                                     "field": "geo"
                               }
                       }
                """;
        List<String> completionResults = getCompletions(content);
        assertNotNull("No completions found", completionResults);

        for (String keyword : LocationCbsContributor.keys) {
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
