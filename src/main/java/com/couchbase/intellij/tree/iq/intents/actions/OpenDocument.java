package com.couchbase.intellij.tree.iq.intents.actions;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.ExistsResult;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tree.OpenDocumentDialog;
import com.couchbase.intellij.workbench.CustomSqlFileEditor;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.project.Project;

public class OpenDocument implements ActionInterface{
    @Override
    public String fire(Project project, String bucketName, String scopeName, JsonObject intents, JsonObject intent) {
        JsonArray documentIds = intents.getArray("ids");
        JsonArray collections = intents.getArray("collections");

        if (collections == null || collections.isEmpty() || collections.size() > 0) {
            return "ask the user a document from which single collection does they want to open and then respond with the updated JSON ";
        }

        if (documentIds == null || documentIds.isEmpty()) {
            return "ask the user for the id of the document they would like to open and then respond with the updated JSON ";
        }

        String collectionName = collections.getString(0);

        StringBuilder response = new StringBuilder();
        documentIds.forEach(documentId -> {
            if (documentId instanceof String) {
                ExistsResult result = ActiveCluster.getInstance().get().bucket(bucketName).scope(scopeName).collection(collectionName).exists((String) documentId);
                if (!result.exists()) {
                    response.append("Let the user know that there's no document with such id in the collection ");
                } else {
                    CustomSqlFileEditor.openDocument(project, bucketName, scopeName, collectionName, (String) documentId, null);
                }
            } else {
                Log.warning("Document id is not a string: " + documentId);
            }
        });

        if (response.isEmpty()) {
            return "let the user know that documents were opened in the editor ";
        } else {
            return response.toString();
        }
    }
}
