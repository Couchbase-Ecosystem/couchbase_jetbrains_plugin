package com.couchbase.intellij.listener;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.MutationResult;
import com.couchbase.intellij.DocumentFormatter;
import com.couchbase.intellij.VirtualFileKeys;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.InferHelper;
import com.couchbase.intellij.persistence.CouchbaseDocumentVirtualFile;
import com.couchbase.intellij.tree.cblite.ActiveCBLDatabase;
import com.couchbase.intellij.workbench.Log;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.MutableDocument;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentSynchronizationVetoer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class JsonDocumentListener extends FileDocumentSynchronizationVetoer {

    private static Project getProject(VirtualFile virtualFile) {
        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        if (virtualFile instanceof CouchbaseDocumentVirtualFile) {
            return ((CouchbaseDocumentVirtualFile) virtualFile).getProject();
        }
        for (Project project : projects) {
            if (ProjectRootManager.getInstance(project).getFileIndex().isInContent(virtualFile)) {
                return project;
            }
        }
        throw new IllegalStateException("Could not find the project that the virtual file belongs to. This might be a bug.");
    }


    @Override
    public boolean maySaveDocument(@NotNull Document document, boolean isSaveExplicit) {
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);


        //filtering files from our plugin
        if (file != null && file.getUserData(VirtualFileKeys.CLUSTER) != null && !ActiveCluster.getInstance().isReadOnlyMode()) {
            if (!ActiveCluster.getInstance().getId().equals(file.getUserData(VirtualFileKeys.CONN_ID))) {
                Log.info("The file " + file.getUserData(VirtualFileKeys.ID) + " was not saved, as it belongs to a connection that is no longer active.");
                return true;
            } else {
                Collection collection = ActiveCluster.getInstance().get().bucket(file.getUserData(VirtualFileKeys.BUCKET))
                        .scope(file.getUserData(VirtualFileKeys.SCOPE))
                        .collection(file.getUserData(VirtualFileKeys.COLLECTION));
                try {
                    GetResult getResult = collection.get(file.getUserData(VirtualFileKeys.ID));

                    if ("true".equals(file.getUserData(VirtualFileKeys.READ_ONLY))) {
                        return false;
                    }

                    // file type is json and is not valid
                    if (file.getFileType() == JsonFileType.INSTANCE) {
                        try {
                            JsonObject.fromJson(document.getText());
                        } catch (Exception e) {
                            return false;
                        }
                    }

                    //cas is null or different from the one in the server
                    if (file.getUserData(VirtualFileKeys.CAS) == null
                            || getResult.cas() != Long.parseLong(file.getUserData(VirtualFileKeys.CAS))) {

                        String[] options = {"Overwrite server's version", "Replace mine with the latest version from server", "Cancel"};
                        int result = Messages.showDialog(
                                "The document that you are trying to save has already been modified in the server. How would you like to proceed?", "Document Conflict"
                                , options, 1, Messages.getWarningIcon());

                        if (result == 0) {
                            saveFile(collection, file, document);
                            return true;
                        } else if (result == 1) {
                            file.putUserData(VirtualFileKeys.CAS, String.valueOf(getResult.cas()));
                            ApplicationManager.getApplication().runWriteAction(() -> {
                                document.setText(getResult.contentAsObject().toString());
                                DocumentFormatter.formatFile(getProject(file), file);
                            });

                            return false;
                        } else {
                            return false;
                        }
                    } else {
                        saveFile(collection, file, document);
                    }
                } catch (DocumentNotFoundException dnf) {
                    //this is a new document, so let's save it
                    saveFile(collection, file, document);
                }
            }
        }
        if (file.getUserData(VirtualFileKeys.CBL_CON_ID) != null
                && file.getUserData(VirtualFileKeys.CBL_CON_ID).equals(ActiveCBLDatabase.getInstance().getDatabaseId())) {

            try {

                com.couchbase.lite.Document doc = ActiveCBLDatabase.getInstance().getDatabase()
                        .getScope(file.getUserData(VirtualFileKeys.SCOPE))
                        .getCollection(file.getUserData(VirtualFileKeys.COLLECTION))
                        .getDocument(file.getUserData(VirtualFileKeys.ID));

                //this is a new document
                if (doc == null) {
                    saveCBLDocument(document, file);
                } else {


                    //cas is different from the one when you opened
                    if (!doc.getRevisionID().equals(file.getUserData(VirtualFileKeys.CAS))) {

                        String[] options = {"Make mine as the current version", "Replace mine with the latest", "Cancel"};
                        int result = Messages.showDialog(
                                "The document that you are trying to save has already been modified. How would you like to proceed?", "Document Conflict"
                                , options, 1, Messages.getWarningIcon());

                        if (result == 0) {
                            saveCBLDocument(document, file);
                            return true;
                        } else if (result == 1) {
                            file.putUserData(VirtualFileKeys.CAS, doc.getRevisionID());
                            ApplicationManager.getApplication().runWriteAction(() -> {

                                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                JsonElement jsonElement = JsonParser.parseString(Objects.requireNonNull(doc.toJSON()));
                                document.setText(gson.toJson(jsonElement));
                                DocumentFormatter.formatFile(getProject(file), file);
                            });
                            return false;
                        } else {
                            return false;
                        }
                    } else {
                        saveCBLDocument(document, file);
                    }
                }

            } catch (CouchbaseLiteException ex) {
                Log.error("Could not save the document", ex);
            }

        }
        return true;
    }

    private static void saveCBLDocument(@NotNull Document document, VirtualFile file) throws CouchbaseLiteException {


        try {
            JsonParser.parseString(document.getText());
        } catch (JsonSyntaxException e) {
            Messages.showErrorDialog("<html>The Document <strong>"
                    + file.getUserData(VirtualFileKeys.ID)
                    + "</strong> can't be saved as it is not a valid JSON</html>", "Couchbase Plugin Error");
            return;
        }

        MutableDocument newDoc = new MutableDocument(file.getUserData(VirtualFileKeys.ID));
        newDoc.setJSON(document.getText());

        ActiveCBLDatabase.getInstance().getDatabase()
                .getScope(file.getUserData(VirtualFileKeys.SCOPE))
                .getCollection(file.getUserData(VirtualFileKeys.COLLECTION))
                .save(newDoc);

        com.couchbase.lite.Document doc = ActiveCBLDatabase.getInstance().getDatabase()
                .getScope(file.getUserData(VirtualFileKeys.SCOPE))
                .getCollection(file.getUserData(VirtualFileKeys.COLLECTION))
                .getDocument(file.getUserData(VirtualFileKeys.ID));

        file.putUserData(VirtualFileKeys.CAS, String.valueOf(doc.getRevisionID()));
    }

    private void saveFile(Collection collection, VirtualFile file, Document document) {


        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(document.getText());
            MutationResult res = collection.upsert(file.getUserData(VirtualFileKeys.ID), node);
            file.putUserData(VirtualFileKeys.CAS, String.valueOf(res.cas()));
            InferHelper.invalidateInferCacheIfOlder(collection.bucketName(), collection.scopeName(), collection.name(), TimeUnit.MINUTES.toMillis(1));

        } catch (JsonProcessingException e) {
            Log.error("Document " + file.getUserData(VirtualFileKeys.ID) + " is not a valid json");
        }

    }
}
