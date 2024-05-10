package com.couchbase.intellij.searchworkbench.validator;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.json.psi.JsonObject;

/**
 * Validate the MatchAll and MatchNone object https://docs.couchbase.com/server/current/search/search-request-params.html#special-queries
 */
public class MatchAllNoneObjectValidator implements SearchObjectValidator {

    public boolean accept(String key) {
        return "match_all".equals(key) || "match_none".equals(key);
    }

    public void validate(String key, JsonObject jsonObject, ProblemsHolder holder) {


        if (!jsonObject.getPropertyList().isEmpty()) {
            holder.registerProblem(jsonObject, getNoAttributesAllowed(key), ProblemHighlightType.GENERIC_ERROR);
        }
    }


    public static String getNoAttributesAllowed(String key) {
        return "No attributes are allowed under '" + key + "'";
    }
}
