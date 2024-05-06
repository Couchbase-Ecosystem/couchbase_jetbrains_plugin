package com.couchbase.intellij.searchworkbench;

import com.couchbase.intellij.searchworkbench.validator.CBSMessageUtil;
import com.couchbase.intellij.searchworkbench.validator.CtlConsistencyObjectValidator;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class CBSCTLInspectionTest extends JavaCodeInsightFixtureTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.enableInspections(CBSJsonKeyInspection.class); // Enable the inspection class
    }

    public void testValidCTL() {
        String json = """
                {
                    "query": {
                         "query": "description:pool name:pool^5"
                    },
                    "ctl": {
                            "timeout": 10000,
                            "consistency": {
                                "vectors": {
                                    "searchIndexName": {
                                        "607/205096593892159": 2,
                                        "640/298739127912798": 4
                                    }
                                },
                                "level": "at_plus",
                                "results": "complete"
                            }
                    }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(0, highlights);
    }

    public void testMissingTimeout() {
        String json = """
                {
                    "query": {
                         "query": "description:pool name:pool^5"
                    },
                    "ctl": {
                            "consistency": {
                                "vectors": {
                                    "searchIndexName": {
                                        "607/205096593892159": 2,
                                        "640/298739127912798": 4
                                    }
                                },
                                "level": "at_plus",
                                "results": "complete"
                            }
                    }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(0, highlights);
    }

    public void testMissingConsistency() {
        String json = """
                {
                    "query": {
                         "query": "description:pool name:pool^5"
                    },
                    "ctl": {
                           
                    }
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSMessageUtil.singleRequiredKeyOccurrenceMessage("consistency", "ctl"))));
    }


    @NotNull
    private List<HighlightInfo> getHighlightInfos(String json) {
        myFixture.configureByText("test.cbs.json", json);
        List<HighlightInfo> highlights = myFixture.doHighlighting();
        return highlights.stream().filter(h -> h.getSeverity() == HighlightSeverity.ERROR).collect(Collectors.toList());
    }


    public void testInvalidCTLObjTypes() {
        String json = """
                {
                    "query": {
                         "query": "description:pool name:pool^5"
                    },
                    "ctl": {
                            "timeout": "",
                            "consistency": ""
                    }
                    
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(2, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_INTEGER_FOR_KEY + "timeout")));
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_JSON_OBJECT_FOR_KEY + "consistency")));
    }


    public void testValidConsistency() {
        String json = """
                {
                    "query": {
                         "query": "description:pool name:pool^5"
                    },
                    "ctl": {
                            "timeout": 10000,
                            "consistency": {
                                "vectors": {
                                    "searchIndexName": {
                                        "607/205096593892159": 2,
                                        "640/298739127912798": 4
                                    }
                                },
                                "level": "at_plus",
                                "results": "complete"
                            }
                    }
                    
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(0, highlights);
    }


    public void testMissingLevel() {
        String json = """
                {
                    "query": {
                         "query": "description:pool name:pool^5"
                    },
                    "ctl": {
                            "timeout": 10000,
                            "consistency": {
                                "vectors": {
                                    "searchIndexName": {
                                        "607/205096593892159": 2,
                                        "640/298739127912798": 4
                                    }
                                },
                                "results": "complete"
                            }
                    }
                    
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSMessageUtil.singleRequiredKeyOccurrenceMessage("level", "consistency"))));
    }


    public void testLevelNotBounded() {
        String json = """
                {
                    "query": {
                         "query": "description:pool name:pool^5"
                    },
                    "ctl": {
                            "timeout": 10000,
                            "consistency": {
                                "level": "not_bounded",
                                "results": "complete"
                            }
                    }
                    
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(0, highlights);
    }

    public void testMissingVector() {
        String json = """
                {
                    "query": {
                         "query": "description:pool name:pool^5"
                    },
                    "ctl": {
                            "timeout": 10000,
                            "consistency": {
                                "level": "at_plus",
                            }
                    }
                    
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSMessageUtil.singleRequiredKeyOccurrenceMessage("vectors", "consistency"))));
    }


    public void testInvalidResult() {
        String json = """
                {
                    "query": {
                         "query": "description:pool name:pool^5"
                    },
                    "ctl": {
                            "timeout": 10000,
                            "consistency": {
                                "vectors": {
                                    "searchIndexName": {
                                        "607/205096593892159": 2,
                                        "640/298739127912798": 4
                                    }
                                },
                                "level": "at_plus",
                                "results": "invalid"
                            }
                    }
                    
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CtlConsistencyObjectValidator.getResultErrorMessage())));
    }


    public void testInvalidConsistencyTypes() {
        String json = """
                {
                    "query": {
                         "query": "description:pool name:pool^5"
                    },
                    "ctl": {
                            "timeout": 10000,
                            "consistency": {
                                "vectors": "",
                                "level": 1,
                                "results": 1
                            }
                    }
                    
                }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);
        assertSize(3, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_JSON_OBJECT_FOR_KEY + "vectors")));
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_STRING_FOR_KEY + "level")));
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.EXPECTED_STRING_FOR_KEY + "results")));
    }
}