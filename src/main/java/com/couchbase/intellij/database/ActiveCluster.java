package com.couchbase.intellij.database;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import com.couchbase.intellij.database.entity.CouchbaseBucket;
import com.couchbase.intellij.database.entity.CouchbaseClusterEntity;
import com.couchbase.intellij.persistence.SavedCluster;
import com.couchbase.intellij.tree.overview.apis.CouchbaseRestAPI;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.ui.ColorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

import java.awt.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class ActiveCluster implements CouchbaseClusterEntity {

    private static final Logger log = Logger.getInstance(ActiveCluster.class);

    private static ActiveCluster activeCluster = new ActiveCluster();
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

    private ActiveCluster() {
    }

    public static ActiveCluster getInstance() {
        return activeCluster;
    }

    @VisibleForTesting
    public static void setInstance(ActiveCluster i) {
        activeCluster = i;
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

    public void connect(SavedCluster savedCluster) {
        if (this.cluster != null) {
            disconnect();
        }

        Cluster cluster = null;

        try {
            String password = DataLoader.getClusterPassword(savedCluster);
            cluster = Cluster.connect(savedCluster.getUrl(),
                    ClusterOptions.clusterOptions(savedCluster.getUsername(), password).environment(env -> {
                        // env.applyProfile("wan-development");
                    }));
            cluster.waitUntilReady(Duration.ofSeconds(5));
            this.cluster = cluster;
            this.savedCluster = savedCluster;
            this.password = password;
            if (savedCluster.getColor() != null) {
                this.color = Color.decode(savedCluster.getColor());
            }
            scheduleSchemaUpdate();
        } catch (Exception e) {
            if (cluster != null) {
                cluster.disconnect();
            }
            throw e;
        }
    }

    public PermissionChecker getPermissions() throws Exception {
        if (permissions == null) {
            try {
                permissions = CouchbaseRestAPI.callWhoAmIEndpoint();
            } catch (Exception e) {
                throw e;
            }
        }
        return new PermissionChecker(permissions);
    }

    public void disconnect() {
        cluster.disconnect();
        this.savedCluster = null;
        this.cluster = null;
        this.password = null;
        this.color = null;
        this.buckets = null;
        this.permissions = null;
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
        return this.savedCluster.isReadOnly() != null && this.savedCluster.isReadOnly();
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
        if (buckets == null) {
            updateSchema();
        } else {
            scheduleSchemaUpdate();
        }
        return buckets;
    }

    private void scheduleSchemaUpdate() {
        if (!schemaUpdating.get() && System.currentTimeMillis() - lastSchemaUpdate > 60000) {
            schemaUpdating.set(true);
            new Task.ConditionalModal(null, "Reading Couchbase cluster schema", true, PerformInBackgroundOption.ALWAYS_BACKGROUND) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    doUpdateSchema();
                }
            }.queue();
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
        log.info("Updating cluster schema");
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
        log.info("updated cluster schema");
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
}