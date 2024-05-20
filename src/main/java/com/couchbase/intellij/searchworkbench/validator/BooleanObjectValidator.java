package com.couchbase.intellij.searchworkbench.validator;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;

import java.util.List;

/**
 * Validate the Boolean object https://docs.couchbase.com/server/current/search/search-request-params.html#boolean-queries
 */
public class BooleanObjectValidator implements SearchObjectValidator {

    public boolean accept(String key) {
        return "must".equals(key) || "must_not".equals(key) || "should".equals(key);
    }

    public void validate(String key, JsonObject jsonObject, ProblemsHolder holder) {

        List<String> attrs = jsonObject.getPropertyList().stream().map(JsonProperty::getName).toList();

        if ("must".equals(key)) {
            if (attrs.size() != 1 || !attrs.contains("conjuncts")) {
                holder.registerProblem(jsonObject, getMustConjunctsErrorMessage(), ProblemHighlightType.GENERIC_ERROR);
            }
        } else if ("must_not".equals(key)) {
            if (attrs.size() != 1 || !attrs.contains("disjuncts")) {
                holder.registerProblem(jsonObject, getMustNotDisjunctsErrorMessage(), ProblemHighlightType.GENERIC_ERROR);
            }
        } else {
            if (!((attrs.size() == 1 && attrs.contains("disjuncts"))
                    || (attrs.size() == 2 && attrs.contains("disjuncts") && attrs.contains("min")))) {
                holder.registerProblem(jsonObject, getShouldErrorMessage(), ProblemHighlightType.GENERIC_ERROR);
            }
        }

    }
    
    public static String getMustConjunctsErrorMessage() {
        return "A single 'conjuncts' is expected inside 'must'";
    }

    public static String getShouldErrorMessage() {
        return "A single 'disjuncts' is expected inside 'should'";
    }

    public static String getMustNotDisjunctsErrorMessage() {
        return "A single 'disjuncts' is expected inside 'must_not'";
    }

}
