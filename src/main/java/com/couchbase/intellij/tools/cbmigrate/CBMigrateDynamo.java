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

public class CBMigrateDynamo {

    private CBMigrateDynamo() {
    }

    public static void migrateMultipleCollections(Project project, List<CBMigrateDynamoCommandBuilder> builders) {

        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Migrating DynamoDB to Couchbase", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {

                indicator.setIndeterminate(true);
                indicator.setText("Migrating data from DynamoDB to Couchbase");

                List<String> successfulMigrations = new ArrayList<>();
                List<String> failedMigrations = new ArrayList<>();

                for (CBMigrateDynamoCommandBuilder builder : builders) {
                    String collectionName = builder.commandList.get(builder.commandList.indexOf("--dynamodb-table" + "-name") + 1);
                    try {
                        ProcessBuilder processBuilder = new ProcessBuilder(builder.build());
                        Log.debug("Running command: " + String.join(" ", builder.commandList));
                        Process process = processBuilder.start();
                        printOutput(process, "Migrating DynamoDB's table \"" + collectionName + "\" to " + "Couchbase" + ":" + " ");
                        int exitCode = process.waitFor();
                        if (exitCode == 0) {
                            Log.info("Successfully migrated DynamoDB table " + collectionName + " to Couchbase");
                            successfulMigrations.add(collectionName);
                        } else {
                            Log.error("An error occurred while migrating collection " + collectionName);
                            failedMigrations.add(collectionName);
                        }
                    } catch (Exception ex) {
                        Log.error("Error while migrating DynamoDB to Couchbase", ex);
                        failedMigrations.add(collectionName);
                    }
                }

                ApplicationManager.getApplication().invokeLater(() -> {
                    if (failedMigrations.isEmpty()) {
                        Messages.showInfoMessage(project, "Successfully migrated all Dynamo collections to " + "Couchbase", "Migration Successful");
                    } else {
                        if (!failedMigrations.isEmpty()) {
                            Messages.showErrorDialog(project, "An error occurred while migrating " + failedMigrations.size() + " of " + successfulMigrations.size() + " collections.\n" + "The failure(s) occurred while migrating the following collections: " + String.join(", ", failedMigrations) + ".\nPlease check the log for more.", "Migration Failed");
                        }
                    }
                });

            }
        });
    }

    public static class CBMigrateDynamoCommandBuilder {

        private final List<String> commandList = new ArrayList<>();

        public CBMigrateDynamoCommandBuilder() {

            commandList.add(CBTools.getTool(CBTools.Type.CBMIGRATE).getPath());
            commandList.add("dynamodb");
        }

        public CBMigrateDynamoCommandBuilder setAwsProfile(String profile) {
            commandList.add("--aws-profile");
            commandList.add(profile);
            return this;
        }

        public CBMigrateDynamoCommandBuilder setAwsRegion(String region) {
            commandList.add("--aws-region");
            commandList.add(region);
            return this;
        }

        public CBMigrateDynamoCommandBuilder setDynamoTable(String table) {
            commandList.add("--dynamodb-table-name");
            commandList.add(table);
            return this;
        }

        public CBMigrateDynamoCommandBuilder setAwsAccessKey(String accessKey) {
            commandList.add("--aws-access-key-id");
            commandList.add(accessKey);
            return this;
        }

        public CBMigrateDynamoCommandBuilder setAwsAccessKeySecret(String accessKeySecret) {
            commandList.add("--aws-secret-access-key");
            commandList.add(accessKeySecret);
            return this;
        }

        public CBMigrateDynamoCommandBuilder setCBCluster(String cluster) {
            commandList.add("--cb-cluster");
            commandList.add(cluster);
            return this;
        }

        public CBMigrateDynamoCommandBuilder setCBUsername(String username) {
            commandList.add("--cb-username");
            commandList.add(username);
            return this;
        }

        public CBMigrateDynamoCommandBuilder setCBPassword(String password) {
            commandList.add("--cb-password");
            commandList.add(password);
            return this;
        }

        public CBMigrateDynamoCommandBuilder setCBBucket(String bucket) {
            commandList.add("--cb-bucket");
            commandList.add(bucket);
            return this;
        }

        public CBMigrateDynamoCommandBuilder setCBScope(String scope) {
            commandList.add("--cb-scope");
            commandList.add(scope);
            return this;
        }

        public CBMigrateDynamoCommandBuilder setCBCollection(String collection) {
            commandList.add("--cb-collection");
            commandList.add(collection);
            return this;
        }

        public CBMigrateDynamoCommandBuilder setCBGenerateKey(String key) {
            commandList.add("--cb-generate-key");
            commandList.add(key);
            return this;
        }

        public CBMigrateDynamoCommandBuilder setCopyIndexes(String key) {
            commandList.add("--copy-indexes");
            commandList.add(key);
            return this;
        }

        public CBMigrateDynamoCommandBuilder setVerbose() {
            commandList.add("--debug");
            return this;
        }

        public CBMigrateDynamoCommandBuilder setSslNoVerify() {
            commandList.add("--cb-no-ssl-verify");
            commandList.add("true");
            return this;
        }

        public List<String> build() {
            return commandList;
        }

    }

}
