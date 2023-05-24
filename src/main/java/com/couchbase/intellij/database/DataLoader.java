package com.couchbase.intellij.database;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.intellij.VirtualFileKeys;
import com.couchbase.intellij.persistence.*;
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
import javax.swing.tree.TreePath;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class DataLoader {

    public static void listBuckets(DefaultMutableTreeNode parentNode, Tree tree) {

        Object userObject = parentNode.getUserObject();
        tree.setPaintBusy(true);
        if (userObject instanceof ConnectionNodeDescriptor) {
            CompletableFuture.runAsync(() -> {
                try {
                    Set<String> buckets = ActiveCluster.getInstance().get()
                            .buckets().getAllBuckets().keySet();
                    parentNode.removeAllChildren();
                    for (String bucket : buckets) {

                        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new BucketNodeDescriptor(bucket));
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
            throw new IllegalStateException("The expected parent was ConnectionNode but gotsomething else");
        }
    }

    public static void listScopes(DefaultMutableTreeNode parentNode, Tree tree) {
        Object userObject = parentNode.getUserObject();
        tree.setPaintBusy(true);
        if (userObject instanceof BucketNodeDescriptor) {
            CompletableFuture.runAsync(() -> {
                try {
                    String bucketName = ((BucketNodeDescriptor) parentNode.getUserObject()).getText();
                    List<ScopeSpec> scopes = ActiveCluster.getInstance().get().bucket(bucketName).collections().getAllScopes();
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
                    ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
                    System.out.println("updating scope structure----");
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
                    DefaultMutableTreeNode scopeNode = (DefaultMutableTreeNode) parentNode.getParent();
                    String scopeName = ((ScopeNodeDescriptor) scopeNode.getUserObject()).getText();
                    String bucketName = ((BucketNodeDescriptor) ((DefaultMutableTreeNode) scopeNode.getParent()).getUserObject()).getText();

                    List<CollectionSpec> collections = ActiveCluster.getInstance().get().bucket(bucketName).collections().getAllScopes().stream()
                            .filter(scope -> scope.name().equals(scopeName))
                            .flatMap(scope -> scope.collections().stream())
                            .collect(Collectors.toList());

                    for (CollectionSpec spec : collections) {
                        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new CollectionNodeDescriptor(spec.name()));
                        childNode.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
                        parentNode.add(childNode);
                    }
                    ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
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
            //CompletableFuture.runAsync(() -> {
            try {
                parentNode.removeAllChildren();

                String collectionName = ((CollectionNodeDescriptor) parentNode.getUserObject()).getText();
                DefaultMutableTreeNode scopeNode = (DefaultMutableTreeNode) parentNode.getParent().getParent();
                String scopeName = ((ScopeNodeDescriptor) scopeNode.getUserObject()).getText();
                String bucketName = ((BucketNodeDescriptor) ((DefaultMutableTreeNode) scopeNode.getParent()).getUserObject()).getText();
                String connName = ((ConnectionNodeDescriptor) ((DefaultMutableTreeNode) scopeNode.getParent().getParent()).getUserObject()).getText();

                final List<JsonObject> results = ActiveCluster.getInstance().get().bucket(bucketName).scope(scopeName)
                        .query("Select meta(c).id as cbFileNameId, meta(c).cas as cbCasNb, c.* from `"
                                + collectionName + "` c order by meta(c).id limit 10", QueryOptions.queryOptions()).rowsAsObject();

                ApplicationManager.getApplication().runWriteAction(() -> {
                    PsiDirectory psiDirectory = findOrCreateFolder(project, connName, bucketName, scopeName, collectionName);
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
                        virtualFile.putUserData(VirtualFileKeys.CONN_ID, ActiveCluster.getInstance().getId());
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


    private static PsiDirectory findOrCreateFolder(Project project, String connection, String bucket, String scope, String collection) {

        String basePath = project.getBasePath(); // Replace with the appropriate base path if needed
        VirtualFile baseDirectory = LocalFileSystem.getInstance().findFileByPath(basePath);

        try {
            String dirPath = connection + File.separator + bucket + File.separator + scope + File.separator + collection;
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
            cluster = Cluster.connect(
                    adjustClusterProtocol(clusterUrl, ssl),
                    ClusterOptions.clusterOptions(username, password).environment(env -> {
                        //env.applyProfile("wan-development");
                    })
            );
            cluster.waitUntilReady(Duration.ofSeconds(5));


            return cluster.buckets().getAllBuckets().keySet();
        } catch (Exception e ){
            cluster.disconnect();
            throw e;
        }

    }

    public static SavedCluster saveDatabaseCredentials(String name, String url, boolean isSSL, String username, String password, String defaultBucket) {
        String key = username + ":" + name;
        SavedCluster sc = new SavedCluster();
        sc.setId(key);
        sc.setName(name);
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

        if (ClustersStorage.getInstance().getValue().getMap() == null) {
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
