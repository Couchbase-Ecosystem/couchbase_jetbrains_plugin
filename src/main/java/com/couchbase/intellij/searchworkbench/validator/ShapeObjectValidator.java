package com.couchbase.intellij.searchworkbench.validator;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.json.psi.*;
import org.apache.commons.collections.map.HashedMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Validate multiple shape objects https://docs.couchbase.com/server/current/search/search-request-params.html#geojson-queries-point
 */
public class ShapeObjectValidator implements SearchObjectValidator {

    public boolean accept(String key) {
        return "shape".equals(key) || "geometries".equals(key);
    }

    public void validate(String key, JsonObject jsonObject, ProblemsHolder holder) {

        String target = "shape";
        List<String> requiredFields = new ArrayList<>(Arrays.asList("type"));
        Map<String, Integer> counter = new HashedMap();
        List<String> currentAttributes = new ArrayList<>();
        String typeValue = null;
        JsonProperty coordinates = null;
        for (JsonProperty property : jsonObject.getPropertyList()) {
            currentAttributes.add(property.getName());
            if ("coordinates".equals(property.getName())) {
                coordinates = property;
            }

            if (!"type".equals(property.getName())
                    && !"coordinates".equals(property.getName())
                    && !"geometries".equals(property.getName())
                    && !"radius".equals(property.getName())) {
                holder.registerProblem(property, CBSValidationUtil.getUnexpectedAttForQuery(property.getName(), target), ProblemHighlightType.GENERIC_ERROR);
            } else {
                counter.put(property.getName(), counter.getOrDefault(property.getName(), 0) + 1);

                if ("type".equals(property.getName())) {
                    if (property.getValue() instanceof JsonStringLiteral) {
                        typeValue = ((JsonStringLiteral) property.getValue()).getValue();
                        if (!"Point".equals(typeValue)
                                && !"Circle".equals(typeValue)
                                && !"Envelope".equals(typeValue)
                                && !"LineString".equals(typeValue)
                                && !"MultiPoint".equals(typeValue)
                                && !"MultiLineString".equals(typeValue)
                                && !"MultiPolygon".equals(typeValue)
                                && !"GeometryCollection".equals(typeValue)
                                && !"Polygon".equals(typeValue)) {
                            holder.registerProblem(property, getInvalidType(), ProblemHighlightType.GENERIC_ERROR);
                        }
                    }
                }
            }

            CBSValidationUtil.validateMultipleOccurrences(counter, jsonObject, holder);
        }

        requiredFields.removeAll(currentAttributes);
        if (!currentAttributes.isEmpty() && !requiredFields.isEmpty()) {
            holder.registerProblem(jsonObject, CBSValidationUtil.getMissingAttForQueryMessage(target, requiredFields), ProblemHighlightType.GENERIC_ERROR);
        }

        if ("Circle".equals(typeValue)) {
            if (!currentAttributes.contains("radius")) {
                holder.registerProblem(jsonObject, CBSValidationUtil.getMissingAttForQueryMessage(target, Arrays.asList("radius")), ProblemHighlightType.GENERIC_ERROR);
            }
            if (!currentAttributes.contains("coordinates")) {
                holder.registerProblem(jsonObject, CBSValidationUtil.getMissingAttForQueryMessage(target, Arrays.asList("coordinates")), ProblemHighlightType.GENERIC_ERROR);
            }
        } else if ("GeometryCollection".equals(typeValue)) {
            if (!currentAttributes.contains("geometries")) {
                holder.registerProblem(jsonObject, CBSValidationUtil.getMissingAttForQueryMessage(target, Arrays.asList("geometries")), ProblemHighlightType.GENERIC_ERROR);
            }
        } else {
            if (!currentAttributes.contains("coordinates")) {
                holder.registerProblem(jsonObject, CBSValidationUtil.getMissingAttForQueryMessage(target, Arrays.asList("coordinates")), ProblemHighlightType.GENERIC_ERROR);
            }
        }

        if (coordinates != null && (("LineString".equals(typeValue) || "Polygon".equals(typeValue)
                || "MultiLineString".equals(typeValue) || "MultiPolygon".equals(typeValue)
                || "Envelope".equals(typeValue) || "MultiPoint".equals(typeValue)))) {
            shouldBeArrayOfArray(typeValue, coordinates, holder);
        }
    }

    public void shouldBeArrayOfArray(String type, JsonProperty property, ProblemsHolder holder) {
        if (property.getValue() instanceof JsonArray) {
            for (JsonValue item : ((JsonArray) property.getValue()).getValueList()) {
                if (!(item instanceof JsonArray)) {
                    holder.registerProblem(item, getArrayOfArraysMessage(type), ProblemHighlightType.GENERIC_ERROR);
                }
            }
        }
    }

    public static String getArrayOfArraysMessage(String key) {
        return "type '" + key + "' expect an Array of Arrays as coordinates";
    }

    public static String getInvalidType() {
        return "valid values for 'type' are 'Point', 'Circle', 'Envelope', 'LineString', 'MultiPoint', " +
                "'MultiLineString', 'MultiPolygon', 'GeometryCollection' or 'Polygon'";
    }
}
