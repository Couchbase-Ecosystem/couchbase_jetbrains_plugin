package com.couchbase.intellij.searchworkbench.actions;

import org.junit.Assert;
import org.junit.Test;

public class JsonCoordinateIdentifierTest {

    private final JsonCoordinateIdentifier identifier = new JsonCoordinateIdentifier();

    private final String radiusExample = """
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

    private final String lineString = """
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
                                "relation": "intersects"
                            }
                        }
                    }
            """;

    private final String rectangle = """
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


    @Test
    public void testIdentifyDistance() throws Exception {
        int offset = radiusExample.indexOf("\"distance\"");
        String result = identifier.getCoordinateObject(radiusExample, offset).getType();
        Assert.assertEquals("radius", result);
    }

    @Test
    public void testIdentifyLon() throws Exception {
        int offset = radiusExample.indexOf("\"lon\"");
        String result = identifier.getCoordinateObject(radiusExample, offset).getType();
        Assert.assertEquals("radius", result);
    }

    @Test
    public void testIdentifyRootQuery() throws Exception {
        int offset = radiusExample.indexOf("\"query\"");
        JsonFilterBlock block = identifier.getCoordinateObject(radiusExample, offset);
        Assert.assertEquals(null, block.getType());
        Assert.assertEquals(0, block.getStart());
        Assert.assertEquals(0, block.getEnd());
    }

    @Test
    public void testIdentifyField() throws Exception {
        int offset = radiusExample.indexOf("\"field\"");
        String result = identifier.getCoordinateObject(radiusExample, offset).getType();
        Assert.assertEquals("radius", result);
    }

    @Test
    public void testIdentifyValue() throws Exception {
        int offset = radiusExample.indexOf("\"geo\"");
        String result = identifier.getCoordinateObject(radiusExample, offset).getType();
        Assert.assertEquals("radius", result);
    }

    @Test
    public void testIdentifyLineString() throws Exception {
        int offset = lineString.indexOf("842721");
        String result = identifier.getCoordinateObject(lineString, offset).getType();
        Assert.assertEquals("LineString", result);
    }

    @Test
    public void testIdentifyRectangle() throws Exception {
        int offset = rectangle.indexOf("28.95");
        String result = identifier.getCoordinateObject(rectangle, offset).getType();
        Assert.assertEquals("rectangle", result);
    }


    private static final String invalidJson = """
            {
              "query": {
                "field": ,
                "geometry": {
                  "shape": {
                    "type": "Circle",
                    "coordinates": ,
                    "radius":
                  },
                  "relation":
                }
              },
              "fields": ["*"]
            }
                        
            """;

    @Test
    public void testInvalidJson() {
        int offset = invalidJson.indexOf("Circle");
        String result = identifier.getCoordinateObject(invalidJson, offset).getType();
        Assert.assertEquals("Circle", result);
    }
}
