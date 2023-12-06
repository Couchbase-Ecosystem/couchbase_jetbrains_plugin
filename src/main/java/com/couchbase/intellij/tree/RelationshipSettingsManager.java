package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.persistence.storage.RelationshipStorage;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileSaverDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class RelationshipSettingsManager {


    public static void showExportDialog(Project project) {
        FileSaverDescriptor fsd = new FileSaverDescriptor("Save Relationship File", "Choose where you want to save the file:");
        VirtualFileWrapper wrapper = FileChooserFactory.getInstance().createSaveFileDialog(fsd, project)
                .save((ActiveCluster.getInstance().getId()) + ".properties");

        Map<String, String> relationships = RelationshipStorage.getInstance().getValue().getRelationships()
                .get(ActiveCluster.getInstance().getId());

        if (relationships == null) {
            Messages.showInfoMessage("There are no relationship mappings for this cluster yet", "File Saved");
            return;
        }

        StringBuilder contentBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : relationships.entrySet()) {
            contentBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        String content = contentBuilder.toString();

        if (wrapper != null) {
            File file = wrapper.getFile();
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(content);
                Messages.showInfoMessage("File exported successfully.", "File Saved");
            } catch (Exception e) {
                Log.error(e);
                Messages.showErrorDialog(e.getMessage(), "An error occurred while exporting the relationships");
            }
        }
    }

    public static void showImportDialog(Project project, Tree tree) {
        FileChooserDescriptor fcd = new FileChooserDescriptor(true, false, false, false, false, false) {
            @Override
            public boolean isFileSelectable(VirtualFile file) {
                return file != null && !file.isDirectory() && "properties".equals(file.getExtension());
            }
        };
        fcd.setTitle("Select Relationship File");
        fcd.setDescription("Choose a .properties file:");

        VirtualFile virtualFile = FileChooser.chooseFile(fcd, project, null);

        if (virtualFile == null) {
            return;
        }

        Map<String, String> relationships = new HashMap<>();
        try (FileInputStream fileInputStream = new FileInputStream(virtualFile.getPath())) {
            Properties properties = new Properties();
            properties.load(fileInputStream);

            for (String key : properties.stringPropertyNames()) {
                relationships.put(key, properties.getProperty(key));
            }
        } catch (IOException e) {
            Log.error(e);
        }

        RelationshipStorage.getInstance().getValue()
                .getRelationships().put(ActiveCluster.getInstance().getId(), relationships);

        collapseTree(tree);
    }

    private static void collapseTree(Tree tree) {
        if (tree != null) {
            TreeModel model = tree.getModel();
            Object root = model.getRoot();

            // Check if root has children
            int childCount = model.getChildCount(root);
            for (int i = 0; i < childCount; i++) {
                Object child = model.getChild(root, i);
                TreePath childPath = new TreePath(new Object[]{root, child});
                tree.collapsePath(childPath);
            }
        }
    }

    public static void populateTravelSample() {

        Map<String, String> map = RelationshipStorage.getInstance().getValue()
                .getRelationships().computeIfAbsent(ActiveCluster.getInstance().getId(), k -> new HashMap<>());

        map.put("travel-sample.inventory.route.airlineid", "travel-sample.inventory.airline.meta().id");
        map.put("travel-sample.inventory.route.airline", "travel-sample.inventory.airline.iata");
        map.put("travel-sample.inventory.route.sourceairport", "travel-sample.inventory.airport.faa");
        map.put("travel-sample.inventory.route.destinationairport", "travel-sample.inventory.airport.faa");
    }
}
