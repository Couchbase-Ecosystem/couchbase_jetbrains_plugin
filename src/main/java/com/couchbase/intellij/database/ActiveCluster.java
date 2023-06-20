package com.couchbase.intellij.database;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import com.couchbase.intellij.persistence.SavedCluster;
import com.intellij.ui.ColorUtil;

import java.awt.*;
import java.time.Duration;

public class ActiveCluster {

    private static final ActiveCluster activeCluster = new ActiveCluster();
    private Cluster cluster;
    private SavedCluster savedCluster;
    private String password;

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

        String password = DataLoader.getClusterPassword(savedCluster);
        Cluster cluster = Cluster.connect(savedCluster.getUrl(),
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
}