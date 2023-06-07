package com.couchbase.intellij.tools;

import com.couchbase.intellij.database.ActiveCluster;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import utils.FileUtils;

public class CBImport {

    public static void quickImport(String bucket, String scope, String collection, String filePath, Project project) {

        try {
            String lastLine = FileUtils.readLastLine(filePath);

            if (!lastLine.startsWith("//type:quickimport")) {
                Messages.showErrorDialog("The file selected was not exported using the Quick Export function."
                        + " Please import it using the Import/Export feature", "Couchbase Plugin Error");
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

                    } catch (Exception e) {
                        ApplicationManager.getApplication().invokeLater(() -> {
                            Messages.showErrorDialog("An error occurred while trying to import the dataset", "Quick Import Error");
                        });
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
