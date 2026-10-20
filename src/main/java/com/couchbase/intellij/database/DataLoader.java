package com.couchbase.intellij.database;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.client.java.manager.query.QueryIndex;
import com.couchbase.client.java.manager.search.SearchIndex;
import com.couchbase.intellij.VirtualFileKeys;
import com.couchbase.intellij.database.collections.CollectionListing;
import com.couchbase.intellij.database.collections.CollectionSources;
import com.couchbase.intellij.database.collections.CollectionSource;
import com.couchbase.intellij.database.entity.CouchbaseCollection;
import com.couchbase.intellij.persistence.*;
import com.couchbase.intellij.persistence.storage.ClustersStorage;
import com.couchbase.intellij.persistence.storage.FavoriteCollectionsStorage;
import com.couchbase.intellij.persistence.storage.PasswordStorage;
import com.couchbase.intellij.persistence.storage.RelationshipStorage;
import com.couchbase.intellij.tree.CollectionTreePopulator;
import com.couchbase.intellij.tree.TreeLoadTask;
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
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.ui.treeStructure.Tree;
import org.intellij.sdk.language.SQLPPFormatter;
import org.jetbrains.annotations.Nullable;
import utils.IndexUtils;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.couchbase.intellij.VirtualFileKeys.READ_ONLY;

public class DataLoader {

    private static boolean deferToUi(Runnable request) {
        if (ApplicationManager.getApplication().isDispatchThread()) return false;
        ApplicationManager.getApplication().invokeLater(request);
        return true;
    }

    public static void listBuckets(DefaultMutableTreeNode parentNode, Tree tree) {
        if (deferToUi(() -> listBuckets(parentNode, tree))) return;
        if (!(parentNode.getUserObject() instanceof ConnectionNodeDescriptor connection))
            throw new IllegalStateException("Expected a connection node");
        ActiveCluster active = ActiveCluster.getInstance();
        Cluster sdk = active.get();
        TreeLoadTask.load(parentNode, tree, active, "buckets", () -> true, () -> {
            Set<String> names = sdk.buckets().getAllBuckets().keySet();
            if (names.contains("travel-sample") && RelationshipStorage.getInstance().getValue().getRelationships().get(active.getId()) == null)
                RelationshipSettingsManager.populateTravelSample();
            return names.stream().sorted(String.CASE_INSENSITIVE_ORDER).toList();
        }, names -> {
            parentNode.removeAllChildren();
            for (String name : names) {
                var child = new DefaultMutableTreeNode(new BucketNodeDescriptor(name, active.getId()));
                child.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
                parentNode.add(child);
            }
            if (names.isEmpty()) parentNode.add(new DefaultMutableTreeNode(new NoResultsNodeDescriptor("No buckets")));
        }, () -> listBuckets(parentNode, tree));
    }

    public static void listScopes(DefaultMutableTreeNode parentNode, Tree tree) {
        if (deferToUi(() -> listScopes(parentNode, tree))) return;
        if (!(parentNode.getUserObject() instanceof BucketNodeDescriptor bucket))
            throw new IllegalStateException("Expected a bucket node");
        ActiveCluster active = ActiveCluster.getInstance();
        Cluster sdk = active.get();
        boolean searchService = active.hasSearchService();
        TreeLoadTask.load(parentNode, tree, active, "scopes", () -> true,
                () -> sdk.bucket(bucket.getText()).collections().getAllScopes().stream()
                        .sorted(Comparator.comparing(ScopeSpec::name, String.CASE_INSENSITIVE_ORDER)).toList(), scopes -> {
            parentNode.removeAllChildren();
            bucket.setCounter(formatCount(scopes.size()));
            for (ScopeSpec scope : scopes) {
                var child = new DefaultMutableTreeNode(new ScopeNodeDescriptor(scope.name(), active.getId(), bucket.getText()));
                if (searchService) {
                    var search = new DefaultMutableTreeNode(new SearchNodeDescriptor(scope.name(), bucket.getText()));
                    search.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
                    child.add(search);
                }
                var collections = new DefaultMutableTreeNode(new CollectionsNodeDescriptor(scope.name(), bucket.getText()));
                collections.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
                child.add(collections);
                parentNode.add(child);
            }
            if (scopes.isEmpty()) parentNode.add(new DefaultMutableTreeNode(new NoResultsNodeDescriptor("No scopes")));
        }, () -> listScopes(parentNode, tree));
    }

    public static void listCollections(DefaultMutableTreeNode parentNode, Tree tree) {
        listCollections(parentNode, tree, CollectionListing.PAGE_SIZE);
    }

    /**
     * Lists the collections of the scope, {@code pagedToLoad} alphabetical entries at a time (favorites and
     * collections pulled in by search are shown on top of those).
     */
    public static void listCollections(DefaultMutableTreeNode parentNode, Tree tree, int pagedToLoad) {
        if (deferToUi(() -> listCollections(parentNode, tree, pagedToLoad))) return;
        if (!(parentNode.getUserObject() instanceof CollectionsNodeDescriptor descriptor))
            throw new IllegalStateException("Expected a collections node");
        long generation = descriptor.beginLoad();
        ActiveCluster active = ActiveCluster.getInstance();
        String connectionId = active.getId();
        FavoriteCollections favorites = FavoriteCollectionsStorage.getInstance().getValue();
        List<String> favoriteNames = List.copyOf(favorites.getFavorites(connectionId, descriptor.getBucket(), descriptor.getScope()));
        List<String> searchAdded = List.copyOf(descriptor.getSearchAdded());
        CollectionSource source = CollectionSources.forScope(descriptor.getBucket(), descriptor.getScope());
        TreeLoadTask.load(parentNode, tree, active, "collections", () -> descriptor.isCurrentLoad(generation), () -> {
            CollectionListing listing = CollectionListing.build(source, favoriteNames, searchAdded, pagedToLoad);
            Map<String, Integer> counts;
            try { counts = CouchbaseRestAPI.getCollectionCounts(descriptor.getBucket(), descriptor.getScope()); }
            catch (Exception unavailable) { Log.debug("Collection counts unavailable", unavailable); counts = Map.of(); }
            return new CollectionPage(listing, counts);
        }, page -> {
            CollectionTreePopulator.apply(parentNode, descriptor, page.listing(), favorites, connectionId, page.counts(),
                    name -> QueryFilterUtil.getQueryFilter(descriptor.getBucket(), descriptor.getScope(), name));
            if (parentNode.getParent() instanceof DefaultMutableTreeNode parent && parent.getUserObject() instanceof ScopeNodeDescriptor scope)
                scope.setCounter(formatCount(page.listing().getTotal()));
        }, () -> listCollections(parentNode, tree, pagedToLoad));
    }

    private record CollectionPage(CollectionListing listing, Map<String, Integer> counts) {}

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
        if (deferToUi(() -> listDocuments(parentNode, tree, newOffset))) return;
        if (!(parentNode.getUserObject() instanceof CollectionNodeDescriptor collection))
            throw new IllegalStateException("Expected a collection node");
        ActiveCluster active = ActiveCluster.getInstance();
        Cluster sdk = active.get();
        QueryFilter filter = collection.getQueryFilter();
        String queryFilter = filter == null ? null : filter.getQuery();
        String start = filter == null ? null : filter.getDocumentStartKey();
        String end = filter == null ? null : filter.getDocumentEndKey();
        int offset = newOffset == 0 && filter != null ? Math.max(0, filter.getOffset()) : newOffset;
        boolean queryService = active.hasQueryService();
        boolean useKvFilter = hasKVFilter(filter);
        boolean filtered = queryFilter != null && !queryFilter.isBlank() || start != null && !start.isBlank() || end != null && !end.isBlank();
        TreeLoadTask.load(parentNode, tree, active, "documents", () -> true, () -> {
            String indexedField = queryService && !useKvFilter ? indexedField(sdk, collection) : null;
            List<DocumentEntry> documents;
            if (!queryService || useKvFilter || indexedField == null) {
                documents = CouchbaseRestAPI.listKVDocuments(collection.getBucket(), collection.getScope(), collection.getText(), offset, 10, start, end)
                        .stream().map(id -> new DocumentEntry(id, FileNodeDescriptor.FileType.UNKNOWN)).toList();
            } else {
                String query = "Select meta(couchbaseAlias).id as cbFileNameId, meta(couchbaseAlias).type as cbMetaType from `"
                        + collection.getText().replace("`", "``") + "` as couchbaseAlias WHERE " + indexedField + " IS NOT MISSING "
                        + (queryFilter == null || queryFilter.isBlank() ? "" : " and " + queryFilter)
                        + (SQLPPQueryUtils.hasOrderBy(queryFilter) ? "" : " order by meta(couchbaseAlias).id ")
                        + (offset == 0 ? "" : " OFFSET " + offset) + " limit 10";
                documents = sdk.bucket(collection.getBucket()).scope(collection.getScope()).query(query).rowsAsObject().stream()
                        .map(row -> new DocumentEntry(row.getString("cbFileNameId"), "base64".equals(row.getString("cbMetaType"))
                                ? FileNodeDescriptor.FileType.BINARY : FileNodeDescriptor.FileType.JSON)).toList();
                if (ActiveCluster.getInstance() == active)
                    InferHelper.invalidateInferCacheIfOlder(collection.getBucket(), collection.getScope(), collection.getText(), TimeUnit.MINUTES.toMillis(5));
            }
            boolean emptyCollection = false;
            if (queryService && newOffset == 0 && documents.isEmpty()) {
                // An empty filtered/indexed page does not establish that the collection is empty.
                boolean unfilteredKvPage = indexedField == null && offset == 0
                        && (start == null || start.isBlank()) && (end == null || end.isBlank());
                if (unfilteredKvPage) emptyCollection = true;
                else {
                    try {
                        emptyCollection = CouchbaseRestAPI.listKVDocuments(collection.getBucket(), collection.getScope(),
                                collection.getText(), 0, 1, null, null).isEmpty();
                    } catch (Exception unavailable) {
                        // Keep Schema available when existence cannot be checked (for example, limited REST permissions).
                        Log.debug("Collection emptiness check unavailable", unavailable);
                    }
                }
            }
            return new DocumentPage(documents, emptyCollection);
        }, page -> {
            List<DocumentEntry> documents = page.documents();
            if (newOffset == 0) {
                parentNode.removeAllChildren();
                if (queryService) {
                    if (!page.emptyCollection()) {
                        var schema = new DefaultMutableTreeNode(new SchemaNodeDescriptor());
                        schema.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
                        parentNode.add(schema);
                    }
                    var indexes = new DefaultMutableTreeNode(new IndexesNodeDescriptor(collection.getBucket(), collection.getScope(), collection.getText()));
                    indexes.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
                    parentNode.add(indexes);
                }
            }
            Set<String> loaded = new HashSet<>();
            for (int i = 0; i < parentNode.getChildCount(); i++)
                if (((DefaultMutableTreeNode) parentNode.getChildAt(i)).getUserObject() instanceof FileNodeDescriptor file) loaded.add(file.getId());
            for (DocumentEntry document : documents) {
                if (!loaded.add(document.id())) continue;
                String name = document.type() == FileNodeDescriptor.FileType.JSON ? document.id() + ".json" : document.id();
                parentNode.add(new DefaultMutableTreeNode(new FileNodeDescriptor(name, collection.getBucket(), collection.getScope(), collection.getText(),
                        document.id(), document.type(), null)));
            }
            if (documents.size() == 10) parentNode.add(new DefaultMutableTreeNode(new LoadMoreNodeDescriptor(collection.getBucket(), collection.getScope(), collection.getText(), offset + 10)));
            else if (documents.isEmpty()) parentNode.add(new DefaultMutableTreeNode(new NoResultsNodeDescriptor(
                    offset > 0 ? "No more documents" : filtered ? "No matching documents" : "No documents")));
        }, () -> listDocuments(parentNode, tree, newOffset));
    }

    private record DocumentEntry(String id, FileNodeDescriptor.FileType type) {}
    private record DocumentPage(List<DocumentEntry> documents, boolean emptyCollection) {}

    public static String getIndexedField(CollectionNodeDescriptor collection) {
        if (!ActiveCluster.getInstance().hasQueryService()) return null;
        return indexedField(ActiveCluster.getInstance().get(), collection);
    }

    private static String indexedField(Cluster sdk, CollectionNodeDescriptor colNode) {
        List<QueryIndex> idxs = sdk.bucket(colNode.getBucket()).scope(colNode.getScope()).collection(colNode.getText()).queryIndexes().getAllIndexes();
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
    public static void loadDocument(Project project, FileNodeDescriptor node, @Nullable Tree tree,
                                    java.util.function.Consumer<CouchbaseDocumentVirtualFile> opened) {
        ActiveCluster active = ActiveCluster.getInstance();
        if (node.getVirtualFile() instanceof CouchbaseDocumentVirtualFile existing) {
            opened.accept(existing);
            return;
        }
        if (tree != null) tree.setPaintBusy(true);
        ClusterUiTask.submit(project, active, "Opening Couchbase document", connection ->
                readDocument(project, connection, node.getBucket(), node.getScope(), node.getCollection(), node.getId()), file -> {
            if (tree != null) tree.setPaintBusy(false);
            node.setVirtualFile(file);
            opened.accept(file);
        }, error -> {
            if (tree != null) tree.setPaintBusy(false);
            Messages.showErrorDialog(project, "Could not open the document. " + error.getMessage(), "Couchbase Document");
        });
    }

    /** Called on a worker; the returned VFS snapshot needs no network access to open in an editor. */
    public static CouchbaseDocumentVirtualFile readDocument(Project project, ClusterUiTask.Connection connection,
            String bucket, String scope, String collection, String id) throws Exception {
        GetResult result = connection.sdk().bucket(bucket).scope(scope).collection(collection).get(id);
        byte[] content = result.contentAsBytes();
        FileType type = JsonFileType.INSTANCE;
        try {
            ObjectMapper mapper = new ObjectMapper();
            content = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(mapper.readTree(content));
        } catch (JsonProcessingException invalidJson) {
            type = UserBinaryFileType.INSTANCE;
        }
        CouchbaseDocumentVirtualFile file = new CouchbaseDocumentVirtualFile(project, type, bucket, scope, collection, id);
        file.putUserData(VirtualFileKeys.CONN_ID, connection.id());
        file.putUserData(VirtualFileKeys.CLUSTER, connection.id());
        file.putUserData(VirtualFileKeys.BUCKET, bucket);
        file.putUserData(VirtualFileKeys.SCOPE, scope);
        file.putUserData(VirtualFileKeys.COLLECTION, collection);
        file.putUserData(VirtualFileKeys.ID, id);
        if (type != JsonFileType.INSTANCE) file.putUserData(READ_ONLY, "true");
        file.acceptServerContent(content, result.cas());
        return file;
    }

    public static void showSchema(DefaultMutableTreeNode parentNode, DefaultTreeModel treeModel, Tree tree) {
        if (deferToUi(() -> showSchema(parentNode, treeModel, tree))) return;
        CollectionNodeDescriptor collection = (CollectionNodeDescriptor) ((DefaultMutableTreeNode) parentNode.getParent()).getUserObject();
        ActiveCluster active = ActiveCluster.getInstance();
        TreeLoadTask.load(parentNode, tree, active, "schema", () -> true,
                () -> InferHelper.inferSchema(collection.getText(), collection.getScope(), collection.getBucket()), result -> {
            parentNode.removeAllChildren();
            if (result == null) throw new IllegalStateException("Schema inference did not return a result. Check the connection and retry.");
            InferHelper.extractArray(parentNode, result.getArray("content"), collection.getBucket() + "." + collection.getScope() + "." + collection.getText());
            if (parentNode.getChildCount() == 0) parentNode.add(new DefaultMutableTreeNode(new NoResultsNodeDescriptor("No schema available")));
        }, () -> showSchema(parentNode, treeModel, tree));
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

        SavedCluster settings = new SavedCluster();
        settings.setUrl(adjustClusterProtocol(clusterUrl, ssl));
        settings.setSslEnable(ssl);
        settings.setUsername(username);
        settings.setLDAP(ldap);
        return listBucketNames(settings, password);
    }

    public static Set<String> listBucketNames(SavedCluster settings, String secret) {
        Cluster cluster = Cluster.connect(settings.getUrl()
                + Objects.toString(settings.getQueryParams(), ""), ConnectionAuthentication.load(settings, secret).clusterOptions());
        try {
            cluster.waitUntilReady(Duration.ofSeconds(10));
            return new HashSet<>(cluster.buckets().getAllBuckets().keySet());
        } finally {
            cluster.disconnect();
        }
    }

    public static SavedCluster saveDatabaseCredentials(String name, String url, String queryParams, boolean isSSL, String username, String password, String defaultBucket, Boolean ldap, Set<String> options) {
        SavedCluster sc = new SavedCluster();
        sc.setName(name);
        sc.setQueryParams(queryParams);
        sc.setSslEnable(isSSL);
        sc.setUsername(username);
        sc.setUrl(adjustClusterProtocol(url, isSSL));
        sc.setDefaultBucket(defaultBucket);
        sc.setLDAP(ldap);
        sc.options(options);
        return saveDatabaseCredentials(sc, password, null);
    }

    /** Persist after a successful connection test; keep the old connection intact on validation failure. */
    public static SavedCluster saveDatabaseCredentials(SavedCluster sc, String secret, SavedCluster previous) {
        String identity = sc.usesCertificateAuthentication() ? "certificate" : sc.getUsername();
        sc.setId(identity + ":" + sc.getName());
        Clusters clusters = ClustersStorage.getInstance().getValue();
        for (SavedCluster existing : clusters.getMap().values()) {
            if (previous != null && Objects.equals(previous.getId(), existing.getId())) continue;
            if (Objects.equals(existing.getId(), sc.getId())) throw new DuplicatedClusterNameAndUserException();
            boolean sameIdentity = sc.usesCertificateAuthentication() == existing.usesCertificateAuthentication()
                    && (sc.usesCertificateAuthentication()
                        ? Objects.equals(sc.getClientKeyStorePath(), existing.getClientKeyStorePath())
                        : Objects.equals(sc.getUsername(), existing.getUsername()));
            if (Objects.equals(existing.getUrl(), sc.getUrl()) && sameIdentity) throw new ClusterAlreadyExistsException();
        }
        PasswordStorage.savePassword(sc, secret);
        if (previous != null && !Objects.equals(previous.getId(), sc.getId())) {
            deleteSavedCluster(previous);
        }
        clusters.getMap().put(sc.getId(), sc);
        ClustersStorage.getInstance().setValue(clusters);
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
        if (deferToUi(() -> listIndexes(parentNode, tree))) return;
        if (!(parentNode.getUserObject() instanceof IndexesNodeDescriptor indexes)) throw new IllegalStateException("Expected an indexes node");
        ActiveCluster active = ActiveCluster.getInstance();
        Cluster sdk = active.get();
        TreeLoadTask.load(parentNode, tree, active, "indexes", () -> true,
                () -> sdk.bucket(indexes.getBucket()).scope(indexes.getScope()).collection(indexes.getCollection()).queryIndexes().getAllIndexes(), results -> {
            parentNode.removeAllChildren();
            for (QueryIndex index : results) {
                String name = index.name() + ".sqlpp";
                VirtualFile file = new LightVirtualFile(name, FileTypeManager.getInstance().getFileTypeByExtension("sqlpp"), SQLPPFormatter.format(IndexUtils.getIndexDefinition(index, false)));
                file.putUserData(READ_ONLY, "true");
                parentNode.add(new DefaultMutableTreeNode(new IndexNodeDescriptor(indexes.getBucket(), indexes.getScope(), indexes.getCollection(), name, file)));
            }
            if (results.isEmpty()) parentNode.add(new DefaultMutableTreeNode(new NoResultsNodeDescriptor("No indexes")));
        }, () -> listIndexes(parentNode, tree));
    }

    public static List<QueryIndex> listIndexes(String bucket, String scope, String collection) {
        if (ActiveCluster.getInstance().hasQueryService()) {
            return ActiveCluster.getInstance().get().bucket(bucket).scope(scope).collection(collection).queryIndexes().getAllIndexes();
        } else {
            return new ArrayList<>();
        }

    }

    public static String getDocMetadata(ClusterUiTask.Connection connection, String bucket, String scope, String collection, String docId) {

        try {
            String query = "Select meta().* from `" + collection + "` use keys \"" + docId + "\"";
            final List<JsonObject> results = connection.sdk().bucket(bucket).scope(scope).query(query).rowsAsObject();

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
        if (deferToUi(() -> listSearchIndexes(parentNode, tree))) return;
        if (!(parentNode.getUserObject() instanceof SearchNodeDescriptor search)) throw new IllegalStateException("Expected a search indexes node");
        ActiveCluster active = ActiveCluster.getInstance();
        Cluster sdk = active.get();
        TreeLoadTask.load(parentNode, tree, active, "search indexes", () -> true, () -> {
            List<SearchIndex> results = new ArrayList<>();
            for (SearchIndex index : sdk.searchIndexes().getAllIndexes()) {
                if (!search.getBucket().equals(index.sourceName())) continue;
                Map<String, Object> mapping = (Map<String, Object>) index.params().get("mapping");
                Map<String, Object> types = mapping == null ? Map.of() : (Map<String, Object>) mapping.getOrDefault("types", Map.of());
                Map<String, Object> docConfig = (Map<String, Object>) index.params().get("doc_config");
                String mode = docConfig == null ? "" : Objects.toString(docConfig.get("mode"), "");
                if ("type_field".equals(mode) && "_default".equals(search.getScope())
                        || !"type_field".equals(mode) && types.keySet().stream().anyMatch(name -> name.startsWith(search.getScope() + "."))) results.add(index);
            }
            return results;
        }, results -> {
            parentNode.removeAllChildren();
            for (SearchIndex index : results) {
                String name = index.name() + ".json";
                VirtualFile file = new LightVirtualFile(name, JsonFileType.INSTANCE, index.toJson());
                file.putUserData(READ_ONLY, "true");
                parentNode.add(new DefaultMutableTreeNode(new SearchIndexNodeDescriptor(index.name(), search.getBucket(), search.getScope(), name, file)));
            }
            if (results.isEmpty()) parentNode.add(new DefaultMutableTreeNode(new NoResultsNodeDescriptor("No search indexes")));
        }, () -> listSearchIndexes(parentNode, tree));
    }
}
