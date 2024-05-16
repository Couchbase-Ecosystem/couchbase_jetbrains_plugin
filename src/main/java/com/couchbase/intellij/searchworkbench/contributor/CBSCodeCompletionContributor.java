package com.couchbase.intellij.searchworkbench.contributor;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.json.JsonElementTypes;
import com.intellij.json.JsonLanguage;
import com.intellij.json.psi.JsonArray;
import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.ProcessingContext;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CBSCodeCompletionContributor extends CompletionContributor {


    private final List<CBSContributor> contributors = Arrays.asList(new KnnCbsContributor(),
            new HighlightCbsContributor(),
            new CtlCbsContributor(),
            new ConsistencyCbsContributor(),
            new QueryCbsContributor(),
            new BooleanCbsContributor(),
            new LocationCbsContributor(),
            new GeometryCbsContributor(),
            new ShapeCbsContributor()
    );
    public static final List<String> topLevelKeywords = Arrays.asList("query", "knn", "ctl", "size", "limit",
            "from", "offset", "highlight", "fields", "facets", "explain", "sort", "includeLocations", "score",
            "search_after", "search_before", "collections");

    public CBSCodeCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(JsonLanguage.INSTANCE),
                new CompletionProvider<>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        PsiElement position = parameters.getPosition();
                        if (!(position.getContainingFile() instanceof JsonFile)) return;

                        PsiFile file = parameters.getOriginalFile();
                        VirtualFile virtualFile = PsiUtilCore.getVirtualFile(file);

                        if (virtualFile == null || !virtualFile.getName().toLowerCase().endsWith(".cbs.json")) {
                            return;
                        }

                        JsonObject jsonObject = PsiTreeUtil.getParentOfType(position, JsonObject.class);
                        if (jsonObject == null) return;
                        if (!isInJsonObjectContext(position)) return;

                        boolean isKey = isKey(position);
                        String attributeName = null;

                        if (!isKey) {
                            attributeName = getAttributeName(position);
                        }

                        List<String> existingKeys = jsonObject.getPropertyList().stream()
                                .map(JsonProperty::getName)
                                .toList();

                        List<String> suggestions = new ArrayList<>();
                        if (jsonObject.getParent() instanceof JsonFile) {

                            if (!isKey) {
                                if ("score".equals(attributeName)) {
                                    suggestions = Arrays.asList("none");
                                }
                            } else {
                                suggestions = topLevelKeywords.stream().filter(e -> !existingKeys.contains(e)).toList();

                                if (!existingKeys.contains("highlight")) {
                                    result.addElement(CBSTemplates.getHighlightTemplate());
                                }
                                if (!existingKeys.contains("knn")) {
                                    result.addElement(CBSTemplates.getKNNTemplate());
                                }
                            }
                        } else {

                            String type;
                            if (jsonObject.getParent() instanceof JsonProperty) {
                                type = ((JsonProperty) jsonObject.getParent()).getName();
                            } else if (jsonObject.getParent() instanceof JsonArray) {
                                type = ((JsonProperty) jsonObject.getParent().getParent()).getName();
                            } else {
                                throw new NotImplementedException("type is not supported");
                            }

                            for (CBSContributor contributor : contributors) {
                                if (contributor.accept(type)) {
                                    if (isKey) {
                                        contributor.contributeKey(type, jsonObject, suggestions, result);
                                    } else {
                                        contributor.contributeValue(jsonObject, attributeName, suggestions);
                                    }
                                }
                            }
                        }

                        //Check if the current position or its parent is a DOUBLE_QUOTED_STRING
                        boolean isWithinQuotes = position.getNode().getElementType() == JsonElementTypes.DOUBLE_QUOTED_STRING
                                || (position.getParent() != null && position.getParent().getNode().getElementType() == JsonElementTypes.DOUBLE_QUOTED_STRING);

                        for (String suggestion : suggestions) {
                            if (!existingKeys.contains(suggestion)) {

                                if (!isWithinQuotes) {
                                    suggestion = "\"" + suggestion + "\""; // Add quotes if not within a DOUBLE_QUOTED_STRING
                                }
                                result.addElement(LookupElementBuilder.create(suggestion));
                            }
                        }
                    }
                }
        );
    }


    private boolean isInJsonObjectContext(PsiElement position) {
        // Direct check for position being within an array of primitives, which should return false.
        // First, check if the position is within a JsonArray. If so, we need to further determine the context.
        JsonArray jsonArray = PsiTreeUtil.getParentOfType(position, JsonArray.class);
        if (jsonArray != null) {
            // If the position is also within a JsonObject that is a child of the JsonArray, allow suggestions.
            JsonObject jsonObjectWithinArray = PsiTreeUtil.getParentOfType(position, JsonObject.class, false);
            // If we're directly within a JsonArray but not within a JsonObject inside this array, disallow suggestions.
            return jsonObjectWithinArray != null && jsonArray.equals(jsonObjectWithinArray.getParent());
        }

        return true;
    }


    private static boolean isKey(PsiElement position) {
        PsiElement parent = position.getParent();
        // Allow for cases where the position's direct parent is a JsonObject or if the position is within an object that is inside an array.
        if (parent instanceof JsonObject || PsiTreeUtil.getParentOfType(position, JsonObject.class, true) != null) {
            PsiElement prevSibling = PsiTreeUtil.prevVisibleLeaf(position);
            // Check for the position being right after a colon, which would indicate typing a value instead of a key.
            // We also make sure that the position is not within quotes, as it might be part of a key.
            if (prevSibling != null && ":".equals(prevSibling.getText())) {
                // Further check if the prevSibling is directly followed by a quote, which might indicate a new key-value pair.
                PsiElement nextSibling = PsiTreeUtil.nextVisibleLeaf(prevSibling);
                return nextSibling != null && nextSibling.getText().matches("\"");
            }
            return true;
        }
        return false;
    }


    private String getAttributeName(PsiElement position) {
        JsonProperty property = PsiTreeUtil.getParentOfType(position, JsonProperty.class);
        if (property != null) {
            return property.getName();
        }
        return null;
    }
}


