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
        JsonObject arguments = intent.getObject("arguments");
        if (collections == null || collections.isEmpty()) {
            if (arguments.containsKey("collection")) {
                if (arguments.get("collection") instanceof String) {
                    collections = JsonArray.create();
                    collections.add(arguments.getString("collection"));
                } else {
                    collections = arguments.getArray("collection");
                }
            } else if (arguments.containsKey("collections")) {
                collections = arguments.getArray("collections");
            }
            if (collections == null || collections.isEmpty()) {
                return "ask the user which one collection does he want to export and then return the response as updated JSON ";
            }
        }
        if (collections.size() > 1) {
            return "Let the user know that only one collection can be exported at a time; ask the user to specify only one collection, update the JSON with collection they choose and then respond back with the updated JSON ";
        }

        String collection = collections.getString(0);
        if (collection.contains(".")) {
            String[] fqname = collection.split("\\.");
            if (fqname.length == 3) {
                bucketName = fqname[0];
                scopeName = fqname[1];
                collection = fqname[2];
            }
        }

        if (arguments.containsKey("bucket")) {
            bucketName = arguments.getString("bucket");
        } else if (arguments.containsKey("bucketName")) {
            bucketName = arguments.getString("bucketName");
        }

        if (arguments.containsKey("scope")) {
            scopeName = arguments.getString("scope");
        } else if (arguments.containsKey("scopeName")) {
            scopeName = arguments.getString("scopeName");
        }

        if (bucketName == null || bucketName.trim().isEmpty()) {
            if (scopeName == null || scopeName.trim().isEmpty()) {
                return String.format("ask the user in which scope and bucket the '%s' collection is located and then return the response as updated JSON. ", collection);
            }
            return String.format("ask the user in which bucket the '%s' collection is located and then return the updated JSON. ", collection);
        } else if (scopeName == null || scopeName.trim().isEmpty()) {
            return String.format("ask the user in which scope the '%s' collection is located and then return the updated JSON. ", collection);
        }

        FileSaverDescriptor fsd = new FileSaverDescriptor("Simple Collection Export", "Choose where you want to save the file:");
        VirtualFileWrapper wrapper = FileChooserFactory.getInstance().createSaveFileDialog(fsd, project).save(("cb_export-" + scopeName + "_" + collection+ "-" + TimeUtils.getCurrentDateTime() + ".json"));
        if (wrapper != null) {
            File file = wrapper.getFile();
            CBExport.simpleCollectionExport(bucketName, scopeName, collection, file.getAbsolutePath(), null);
            return String.format("answer to the user that the export of collection '%s' was successful and they can find exported data in the file located at '%s' ", collection, file.getAbsolutePath());
        }
        return String.format("let the user know that there was an unexpected problem while exporting collection '%s'. ", collection);
    }
}
