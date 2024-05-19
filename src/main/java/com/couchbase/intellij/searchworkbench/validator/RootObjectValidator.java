package com.couchbase.intellij.searchworkbench.validator;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import org.apache.commons.collections.map.HashedMap;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Validate the top json query
 */
public class RootObjectValidator implements SearchObjectValidator {

    private final Set<String> validRootKeys;

    public RootObjectValidator() {
        validRootKeys = new HashSet<>(Arrays.asList("query", "knn", "ctl", "size", "limit", "from", "offset",
                "highlight", "fields", "facets", "explain", "sort", "includeLocations", "score", "search_after", "search_before", "collections"));
    }

    @Override
    public boolean accept(String key) {
        return key == null;
    }

    public void validate(String key, JsonObject jsonObject, ProblemsHolder holder) {
        if (jsonObject.getPropertyList().isEmpty()) {
            holder.registerProblem(jsonObject, "Your search query can't be empty", ProblemHighlightType.GENERIC_ERROR);
        } else {
            Map<String, Integer> counter = new HashedMap();
            for (JsonProperty property : jsonObject.getPropertyList()) {
                if (!validRootKeys.contains(property.getName())) {
                    holder.registerProblem(property, getUnexpectedAttUnderRoot(property.getName()), ProblemHighlightType.GENERIC_ERROR);
                } else {
                    counter.put(property.getName(), counter.getOrDefault(property.getName(), 0) + 1);
                }
            }

            if (counter.getOrDefault("query", 0) == 0
                    && counter.getOrDefault("knn", 0) == 0) {
                holder.registerProblem(jsonObject, getQueryOrKnnReqMessage(), ProblemHighlightType.GENERIC_ERROR);
            }

            if (counter.getOrDefault("search_before", 0) == 1
                    && counter.getOrDefault("search_after", 0) == 1) {
                holder.registerProblem(jsonObject, "'search_before' and 'search_after' can't be used in the same query", ProblemHighlightType.GENERIC_ERROR);
            }

            for (Map.Entry<String, Integer> entry : counter.entrySet()) {
                if (entry.getValue() > 1) {
                    holder.registerProblem(jsonObject, getSingleOccurrenceErrorMessage(entry.getKey()), ProblemHighlightType.GENERIC_ERROR);
                }
            }
        }
    }

    @NotNull
    public static String getQueryOrKnnReqMessage() {
        return "'query' and/or 'knn' attributes are expected at the top level";
    }

    public static String getUnexpectedAttUnderRoot(String key) {
        return "Unexpected attribute '" + key + "' at the top level";
    }

    public static String getSingleOccurrenceErrorMessage(String key) {
        return "The attribute '" + key + "' must appear once at the top level";
    }
}
