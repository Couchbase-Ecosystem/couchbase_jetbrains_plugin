package com.couchbase.intellij.searchworkbench.validator;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.json.psi.JsonNumberLiteral;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.JsonStringLiteral;
import org.apache.commons.collections.map.HashedMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Validate multiple query types
 */
public class QueryTypeObjectValidator implements SearchObjectValidator {

    public boolean accept(String key) {
        return "conjuncts".equals(key)
                || "disjuncts".equals(key)
                || "query".equals(key);
    }

    public void validate(String key, JsonObject jsonObject, ProblemsHolder holder) {

        if (jsonObject.getPropertyList().isEmpty()) {
            holder.registerProblem(jsonObject, "'" + key + "' can't be empty", ProblemHighlightType.GENERIC_ERROR);
        }

        boolean containsQuery = containsQuery(jsonObject);
        List<String> properties = jsonObject.getPropertyList().stream().map(JsonProperty::getName).toList();
        boolean isFieldMissing = !properties.contains("field");
        boolean containsMatchAllNone = properties.contains("match_all") || properties.contains("match_none");
        boolean isBooleanQuery = properties.contains("must") || properties.contains("must_not") || properties.contains("should");
        boolean isCompound = validateCompound(jsonObject, properties, holder);

        if (!containsMatchAllNone && !isCompound && !isBooleanQuery && !properties.contains("geometry")) {
            if (isFieldMissing && !containsQuery) {
                holder.registerProblem(jsonObject, getMissingFieldOrQueryMessage(), ProblemHighlightType.GENERIC_ERROR);
            } else if (!isFieldMissing && !containsQuery) {

                if (jsonObject.getPropertyList().size() == 1) {
                    holder.registerProblem(jsonObject, getMissingFieldOperationMessage(), ProblemHighlightType.GENERIC_ERROR);
                } else {
                    validateQueryType(jsonObject, holder);
                }

            } else {
                if (jsonObject.getPropertyList().size() > 1) {
                    holder.registerProblem(jsonObject, getInvalidFieldWithQueryMessage(), ProblemHighlightType.GENERIC_ERROR);
                }
            }
        }
    }


    /**
     * returns true if the query has a compount
     *
     * @param properties
     * @return
     */
    private boolean validateCompound(JsonObject jsonObject, List<String> properties, ProblemsHolder holder) {

        boolean hasCompound = properties.contains("conjuncts") || properties.contains("disjuncts");
        List<String> allowedCompoundAttrs = Arrays.asList("disjuncts", "conjuncts", "min");

        if (hasCompound) {
            for (JsonProperty property : jsonObject.getPropertyList()) {
                if (!allowedCompoundAttrs.contains(property.getName())) {
                    holder.registerProblem(jsonObject, getFieldNotAllowedOnCompound(), ProblemHighlightType.GENERIC_ERROR);
                }
            }
        }

        return hasCompound;
    }


    public static String getFieldNotAllowedOnCompound() {
        return "This field is not allowed at this level when the query contains disjuncts and/or conjuncts";
    }

    public static String getMissingFieldOperationMessage() {
        return "The operation has been specified for the target field";
    }


    public static String getMissingFieldOrQueryMessage() {
        return "The attribute 'query' or 'field' is expected";
    }

    public static String getInvalidFieldWithQueryMessage() {
        return "No additional attributes are supported with 'query'";
    }

    private boolean containsQuery(JsonObject jsonObject) {
        for (JsonProperty property : jsonObject.getPropertyList()) {
            if (property.getName().equals("query")) {
                return true;
            }
        }
        return false;
    }


    private void validateQueryType(JsonObject jsonObject, ProblemsHolder holder) {

        for (JsonProperty property : jsonObject.getPropertyList()) {

            switch (property.getName()) {
                case "match":
                    validateMatch(jsonObject, holder);
                    return;
                case "match_phrase":
                    validateMatchPhrase(jsonObject, holder);
                    return;
                case "bool":
                    validateBoolean(jsonObject, holder);
                    return;
                case "prefix":
                    validatePrefix(jsonObject, holder);
                    return;
                case "regexp":
                    validateRegex(jsonObject, holder);
                    return;
                case "term":
                    validateTerm(jsonObject, holder);
                    return;
                case "terms":
                    validateTerms(jsonObject, holder);
                    return;
                case "wildcard":
                    validateWildcard(jsonObject, holder);
                    return;
                case "cidr":
                    validateCidr(jsonObject, holder);
                    return;
                case "inclusive_min":
                case "inclusive_max":
                    validateGenericRange(jsonObject, holder);
                    return;
                case "min":
                case "max":
                    if (property.getValue() instanceof JsonNumberLiteral) {
                        validateNumericRange(jsonObject, holder);
                        return;
                    } else if (property.getValue() instanceof JsonStringLiteral) {
                        validateTermRange(jsonObject, holder);
                        return;
                    } else {
                        holder.registerProblem(jsonObject, invalidQueryFormatMessage(), ProblemHighlightType.GENERIC_ERROR);
                        return;
                    }
                case "inclusive_start":
                case "inclusive_end":
                case "start":
                case "end":
                    validateDateRange(jsonObject, holder);
                    return;
                case "distance":
                case "location":
                    validateDistanceRadius(jsonObject, holder);
                    return;
                case "top_left":
                case "bottom_right":
                    validateRectangle(jsonObject, holder);
                    return;
                case "polygon_points":
                    return;
            }
        }
        holder.registerProblem(jsonObject, invalidQueryFormatMessage(), ProblemHighlightType.GENERIC_ERROR);
    }


    public void validate(JsonObject jsonObject, ProblemsHolder holder, String target, List<String> allowedFields, List<String> req) {

        List<String> requiredFields = new ArrayList<>(req);

        Map<String, Integer> counter = new HashedMap();
        List<String> currentAttributes = new ArrayList<>();
        for (JsonProperty property : jsonObject.getPropertyList()) {
            currentAttributes.add(property.getName());
            if (!allowedFields.contains(property.getName())) {
                holder.registerProblem(property, CBSValidationUtil.getUnexpectedAttForQuery(property.getName(), target), ProblemHighlightType.GENERIC_ERROR);
            } else {
                counter.put(property.getName(), counter.getOrDefault(property.getName(), 0) + 1);
            }

            CBSValidationUtil.validateMultipleOccurrences(counter, jsonObject, holder);
        }

        requiredFields.removeAll(currentAttributes);
        if (!currentAttributes.isEmpty() && !requiredFields.isEmpty()) {
            holder.registerProblem(jsonObject, CBSValidationUtil.getMissingAttForQueryMessage(target, requiredFields), ProblemHighlightType.GENERIC_ERROR);
        }
    }


    public void validateBoolean(JsonObject jsonObject, ProblemsHolder holder) {
        validate(jsonObject, holder, "boolean", Arrays.asList("field", "bool", "boost"), Arrays.asList("field", "bool"));
    }

    public void validatePrefix(JsonObject jsonObject, ProblemsHolder holder) {
        validate(jsonObject, holder, "prefix", Arrays.asList("field", "prefix", "boost"), Arrays.asList("field", "prefix"));
    }

    public void validateRegex(JsonObject jsonObject, ProblemsHolder holder) {
        validate(jsonObject, holder, "regex", Arrays.asList("field", "regexp", "boost"), Arrays.asList("field", "regexp"));
    }

    public void validateTerm(JsonObject jsonObject, ProblemsHolder holder) {
        validate(jsonObject, holder, "term", Arrays.asList("field", "term", "boost", "fuzziness"), Arrays.asList("field", "term"));
    }

    public void validateTerms(JsonObject jsonObject, ProblemsHolder holder) {
        validate(jsonObject, holder, "terms", Arrays.asList("field", "terms", "boost"), Arrays.asList("field", "terms"));
    }

    public void validateWildcard(JsonObject jsonObject, ProblemsHolder holder) {
        validate(jsonObject, holder, "wildcard", Arrays.asList("field", "wildcard", "boost"), Arrays.asList("field", "wildcard"));
    }

    public void validateCidr(JsonObject jsonObject, ProblemsHolder holder) {
        validate(jsonObject, holder, "cidr", Arrays.asList("field", "cidr", "boost"), Arrays.asList("field", "cidr"));
    }

    public void validateGenericRange(JsonObject jsonObject, ProblemsHolder holder) {
        List<String> attNames = jsonObject.getPropertyList().stream().map(JsonProperty::getName).toList();
        if (!attNames.contains("min") && !attNames.contains("max")) {
            holder.registerProblem(jsonObject, minOrMaxRequiredMessage(), ProblemHighlightType.GENERIC_ERROR);
        }
    }

    public void validateNumericRange(JsonObject jsonObject, ProblemsHolder holder) {
        validate(jsonObject, holder, "numeric range", Arrays.asList("field", "min", "max", "inclusive_min", "inclusive_max", "boost"), Arrays.asList("field"));


    }

    public void validateTermRange(JsonObject jsonObject, ProblemsHolder holder) {
        validate(jsonObject, holder, "term range", Arrays.asList("field", "min", "max", "inclusive_min", "inclusive_max", "boost"), Arrays.asList("field"));
    }

    public void validateDistanceRadius(JsonObject jsonObject, ProblemsHolder holder) {
        validate(jsonObject, holder, "distance radius", Arrays.asList("field", "location", "distance", "boost"), Arrays.asList("field", "location", "distance"));
    }

    public void validateRectangle(JsonObject jsonObject, ProblemsHolder holder) {
        validate(jsonObject, holder, "rectangle based", Arrays.asList("field", "top_left", "bottom_right", "boost"), Arrays.asList("field", "top_left", "bottom_right"));
    }

    public void validateDateRange(JsonObject jsonObject, ProblemsHolder holder) {
        validate(jsonObject, holder, "date range", Arrays.asList("field", "start", "end", "inclusive_start", "inclusive_end", "boost"), Arrays.asList("field"));

        List<String> attNames = jsonObject.getPropertyList().stream().map(JsonProperty::getName).toList();
        if (!attNames.contains("start") && !attNames.contains("end")) {
            holder.registerProblem(jsonObject, startOrEndRequiredMessage(), ProblemHighlightType.GENERIC_ERROR);
        }
    }

    public void validateMatch(JsonObject jsonObject, ProblemsHolder holder) {
        validate(jsonObject, holder, "match", Arrays.asList("field", "match", "analyzer", "operator", "fuzziness", "boost", "prefix_length"), Arrays.asList("field", "match"));

        String matchValue = null;
        boolean isOperatorPresent = false;
        for (JsonProperty property : jsonObject.getPropertyList()) {

            if ("match".equals(property.getName())
                    && property.getValue() instanceof JsonStringLiteral) {
                matchValue = ((JsonStringLiteral) property.getValue()).getValue();

            } else if ("operator".equals(property.getName())) {
                isOperatorPresent = true;

                validateOperatorValue(holder, property);
            }
        }

        if (matchValue != null && matchValue.contains(" ") && !isOperatorPresent) {
            holder.registerProblem(jsonObject, matchWithSpaceMessage(), ProblemHighlightType.GENERIC_ERROR);
        }

    }

    private static void validateOperatorValue(ProblemsHolder holder, JsonProperty property) {
        if (property.getValue() instanceof JsonStringLiteral
                && !("or".equals(((JsonStringLiteral) property.getValue()).getValue())
                || "and".equals(((JsonStringLiteral) property.getValue()).getValue()))) {
            holder.registerProblem(property, invalidOperatorMessage(), ProblemHighlightType.GENERIC_ERROR);
        }
    }

    public void validateMatchPhrase(JsonObject jsonObject, ProblemsHolder holder) {
        validate(jsonObject, holder, "match phrase", Arrays.asList("field", "match_phrase", "analyzer", "operator", "fuzziness", "boost"), Arrays.asList("field", "match_phrase"));

        for (JsonProperty property : jsonObject.getPropertyList()) {
            if ("operator".equals(property.getName())) {
                validateOperatorValue(holder, property);
            }
        }
    }


    public static String matchWithSpaceMessage() {
        return "'operator' needs to be defined when match value contains whitespaces";
    }

    public static String invalidOperatorMessage() {
        return "the value of 'operator' can only be 'or' or 'and'";
    }

    public static String invalidQueryFormatMessage() {
        return "Invalid query format";
    }

    public static String minOrMaxRequiredMessage() {
        return "'min' or 'max' is required for this query type";
    }

    public static String startOrEndRequiredMessage() {
        return "'start' or 'end' is required for this query type";
    }

}
