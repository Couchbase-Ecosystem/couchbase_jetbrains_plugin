package com.couchbase.intellij.searchworkbench.contributor;

import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;

import java.util.List;

public class ContributorUtil {

    public static void suggestMissing(JsonObject jsonObject, List<String> keys, List<String> suggestions) {
        List<String> existingKeys = jsonObject.getPropertyList().stream()
                .map(JsonProperty::getName)
                .toList();
        suggestions.addAll(keys.stream().filter(e -> !existingKeys.contains(e)).toList());
    }
}
