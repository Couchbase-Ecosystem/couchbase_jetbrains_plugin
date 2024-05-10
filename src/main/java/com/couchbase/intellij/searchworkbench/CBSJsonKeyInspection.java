package com.couchbase.intellij.searchworkbench;

import com.couchbase.intellij.searchworkbench.validator.*;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.json.psi.*;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CBSJsonKeyInspection extends LocalInspectionTool {
    public static final String EXPECTED_ARRAY_FOR_KEY = "Expected array for key: ";
    public static final String EXPECTED_JSON_OBJECT_FOR_KEY = "Expected JSON object for key: ";
    public static final String EXPECTED_INTEGER_FOR_KEY = "Expected integer for key: ";
    public static final String EXPECTED_STRING_FOR_KEY = "Expected string for key: ";

    public static final String EXPECTED_STRING_VALUE = "Expected string value";
    public static final String EXPECTED_BOOLEAN_FOR_KEY = "Expected boolean for key: ";
    public static final String LAT_LON_VALUE_MESSAGE = "Each item of the array must be in the format 'lat,lon'";


    @Override
    public @Nullable PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        PsiFile psiFile = holder.getFile();
        VirtualFile virtualFile = psiFile.getVirtualFile();

        if (virtualFile != null && virtualFile.getName().endsWith(".cbs.json")) {
            return new JsonInspectionVisitor(holder);
        }

        return null;
    }

    private static class JsonInspectionVisitor extends JsonElementVisitor {

        private final Map<String, String> allAttributes;

        private final List<SearchObjectValidator> validators;


        private final ProblemsHolder holder;

        public JsonInspectionVisitor(ProblemsHolder holder) {

            this.holder = holder;

            allAttributes = new HashMap<>();

            allAttributes.put("ctl", "json");
            allAttributes.put("highlight", "json");
            allAttributes.put("facets", "json");
            allAttributes.put("knn", "array");
            allAttributes.put("size", "integer");
            allAttributes.put("from", "integer");
            allAttributes.put("fields", "array");
            allAttributes.put("explain", "boolean");
            allAttributes.put("sort", "array");
            allAttributes.put("includeLocations", "boolean");
            allAttributes.put("score", "string");
            allAttributes.put("search_after", "array");
            allAttributes.put("search_before", "array");
            allAttributes.put("limit", "integer");
            allAttributes.put("offset", "integer");
            allAttributes.put("collections", "array");
            allAttributes.put("consistency", "json");
            allAttributes.put("vectors", "json");
            allAttributes.put("match_all", "json");
            allAttributes.put("match_none", "json");
            allAttributes.put("must", "json");
            allAttributes.put("must_not", "json");
            allAttributes.put("should", "json");
            allAttributes.put("shape", "json");
            allAttributes.put("field", "string");
            allAttributes.put("vector", "array");
            allAttributes.put("timeout", "integer");
            allAttributes.put("k", "integer");
            allAttributes.put("style", "string");
            allAttributes.put("results", "string");
            allAttributes.put("level", "string");
            allAttributes.put("match", "string");
            allAttributes.put("analyzer", "string");
            allAttributes.put("operator", "string");
            allAttributes.put("boost", "integer");
            allAttributes.put("fuzziness", "integer");
            allAttributes.put("prefix_length", "integer");
            allAttributes.put("match_phrase", "string");
            allAttributes.put("bool", "boolean");
            allAttributes.put("prefix", "string");
            allAttributes.put("term", "string");
            allAttributes.put("regexp", "string");
            allAttributes.put("terms", "array");
            allAttributes.put("wildcard", "string");
            allAttributes.put("cidr", "string");
            allAttributes.put("inclusive_min", "boolean");
            allAttributes.put("inclusive_max", "boolean");
            allAttributes.put("inclusive_start", "boolean");
            allAttributes.put("inclusive_end", "boolean");
            allAttributes.put("start", "string");
            allAttributes.put("end", "string");
            allAttributes.put("conjuncts", "array");
            allAttributes.put("disjuncts", "array");
            allAttributes.put("relation", "string");
            allAttributes.put("type", "string");
            allAttributes.put("coordinates", "array");
            allAttributes.put("radius", "string");
            allAttributes.put("geometries", "array");

            validators = new ArrayList<>();
            validators.add(new RootObjectValidator());
            validators.add(new QueryObjectValidator());
            validators.add(new KnnObjectValidator());
            validators.add(new HighlightObjectValidator());
            validators.add(new CTLObjectValidator());
            validators.add(new CtlConsistencyObjectValidator());
            validators.add(new MatchAllNoneObjectValidator());
            validators.add(new QueryTypeObjectValidator());
            validators.add(new BooleanObjectValidator());
            validators.add(new GeometryObjectValidator());
            validators.add(new ShapeObjectValidator());
        }

        @Override
        public void visitObject(@NotNull JsonObject jsonObject) {
            validateObjects(jsonObject);
        }

        @Override
        public void visitProperty(@NotNull JsonProperty property) {
            validateProperty(property);
        }

        private void validateObjects(JsonObject jsonObject) {

            String type;
            if (jsonObject.getParent() instanceof JsonFile) {
                type = null;
            } else if (jsonObject.getParent() instanceof JsonProperty) {
                type = ((JsonProperty) jsonObject.getParent()).getName();
            } else if (jsonObject.getParent() instanceof JsonArray) {
                type = ((JsonProperty) jsonObject.getParent().getParent()).getName();
            } else {
                throw new NotImplementedException("type is not supported");
            }

            for (SearchObjectValidator validator : validators) {
                if (validator.accept(type)) {
                    validator.validate(type, jsonObject, holder);
                }
            }
        }

        private void validateProperty(JsonProperty property) {
            String key = property.getName();

            if ("query".equals(key)) {
                if (isTopLevel(property)) {
                    validateType(property, "json");
                } else {
                    validateType(property, "string");
                }
            } else if ("location".equals(key) || "top_left".equals(key) || "bottom_right".equals(key)) {
                if (property.getValue() instanceof JsonArray) {
                    if (((JsonArray) property.getValue()).getValueList().size() != 2) {
                        holder.registerProblem(property, getLocationArrayMessage(key), ProblemHighlightType.GENERIC_ERROR);
                    }
                } else if (property.getValue() instanceof JsonObject) {
                    List<String> attrs = ((JsonObject) property.getValue()).getPropertyList().stream().map(JsonProperty::getName).toList();
                    if (!(attrs.size() == 2 && attrs.contains("lat") && attrs.contains("lon"))) {
                        holder.registerProblem(property, getLocationObjectMessage(key), ProblemHighlightType.GENERIC_ERROR);
                    }

                } else {
                    holder.registerProblem(property, getLocationTypeMessage(key), ProblemHighlightType.GENERIC_ERROR);
                }

            } else if ("min".equals(key) || "max".equals(key)) {
                if (!(property.getValue() instanceof JsonStringLiteral) && !(property.getValue() instanceof JsonNumberLiteral)) {
                    holder.registerProblem(property, "'" + key + "' should be an integer for a numeric range query or a string for term range query", ProblemHighlightType.GENERIC_ERROR);
                }
            } else if ("distance".equals(key) || "radius".equals(key)) {
                if (!(property.getValue() instanceof JsonStringLiteral)) {
                    holder.registerProblem(property, EXPECTED_STRING_FOR_KEY + key, ProblemHighlightType.GENERIC_ERROR);
                } else {
                    String value = ((JsonStringLiteral) property.getValue()).getValue();
                    if (!(value.endsWith("mm") || value.endsWith("cm") || value.endsWith("in")
                            || value.endsWith("yd") || value.endsWith("ft") || value.endsWith("km")
                            || value.endsWith("mi") || value.endsWith("nm") || value.endsWith("m"))) {
                        holder.registerProblem(property, getDistanceUnitMessage(key), ProblemHighlightType.GENERIC_ERROR);
                    }
                }

                //https://docs.couchbase.com/server/current/search/search-request-params.html#geopoint-queries-polygon
            } else if ("polygon_points".equals(key)) {
                if (!(property.getValue() instanceof JsonArray)) {
                    holder.registerProblem(property, EXPECTED_ARRAY_FOR_KEY + key, ProblemHighlightType.GENERIC_ERROR);
                } else {
                    List<JsonValue> values = ((JsonArray) property.getValue()).getValueList();
                    for (JsonValue value : values) {
                        if (!(value instanceof JsonStringLiteral)) {
                            holder.registerProblem(value, EXPECTED_STRING_VALUE, ProblemHighlightType.GENERIC_ERROR);
                        } else {
                            String valueString = ((JsonStringLiteral) value).getValue();
                            if (!valueString.contains(",")) {
                                holder.registerProblem(value, LAT_LON_VALUE_MESSAGE, ProblemHighlightType.GENERIC_ERROR);
                            }
                        }
                    }
                }
            } else {
                validateType(property, allAttributes.get(key));
            }
        }

        private void validateType(JsonProperty property, String expectedType) {
            String key = property.getName();
            JsonValue value = property.getValue();
            if (expectedType != null) {
                switch (expectedType) {
                    case "boolean":
                        if (!(value instanceof JsonBooleanLiteral)) {
                            holder.registerProblem(property, EXPECTED_BOOLEAN_FOR_KEY + key, ProblemHighlightType.GENERIC_ERROR);
                        }
                        break;
                    case "string":
                        if (!(value instanceof JsonStringLiteral)) {
                            holder.registerProblem(property, EXPECTED_STRING_FOR_KEY + key, ProblemHighlightType.GENERIC_ERROR);
                        }
                        break;
                    case "integer":
                        if (!(value instanceof JsonNumberLiteral)) {
                            holder.registerProblem(property, EXPECTED_INTEGER_FOR_KEY + key, ProblemHighlightType.GENERIC_ERROR);
                        }
                        break;
                    case "json":
                        if (!(value instanceof JsonObject)) {
                            holder.registerProblem(property, EXPECTED_JSON_OBJECT_FOR_KEY + key, ProblemHighlightType.GENERIC_ERROR);
                        }
                        break;
                    case "array":
                        if (!(value instanceof JsonArray)) {
                            holder.registerProblem(property, EXPECTED_ARRAY_FOR_KEY + key, ProblemHighlightType.GENERIC_ERROR);
                        }
                        break;
                }
            }
        }

        private void validateArrayOfStrings(JsonProperty property, JsonArray jsonArray) {
            // Ensure the array is either empty or contains only strings
            for (JsonValue element : jsonArray.getValueList()) {
                if (!(element instanceof JsonStringLiteral)) {
                    holder.registerProblem(
                            property,
                            "Expected array of strings or empty array for key: " + property.getName(),
                            ProblemHighlightType.GENERIC_ERROR
                    );
                    break; // Stop if any non-string is found
                }
            }
        }

        private void validateArrayOfDoubles(JsonProperty property, JsonArray jsonArray) {
            for (JsonValue element : jsonArray.getValueList()) {
                if (!(element instanceof JsonNumberLiteral)) {
                    holder.registerProblem(
                            property,
                            "Expected array of doubles for key: " + property.getName(),
                            ProblemHighlightType.GENERIC_ERROR
                    );
                    return;
                }
            }
        }

        private void validateArrayOfObjects(JsonProperty property, JsonArray jsonArray) {
            for (JsonValue element : jsonArray.getValueList()) {
                if (!(element instanceof JsonObject)) {
                    holder.registerProblem(
                            property,
                            "Expected array of objects or empty array for key: " + property.getName(),
                            ProblemHighlightType.GENERIC_ERROR
                    );
                    break; // Stop if any non-string is found
                }
            }
        }


        private boolean isTopLevel(JsonProperty property) {
            PsiElement parent = property.getParent(); // Get the immediate parent
            return parent instanceof JsonObject && parent.getParent() instanceof JsonFile; // Only true for top-level
        }
    }

    @NotNull
    public static String getLocationTypeMessage(String key) {
        return "'" + key + "' must be an array or a json object";
    }

    @NotNull
    public static String getLocationObjectMessage(String key) {
        return "'" + key + "' should contain 2 attributes:'lat' and 'lon'";
    }

    @NotNull
    public static String getLocationArrayMessage(String key) {
        return "'" + key + "' array must contain 2 float values: lon and lat";
    }

    public static String getDistanceUnitMessage(String key) {
        return "'" + key + "' value must end with the unit mm,cm,in,yd,ft,km,mi,nm or m. Ex: '10m'";

    }
}
