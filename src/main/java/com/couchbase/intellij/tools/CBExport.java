package com.couchbase.intellij.tools;

import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.query.QueryIndex;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.DataLoader;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import utils.IndexUtils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class CBExport {

    public static void quickCollectionExport(String bucket, String scope, String collection, String filePath, Project project) {

        final List<QueryIndex> indexes = DataLoader.listIndexes(bucket, scope, collection);

        boolean idx = false;
        if (!indexes.isEmpty()) {
            int result = Messages.showYesNoDialog("<html>The collection <strong>" + collection + "</strong> contains "
                            + (indexes.size() > 1 ? " indexes." : " index.")
                            + " Would you like to include"
                            + (indexes.size() > 1 ? " them" : " it")
                            + " in the exported file?</html>",
                    "Quick Export", Messages.getQuestionIcon());

            if (result == Messages.YES) {
                idx = true;
            }
        }

        final boolean includeIndexes = idx;
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Exporting '" + collection + "'", false) {
            public void run(@NotNull ProgressIndicator indicator) {
                // The progress indicator shows a moving bar by default. If you want to show progress, use:
                indicator.setIndeterminate(true);
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder(CBTools.getCbExport().getPath(),
                            "json", "--no-ssl-verify", "-c", ActiveCluster.getInstance().getClusterURL(),
                            "-u", ActiveCluster.getInstance().getUsername(), "-p", ActiveCluster.getInstance().getPassword(),
                            "-b", bucket,
                            "--include-data", scope + "." + collection,
                            "--scope-field", "cbms", "--collection-field", "cbmc", "--include-key",
                            "cbmid", "-f", "list", "-o", filePath, "-t", "4");
                    Process process = processBuilder.start();

                    int exitCode = process.waitFor();
                    if (exitCode != 0) {
                        ApplicationManager.getApplication().invokeLater(() -> {
                            Messages.showErrorDialog("The error " + exitCode + " occurred while trying to export the data", "Quick Export Error");
                        });
                    } else {

                        String metadata = "//type:quickimport;scope:" + scope + ";col:" + collection;
                        if (includeIndexes) {
                            metadata += ";idx:" + collection + "|" + getEncodedIndexes(indexes);
                        }
                        Files.write(Paths.get(filePath), (System.lineSeparator() + metadata).getBytes(), StandardOpenOption.APPEND);

                        ApplicationManager.getApplication().invokeLater(() -> {
                            Log.info("File " + filePath + " from collection " + collection + " was exported successfully");
                            Messages.showInfoMessage("File saved successfully.", "Quick Export");
                        });
                    }

                } catch (Exception e) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        Messages.showErrorDialog("An error occurred while trying to export the dataset", "Quick Export Error");
                    });
                    Log.error(e);
                    e.printStackTrace();
                }

            }
        });
    }


    public static void quickScopeExport(String bucket, String scope, String filePath) {

        final List<CollectionSpec> collections = ActiveCluster.getInstance().get().bucket(bucket)
                .collections().getAllScopes().stream()
                .filter(s -> s.name().equals(scope))
                .flatMap(s -> s.collections().stream())
                .collect(Collectors.toList());

        boolean idx = false;
        if (hasIndexes(bucket, scope, collections)) {
            int result = Messages.showYesNoDialog("<html>Would you like to also export the indexes of the scope <strong>" + scope + "</strong>?</html>",
                    "Quick Export", Messages.getQuestionIcon());

            if (result == Messages.YES) {
                idx = true;
            }
        }
        final boolean includeIndexes = idx;
        ProgressManager.getInstance().run(new Task.Backgroundable(null, "Exporting '" + scope + "'", false) {
            public void run(@NotNull ProgressIndicator indicator) {
                // The progress indicator shows a moving bar by default. If you want to show progress, use:
                indicator.setIndeterminate(true);
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder(CBTools.getCbExport().getPath(),
                            "json", "--no-ssl-verify", "-c", ActiveCluster.getInstance().getClusterURL(),
                            "-u", ActiveCluster.getInstance().getUsername(), "-p", ActiveCluster.getInstance().getPassword(),
                            "-b", bucket,
                            "--include-data", scope,
                            "--scope-field", "cbms", "--collection-field", "cbmc", "--include-key",
                            "cbmid", "-f", "list", "-o", filePath, "-t", "4");
                    Process process = processBuilder.start();

                    int exitCode = process.waitFor();
                    if (exitCode != 0) {
                        ApplicationManager.getApplication().invokeLater(() -> {
                            Messages.showErrorDialog("The error " + exitCode + " occurred while trying to export the scope", "Quick Export Error");
                        });
                    } else {

                        String indexes = "";
                        if (includeIndexes) {
                            indexes += ";idx:";
                            List<String> encodedIndexes = new ArrayList<>();
                            for (CollectionSpec spec : collections) {
                                List<QueryIndex> result = DataLoader.listIndexes(bucket, scope, spec.name());
                                encodedIndexes.add(spec.name() + "|" + getEncodedIndexes(result));
                            }
                            indexes += encodedIndexes.stream().collect(Collectors.joining("#"));
                        }

                        String metadata = "//type:quickimport;scope:" + scope + ";col:" + collections.stream()
                                .map(e -> e.name()).collect(Collectors.joining(",")) + indexes;
                        Files.write(Paths.get(filePath), (System.lineSeparator() + metadata).getBytes(), StandardOpenOption.APPEND);

                        ApplicationManager.getApplication().invokeLater(() -> {
                            Log.info("File " + filePath + " from scope " + scope + " was exported successfully");
                            Messages.showInfoMessage("File saved successfully.", "Quick Export");
                        });
                    }

                } catch (Exception e) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        Messages.showErrorDialog("An error occurred while trying to export the dataset", "Quick Export Error");
                    });
                    Log.error(e);
                    e.printStackTrace();
                }

            }
        });
    }

    private static boolean hasIndexes(String bucket, String scope, List<CollectionSpec> cols) {

        int tries = cols.size() < 20 ? cols.size() : 20;
        for (int i = 0; i < tries; i++) {
            List<QueryIndex> result = DataLoader.listIndexes(bucket, scope, cols.get(i).name());
            if (!result.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private static String getEncodedIndexes(List<QueryIndex> indexes) {
        String result = indexes.stream().map(IndexUtils::getIndexDefinition).collect(Collectors.joining("#"));
        return Base64.getEncoder().encodeToString(result.getBytes());
    }
}
