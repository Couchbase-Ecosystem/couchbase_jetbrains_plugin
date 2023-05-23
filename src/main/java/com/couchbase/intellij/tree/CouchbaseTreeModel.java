package com.couchbase.intellij.tree;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tree.ConnectionNodeDescriptor;
import com.couchbase.intellij.tree.LoadingNodeDescriptor;
import com.intellij.openapi.project.Project;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;


public class CouchbaseTreeModel {

    //TODO: The connection here needs to be refactored
    public static DefaultTreeModel getTreeModel(Project project) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");

        DefaultMutableTreeNode adminLocal = new DefaultMutableTreeNode(new ConnectionNodeDescriptor("admin:local", true));
        adminLocal.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
        root.add(adminLocal);

        Cluster cluster = Cluster.connect(
                "localhost",
                ClusterOptions.clusterOptions("kaustav", "password").environment(env -> {
                    //env.applyProfile("wan-development");
                })
        );

        ActiveCluster.set(cluster, "admin:local" );
        DefaultTreeModel model = new DefaultTreeModel(root);
        return model;
    }


}