package com.couchbase.intellij.tree.iq.intents.actions;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.tools.CBExport;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileSaverDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import utils.TimeUtils;

import java.io.File;

public class ExportCollection implements ActionInterface {
    @Override
    public String fire(Project project, String bucketName, String scopeName, JsonObject intents, JsonObject intent) {
        JsonArray collections = intents.getArray("collections");
        if (collections == null || collections.isEmpty()) {
            return "ask the user which collection does he want to export and then return the response as updated JSON ";
        } else if (collections.size() > 1) {
            return "ask the user to specify only one collection and then return the response as updated JSON ";
        }

        String collection = collections.getString(0);

        if (bucketName == null || bucketName.isEmpty()) {
            if (scopeName == null || scopeName.isEmpty()) {
                return "ask the user in which scope and bucket the collection is located and then return the response as updated JSON ";
            }
            return "ask the user in which bucket the collection is located and then return the updated JSON ";
        }

        FileSaverDescriptor fsd = new FileSaverDescriptor("Simple Collection Export", "Choose where you want to save the file:");
        VirtualFileWrapper wrapper = FileChooserFactory.getInstance().createSaveFileDialog(fsd, project).save(("cb_export-" + scopeName + "_" + collection+ "-" + TimeUtils.getCurrentDateTime() + ".json"));
        if (wrapper != null) {
            File file = wrapper.getFile();
            CBExport.simpleCollectionExport(bucketName, scopeName, collection, file.getAbsolutePath(), null);
            return String.format("answer to the user that the export was successful and they can find exported data in the file located at '%s' ", file.getAbsolutePath());
        }
        return "let the user know that there was an unexpected problem with the export ";
    }
}
