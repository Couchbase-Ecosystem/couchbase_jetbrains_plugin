package com.couchbase.intellij.searchworkbench.validator;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import org.apache.commons.collections.map.HashedMap;

import java.util.Map;

/**
 * Validate the CTL object https://docs.couchbase.com/server/current/search/search-request-params.html#ctl
 */
public class CTLObjectValidator implements SearchObjectValidator {

    public boolean accept(String key) {
        return "ctl".equals(key);
    }

    public void validate(JsonObject jsonObject, ProblemsHolder holder) {


        Map<String, Integer> counter = new HashedMap();
        for (JsonProperty property : jsonObject.getPropertyList()) {
            if (!"consistency".equals(property.getName())
                    && !"timeout".equals(property.getName())) {
                holder.registerProblem(property, CBSMessageUtil.getUnexpectedAttUnder(property.getName(), "ctl"), ProblemHighlightType.GENERIC_ERROR);
            } else {
                counter.put(property.getName(), counter.getOrDefault(property.getName(), 0) + 1);
            }
        }

        for (Map.Entry<String, Integer> entry : counter.entrySet()) {
            if (entry.getValue() > 1) {
                holder.registerProblem(jsonObject, CBSMessageUtil.singleOptionalKeyOccurrenceMessage(entry.getKey(), "ctl"),
                        ProblemHighlightType.GENERIC_ERROR);
            }
        }

        if (counter.getOrDefault("consistency", 0) == 0) {
            holder.registerProblem(jsonObject, CBSMessageUtil.singleRequiredKeyOccurrenceMessage("consistency", "ctl"),
                    ProblemHighlightType.GENERIC_ERROR);
        }
    }

}
