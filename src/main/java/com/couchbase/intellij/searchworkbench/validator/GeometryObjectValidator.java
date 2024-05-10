package com.couchbase.intellij.searchworkbench.validator;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.JsonStringLiteral;
import org.apache.commons.collections.map.HashedMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Validate multiple Geometry objects https://docs.couchbase.com/server/current/search/search-request-params.html#geojson-queries-point
 */
public class GeometryObjectValidator implements SearchObjectValidator {

    public boolean accept(String key) {
        return "geometry".equals(key);
    }

    public void validate(String key, JsonObject jsonObject, ProblemsHolder holder) {

        String target = "geometry";
        List<String> requiredFields = new ArrayList<>(Arrays.asList("shape", "relation"));
        Map<String, Integer> counter = new HashedMap();
        List<String> currentAttributes = new ArrayList<>();
        for (JsonProperty property : jsonObject.getPropertyList()) {
            currentAttributes.add(property.getName());
            if (!"shape".equals(property.getName())
                    && !"relation".equals(property.getName())
                    && !"boost".equals(property.getName())) {
                holder.registerProblem(property, CBSValidationUtil.getUnexpectedAttForQuery(property.getName(), target), ProblemHighlightType.GENERIC_ERROR);
            } else {
                counter.put(property.getName(), counter.getOrDefault(property.getName(), 0) + 1);

                if ("relation".equals(property.getName())) {
                    if (property.getValue() instanceof JsonStringLiteral) {
                        String value = ((JsonStringLiteral) property.getValue()).getValue();
                        if (!"intersects".equals(value)
                                && !"contains".equals(value)
                                && !"within".equals(value)) {
                            holder.registerProblem(property, getInvalidRelationValue(), ProblemHighlightType.GENERIC_ERROR);
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
    }


    public static String getInvalidRelationValue() {
        return "'relation' can only be 'intersects', 'contains' or 'within'";
    }
}
