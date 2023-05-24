package com.couchbase.intellij.database;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import com.couchbase.intellij.persistence.SavedCluster;

import java.time.Duration;

public class ActiveCluster {

    private static ActiveCluster activeCluster = new ActiveCluster();

    private ActiveCluster(){

    }

    private Cluster cluster;
    private SavedCluster savedCluster;
    private String password;

    public static ActiveCluster getInstance() {
        return activeCluster;
    }

    public Cluster get() {
        return cluster;
    }
    public String getId() {
        if(this.savedCluster == null) {
            return null;
        }
        return this.savedCluster.getId();
    }

    public void connect(SavedCluster savedCluster) {
        if(this.cluster != null) {
            disconnect();
        }

        String password = DataLoader.getClusterPassword(savedCluster);
        Cluster cluster = Cluster.connect(savedCluster.getUrl(),
                ClusterOptions.clusterOptions(savedCluster.getUsername(), password).environment(env -> {
                    //env.applyProfile("wan-development");
                })
        );
        cluster.waitUntilReady(Duration.ofSeconds(5));
        this.cluster = cluster;
        this.savedCluster = savedCluster;
        this.password = password;
    }

    public void disconnect() {
        cluster.disconnect();
        this.savedCluster = null;
        this.cluster = null;
        this.password = null;
    }

    public String getUsername() {
        if(this.savedCluster == null) {
            return null;
        }
        return this.savedCluster.getUsername();
    }

    public String getPassword() {
        return this.password;
    }
}
