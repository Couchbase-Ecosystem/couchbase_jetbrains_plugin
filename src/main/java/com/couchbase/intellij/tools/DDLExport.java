package com.couchbase.intellij.tools;

import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.client.java.manager.query.QueryIndex;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.DataLoader;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import utils.IndexUtils;
import utils.TimeUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.stream.Collectors;

public class DDLExport {

    private static String exportScope(String bucket, String scope, boolean includeIndexes) {
        StringBuilder sb = new StringBuilder();
        String scopePrefix = "`" + bucket + "`.`" + scope + "`";

        if (!"_default".equals(scope)) {
            sb.append("CREATE SCOPE ").append(scopePrefix).append(";");
        }
        sb.append("\n");

        final List<CollectionSpec> collections = ActiveCluster.getInstance().get().bucket(bucket)
                .collections().getAllScopes().stream()
                .filter(s -> s.name().equals(scope))
                .flatMap(s -> s.collections().stream())
                .collect(Collectors.toList());

        for (CollectionSpec spec : collections) {

            sb.append("\n");
            sb.append("/* DDL for collection ").append(scope).append(".").append(spec.name()).append(" */");
            sb.append("\n");


            if (!"_default".equals(spec.name())) {
                sb.append("CREATE COLLECTION ").append(scopePrefix).append(".`").append(spec.name()).append("`; \n");
            }
            if (includeIndexes) {
                List<QueryIndex> result = DataLoader.listIndexes(bucket, scope, spec.name());
                sb.append(result.stream().map(e -> IndexUtils.getIndexDefinition(e, true)).collect(Collectors.joining("; \n")));

                if (result.size() >= 1) {
                    sb.append("; \n");
                }
            }
            sb.append("\n");
        }

        sb.append("\n\n");

        return sb.toString();
    }

    public static void exportScope(final String bucket, final List<String> scopes, final String filePath, final boolean includeIndexes) {
        ProgressManager.getInstance().run(new Task.Backgroundable(null, "Exporting DDL'", true) {
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);
                try {
                    StringBuilder sb = new StringBuilder();

                    boolean allScopes = scopes.contains("All Scopes");
                    List<String> selectedScopes = scopes;

                    if (allScopes) {
                        selectedScopes = ActiveCluster.getInstance().get().bucket(bucket).collections().getAllScopes().stream().map(ScopeSpec::name).collect(Collectors.toList());
                    }

                    for (String scope : selectedScopes) {
                        sb.append("/*************************/ \n");
                        sb.append("/** Scope ").append(scope).append(" */ \n");
                        sb.append("/*************************/ \n");
                        sb.append(exportScope(bucket, scope, includeIndexes));
                        sb.append("\n");
                    }

                    String fileName = bucket + (allScopes ? "_all_" : getScopesFileName(scopes)) + TimeUtils.getCurrentDateTime() + ".sqlpp";
                    if (!filePath.endsWith(File.separator)) {
                        fileName = File.separator + fileName;
                    }
                    String fullPath = filePath + fileName;
                    File file = new File(fullPath);

                    FileWriter fileWriter = new FileWriter(file);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(sb.toString());
                    bufferedWriter.close();

                    Log.info("DDL file " + fullPath + " was exported successfully.");

                    ApplicationManager.getApplication().invokeLater(() -> Messages.showInfoMessage("File exported successfully.", "DDL Export"));

                } catch (Exception e) {
                    Log.error("An error occurred while writing to export the DDL: ", e);
                }
            }
        });
    }

    private static String getScopesFileName(List<String> scopes) {

        if (scopes.size() == 1) {
            return scopes.get(0) + "_";
        } else {
            return scopes.size() + "_scopes_";
        }
    }
}
