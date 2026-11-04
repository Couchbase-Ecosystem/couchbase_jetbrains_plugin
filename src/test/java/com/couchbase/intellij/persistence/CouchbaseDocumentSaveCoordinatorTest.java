package com.couchbase.intellij.persistence;

import com.couchbase.client.java.Cluster;
import com.couchbase.intellij.VirtualFileKeys;
import com.couchbase.intellij.database.ActiveCluster;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.testFramework.LoggedErrorProcessor;
import com.intellij.testFramework.PlatformTestUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class CouchbaseDocumentSaveCoordinatorTest extends BasePlatformTestCase {

    static {
        com.couchbase.intellij.testutil.AllowedThreads.allowJdkAwtThreads();
    }

    private ActiveCluster previousCluster;
    private Cluster sdk;
    private CouchbaseDocumentVirtualFile file;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        previousCluster = ActiveCluster.getInstance();
        sdk = mock(Cluster.class, RETURNS_DEEP_STUBS);
        ActiveCluster active = mock(ActiveCluster.class);
        when(active.get()).thenReturn(sdk);
        when(active.getId()).thenReturn("conn");
        ActiveCluster.setInstance(active);
        file = new CouchbaseDocumentVirtualFile(getProject(), JsonFileType.INSTANCE, "b", "s", "c", "id");
        file.putUserData(VirtualFileKeys.CONN_ID, "conn");
        file.putUserData(VirtualFileKeys.CAS, "1");
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            ActiveCluster.setInstance(previousCluster);
        } finally {
            super.tearDown();
        }
    }

    public void testInvalidJsonIsRejectedBeforeAnyServerCall() {
        Document document = EditorFactory.getInstance().createDocument("{\"a\":1}}");
        assertFalse(CouchbaseDocumentSaveCoordinator.maySave(document, file, false));
        waitForSaveToFinish(document);
        assertTrue(file.getUserData(CouchbaseDocumentSaveCoordinator.LAST_ERROR).contains("valid JSON"));
        verify(sdk, never()).bucket(anyString());
    }

    public void testAnErrorInTheBackgroundSaveReleasesThePendingSave() {
        when(sdk.bucket("b").scope("s").collection("c").get("id")).thenThrow(new AssertionError("boom"));
        Document document = EditorFactory.getInstance().createDocument("[1,2,3]");
        LoggedErrorProcessor.executeWith(new LoggedErrorProcessor() {
            @Override
            public @NotNull Set<Action> processError(@NotNull String category, @NotNull String message, String @NotNull [] details, Throwable t) {
                return EnumSet.noneOf(Action.class);
            }
        }, () -> {
            assertFalse(CouchbaseDocumentSaveCoordinator.maySave(document, file, false));
            waitForSaveToFinish(document);
        });
        assertTrue(file.getUserData(CouchbaseDocumentSaveCoordinator.LAST_ERROR).contains("boom"));
    }

    public void testAutosaveBacksOffAfterAFailedSaveButAnExplicitSaveRetriesAtOnce() {
        var collection = sdk.bucket("b").scope("s").collection("c");
        when(collection.get("id")).thenThrow(new RuntimeException("connection refused"));
        Document document = EditorFactory.getInstance().createDocument("{\"a\":1}");

        assertFalse(CouchbaseDocumentSaveCoordinator.maySave(document, file, false));
        waitForSaveToFinish(document);
        assertTrue(file.getUserData(CouchbaseDocumentSaveCoordinator.LAST_ERROR).contains("connection refused"));
        verify(collection, times(1)).get("id");

        assertFalse(CouchbaseDocumentSaveCoordinator.maySave(document, file, false));
        assertFalse("autosave must not hit the server again right away", CouchbaseDocumentSaveCoordinator.isSaving(document));
        verify(collection, times(1)).get("id");

        assertFalse(CouchbaseDocumentSaveCoordinator.maySave(document, file, true));
        assertTrue("an explicit save always tries", CouchbaseDocumentSaveCoordinator.isSaving(document));
        waitForSaveToFinish(document);
        verify(collection, times(2)).get("id");
    }

    private static void waitForSaveToFinish(Document document) {
        long deadline = System.currentTimeMillis() + 10_000;
        while (CouchbaseDocumentSaveCoordinator.isSaving(document)) {
            assertTrue("The save was never released", System.currentTimeMillis() < deadline);
            PlatformTestUtil.dispatchAllEventsInIdeEventQueue();
            try { Thread.sleep(10); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
        }
    }
}
