package com.couchbase.intellij.searchworkbench;

import com.couchbase.intellij.searchworkbench.validator.BooleanObjectValidator;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class CBSBooleanInspectionTest extends JavaCodeInsightFixtureTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.enableInspections(CBSJsonKeyInspection.class);
    }

    public void testValidMust() {
        String json = """
                {
                    "query":{
                         "must":{
                             "conjuncts":[
                                 {
                                     "field": "reviews.content",
                                     "match": "location"
                                 },
                                 {
                                     "field":"reviews.content",
                                     "match_phrase": "nice view"
                                 }
                             ]
                         }
                     }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(0, highlights);
    }

    public void testInvalidMustDisjuncts() {
        String json = """
                {
                    "query":{
                         "must":{
                             "disjuncts":[
                                 {
                                     "field": "reviews.content",
                                     "match": "location"
                                 },
                                 {
                                     "field":"reviews.content",
                                     "match_phrase": "nice view"
                                 }
                             ]
                         }
                     }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(BooleanObjectValidator.getMustConjunctsErrorMessage())));
    }

    public void testInvalidMust() {
        String json = """
                {
                    "query":{
                         "must":{
                             "conjuncts":[
                                 {
                                     "field": "reviews.content",
                                     "match": "location"
                                 },
                                 {
                                     "field":"reviews.content",
                                     "match_phrase": "nice view"
                                 }
                             ],
                             "min":5
                         }
                     }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(BooleanObjectValidator.getMustConjunctsErrorMessage())));
    }


    public void testValidMustNot() {
        String json = """
                {
                    "query":{
                          "must_not":{
                              "disjuncts":[
                                  {
                                      "field": "free_breakfast",
                                      "bool": false
                                  },
                                  {
                                      "field": "ratings.Cleanliness",
                                      "min": 1,
                                      "max": 3
                                  }
                              ]
                          }
                      }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(0, highlights);
    }

    public void testInvalidMustNotConjunct() {
        String json = """
                {
                    "query":{
                         "must_not":{
                             "conjuncts":[
                                 {
                                     "field": "reviews.content",
                                     "match": "location"
                                 },
                                 {
                                     "field":"reviews.content",
                                     "match_phrase": "nice view"
                                 }
                             ]
                         }
                     }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(BooleanObjectValidator.getMustNotDisjunctsErrorMessage())));
    }

    public void testInvalidMustNotMin() {
        String json = """
                {
                    "query":{
                          "must_not":{
                              "disjuncts":[
                                  {
                                      "field": "free_breakfast",
                                      "bool": false
                                  },
                                  {
                                      "field": "ratings.Cleanliness",
                                      "min": 1,
                                      "max": 3
                                  }
                              ],
                              "min": 1
                          }
                      }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(BooleanObjectValidator.getMustNotDisjunctsErrorMessage())));
    }


    public void testValidShould() {
        String json = """
                {
                    "query":{
                          "should":{
                              "disjuncts":[
                                  {
                                      "field": "free_parking",
                                      "bool": true
                                  },
                                  {
                                      "field": "free_internet",
                                      "bool": true
                                  }
                              ],
                              "min": 1
                          }
                      }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(0, highlights);
    }


    public void testInvalidShould() {
        String json = """
                {
                    "query":{
                          "should":{
                              "conjuncts":[
                                  {
                                      "field": "free_parking",
                                      "bool": true
                                  },
                                  {
                                      "field": "free_internet",
                                      "bool": true
                                  }
                              ],
                              "min": 1
                          }
                      }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(BooleanObjectValidator.getShouldErrorMessage())));
    }


    @NotNull
    private List<HighlightInfo> getHighlightInfos(String json) {
        myFixture.configureByText("test.cbs.json", json);
        List<HighlightInfo> highlights = myFixture.doHighlighting();
        return highlights.stream().filter(h -> h.getSeverity() == HighlightSeverity.ERROR).collect(Collectors.toList());
    }

}