package com.couchbase.intellij.tools.cbmigrate;

import com.couchbase.intellij.tools.CBTools;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static utils.ProcessUtils.printOutput;

public class CBMigrateMongo {

    private CBMigrateMongo() {
    }

    public static class CBMigrateCommandBuilder {

        private final List<String> commandList = new ArrayList<>();

        public CBMigrateCommandBuilder() {

            commandList.add(CBTools.getTool(CBTools.Type.CBMIGRATE).getPath());
            commandList.add("mongo");
        }

        public CBMigrateCommandBuilder setMongoDBURI(String uri) {
            commandList.add("--mongodb-uri");
            commandList.add(uri);
            return this;
        }

        public CBMigrateCommandBuilder setMongoDBDatabase(String database) {
            commandList.add("--mongodb-database");
            commandList.add(database);
            return this;
        }

        public CBMigrateCommandBuilder setMongoDBCollection(String collection) {
            commandList.add("--mongodb-collection");
            commandList.add(collection);
            return this;
        }

        public CBMigrateCommandBuilder setCBCluster(String cluster) {
            commandList.add("--cb-cluster");
            commandList.add(cluster);
            return this;
        }

        public CBMigrateCommandBuilder setCBUsername(String username) {
            commandList.add("--cb-username");
            commandList.add(username);
            return this;
        }

        public CBMigrateCommandBuilder setCBPassword(String password) {
            commandList.add("--cb-password");
            commandList.add(password);
            return this;
        }

        public CBMigrateCommandBuilder setCBBucket(String bucket) {
            commandList.add("--cb-bucket");
            commandList.add(bucket);
            return this;
        }

        public CBMigrateCommandBuilder setCBScope(String scope) {
            commandList.add("--cb-scope");
            commandList.add(scope);
            return this;
        }

        public CBMigrateCommandBuilder setCBCollection(String collection) {
            commandList.add("--cb-collection");
            commandList.add(collection);
            return this;
        }

        public CBMigrateCommandBuilder setCBGenerateKey(String key) {
            commandList.add("--cb-generate-key");
            commandList.add(key);
            return this;
        }

        public CBMigrateCommandBuilder setVerbose() {
            commandList.add("--verbose");
            return this;
        }

        public List<String> build() {
            return commandList;
        }

    }

    public static void migrateMultipleCollections(Project project, List<CBMigrateCommandBuilder> builders) {

        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Migrating MongoDB to Couchbase", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {

                indicator.setIndeterminate(true);
                indicator.setText("Migrating data from MongoDB to Couchbase");

                List<String> successfulMigrations = new ArrayList<>();
                List<String> failedMigrations = new ArrayList<>();

                for (CBMigrateCommandBuilder builder : builders) {
                    String collectionName = builder.commandList
                            .get(builder.commandList.indexOf("--mongodb-collection") + 1);
                    try {
                        ProcessBuilder processBuilder = new ProcessBuilder(builder.build());
                        Log.debug("Running command: " + String.join(" ", builder.commandList));
                        Process process = processBuilder.start();
                        printOutput(process, "Migrating MongoDB's collection \"" + collectionName + "\" to Couchbase: ");
                        int exitCode = process.waitFor();
                        if (exitCode == 0) {
                            Log.info("Successfully migrated MongoDB collection " + collectionName + " to Couchbase");
                            successfulMigrations.add(collectionName);
                        } else {
                            Log.error("An error occurred while migrating collection " + collectionName);
                            failedMigrations.add(collectionName);
                        }
                    } catch (Exception ex) {
                        Log.error("Error while migrating MongoDB to Couchbase", ex);
                        failedMigrations.add(collectionName);
                    }
                }

                ApplicationManager.getApplication().invokeLater(() -> {
                    if (failedMigrations.isEmpty()) {
                        Messages.showInfoMessage(project,
                                "Successfully migrated all MongoDB collections to Couchbase",
                                "Migration Successful");
                    } else {
                        if (!failedMigrations.isEmpty()) {
                            Messages.showErrorDialog(project,
                                    "An error occurred while migrating " + failedMigrations.size() + " of " + successfulMigrations.size() + " collections.\n"
                                            + "The failure(s) occurred while migrating the following collections: " + String.join(", ", failedMigrations) + ".\nPlease check the log for more.",
                                    "Migration Failed");
                        }
                    }
                });

            }
        });
    }

}
