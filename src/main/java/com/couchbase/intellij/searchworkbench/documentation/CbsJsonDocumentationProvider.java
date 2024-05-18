package com.couchbase.intellij.searchworkbench.documentation;

import com.couchbase.intellij.workbench.chart.ChartUtil;
import com.intellij.json.JsonFileType;
import com.intellij.json.psi.JsonArray;
import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonProperty;
import com.intellij.lang.documentation.DocumentationProvider;
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

                String type = null;
                if (property.getParent() != null && property.getParent().getParent() instanceof JsonFile) {
                    type = null;
                } else if (property.getParent() != null && property.getParent().getParent() instanceof JsonProperty) {
                    type = ((JsonProperty) property.getParent().getParent()).getName();
                } else if (property.getParent().getParent() instanceof JsonArray) {
                    type = ((JsonProperty) property.getParent().getParent().getParent()).getName();
                }

                return getDocumentationForKey(key, type);
            }
        }
        return null;
    }

    private boolean isCbsJsonFile(PsiElement element) {
        PsiFile file = element.getContainingFile();
        return file != null && file.getName().endsWith(".cbs.json");
    }

    private String getDocumentationForKey(String key, String type) {
        // Provide documentation for each key
        switch (key) {
            case "query":
                if (type == null) {
                    return ChartUtil.loadResourceAsString("/docs/search/query.html");
                } else {
                    return ChartUtil.loadResourceAsString("/docs/search/query_string.html");
                }
            case "must":
                return ChartUtil.loadResourceAsString("/docs/search/must.html");
            case "must_not":
                return ChartUtil.loadResourceAsString("/docs/search/must_not.html");
            case "should":
                return ChartUtil.loadResourceAsString("/docs/search/should.html");
            case "conjuncts":
                return ChartUtil.loadResourceAsString("/docs/search/conjuncts.html");
            case "disjuncts":
                return ChartUtil.loadResourceAsString("/docs/search/disjuncts.html");
            case "match":
                return ChartUtil.loadResourceAsString("/docs/search/match.html");
            case "match_phrase":
                return ChartUtil.loadResourceAsString("/docs/search/match_phrase.html");
            case "bool":
                return ChartUtil.loadResourceAsString("/docs/search/bool.html");
            case "prefix":
                return ChartUtil.loadResourceAsString("/docs/search/prefix.html");
            case "regexp":
                return ChartUtil.loadResourceAsString("/docs/search/regexp.html");
            case "term":
                return ChartUtil.loadResourceAsString("/docs/search/term.html");
            case "terms":
                return ChartUtil.loadResourceAsString("/docs/search/terms.html");
            case "wildcard":
                return ChartUtil.loadResourceAsString("/docs/search/wildcard.html");
            case "min":
                return ChartUtil.loadResourceAsString("/docs/search/min.html");
            case "max":
                return ChartUtil.loadResourceAsString("/docs/search/max.html");
            case "inclusive_max":
                return ChartUtil.loadResourceAsString("/docs/search/inclusive_max.html");
            case "inclusive_min":
                return ChartUtil.loadResourceAsString("/docs/search/inclusive_min.html");
            case "start":
                return ChartUtil.loadResourceAsString("/docs/search/start.html");
            case "end":
                return ChartUtil.loadResourceAsString("/docs/search/end.html");
            case "inclusive_start":
                return ChartUtil.loadResourceAsString("/docs/search/inclusive_start.html");
            case "inclusive_end":
                return ChartUtil.loadResourceAsString("/docs/search/inclusive_end.html");
            case "cidr":
                return ChartUtil.loadResourceAsString("/docs/search/cidr.html");
            case "knn":
                return ChartUtil.loadResourceAsString("/docs/search/knn.html");
            case "k":
                return ChartUtil.loadResourceAsString("/docs/search/k.html");
            case "vector":
                return ChartUtil.loadResourceAsString("/docs/search/vector.html");
            case "distance":
                return ChartUtil.loadResourceAsString("/docs/search/distance.html");
            case "location":
                return ChartUtil.loadResourceAsString("/docs/search/location.html");
            case "lat":
                return ChartUtil.loadResourceAsString("/docs/search/lat.html");
            case "lon":
                return ChartUtil.loadResourceAsString("/docs/search/lon.html");
            case "top_left":
                return ChartUtil.loadResourceAsString("/docs/search/top_left.html");
            case "bottom_right":
                return ChartUtil.loadResourceAsString("/docs/search/bottom_right.html");
            case "polygon_points":
                return ChartUtil.loadResourceAsString("/docs/search/polygon_points.html");
            case "geometry":
                return ChartUtil.loadResourceAsString("/docs/search/geometry.html");
            case "shape":
                return ChartUtil.loadResourceAsString("/docs/search/shape.html");
            case "type":
                if ("shape".equals(type)) {
                    return ChartUtil.loadResourceAsString("/docs/search/type_shape.html");
                } else {
                    return ChartUtil.loadResourceAsString("/docs/search/type_facet.html");
                }
            case "coordinates":
                return ChartUtil.loadResourceAsString("/docs/search/coordinates.html");
            case "relation":
                return ChartUtil.loadResourceAsString("/docs/search/relation.html");
            case "geometries":
                return ChartUtil.loadResourceAsString("/docs/search/geometries.html");
            case "radius":
                return ChartUtil.loadResourceAsString("/docs/search/radius.html");
            case "match_all":
                return ChartUtil.loadResourceAsString("/docs/search/match_all.html");
            case "match_none":
                return ChartUtil.loadResourceAsString("/docs/search/match_none.html");
            case "analyzer":
                return ChartUtil.loadResourceAsString("/docs/search/analyzer.html");
            case "boost":
                return ChartUtil.loadResourceAsString("/docs/search/boost.html");
            case "field":
                return ChartUtil.loadResourceAsString("/docs/search/field.html");
            case "fuzziness":
                return ChartUtil.loadResourceAsString("/docs/search/fuzziness.html");
            case "operator":
                return ChartUtil.loadResourceAsString("/docs/search/operator.html");
            case "prefix_length":
                return ChartUtil.loadResourceAsString("/docs/search/prefix_length.html");
            case "size":
            case "limit":
                return ChartUtil.loadResourceAsString("/docs/search/limit.html");
            case "from":
            case "offset":
                return ChartUtil.loadResourceAsString("/docs/search/offset.html");
            case "fields":
                return ChartUtil.loadResourceAsString("/docs/search/fields.html");
            default:
                return "No documentation available for this key.";
        }
    }


    @Override
    public @Nullable List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        if (element instanceof JsonProperty) {
            JsonProperty property = (JsonProperty) element;
            String key = property.getName();

            String type;
            if (property.getParent() != null && property.getParent().getParent() instanceof JsonFile) {
                type = null;
            } else if (property.getParent() != null && property.getParent().getParent() instanceof JsonProperty) {
                type = ((JsonProperty) property.getParent().getParent()).getName();
            } else if (property.getParent().getParent() instanceof JsonArray) {
                type = ((JsonProperty) property.getParent().getParent().getParent()).getName();
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
            case "consistency":
            case "level":
            case "results":
                return Collections.singletonList("https://docs.couchbase.com/server/current/search/search-request-params.html#consistency");
            case "vectors":
                return Collections.singletonList("https://docs.couchbase.com/server/current/search/search-request-params.html#vectors");
            case "highlight":
            case "style":
                return Collections.singletonList("https://docs.couchbase.com/server/current/search/search-request-params.html#highlight");
            case "fields":
                if ("highlight".equals(type)) {
                    return Collections.singletonList("https://docs.couchbase.com/server/current/search/search-request-params.html#highlight");
                } else {
                    return Collections.singletonList("https://docs.couchbase.com/server/current/search/search-request-params.html");
                }
            case "size":
            case "limit":
            case "from":
            case "offset":
            case "includeLocations":
            case "explain":
            case "score":
            case "search_after":
            case "search_before":
            case "collections":
                return Collections.singletonList("https://docs.couchbase.com/server/current/search/search-request-params.html");
            case "sort":
                return Collections.singletonList("https://docs.couchbase.com/server/current/search/search-request-params.html#sort");
            case "relation":
                return Collections.singletonList("https://docs.couchbase.com/server/current/search/search-request-params.html#geojson-queries-point");
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
