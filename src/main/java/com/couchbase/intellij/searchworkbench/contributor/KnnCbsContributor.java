package com.couchbase.intellij.searchworkbench.contributor;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;

import java.util.Arrays;
import java.util.List;

public class KnnCbsContributor implements CBSContributor {

    public static final List<String> keys = Arrays.asList("k", "field", "vector", "boost");

    @Override
    public boolean accept(String parentKey) {
        return "knn".equals(parentKey);
    }

    @Override
    public void contributeKey(String parentKey, JsonObject jsonObject, List<String> contributors, CompletionResultSet result) {
        List<String> existingKeys = jsonObject.getPropertyList().stream()
                .map(JsonProperty::getName)
                .toList();
        ContributorUtil.suggestMissing(jsonObject, keys, contributors);
        result.addElement(CBSTemplates.getVectorTemplate(existingKeys));
    }

    @Override
    public void contributeValue(JsonObject jsonObject, String attributeKey, List<String> contributors) {
        //do nothing
    }

}
