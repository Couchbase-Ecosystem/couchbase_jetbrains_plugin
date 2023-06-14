package com.couchbase.intellij.tools;

import com.couchbase.client.java.manager.collection.CollectionManager;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import utils.FileUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CBImport {

    public static void quickCollectionImport(String bucket, String scope, String collection, String filePath, Project project) {

        try {
            String lastLine = FileUtils.readLastLine(filePath);

            if (!lastLine.startsWith("//type:quickimport")) {
                Messages.showErrorDialog("The file selected was not exported using the Quick Export function."
                        + " Please import it using the Import/Export feature", "Quick Import Error");
                return;
            }

            String[] meta = lastLine.split(";");
            String metaScope = null;
            String metaCol = null;

            for (String m : meta) {
                if (m.startsWith("scope:")) {
                    metaScope = m.split(":")[1];
                } else if (m.startsWith("col:")) {
                    metaCol = m.split(":")[1];
                }
            }

            if (metaCol.contains(",")) {
                Messages.showErrorDialog("The file selected is an export of a scope and can't be imported inside a collection"
                        , "Quick Import Error");
                return;
            }


            if (!scope.equals(metaScope) || !collection.equals(metaCol)) {

                int result = Messages.showYesNoDialog("<html>The dataset was originally exported from collection <strong>'" + metaCol + "'</strong>" +
                                " and scope <strong>'" + metaScope + "'</strong>, but you are importing it into the collection <strong>'" + collection + "'</strong> " +
                                "and scope <strong>'" + scope + "'</strong>. Are you sure that you want to proceed?</html>",
                        "Quick Import", Messages.getQuestionIcon());

                if (result != Messages.YES) {
                    return;
                }
            } else {
                String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                int result = Messages.showYesNoDialog("<html>Are you sure that you would like to import the file <strong>"
                                + fileName + "</strong> into the collection <strong>'" + collection + "'</strong> " +
                                "of the scope <strong>'" + scope + "'</strong>?</html>",
                        "Quick Import", Messages.getQuestionIcon());

                if (result != Messages.YES) {
                    return;
                }
            }


            ProgressManager.getInstance().run(new Task.Backgroundable(project, "Importing '" + collection + "'", false) {
                public void run(@NotNull ProgressIndicator indicator) {
                    indicator.setIndeterminate(true);
                    try {
                        ProcessBuilder processBuilder = new ProcessBuilder(CBTools.getCbImport().getPath(),
                                "json", "--no-ssl-verify", "-c", ActiveCluster.getInstance().getClusterURL(),
                                "-u", ActiveCluster.getInstance().getUsername(), "-p", ActiveCluster.getInstance().getPassword(),
                                "-b", bucket,
                                "--format", "list",
                                "-d", "file://" + filePath,
                                "--scope-collection-exp", scope + "." + collection,
                                "-g", "%cbmid%",
                                "--ignore-fields", "cbmid,cbms,cbmc",
                                "-t", "4");
                        executeProcess(processBuilder);

                    } catch (Exception e) {
                        ApplicationManager.getApplication().invokeLater(() -> {
                            Messages.showErrorDialog("An error occurred while trying to import the dataset", "Quick Import Error");
                        });
                        Log.error(e);
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            Log.error(e);
            e.printStackTrace();
        }

    }


    public static void quickScopeImport(String bucket, String scope, String filePath, Project project) {

        try {
            String lastLine = FileUtils.readLastLine(filePath);

            if (!lastLine.startsWith("//type:quickimport")) {
                Messages.showErrorDialog("The file selected was not exported using the Quick Export function."
                        + " Please import it using the Import/Export feature", "Quick Import Error");
                return;
            }

            String[] meta = lastLine.split(";");
            String metaScope = null;
            String metaCol = null;

            for (String m : meta) {
                if (m.startsWith("scope:")) {
                    metaScope = m.split(":")[1];
                } else if (m.startsWith("col:")) {
                    metaCol = m.split(":")[1];
                }
            }

            Set<String> datasetCols = new HashSet<>(Arrays.asList(metaCol.split(",")));

            Set<String> currentCollections = ActiveCluster.getInstance().get().bucket(bucket)
                    .collections().getAllScopes().stream()
                    .filter(s -> s.name().equals(scope))
                    .flatMap(s -> s.collections().stream())
                    .map(cs -> cs.name())
                    .collect(Collectors.toSet());

            datasetCols.removeAll(currentCollections);

            if (!datasetCols.isEmpty()) {

                int result = Messages.showYesNoDialog("<html>This dataset contains " + datasetCols.size() + " collection(s) " +
                                "that don't exist in the scope that you are importing into. Would you like to also create them?</html>",
                        "Quick Import", Messages.getQuestionIcon());

                if (result == Messages.YES) {
                    CollectionManager collectionManager = ActiveCluster.getInstance().get().bucket(bucket).collections();
                    try {
                        for (String newCollection : datasetCols) {
                            Log.debug("Creating collection " + newCollection);
                            collectionManager.createCollection(CollectionSpec.create(newCollection, scope));
                        }
                    } catch (Exception e) {
                        Messages.showErrorDialog("An error occurred while trying to create the collections. Please try again", "Quick Import Error");
                        Log.error(e);
                        e.printStackTrace();
                    }
                }
            } else {
                String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                int result = Messages.showYesNoDialog("<html>Are you sure that you would like to import the file <strong>"
                                + fileName + "</strong> into the the scope <strong>'" + scope + "'</strong>?</html>",
                        "Quick Import", Messages.getQuestionIcon());

                if (result != Messages.YES) {
                    return;
                }
            }

            ProgressManager.getInstance().run(new Task.Backgroundable(project, "Importing '" + scope + "'", false) {
                public void run(@NotNull ProgressIndicator indicator) {
                    indicator.setIndeterminate(true);
                    try {
                        ProcessBuilder processBuilder = new ProcessBuilder(CBTools.getCbImport().getPath(),
                                "json", "--no-ssl-verify", "-c", ActiveCluster.getInstance().getClusterURL(),
                                "-u", ActiveCluster.getInstance().getUsername(), "-p", ActiveCluster.getInstance().getPassword(),
                                "-b", bucket,
                                "--format", "list",
                                "-d", "file://" + filePath,
                                "--scope-collection-exp", "%cbms%.%cbmc%",
                                "-g", "%cbmid%",
                                "--ignore-fields", "cbmid,cbms,cbmc",
                                "-t", "4");
                        executeProcess(processBuilder);

                    } catch (Exception e) {
                        ApplicationManager.getApplication().invokeLater(() -> {
                            Messages.showErrorDialog("An error occurred while trying to import the dataset", "Quick Import Error");
                        });
                        Log.error(e);
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            Log.error(e);
            e.printStackTrace();
        }

    }

    private static void executeProcess(ProcessBuilder processBuilder) throws Exception {
        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            ApplicationManager.getApplication().invokeLater(() -> {
                Messages.showErrorDialog("The error '" + exitCode + "' occurred while trying to import the dataset", "Quick Import Error");
            });
        } else {

            ApplicationManager.getApplication().invokeLater(() -> {
                Messages.showInfoMessage("Dataset imported successfully.", "Quick Import");
            });
        }
    }
}
