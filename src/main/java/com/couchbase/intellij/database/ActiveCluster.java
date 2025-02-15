package com.couchbase.intellij.database;

import com.couchbase.client.core.cnc.EventBus;
import com.couchbase.client.core.cnc.events.endpoint.UnexpectedEndpointDisconnectedEvent;
import com.couchbase.client.core.env.PasswordAuthenticator;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import com.couchbase.intellij.database.entity.CouchbaseBucket;
import com.couchbase.intellij.database.entity.CouchbaseClusterEntity;
import com.couchbase.intellij.persistence.SavedCluster;
import com.couchbase.intellij.tree.overview.apis.CouchbaseRestAPI;
import com.couchbase.intellij.tree.overview.apis.ServerOverview;
import com.couchbase.intellij.utils.Subscribable;
import com.couchbase.intellij.workbench.CbShellEmulator;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.ColorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import utils.CBConfigUtil;

import java.awt.*;
import java.time.Duration;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ActiveCluster implements CouchbaseClusterEntity {

    public static Subscribable<ActiveCluster> INSTANCE = new Subscribable<>(new ActiveCluster());
    /**
     * Connection listeners are invoked only when a new cluster connection is established
     */
    private final List<Runnable> newConnectionListener = new ArrayList<>();

    private static List<String> searchNodes;
    private Cluster cluster;
    private SavedCluster savedCluster;
    private String password;
    private List<String> services;
    private String version;

    private Color color;

    private Permissions permissions;

    private Set<CouchbaseBucket> buckets;
    private long lastSchemaUpdate = 0;
    private AtomicBoolean schemaUpdating = new AtomicBoolean(false);

    private Runnable disconnectListener;

    private final Subscribable<QueryContext> queryContext = new Subscribable<>();

    public static final AtomicBoolean ReconnectOnDisconnect = new AtomicBoolean();
    /**
     * Subscribers are invoked upon subscription and when a new connection is established
     * and initialized (schema has been populated)
     */
    private static final List<Consumer<ActiveCluster>> clusterListeners = new ArrayList<>();

    private final List<Runnable> disconnectListeners = new ArrayList<>();

    private Integer queryLimit = 200;

    protected ActiveCluster() {
    }

    public static ActiveCluster getInstance() {
        return INSTANCE.get().orElse(null);
    }

    public static void subscribe(Consumer<ActiveCluster> listener) {
        clusterListeners.add(listener);
        if (INSTANCE != null && INSTANCE.isPresent() && INSTANCE.getValue().cluster != null) {
            listener.accept(INSTANCE.getValue());
        }
    }

    public static void subscribeNew(Consumer<ActiveCluster> listener) {
        clusterListeners.add(listener);
    }

    public void setQueryContext(@Nullable QueryContext context) {
        this.queryContext.set(context);
    }

    @NotNull
    public Subscribable<QueryContext> getQueryContext() {
        return this.queryContext;
    }

    @VisibleForTesting
    public static void setInstance(ActiveCluster i) {
        INSTANCE.set(i);
    }

    public void registerNewConnectionListener(Runnable runnable) {
        this.newConnectionListener.add(runnable);
    }

    public void deregisterNewConnectionListener(Runnable runnable) {
        this.newConnectionListener.remove(runnable);
    }

    public void onDisconnect(Runnable runnable) {
        disconnectListeners.add(runnable);
    }

    public Cluster get() {
        return cluster;
    }

    public String getId() {
        if (this.savedCluster == null) {
            return null;
        }
        return this.savedCluster.getId();
    }

    public PermissionChecker getPermissions() throws Exception {
        if (permissions == null) {
            permissions = CouchbaseRestAPI.callWhoAmIEndpoint();
        }
        return new PermissionChecker(permissions);
    }

    public void connect(SavedCluster savedCluster, Consumer<Exception> connectListener, Runnable disconnectListener) throws Exception {
        if (this.cluster != null) {
            disconnect();
        }

        ProgressManager.getInstance().run(new Task.Backgroundable(null, "Connecting to Couchbase cluster '" + savedCluster.getId() + "'", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    String password = DataLoader.getClusterPassword(savedCluster);

                    ClusterOptions options;
                    if (!savedCluster.getLDAP()) {
                        options = ClusterOptions.clusterOptions(savedCluster.getUsername(), password);
                    } else {
                        PasswordAuthenticator authenticator = PasswordAuthenticator.builder().username(savedCluster.getUsername())
                                .password(password)
                                .onlyEnablePlainSaslMechanism().build();

                        options = ClusterOptions.clusterOptions(authenticator);
                    }

//TODO: Don't know why this doesn't work
//                    options.environment(env -> {
//                        env.applyProfile("wan-development");
//                    });

                    Cluster cluster = Cluster.connect(savedCluster.getUrl()
                                    + (savedCluster.getQueryParams() == null ? "" : savedCluster.getQueryParams()),
                            options);

                    cluster.waitUntilReady(Duration.ofSeconds(10));
                    ActiveCluster.this.cluster = cluster;
                    ActiveCluster.this.savedCluster = savedCluster;
                    ActiveCluster.this.password = password;
                    ActiveCluster.this.disconnectListener = disconnectListener;

                    EventBus eventBus = cluster.environment().eventBus();
                    eventBus.subscribe(event -> {
                        if (event instanceof UnexpectedEndpointDisconnectedEvent) {
                            if (cluster != null) {
                                if (ReconnectOnDisconnect.get()) {
                                    try {
                                        Log.info("Reconnecting to cluster '" + savedCluster.getId() + "'");
                                        cluster.disconnect();
                                        connect(savedCluster, connectListener, disconnectListener);
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                    return;
                                }
                                Log.info("Disconnected from cluster " + savedCluster.getId());
                                ApplicationManager.getApplication().invokeLater(() -> {
                                    try {
                                        Messages.showErrorDialog(
                                                String.format("Lost connection to cluster '%s'", savedCluster.getId()),
                                                "Lost connection"
                                        );
                                    } catch (Exception e) {
                                        // noop, idea be crazy sometimes
                                    }
                                });
                                eventBus.stop(Duration.ZERO);
                                disconnect();
                            }
                        }

                    });
                    if (savedCluster.getColor() != null) {
                        ActiveCluster.this.color = Color.decode(savedCluster.getColor());
                    }

                    ServerOverview overview = CouchbaseRestAPI.getOverview();
                    setServices(overview.getNodes().stream()
                            .flatMap(node -> node.getServices().stream()).distinct().collect(Collectors.toList()));

                    setVersion(overview.getNodes().get(0).getVersion()
                            .substring(0, overview.getNodes().get(0).getVersion().indexOf('-')));

                    if (hasSearchService()) {
                        searchNodes = new ArrayList<>();
                        searchNodes.addAll(overview.getNodes().stream()
                                .filter(e -> e.getServices().contains("fts"))
                                .map(e -> e.getHostname().substring(0, e.getHostname().indexOf(":")))
                                .collect(Collectors.toSet()));
                    }

                    //Notify Listeners that we connected to a new cluster.
                    //NOTE: Only singletons can register here, otherwise we will get a memory leak
                    CompletableFuture.runAsync(() -> {
                        for (Runnable run : newConnectionListener) {
                            try {
                                run.run();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.debug("Failed to notify connection event.", e);
                            }
                        }
                    });

                    scheduleSchemaUpdate(e -> {
                        if (e == null) {
                            // notify all subscribers
                            clusterListeners.forEach(s -> s.accept(ActiveCluster.this));
                        }
                        connectListener.accept(e);
                    });
                    INSTANCE.set(ActiveCluster.this);
                } catch (Exception e) {
                    ApplicationManager.getApplication().invokeLater(() -> Messages.showErrorDialog(
                            String.format("Error while connecting to cluster '%s': \n %s", savedCluster.getId(), e.getMessage()),
                            "Failed to connect to cluster"
                    ));
                    if (connectListener != null) {
                        connectListener.accept(e);
                    }
                    if (cluster != null) {
                        disconnect();
                    }
                }
            }
        });
    }

    public void disconnect() {
        if (cluster != null) {
            try {
                cluster.disconnect();
            } catch (Exception e) {
                Log.debug("Failed to disconnect from the server", e);
            }
        }
        if (disconnectListener != null) {
            try {
                disconnectListener.run();
            } catch (Exception e) {
                Log.error(e);
            }
            disconnectListeners.forEach(r -> {
                try {
                    r.run();
                } catch (Exception e) {
                    Log.error(e);
                }
            });
        }
        this.savedCluster = null;
        this.cluster = null;
        this.password = null;
        this.color = null;
        this.buckets = null;
        this.disconnectListener = null;
        this.permissions = null;
        this.searchNodes = null;
    }

    public String getUsername() {
        if (this.savedCluster == null) {
            return null;
        }
        return this.savedCluster.getUsername();
    }

    public String getPassword() {
        return this.password;
    }

    public boolean isSSLEnabled() {
        if (this.savedCluster == null) {
            return false;
        }
        return this.savedCluster.isSslEnable();
    }

    public String getClusterURL() {
        if (this.savedCluster == null) {
            return null;
        }
        return this.savedCluster.getUrl();
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        if (color != null) {
            this.savedCluster.setColor(ColorUtil.toHtmlColor(color));
        } else {
            this.savedCluster.setColor(null);
        }
        this.color = color;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isReadOnlyMode() {
        return this.savedCluster != null && this.savedCluster.isReadOnly() != null && this.savedCluster.isReadOnly();
    }

    public void setReadOnlyMode(boolean mode) {
        this.savedCluster.setReadOnly(mode);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public CouchbaseClusterEntity getParent() {
        return null;
    }

    @Override
    public Stream<? extends CouchbaseClusterEntity> getChild(String name) {
        if (getChildren() == null) {
            return Stream.empty();
        }

        return getChildren().stream()
                .flatMap(b -> Stream.concat(
                        Stream.of(b),
                        Stream.concat(
                                b.getChildren().stream(),
                                b.getChildren().stream().flatMap(s -> s.getChildren().stream())
                        )
                ))
                .filter(e -> name.equalsIgnoreCase(e.getName()));
    }

    @Override
    public Set<CouchbaseBucket> getChildren() {
        if (cluster == null) {
            return Collections.EMPTY_SET;
        }
        if (buckets == null) {
            updateSchema();
        } else {
            scheduleSchemaUpdate(null);
        }
        return buckets;
    }

    private List<Consumer<Exception>> schemaListeners = Collections.synchronizedList(new ArrayList<>());

    private void scheduleSchemaUpdate(Consumer<Exception> onComplete) {

        if (!hasQueryService()) {
            return;
        }
        if (schemaUpdating.get()) {
            schemaListeners.add(onComplete);
        } else if (System.currentTimeMillis() - lastSchemaUpdate > 60000) {
            schemaListeners.add(onComplete);
            schemaUpdating.set(true);

            ProgressManager.getInstance().run(new Task.Backgroundable(null, "Reading Couchbase cluster schema", true) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    try {
                        doUpdateSchema();
                        schemaListeners.forEach(l -> l.accept(null));
                        schemaListeners.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                        schemaListeners.forEach(l -> l.accept(e));
                        schemaListeners.clear();
                        ApplicationManager.getApplication().invokeLater(() -> Messages.showErrorDialog("Could not read cluster schema.", "Couchbase Connection Error"));
                        disconnect();
                    }
                }
            });
        } else {
            if (onComplete != null) {
                onComplete.accept(null);
            }
        }
    }

    private void doUpdateSchema() {
        try {
            updateSchema();
        } finally {
            schemaUpdating.set(false);
        }
    }

    @Override
    public void updateSchema() {

        if (!hasQueryService()) {
            return;
        }
        Log.debug("Updating cluster schema");
        Set<CouchbaseBucket> newbuckets = new HashSet<>();
        if (buckets == null) {
            buckets = newbuckets;
        }
        cluster.buckets().getAllBuckets().forEach((b, settings) -> {
            CouchbaseBucket bucket = new CouchbaseBucket(this, b);
            bucket.updateSchema();
            newbuckets.add(bucket);
        });
        buckets = newbuckets;
        lastSchemaUpdate = System.currentTimeMillis();
        Log.debug("updated cluster schema");
    }

    @VisibleForTesting
    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    @Override
    public SavedCluster getSavedCluster() {
        return savedCluster;
    }

    @Override
    public CouchbaseClusterEntity getRoot() {
        return this;
    }

    @Override
    public String path() {
        return "";
    }

    @Override
    public List<String> pathElements() {
        return new ArrayList<>();
    }

    @Override
    public Cluster getCluster() {
        return cluster;
    }

    public boolean hasQueryService() {
        return CBConfigUtil.hasQueryService(services);
    }

    public boolean hasSearchService() {
        return CBConfigUtil.hasSearchService(services);
    }

    public boolean isCapella() {
        return savedCluster != null && savedCluster.getUrl().contains("cloud.couchbase.com");
    }

    public void clearSchema() {
        if (!this.schemaUpdating.get() && this.buckets != null) {
            this.buckets.clear();
            this.lastSchemaUpdate = 0;
        }
    }

    public void setQueryLimit(Integer limit) {
        this.queryLimit = limit;
    }

    public @Nullable Integer getQueryLimit() {
        return this.queryLimit;
    }

    public static List<String> searchNodes() {
        return searchNodes;
    }
}