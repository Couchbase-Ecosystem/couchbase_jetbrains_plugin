package com.couchbase.intellij.searchworkbench.contributor;

import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;

import java.util.List;

public class BooleanCbsContributor implements CBSContributor {


    @Override
    public boolean accept(String parentKey) {
        return "must".equals(parentKey) || "must_not".equals(parentKey) || "should".equals(parentKey);
    }

    @Override
    public void contributeKey(String parentKey, JsonObject jsonObject, List<String> contributors) {
        List<String> existingKeys = jsonObject.getPropertyList().stream()
                .map(JsonProperty::getName)
                .toList();

        if ("must".equals(parentKey) && !existingKeys.contains("conjuncts")) {
            contributors.add("conjuncts");

        } else if (!existingKeys.contains("disjuncts")
                && ("must_not".equals(parentKey) || "should".equals(parentKey))) {
            contributors.add("disjuncts");
        }
    }

    @Override
    public void contributeValue(JsonObject jsonObject, String attributeKey, List<String> contributors) {
    }

}
