package com.couchbase.intellij.searchworkbench.contributor;

import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;

import java.util.*;
import java.util.stream.Collectors;

public class QueryCbsContributor implements CBSContributor {

    public static final List<String> boolQueries = Arrays.asList("must", "must_not", "should");
    public static final List<String> compound = Arrays.asList("disjuncts", "conjuncts");
    public static final List<String> query = Arrays.asList("boost", "conjuncts");
    public static final List<String> match = Arrays.asList("match", "analyzer", "operator", "boost", "fuzziness", "prefix_length", "field");
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
    public void contributeKey(String parentKey, JsonObject jsonObject, List<String> contributors) {

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
        } else if (compound.stream().anyMatch(existingKeys::contains)) {
            contributors.addAll(compound.stream().filter(e -> !existingKeys.contains(e)).toList());
        } else if (!existingKeys.contains("query")) {
            HashSet<String> suggestions = new HashSet<>();
            if (!existingKeys.contains("field")) {
                suggestions.add("field");
            }

            if (existingKeys.isEmpty()) {
                suggestions.add("query");
            }

            suggestions.addAll(getMatchContributors(existingKeys));
            suggestions.addAll(getBoolContributors(existingKeys));
            suggestions.addAll(getPrefixContributors(existingKeys));
            suggestions.addAll(getRegexpContributors(existingKeys));
            suggestions.addAll(getTermContributors(existingKeys));
            suggestions.addAll(getTermsContributors(existingKeys));
            suggestions.addAll(getWildcardContributors(existingKeys));
            suggestions.addAll(getNumericContributors(existingKeys));
            suggestions.addAll(getDateContributors(existingKeys));
            suggestions.addAll(getCidrContributors(existingKeys));
            suggestions.addAll(getRadiusContributors(existingKeys));
            suggestions.addAll(getRectangleContributors(existingKeys));
            suggestions.addAll(getPolygonContributors(existingKeys));
            suggestions.addAll(getGeometryContributors(existingKeys));


            List<String> suggestions2 = suggestions.stream().filter(e -> !existingKeys.contains(e)).toList();
            System.out.println(suggestions2);
            contributors.addAll(suggestions2);
        }
    }


    private List<String> getGeometryContributors(List<String> existingKeys) {
        return genericContributor(geometry, existingKeys);
    }

    private List<String> getPolygonContributors(List<String> existingKeys) {
        return genericContributor(polygon, existingKeys);
    }

    private List<String> getRectangleContributors(List<String> existingKeys) {
        return genericContributor(rectangle, existingKeys);
    }

    private List<String> getRadiusContributors(List<String> existingKeys) {
        return genericContributor(radius, existingKeys);
    }

    private List<String> genericContributor(List<String> keys, List<String> existingKeys) {
        if (keys.containsAll(existingKeys)) {
            return keys;
        } else {
            return new ArrayList<>();
        }
    }

    private List<String> getMatchContributors(List<String> existingKeys) {
        return genericContributor(match, existingKeys);
    }

    private List<String> getBoolContributors(List<String> existingKeys) {
        return genericContributor(bool, existingKeys);
    }

    private List<String> getCidrContributors(List<String> existingKeys) {
        return genericContributor(cidr, existingKeys);
    }

    private List<String> getPrefixContributors(List<String> existingKeys) {
        return genericContributor(prefix, existingKeys);
    }

    private List<String> getRegexpContributors(List<String> existingKeys) {
        return genericContributor(regexp, existingKeys);
    }

    private List<String> getTermContributors(List<String> existingKeys) {
        return genericContributor(term, existingKeys);
    }

    private List<String> getTermsContributors(List<String> existingKeys) {
        return genericContributor(terms, existingKeys);
    }

    private List<String> getWildcardContributors(List<String> existingKeys) {
        return genericContributor(wildcard, existingKeys);
    }

    private List<String> getNumericContributors(List<String> existingKeys) {
        return genericContributor(numeric, existingKeys);
    }

    private List<String> getDateContributors(List<String> existingKeys) {
        return genericContributor(date, existingKeys);
    }


    @Override
    public void contributeValue(JsonObject jsonObject, String attributeKey, List<String> contributors) {

    }


    //ContributorUtil.suggestMissing(jsonObject, keys, contributors);
}

//    @Override
//    public void contributeValue(JsonObject jsonObject, String attributeKey, List<String> contributors) {
////        if ("level".equals(attributeKey)) {
////            contributors.add("at_plus");
////            contributors.add("not_bounded");
////        } else if ("results".equals(attributeKey)) {
////            contributors.add("complete");
////        }
//    }


