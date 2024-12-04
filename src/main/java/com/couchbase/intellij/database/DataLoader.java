package com.couchbase.intellij.database;

import com.couchbase.client.core.env.PasswordAuthenticator;
import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.core.error.IndexFailureException;
import com.couchbase.client.core.error.PlanningFailureException;
import com.couchbase.client.core.error.TimeoutException;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.client.java.manager.query.QueryIndex;
import com.couchbase.client.java.manager.search.SearchIndex;
import com.couchbase.client.java.manager.search.SearchIndexManager;
import com.couchbase.intellij.VirtualFileKeys;
import com.couchbase.intellij.database.entity.CouchbaseCollection;
import com.couchbase.intellij.persistence.*;
import com.couchbase.intellij.persistence.storage.ClustersStorage;
import com.couchbase.intellij.persistence.storage.PasswordStorage;
import com.couchbase.intellij.persistence.storage.QueryFiltersStorage;
import com.couchbase.intellij.persistence.storage.RelationshipStorage;
import com.couchbase.intellij.tree.RelationshipSettingsManager;
import com.couchbase.intellij.tree.docfilter.QueryFilterUtil;
import com.couchbase.intellij.tree.node.*;
import com.couchbase.intellij.tree.overview.apis.CouchbaseRestAPI;
import com.couchbase.intellij.workbench.Log;
import com.couchbase.intellij.workbench.SQLPPQueryUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.UserBinaryFileType;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.ui.treeStructure.Tree;
import org.intellij.sdk.language.SQLPPFormatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import utils.IndexUtils;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.couchbase.intellij.VirtualFileKeys.READ_ONLY;

public class DataLoader {

    public static void listBuckets(DefaultMutableTreeNode parentNode, Tree tree) {

        Object userObject = parentNode.getUserObject();

        if (userObject instanceof ConnectionNodeDescriptor) {
            CompletableFuture.runAsync(() -> {
                try {
                    if (ActiveCluster.getInstance().get() == null) {
                        return;
                    }
                    tree.setPaintBusy(true);
                    Set<String> buckets = ActiveCluster.getInstance().get().buckets().getAllBuckets().keySet();
                    parentNode.removeAllChildren();

                    if (!buckets.isEmpty()) {
                        for (String bucket : buckets) {

                            //NOTE: if the user has a travel-sample bucket and no relationships yet, we add the relationships
                            //from travel-sample
                            if ("travel-sample".equals(bucket) && RelationshipStorage.getInstance().getValue().getRelationships().get(ActiveCluster.getInstance().getId()) == null) {
                                RelationshipSettingsManager.populateTravelSample();
                            }

                            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new BucketNodeDescriptor(bucket, ActiveCluster.getInstance().getId()));
                            childNode.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
                            parentNode.add(childNode);
                        }
                    } else {
                        parentNode.add(new DefaultMutableTreeNode(new NoResultsNodeDescriptor()));
                    }

                    ApplicationManager.getApplication().invokeLater(() -> {
                        ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
                    });

                } catch (Exception e) {
                    Log.error(e);
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
        if (userObject instanceof BucketNodeDescriptor) {
            BucketNodeDescriptor node = (BucketNodeDescriptor) userObject;
            CompletableFuture.runAsync(() -> {
                try {
                    tree.setPaintBusy(true);
                    String bucketName = node.getText();
                    List<ScopeSpec> scopes = ActiveCluster.getInstance().get().bucket(bucketName).collections().getAllScopes();
                    parentNode.removeAllChildren();
                    node.setCounter(formatCount(scopes.size()));

                    if (!scopes.isEmpty()) {

                        for (ScopeSpec scopeSpec : scopes) {
                            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new ScopeNodeDescriptor(scopeSpec.name(), ActiveCluster.getInstance().getId(), bucketName));

                            if (ActiveCluster.getInstance().hasSearchService()) {
                                DefaultMutableTreeNode search = new DefaultMutableTreeNode(new SearchNodeDescriptor(scopeSpec.name(), bucketName));
                                search.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
                                childNode.add(search);
                            }

                            DefaultMutableTreeNode collections = new DefaultMutableTreeNode(new CollectionsNodeDescriptor(scopeSpec.name(), bucketName));
                            collections.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
                            childNode.add(collections);

                            parentNode.add(childNode);

                        }
                    } else {
                        parentNode.add(new DefaultMutableTreeNode(new NoResultsNodeDescriptor()));

                    }
                    ApplicationManager.getApplication().invokeLater(() -> {
                        ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
                    });

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
        if (userObject instanceof CollectionsNodeDescriptor) {
            CompletableFuture.runAsync(() -> {
                tree.setPaintBusy(true);
                try {
                    parentNode.removeAllChildren();
                    CollectionsNodeDescriptor colsDesc = (CollectionsNodeDescriptor) userObject;
                    Map<String, Integer> counts = CouchbaseRestAPI.getCollectionCounts(colsDesc.getBucket(), colsDesc.getScope());

                    List<CollectionSpec> collections = ActiveCluster.getInstance().get().bucket(colsDesc.getBucket()).collections()
                            .getAllScopes().stream().filter(scope -> scope.name().equals(colsDesc.getScope())).flatMap(scope -> scope.collections().stream()).toList();

                    ((ScopeNodeDescriptor) ((DefaultMutableTreeNode) parentNode.getParent()).getUserObject()).setCounter(formatCount(collections.size()));

                    if (!collections.isEmpty()) {
                        for (CollectionSpec spec : collections) {

                            QueryFilter filter = QueryFilterUtil.getQueryFilter(colsDesc.getBucket(), colsDesc.getScope(), spec.name());
                            CollectionNodeDescriptor colNodeDesc = new CollectionNodeDescriptor(spec.name(),
                                    ActiveCluster.getInstance().getId(), colsDesc.getBucket(), colsDesc.getScope(), filter);
                            colNodeDesc.setCounter(formatCount(counts.get(colsDesc.getBucket() + "." + colsDesc.getScope() + "." + spec.name())));
                            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(colNodeDesc);
                            childNode.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
                            parentNode.add(childNode);
                        }
                    } else {
                        parentNode.add(new DefaultMutableTreeNode(new NoResultsNodeDescriptor()));
                    }
                    ApplicationManager.getApplication().invokeLater(() -> {
                        ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
                    });

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

    private static boolean hasKVFilter(QueryFilter filter) {

        if(filter == null || filter.getQuery() != null) {
            return false;
        } if( (filter.getDocumentStartKey() != null && !filter.getDocumentStartKey().isEmpty())
                || (filter.getDocumentEndKey() != null && !filter.getDocumentEndKey().isEmpty())
                || filter.getOffset() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static void listDocuments(DefaultMutableTreeNode parentNode, Tree tree, int newOffset) {
        Object userObject = parentNode.getUserObject();

        if (userObject instanceof CollectionNodeDescriptor) {
            CollectionNodeDescriptor colNode = (CollectionNodeDescriptor) parentNode.getUserObject();
            try {
                tree.setPaintBusy(true);
                //When KV
                if (!ActiveCluster.getInstance().hasQueryService() ) {
                    if (newOffset == 0) {
                        //removed loading node
                        parentNode.removeAllChildren();

                        if(colNode.getQueryFilter() != null && colNode.getQueryFilter().getOffset() > 0) {
                            newOffset = colNode.getQueryFilter().getOffset();
                        }
                    } else {
                        //removes "Load More" node
                        parentNode.remove(parentNode.getChildCount() - 1);
                    }
                    loadKVDocuments(parentNode, tree, newOffset, colNode);
                } else {
                    if (newOffset == 0) {
                        parentNode.removeAllChildren();
                        DefaultMutableTreeNode schemaNode = new DefaultMutableTreeNode(new SchemaNodeDescriptor());
                        schemaNode.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
                        parentNode.add(schemaNode);

                        DefaultMutableTreeNode indexes = new DefaultMutableTreeNode(new IndexesNodeDescriptor(colNode.getBucket(), colNode.getScope(), colNode.getText()));
                        indexes.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
                        parentNode.add(indexes);

                        if(colNode.getQueryFilter() != null && colNode.getQueryFilter().getOffset() > 0) {
                            newOffset = colNode.getQueryFilter().getOffset();
                        }
                    } else {
                        //removes "Load More" node
                        parentNode.remove(parentNode.getChildCount() - 1);
                    }


                    //selects the attribute that needs to be used according to the index
                    String idxField = getIndexedField(colNode);

                    if(hasKVFilter(colNode.getQueryFilter()) || idxField == null) {
                        loadKVDocuments(parentNode, tree, newOffset, colNode);
                    } else {
                        String filter = colNode.getQueryFilter() != null ? colNode.getQueryFilter().getQuery() : null;
                        String query = "Select meta(couchbaseAlias).id as cbFileNameId, meta(couchbaseAlias).type as cbMetaType  from `" + colNode.getText() + "` as couchbaseAlias WHERE " + idxField + " IS NOT MISSING "
                                + ((filter == null || filter.isEmpty()) ? "" : (" and " + filter)) + (SQLPPQueryUtils.hasOrderBy(filter) ? "" : "  order by meta(couchbaseAlias).id ") + (newOffset == 0 ? "" : " OFFSET " + newOffset) + " limit 10";

                        final List<JsonObject> results = ActiveCluster.getInstance().get().bucket(colNode.getBucket()).scope(colNode.getScope()).query(query).rowsAsObject();
                        InferHelper.invalidateInferCacheIfOlder(colNode.getBucket(), colNode.getScope(), colNode.getText(), TimeUnit.MINUTES.toMillis(5));

                        if (!results.isEmpty()) {
                            for (JsonObject obj : results) {
                                String docId = obj.getString("cbFileNameId");
                                String type = obj.getString("cbMetaType");
                                String fileName = docId + ".json";
                                FileNodeDescriptor.FileType fileType = "base64".equals(type) ? FileNodeDescriptor.FileType.BINARY : FileNodeDescriptor.FileType.JSON;
                                if (fileType == FileNodeDescriptor.FileType.BINARY) {
                                    fileName = docId;
                                }
                                FileNodeDescriptor node = new FileNodeDescriptor(fileName, colNode.getBucket(), colNode.getScope(), colNode.getText(), docId, fileType, null);
                                DefaultMutableTreeNode jsonFileNode = new DefaultMutableTreeNode(node);
                                parentNode.add(jsonFileNode);
                            }

                            if (results.size() == 10) {
                                DefaultMutableTreeNode loadMoreNode = new DefaultMutableTreeNode(new LoadMoreNodeDescriptor(colNode.getBucket(), colNode.getScope(), colNode.getText(), newOffset + 10));
                                parentNode.add(loadMoreNode);
                            }
                        } else if (newOffset == 0) {
                            parentNode.add(new DefaultMutableTreeNode(new NoResultsNodeDescriptor()));
                        }
                        ApplicationManager.getApplication().invokeLater(() -> {
                            ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
                        });
                    }
                }

            } catch (Exception e) {
                Log.error(e);
                e.printStackTrace();
            } finally {
                tree.setPaintBusy(false);
            }
        } else {
            throw new IllegalStateException("The expected parent was CollectionNodeDescriptor but got something else");
        }
    }

    public static String getIndexedField(CollectionNodeDescriptor colNode) {
        if(!ActiveCluster.getInstance().hasQueryService()) {
            return null;
        }
        List<QueryIndex> idxs = ActiveCluster.getInstance().get().bucket(colNode.getBucket()).scope(colNode.getScope()).collection(colNode.getText()).queryIndexes().getAllIndexes();
        String filter = null;
        for (QueryIndex idx : idxs) {
            if (idx.primary()) {
                return "meta(couchbaseAlias).id";
            } else {
                AbstractMap.SimpleEntry<String, Boolean> result = getValidIndexKey(idx.indexKey());
                if (result != null) {
                    if (!idx.condition().isPresent() && result.getValue()) {
                        return result.getKey();
                    } else {
                        filter = result.getKey();
                    }
                }
            }
        }
        return filter;
    }

    private static AbstractMap.SimpleEntry<String, Boolean> getValidIndexKey(JsonArray array) {
        for (int i = 0; i < array.size(); i++) {
            String key = array.getString(i);
            if (!key.contains("(")) {
                if (key.endsWith(" DESC") || key.endsWith(" ASC")) {
                    key = key.replace(" ASC", "").replace(" DESC", "").trim();
                }
                key = key.replaceAll("`", "");

                return new AbstractMap.SimpleEntry<>(key, i == 0);
            }
        }
        return null;
    }

    private static void loadKVDocuments(DefaultMutableTreeNode parentNode, Tree tree, int newOffset, CollectionNodeDescriptor colNode) throws Exception {
        String startKey = colNode.getQueryFilter()!=null?colNode.getQueryFilter().getDocumentStartKey(): null;
        String endKey = colNode.getQueryFilter()!=null?colNode.getQueryFilter().getDocumentEndKey(): null;

        List<String> docIds = CouchbaseRestAPI.listKVDocuments(colNode.getBucket(), colNode.getScope(), colNode.getText(), newOffset, 10, startKey, endKey);

        if (!docIds.isEmpty()) {
            for (String id : docIds) {
                FileNodeDescriptor node = new FileNodeDescriptor(id, colNode.getBucket(), colNode.getScope(),
                        colNode.getText(), id, FileNodeDescriptor.FileType.UNKNOWN, null);
                DefaultMutableTreeNode jsonFileNode = new DefaultMutableTreeNode(node);
                parentNode.add(jsonFileNode);
            }
            if (docIds.size() == 10) {
                DefaultMutableTreeNode loadMoreNode = new DefaultMutableTreeNode(new LoadMoreNodeDescriptor(colNode.getBucket(),
                        colNode.getScope(), colNode.getText(), newOffset + 10));
                parentNode.add(loadMoreNode);
            }

        } else if (newOffset == 0) {
            parentNode.add(new DefaultMutableTreeNode(new NoResultsNodeDescriptor()));
        }

        ApplicationManager.getApplication().invokeLater(() -> {
            ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
        });
    }

    public static boolean stubsAvailable(String bucket, String scope, String collection) {
        return ActiveCluster.getInstance()
                .getChild(bucket)
                .flatMap(b -> b.getChild(scope))
                .flatMap(s -> s.getChild(collection))
                .map(col -> ((CouchbaseCollection) col).generateDocument())
                .anyMatch(Objects::nonNull);
    }

    /**
     * Creates the file before it is opened.
     *
     * @param project the project where the folder will be created and the file will be stored
     * @param node    Node where the virtual file will be stored
     * @param tree    used to set the loading status
     */
    public static void loadDocument(Project project, FileNodeDescriptor node, @Nullable Tree tree) {
        if (tree != null) {
            tree.setPaintBusy(true);
        }

        if (node.getVirtualFile() != null) {
            return;
        }

        String docContent = "{}";
        String cas = null;
        boolean isDifferentFormat = false;

        try {
            GetResult result = ActiveCluster.getInstance().get().bucket(node.getBucket()).scope(node.getScope()).collection(node.getCollection()).get(node.getId());
            cas = String.valueOf(result.cas());

            docContent = new String(result.contentAsBytes());

            //checks if document us a valid Json
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.readTree(docContent);
            } catch (JsonProcessingException e) {
                isDifferentFormat = true;
            }

        } catch (DocumentNotFoundException dnf) {
            ApplicationManager.getApplication().invokeLater(() -> Messages.showInfoMessage("<html>The document <strong>" + node.getId() + "</strong> doesn't exists anymore.</html>", "Couchbase Plugin Error"));
            return;

        } catch (TimeoutException te) {
            te.printStackTrace();
            Log.error("Request to get the document " + node.getId() + " timed out.", te);
            ApplicationManager.getApplication().invokeLater(() -> Messages.showInfoMessage("<html>The request to get the document <strong>" + node.getId() + "</strong> timed out. Please try again or check your network connection.</html>", "Couchbase Plugin Error"));

            return;
        } catch (Exception e) {
            Log.error("Could not load the document " + node.getId() + ".", e);
            ApplicationManager.getApplication().invokeLater(() -> Messages.showInfoMessage("<html>Could not load the document <strong>" + node.getId() + "</strong>. Please check the log for more.</html>", "Couchbase Plugin Error"));
            return;
        } finally {
            if (tree != null) {
                tree.setPaintBusy(false);
            }
        }

        final boolean isBinary = isDifferentFormat;
        final String docCass = cas;
        try {
            ApplicationManager.getApplication().runWriteAction(() -> {
                final FileType type = isBinary ? UserBinaryFileType.INSTANCE : JsonFileType.INSTANCE;
                CouchbaseDocumentVirtualFile virtualFile = new CouchbaseDocumentVirtualFile(project, type, node.getBucket(), node.getScope(), node.getCollection(), node.getId());

                virtualFile.putUserData(VirtualFileKeys.CONN_ID, ActiveCluster.getInstance().getId());
                virtualFile.putUserData(VirtualFileKeys.CLUSTER, ActiveCluster.getInstance().getId());
                virtualFile.putUserData(VirtualFileKeys.BUCKET, node.getBucket());
                virtualFile.putUserData(VirtualFileKeys.SCOPE, node.getScope());
                virtualFile.putUserData(VirtualFileKeys.COLLECTION, node.getCollection());
                virtualFile.putUserData(VirtualFileKeys.ID, node.getId());
                virtualFile.putUserData(VirtualFileKeys.CAS, String.valueOf(docCass));
                if (isBinary) {
                    virtualFile.putUserData(READ_ONLY, String.valueOf(isBinary));
                }

                node.setVirtualFile(virtualFile);
            });
        } catch (Exception e) {
            if (tree != null) {
                tree.setPaintBusy(false);
            }
            Log.error("An error occurred while trying to load the file", e);
            ApplicationManager.getApplication().invokeLater(() -> Messages.showInfoMessage("<html>Could not load the document <strong>" + node.getId() + "</strong>. Please check the log for more.</html>", "Couchbase Plugin Error"));
        }
    }

    public static void showSchema(DefaultMutableTreeNode parentNode, DefaultTreeModel treeModel, Tree tree) {
        Object userObject = parentNode.getUserObject();

        if (userObject instanceof SchemaNodeDescriptor) {
            CompletableFuture.runAsync(() -> {

                try {
                    tree.setPaintBusy(true);
                    parentNode.removeAllChildren();

                    CollectionNodeDescriptor colNode = (CollectionNodeDescriptor) ((DefaultMutableTreeNode) parentNode.getParent()).getUserObject();
                    String collectionName = colNode.getText();
                    String scopeName = colNode.getScope();
                    String bucketName = colNode.getBucket();

                    ProgressManager.getInstance().run(new Task.Backgroundable(null, String.format("Running INFER for collection %s.%s.%s", bucketName, scopeName, collectionName), false) {

                        @Override
                        public void run(@NotNull ProgressIndicator indicator) {
                            JsonObject inferenceQueryResults = InferHelper.inferSchema(collectionName, scopeName, bucketName);
                            if (inferenceQueryResults != null) {
                                JsonArray array = inferenceQueryResults.getArray("content");
                                InferHelper.extractArray(parentNode, array, colNode.getBucket() + "." + colNode.getScope() + "." + colNode.getText());
                            } else {
                                Log.debug("Could not infer the schema for " + colNode.getText());
                            }

                            ApplicationManager.getApplication().invokeLater(() -> {
                                treeModel.nodeStructureChanged(parentNode);
                            });
                        }
                    });
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

    public static Set<String> listBucketNames(String clusterUrl, boolean ssl, String username, String password, boolean ldap) {

        Cluster cluster = null;
        try {

            ClusterOptions options;

            if (!ldap) {
                options = ClusterOptions.clusterOptions(username, password);
            } else {
                PasswordAuthenticator authenticator = PasswordAuthenticator.builder().username(username).password(password).onlyEnablePlainSaslMechanism().build();

                options = ClusterOptions.clusterOptions(authenticator);
            }

            cluster = Cluster.connect(adjustClusterProtocol(clusterUrl, ssl), options.environment(env -> {
                //env.applyProfile("wan-development");
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

    public static SavedCluster saveDatabaseCredentials(String name, String url, String queryParams, boolean isSSL, String username, String password, String defaultBucket, Boolean ldap) {
        String key = username + ":" + name;
        SavedCluster sc = new SavedCluster();
        sc.setId(key);
        sc.setName(name);
        sc.setQueryParams(queryParams);
        sc.setSslEnable(isSSL);
        sc.setUsername(username);
        sc.setUrl(adjustClusterProtocol(url, isSSL));
        sc.setDefaultBucket(defaultBucket);
        sc.setLDAP(ldap);

        Clusters clusters = ClustersStorage.getInstance().getValue();

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

                ApplicationManager.getApplication().invokeLater(() -> Messages.showInfoMessage("The primary index for the collection " + bucket + "." + scope + "." + collection + " was created successfully.", "Primary Index Creation"));
            } catch (Exception e) {
                Log.error(e);
                e.printStackTrace();
            }
        });
    }

    public static void listIndexes(DefaultMutableTreeNode parentNode, Tree tree) {
        Object userObject = parentNode.getUserObject();

        if (userObject instanceof IndexesNodeDescriptor) {
            tree.setPaintBusy(true);
            IndexesNodeDescriptor idxs = (IndexesNodeDescriptor) userObject;
            parentNode.removeAllChildren();

            List<QueryIndex> results = ActiveCluster.getInstance().get().bucket(idxs.getBucket()).scope(idxs.getScope()).collection(idxs.getCollection()).queryIndexes().getAllIndexes();

            if (!results.isEmpty()) {
                for (QueryIndex qi : results) {
                    String fileName = qi.name() + ".sqlpp";
                    VirtualFile virtualFile = new LightVirtualFile(fileName, FileTypeManager.getInstance().getFileTypeByExtension("sqlpp"), SQLPPFormatter.format(IndexUtils.getIndexDefinition(qi, false)));
                    virtualFile.putUserData(READ_ONLY, "true");

                    IndexNodeDescriptor node = new IndexNodeDescriptor(idxs.getBucket(), idxs.getScope(), idxs.getCollection(), fileName, virtualFile);
                    DefaultMutableTreeNode jsonFileNode = new DefaultMutableTreeNode(node);
                    parentNode.add(jsonFileNode);
                }
            } else {
                parentNode.add(new DefaultMutableTreeNode(new NoResultsNodeDescriptor()));
            }
            ApplicationManager.getApplication().invokeLater(() -> {
                ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
                tree.setPaintBusy(false);
            });
        } else {
            throw new IllegalStateException("The expected parent was IndexesNodeDescriptor but got something else");
        }
    }

    public static List<QueryIndex> listIndexes(String bucket, String scope, String collection) {
        if (ActiveCluster.getInstance().hasQueryService()) {
            return ActiveCluster.getInstance().get().bucket(bucket).scope(scope).collection(collection).queryIndexes().getAllIndexes();
        } else {
            return new ArrayList<>();
        }

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

    public static String formatCount(Integer num) {
        if (num == null) {
            return "?";
        }
        if (num < 1000) {
            return String.valueOf(num);
        } else if (num < 1000000) {
            return String.format("%.1fk", num / 1000.0);
        } else {
            return String.format("%.2fM", num / 1000000.0);
        }
    }


    public static void listSearchIndexes(DefaultMutableTreeNode parentNode, Tree tree) {
        Object userObject = parentNode.getUserObject();
        if (userObject instanceof SearchNodeDescriptor) {
            CompletableFuture.runAsync(() -> {
                tree.setPaintBusy(true);
                parentNode.removeAllChildren();

                try {
                    SearchNodeDescriptor searchDesc = (SearchNodeDescriptor) userObject;

                    SearchIndexManager topSearch = ActiveCluster.getInstance().get().searchIndexes();
                    List<SearchIndex> allIdx = topSearch.getAllIndexes();

                    List<SearchIndex> results = new ArrayList<>();

                    for (SearchIndex idx : allIdx) {
                        if (!searchDesc.getBucket().equals(idx.sourceName())) {
                            continue;
                        }

                        Map<String, Object> mapping = (Map<String, Object>) idx.params().get("mapping");
                        Map<String, Object> types = (Map<String, Object>) mapping.get("types");
                        Map<String, Object> docConfig = (Map<String, Object>) idx.params().get("doc_config");
                        String mode = docConfig.get("mode").toString();

                        if ("type_field".equals(mode) && "_default".equals(searchDesc.getScope())) {
                            results.add(idx);
                        } else if (!"type_field".equals(mode) && !types.keySet().stream()
                                .filter(e -> e.startsWith(searchDesc.getScope() + "."))
                                .collect(Collectors.toSet()).isEmpty()) {
                            results.add(idx);
                        }
                    }

                    if (!results.isEmpty()) {
                        for (SearchIndex searchIndex : results) {

                            String fileName = searchIndex.name() + ".json";
                            VirtualFile virtualFile = new LightVirtualFile(fileName, JsonFileType.INSTANCE, searchIndex.toJson());
                            virtualFile.putUserData(READ_ONLY, "true");

                            SearchIndexNodeDescriptor node = new SearchIndexNodeDescriptor(searchIndex.name(), searchDesc.getBucket(), searchDesc.getScope(), fileName, virtualFile);
                            DefaultMutableTreeNode jsonFileNode = new DefaultMutableTreeNode(node);
                            parentNode.add(jsonFileNode);
                        }
                    } else {
                        parentNode.add(new DefaultMutableTreeNode(new NoResultsNodeDescriptor()));
                    }
                    ApplicationManager.getApplication().invokeLater(() -> {
                        ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
                        tree.setPaintBusy(false);
                    });

                } catch (Exception e) {
                    Log.error(e);
                } finally {
                    tree.setPaintBusy(false);
                }
            });
        }
    }
}
