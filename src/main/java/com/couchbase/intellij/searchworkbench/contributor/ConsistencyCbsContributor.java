package com.couchbase.intellij.searchworkbench.contributor;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.json.psi.JsonObject;

import java.util.Arrays;
import java.util.List;

public class ConsistencyCbsContributor implements CBSContributor {

    public static final List<String> keys = Arrays.asList("vectors", "level", "results");

    @Override
    public boolean accept(String parentKey) {
        return "consistency".equals(parentKey);
    }

    @Override
    public void contributeKey(String parentKey, JsonObject jsonObject, List<String> contributors, CompletionResultSet result) {
        ContributorUtil.suggestMissing(jsonObject, keys, contributors);
    }

    @Override
    public void contributeValue(JsonObject jsonObject, String attributeKey, List<String> contributors) {
        if ("level".equals(attributeKey)) {
            contributors.add("at_plus");
            contributors.add("not_bounded");
        } else if ("results".equals(attributeKey)) {
            contributors.add("complete");
        }
    }

}
