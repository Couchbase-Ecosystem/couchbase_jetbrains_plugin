package com.couchbase.intellij.tree.iq.intents.actions;

import com.couchbase.client.core.error.BucketNotFoundDuringLoadException;
import com.couchbase.client.core.error.BucketNotFoundException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.collection.CollectionManager;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tree.iq.IQWindowContent;
import com.intellij.openapi.project.Project;

public class CreateCollection implements ActionInterface {
    @Override
    public String fire(Project project, String bucketName, String scopeName, JsonObject intents, JsonObject intent) {
        JsonObject arguments = intent.getObject("arguments");
        StringBuilder prompt = new StringBuilder();

        if (arguments.containsKey("bucketName")) {
            bucketName = arguments.getString("bucketName");
        }

        if (arguments.containsKey("scopeName")) {
            scopeName = arguments.getString("scopeName");
        }

        if (bucketName == null) {
            if (scopeName == null) {
                prompt.append("ask the user in which bucket and scope does he want to create the collection ");
            } else {
                prompt.append("ask the user in which bucket does he want to create the collection ");
            }
        } else if (scopeName == null) {
            prompt.append("ask the user in which scope does he want to create the collection ");
        }

        if (prompt.isEmpty()) {
            Cluster cluster = ActiveCluster.getInstance().getCluster();
            String collectionName = arguments.getString("collectionName");

            try {
                if (!cluster.buckets().getAllBuckets().containsKey(bucketName)) {
                    return String.format("Let the user know that there is no bucket named '%s', ask if he wants to amend the bucket name or create one then return the response as updated JSON only", bucketName);
                }
                CollectionManager colman = cluster.bucket(bucketName).collections();
                String finalScopeName = scopeName;
                ScopeSpec scope = colman.getAllScopes().stream().filter(ss -> finalScopeName.equals(ss.name())).findFirst().orElse(null);
                if (scope == null) {
                    return String.format("Let the user know that there is no scope named '%s' in the bucket '%s', ask if he wants to amend the scope name or create one then return the response as updated JSON only", scopeName, bucketName);
                }
                if (scope.collections().stream().anyMatch(cs -> collectionName.equals(cs.name()))) {
                    return String.format("Let the user know that collection '%s' already exists, ask if they want to amend the collection name and, if they do, respond with amended JSON only", collectionName);
                }

                try {
                    colman.createCollection(CollectionSpec.create(collectionName, scopeName));
                } catch (Exception e) {
                    return "Tell the user that there was a problem and collection creation failed with message '" + e.getMessage() + "'";
                }
                return "Let the user know that collection was created.";
            } catch (BucketNotFoundException e) {
                return "Let the user know that there is no such bucket on the cluster, ask if he wants to amend the bucket name or create one then return the response as updated JSON only";
            }
        }

        prompt.append("and then return the response as updated JSON");
        return prompt.toString();
    }
}
