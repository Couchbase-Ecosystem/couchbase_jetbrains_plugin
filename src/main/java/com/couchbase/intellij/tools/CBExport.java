package com.couchbase.intellij.tools;

import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.intellij.database.ActiveCluster;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

public class CBExport {

    public static void quickCollectionExport(String bucket, String scope, String collection, String filePath, Project project) {


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
                        Files.write(Paths.get(filePath), (System.lineSeparator() + metadata).getBytes(), StandardOpenOption.APPEND);

                        ApplicationManager.getApplication().invokeLater(() -> {
                            Messages.showInfoMessage("File saved successfully.", "Quick Export");
                        });
                    }

                } catch (Exception e) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        Messages.showErrorDialog("An error occurred while trying to export the dataset", "Quick Export Error");
                    });
                    e.printStackTrace();
                }

            }
        });
    }


    public static void quickScopeExport(String bucket, String scope, String filePath) {

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

                        List<CollectionSpec> collections = ActiveCluster.getInstance().get().bucket(bucket)
                                .collections().getAllScopes().stream()
                                .filter(s -> s.name().equals(scope))
                                .flatMap(s -> s.collections().stream())
                                .collect(Collectors.toList());

                        String metadata = "//type:quickimport;scope:" + scope + ";col:" + collections.stream()
                                .map(e -> e.name()).collect(Collectors.joining(","));
                        Files.write(Paths.get(filePath), (System.lineSeparator() + metadata).getBytes(), StandardOpenOption.APPEND);

                        ApplicationManager.getApplication().invokeLater(() -> {
                            Messages.showInfoMessage("File saved successfully.", "Quick Export");
                        });
                    }

                } catch (Exception e) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        Messages.showErrorDialog("An error occurred while trying to export the dataset", "Quick Export Error");
                    });
                    e.printStackTrace();
                }

            }
        });
    }
}