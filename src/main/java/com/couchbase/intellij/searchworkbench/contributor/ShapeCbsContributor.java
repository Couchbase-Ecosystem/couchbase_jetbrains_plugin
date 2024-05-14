package com.couchbase.intellij.searchworkbench.contributor;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.JsonStringLiteral;

import java.util.ArrayList;
import java.util.List;

public class ShapeCbsContributor implements CBSContributor {

    @Override
    public boolean accept(String parentKey) {
        return "shape".equals(parentKey);
    }

    @Override
    public void contributeKey(String parentKey, JsonObject jsonObject, List<String> contributors, CompletionResultSet result) {
        String type = null;

        for (JsonProperty property : jsonObject.getPropertyList()) {
            if ("type".equals(property.getName())
                    && property.getValue() instanceof JsonStringLiteral) {
                type = ((JsonStringLiteral) property.getValue()).getValue();
            }
        }

        List<String> suggestions = new ArrayList<>();

        if ("Circle".equals(type)) {
            suggestions.add("coordinates");
            suggestions.add("type");
            suggestions.add("radius");

        } else if ("GeometryCollection".equals(type)) {
            suggestions.add("type");
            suggestions.add("geometries");
        } else if (type == null) {
            suggestions.add("coordinates");
            suggestions.add("type");
            suggestions.add("radius");
            suggestions.add("geometries");
        } else {
            suggestions.add("coordinates");
            suggestions.add("type");
        }

        ContributorUtil.suggestMissing(jsonObject, suggestions, contributors);
    }

    @Override
    public void contributeValue(JsonObject jsonObject, String attributeKey, List<String> contributors) {
        if ("type".equals(attributeKey)) {
            contributors.add("Point");
            contributors.add("LineString");
            contributors.add("Polygon");
            contributors.add("MultiPoint");
            contributors.add("MultiLineString");
            contributors.add("MultiPolygon");
            contributors.add("GeometryCollection");
            contributors.add("Circle");
            contributors.add("Envelope");
        }
    }

}
