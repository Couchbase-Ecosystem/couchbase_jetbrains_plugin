package com.couchbase.intellij.tools;

import com.couchbase.client.core.error.IndexExistsException;
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

import java.util.*;
import java.util.stream.Collectors;

import static utils.ProcessUtils.printOutput;

public class CBImport {

    public static void simpleCollectionImport(String bucket, String scope, String collection, String filePath, Project project) {

        try {
            String lastLine = FileUtils.readLastLine(filePath);

            if (!lastLine.startsWith("//type:simpleimport")) {
                Messages.showErrorDialog("The file selected was not exported using the Simple Export function." + " Please import it using the Import/Export feature", "Simple Import Error");
                return;
            }

            ExportedMetadata meta = partLastLine(lastLine);
            String originalCol = meta.getCollections().get(0);


            if (meta.getCollections().size() > 1) {
                Messages.showErrorDialog("The file selected is an export of a scope and can't be imported inside a collection", "Simple Import Error");
                return;
            }

            if (!scope.equals(meta.getScope()) || !collection.equals(meta.getCollections().get(0))) {

                int result = Messages.showYesNoDialog("<html>The dataset was originally exported from collection <strong>'" + originalCol + "'</strong>" + " and scope <strong>'" + meta.getScope() + "'</strong>, but you are importing it into the collection <strong>'" + collection + "'</strong> " + "and scope <strong>'" + scope + "'</strong>. Are you sure that you want to proceed?" +
                        "<br><br><small>Keys that already exists in the database will be overwritten by the ones in the dataset</small></html>", "Simple Import", Messages.getQuestionIcon());

                if (result != Messages.YES) {
                    return;
                }
            } else {
                String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                int result = Messages.showYesNoDialog("<html>Are you sure that you would like to import the file <strong>" + fileName + "</strong> into the collection <strong>'" + collection + "'</strong> " + "of the scope <strong>'" + scope + "'</strong>?" +
                        "<br><br><small>Keys that already exists in the database will be overwritten by the ones in the dataset</small></html>", "Simple Import", Messages.getQuestionIcon());

                if (result != Messages.YES) {
                    return;
                }
            }

            final boolean createIndexes = shouldImportIndexes(meta);

            ProgressManager.getInstance().run(new Task.Backgroundable(project, "Importing '" + collection + "'", false) {
                public void run(@NotNull ProgressIndicator indicator) {
                    indicator.setIndeterminate(true);
                    try {
                        ProcessBuilder processBuilder = new ProcessBuilder(CBTools.getTool(CBTools.Type.CB_IMPORT).getPath(), "json", "--no-ssl-verify", "-c", ActiveCluster.getInstance().getClusterURL(), "-u", ActiveCluster.getInstance().getUsername(), "-p", ActiveCluster.getInstance().getPassword(), "-b", bucket, "--format", "list", "-d", "file://" + filePath, "--scope-collection-exp", scope + "." + collection, "-g", "%cbmid%", "--ignore-fields", "cbmid,cbms,cbmc", "-t", "4");

                        if (createIndexes) {
                            createIndexes(meta, bucket, scope, originalCol, collection);
                        }
                        executeProcess(processBuilder);
                    } catch (Exception e) {
                        ApplicationManager.getApplication().invokeLater(() -> Messages.showErrorDialog("An error occurred while trying to import the dataset", "Simple Import Error"));
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


    public static void simpleScopeImport(String bucket, String scope, String filePath, Project project) {

        try {
            String lastLine = FileUtils.readLastLine(filePath);

            if (!lastLine.startsWith("//type:simpleimport")) {
                Messages.showErrorDialog("The file selected was not exported using the Simple Export function." + " Please import it using the Import/Export feature", "Simple Import Error");
                return;
            }

            ExportedMetadata meta = partLastLine(lastLine);
            Set<String> datasetCols = new HashSet<>(meta.getCollections());
            Set<String> currentCollections = ActiveCluster.getInstance().get().bucket(bucket).collections().getAllScopes().stream().filter(s -> s.name().equals(scope)).flatMap(s -> s.collections().stream()).map(CollectionSpec::name).collect(Collectors.toSet());

            if (datasetCols.contains("_default") && !currentCollections.contains("_default")) {
                Messages.showErrorDialog("You can't import a dataset that contains the _default collection into another one that doesn't contains it using Simple Export. Please use the Import/Export instead.", "Simple Import Error");
                return;
            }

            datasetCols.removeAll(currentCollections);

            boolean skipCols = false;
            if (!datasetCols.isEmpty()) {

                int result = Messages.showYesNoDialog("<html>This dataset contains " + datasetCols.size() + " collection(s) " + "that don't exist in the scope that you are importing into. Would you like to also create them?</html>", "Simple Import", Messages.getQuestionIcon());

                if (result == Messages.YES) {
                    CollectionManager collectionManager = ActiveCluster.getInstance().get().bucket(bucket).collections();
                    try {
                        for (String newCollection : datasetCols) {
                            collectionManager.createCollection(CollectionSpec.create(newCollection, scope));
                            Log.info("Collection " + newCollection + " was created successfully");
                        }
                    } catch (Exception e) {
                        Messages.showErrorDialog("An error occurred while trying to create the collections. Please try again", "Simple Import Error");
                        Log.error("An error occurred while creating a collection ", e);
                        e.printStackTrace();
                    }
                } else {
                    skipCols = true;
                }
            } else {
                String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                int result = Messages.showYesNoDialog("<html>Are you sure that you would like to import the file <strong>" + fileName + "</strong> into the the scope <strong>'" + scope + "'</strong>?" +
                        "<br><br><small>Keys that already exists in the database will be overwritten by the ones in the dataset</small></html>", "Simple Import", Messages.getQuestionIcon());

                if (result != Messages.YES) {
                    return;
                }
            }

            final boolean createIndexes = shouldImportIndexes(meta);
            final Set<String> skippedCollections = skipCols ? datasetCols : new HashSet<>();

            ProgressManager.getInstance().run(new Task.Backgroundable(project, "Importing '" + scope + "'", false) {
                public void run(@NotNull ProgressIndicator indicator) {
                    indicator.setIndeterminate(true);
                    try {
                        ProcessBuilder processBuilder = new ProcessBuilder(CBTools.getTool(CBTools.Type.CB_IMPORT).getPath(), "json", "--no-ssl-verify", "-c", ActiveCluster.getInstance().getClusterURL(), "-u", ActiveCluster.getInstance().getUsername(), "-p", ActiveCluster.getInstance().getPassword(), "-b", bucket, "--format", "list", "-d", "file://" + filePath, "--scope-collection-exp", scope + ".%cbmc%", "-g", "%cbmid%", "--ignore-fields", "cbmid,cbms,cbmc", "-t", "4");

                        if (createIndexes) {
                            createIndexes(meta, bucket, scope, skippedCollections);
                        }
                        executeProcess(processBuilder);

                    } catch (Exception e) {
                        ApplicationManager.getApplication().invokeLater(() -> Messages.showErrorDialog("An error occurred while trying to import the dataset", "Simple Import Error"));
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

    private static boolean shouldImportIndexes(ExportedMetadata meta) {
        boolean idx = false;
        if (!meta.getIndexes().isEmpty()) {
            int result = Messages.showYesNoDialog("<html>This dataset contain indexes. Would you like to also create them?" + "<br><small>If the indexes already exist in your environment, they won't be recreated.</small></html>", "Simple Import", Messages.getQuestionIcon());

            if (result == Messages.YES) {
                idx = true;
            }
        }
        return idx;
    }

    private static void createIndexes(ExportedMetadata meta, String bucket, String newScope, Set<String> skipCols) {
        for (Map.Entry<String, String> entry : meta.getIndexes().entrySet()) {

            if (skipCols.contains(entry.getKey())) {
                continue;
            }
            createIndexes(meta, bucket, newScope, entry.getKey(), entry.getKey());
        }
    }

    private static void createIndexes(ExportedMetadata meta, String bucket, String scope, String originalCol, String newCol) {

        String indexes = meta.getIndexes().get(originalCol);

        byte[] decodedBytes = Base64.getDecoder().decode(indexes);
        String decodedString = new String(decodedBytes);
        //adjustment if the primary index is unnamed.
        if (decodedString.contains("#primary") || decodedString.contains("#PRIMARY")) {
            decodedString = decodedString.replace("`#primary`", "").replace("`#PRIMARY`", "");
        }
        String[] idxArray = decodedString.split("#");

        String colPath = "`" + meta.bucket + "`.`" + meta.scope + "`.`" + originalCol + "`";
        String bucketPath = "`" + meta.bucket + "`";

        for (String s : idxArray) {
            if (s.contains(colPath)) {
                s = s.replace(colPath, "`" + bucket + "`.`" + scope + "`.`" + newCol + "`");
            } else if (s.contains(bucketPath)) {
                s = s.replace(bucketPath, "`" + bucket + "`.`" + scope + "`.`" + newCol + "`");
            }

            try {
                ActiveCluster.getInstance().get().bucket(bucket).scope(scope).query(s);
                Log.info("Index " + s + " created successfully.");
            } catch (IndexExistsException ex) {
                Log.info("Index " + s + " already exists.");
            } catch (Exception e) {
                Log.error("Could not create index " + s, e);
            }
        }

    }

    private static void executeProcess(ProcessBuilder processBuilder) throws Exception {
        Process process = processBuilder.start();
        printOutput(process, "Output from cbimport:");

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            ApplicationManager.getApplication().invokeLater(() -> Messages.showErrorDialog("The error '" + exitCode + "' occurred while trying to import the dataset", "Simple Import Error"));
        } else {

            ApplicationManager.getApplication().invokeLater(() -> Messages.showInfoMessage("Dataset imported successfully.", "Simple Import"));
        }
    }

    private static ExportedMetadata partLastLine(String lastLine) {
        String[] meta = lastLine.split(";");
        List<String> metaCol = new ArrayList<>();
        String metaScope = null;
        String metaBucket = null;
        Map<String, String> indexes = new HashMap<>();

        for (String m : meta) {
            if (m.startsWith("scope:")) {
                metaScope = m.split(":")[1];
            } else if (m.startsWith("bucket:")) {
                metaBucket = m.split(":")[1];
            } else if (m.startsWith("col:")) {
                String colSplit = m.split(":")[1];
                if (colSplit.contains(",")) {
                    metaCol.addAll(Arrays.asList(colSplit.split(",")));
                } else {
                    metaCol.add(colSplit);
                }
            } else if (m.startsWith("idx:")) {
                String content = m.split(":")[1];
                if (content.contains("#")) {
                    String[] colIndexes = content.split("#");
                    for (String colIndex : colIndexes) {
                        String[] ix = colIndex.split("\\|");
                        if (ix.length > 1) {
                            indexes.put(ix[0], ix[1]);
                        }
                    }
                } else {
                    String[] ix = content.split("\\|");
                    indexes.put(ix[0], ix[1]);
                }
            }
        }

        return new ExportedMetadata(metaBucket, metaScope, metaCol, indexes);
    }


    static class ExportedMetadata {

        private final String bucket;
        private final String scope;
        private final List<String> collections;
        private final Map<String, String> indexes;

        public ExportedMetadata(String bucket, String scope, List<String> collections, Map<String, String> indexes) {
            this.bucket = bucket;
            this.scope = scope;
            this.collections = collections;
            this.indexes = indexes;
        }

        public String getScope() {
            return scope;
        }

        public List<String> getCollections() {
            return collections;
        }

        public Map<String, String> getIndexes() {
            return indexes;
        }

        public String getBucket() {
            return bucket;
        }
    }
}
