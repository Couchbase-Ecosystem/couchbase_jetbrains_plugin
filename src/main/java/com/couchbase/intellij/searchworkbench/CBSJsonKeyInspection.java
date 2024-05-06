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

import java.util.*;

public class CBSJsonKeyInspection extends LocalInspectionTool {
    /**
     * Map of objects that can only appear in the top level of the document
     */
    private static final Map<String, String> TOP_LEVEL_OBJ = new HashMap<>();

    /**
     * Map of properties that can only appear in the top level of the document
     */
    private static final Map<String, String> TOP_LEVEL_PROP = new HashMap<>();
    private static final Map<String, String> NESTED_PROP = new HashMap<>();
    private static final Map<String, String> NESTED_OBJ = new HashMap<>();

    private static final Map<String, String> allAttributes = new HashMap<>();

    private static final List<SearchObjectValidator> validators = new ArrayList<>();
    public static final String EXPECTED_ARRAY_FOR_KEY = "Expected array for key: ";
    public static final String EXPECTED_JSON_OBJECT_FOR_KEY = "Expected JSON object for key: ";
    public static final String EXPECTED_INTEGER_FOR_KEY = "Expected integer for key: ";
    public static final String EXPECTED_STRING_FOR_KEY = "Expected string for key: ";
    public static final String EXPECTED_BOOLEAN_FOR_KEY = "Expected boolean for key: ";

    static {
        TOP_LEVEL_OBJ.put("query", "json");
        TOP_LEVEL_OBJ.put("knn", "array");
        TOP_LEVEL_OBJ.put("ctl", "json");
        TOP_LEVEL_OBJ.put("highlight", "json");
        TOP_LEVEL_OBJ.put("facets", "json");
    }

    static {
        TOP_LEVEL_PROP.put("knn", "array");
        TOP_LEVEL_PROP.put("size", "integer");
        TOP_LEVEL_PROP.put("from", "integer");
        TOP_LEVEL_PROP.put("fields", "array");
        TOP_LEVEL_PROP.put("explain", "boolean");
        TOP_LEVEL_PROP.put("sort", "array");
        TOP_LEVEL_PROP.put("includeLocations", "boolean");
        TOP_LEVEL_PROP.put("score", "string");
        TOP_LEVEL_PROP.put("search_after", "array");
        TOP_LEVEL_PROP.put("search_before", "array");
        TOP_LEVEL_PROP.put("limit", "integer");
        TOP_LEVEL_PROP.put("offset", "integer");
        TOP_LEVEL_PROP.put("collections", "array");
    }

    static {
        NESTED_OBJ.put("consistency", "json");
        NESTED_OBJ.put("vectors", "json");
    }

    static {
        NESTED_PROP.put("query", "string");
        NESTED_PROP.put("field", "string");
        NESTED_PROP.put("vector", "array");
        NESTED_PROP.put("timeout", "integer");
        NESTED_PROP.put("k", "integer");
        NESTED_PROP.put("fields", "array");
        NESTED_PROP.put("style", "string");
        NESTED_PROP.put("results", "string");
        NESTED_PROP.put("level", "string");

    }

    static {
        allAttributes.putAll(TOP_LEVEL_OBJ);
        allAttributes.putAll(TOP_LEVEL_PROP);
        allAttributes.putAll(NESTED_OBJ);
        allAttributes.putAll(NESTED_PROP);
    }

    static {
        validators.add(new RootObjectValidator());
        validators.add(new QueryObjectValidator());
        validators.add(new KnnObjectValidator());
        validators.add(new HighlightObjectValidator());
        validators.add(new CTLObjectValidator());
        validators.add(new CtlConsistencyObjectValidator());
    }


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
        private final ProblemsHolder holder;

        public JsonInspectionVisitor(ProblemsHolder holder) {
            this.holder = holder;
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
                    validator.validate(jsonObject, holder);
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
            } else {
                validateType(property, allAttributes.get(key));
            }
//                boolean fieldTypeVal = false;
//                if (TOP_LEVEL_PROP.containsKey(key)) {
//                    validateType(property, TOP_LEVEL_PROP.get(key));
//                }
//
//                if (isTopLevel(property)) {
//                    validateType(property, TOP_LEVEL_PROP.get(key));
//                } else {
//                    validateType(property, NESTED_PROP.get(key));
//                }
//                String expectedType = TOP_LEVEL_KEYS.get(key);
//                validateType(property, key, expectedType, value);
//
//                if ("query".equals(key)) {
//                    validateQueryType(property); // Determine type for 'query' based on context
//                } else if ("conjuncts".equals(key) || "disjuncts".equals(key)) {
//                    validateConjunctsDisjuncts(property); // Ensure they're arrays and under 'query'
//                } else if (!TOP_LEVEL_KEYS.containsKey(key)) {
//                    holder.registerProblem(property, "Invalid key: " + key, ProblemHighlightType.GENERIC_ERROR);
//                }

        }


        private void validateTopLevelKey(JsonProperty property, String key) {
            String expectedType = TOP_LEVEL_OBJ.get(key);
            validateType(property, expectedType);
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
//                        else {
//                            // Validate specific keys that must be arrays of strings or empty arrays
//                            if (key.equals("fields") || key.equals("search_after") || key.equals("search_before") || key.equals("collections")) {
//                                validateArrayOfStrings(property, (JsonArray) value); // Ensure it's an array of strings
//                            } else if (key.equals("knn")) {
//                                validateKnnArray(property, (JsonArray) value);
//                            }
//                        }
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


        private void validateKnnArray(JsonProperty property, JsonArray jsonArray) {
            for (JsonValue element : jsonArray.getValueList()) {
                if (!(element instanceof JsonObject)) {
                    holder.registerProblem(
                            property,
                            "Expected array of objects or empty array for key: " + property.getName(),
                            ProblemHighlightType.GENERIC_ERROR
                    );
                    return; // Stop if any non-object is found
                }

                JsonObject jsonObject = (JsonObject) element;
                Set<String> requiredKeys = new HashSet<>(Set.of("k", "field", "vector"));
                Set<String> encounteredKeys = new HashSet<>();

                for (JsonProperty prop : jsonObject.getPropertyList()) {
                    String key = prop.getName();
                    if (encounteredKeys.contains(key)) {
                        holder.registerProblem(
                                prop,
                                "Key '" + key + "' should not be repeated within the same entity",
                                ProblemHighlightType.GENERIC_ERROR
                        );
                        return; // Stop if key repetition is found
                    }

                    encounteredKeys.add(key);
                    if (requiredKeys.contains(key)) {
                        validateRequiredKnnKey(prop, key);
                        requiredKeys.remove(key); // Key validated
                    } else {
                        holder.registerProblem(
                                prop,
                                "Unexpected key: " + key,
                                ProblemHighlightType.GENERIC_ERROR
                        );
                    }
                }

                if (!requiredKeys.isEmpty()) {
                    holder.registerProblem(
                            property,
                            "Missing required keys in 'knn' object: " + requiredKeys,
                            ProblemHighlightType.GENERIC_ERROR
                    );
                }
            }
        }


        private void validateRequiredKnnKey(JsonProperty prop, String key) {
            JsonValue value = prop.getValue();

            switch (key) {
                case "k":
                    if (!(value instanceof JsonNumberLiteral)) {
                        holder.registerProblem(prop, "Expected integer for key: " + key, ProblemHighlightType.GENERIC_ERROR);
                    }
                    break;
                case "field":
                    if (!(value instanceof JsonStringLiteral)) {
                        holder.registerProblem(prop, "Expected string for key: " + key, ProblemHighlightType.GENERIC_ERROR);
                    }
                    break;
                case "vector":
                    if (!(value instanceof JsonArray)) {
                        holder.registerProblem(prop, "Expected array for key: " + key, ProblemHighlightType.GENERIC_ERROR);
                    } else {
                        validateArrayOfDoubles(prop, (JsonArray) value); // Validate it's an array of doubles
                    }
                    break;
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
                    return; // Stop if any non-double is found
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


        private void validateQueryType(JsonProperty property) {
            JsonValue value = property.getValue();

            if (isTopLevel(property)) {
                // 'query' at top-level must be a JSON object
                if (!(value instanceof JsonObject)) {
                    holder.registerProblem(property, "'query' at top-level must be a JSON object", ProblemHighlightType.GENERIC_ERROR);
                }
            } else if (isUnderQuery(property)) {
                // 'query' nested within 'query' must be a string
                if (!(value instanceof JsonStringLiteral)) {
                    holder.registerProblem(property, "'query' nested within 'query' must be a string", ProblemHighlightType.GENERIC_ERROR);
                }
            } else {
                // If it's neither top-level nor nested under 'query', it's invalid
                holder.registerProblem(property, "'query' outside of 'query' context", ProblemHighlightType.GENERIC_ERROR);
            }
        }

        private boolean isTopLevel(JsonProperty property) {
            PsiElement parent = property.getParent(); // Get the immediate parent
            return parent instanceof JsonObject && parent.getParent() instanceof JsonFile; // Only true for top-level
        }

        private void validateConjunctsDisjuncts(JsonProperty property) {
            JsonValue value = property.getValue();

            if (!(value instanceof JsonArray)) {
                holder.registerProblem(property, "'conjuncts' and 'disjuncts' must be arrays", ProblemHighlightType.GENERIC_ERROR);
            }

            // Ensure they are under 'query'
            if (!isUnderQuery(property)) {
                holder.registerProblem(property, "'conjuncts' and 'disjuncts' must be within 'query'", ProblemHighlightType.GENERIC_ERROR);
            }
        }

        private boolean isUnderQuery(JsonProperty property) {
            PsiElement parent = property.getParent(); // Start from the immediate parent
            while (parent != null) {
                if (parent instanceof JsonProperty) { // Check if it's a JsonProperty
                    JsonProperty jsonParent = (JsonProperty) parent;
                    if ("query".equals(jsonParent.getName())) { // Check if the name is 'query'
                        return true; // Found 'query' as an ancestor
                    }
                }
                parent = parent.getParent();
            }
            return false;
        }
    }

    @NotNull
    public static String getUnexpectedAttNameMsg(String key) {
        return "Unexpected attribute name \"" + key + "\"";
    }
}
