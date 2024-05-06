package com.couchbase.intellij.searchworkbench.validator;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Validates the query object https://docs.couchbase.com/server/current/search/search-request-params.html#query-object
 * it expects other validators to check if specific fields are present.
 */
public class QueryObjectValidator implements SearchObjectValidator {

    private final Set<String> validQueryKeys;

    public QueryObjectValidator() {

        validQueryKeys = new HashSet<>(Arrays.asList("must", "query", "must_not", "should", "disjuncts", "conjuncts",
                "match", "field", "analyzer", "operator", "match_phrase", "bool",
                "prefix", "regexp", "term", "fuzziness", "terms", "wildcard", "min",
                "max", "inclusive_min", "inclusive_max", "start",
                "end", "inclusive_start", "inclusive_end", "cidr", "location", "distance", "top_left", "bottom_right",
                "polygon_points", "geometry", "match_all", "match_none", "analyzer", "boost"));
    }

    public boolean accept(String key) {
        return "query".equals(key);
    }

    public void validate(JsonObject jsonObject, ProblemsHolder holder) {
        for (JsonProperty property : jsonObject.getPropertyList()) {
            if (!validQueryKeys.contains(property.getName())) {
                holder.registerProblem(property, getUnexpectedAttUnderQuery(property.getName()), ProblemHighlightType.GENERIC_ERROR);
            }
        }

        if (jsonObject.getPropertyList().isEmpty()) {
            holder.registerProblem(jsonObject, "'query' can't be empty'", ProblemHighlightType.GENERIC_ERROR);
        }
    }

    public static String getUnexpectedAttUnderQuery(String key) {
        return "Unexpected attribute '" + key + "' under 'query'";
    }
}
