package com.couchbase.intellij.tools;

import static utils.ProcessUtils.printOutput;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class CBMigrate {

    private CBMigrate() {
    }

    public static class CBMigrateCommandBuilder {

        private final List<String> commandList = new ArrayList<>();

        public CBMigrateCommandBuilder() {

            commandList.add("/Users/kaustavghosh/go/bin/cbmigrate");
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

    public static void migrateSingleCollection(Project project, CBMigrateCommandBuilder builder) {
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Migrating MongoDB to Couchbase", false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {

                indicator.setIndeterminate(true);
                indicator.setText("Migrating data from MongoDB to Couchbase");

                try {
                    ProcessBuilder processBuilder = new ProcessBuilder(builder.build());

                    Log.debug("Running command: " + String.join(" ", builder.commandList));

                    Process process = processBuilder.start();
                    printOutput(process, "Migrating MongoDB to Couchbase: ");

                    int exitCode = process.waitFor();
                    String collectionName = builder.commandList
                            .get(builder.commandList.indexOf("--mongodb-collection") + 1);
                    if (exitCode == 0) {
                        // Get the string after collection
                        Log.info("Successfully migrated MongoDB collection " + collectionName + " to Couchbase");
                        ApplicationManager.getApplication().invokeLater(() -> Messages.showInfoMessage(project,
                                "MongoDB collection " + collectionName + " migrated to Couchbase",
                                "Migration Successful"));
                    } else {
                        Log.error("An error occurred while migrating collection" + collectionName);
                        ApplicationManager.getApplication().invokeLater(() -> Messages.showErrorDialog(project,
                                "An error occurred while migrating collection " + collectionName, "Migration Failed"));
                    }

                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (Exception ex) {
                    Log.error("Error while migrating MongoDB to Couchbase", ex);
                    ApplicationManager.getApplication().invokeLater(() -> Messages.showErrorDialog(project,
                            "MongoDB to Couchbase migration failed", "Migration Failed"));
                }
            }
        });
    }

    public static void migrateMultipleCollections(Project project, List<CBMigrateCommandBuilder> builders) {

        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Migrating MongoDB to Couchbase", false) {
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
                        printOutput(process, "Migrating MongoDB to Couchbase: ");
                        int exitCode = process.waitFor();
                        if (exitCode == 0) {
                            Log.info("Successfully migrated MongoDB collection " + collectionName + " to Couchbase");
                            successfulMigrations.add(collectionName);
                        } else {
                            Log.error("An error occurred while migrating collection " + collectionName);
                            failedMigrations.add(collectionName);
                        }
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    } catch (Exception ex) {
                        Log.error("Error while migrating MongoDB to Couchbase", ex);
                        failedMigrations.add(collectionName);
                    }
                }

                ApplicationManager.getApplication().invokeLater(() -> {
                    if (!successfulMigrations.isEmpty()) {
                        Messages.showInfoMessage(project,
                                "Successfully migrated the following MongoDB collections to Couchbase: "
                                        + String.join(", ", successfulMigrations),
                                "Migration Successful");
                    }
                    if (!failedMigrations.isEmpty()) {
                        Messages.showErrorDialog(project,
                                "An error occurred while migrating the following collections: "
                                        + String.join(", ", failedMigrations),
                                "Migration Failed");
                    }
                });

            }
        });
    }

}
