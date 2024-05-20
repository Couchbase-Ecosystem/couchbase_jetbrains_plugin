package com.couchbase.intellij.searchworkbench;

import com.couchbase.intellij.searchworkbench.validator.CBSValidationUtil;
import com.couchbase.intellij.searchworkbench.validator.GeometryObjectValidator;
import com.couchbase.intellij.searchworkbench.validator.ShapeObjectValidator;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CBSGeoInspectionTest extends JavaCodeInsightFixtureTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.enableInspections(CBSJsonKeyInspection.class); // Enable the inspection class
    }

    public void testValidPointJson() {
        String json = """
                {
                    "query": {
                            "field": "geojson",
                            "geometry": {
                                "shape": {
                                    "type": "Point",
                                    "coordinates": [0.47482593026924746, 51.31232878073189]
                                },
                                "relation": "intersects"
                            }
                        }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);
        assertFalse(hasErrors);
    }

    public void testInvalidShapeMissingCoordinate() {
        String json = """
                {
                    "query": {
                            "field": "geojson",
                            "geometry": {
                                "shape": {
                                    "type": "Point"
                                },
                                "relation": "intersects"
                            }
                        }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSValidationUtil.getMissingAttForQueryMessage("shape", Arrays.asList("coordinates")))));
    }

    public void testInvalidShapeMissingType() {
        String json = """
                {
                    "query": {
                            "field": "geojson",
                            "geometry": {
                                "shape": {
                                    "coordinates": [0.47482593026924746, 51.31232878073189]
                                },
                                "relation": "intersects"
                            }
                        }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSValidationUtil.getMissingAttForQueryMessage("shape", Arrays.asList("type")))));
    }


    public void testInvalidShapeMissingGeometries() {
        String json = """
                {
                    "query": {
                            "field": "geojson",
                            "geometry": {
                                "shape": {
                                    "type": "GeometryCollection"
                                },
                                "relation": "intersects"
                            }
                        }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSValidationUtil.getMissingAttForQueryMessage("shape", Arrays.asList("geometries")))));
    }


    public void testInvalidShapeMissingRadius() {
        String json = """
                {
                    "query": {
                            "field": "geojson",
                            "geometry": {
                                "shape": {
                                    "type": "Circle",
                                    "coordinates": [-2.2450285424707204, 53.48503270828377]
                                },
                                "relation": "intersects"
                            }
                        }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSValidationUtil.getMissingAttForQueryMessage("shape", Arrays.asList("radius")))));
    }

    public void testValidCircle() {
        String json = """
                {
                    "query": {
                         "field": "geojson",
                         "geometry": {
                             "shape": {
                                 "coordinates": [-2.2450285424707204, 53.48503270828377],
                                 "type": "Circle",
                                 "radius": "100mi"
                             },
                             "relation": "within"
                         }
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);
        assertFalse(hasErrors);
    }

    public void testInvalidShapeInvalidRadius() {
        String json = """
                {
                    "query": {
                            "field": "geojson",
                            "geometry": {
                                "shape": {
                                    "type": "Circle",
                                    "coordinates": [-2.2450285424707204, 53.48503270828377],
                                    "radius": "100"
                                },
                                "relation": "intersects"
                            }
                        }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSJsonKeyInspection.getDistanceUnitMessage("radius"))));
    }


    public void testInvalidRelationValue() {
        String json = """
                {
                    "query": {
                            "field": "geojson",
                            "geometry": {
                                "shape": {
                                    "type": "Point",
                                    "coordinates": [0.47482593026924746, 51.31232878073189]
                                },
                                "relation": "union"
                            }
                        }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(GeometryObjectValidator.getInvalidRelationValue())));
    }

    public void testMissingRelation() {
        String json = """
                {
                    "query": {
                            "field": "geojson",
                            "geometry": {
                                "shape": {
                                    "type": "Point",
                                    "coordinates": [0.47482593026924746, 51.31232878073189]
                                }
                            }
                        }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSValidationUtil.getMissingAttForQueryMessage("geometry",
                        Arrays.asList("relation")))));
    }


    public void testMissingShape() {
        String json = """
                {
                    "query": {
                        "field": "geojson",
                        "geometry": {
                            "relation": "intersects"
                        }
                    }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(CBSValidationUtil.getMissingAttForQueryMessage("geometry",
                        Arrays.asList("shape")))));
    }


    public void testValidLineString() {
        String json = """
                {
                    "query": {
                            "field": "geojson",
                            "geometry": {
                                "shape": {
                                "type": "LineString",
                                "coordinates": [
                                        [-2.753735609842721, 53.94860827535115],
                                        [-2.599898256093695,53.65007434185782]
                                    ]
                                },
                                "relation": "contains"
                            }
                        }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);
        assertFalse(hasErrors);
    }


    public void testInvalidLineStringCoordinates() {
        String json = """
                {
                    "query": {
                            "field": "geojson",
                            "geometry": {
                                "shape": {
                                "type": "LineString",
                                "coordinates": [-2.753735609842721, 53.94860827535115]
                                },
                                "relation": "contains"
                            }
                        }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(2, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(ShapeObjectValidator.getArrayOfArraysMessage("LineString"))));
    }


    public void testInvalidType() {
        String json = """
                {
                    "query": {
                            "field": "geojson",
                            "geometry": {
                                "shape": {
                                "type": "LineStrings",
                                "coordinates": [
                                        [-2.753735609842721, 53.94860827535115],
                                        [-2.599898256093695,53.65007434185782]
                                    ]
                                },
                                "relation": "contains"
                            }
                        }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(1, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(ShapeObjectValidator.getInvalidType())));
    }


    public void testValidPolygon() {
        String json = """
                {
                    "query": {
                            "field": "geojson",
                            "geometry": {
                                "shape": {
                                    "type": "Polygon",
                                    "coordinates": [
                                        [
                                        [
                                            0.47482593026924746,
                                            51.31232878073189
                                        ],
                                        [
                                            0.6143265647863245,
                                            51.31232878073189
                                        ],
                                        [
                                            0.6143265647863245,
                                            51.384000374770466
                                        ],
                                        [
                                            0.47482593026924746,
                                            51.384000374770466
                                        ],
                                        [
                                            0.47482593026924746,
                                            51.31232878073189
                                        ]
                                        ]
                                    ]
                                },
                                "relation": "within"
                            }
                        }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);
        assertFalse(hasErrors);
    }


    public void testInvalidPolygonCoordinates() {
        String json = """
                {
                    "query": {
                                 "field": "geojson",
                                 "geometry": {
                                     "shape": {
                                         "type": "Polygon",
                                         "coordinates":
                                             [
                                                 0.47482593026924746,
                                                 51.31232878073189
                                             ]
                                         
                                     },
                                     "relation": "within"
                                 }
                             }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(2, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(ShapeObjectValidator.getArrayOfArraysMessage("Polygon"))));
    }


    public void testValidMultiPoint() {
        String json = """
                {
                    "query": {
                            "field": "geojson",
                            "geometry": {
                                "shape": {
                                    "type": "MultiPoint",
                                    "coordinates": [
                                        [1.954764, 50.962097],
                                        [3.029578, 49.868547]
                                    ]
                                },
                                "relation": "intersects"
                            }
                        }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);
        assertFalse(hasErrors);
    }


    public void testInvalidMultiPointCoordinates() {
        String json = """
                {
                    "query": {
                            "field": "geojson",
                            "geometry": {
                                "shape": {
                                    "type": "MultiPoint",
                                    "coordinates": [1.954764, 50.962097]
                                },
                                "relation": "intersects"
                            }
                        }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(2, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(ShapeObjectValidator.getArrayOfArraysMessage("MultiPoint"))));
    }


    public void testValidMultiLine() {
        String json = """
                {
                    "query": {
                         "field": "geojson",
                         "geometry": {
                             "shape": {
                                 "type": "MultiLineString",
                                 "coordinates": [
                                         [ [1.954764, 50.962097], [3.029578, 49.868547] ],
                                         [ [3.029578, 49.868547], [-0.387444, 48.545836] ]
                                     ]
                             },
                             "relation": "intersects"
                         }
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);
        assertFalse(hasErrors);
    }


    public void testInvalidMultiLineCoordinates() {
        String json = """
                {
                    "query": {
                         "field": "geojson",
                         "geometry": {
                             "shape": {
                                 "type": "MultiLineString",
                                 "coordinates": [1.954764, 50.962097]
                             },
                             "relation": "intersects"
                         }
                     }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(2, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(ShapeObjectValidator.getArrayOfArraysMessage("MultiLineString"))));
    }


    public void testValidMultiPolygon() {
        String json = """
                {
                    "query": {
                              "field": "geojson",
                              "geometry": {
                                  "shape": {
                                      "type": "MultiPolygon",
                                      "coordinates": [
                                          [
                                              [[-1.8167959002718135, 53.8626654046235],
                                              [-1.8728039536828476, 53.6335890387158],
                                              [-1.4029586167332582, 53.57727933778668],
                                              [-1.0031233465474827, 53.664942195474936],
                                              [-1.1742590653039997, 53.84522968338081],
                                              [-1.5523134258297944, 53.89384804206853],
                                              [-1.8167959002718135, 53.8626654046235]]
                                          ],
                                          [
                                              [[-2.4935598789906805, 53.64373525825596],
                                              [-2.664695597747226, 53.33735696804186],
                                              [-2.0143798664721544, 53.28065279675474],
                                              [-1.8572461610683888, 53.550482816448266],
                                              [-2.309977926141812, 53.604982015453714],
                                              [-2.4935598789906805, 53.64373525825596]]
                                          ]
                                      ]
                                  },
                                  "relation": "within"
                              }
                          }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);
        assertFalse(hasErrors);
    }


    public void testInvalidMultiPolygonCoordinates() {
        String json = """
                {
                    "query": {
                              "field": "geojson",
                              "geometry": {
                                  "shape": {
                                      "type": "MultiPolygon",
                                      "coordinates": [-1.8167959002718135, 53.8626654046235]
                                  },
                                  "relation": "within"
                              }
                          }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(2, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(ShapeObjectValidator.getArrayOfArraysMessage("MultiPolygon"))));
    }


    public void testValidGeometryCollection() {
        String json = """
                {
                    "query": {
                              "field": "geojson",
                              "geometry": {
                                  "shape": {
                                      "type": "GeometryCollection",
                                      "geometries": [
                                          {
                                              "type": "LineString",
                                              "coordinates": [
                                                  [-2.753735609842721, 53.94860827535115],
                                                  [-2.599898256093695, 53.65007434185782]
                                              ]
                                          },
                                          {
                                              "type": "MultiPolygon",
                                              "coordinates": [
                                                  [[
                                                      [-1.8167959002718135, 53.8626654046235],
                                                      [-1.8728039536828476, 53.6335890387158],
                                                      [-1.4029586167332582, 53.57727933778668],
                                                      [-1.0031233465474827, 53.664942195474936],
                                                      [-1.1742590653039997, 53.84522968338081],
                                                      [-1.5523134258297944, 53.89384804206853],
                                                      [-1.8167959002718135, 53.8626654046235]
                                                  ]],
                                                  [[
                                                      [-2.4935598789906805, 53.64373525825596],
                                                      [-2.664695597747226, 53.33735696804186],
                                                      [-2.0143798664721544, 53.28065279675474],
                                                      [-1.8572461610683888, 53.550482816448266],
                                                      [-2.309977926141812, 53.604982015453714],
                                                      [-2.4935598789906805, 53.64373525825596]
                                                  ]]
                                              ]
                                          }
                                      ]
                                  },
                                  "relation": "contains"
                              }
                          }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);
        assertFalse(hasErrors);
    }


    public void testInvalidGeometryCollection() {
        String json = """
                {
                    "query": {
                              "field": "geojson",
                              "geometry": {
                                  "shape": {
                                      "type": "GeometryCollection",
                                      "geometries": [
                                          {
                                              "type": "LineString",
                                              "coordinates": [-2.753735609842721, 53.94860827535115]
                                          },
                                          {
                                              "type": "MultiPolygon",
                                              "coordinates": [
                                                  [[
                                                      [-1.8167959002718135, 53.8626654046235],
                                                      [-1.8728039536828476, 53.6335890387158],
                                                      [-1.4029586167332582, 53.57727933778668],
                                                      [-1.0031233465474827, 53.664942195474936],
                                                      [-1.1742590653039997, 53.84522968338081],
                                                      [-1.5523134258297944, 53.89384804206853],
                                                      [-1.8167959002718135, 53.8626654046235]
                                                  ]],
                                                  [[
                                                      [-2.4935598789906805, 53.64373525825596],
                                                      [-2.664695597747226, 53.33735696804186],
                                                      [-2.0143798664721544, 53.28065279675474],
                                                      [-1.8572461610683888, 53.550482816448266],
                                                      [-2.309977926141812, 53.604982015453714],
                                                      [-2.4935598789906805, 53.64373525825596]
                                                  ]]
                                              ]
                                          }
                                      ]
                                  },
                                  "relation": "contains"
                              }
                          }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(2, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(ShapeObjectValidator.getArrayOfArraysMessage("LineString"))));
    }


    public void testValidEnvelope() {
        String json = """
                {
                   "query": {
                       "field": "geojson",
                       "geometry": {
                           "shape": {
                               "type": "Envelope",
                               "coordinates": [
                                   [-3.1720240079703785, 53.58545703217979],
                                   [-1.8566251855731082, 53.282076725710596]
                               ]
                           },
                           "relation": "intersects"
                       }
                   }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        boolean hasErrors = highlights.stream()
                .anyMatch(h -> h.getSeverity() == HighlightSeverity.ERROR);
        assertFalse(hasErrors);
    }


    public void testInvalidEnvelopeCoordinates() {
        String json = """
                {
                    "query": {
                        "field": "geojson",
                        "geometry": {
                            "shape": {
                                "type": "Envelope",
                                "coordinates": [-3.1720240079703785, 53.58545703217979]
                            },
                            "relation": "intersects"
                        }
                    }
                 }
                """;

        List<HighlightInfo> highlights = getHighlightInfos(json);

        assertSize(2, highlights);
        assertTrue(highlights.stream()
                .anyMatch(h -> h.getDescription().equals(ShapeObjectValidator.getArrayOfArraysMessage("Envelope"))));
    }

    @NotNull
    private List<HighlightInfo> getHighlightInfos(String json) {
        myFixture.configureByText("test.cbs.json", json);
        List<HighlightInfo> highlights = myFixture.doHighlighting();
        return highlights.stream().filter(h -> h.getSeverity() == HighlightSeverity.ERROR).collect(Collectors.toList());
    }

}