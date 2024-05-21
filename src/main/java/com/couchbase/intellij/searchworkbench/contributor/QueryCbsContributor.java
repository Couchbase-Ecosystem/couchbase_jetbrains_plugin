package com.couchbase.intellij.searchworkbench.contributor;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;

import java.util.*;
import java.util.stream.Collectors;

public class QueryCbsContributor implements CBSContributor {

    public static final List<String> boolQueries = Arrays.asList("must", "must_not", "should");
    public static final List<String> compound = Arrays.asList("disjuncts", "conjuncts");
    public static final List<String> query = Arrays.asList("boost", "conjuncts");
    public static final List<String> match = Arrays.asList("match", "analyzer", "operator", "boost", "fuzziness", "prefix_length", "field");
    public static final List<String> match_phrase = Arrays.asList("match_phrase", "analyzer", "operator", "boost", "fuzziness", "prefix_length", "field");
    public static final List<String> bool = Arrays.asList("bool", "boost", "field");
    public static final List<String> prefix = Arrays.asList("prefix", "boost", "field");
    public static final List<String> regexp = Arrays.asList("regexp", "boost", "field");
    public static final List<String> terms = Arrays.asList("terms", "boost", "field");

    public static final List<String> cidr = Arrays.asList("cidr", "boost", "field");
    public static final List<String> wildcard = Arrays.asList("wildcard", "boost", "field");
    public static final List<String> term = Arrays.asList("term", "boost", "field", "fuzziness");

    public static final List<String> polygon = Arrays.asList("polygon_points", "boost", "field");


    public static final List<String> numeric = Arrays.asList("min", "max", "inclusive_min", "inclusive_max", "field", "boost");
    public static final List<String> date = Arrays.asList("start", "end", "inclusive_start", "inclusive_end", "field", "boost");

    public static final List<String> radius = Arrays.asList("location", "distance", "field", "boost");
    public static final List<String> rectangle = Arrays.asList("top_left", "bottom_right", "field", "boost");

    public static final List<String> geometry = Arrays.asList("geometry", "field", "boost");

    public static final List<String> special = Arrays.asList("match_all", "match_none");

    // Combine all lists into one HashSet
    public static final Set<String> allQueryKeys = new HashSet<>();

    static {
        allQueryKeys.add("query");
        allQueryKeys.addAll(special);
        allQueryKeys.addAll(boolQueries);
        allQueryKeys.addAll(compound);
        allQueryKeys.addAll(query);
        allQueryKeys.addAll(match);
        allQueryKeys.addAll(match_phrase);
        allQueryKeys.addAll(bool);
        allQueryKeys.addAll(prefix);
        allQueryKeys.addAll(regexp);
        allQueryKeys.addAll(terms);
        allQueryKeys.addAll(cidr);
        allQueryKeys.addAll(wildcard);
        allQueryKeys.addAll(term);
        allQueryKeys.addAll(polygon);
        allQueryKeys.addAll(numeric);
        allQueryKeys.addAll(date);
        allQueryKeys.addAll(radius);
        allQueryKeys.addAll(rectangle);
        allQueryKeys.addAll(geometry);
    }


    @Override
    public boolean accept(String parentKey) {
        return "query".equals(parentKey) || "disjuncts".equals(parentKey) || "conjuncts".equals(parentKey);
    }

    @Override
    public void contributeKey(String parentKey, JsonObject jsonObject, List<String> contributors, CompletionResultSet result) {

        List<String> tempKeys = new ArrayList<>(jsonObject.getPropertyList().stream()
                .map(JsonProperty::getName)
                .toList());

        //needed for testing
        final List<String> existingKeys = tempKeys.stream().filter(e -> !e.contains("IntellijIdeaRulezzz")).collect(Collectors.toList());

        if (existingKeys.isEmpty()) {
            contributors.addAll(allQueryKeys);
        }

        if (boolQueries.stream().anyMatch(existingKeys::contains)) {
            contributors.addAll(boolQueries.stream().filter(e -> !existingKeys.contains(e)).toList());
            addBooleanTemplates(existingKeys, result);
        } else if (!existingKeys.contains("query") && !compound.stream().anyMatch(existingKeys::contains)) {
            HashSet<String> suggestions = new HashSet<>();
            if (!existingKeys.contains("field")) {
                suggestions.add("field");
            }

            if (existingKeys.isEmpty()) {
                result.addElement(CBSTemplates.getQueryTemplate(existingKeys));
                result.addElement(CBSTemplates.getEmptyTemplate("match_all", "match_all template"));
                result.addElement(CBSTemplates.getEmptyTemplate("match_none", "match_all template"));
                suggestions.add("query");
                addBooleanTemplates(existingKeys, result);
                result.addElement(CBSTemplates.getConjunctsTemplate(existingKeys));
                result.addElement(CBSTemplates.getDisjunctsTemplate(existingKeys));
            }

            suggestions.addAll(getMatchContributors(existingKeys, result));
            suggestions.addAll(getMatchPhraseContributors(existingKeys, result));
            suggestions.addAll(getBoolContributors(existingKeys, result));
            suggestions.addAll(getPrefixContributors(existingKeys, result));
            suggestions.addAll(getRegexpContributors(existingKeys, result));
            suggestions.addAll(getTermContributors(existingKeys, result));
            suggestions.addAll(getTermsContributors(existingKeys, result));
            suggestions.addAll(getWildcardContributors(existingKeys, result));
            suggestions.addAll(getNumericContributors(existingKeys, result));
            suggestions.addAll(getDateContributors(existingKeys, result));
            suggestions.addAll(getCidrContributors(existingKeys, result));
            suggestions.addAll(getRadiusContributors(existingKeys, result));
            suggestions.addAll(getRectangleContributors(existingKeys, result));
            suggestions.addAll(getPolygonContributors(existingKeys, result));
            suggestions.addAll(getGeometryContributors(existingKeys, result));

            contributors.addAll(suggestions.stream().filter(e -> !existingKeys.contains(e)).toList());
        }
    }

    public void addBooleanTemplates(List<String> existingKeys, CompletionResultSet result) {
        if (!existingKeys.contains("must")) {
            result.addElement(CBSTemplates.getMustTemplate());
        }

        if (!existingKeys.contains("must_not")) {
            result.addElement(CBSTemplates.getMustNotTemplate());
        }

        if (!existingKeys.contains("should")) {
            result.addElement(CBSTemplates.getShouldTemplate());
        }
    }

    private List<String> getGeometryContributors(List<String> existingKeys, CompletionResultSet result) {
        if (geometry.containsAll(existingKeys)) {
            result.addElement(CBSTemplates.getPointGeoJsonTemplate(existingKeys));
            result.addElement(CBSTemplates.getLineStringTemplate(existingKeys));
            result.addElement(CBSTemplates.getPolygonTemplate(existingKeys));
            result.addElement(CBSTemplates.getMultiPointTemplate(existingKeys));
            result.addElement(CBSTemplates.getMultiLineStringTemplate(existingKeys));
            result.addElement(CBSTemplates.getMultiPolygonTemplate(existingKeys));
            result.addElement(CBSTemplates.getEnvelopeTemplate(existingKeys));
            result.addElement(CBSTemplates.getCircleTemplate(existingKeys));
            result.addElement(CBSTemplates.getGeometryCollectionTemplate(existingKeys));
        }

        return genericContributor(geometry, existingKeys, result);
    }

    private List<String> getPolygonContributors(List<String> existingKeys, CompletionResultSet result) {
        return genericContributor(polygon, existingKeys, result,
                getSingleTemplate("polygon_points_query", "Polygon-Based Geopoint Queries\n", new ArrayList<>(polygon)));
    }

    private List<String> getRectangleContributors(List<String> existingKeys, CompletionResultSet result) {
        if (rectangle.containsAll(existingKeys)) {
            result.addElement(CBSTemplates.getRectangleTemplate(existingKeys));
        }
        return genericContributor(rectangle, existingKeys, result);
    }

    private List<String> getRadiusContributors(List<String> existingKeys, CompletionResultSet result) {
        if (radius.containsAll(existingKeys)) {
            result.addElement(CBSTemplates.getRadiusTemplate(existingKeys));
        }
        return genericContributor(radius, existingKeys, result);
    }

    private List<String> genericContributor(List<String> keys, List<String> existingKeys, CompletionResultSet result) {
        return genericContributor(keys, existingKeys, result, new ArrayList<>());
    }

    private List<String> genericContributor(List<String> keys, List<String> existingKeys,
                                            CompletionResultSet result, List<CBSTemplateDef> templateDefs) {
        if (keys.containsAll(existingKeys)) {
            if (!templateDefs.isEmpty()) {
                for (CBSTemplateDef def : templateDefs) {
                    def.getAttrs().removeAll(existingKeys);
                    def.getAttrs().remove("boost");
                    result.addElement(CBSTemplates.getGenericTemplate(def.getKey(), def.getDesc(), def.getAttrs()));
                }
            }

            return keys;
        } else {
            return new ArrayList<>();
        }
    }

    private List<String> getMatchContributors(List<String> existingKeys, CompletionResultSet result) {
        List<CBSTemplateDef> templates = Arrays.asList(
                new CBSTemplateDef("match_query", "Simple match query search", new ArrayList<>(Arrays.asList("field", "match"))),
                new CBSTemplateDef("match_query_all", "Match query search with all attributes", new ArrayList<>(match))
        );

        return genericContributor(match, existingKeys, result, templates);
    }

    private List<String> getMatchPhraseContributors(List<String> existingKeys, CompletionResultSet result) {
        List<CBSTemplateDef> templates = Arrays.asList(
                new CBSTemplateDef("match_phrase_query", "Simple match phrase query search", new ArrayList<>(Arrays.asList("field", "match_phrase"))),
                new CBSTemplateDef("match_phrase_query_all", "Match phrase query search with all attributes", new ArrayList<>(match_phrase))
        );

        return genericContributor(match_phrase, existingKeys, result, templates);
    }

    private List<String> getBoolContributors(List<String> existingKeys, CompletionResultSet result) {
        return genericContributor(bool, existingKeys, result,
                getSingleTemplate("bool_query", "Boolean query", new ArrayList<>(bool)));
    }

    private List<String> getCidrContributors(List<String> existingKeys, CompletionResultSet result) {
        return genericContributor(cidr, existingKeys, result,
                getSingleTemplate("cidr_query", "CIDR query", new ArrayList<>(cidr)));
    }

    private List<String> getPrefixContributors(List<String> existingKeys, CompletionResultSet result) {
        return genericContributor(prefix, existingKeys, result,
                getSingleTemplate("prefix_query", "Prefix query", new ArrayList<>(prefix)));
    }

    private List<String> getRegexpContributors(List<String> existingKeys, CompletionResultSet result) {
        return genericContributor(regexp, existingKeys, result,
                getSingleTemplate("regex_query", "Regex query", new ArrayList<>(regexp)));
    }

    private List<String> getTermContributors(List<String> existingKeys, CompletionResultSet result) {
        return genericContributor(term, existingKeys, result,
                getSingleTemplate("term_query", "Term query", new ArrayList<>(term)));
    }

    private List<String> getTermsContributors(List<String> existingKeys, CompletionResultSet result) {
        return genericContributor(terms, existingKeys, result,
                getSingleTemplate("terms_query", "Terms query", new ArrayList<>(terms)));
    }

    private List<String> getWildcardContributors(List<String> existingKeys, CompletionResultSet result) {
        return genericContributor(wildcard, existingKeys, result,
                getSingleTemplate("wildcard_query", "Wildcard query", new ArrayList<>(wildcard)));
    }

    private List<CBSTemplateDef> getSingleTemplate(String key, String desc, List<String> fields) {
        return Arrays.asList(
                new CBSTemplateDef(key, desc, new ArrayList<>(fields))
        );
    }

    private List<String> getNumericContributors(List<String> existingKeys, CompletionResultSet result) {
        List<CBSTemplateDef> templates = Arrays.asList(
                new CBSTemplateDef("numeric_range", "Simple numeric range search", new ArrayList<>(Arrays.asList("field", "min", "max"))),
                new CBSTemplateDef("numeric_range_all", "Data numeric search with all attributes", new ArrayList<>(numeric))
        );
        return genericContributor(numeric, existingKeys, result, templates);
    }

    private List<String> getDateContributors(List<String> existingKeys, CompletionResultSet result) {
        List<CBSTemplateDef> templates = Arrays.asList(
                new CBSTemplateDef("date_range", "Simple data range search", new ArrayList<>(Arrays.asList("field", "start", "end"))),
                new CBSTemplateDef("date_range_all", "Data range search with all attributes", new ArrayList<>(date))
        );

        return genericContributor(date, existingKeys, result, templates);
    }


    @Override
    public void contributeValue(JsonObject jsonObject, String attributeKey, List<String> contributors, Map<String, String> fields) {
        if ("operator".equals(attributeKey)) {
            contributors.add("or");
            contributors.add("and");
        } else if (attributeKey.equals("field")) {
            contributors.addAll(fields.keySet());
        }
    }
}


