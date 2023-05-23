package com.couchbase.intellij.database;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.intellij.VirtualFileKeys;
import com.couchbase.intellij.tree.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.treeStructure.Tree;


import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class DataLoader {

    public static void listBuckets(DefaultMutableTreeNode parentNode, DefaultTreeModel treeModel, Tree tree) {
        Object userObject = parentNode.getUserObject();
        tree.setPaintBusy(true);
        if (userObject instanceof ConnectionNodeDescriptor) {
            CompletableFuture.runAsync(() -> {
                try {
                    Set<String> buckets = ActiveCluster.get().buckets().getAllBuckets().keySet();
                    parentNode.removeAllChildren();
                    for (String bucket : buckets) {
                        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new BucketNodeDescriptor(bucket));
                        childNode.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
                        parentNode.add(childNode);
                    }
                    treeModel.nodeStructureChanged(parentNode);
                } finally {
                    tree.setPaintBusy(false);
                }
            });
        } else {
            throw new IllegalStateException("The expected parent was ConnectionNode but gotsomething else");
        }
    }

    public static void listScopes(DefaultMutableTreeNode parentNode, DefaultTreeModel treeModel, Tree tree) {
        Object userObject = parentNode.getUserObject();
        tree.setPaintBusy(true);
        if (userObject instanceof BucketNodeDescriptor) {
            CompletableFuture.runAsync(() -> {
                try {
                    String bucketName = ((BucketNodeDescriptor) parentNode.getUserObject()).getText();
                    List<ScopeSpec> scopes = ActiveCluster.get().bucket(bucketName).collections().getAllScopes();
                    parentNode.removeAllChildren();
                    for (ScopeSpec scopeSpec : scopes) {
                        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new ScopeNodeDescriptor(scopeSpec.name()));

                        DefaultMutableTreeNode collections = new DefaultMutableTreeNode(new CollectionsNodeDescriptor());
                        collections.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
                        childNode.add(collections);

                        DefaultMutableTreeNode indexes = new DefaultMutableTreeNode(new IndexesNodeDescriptor());
                        indexes.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
                        childNode.add(indexes);
                        parentNode.add(childNode);
                    }
                    treeModel.nodeStructureChanged(parentNode);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    tree.setPaintBusy(false);
                }
            });
        } else {
            throw new IllegalStateException("The expected parent was BucketNode but got something else");
        }
    }


    public static void listCollections(DefaultMutableTreeNode parentNode, DefaultTreeModel treeModel, Tree tree) {
        Object userObject = parentNode.getUserObject();
        tree.setPaintBusy(true);
        if (userObject instanceof CollectionsNodeDescriptor) {
            CompletableFuture.runAsync(() -> {
                try {
                    parentNode.removeAllChildren();
                    DefaultMutableTreeNode scopeNode = (DefaultMutableTreeNode) parentNode.getParent();
                    String scopeName = ((ScopeNodeDescriptor) scopeNode.getUserObject()).getText();
                    String bucketName = ((BucketNodeDescriptor) ((DefaultMutableTreeNode) scopeNode.getParent()).getUserObject()).getText();

                    List<CollectionSpec> collections = ActiveCluster.get().bucket(bucketName).collections().getAllScopes().stream()
                            .filter(scope -> scope.name().equals(scopeName))
                            .flatMap(scope -> scope.collections().stream())
                            .collect(Collectors.toList());

                    for (CollectionSpec spec : collections) {
                        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new CollectionNodeDescriptor(spec.name()));
                        childNode.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
                        parentNode.add(childNode);

                    }
                    treeModel.nodeStructureChanged(parentNode);
                } finally {
                    tree.setPaintBusy(false);
                }
            });
        } else {
            throw new IllegalStateException("The expected parent was CollectionsNodeDescriptor but got something else");
        }
    }

    public static void listDocuments(Project project, DefaultMutableTreeNode parentNode, DefaultTreeModel treeModel, Tree tree) {
        Object userObject = parentNode.getUserObject();
        tree.setPaintBusy(true);
        if (userObject instanceof CollectionNodeDescriptor) {
            //CompletableFuture.runAsync(() -> {
            try {
                parentNode.removeAllChildren();

                String collectionName = ((CollectionNodeDescriptor) parentNode.getUserObject()).getText();
                DefaultMutableTreeNode scopeNode = (DefaultMutableTreeNode) parentNode.getParent().getParent();
                String scopeName = ((ScopeNodeDescriptor) scopeNode.getUserObject()).getText();
                String bucketName = ((BucketNodeDescriptor) ((DefaultMutableTreeNode) scopeNode.getParent()).getUserObject()).getText();
                String connName = ((ConnectionNodeDescriptor) ((DefaultMutableTreeNode) scopeNode.getParent().getParent()).getUserObject()).getText();

                final List<JsonObject> results = ActiveCluster.get().bucket(bucketName).scope(scopeName)
                        .query("Select meta(c).id as cbFileNameId, meta(c).cas as cbCasNb, c.* from `"
                                + collectionName + "` c order by meta(c).id limit 10", QueryOptions.queryOptions()).rowsAsObject();

                ApplicationManager.getApplication().runWriteAction(() -> {
                    PsiDirectory psiDirectory = findOrCreateFolder(project, connName, bucketName, scopeName, collectionName);

                    // Add a schema subfolder
                    DefaultMutableTreeNode schemaNode = new DefaultMutableTreeNode(new SchemaNodeDescriptor());
                    schemaNode.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
                    parentNode.add(schemaNode);

                    for (JsonObject obj : results) {

                        String docId = obj.getString("cbFileNameId");
                        Long cas = obj.getLong("cbCasNb");
                        obj.removeKey("cbFileNameId");
                        obj.removeKey("cbCasNb");

                        String fileName = docId + ".json";
                        //removes the id that we added

                        String fileContent = obj.toString(); // replace with actual JSON content if needed

                        // Check if the file already exists before creating it
                        PsiFile psiFile = psiDirectory.findFile(fileName);
                        if (psiFile == null) {
                            psiFile = psiDirectory.getManager().findDirectory(psiDirectory.getVirtualFile())
                                    .createFile(fileName);
                        }

                        // Get the Document associated with the PsiFile
                        Document document = FileDocumentManager.getInstance().getDocument(psiFile.getVirtualFile());
                        if (document != null) {
                            document.setText(fileContent);
                        }

                        // Retrieve the VirtualFile from the PsiFile
                        VirtualFile virtualFile = psiFile.getVirtualFile();
                        virtualFile.putUserData(VirtualFileKeys.CLUSTER, connName);
                        virtualFile.putUserData(VirtualFileKeys.BUCKET, bucketName);
                        virtualFile.putUserData(VirtualFileKeys.SCOPE, scopeName);
                        virtualFile.putUserData(VirtualFileKeys.COLLECTION, collectionName);
                        virtualFile.putUserData(VirtualFileKeys.ID, docId);
                        virtualFile.putUserData(VirtualFileKeys.CAS, cas.toString());

                        FileNodeDescriptor node = new FileNodeDescriptor(fileName, virtualFile);
                        DefaultMutableTreeNode jsonFileNode = new DefaultMutableTreeNode(node);
                        parentNode.add(jsonFileNode);
                    }
                    treeModel.nodeStructureChanged(parentNode);
                });

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            } finally {
                tree.setPaintBusy(false);
            }
            // });
        } else {
            throw new IllegalStateException("The expected parent was CollectionNodeDescriptor but got something else");
        }
    }

    public static void showSchema(DefaultMutableTreeNode parentNode, DefaultTreeModel treeModel, Tree tree) {
        Object userObject = parentNode.getUserObject();
        tree.setPaintBusy(true);
        if (userObject instanceof SchemaNodeDescriptor) {
            CompletableFuture.runAsync(() -> {
                try {
                    parentNode.removeAllChildren();

                    DefaultMutableTreeNode collectionNode = (DefaultMutableTreeNode) parentNode.getParent();
                    String collectionName = ((CollectionNodeDescriptor) collectionNode.getUserObject()).getText();
                    DefaultMutableTreeNode scopeNode = (DefaultMutableTreeNode) collectionNode.getParent().getParent();
                    String scopeName = ((ScopeNodeDescriptor) scopeNode.getUserObject()).getText();
                    String bucketName = ((BucketNodeDescriptor) ((DefaultMutableTreeNode) scopeNode.getParent()).getUserObject()).getText();

                    // Replace with your schema inference query
//                    String temporaryUsername = "kaustav";
//                    String temporaryPassword = "password";
                    String inferSchemaQuery = "SELECT d.* FROM CURL(\"http://localhost:8093/query/service\", {\"data\": \"statement=INFER `" + bucketName + "`.`" + scopeName + "`.`" + collectionName + "` WITH {\\\"sample_size\\\": 1000}\", \"user\": \""
                            + ActiveCluster.getActiveClusterUsername() + ":" + ActiveCluster.getActiveClusterPassword() + "\"}) d";

                    // Execute the schema inference query
                    final List<JsonObject> results = ActiveCluster.get().bucket(bucketName).scope(scopeName)
                            .query(inferSchemaQuery, QueryOptions.queryOptions()).rowsAsObject();

                    // Process the results and add them to the tree structure
                    for (JsonObject obj : results) {
                        // Replace with your code for processing the schema data and adding it to the tree structure
                        JsonObject inferSchemaRow = obj.getArray("results").getArray(0).getObject(0);
                        JsonObject inferSchemaProperties = extractTypes(inferSchemaRow.getObject("properties"));
                        String schemaString = inferSchemaProperties.toString();
                        String prettySchemaString = prettyPrintJson(schemaString);

                        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new SchemaDataNodeDescriptor(prettySchemaString));
                        parentNode.add(childNode);
                    }
                    treeModel.nodeStructureChanged(parentNode);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    tree.setPaintBusy(false);
                }
            });
        } else {
            throw new IllegalStateException("The expected parent was SchemaNodeDescriptor but got something else");
        }
    }

    public static JsonObject extractTypes(JsonObject properties) {
        JsonObject result = JsonObject.create();

        try {
            for (String key : properties.getNames()) {
                JsonObject property = properties.getObject(key);
                if (property != null && property.containsKey("type")) {
                    Object type = property.get("type");
                    if (type instanceof String) {
                        if (((String) type).equalsIgnoreCase("object"))
                            result.put(key, extractTypes(property.getObject("properties")));
                        else if (((String) type).equalsIgnoreCase("array")) {
                            JsonObject items = property.getObject("items");
                            if (items != null && items.containsKey("type")) {
                                Object itemType = items.get("type");
                                if (itemType instanceof String) {
                                    if (((String) itemType).equalsIgnoreCase("object"))
                                        result.put(key, "array of " + extractTypes(items.getObject("properties")));
                                    else
                                        result.put(key, "array of " + itemType);
                                } else if (itemType instanceof JsonArray) {
                                    StringBuilder types = new StringBuilder();
                                    for (int i = 0; i < ((JsonArray) itemType).size(); i++) {
                                        types.append(((JsonArray) itemType).getString(i)).append(" | ");
                                    }

                                    types = new StringBuilder(types.substring(0, types.length() - 3));
                                    result.put(key, "array of " + types.toString());
                                }
                            } else {
                                result.put(key, "array");
                            }
                        } else {
                            result.put(key, type);
                        }
                    } else if (type instanceof JsonArray) {
                        StringBuilder types = new StringBuilder();
                        for (int i = 0; i < ((JsonArray) type).size(); i++) {
                            types.append(((JsonArray) type).getString(i)).append(" | ");
                        }

                        types = new StringBuilder(types.substring(0, types.length() - 3));
                        result.put(key, types.toString());
                    }
                } else if (property != null && property.containsKey("properties")) {
                    result.put(key, extractTypes(property.getObject("properties")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    public static String prettyPrintJson(String json) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(json);
        return gson.toJson(je);
    }


    private static PsiDirectory findOrCreateFolder(Project project, String connection, String bucket, String scope, String collection) {

        String basePath = project.getBasePath(); // Replace with the appropriate base path if needed
        VirtualFile baseDirectory = LocalFileSystem.getInstance().findFileByPath(basePath);

        //return ApplicationManager.getApplication().runWriteAction((Computable<PsiDirectory>) () -> {
        try {
            String dirPath = connection + File.separator + bucket + File.separator + scope + File.separator + collection;
            VirtualFile directory = VfsUtil.createDirectoryIfMissing(baseDirectory, dirPath);
            return PsiManager.getInstance(project).findDirectory(directory);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        //});

    }
}
