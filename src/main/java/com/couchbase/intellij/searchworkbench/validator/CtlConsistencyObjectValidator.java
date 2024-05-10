package com.couchbase.intellij.searchworkbench.validator;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.JsonStringLiteral;
import org.apache.commons.collections.map.HashedMap;

import java.util.Map;

/**
 * Validate the consistency object https://docs.couchbase.com/server/current/search/search-request-params.html#consistency
 */
public class CtlConsistencyObjectValidator implements SearchObjectValidator {

    public boolean accept(String key) {
        return "consistency".equals(key);
    }

    public void validate(String key, JsonObject jsonObject, ProblemsHolder holder) {
        Map<String, Integer> counter = new HashedMap();

        String level = null;
        String results = null;
        for (JsonProperty property : jsonObject.getPropertyList()) {
            if (!"vectors".equals(property.getName())
                    && !"level".equals(property.getName())
                    && !"results".equals(property.getName())) {
                holder.registerProblem(property, CBSValidationUtil.getUnexpectedAttUnder(property.getName(), "consistency"), ProblemHighlightType.GENERIC_ERROR);
            } else {
                counter.put(property.getName(), counter.getOrDefault(property.getName(), 0) + 1);
            }


            if ("level".equals(property.getName())
                    && property.getValue() instanceof JsonStringLiteral) {

                if (!("at_plus".equals(((JsonStringLiteral) property.getValue()).getValue())
                        || "not_bounded".equals(((JsonStringLiteral) property.getValue()).getValue()))) {
                    holder.registerProblem(property, getLevelValuesErrorMessage(), ProblemHighlightType.GENERIC_ERROR);
                } else {
                    level = ((JsonStringLiteral) property.getValue()).getValue();
                }
            }

            if ("results".equals(property.getName())
                    && property.getValue() instanceof JsonStringLiteral) {
                results = ((JsonStringLiteral) property.getValue()).getValue();
            }
        }

        for (Map.Entry<String, Integer> entry : counter.entrySet()) {
            if (entry.getValue() > 1) {
                holder.registerProblem(jsonObject, CBSValidationUtil.singleOptionalKeyOccurrenceMessage(entry.getKey(), "consistency"),
                        ProblemHighlightType.GENERIC_ERROR);
            }
        }

        if (counter.getOrDefault("level", 0) == 0) {
            holder.registerProblem(jsonObject, CBSValidationUtil.singleRequiredKeyOccurrenceMessage("level", "consistency"),
                    ProblemHighlightType.GENERIC_ERROR);
        }


        if ("at_plus".equals(level) && counter.getOrDefault("vectors", 0) == 0) {
            holder.registerProblem(jsonObject, CBSValidationUtil.singleRequiredKeyOccurrenceMessage("vectors", "consistency"),
                    ProblemHighlightType.GENERIC_ERROR);
        }

        if (results != null && !"complete".equals(results)) {
            holder.registerProblem(jsonObject, getResultErrorMessage(),
                    ProblemHighlightType.GENERIC_ERROR);
        }

    }

    public static String getLevelValuesErrorMessage() {
        return "The value of the 'level' attribute must be 'at_plus' or 'not_bounded'";
    }

    public static String getResultErrorMessage() {
        return "The value of the 'result' attribute must be 'complete'";
    }
}
