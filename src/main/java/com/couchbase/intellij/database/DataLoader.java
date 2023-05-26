package com.couchbase.intellij.database;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.intellij.VirtualFileKeys;
import com.couchbase.intellij.persistence.ClusterAlreadyExistsException;
import com.couchbase.intellij.persistence.Clusters;
import com.couchbase.intellij.persistence.ClustersStorage;
import com.couchbase.intellij.persistence.DuplicatedClusterNameAndUserException;
import com.couchbase.intellij.persistence.PasswordStorage;
import com.couchbase.intellij.persistence.SavedCluster;
import com.couchbase.intellij.tree.BucketNodeDescriptor;
import com.couchbase.intellij.tree.CollectionNodeDescriptor;
import com.couchbase.intellij.tree.CollectionsNodeDescriptor;
import com.couchbase.intellij.tree.ConnectionNodeDescriptor;
import com.couchbase.intellij.tree.FileNodeDescriptor;
import com.couchbase.intellij.tree.IndexesNodeDescriptor;
import com.couchbase.intellij.tree.LoadingNodeDescriptor;
import com.couchbase.intellij.tree.SchemaDataNodeDescriptor;
import com.couchbase.intellij.tree.SchemaNodeDescriptor;
import com.couchbase.intellij.tree.ScopeNodeDescriptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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

public class DataLoader {

    public static void listBuckets(DefaultMutableTreeNode parentNode, Tree tree) {

        Object userObject = parentNode.getUserObject();
        tree.setPaintBusy(true);
        if (userObject instanceof ConnectionNodeDescriptor) {
            CompletableFuture.runAsync(() -> {
                try {
                    Set<String> buckets = ActiveCluster.getInstance().get().buckets().getAllBuckets().keySet();
                    parentNode.removeAllChildren();
                    for (String bucket : buckets) {

                        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new BucketNodeDescriptor(bucket,
                                ActiveCluster.getInstance().getId()));
                        childNode.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
                        parentNode.add(childNode);
                    }

                    ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    tree.setPaintBusy(false);
                }
            });
        } else {
            throw new IllegalStateException("The expected parent was ConnectionNode but got something else");
        }
    }

    public static void listScopes(DefaultMutableTreeNode parentNode, Tree tree) {
        Object userObject = parentNode.getUserObject();
        tree.setPaintBusy(true);
        if (userObject instanceof BucketNodeDescriptor) {
            CompletableFuture.runAsync(() -> {
                try {
                    String bucketName = ((BucketNodeDescriptor) parentNode.getUserObject()).getText();
                    List<ScopeSpec> scopes = ActiveCluster.getInstance().get().bucket(bucketName).collections()
                            .getAllScopes();
                    parentNode.removeAllChildren();
                    for (ScopeSpec scopeSpec : scopes) {
                        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(
                                new ScopeNodeDescriptor(scopeSpec.name(), ActiveCluster.getInstance().getId(),
                                        bucketName));

                        DefaultMutableTreeNode collections = new DefaultMutableTreeNode(
                                new CollectionsNodeDescriptor(ActiveCluster.getInstance().getId(), bucketName,
                                        scopeSpec.name()));
                        collections.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
                        childNode.add(collections);

                        DefaultMutableTreeNode indexes = new DefaultMutableTreeNode(new IndexesNodeDescriptor());
                        indexes.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
                        childNode.add(indexes);
                        parentNode.add(childNode);
                    }
                    ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
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

    public static void listCollections(DefaultMutableTreeNode parentNode, Tree tree) {
        Object userObject = parentNode.getUserObject();
        tree.setPaintBusy(true);
        if (userObject instanceof CollectionsNodeDescriptor) {
            CompletableFuture.runAsync(() -> {
                try {
                    parentNode.removeAllChildren();
                    CollectionsNodeDescriptor cols = (CollectionsNodeDescriptor) userObject;

                    List<CollectionSpec> collections = ActiveCluster.getInstance().get().bucket(cols.getBucket())
                            .collections().getAllScopes().stream()
                            .filter(scope -> scope.name().equals(cols.getScope()))
                            .flatMap(scope -> scope.collections().stream())
                            .collect(Collectors.toList());

                    for (CollectionSpec spec : collections) {
                        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(
                                new CollectionNodeDescriptor(spec.name(), ActiveCluster.getInstance().getId(),
                                        cols.getBucket(), cols.getScope()));
                        childNode.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
                        parentNode.add(childNode);
                    }
                    ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    tree.setPaintBusy(false);
                }
            });
        } else {
            throw new IllegalStateException("The expected parent was CollectionsNodeDescriptor but got something else");
        }
    }

    public static void listDocuments(Project project, DefaultMutableTreeNode parentNode, Tree tree) {
        Object userObject = parentNode.getUserObject();
        tree.setPaintBusy(true);
        if (userObject instanceof CollectionNodeDescriptor) {
            // CompletableFuture.runAsync(() -> {
            try {
                parentNode.removeAllChildren();

                CollectionNodeDescriptor colNode = (CollectionNodeDescriptor) userObject;

                final List<JsonObject> results = ActiveCluster.getInstance().get().bucket(colNode.getBucket())
                        .scope(colNode.getScope())
                        .query("Select meta(c).id as cbFileNameId, meta(c).cas as cbCasNb, c.* from `"
                                + colNode.getText() + "` c order by meta(c).id limit 10", QueryOptions.queryOptions())
                        .rowsAsObject();

                ApplicationManager.getApplication().runWriteAction(() -> {
                    PsiDirectory psiDirectory = findOrCreateFolder(project, ActiveCluster.getInstance().getId(),
                            colNode.getBucket(), colNode.getScope(),
                            colNode.getText());

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
                        // removes the id that we added

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
                        virtualFile.putUserData(VirtualFileKeys.CONN_ID, ActiveCluster.getInstance().getId());
                        virtualFile.putUserData(VirtualFileKeys.CLUSTER, ActiveCluster.getInstance().getId());
                        virtualFile.putUserData(VirtualFileKeys.BUCKET, colNode.getBucket());
                        virtualFile.putUserData(VirtualFileKeys.SCOPE, colNode.getScope());
                        virtualFile.putUserData(VirtualFileKeys.COLLECTION, colNode.getText());
                        virtualFile.putUserData(VirtualFileKeys.ID, docId);
                        virtualFile.putUserData(VirtualFileKeys.CAS, cas.toString());

                        FileNodeDescriptor node = new FileNodeDescriptor(fileName, virtualFile);
                        DefaultMutableTreeNode jsonFileNode = new DefaultMutableTreeNode(node);
                        parentNode.add(jsonFileNode);
                    }
                    ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
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

                    CollectionNodeDescriptor colNode = (CollectionNodeDescriptor) ((DefaultMutableTreeNode) parentNode
                            .getParent()).getUserObject();
                    String collectionName = colNode.getText();
                    String scopeName = colNode.getScope();
                    String bucketName = colNode.getBucket();

                    String clusterURL = ActiveCluster.getInstance().getClusterURL(); // couchbase://localhost
                    String serverURI = "";
                    if (ActiveCluster.getInstance().isSSLEnabled()) {
                        serverURI = clusterURL.replace("couchbases://", "https://");
                        serverURI += ":18093/query/service";
                    } else {
                        serverURI = clusterURL.replace("couchbase://", "http://");
                        serverURI += ":8093/query/service";
                    }

                    // // Approach 1: Using HTTP Request
                    // Create an HttpClient
                    HttpClient client = HttpClient.newHttpClient();

                    // Build the request body
                    String requestBody = "{\"statement\":\"INFER `" + bucketName + "`.`" +
                            scopeName + "`.`"
                            + collectionName + "` WITH {\\\"sample_size\\\": 1000}\"}";

                    // Build the request
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(serverURI))
                            .header("Content-Type", "application/json")
                            .header("Authorization",
                                    "Basic " + Base64.getEncoder()
                                            .encodeToString((ActiveCluster.getInstance().getUsername() + ":"
                                                    + ActiveCluster.getInstance().getPassword()).getBytes()))
                            .POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();

                    // Send the request and get the response
                    HttpResponse<String> response = client.send(request,
                            HttpResponse.BodyHandlers.ofString());

                    // Get the response body
                    String responseBody = response.body();

                    // // Get the response body as a JsonObject
                    JsonObject inferenceQueryResults = JsonObject.fromJson(responseBody);

                    // // Approach 2: Using CURL function
                    // String inferSchemaQuery = "SELECT d.* FROM
                    // CURL(\"http://localhost:8093/query/service\", {\"data\": \"statement=INFER `"
                    // + bucketName + "`.`" + scopeName + "`.`" + collectionName
                    // + "` WITH {\\\"sample_size\\\": 1000}\", \"user\": \""
                    // + ActiveCluster.getInstance().getUsername() + ":"
                    // + ActiveCluster.getInstance().getPassword()
                    // + "\"}) d";
                    //
                    // // Execute the schema inference query
                    // final JsonObject inferenceQueryResults =
                    // ActiveCluster.getInstance().get().bucket(bucketName)
                    // .scope(scopeName).query(inferSchemaQuery,
                    // QueryOptions.queryOptions())
                    // .rowsAsObject().get(0);

                    // Process the results and add them to the tree structure
                    // Replace with your code for processing the schema data and adding it to the
                    // tree structure

                    JsonObject inferSchemaRow = inferenceQueryResults.getArray("results").getArray(0).getObject(0);
                    JsonObject inferSchemaProperties = extractTypes(inferSchemaRow.getObject("properties"));

                    addSchemaToTree(inferSchemaProperties, parentNode);
                    treeModel.nodeStructureChanged(parentNode);

//                    String schemaString = inferSchemaProperties.toString();
//                    String prettySchemaString = prettyPrintJson(schemaString);
//
//                    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(
//                            new SchemaDataNodeDescriptor(prettySchemaString));
//                    parentNode.add(childNode);
//
//                    treeModel.nodeStructureChanged(parentNode);

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
        for (String key : properties.getNames()) {
            JsonObject property = properties.getObject(key);
            if (property != null && property.containsKey("type")) {
                Object type = property.get("type");
                if (type instanceof String) {
                    String typeString = (String) type;
                    if (typeString.equalsIgnoreCase("object")) {
                        result.put(key, extractTypes(property.getObject("properties")));
                    } else if (typeString.equalsIgnoreCase("array")) {
                        JsonObject items = property.getObject("items");
                        if (items != null && items.containsKey("type")) {
                            Object itemType = items.get("type");
                            if (itemType instanceof String) {
                                String itemTypeString = (String) itemType;
                                if (itemTypeString.equalsIgnoreCase("object")) {
                                    result.put(key, "array of " + extractTypes(items.getObject("properties")));
                                } else {
                                    result.put(key, "array of " + itemType);
                                }
                            } else if (itemType instanceof JsonArray) {
                                result.put(key, "array of " + extractTypesFromArray((JsonArray) itemType));
                            }
                        } else {
                            result.put(key, "array");
                        }
                    } else {
                        result.put(key, type);
                    }
                } else if (type instanceof JsonArray) {
                    result.put(key, extractTypesFromArray((JsonArray) type));
                }
            } else if (property != null && property.containsKey("properties")) {
                result.put(key, extractTypes(property.getObject("properties")));
            }
        }
        return result;
    }

    private static String extractTypesFromArray(JsonArray array) {
        StringBuilder types = new StringBuilder();
        for (int i = 0; i < array.size(); i++) {
            Object item = array.get(i);
            if (item instanceof String) {
                types.append(item).append(" | ");
            } else if (item instanceof JsonObject) {
                types.append(extractTypes((JsonObject) item)).append(" | ");
            }
        }
        types.delete(types.length() - 3, types.length());
        return types.toString();
    }

    private static void addSchemaToTree(JsonObject schema, DefaultMutableTreeNode parentNode) {
        for (String key : schema.getNames()) {
            Object value = schema.get(key);
            if (value instanceof JsonObject) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new SchemaDataNodeDescriptor(key));
                addSchemaToTree((JsonObject) value, childNode);
                parentNode.add(childNode);
            } else if (value instanceof String && ((String) value).startsWith("array of {")) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new SchemaDataNodeDescriptor(key + ": array of objects"));
                JsonObject arraySchema = JsonObject.fromJson(((String) value).substring(9));
                addSchemaToTree(arraySchema, childNode);
                parentNode.add(childNode);
            } else {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new SchemaDataNodeDescriptor(key + ": " + value.toString()));
                parentNode.add(childNode);
            }
        }
    }


    public static String prettyPrintJson(String json) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(json);
        return gson.toJson(je);
    }

    private static PsiDirectory findOrCreateFolder(Project project, String connection, String bucket, String scope,
                                                   String collection) {

        String basePath = project.getBasePath(); // Replace with the appropriate base path if needed
        VirtualFile baseDirectory = LocalFileSystem.getInstance().findFileByPath(basePath);

        try {
            String dirPath = connection + File.separator + bucket + File.separator + scope + File.separator
                    + collection;
            VirtualFile directory = VfsUtil.createDirectoryIfMissing(baseDirectory, dirPath);
            return PsiManager.getInstance(project).findDirectory(directory);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public static String adjustClusterProtocol(String cluster, boolean ssl) {
        if (cluster.startsWith("couchbase://") || cluster.startsWith("couchbases://")) {
            return cluster;
        }

        String protocol = "";
        if (ssl) {
            protocol = "couchbases://";
        } else {
            protocol = "couchbase://";
        }
        return protocol + cluster;
    }

    public static Set<String> listBucketNames(String clusterUrl, boolean ssl, String username, String password) {

        Cluster cluster = null;
        try {
            cluster = Cluster.connect(adjustClusterProtocol(clusterUrl, ssl),
                    ClusterOptions.clusterOptions(username, password).environment(env -> {
                        // env.applyProfile("wan-development");
                    }));
            cluster.waitUntilReady(Duration.ofSeconds(5));

            return cluster.buckets().getAllBuckets().keySet();
        } catch (Exception e) {
            cluster.disconnect();
            throw e;
        }

    }

    public static SavedCluster saveDatabaseCredentials(String name, String url, boolean isSSL, String username,
                                                       String password, String defaultBucket) {
        String key = username + ":" + name;
        SavedCluster sc = new SavedCluster();
        sc.setId(key);
        sc.setName(name);
        sc.setSslEnable(isSSL);
        sc.setUsername(username);
        sc.setUrl(adjustClusterProtocol(url, isSSL));
        sc.setDefaultBucket(defaultBucket);

        Clusters clusters = ClustersStorage.getInstance().getValue();
        if (clusters == null) {
            clusters = new Clusters();
        }

        if (clusters.getMap() == null) {
            clusters.setMap(new HashMap<>());
        }

        if (clusters.getMap().containsKey(sc.getId())) {
            throw new DuplicatedClusterNameAndUserException();
        }

        for (Map.Entry<String, SavedCluster> entry : clusters.getMap().entrySet()) {
            if (entry.getValue().getUrl().equals(sc.getUrl()) && entry.getValue().getUsername().equals(username)) {
                throw new ClusterAlreadyExistsException();
            }
        }

        clusters.getMap().put(key, sc);
        ClustersStorage.getInstance().setValue(clusters);
        PasswordStorage.savePassword(sc, password);

        return sc;
    }

    public static Map<String, SavedCluster> getSavedClusters() {
        if (ClustersStorage.getInstance().getValue() == null
                || ClustersStorage.getInstance().getValue().getMap() == null) {
            return new HashMap<>();
        }
        return ClustersStorage.getInstance().getValue().getMap();
    }

    public static String getClusterPassword(SavedCluster sv) {
        return PasswordStorage.getPassword(sv);
    }

    public static void deleteSavedCluster(SavedCluster sv) {
        PasswordStorage.savePassword(sv, null);
        ClustersStorage.getInstance().getValue().getMap().remove(sv.getId());
    }
}
