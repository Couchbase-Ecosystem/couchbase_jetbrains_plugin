package com.couchbase.intellij.searchworkbench.contributor;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.json.psi.JsonObject;

import java.util.List;
import java.util.Map;

public interface CBSContributor {

    boolean accept(String key);

    void contributeKey(String parentKey, JsonObject jsonObject, List<String> contributors, CompletionResultSet result);

    void contributeValue(JsonObject jsonObject, String attributeKey, List<String> contributors, Map<String, String> fields);
}
