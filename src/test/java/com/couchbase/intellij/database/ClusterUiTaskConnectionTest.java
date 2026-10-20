package com.couchbase.intellij.database;

import com.couchbase.client.java.Cluster;
import org.junit.Test;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ClusterUiTaskConnectionTest {
    @Test public void replacingSdkOnTheSameConnectionInvalidatesTheSnapshot() {
        ActiveCluster previous = ActiveCluster.INSTANCE.getValue();
        ActiveCluster active = new ActiveCluster(); Cluster original = mock(Cluster.class);
        active.setCluster(original); ActiveCluster.setInstance(active);
        try {
            var request = ClusterUiTask.capture(active);
            assertSame(original, request.sdk());
            active.setCluster(mock(Cluster.class));
            assertFalse(request.isCurrent());
            assertThrows(IllegalStateException.class, request::sdk);
            assertThrows(IllegalStateException.class, request::requireWritable);
        } finally { ActiveCluster.setInstance(previous); }
    }

    @Test public void changingConnectionIdInvalidatesTheSnapshotEvenWithTheSameSdk() {
        ActiveCluster previous = ActiveCluster.INSTANCE.getValue();
        AtomicReference<String> id = new AtomicReference<>("A");
        ActiveCluster active = new ActiveCluster() { @Override public String getId() { return id.get(); } };
        active.setCluster(mock(Cluster.class)); ActiveCluster.setInstance(active);
        try {
            var request = ClusterUiTask.capture(active); id.set("B");
            assertThrows(IllegalStateException.class, request::requireCurrent);
        } finally { ActiveCluster.setInstance(previous); }
    }

    @Test public void writableCheckUsesTheCurrentReadOnlySetting() {
        ActiveCluster previous = ActiveCluster.INSTANCE.getValue();
        ActiveCluster active = mock(ActiveCluster.class);
        when(active.get()).thenReturn(mock(Cluster.class)); when(active.getId()).thenReturn("A");
        ActiveCluster.setInstance(active);
        try {
            var request = ClusterUiTask.capture(active); request.requireWritable();
            when(active.isReadOnlyMode()).thenReturn(true);
            assertThrows(IllegalStateException.class, request::requireWritable);
        } finally { ActiveCluster.setInstance(previous); }
    }
}
