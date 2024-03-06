package org.intellij.sdk.language;

import com.couchbase.intellij.VirtualFileKeys;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.entity.CouchbaseCollection;
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
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JsonKeyCompletionContributor extends CompletionContributor {
    public JsonKeyCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(JsonLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        PsiElement position = parameters.getPosition();
                        if (!(position.getContainingFile() instanceof JsonFile)) return;

                        PsiFile file = parameters.getOriginalFile();
                        VirtualFile virtualFile = PsiUtilCore.getVirtualFile(file);

                        if (virtualFile == null) {
                            return;
                        }

                        if (ActiveCluster.getInstance() == null
                                || !ActiveCluster.getInstance().getId().equals(virtualFile.getUserData(VirtualFileKeys.CONN_ID))) {
                            return;
                        }

                        // Ensure we are inside an object.
                        if (!isInJsonObjectContext(position)) return;


                        JsonObject jsonObject = PsiTreeUtil.getParentOfType(position, JsonObject.class);
                        if (jsonObject == null) return;

                        List<String> existingKeys = jsonObject.getPropertyList().stream()
                                .map(JsonProperty::getName)
                                .toList();

                        //String[] suggestions = {"val1", "val2", "val3", "val4", "val5", "val6", "val7"};

                        final String bucketName = virtualFile.getUserData(VirtualFileKeys.BUCKET);
                        final String scopeName = virtualFile.getUserData(VirtualFileKeys.SCOPE);
                        final String colName = virtualFile.getUserData(VirtualFileKeys.COLLECTION);


                        Set<String> suggestions = ActiveCluster.getInstance().getChild(bucketName)
                                .flatMap(bucket -> bucket.getChild(scopeName))
                                .flatMap(scope -> scope.getChild(colName))
                                .map(col -> ((CouchbaseCollection) col).getAllAttributeNames())
                                .flatMap(Set::stream)
                                .collect(Collectors.toSet());

                        // Check if the current position or its parent is a DOUBLE_QUOTED_STRING
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
                });
    }

    private boolean isInJsonObjectContext(PsiElement position) {
        // Direct check for position being within an array of primitives, which should return false.
        // First, check if the position is within a JsonArray. If so, we need to further determine the context.
        JsonArray jsonArray = PsiTreeUtil.getParentOfType(position, JsonArray.class);
        if (jsonArray != null) {
            // If the position is also within a JsonObject that is a child of the JsonArray, allow suggestions.
            JsonObject jsonObjectWithinArray = PsiTreeUtil.getParentOfType(position, JsonObject.class, false);
            if (jsonObjectWithinArray != null && jsonArray.equals(jsonObjectWithinArray.getParent())) {
                return isKey(position);
            } else {
                // If we're directly within a JsonArray but not within a JsonObject inside this array, disallow suggestions.
                return false;
            }
        }

        return isKey(position);
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
}