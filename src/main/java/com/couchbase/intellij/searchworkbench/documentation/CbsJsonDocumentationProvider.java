package com.couchbase.intellij.searchworkbench.documentation;

import com.intellij.json.JsonFileType;
import com.intellij.json.psi.JsonArray;
import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonProperty;
import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CbsJsonDocumentationProvider implements DocumentationProvider {

    @Override
    public @Nullable String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (isCbsJsonFile(element)) {
            if (element instanceof JsonProperty) {
                JsonProperty property = (JsonProperty) element;
                String key = property.getName();
                return getDocumentationForKey(key);
            }
        }
        return null;
    }

    private boolean isCbsJsonFile(PsiElement element) {
        PsiFile file = element.getContainingFile();
        return file != null && file.getName().endsWith(".cbs.json");
    }


    private String getDocumentationForKey(String key) {
        // Provide documentation for each key
        switch (key) {
            case "key1":
                return "Documentation for key1: This key does ...";
            case "key2":
                return "Documentation for key2: This key is used for ...";
            // Add more keys as necessary
            default:
                return "No documentation available for this key.";
        }
    }

    @Override
    public @Nullable
    @NlsContexts.PopupTitle String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        if (element instanceof JsonProperty) {
            JsonProperty property = (JsonProperty) element;
            String key = property.getName();
            return getQuickInfoForKey(key);
        }
        return null;
    }

    private String getQuickInfoForKey(String key) {
        // Provide quick navigate info for each key
        switch (key) {
            case "key1":
                return "Quick info for key1: This key is used for ...";
            case "key2":
                return "Quick info for key2: This key represents ...";
            // Add more keys as necessary
            default:
                return "No quick info available for this key.";
        }
    }

    @Override
    public @Nullable List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        if (element instanceof JsonProperty) {
            JsonProperty property = (JsonProperty) element;
            String key = property.getName();

            String type;
            if (property.getParent() instanceof JsonFile) {
                type = null;
            } else if (property.getParent() instanceof JsonProperty) {
                type = ((JsonProperty) property.getParent()).getName();
            } else if (property.getParent() instanceof JsonArray) {
                type = ((JsonProperty) property.getParent().getParent()).getName();
            } else {
                throw new NotImplementedException("type is not supported");
            }

            return getUrlsForKey(key, type);
        }
        return null;
    }

    private List<String> getUrlsForKey(String key, String type) {
        switch (key) {
            case "query":
                if (type == null) {
                    return Collections.singletonList("https://docs.couchbase.com/server/current/search/search-request-params.html#query-object");
                } else {
                    return Collections.singletonList("https://docs.couchbase.com/server/current/search/search-request-params.html#query-string-query-syntax");
                }
            case "knn":
            case "k":
            case "vector":
                return Collections.singletonList("https://docs.couchbase.com/server/current/search/search-request-params.html#knn-object");
            case "must":
            case "should":
            case "must_not":
                return Collections.singletonList("https://docs.couchbase.com/server/current/search/search-request-params.html#boolean-queries");
            case "conjuncts":
            case "disjuncts":
                return Collections.singletonList("https://docs.couchbase.com/server/current/search/search-request-params.html#compound-queries");
            case "match":
            case "match_phrase":
                return Collections.singletonList("https://docs.couchbase.com/server/current/search/search-request-params.html#analytic-queries");
            case "bool":
            case "prefix":
            case "regexp":
            case "term":
            case "terms":
            case "wildcard":
                return Collections.singletonList("https://docs.couchbase.com/server/current/search/search-request-params.html#non-analytic-queries");
            case "min":
            case "max":
            case "inclusive_min":
            case "inclusive_max":
                return Collections.singletonList("https://docs.couchbase.com/server/current/search/search-request-params.html#numeric-range-queries");
            case "start":
            case "end":
            case "inclusive_start":
            case "inclusive_end":
                return Collections.singletonList("https://docs.couchbase.com/server/current/search/search-request-params.html#date-range-queries");
            case "cidr":
                return Collections.singletonList("https://docs.couchbase.com/server/current/search/search-request-params.html#ip-address-range-queries");
            case "location":
            case "distance":
                return Collections.singletonList("https://docs.couchbase.com/server/current/search/search-request-params.html#geopoint-queries-distance");
            case "top_left":
            case "bottom_right":
                return Collections.singletonList("https://docs.couchbase.com/server/current/search/search-request-params.html#geopoint-queries-rectangle");
            case "polygon_points":
                return Collections.singletonList("https://docs.couchbase.com/server/current/search/search-request-params.html#geopoint-queries-polygon");
            case "radius":
                return Collections.singletonList("https://docs.couchbase.com/server/current/search/search-request-params.html#geojson-queries-circle");
            case "match_all":
            case "match_none":
                return Collections.singletonList("https://docs.couchbase.com/server/current/search/search-request-params.html#special-queries");
            case "analyzer":
            case "boost":
            case "field":
            case "fuzziness":
            case "operator":
            case "prefix_length":
                return Collections.singletonList("https://docs.couchbase.com/server/current/search/search-request-params.html#additional-query-properties");
            case "ctl":
                return Collections.singletonList("https://docs.couchbase.com/server/current/search/search-request-params.html#ctl");
                
            default:
                return null;
        }
    }

    @Override
    public @Nullable PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
        if (object instanceof String) {
            String key = (String) object;
            if (isRecognizedKey(key)) {
                return createDummyJsonProperty(psiManager, key);
            }
        }
        return null;
    }

    private boolean isRecognizedKey(String key) {
        return "key1".equals(key) || "key2".equals(key); // Add more keys as necessary
    }

    private PsiElement createDummyJsonProperty(PsiManager psiManager, String key) {
        PsiFileFactory factory = PsiFileFactory.getInstance(psiManager.getProject());
        JsonFile dummyFile = (JsonFile) factory.createFileFromText("dummy.json", JsonFileType.INSTANCE, "{ \"" + key + "\": null }");
        JsonProperty property = (JsonProperty) dummyFile.getTopLevelValue().getFirstChild();
        return property;
    }

    @Override
    public @Nullable PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
        return null;
    }

}
