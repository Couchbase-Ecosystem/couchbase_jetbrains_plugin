package com.couchbase.intellij.tree;

import com.couchbase.client.java.Cluster;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.ClusterUiTask;
import org.junit.Test;

import javax.swing.tree.DefaultMutableTreeNode;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TreeRightClickListenerMenuTargetTest {

    @Test
    public void deleteActionsUseTheConnectionAndNodeTheMenuWasOpenedFor() {
        ActiveCluster previous = ActiveCluster.INSTANCE.getValue();
        ActiveCluster active = new ActiveCluster() {};
        active.setCluster(mock(Cluster.class));
        ActiveCluster.setInstance(active);
        try {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode();
            DefaultMutableTreeNode bucket = new DefaultMutableTreeNode();
            root.add(bucket);
            ClusterUiTask.Connection menuConnection = ClusterUiTask.capture(active);
            assertTrue(TreeRightClickListener.isMenuTargetCurrent(menuConnection, root, bucket));

            // The tree was reloaded while the menu or confirmation dialog was open.
            assertFalse(TreeRightClickListener.isMenuTargetCurrent(menuConnection, new DefaultMutableTreeNode(), bucket));
            root.remove(bucket);
            assertFalse(TreeRightClickListener.isMenuTargetCurrent(menuConnection, root, bucket));
            root.add(bucket);

            // The user switched or reconnected the cluster after opening the menu.
            ActiveCluster other = new ActiveCluster() {};
            other.setCluster(mock(Cluster.class));
            ActiveCluster.setInstance(other);
            assertFalse(TreeRightClickListener.isMenuTargetCurrent(menuConnection, root, bucket));
        } finally {
            ActiveCluster.setInstance(previous);
        }
    }
}
