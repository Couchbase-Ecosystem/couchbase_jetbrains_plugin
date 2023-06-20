package com.couchbase.intellij.database;

import com.couchbase.client.core.error.PlanningFailureException;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.client.java.manager.query.QueryIndex;
import com.couchbase.intellij.VirtualFileKeys;
import com.couchbase.intellij.persistence.ClusterAlreadyExistsException;
import com.couchbase.intellij.persistence.Clusters;
import com.couchbase.intellij.persistence.DuplicatedClusterNameAndUserException;
import com.couchbase.intellij.persistence.SavedCluster;
import com.couchbase.intellij.persistence.storage.ClustersStorage;
import com.couchbase.intellij.persistence.storage.PasswordStorage;
import com.couchbase.intellij.persistence.storage.QueryFiltersStorage;
import com.couchbase.intellij.tree.node.*;
import com.couchbase.intellij.workbench.Log;
import com.couchbase.intellij.workbench.SQLPPQueryUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.ui.treeStructure.Tree;
import org.intellij.sdk.language.SQLPPFormatter;
import utils.IndexUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.couchbase.intellij.VirtualFileKeys.READ_ONLY;

public class DataLoader {

    public static void listBuckets(DefaultMutableTreeNode parentNode, Tree tree) {

        Object userObject = parentNode.getUserObject();
        tree.setPaintBusy(true);
        if (userObject instanceof ConnectionNodeDescriptor) {
            CompletableFuture.runAsync(() -> {
                try {
                    Set<String> buckets = ActiveCluster.getInstance().get().buckets().getAllBuckets().keySet();
                    parentNode.removeAllChildren();

                    if (!buckets.isEmpty()) {
                        for (String bucket : buckets) {

                            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new BucketNodeDescriptor(bucket, ActiveCluster.getInstance().getId()));
                            childNode.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
                            parentNode.add(childNode);
                        }
                    } else {
                        parentNode.add(new DefaultMutableTreeNode(new NoResultsNodeDescriptor()));
                    }

                    ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
                } catch (Exception e) {
                    Log.error(e);
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
                    List<ScopeSpec> scopes = ActiveCluster.getInstance().get().bucket(bucketName).collections().getAllScopes();
                    parentNode.removeAllChildren();

                    if (!scopes.isEmpty()) {

                        for (ScopeSpec scopeSpec : scopes) {
                            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new ScopeNodeDescriptor(scopeSpec.name(), ActiveCluster.getInstance().getId(), bucketName));

//                            DefaultMutableTreeNode collections = new DefaultMutableTreeNode(
//                                    new CollectionsNodeDescriptor(ActiveCluster.getInstance().getId(), bucketName,
//                                            scopeSpec.name()));
//                            collections.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
                            childNode.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));

                            parentNode.add(childNode);
                        }
                    } else {
                        parentNode.add(new DefaultMutableTreeNode(new NoResultsNodeDescriptor()));
                    }

                    ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
                } catch (Exception e) {
                    Log.error(e);
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
        if (userObject instanceof ScopeNodeDescriptor) {
            CompletableFuture.runAsync(() -> {
                try {
                    parentNode.removeAllChildren();
                    ScopeNodeDescriptor cols = (ScopeNodeDescriptor) userObject;

                    List<CollectionSpec> collections = ActiveCluster.getInstance().get().bucket(cols.getBucket()).collections().getAllScopes().stream().filter(scope -> scope.name().equals(cols.getText())).flatMap(scope -> scope.collections().stream()).collect(Collectors.toList());

                    if (!collections.isEmpty()) {
                        for (CollectionSpec spec : collections) {

                            String filter = QueryFiltersStorage.getInstance().getValue().getQueryFilter(ActiveCluster.getInstance().getId(), cols.getBucket(), cols.getText(), spec.name());

                            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new CollectionNodeDescriptor(spec.name(), ActiveCluster.getInstance().getId(), cols.getBucket(), cols.getText(), filter));

                            childNode.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
                            parentNode.add(childNode);
                        }
                    } else {
                        parentNode.add(new DefaultMutableTreeNode(new NoResultsNodeDescriptor()));
                    }
                    ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
                } catch (Exception e) {
                    Log.error(e);
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

                    DefaultMutableTreeNode indexes = new DefaultMutableTreeNode(new IndexesNodeDescriptor(colNode.getBucket(), colNode.getScope(), colNode.getText()));
                    indexes.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
                    parentNode.add(indexes);
                } else {
                    parentNode.remove(parentNode.getChildCount() - 1);
                }


                String filter = colNode.getQueryFilter();
                String query = "Select meta(couchbaseAlias).id as cbFileNameId, meta(couchbaseAlias).cas as cbCasNb, couchbaseAlias.* from `"

                        + colNode.getText() + "` as couchbaseAlias " + ((filter == null || filter.isEmpty()) ? "" : (" where " + filter)) + (SQLPPQueryUtils.hasOrderBy(filter) ? "" : "  order by meta(couchbaseAlias).id ") + (newOffset == 0 ? "" : " OFFSET " + newOffset) + " limit 10";

                final List<JsonObject> results = ActiveCluster.getInstance().get().bucket(colNode.getBucket()).scope(colNode.getScope()).query(query).rowsAsObject();

                if (!results.isEmpty()) {
                    ApplicationManager.getApplication().runWriteAction(() -> {
                        PsiDirectory psiDirectory = findOrCreateFolder(project, ActiveCluster.getInstance().getId(), colNode.getBucket(), colNode.getScope(), colNode.getText());

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
                                psiFile = psiDirectory.getManager().findDirectory(psiDirectory.getVirtualFile()).createFile(fileName);
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
                        DefaultMutableTreeNode loadMoreNode = new DefaultMutableTreeNode(new LoadMoreNodeDescriptor(colNode.getBucket(), colNode.getScope(), colNode.getText(), newOffset + 10));
                        parentNode.add(loadMoreNode);
                    }
                } else if (newOffset == 0) {
                    parentNode.add(new DefaultMutableTreeNode(new NoResultsNodeDescriptor()));
                }
                ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
            } catch (PlanningFailureException e) {
                parentNode.removeAllChildren();
                MissingIndexNodeDescriptor idx = new MissingIndexNodeDescriptor(colNode.getBucket(), colNode.getScope(), colNode.getText());
                parentNode.add(new DefaultMutableTreeNode(idx));
                ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
            } catch (Exception e) {
                Log.error(e);
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

                    CollectionNodeDescriptor colNode = (CollectionNodeDescriptor) ((DefaultMutableTreeNode) parentNode.getParent()).getUserObject();
                    String collectionName = colNode.getText();
                    String scopeName = colNode.getScope();
                    String bucketName = colNode.getBucket();

                    JsonObject inferenceQueryResults = InferHelper.inferSchema(collectionName, scopeName, bucketName);

                    if (inferenceQueryResults != null) {
                        JsonArray array = inferenceQueryResults.getArray("content");
                        InferHelper.extractArray(parentNode, array);
                    } else {
                        System.err.println("Could not infer the schema for " + colNode.getText());
                    }

                    treeModel.nodeStructureChanged(parentNode);
                } catch (Exception e) {
                    Log.error(e);
                    e.printStackTrace();
                } finally {
                    tree.setPaintBusy(false);
                }
            });
        } else {
            throw new IllegalStateException("The expected parent was SchemaNodeDescriptor but got something else");
        }
    }

    private static PsiDirectory findOrCreateFolder(Project project, String connection, String bucket, String scope, String collection) {

        String basePath = project.getBasePath();
        VirtualFile baseDirectory = LocalFileSystem.getInstance().findFileByPath(basePath);

        try {
            String dirPath = connection + File.separator + bucket + File.separator + scope + File.separator + collection;
            VirtualFile directory = VfsUtil.createDirectoryIfMissing(baseDirectory, dirPath);
            return PsiManager.getInstance(project).findDirectory(directory);

        } catch (IOException e) {
            Log.error(e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public static String adjustClusterProtocol(String cluster, boolean ssl) {
        if (cluster.startsWith("couchbase://") || cluster.startsWith("couchbases://")) {
            return cluster;
        }

        String protocol;
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
            cluster = Cluster.connect(adjustClusterProtocol(clusterUrl, ssl), ClusterOptions.clusterOptions(username, password).environment(env -> {
                // env.applyProfile("wan-development");
            }));
            cluster.waitUntilReady(Duration.ofSeconds(5));

            return cluster.buckets().getAllBuckets().keySet();
        } catch (Exception e) {
            Log.error(e);
            assert cluster != null;
            cluster.disconnect();
            throw e;
        }

    }

    public static SavedCluster saveDatabaseCredentials(String name, String url, boolean isSSL, String username, String password, String defaultBucket) {
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
        if (ClustersStorage.getInstance().getValue() == null || ClustersStorage.getInstance().getValue().getMap() == null) {
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

    public static void createPrimaryIndex(String bucket, String scope, String collection) {

        CompletableFuture.runAsync(() -> {
            try {
                ActiveCluster.getInstance().get().bucket(bucket).scope(scope).collection(collection).queryIndexes().createPrimaryIndex();

                SwingUtilities.invokeLater(() -> Messages.showInfoMessage("The primary index for the collection " + bucket + "." + scope + "." + collection + " was created successfully.", "Primary Index Creation"));
            } catch (Exception e) {
                Log.error(e);
                e.printStackTrace();
            }
        });
    }

    public static void listIndexes(DefaultMutableTreeNode parentNode, Tree tree) {
        Object userObject = parentNode.getUserObject();
        tree.setPaintBusy(true);
        if (userObject instanceof IndexesNodeDescriptor) {
            IndexesNodeDescriptor idxs = (IndexesNodeDescriptor) userObject;
            parentNode.removeAllChildren();

            List<QueryIndex> results = ActiveCluster.getInstance().get().bucket(idxs.getBucket()).scope(idxs.getScope()).collection(idxs.getCollection()).queryIndexes().getAllIndexes();

            if (!results.isEmpty()) {
                for (QueryIndex qi : results) {
                    String fileName = qi.name() + ".sqlpp";
                    VirtualFile virtualFile = new LightVirtualFile(fileName,
                            FileTypeManager.getInstance().getFileTypeByExtension("sqlpp"), SQLPPFormatter.format(IndexUtils.getIndexDefinition(qi)));
                    virtualFile.putUserData(READ_ONLY, "true");

                    IndexNodeDescriptor node = new IndexNodeDescriptor(idxs.getBucket(), idxs.getScope(), idxs.getCollection(), fileName, virtualFile);
                    DefaultMutableTreeNode jsonFileNode = new DefaultMutableTreeNode(node);
                    parentNode.add(jsonFileNode);
                }
            } else {
                parentNode.add(new DefaultMutableTreeNode(new NoResultsNodeDescriptor()));
            }
            ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
        } else {
            throw new IllegalStateException("The expected parent was IndexesNodeDescriptor but got something else");
        }
    }

    public static List<QueryIndex> listIndexes(String bucket, String scope, String collection) {
        return ActiveCluster.getInstance().get().bucket(bucket).scope(scope).collection(collection).queryIndexes().getAllIndexes();
    }

    public static String getDocMetadata(String bucket, String scope, String collection, String docId) {

        try {
            String query = "Select meta().* from `" + collection + "` use keys \"" + docId + "\"";
            final List<JsonObject> results = ActiveCluster.getInstance().get().bucket(bucket).scope(scope).query(query).rowsAsObject();

            return results.get(0).toString();
        } catch (Exception e) {
            e.printStackTrace();
            Log.error("Failed to load the metadata for document " + docId, e);
            return null;
        }
    }
}
