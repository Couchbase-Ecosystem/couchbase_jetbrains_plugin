package com.couchbase.intellij.tree.iq.intents.actions;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.project.Project;

public class CreateScope implements ActionInterface {
    public String fire(Project project, String bucketName, String scopeName, JsonObject intents, JsonObject intent) {
        scopeName = intent.getString("scopeName");

        if (bucketName == null || bucketName.isEmpty()) {
            if (scopeName == null || scopeName.isEmpty()) {
                return "ask the user in which bucket does he want to create the scope and how he wants to name it ";
            } else {
                return "ask the user in which bucket does he want to create the scope ";
            }
        }

        Cluster cluster = ActiveCluster.getInstance().getCluster();
        if (cluster == null) {
            return "ask the user to connect to their couchbase cluster first ";
        }

        try {
            cluster.bucket(bucketName).collections().createScope(scopeName);
            return "let the user know that you created the scope ";
        } catch (Exception e) {
            Log.error(e);
            return "let the user know that there was a problem creating scope and details are available in couchbase plugin log window ";
        }
    }
}
