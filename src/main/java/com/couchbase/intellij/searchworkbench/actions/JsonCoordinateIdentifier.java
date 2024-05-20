package com.couchbase.intellij.searchworkbench.actions;

public class JsonCoordinateIdentifier {


    public static int maxBraceDepth(String str) {
        int currentDepth = 0;
        int maxDepth = 0;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            if (c == '{') {
                currentDepth++;
                if (currentDepth > maxDepth) {
                    maxDepth = currentDepth;
                }
            } else if (c == '}') {
                if (currentDepth > 0) {
                    currentDepth--;
                } else {
                    // If we encounter an unmatched closing brace, we can ignore it or handle it as needed.
                }
            }
        }

        return maxDepth;
    }


    public JsonFilterBlock getCoordinateObject(String jsonString, int offset) {

        int start = findOpeningBrace(jsonString, offset);
        int end = findClosingBrace(jsonString, start);

        while (start >= 0 && end > start) {
            String jsonObjectString = jsonString.substring(start, end + 1);

            if (containsTopLevelLatOrLon(jsonObjectString)) {
                offset = start - 1; // Move offset before this object to find the parent
                start = findOpeningBrace(jsonString, offset);
                end = findClosingBrace(jsonString, start);
            } else {

                // when lat/lon is present we allow a nested level of 2, otherwise the allowed is 1
                if ((maxBraceDepth(jsonObjectString) == 2
                        && (jsonObjectString.contains("\"lat\"") || jsonObjectString.contains("\"lon\"")))
                        || maxBraceDepth(jsonObjectString) == 1) {
                    return new JsonFilterBlock(determineType(jsonObjectString), start, end, offset);
                } else {
                    return new JsonFilterBlock(null, 0, 0, offset);
                }
            }
        }

        return null;
    }

    private int findOpeningBrace(String jsonString, int offset) {
        int braceCount = 0;
        for (int i = offset; i >= 0; i--) {
            char c = jsonString.charAt(i);
            if (c == '{') {
                if (braceCount == 0) {
                    return i;
                } else {
                    braceCount--;
                }
            } else if (c == '}') {
                braceCount++;
            }
        }
        return -1;
    }

    private int findClosingBrace(String jsonString, int start) {
        int braceCount = 0;
        for (int i = start + 1; i < jsonString.length(); i++) {
            char c = jsonString.charAt(i);
            if (c == '}') {
                if (braceCount == 0) {
                    return i;
                } else {
                    braceCount--;
                }
            } else if (c == '{') {
                braceCount++;
            }
        }
        return -1;
    }

    private String determineType(String jsonNode) {
        if (jsonNode.contains("\"distance\"")) {
            return GeoQueryConstants.RADIUS;
        }
        if (jsonNode.contains("\"top_left\"") || jsonNode.contains("\"bottom_right\"")) {
            return GeoQueryConstants.RECTANGLE;
        }
        if (jsonNode.contains("\"polygon_points\"")) {
            return GeoQueryConstants.POLYGON_POINTS;
        }

        if (jsonNode.contains("\"Point\"")) {
            return GeoQueryConstants.POINT;
        }

        if (jsonNode.contains("\"LineString\"")) {
            return GeoQueryConstants.LINE_STRING;
        }

        if (jsonNode.contains("\"Polygon\"")) {
            return GeoQueryConstants.POLYGON;
        }

        if (jsonNode.contains("\"MultiPoint\"")) {
            return GeoQueryConstants.MULTI_POINT;
        }

        if (jsonNode.contains("\"MultiLineString\"")) {
            return GeoQueryConstants.MULTI_LINE_STRING;
        }

        if (jsonNode.contains("\"MultiPolygon\"")) {
            return GeoQueryConstants.MULTI_POLYGON;
        }

        if (jsonNode.contains("\"Envelope\"")) {
            return GeoQueryConstants.ENVELOPE;
        }

        if (jsonNode.contains("\"Circle\"") || jsonNode.contains("\"radius\"")) {
            return GeoQueryConstants.CIRCLE;
        }
        return null;
    }

    public static boolean containsTopLevelLatOrLon(String str) {

        if (str.contains("\"lat\"")) {
            return countBraces(str.substring(0, str.indexOf("\"lat\""))) == 1;
        }

        if (str.contains("\"lon\"")) {
            return countBraces(str.substring(0, str.indexOf("\"lon\""))) == 1;
        }

        return false;
    }

    public static int countBraces(String str) {
        int braceCount = 0;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            if (c == '{') {
                braceCount++;
            } else if (c == '}') {
                braceCount--;
            }
        }

        return braceCount;
    }

}

