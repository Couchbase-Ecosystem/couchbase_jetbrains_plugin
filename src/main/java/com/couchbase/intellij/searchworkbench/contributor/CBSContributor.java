package com.couchbase.intellij.searchworkbench.contributor;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.json.psi.JsonObject;

import java.util.List;

public interface CBSContributor {

    boolean accept(String key);

    void contributeKey(String parentKey, JsonObject jsonObject, List<String> contributors, CompletionResultSet result);

    void contributeValue(JsonObject jsonObject, String attributeKey, List<String> contributors);
}
