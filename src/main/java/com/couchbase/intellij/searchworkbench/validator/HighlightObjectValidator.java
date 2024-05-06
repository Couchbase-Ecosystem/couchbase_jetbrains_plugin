package com.couchbase.intellij.searchworkbench.validator;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.JsonStringLiteral;
import org.apache.commons.collections.map.HashedMap;

import java.util.Map;

/**
 * Validate the Highlight object https://docs.couchbase.com/server/current/search/search-request-params.html#highlight
 */
public class HighlightObjectValidator implements SearchObjectValidator {

    public boolean accept(String key) {
        return "highlight".equals(key);
    }

    public void validate(JsonObject jsonObject, ProblemsHolder holder) {


        Map<String, Integer> counter = new HashedMap();
        for (JsonProperty property : jsonObject.getPropertyList()) {
            if (!"style".equals(property.getName())
                    && !"fields".equals(property.getName())) {
                holder.registerProblem(property, getUnexpectedAttUnderHighlight(property.getName()), ProblemHighlightType.GENERIC_ERROR);
            } else {
                counter.put(property.getName(), counter.getOrDefault(property.getName(), 0) + 1);
            }

            if ("style".equals(property.getName())
                    && property.getValue() instanceof JsonStringLiteral
                    && !("html".equals(((JsonStringLiteral) property.getValue()).getValue())
                    || "ansi".equals(((JsonStringLiteral) property.getValue()).getValue()))) {
                holder.registerProblem(property, getStyleValuesErrorMessage(), ProblemHighlightType.GENERIC_ERROR);
            }
        }

        for (Map.Entry<String, Integer> entry : counter.entrySet()) {
            if (entry.getValue() > 1) {
                holder.registerProblem(jsonObject, singleKeyOccurrenceMessage(entry.getKey()), ProblemHighlightType.GENERIC_ERROR);
            }
        }
    }


    public static String getUnexpectedAttUnderHighlight(String key) {
        return "Unexpected attribute '" + key + "' under 'highlight' object";
    }

    public static String singleKeyOccurrenceMessage(String key) {
        return "The attribute '" + key + "' must not appear more than once under 'highlight'";
    }

    public static String getStyleValuesErrorMessage() {
        return "The value of the 'style' attribute must be 'ansi' or 'html'";
    }
}
