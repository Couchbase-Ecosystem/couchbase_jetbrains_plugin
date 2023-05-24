package com.couchbase.intellij.listener;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.MutationResult;
import com.couchbase.intellij.DocumentFormatter;
import com.couchbase.intellij.VirtualFileKeys;
import com.couchbase.intellij.database.ActiveCluster;
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

public class JsonDocumentListener extends FileDocumentSynchronizationVetoer {

    @Override
    public boolean maySaveDocument(@NotNull Document document, boolean isSaveExplicit) {
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);

        //filtering files from our plugin
        if (file != null && file.equals(file) && file.getUserData(VirtualFileKeys.CLUSTER) != null) {
            if (!ActiveCluster.getInstance().getId().equals(file.getUserData(VirtualFileKeys.CONN_ID))) {
                Messages.showMessageDialog("Couchbase Plugin Error",
                        "The file that you are trying to save belongs to a connection that is no longer active"
                        , Messages.getErrorIcon());
                return false;
            } else {
                Collection collection = ActiveCluster.getInstance().get().bucket(file.getUserData(VirtualFileKeys.BUCKET))
                        .scope(file.getUserData(VirtualFileKeys.SCOPE))
                        .collection(file.getUserData(VirtualFileKeys.COLLECTION));
                try {
                    GetResult getResult = collection.get(file.getUserData(VirtualFileKeys.ID));

                    //cas is null or different from the one in the server
                    System.out.println(" cas = "+file.getUserData(VirtualFileKeys.CAS)+" == "+Long.parseLong(file.getUserData(VirtualFileKeys.CAS)));
                    if (file.getUserData(VirtualFileKeys.CAS) == null
                            || getResult.cas() != Long.parseLong(file.getUserData(VirtualFileKeys.CAS))) {

                        String[] options = {"Overwrite server's version", "Replace mine with the latest version from server", "Cancel"};
                        int result = Messages.showDialog("Document Conflict",
                                "The document that you are trying to save has already been modified in the server. How would you like to proceed?"
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
        return true;
    }

    private void saveFile(Collection collection, VirtualFile file, Document document) {
         MutationResult res = collection.upsert(file.getUserData(VirtualFileKeys.ID), JsonObject.fromJson(document.getText()));
         file.putUserData(VirtualFileKeys.CAS, String.valueOf(res.cas()));
    }

    private static Project getProject(VirtualFile virtualFile) {
        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        for (Project project : projects) {
            if (ProjectRootManager.getInstance(project).getFileIndex().isInContent(virtualFile)) {
                return project;
            }
        }
        throw new IllegalStateException("Could not find the project that the virtual file belongs to. This might be a bug.");
    }
}
