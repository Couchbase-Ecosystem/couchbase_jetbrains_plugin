package com.couchbase.intellij.tree.iq.intents.actions;

import com.couchbase.client.java.json.JsonObject;
import com.intellij.openapi.project.Project;

public interface ActionInterface {
    String fire(Project project, String bucketName, String scopeName, JsonObject intents, JsonObject intent);
    default boolean needsContext() {
        return true;
    }
}
