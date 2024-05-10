package com.couchbase.intellij.searchworkbench.validator;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.json.psi.JsonObject;

import java.util.List;
import java.util.Map;

public class CBSValidationUtil {


    public static String getUnexpectedAttUnder(String key, String target) {
        return "Unexpected attribute '" + key + "' under '" + target + "' object";
    }

    public static String singleOptionalKeyOccurrenceMessage(String key, String target) {
        return "The attribute '" + key + "' must not appear more than once under '" + target + "'";
    }


    public static String singleRequiredKeyOccurrenceMessage(String key, String target) {
        return "The attribute '" + key + "' must appear once under '" + target + "'";
    }

    public static String singleOptionalKeyOccurrenceMessage(String key) {
        return "The attribute '" + key + "' must not appear more than once for this query type";
    }

    public static void validateMultipleOccurrences(Map<String, Integer> counter, JsonObject jsonObject, ProblemsHolder holder) {
        for (Map.Entry<String, Integer> entry : counter.entrySet()) {
            if (entry.getValue() > 1) {
                holder.registerProblem(jsonObject, singleOptionalKeyOccurrenceMessage(entry.getKey()), ProblemHighlightType.GENERIC_ERROR);
            }
        }
    }

    public static String getMissingAttForQueryMessage(String target, List<String> missingFields) {
        if (missingFields.size() == 1) {
            return "The attribute '" + missingFields.get(0) + "' is expected for a '" + target + "' query";
        } else {
            return "The attributes " + String.join(", ", missingFields) + " are expected for a '" + target + "' query";
        }
    }

    public static String getUnexpectedAttForQuery(String key, String query) {
        return "Unexpected attribute '" + key + "' for a '" + query + "' query";
    }
}
