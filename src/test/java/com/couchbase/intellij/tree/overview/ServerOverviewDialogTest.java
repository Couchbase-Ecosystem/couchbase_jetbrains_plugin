package com.couchbase.intellij.tree.overview;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class ServerOverviewDialogTest {

    @Test
    public void opensOnTheRequestedBucketOnlyWhenItExists() {
        assertEquals("travel-sample", ServerOverviewDialog.pageToOpen("travel-sample", Set.of("travel-sample", "beer")));
        assertEquals(ServerOverviewDialog.GENERAL, ServerOverviewDialog.pageToOpen("gone", Set.of("travel-sample")));
        assertEquals(ServerOverviewDialog.GENERAL, ServerOverviewDialog.pageToOpen(null, Set.of("travel-sample")));
        assertEquals(ServerOverviewDialog.GENERAL, ServerOverviewDialog.pageToOpen("travel-sample", Set.of()));
    }
}
