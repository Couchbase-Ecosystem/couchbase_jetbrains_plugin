package com.couchbase.intellij.searchworkbench.contributor;

import com.intellij.json.psi.JsonObject;

import java.util.Arrays;
import java.util.List;

public class GeometryCbsContributor implements CBSContributor {

    public static final List<String> keys = Arrays.asList("shape", "shape");

    @Override
    public boolean accept(String parentKey) {
        return "geometry".equals(parentKey);
    }

    @Override
    public void contributeKey(String parentKey, JsonObject jsonObject, List<String> contributors) {
        ContributorUtil.suggestMissing(jsonObject, keys, contributors);
    }

    @Override
    public void contributeValue(JsonObject jsonObject, String attributeKey, List<String> contributors) {
        if ("relation".equals(attributeKey)) {
            contributors.add("intersects");
            contributors.add("contains");
            contributors.add("within");
        }
    }

}
