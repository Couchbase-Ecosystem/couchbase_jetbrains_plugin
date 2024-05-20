package com.couchbase.intellij.searchworkbench;

import com.couchbase.intellij.searchworkbench.validator.CBSValidationUtil;
import com.couchbase.intellij.searchworkbench.validator.QueryTypeObjectValidator;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class CBSQueryTypesInspectionTest extends JavaCodeInsightFixtureTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.enableInspections(CBSJsonKeyInspection.class); // Enable the inspection class
    }

    public void testValidMatch() {
        String json = """
                {
                    "query": {
                        "match": "best great",
                        "field": "reviews.content",
                        "analyzer": "standard",
                        "operator": "or"
                    }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);
        assertFalse(hasErrors);
    }

    public void testInvalidMatchTypes() {
        String json = """
                {
                    "query": {
                        "match": [],
                        "field": [],
                        "analyzer": 2,
                        "operator": [],
                        "fuzziness": "2",
                        "prefix_length": "4"
                    }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(6, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_STRING_FOR_KEY + "match")));
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_STRING_FOR_KEY + "field")));
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_STRING_FOR_KEY + "analyzer")));
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_STRING_FOR_KEY + "operator")));
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_INTEGER_FOR_KEY + "fuzziness")));
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_INTEGER_FOR_KEY + "prefix_length")));
    }

    public void testInvalidValidMatchOperator() {
        String json = """
                {
                    "query": {
                        "match": "best great",
                        "field": "reviews.content",
                        "analyzer": "standard",
                        "operator": "not"
                    }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(QueryTypeObjectValidator.invalidOperatorMessage())));
    }

    public void testValidMatchWithoutOperator() {
        String json = """
                {
                    "query": {
                        "match": "best great",
                        "field": "reviews.content",
                        "analyzer": "standard"
                    }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(QueryTypeObjectValidator.matchWithSpaceMessage())));
    }

    public void testInvalidValidMatch() {
        String json = """
                {
                    "query": {
                        "match": "best",
                        "field": "reviews.content",
                        "analyzer": "standard",
                        "min": 2
                    }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSValidationUtil.getUnexpectedAttForQuery("min", "match"))));
    }

    public void testInvalidValidNumericRange() {
        String json = """
                {
                    "query": {
                        "min": 2,
                        "match": "best",
                        "field": "reviews.content"                        
                    }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSValidationUtil.getUnexpectedAttForQuery("match", "numeric range"))));
    }


    public void testValidMatchPhrase() {
        String json = """
                {
                    "query": {
                        "match_phrase": "best great",
                        "field": "reviews.content",
                        "analyzer": "standard",
                        "operator": "or",
                        "fuzziness": 2
                    }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);
        assertFalse(hasErrors);
    }

    public void testInvalidMatchPhrase() {
        String json = """
                {
                    "query": {
                        "match_phrase": [],
                        "field": "reviews.content",
                        "analyzer": "standard",
                        "operator": "or",
                        "fuzziness": 2
                    }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_STRING_FOR_KEY + "match_phrase")));
    }


    public void testValidBoolean() {
        String json = """
                {
                     "query": {
                         "field": "pets_ok",
                         "bool": true
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);
        assertFalse(hasErrors);
    }

    public void testInvalidBoolean() {
        String json = """
                {
                    "query": {
                        "field": "pets_ok",
                        "bool": "true"
                    }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_BOOLEAN_FOR_KEY + "bool")));
    }


    public void testValidPrefix() {
        String json = """
                {
                     "query": {
                          "prefix": "inter",
                          "field": "reviews.content"
                      }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);
        assertFalse(hasErrors);
    }

    public void testInvalidPrefix() {
        String json = """
                    {
                        "query": {
                             "prefix": ["inter"],
                             "field": "reviews.content"
                         }
                    }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_STRING_FOR_KEY + "prefix")));
    }

    public void testInvalidPrefixWithFuzziness() {
        String json = """
                    {
                        "query": {
                             "prefix": "inter",
                             "field": "reviews.content",
                             "fuzziness": 2
                         }
                    }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSValidationUtil.getUnexpectedAttForQuery("fuzziness", "prefix"))));
    }


    public void testValidRegex() {
        String json = """
                {
                     "query": {
                           "regexp": "plan.+",
                           "field": "reviews.content"
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);
        assertFalse(hasErrors);
    }

    public void testInvalidRegex() {
        String json = """
                    {
                        "query": {
                           "regexp": ["plan.+"],
                           "field": "reviews.content"
                        }
                    }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_STRING_FOR_KEY + "regexp")));
    }


    public void testValidTerm() {
        String json = """
                {
                      "query": {
                          "term": "interest",
                          "field": "reviews.content",
                          "fuzziness": 2
                      }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);
        assertFalse(hasErrors);
    }

    public void testInvalidTermAnalyzer() {
        String json = """
                    {
                      "query": {
                          "term": "interest",
                          "field": "reviews.content",
                          "fuzziness": 2,
                          "analyzer": "standard"
                      }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSValidationUtil.getUnexpectedAttForQuery("analyzer", "term"))));
    }

    public void testInvalidTerm() {
        String json = """
                    {
                      "query": {
                          "term": ["interest"],
                          "field": "reviews.content",
                          "fuzziness": 2
                      }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_STRING_FOR_KEY + "term")));
    }


    public void testValidTerms() {
        String json = """
                {
                      "query": {
                           "terms": ["nice", "view"],
                           "field": "reviews.content"
                       }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);
        assertFalse(hasErrors);
    }

    public void testInvalidTermsAnalyzer() {
        String json = """
                    {
                      "query": {
                          "terms": ["nice", "view"],
                          "field": "reviews.content",
                          "analyzer": "standard"
                      }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSValidationUtil.getUnexpectedAttForQuery("analyzer", "terms"))));
    }

    public void testInvalidTerms() {
        String json = """
                    {
                      "query": {
                          "terms": "interest",
                          "field": "reviews.content"
                      }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_ARRAY_FOR_KEY + "terms")));
    }


    public void testValidWildcard() {
        String json = """
                {
                     "query": {
                          "wildcard": "inter*",
                          "field": "reviews.content"
                      }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);
        assertFalse(hasErrors);
    }

    public void testInvalidWildcard() {
        String json = """
                    {
                        "query": {
                          "wildcard": ["inter*"],
                          "field": "reviews.content"
                      }
                    }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_STRING_FOR_KEY + "wildcard")));
    }

    public void testValidCIDR() {
        String json = """
                {
                     "query": {
                          "cidr": "2.7.13.0/24",
                          "field": "ipv4"
                      }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);
        assertFalse(hasErrors);
    }

    public void testInvalidCIDR() {
        String json = """
                    {
                        "query": {
                          "cidr": ["2.7.13.0/24"],
                          "field": "ipv4"
                      }
                    }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_STRING_FOR_KEY + "cidr")));
    }


    public void testValidNumericRange() {
        String json = """
                {
                     "query": {
                           "min": 3,
                           "max": 5,
                           "inclusive_min": false,
                           "inclusive_max": true,
                           "field": "reviews.ratings.Cleanliness"
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);
        assertFalse(hasErrors);
    }

    public void testInvalidMinMax() {
        String json = """
                {
                     "query": {
                           "inclusive_min": false,
                           "inclusive_max": true,
                           "field": "reviews.ratings.Cleanliness"
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(QueryTypeObjectValidator.minOrMaxRequiredMessage())));
    }


    public void testInvalidRange() {
        String json = """
                {
                     "query": {
                           "min": 3,
                           "max": 5,
                           "inclusive_min": "false",
                           "inclusive_max": "true",
                           "field": "reviews.ratings.Cleanliness"
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(2, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_BOOLEAN_FOR_KEY + "inclusive_min")));
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_BOOLEAN_FOR_KEY + "inclusive_max")));
    }


    public void testValidDateRange() {
        String json = """
                {
                     "query": {
                         "start": "2001-10-09T10:20:30-08:00",
                         "end": "2016-10-31",
                         "inclusive_start": false,
                         "inclusive_end": false,
                         "field": "reviews.date"
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);
        assertFalse(hasErrors);
    }

    public void testValidDateRangeStart() {
        String json = """
                {
                     "query": {
                         "start": "2001-10-09T10:20:30-08:00",
                         "inclusive_start": false,
                         "inclusive_end": false,
                         "field": "reviews.date"
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);
        assertFalse(hasErrors);
    }

    public void testValidDateRangeEnd() {
        String json = """
                {
                     "query": {
                         "end": "2016-10-31",
                         "inclusive_start": false,
                         "inclusive_end": false,
                         "field": "reviews.date"
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);
        assertFalse(hasErrors);
    }


    public void testInvalidDateRange() {
        String json = """
                {
                     "query": {
                         "start": 111111111111,
                         "end": 111111111111,
                         "inclusive_start": "false",
                         "inclusive_end": "false",
                         "field": "reviews.date"
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(4, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_STRING_FOR_KEY + "start")));
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_STRING_FOR_KEY + "end")));
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_BOOLEAN_FOR_KEY + "inclusive_start")));
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_BOOLEAN_FOR_KEY + "inclusive_end")));
    }


    public void testInvalidDateRangeMissingStartEnd() {
        String json = """
                {
                     "query": {
                         "inclusive_start": false,
                         "inclusive_end": false,
                         "field": "reviews.date"
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(QueryTypeObjectValidator.startOrEndRequiredMessage())));
    }


    public void testValidConjuncts() {
        String json = """
                {
                     "query":{
                              "conjuncts":[
                                  {
                                      "field": "reviews.date",
                                      "start": "2001-10-09",
                                      "end": "2016-10-31",
                                      "inclusive_start": false,
                                      "inclusive_end": false
                                  },
                                  {
                                      "field": "description",
                                      "match": "pool"
                                  }
                              ]
                          }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);
        assertFalse(hasErrors);
    }

    public void testInvalidConjuncts() {
        String json = """
                {
                     "query":{
                              "field": "description",
                              "conjuncts":[
                                  {
                                      "field": "reviews.date",
                                      "start": "2001-10-09",
                                      "end": "2016-10-31",
                                      "inclusive_start": false,
                                      "inclusive_end": false
                                  },
                                  {
                                      "field": "description",
                                      "match": "pool"
                                  }
                              ]
                          }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(QueryTypeObjectValidator.getFieldNotAllowedOnCompound())));
    }

    public void testInvalidConjunctsType() {
        String json = """
                {
                     "query":{
                              "conjuncts":{
                                      "field": "reviews.date",
                                      "start": "2001-10-09",
                                      "end": "2016-10-31",
                                      "inclusive_start": false,
                                      "inclusive_end": false
                                  }
                              
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_ARRAY_FOR_KEY + "conjuncts")));
    }


    public void testInvaliDisjunctsType() {
        String json = """
                {
                     "query":{
                              "disjuncts":{
                                      "field": "reviews.date",
                                      "start": "2001-10-09",
                                      "end": "2016-10-31",
                                      "inclusive_start": false,
                                      "inclusive_end": false
                                  }
                              
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_ARRAY_FOR_KEY + "disjuncts")));
    }


    public void testValidDisjuncts() {
        String json = """
                {
                     "query":{
                           "disjuncts":[
                               {
                                   "field": "free_parking",
                                   "bool": true
                               },
                               {
                                   "field": "checkin",
                                   "match": "1PM"
                               }
                           ],
                           "min": 1
                       }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);
        assertFalse(hasErrors);
    }

    public void testInvalidDisjuncts() {
        String json = """
                {
                     "query":{
                           "disjuncts":[
                               {
                                   "field": "free_parking",
                                   "bool": true
                               },
                               {
                                   "field": "checkin",
                                   "match": "1PM"
                               }
                           ],
                           "min": 1,
                           "field": "pets_ok",
                           "bool": true
                       }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(2, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(QueryTypeObjectValidator.getFieldNotAllowedOnCompound())));
    }


    public void testValidDistanceObj() {
        String json = """
                {
                     "query": {
                           "location": {
                             "lon": -2.235143,
                             "lat": 53.482358
                           },
                             "distance": "100mi",
                             "field": "geo"
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);
        assertFalse(hasErrors);
    }

    public void testInvalidDistanceObjMissingDistance() {
        String json = """
                {
                     "query": {
                           "location": {
                             "lon": -2.235143,
                             "lat": 53.482358
                           },
                             "field": "geo"
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);


        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(
                        CBSValidationUtil.getMissingAttForQueryMessage("distance radius", List.of("distance")))));
    }


    public void testInvalidMissingLocation() {
        String json = """
                {
                     "query": {
                             "distance": "100mi",
                             "field": "geo"
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);


        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(
                        CBSValidationUtil.getMissingAttForQueryMessage("distance radius", List.of("location")))));
    }

    public void testInvalidLocation() {
        String json = """
                {
                     "query": {
                             "location": "37.79393211306212,-122.44234633404847",
                             "distance": "100mi",
                             "field": "geo"
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);


        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.getLocationTypeMessage("location"))));
    }

    public void testInvalidLocationSingleArrayVal() {
        String json = """
                {
                     "query": {
                             "location": [37.79393211306212],
                             "distance": "100mi",
                             "field": "geo"
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);


        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.getLocationArrayMessage("location"))));
    }

    public void testValidDistanceArrayLocation() {
        String json = """
                {
                     "query": {
                           "location": [ -2.235143, 53.482358],
                             "distance": "100mi",
                             "field": "geo"
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);
        assertFalse(hasErrors);
    }

    public void testInvalidLocationObj() {
        String json = """
                {
                     "query": {
                             "location": {
                                     "lon": -2.235143
                                   },
                             "distance": "100mi",
                             "field": "geo"
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);


        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.getLocationObjectMessage("location"))));
    }

    public void testInvalidDistanceAttr() {
        String json = """
                {
                     "query": {
                            "location": {
                              "lon": -2.235143,
                              "lat": 53.482358
                            },
                            "distance": "100mi",
                            "field": "geo",
                            "fuzziness": 3
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);


        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSValidationUtil.getUnexpectedAttForQuery("fuzziness", "distance radius"))));
    }

    public void testInvalidDistanceValue() {
        String json = """
                {
                     "query": {
                            "location": {
                              "lon": -2.235143,
                              "lat": 53.482358
                            },
                            "distance": 100,
                            "field": "geo"
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);


        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_STRING_FOR_KEY + "distance")));
    }

    public void testInvalidDistanceUnit() {
        String json = """
                {
                     "query": {
                            "location": {
                              "lon": -2.235143,
                              "lat": 53.482358
                            },
                            "distance": "100",
                            "field": "geo"
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);


        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.getDistanceUnitMessage("distance"))));
    }


    public void testValidRectangle() {
        String json = """
                {
                     "query": {
                            "top_left": [-2.235143, 53.482358],
                            "bottom_right": {
                              "lon": 28.955043,
                              "lat": 40.991862
                            },
                            "field": "geo"
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);
        assertFalse(hasErrors);
    }

    public void testInvalidRectangleMissingTopLeft() {
        String json = """
                {
                     "query": {
                            "bottom_right": {
                              "lon": 28.955043,
                              "lat": 40.991862
                            },
                            "field": "geo"
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);


        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(
                        CBSValidationUtil.getMissingAttForQueryMessage("rectangle based", List.of("top_left")))));
    }

    public void testInvalidRectangleMissingBottomRight() {
        String json = """
                {
                     "query": {
                            "top_left": [-2.235143, 53.482358],
                            "field": "geo"
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);


        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(
                        CBSValidationUtil.getMissingAttForQueryMessage("rectangle based", List.of("bottom_right")))));
    }


    public void testInvalidRectangleAttr() {
        String json = """
                {
                     "query": {
                            "top_left": [-2.235143, 53.482358],
                            "bottom_right": {
                              "lon": 28.955043,
                              "lat": 40.991862
                            },
                            "field": "geo",
                            "fuzziness": 3
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);


        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSValidationUtil.getUnexpectedAttForQuery("fuzziness", "rectangle based"))));
    }

    public void testInvalidRectangleLocationAttrs() {
        String json = """
                {
                     "query": {
                            "top_left": [-2.235143],
                            "bottom_right": {
                              "lon": 28.955043
                            },
                            "field": "geo"
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);


        assertSize(2, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.getLocationObjectMessage("bottom_right"))));
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.getLocationArrayMessage("top_left"))));
    }

    public void testValidPolygon() {
        String json = """
                {
                    "query": {
                           "field": "geo",
                           "polygon_points": [
                             "37.79393211306212,-122.44234633404847",
                             "37.77995881733997,-122.43977141339417",
                             "37.788031092020155,-122.42925715405579",
                             "37.79026946582319,-122.41149020154114",
                             "37.79571192027403,-122.40735054016113",
                             "37.79393211306212,-122.44234633404847"
                           ]
                       }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);
        assertFalse(hasErrors);
    }

    public void testInvalidPolygon() {
        String json = """
                {
                    "query": {
                           "field": "geo",
                           "polygon_points": {}
                       }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_ARRAY_FOR_KEY + "polygon_points")));
    }

    public void testInvalidPolygonArray() {
        String json = """
                {
                    "query": {
                           "field": "geo",
                           "polygon_points": [
                           "37.79393211306212/-122.44234633404847",
                                   "37.77995881733997|-122.43977141339417",
                                   "37.788031092020155;-122.42925715405579",
                           ]
                       }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(3, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.LAT_LON_VALUE_MESSAGE)));
    }


    @NotNull
    private List<HighlightInfo> getHighlightInfos(String json) {
        myFixture.configureByText("test.cbs.json", json);
        List<HighlightInfo> highlights = myFixture.doHighlighting();
        return highlights.stream().filter(h -> h.getSeverity() == HighlightSeverity.ERROR).collect(Collectors.toList());
    }
}