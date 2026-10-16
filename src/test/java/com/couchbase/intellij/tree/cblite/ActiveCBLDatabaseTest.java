package com.couchbase.intellij.tree.cblite;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

// connect()/getDatabase() are not covered: the Couchbase Lite native library does not load in unit tests.
public class ActiveCBLDatabaseTest {

    private final ActiveCBLDatabase active = ActiveCBLDatabase.getInstance();

    @After
    public void tearDown() throws Exception {
        // disconnect() also clears the listener map, so this resets the singleton for other tests.
        active.disconnect();
    }

    @Test
    public void getInstanceReturnsSameSingleton() {
        Assert.assertSame(ActiveCBLDatabase.getInstance(), ActiveCBLDatabase.getInstance());
    }

    @Test
    public void noConnectionMeansNoDatabaseIdAndNoDatabase() throws Exception {
        active.disconnect();

        Assert.assertNull(active.getDatabase());
        Assert.assertNull(active.getSavedDatabase());
        Assert.assertNull(active.getDatabaseId());
    }

    @Test
    public void disconnectWithoutConnectionRunsListenersAndClearsThem() throws Exception {
        List<String> calls = new ArrayList<>();
        active.addDisconnectListener("a", () -> calls.add("a"));

        active.disconnect();
        active.disconnect();

        Assert.assertEquals(1, calls.size());
        Assert.assertEquals("a", calls.get(0));
    }

    @Test
    public void addingListenerWithSameKeyReplacesPrevious() throws Exception {
        List<String> calls = new ArrayList<>();
        active.addDisconnectListener("k", () -> calls.add("first"));
        active.addDisconnectListener("k", () -> calls.add("second"));

        active.disconnect();

        Assert.assertEquals(List.of("second"), calls);
    }

    @Test
    public void removedListenerIsNotRunOnDisconnect() throws Exception {
        List<String> calls = new ArrayList<>();
        active.addDisconnectListener("k", () -> calls.add("k"));
        active.removeDisconnectListener("k");

        active.disconnect();

        Assert.assertTrue(calls.isEmpty());
    }

    @Test
    public void failingListenerDoesNotStopOtherListeners() throws Exception {
        List<String> calls = new ArrayList<>();
        active.addDisconnectListener("boom", () -> {
            throw new IllegalStateException("boom");
        });
        active.addDisconnectListener("ok", () -> calls.add("ok"));

        active.disconnect();

        Assert.assertEquals(List.of("ok"), calls);
    }
}
