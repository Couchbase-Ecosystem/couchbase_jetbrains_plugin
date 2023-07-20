package com.couchbase.intellij.database;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import com.couchbase.intellij.database.entity.CouchbaseBucket;
import com.couchbase.intellij.database.entity.CouchbaseClusterEntity;
import com.couchbase.intellij.persistence.SavedCluster;
import com.intellij.ui.ColorUtil;

import java.awt.*;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class ActiveCluster implements CouchbaseClusterEntity {

    private static final ActiveCluster activeCluster = new ActiveCluster();
    private Cluster cluster;
    private SavedCluster savedCluster;
    private String password;

    private List<String> services;

    private String version;

    private Color color;

    private ActiveCluster() {
    }

    public static ActiveCluster getInstance() {
        return activeCluster;
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
        } catch (Exception e) {
            if (cluster != null) {
                cluster.disconnect();
            }
            throw e;
        }
    }

    public void disconnect() {
        cluster.disconnect();
        this.savedCluster = null;
        this.cluster = null;
        this.password = null;
        this.color = null;
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
    public Set<CouchbaseBucket> getChildren() {
        Set<CouchbaseBucket> buckets = new HashSet<>();
        cluster.buckets().getAllBuckets().forEach((b, settings) -> {
            buckets.add(new CouchbaseBucket(this, b));
        });
        return buckets;
    }

    @Override
    public Cluster getCluster() {
        return cluster;
    }
}