package com.couchbase.intellij.searchworkbench.validator;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;

/**
 * Validate the KNN object https://docs.couchbase.com/server/current/search/search-request-params.html#knn-object
 */
public class KnnObjectValidator implements SearchObjectValidator {

    public boolean accept(String key) {
        return "knn".equals(key);
    }

    public void validate(String key, JsonObject jsonObject, ProblemsHolder holder) {


        for (JsonProperty property : jsonObject.getPropertyList()) {
            if (!"k".equals(property.getName())
                    && !"field".equals(property.getName())
                    && !"vector".equals(property.getName())
                    && !"boost".equals(property.getName())) {
                holder.registerProblem(property, getUnexpectedAttUnderKnn(property.getName()), ProblemHighlightType.GENERIC_ERROR);
            }
        }

        validateOccurrences("k", jsonObject, holder);
        validateOccurrences("field", jsonObject, holder);
        validateOccurrences("vector", jsonObject, holder);
    }

    public void validateOccurrences(String key, JsonObject jsonObject, ProblemsHolder holder) {
        long count = jsonObject.getPropertyList().stream().filter(e -> key.equals(e.getName())).count();
        if (count != 1) {
            holder.registerProblem(jsonObject, singleKeyOccurrenceMessage(key), ProblemHighlightType.GENERIC_ERROR);
        }

    }

    public static String getUnexpectedAttUnderKnn(String key) {
        return "Unexpected attribute '" + key + "' under 'knn' object";
    }

    public static String singleKeyOccurrenceMessage(String key) {
        return "The key '" + key + "' must appear once for this object under knn";
    }
}
