package com.couchbase.intellij.database;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.intellij.VirtualFileKeys;
import com.couchbase.intellij.persistence.*;
import com.couchbase.intellij.tree.*;
import com.couchbase.intellij.workbench.SQLPPQueryUtils;
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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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

                        String filter = QueryFiltersStorage.getInstance()
                                .getValue()
                                .getQueryFilter(ActiveCluster.getInstance().getId(),
                                        cols.getBucket(), cols.getScope(), spec.name());

                        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(
                                new CollectionNodeDescriptor(spec.name(), ActiveCluster.getInstance().getId(),
                                        cols.getBucket(), cols.getScope(), filter));

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

    public static void listDocuments(Project project, DefaultMutableTreeNode parentNode, Tree tree, int newOffset) {
        Object userObject = parentNode.getUserObject();
        tree.setPaintBusy(true);
        if (userObject instanceof CollectionNodeDescriptor) {
            CollectionNodeDescriptor colNode = (CollectionNodeDescriptor) parentNode.getUserObject();
            // CompletableFuture.runAsync(() -> {
            try {
                if (newOffset == 0) {
                    parentNode.removeAllChildren();
                    DefaultMutableTreeNode schemaNode = new DefaultMutableTreeNode(new SchemaNodeDescriptor());
                    schemaNode.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
                    parentNode.add(schemaNode);
                } else {
                    parentNode.remove(parentNode.getChildCount() - 1);
                }


                String filter = colNode.getQueryFilter();
                String query = "Select meta(couchbaseAlias).id as cbFileNameId, meta(couchbaseAlias).cas as cbCasNb, couchbaseAlias.* from `"

                        + colNode.getText() + "` as couchbaseAlias "
                        + ((filter == null || filter.isEmpty()) ? "" : (" where " + filter))
                        + (SQLPPQueryUtils.hasOrderBy(filter) ? "" : "  order by meta(couchbaseAlias).id ")
                        + (newOffset == 0 ? "" : " OFFSET " + newOffset)
                        + " limit 10";

                final List<JsonObject> results = ActiveCluster.getInstance().get().bucket(colNode.getBucket())
                        .scope(colNode.getScope())
                        .query(query)
                        .rowsAsObject();

                ApplicationManager.getApplication().runWriteAction(() -> {
                    PsiDirectory psiDirectory = findOrCreateFolder(project, ActiveCluster.getInstance().getId(),
                            colNode.getBucket(), colNode.getScope(),
                            colNode.getText());

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

                });

                if (results.size() == 10) {
                    DefaultMutableTreeNode loadMoreNode = new DefaultMutableTreeNode(
                            new LoadMoreNodeDescriptor(colNode.getBucket(), colNode.getScope(), colNode.getText(), newOffset + 10));
                    parentNode.add(loadMoreNode);
                }
                ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
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
                    JsonArray array = inferenceQueryResults.getArray("results").getArray(0);
                    extractArray(parentNode, array);
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

    private static void extractArray(DefaultMutableTreeNode parentNode, JsonArray array) {
        for (int i = 0; i < array.size(); i++) {
            JsonObject inferSchemaRow = array.getObject(i);

            String tooltip = "#docs: " + inferSchemaRow.getNumber("#docs") + ", pattern: " + inferSchemaRow.getString("Flavor");
            SchemaFlavorNodeDescriptor sf = new SchemaFlavorNodeDescriptor("Pattern Found #" + (i + 1), tooltip);
            DefaultMutableTreeNode flavorNode = new DefaultMutableTreeNode(sf);
            parentNode.add(flavorNode);
            JsonObject properties = inferSchemaRow.getObject("properties");

            if (properties != null) {
                extractTypes(flavorNode, inferSchemaRow.getObject("properties"));

            } else {
                JsonArray samples = inferSchemaRow.getArray("samples");
                if (samples != null) {
                    String additionalTooltip = samples.toList()
                            .stream()
                            .map(e -> e.toString()).collect(Collectors.joining(","));
                    sf.setTooltip(sf.getTooltip() + ", samples: " + additionalTooltip);
                } else {
                    System.err.println("Infer reached an unexpected state");
                }
            }
        }
    }

    public static void extractTypes(DefaultMutableTreeNode parentNode, JsonObject properties) {
        for (String key : properties.getNames()) {
            JsonObject property = properties.getObject(key);
            String type = property.get("type").toString();
            //if it is an Object
            if (type.equals("object")) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(
                        new SchemaDataNodeDescriptor(key));
                extractTypes(childNode, property.getObject("properties"));
                parentNode.add(childNode);

            } else if (type.equals("array")) {
                try {
                    JsonObject items = property.getObject("items");
                    String itemTypeString = (String) items.get("type");
                    if (itemTypeString != null) {
                        DefaultMutableTreeNode childNode;
                        if (itemTypeString.equals("object")) {
                            //result.put(key, "array of " + extractTypes(items.getObject("properties")));
                            childNode = new DefaultMutableTreeNode(
                                    new SchemaDataNodeDescriptor(key, "array of objects", null));
                            extractTypes(childNode, items.getObject("properties"));
                        } else {
                            childNode = new DefaultMutableTreeNode(
                                    new SchemaDataNodeDescriptor(key, "array of " + itemTypeString + "s", null));
                        }
                        parentNode.add(childNode);
                    } else {
                        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(
                                new SchemaDataNodeDescriptor(key, type, null));
                        parentNode.add(childNode);
                    }
                } catch (ClassCastException cce) {
                    JsonArray array = property.getArray("items");
                    extractArray(parentNode, array);
                }
            } else {
                addLeaf(parentNode, key, property);
            }
        }
    }

    private static void addLeaf(DefaultMutableTreeNode parentNode, String key, JsonObject property) {
        String type = property.get("type").toString();
        boolean containsNull = false;
        if (type.contains("[")) {
            type = type.replace("[", "")
                    .replace("]", "")
                    .replace("\"", "")
                    .replace(",", " | ");

            if (type.contains("null")) {
                containsNull = true;
            }
        }

        String samples = null;
        JsonArray samplesArray = property.getArray("samples");
        if (samplesArray != null) {
            if (containsNull) {
                if (samplesArray.size() > 1) {
                    //ignoring the first array that will only contains null
                    samplesArray = samplesArray.getArray(1);
                } else {
                    samplesArray = samplesArray.getArray(0);
                }

                samples = samplesArray.toList()
                        .stream()
                        .map(e -> e == null ? "null" : e.toString())
                        .collect(Collectors.joining(" , "));
            }
        }

        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(
                new SchemaDataNodeDescriptor(key, type, samples));
        parentNode.add(childNode);
    }

    private static JsonObject extractResult(JsonObject obj) {
        JsonObject result = JsonObject.create();
        result.put("isLeaf", true);
        String type = obj.get("type").toString();
        boolean containsNull = false;
        if (type.contains("[")) {
            type = type.replace("[", "")
                    .replace("]", "")
                    .replace("\"", "")
                    .replace(",", " | ");

            if (type.contains("null")) {
                containsNull = true;
            }
            result.put("type", type);
        } else {
            result.put("type", type);
        }

        if (obj.getArray("samples") != null) {
            if (containsNull) {
                JsonArray array = obj.getArray("samples");
                if (array.size() > 1) {
                    //ignoring the first array that will only contains null
                    result.put("samples", array.getArray(1));
                } else {
                    result.put("samples", array.getArray(0));
                }
            } else {
                result.put("samples", obj.getArray("samples"));
            }

        }

        return result;
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
