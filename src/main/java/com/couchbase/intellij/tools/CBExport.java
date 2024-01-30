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
import utils.TimeUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import static utils.ProcessUtils.printOutput;

public class CBExport {

    public static void simpleCollectionExport(String bucket, String scope, String collection, String filePath, Project project) {

        final List<QueryIndex> indexes = DataLoader.listIndexes(bucket, scope, collection);

        boolean idx = false;
        if (!indexes.isEmpty()) {
            int result = Messages.showYesNoDialog("<html>The collection <strong>" + collection + "</strong> contains "
                            + (indexes.size() > 1 ? " indexes." : " index.")
                            + " Would you like to include"
                            + (indexes.size() > 1 ? " them" : " it")
                            + " in the exported file?</html>",
                    "Simple Export", Messages.getQuestionIcon());

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
                    ProcessBuilder processBuilder = new ProcessBuilder(CBTools.getTool(CBTools.Type.CB_EXPORT).getPath(),
                            "json", "--no-ssl-verify", "-c", ActiveCluster.getInstance().getClusterURL(),
                            "-u", ActiveCluster.getInstance().getUsername(), "-p", ActiveCluster.getInstance().getPassword(),
                            "-b", bucket,
                            "--include-data", scope + "." + collection,
                            "--scope-field", "cbms", "--collection-field", "cbmc", "--include-key",
                            "cbmid", "-f", "list", "-o", filePath, "-t", "4");
                    Process process = processBuilder.start();
                    printOutput(process, "Output from cbexport:");

                    int exitCode = process.waitFor();
                    if (exitCode != 0) {
                        ApplicationManager.getApplication().invokeLater(() -> Messages.showErrorDialog("The error " + exitCode + " occurred while trying to export the data", "Simple Export Error"));
                    } else {

                        String metadata = "//type:simpleimport;bucket:" + bucket + ";scope:" + scope + ";col:" + collection;
                        if (includeIndexes) {
                            metadata += ";idx:" + collection + "|" + getEncodedIndexes(indexes);
                        }
                        Files.write(Paths.get(filePath), (System.lineSeparator() + metadata).getBytes(), StandardOpenOption.APPEND);

                        ApplicationManager.getApplication().invokeLater(() -> {
                            Log.info("File " + filePath + " from collection " + collection + " was exported successfully");
                            Messages.showInfoMessage("File saved successfully.", "Simple Export");
                        });
                    }

                } catch (Exception e) {
                    ApplicationManager.getApplication().invokeLater(() -> Messages.showErrorDialog("An error occurred while trying to export the dataset", "Simple Export Error"));
                    Log.error(e);
                    e.printStackTrace();
                }

            }
        });
    }


    public static void simpleScopeExport(String bucket, String scope, String filePath) {

        final List<CollectionSpec> collections = ActiveCluster.getInstance().get().bucket(bucket)
                .collections().getAllScopes().stream()
                .filter(s -> s.name().equals(scope))
                .flatMap(s -> s.collections().stream())
                .collect(Collectors.toList());

        boolean idx = false;
        if (ActiveCluster.getInstance().hasQueryService() && hasIndexes(bucket, scope, collections)) {
            int result = Messages.showYesNoDialog("<html>Would you like to also export the indexes of the scope <strong>" + scope + "</strong>?</html>",
                    "Simple Export", Messages.getQuestionIcon());

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
                    ProcessBuilder processBuilder = new ProcessBuilder(CBTools.getTool(CBTools.Type.CB_EXPORT).getPath(),
                            "json", "--no-ssl-verify", "-c", ActiveCluster.getInstance().getClusterURL(),
                            "-u", ActiveCluster.getInstance().getUsername(), "-p", ActiveCluster.getInstance().getPassword(),
                            "-b", bucket,
                            "--include-data", scope,
                            "--scope-field", "cbms", "--collection-field", "cbmc", "--include-key",
                            "cbmid", "-f", "list", "-o", filePath, "-t", "4");
                    Process process = processBuilder.start();
                    printOutput(process, "Output from cbexport:");

                    int exitCode = process.waitFor();
                    if (exitCode != 0) {
                        ApplicationManager.getApplication().invokeLater(() -> Messages.showErrorDialog("The error " + exitCode + " occurred while trying to export the scope", "Simple Export Error"));
                    } else {

                        String indexes = "";
                        if (includeIndexes) {
                            indexes += ";idx:";
                            List<String> encodedIndexes = new ArrayList<>();
                            for (CollectionSpec spec : collections) {
                                List<QueryIndex> result = DataLoader.listIndexes(bucket, scope, spec.name());
                                encodedIndexes.add(spec.name() + "|" + getEncodedIndexes(result));
                            }
                            indexes += String.join("#", encodedIndexes);
                        }

                        String metadata = "//type:simpleimport;bucket:" + bucket + ";scope:" + scope + ";col:" + collections.stream()
                                .map(CollectionSpec::name).collect(Collectors.joining(",")) + indexes;
                        Files.write(Paths.get(filePath), (System.lineSeparator() + metadata).getBytes(), StandardOpenOption.APPEND);

                        ApplicationManager.getApplication().invokeLater(() -> {
                            Log.info("File " + filePath + " from scope " + scope + " was exported successfully");
                            Messages.showInfoMessage("File saved successfully.", "Simple Export");
                        });
                    }

                } catch (Exception e) {
                    ApplicationManager.getApplication().invokeLater(() -> Messages.showErrorDialog("An error occurred while trying to export the dataset", "Simple Export Error"));
                    Log.error(e);
                    e.printStackTrace();
                }

            }
        });
    }

    public static void export(String bucket, List<String> scopes, List<String> cols, String filePath, String keyName,
                              String scopeName, String colName, String format, String threads, boolean verbose) {


        boolean allScopes = scopes.contains("All Scopes");
        List<String> scp = new ArrayList<>();
        List<String> collections = new ArrayList<>();
        if (!allScopes) {
            //add just the "All collections of xxx"
            for (String col : cols) {
                if (col.contains(" ")) {
                    scp.add(col.split(" ")[3]);
                }
            }

            for (String col : cols) {
                if (!col.contains(" ") && !hasAnyScope(col, scp)) {
                    collections.add(col);
                }
            }
        }

        scp.addAll(collections);

        String fileName = bucket + "_cbexport_" + TimeUtils.getCurrentDateTime() + ".json";
        String path;
        if (filePath.endsWith(File.separator)) {
            path = filePath + fileName;
        } else {
            path = filePath + File.separator + fileName;
        }
        String includeData = String.join(",", scp);
        final String fullPath = path;


        ProgressManager.getInstance().run(new Task.Backgroundable(null, "Exporting '" + bucket + "'", true) {
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);
                try {
                    List<String> cmd = new ArrayList<>();
                    cmd.add(CBTools.getTool(CBTools.Type.CB_EXPORT).getPath());
                    cmd.add("json");
                    cmd.add("--no-ssl-verify");
                    cmd.add("-c");
                    cmd.add(ActiveCluster.getInstance().getClusterURL());
                    cmd.add("-u");
                    cmd.add(ActiveCluster.getInstance().getUsername());
                    cmd.add("-p");
                    cmd.add(ActiveCluster.getInstance().getPassword());
                    cmd.add("-b");
                    cmd.add(bucket);
                    if (!allScopes) {
                        cmd.add("--include-data");
                        cmd.add(includeData);
                    }
                    cmd.add("--scope-field");
                    cmd.add(scopeName);
                    cmd.add("--collection-field");
                    cmd.add(colName);
                    cmd.add("--include-key");
                    cmd.add(keyName);
                    cmd.add("-f");
                    cmd.add(format);
                    cmd.add("-o");
                    cmd.add(fullPath);
                    cmd.add("-t");
                    cmd.add(threads);
                    if (verbose) {
                        cmd.add("-v");
                    }
                    ProcessBuilder processBuilder = new ProcessBuilder(cmd);

                    if (Log.isDebug()) {
                        Log.debug(String.join(" ", (String.join(" ", processBuilder.command()).replace(
                                ActiveCluster.getInstance().getPassword(), "********"))));
                    }

                    Process process = processBuilder.start();
                    printOutput(process, "Output from cbexport:");

                    int exitCode = process.waitFor();
                    if (exitCode != 0) {
                        ApplicationManager.getApplication().invokeLater(() -> Messages.showErrorDialog("The error " + exitCode + " occurred while trying to export the scope", "Data Export Error"));
                    } else {

                        ApplicationManager.getApplication().invokeLater(() -> {
                            Log.info("File " + fileName + " was exported successfully");
                            Messages.showInfoMessage("File " + fullPath + " saved successfully.", "Data Export");
                        });
                    }

                } catch (Exception e) {
                    ApplicationManager.getApplication().invokeLater(() -> Messages.showErrorDialog("An error occurred while trying to export the dataset", "Data Export Error"));
                    Log.error(e);
                    e.printStackTrace();
                }

            }
        });

    }

    private static boolean hasIndexes(String bucket, String scope, List<CollectionSpec> cols) {

        int tries = Math.min(cols.size(), 20);
        for (int i = 0; i < tries; i++) {
            List<QueryIndex> result = DataLoader.listIndexes(bucket, scope, cols.get(i).name());
            if (!result.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private static String getEncodedIndexes(List<QueryIndex> indexes) {
        String result = indexes.stream().map(e -> IndexUtils.getIndexDefinition(e, false)).collect(Collectors.joining("#"));
        return Base64.getEncoder().encodeToString(result.getBytes());
    }

    private static boolean hasAnyScope(String collection, List<String> scopes) {
        for (String scope : scopes) {
            if (collection.startsWith(scope + ".")) {
                return true;
            }
        }
        return false;
    }
}
