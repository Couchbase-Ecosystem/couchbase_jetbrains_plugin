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
                        ProcessBuilder processBuilder = new ProcessBuilder(CBTools.getCbImport().getPath(), "json", "--no-ssl-verify", "-c", ActiveCluster.getInstance().getClusterURL(), "-u", ActiveCluster.getInstance().getUsername(), "-p", ActiveCluster.getInstance().getPassword(), "-b", bucket, "--format", "list", "-d", "file://" + filePath, "--scope-collection-exp", scope + "." + collection, "-g", "%cbmid%", "--ignore-fields", "cbmid,cbms,cbmc", "-t", "4");

                        if (createIndexes) {
                            createIndexes(bucket, scope, originalCol, collection, meta.getIndexes().get(originalCol));
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
                        ProcessBuilder processBuilder = new ProcessBuilder(CBTools.getCbImport().getPath(), "json", "--no-ssl-verify", "-c", ActiveCluster.getInstance().getClusterURL(), "-u", ActiveCluster.getInstance().getUsername(), "-p", ActiveCluster.getInstance().getPassword(), "-b", bucket, "--format", "list", "-d", "file://" + filePath, "--scope-collection-exp", "%cbms%.%cbmc%", "-g", "%cbmid%", "--ignore-fields", "cbmid,cbms,cbmc", "-t", "4");

                        if (createIndexes) {
                            createIndexes(bucket, meta.getScope(), scope, meta.getIndexes(), skippedCollections);
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

    private static void createIndexes(String bucket, String originalScope, String newScope, Map<String, String> indexes, Set<String> skipCols) {
        boolean replaceScope = !originalScope.equals(newScope);
        for (Map.Entry<String, String> entry : indexes.entrySet()) {

            if (skipCols.contains(entry.getKey())) {
                continue;
            }

            byte[] decodedBytes = Base64.getDecoder().decode(entry.getValue());
            String decodedString = new String(decodedBytes);
            String[] idxArray = decodedString.split("#");

            for (String s : idxArray) {
                String query = s;
                if (replaceScope) {
                    query = query.replace("`" + originalScope + "`", "`" + newScope + "`");
                }
                try {
                    ActiveCluster.getInstance().get().bucket(bucket).scope(newScope).query(query);
                    Log.info("Index " + query + " created successfully.");
                } catch (IndexExistsException ex) {
                    Log.info("Index " + query + " already exists.");
                } catch (Exception e) {
                    Log.error("Could not create index " + query, e);
                }
            }
        }
    }

    private static void createIndexes(String bucket, String scope, String originalCol, String newCol, String indexes) {

        boolean replaceCol = !originalCol.equals(newCol);
        byte[] decodedBytes = Base64.getDecoder().decode(indexes);
        String decodedString = new String(decodedBytes);
        String[] idxArray = decodedString.split("#");

        for (String s : idxArray) {
            String query = s;
            if (replaceCol) {
                query = query.replace("`" + originalCol + "`", "`" + newCol + "`");
            }
            try {
                ActiveCluster.getInstance().get().bucket(bucket).scope(scope).query(query);
                Log.info("Index " + query + " created successfully.");
            } catch (IndexExistsException ex) {
                Log.info("Index " + query + " already exists.");
            } catch (Exception e) {
                Log.error("Could not create index " + query, e);
            }
        }

    }

    private static void executeProcess(ProcessBuilder processBuilder) throws Exception {
        Process process = processBuilder.start();
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
        Map<String, String> indexes = new HashMap<>();

        for (String m : meta) {
            if (m.startsWith("scope:")) {
                metaScope = m.split(":")[1];
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

        return new ExportedMetadata(metaScope, metaCol, indexes);
    }

    static class ExportedMetadata {
        private final String scope;
        private final List<String> collections;
        private final Map<String, String> indexes;

        public ExportedMetadata(String scope, List<String> collections, Map<String, String> indexes) {
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
    }
}
