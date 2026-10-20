package com.couchbase.intellij.tree;

import com.couchbase.client.java.*;
import com.couchbase.client.java.kv.*;
import com.couchbase.client.java.manager.collection.*;
import com.couchbase.intellij.VirtualFileKeys;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.persistence.*;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.*;
import com.intellij.ui.treeStructure.Tree;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.junit.Test;
import org.mockito.Mockito;
import javax.swing.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/** Deliberately stalled SDK calls prove that editor and dialog events remain responsive. */
public class AsyncDocumentUiTest extends DialogUiTestCase {
    private ActiveCluster previous;
    private ActiveCluster active;
    private Cluster sdk;
    private Bucket bucket;
    private com.couchbase.client.java.Collection collection;
    private final AtomicBoolean readOnly = new AtomicBoolean();
    private final CountDownLatch started = new CountDownLatch(1);
    private final CountDownLatch release = new CountDownLatch(1);

    @Override protected void setUp() throws Exception {
        super.setUp();
        previous = ActiveCluster.getInstance(); active = Mockito.mock(ActiveCluster.class);
        sdk = Mockito.mock(Cluster.class); bucket = Mockito.mock(Bucket.class);
        Scope scope = Mockito.mock(Scope.class); collection = Mockito.mock(com.couchbase.client.java.Collection.class);
        var settings = new SavedCluster(); settings.setId("async-ui");
        Mockito.when(active.get()).thenReturn(sdk); Mockito.when(active.getCluster()).thenReturn(sdk);
        Mockito.when(active.getId()).thenReturn(settings.getId()); Mockito.when(active.getSavedCluster()).thenReturn(settings);
        Mockito.when(active.isReadOnlyMode()).thenAnswer(call -> readOnly.get());
        Mockito.when(sdk.bucket("bucket")).thenReturn(bucket);
        Mockito.when(bucket.scope("scope")).thenReturn(scope);
        Mockito.when(scope.collection("collection")).thenReturn(collection);
        ActiveCluster.setInstance(active);
    }

    private void stall() throws InterruptedException {
        assertFalse("SDK work must not run on the EDT", SwingUtilities.isEventDispatchThread());
        started.countDown();
        assertTrue("Test releases the server response", release.await(10, TimeUnit.SECONDS));
    }

    private void heartbeat() throws Exception {
        assertTrue("SDK work started", started.await(5, TimeUnit.SECONDS));
        CountDownLatch ticks = new CountDownLatch(4);
        Timer timer = GuiActionRunner.execute(() -> {
            Timer value = new Timer(50, event -> ticks.countDown()); value.start(); return value;
        });
        try { assertTrue("UI events continue while the SDK is pending", ticks.await(2, TimeUnit.SECONDS)); }
        finally { GuiActionRunner.execute(timer::stop); }
    }

    @Test public void openingADocumentDoesNotBlockDialogInput() throws Exception {
        var exists = Mockito.mock(ExistsResult.class); Mockito.when(exists.exists()).thenReturn(true);
        Mockito.when(collection.exists("doc")).thenAnswer(call -> { stall(); return exists; });
        GetResult loaded = result("{}", 1);
        Mockito.when(collection.get("doc")).thenReturn(loaded);
        var dialog = showDialog(() -> new OpenDocumentDialog(false, getProject(), null, "bucket", "scope", "collection"));
        dialog.textBox("documentId").enterText("doc"); dialog.button(JButtonMatcher.withText("OK")).click();
        heartbeat();
        dialog.button(JButtonMatcher.withText("OK")).requireDisabled();
        dialog.button(JButtonMatcher.withText("Cancel")).requireEnabled();
        release.countDown();
        await("document opens", () -> GuiActionRunner.execute(() -> FileEditorManager.getInstance(getProject()).getOpenFiles().length > 0));
    }

    @Test public void collectionCreationStaysResponsiveAndDuplicateErrorsAllowRetry() throws Exception {
        CollectionManager manager = Mockito.mock(CollectionManager.class);
        Mockito.when(bucket.collections()).thenReturn(manager);
        ScopeSpec scope = ScopeSpec.create("scope", java.util.Set.of(CollectionSpec.create("existing", "scope")));
        Mockito.when(manager.getScope("scope")).thenAnswer(call -> { stall(); return scope; });
        var dialog = showDialog(() -> new NewCollectionDialog(getProject(), "bucket", "scope", null, new Tree()));
        dialog.textBox("newCollectionName").enterText("existing"); dialog.button(JButtonMatcher.withText("OK")).click();
        heartbeat(); dialog.button(JButtonMatcher.withText("OK")).requireDisabled();
        release.countDown();
        await("duplicate is reported", () -> GuiActionRunner.execute(() -> dialog.label("newCollectionError").text().contains("already exists")));
        dialog.textBox("newCollectionName").deleteText().enterText("new_collection");
        dialog.button(JButtonMatcher.withText("OK")).click();
        await("creation completes", () -> GuiActionRunner.execute(() -> !dialog.target().isShowing()));
        Mockito.verify(manager).createCollection("scope", "new_collection");
    }

    private GetResult result(String text, long cas) {
        GetResult result = Mockito.mock(GetResult.class);
        Mockito.when(result.contentAsBytes()).thenReturn(text.getBytes(StandardCharsets.UTF_8));
        Mockito.when(result.cas()).thenReturn(cas); return result;
    }

    private Document editor(String id) {
        Document[] document = {null};
        ApplicationManager.getApplication().invokeAndWait(() -> {
            var file = new CouchbaseDocumentVirtualFile(getProject(), JsonFileType.INSTANCE, "bucket", "scope", "collection", id);
            file.putUserData(VirtualFileKeys.CONN_ID, "async-ui"); file.putUserData(VirtualFileKeys.CLUSTER, "async-ui");
            file.acceptServerContent("{}".getBytes(StandardCharsets.UTF_8), 1);
            FileEditorManager.getInstance(getProject()).openFile(file, true);
            document[0] = FileDocumentManager.getInstance().getDocument(file);
        });
        return document[0];
    }

    private void edit(Document document, String text) {
        ApplicationManager.getApplication().invokeAndWait(() -> ApplicationManager.getApplication().runWriteAction(() -> document.setText(text)));
    }
    private void save(Document document) {
        ApplicationManager.getApplication().invokeAndWait(() -> FileDocumentManager.getInstance().saveDocument(document));
    }
    private void completed(Document document) {
        await("save completes", () -> GuiActionRunner.execute(() -> !CouchbaseDocumentSaveCoordinator.isSaving(document)));
    }

    @Test public void savingDoesNotBlockTypingOrMarkNewerEditsAsSaved() throws Exception {
        AtomicLong cas = new AtomicLong(1); AtomicInteger requests = new AtomicInteger();
        AtomicReference<String> server = new AtomicReference<>("{}");
        Mockito.when(collection.get("save")).thenAnswer(call -> {
            if (requests.getAndIncrement() == 0) stall();
            return result(server.get(), cas.get());
        });
        Mockito.when(collection.replace(Mockito.eq("save"), Mockito.any(), Mockito.any(ReplaceOptions.class))).thenAnswer(call -> {
            assertFalse(SwingUtilities.isEventDispatchThread()); server.set(call.getArgument(1));
            MutationResult result = Mockito.mock(MutationResult.class); Mockito.when(result.cas()).thenReturn(cas.incrementAndGet()); return result;
        });
        Document document = editor("save");
        edit(document, "{\"value\":1}"); save(document); heartbeat();
        ApplicationManager.getApplication().invokeAndWait(() -> {
            assertFalse("Project closing waits for pending server writes", new CouchbaseDocumentCloseHandler().canClose(getProject()));
            // Keep the fixture project alive to exercise typing while the save is pending.
            getProject().putUserData(CouchbaseDocumentCloseHandler.CLOSE_AFTER_SAVE, null);
        });
        edit(document, "{\"value\":2}");
        release.countDown(); completed(document);
        assertEquals("{\"value\":1}", server.get());
        assertTrue(GuiActionRunner.execute(() -> FileDocumentManager.getInstance().isDocumentUnsaved(document)));
        save(document); completed(document);
        assertEquals("{\"value\":2}", server.get());
        assertFalse(GuiActionRunner.execute(() -> FileDocumentManager.getInstance().isDocumentUnsaved(document)));
    }

    @Test public void switchingToReadOnlyDuringThePrecheckPreventsTheWrite() throws Exception {
        Mockito.when(collection.get("readonly")).thenAnswer(call -> { stall(); return result("{}", 1); });
        Document document = editor("readonly"); edit(document, "{\"value\":1}"); save(document); heartbeat();
        readOnly.set(true); release.countDown(); completed(document);
        Mockito.verify(collection, Mockito.never()).replace(Mockito.anyString(), Mockito.any(), Mockito.any(ReplaceOptions.class));
        assertTrue(GuiActionRunner.execute(() -> FileDocumentManager.getInstance().isDocumentUnsaved(document)));
    }

    @Test public void reconnectingDuringSavePrechecksKeepsEditsAndDoesNotWriteToEitherSdk() throws Exception {
        Mockito.when(collection.get("reconnecting-save")).thenAnswer(call -> { stall(); return result("{}", 1); });
        Document document = editor("reconnecting-save"); edit(document, "{\"value\":1}"); save(document); heartbeat();
        edit(document, "{\"value\":2}"); save(document);
        Cluster replacement = Mockito.mock(Cluster.class);
        Mockito.when(active.get()).thenReturn(replacement); Mockito.when(active.getCluster()).thenReturn(replacement);
        release.countDown(); completed(document);
        Mockito.verify(collection, Mockito.never()).replace(Mockito.anyString(), Mockito.any(), Mockito.any(ReplaceOptions.class));
        Mockito.verifyNoInteractions(replacement);
        assertTrue(GuiActionRunner.execute(() -> FileDocumentManager.getInstance().isDocumentUnsaved(document)));
    }

    @Test public void closingWithUnsavedChangesCanCancelOrDiscardWithoutServerIo() {
        Document document = editor("close"); edit(document, "{\"value\":1}");
        var previousDialog = com.intellij.openapi.ui.TestDialogManager.setTestDialog(message -> 2);
        try {
            ApplicationManager.getApplication().invokeAndWait(() ->
                    assertFalse(new CouchbaseDocumentCloseHandler().canClose(getProject())));
            assertTrue(GuiActionRunner.execute(() -> FileDocumentManager.getInstance().isDocumentUnsaved(document)));
            com.intellij.openapi.ui.TestDialogManager.setTestDialog(message -> 1);
            ApplicationManager.getApplication().invokeAndWait(() ->
                    assertTrue(new CouchbaseDocumentCloseHandler().canClose(getProject())));
            assertFalse(GuiActionRunner.execute(() -> FileDocumentManager.getInstance().isDocumentUnsaved(document)));
            Mockito.verifyNoInteractions(collection);
        } finally { com.intellij.openapi.ui.TestDialogManager.setTestDialog(previousDialog); }
    }

    @Test public void cancellingDuringTheExistenceCheckPreventsDocumentCreation() throws Exception {
        var absent = Mockito.mock(ExistsResult.class);
        Mockito.when(collection.exists("cancelled")).thenAnswer(call -> { stall(); return absent; });
        var dialog = showDialog(() -> new OpenDocumentDialog(true, getProject(), null, "bucket", "scope", "collection"));
        dialog.textBox("documentId").enterText("cancelled"); dialog.button(JButtonMatcher.withText("OK")).click();
        heartbeat();
        dialog.button(JButtonMatcher.withText("Cancel")).requireEnabled().click();
        await("Cancel closes the dialog before the precheck returns", () -> GuiActionRunner.execute(() -> !dialog.target().isShowing()));
        release.countDown();
        Mockito.verify(collection, Mockito.after(500).never()).insert(Mockito.anyString(), Mockito.any());
        Mockito.verify(collection, Mockito.never()).get("cancelled");
    }

    @Test public void cancellingCollectionCreationDuringMetadataLoadingPreventsTheMutation() throws Exception {
        CollectionManager manager = Mockito.mock(CollectionManager.class); Mockito.when(bucket.collections()).thenReturn(manager);
        ScopeSpec scope = ScopeSpec.create("scope", java.util.Set.of());
        Mockito.when(manager.getScope("scope")).thenAnswer(call -> { stall(); return scope; });
        var dialog = showDialog(() -> new NewCollectionDialog(getProject(), "bucket", "scope", null, new Tree()));
        dialog.textBox("newCollectionName").enterText("cancelled"); dialog.button(JButtonMatcher.withText("OK")).click();
        heartbeat();
        dialog.button(JButtonMatcher.withText("Cancel")).requireEnabled().click();
        await("Cancel closes the dialog before the precheck returns", () -> GuiActionRunner.execute(() -> !dialog.target().isShowing()));
        release.countDown();
        Mockito.verify(manager, Mockito.after(500).never()).createCollection("scope", "cancelled");
    }

    @Test public void cancellingScopeCreationDuringMetadataLoadingPreventsTheMutation() throws Exception {
        CollectionManager manager = Mockito.mock(CollectionManager.class); Mockito.when(bucket.collections()).thenReturn(manager);
        Mockito.when(manager.getAllScopes()).thenAnswer(call -> { stall(); return java.util.List.of(); });
        var dialog = showDialog(() -> new NewScopeCreationDialog(getProject(), "bucket"));
        dialog.textBox().enterText("cancelled"); dialog.button(JButtonMatcher.withText("OK")).click();
        heartbeat();
        dialog.button(JButtonMatcher.withText("Cancel")).requireEnabled().click();
        await("Cancel closes the dialog before the precheck returns", () -> GuiActionRunner.execute(() -> !dialog.target().isShowing()));
        release.countDown();
        Mockito.verify(manager, Mockito.after(500).never()).createScope("cancelled");
    }

    @Test public void reconnectingDuringDocumentPrechecksPreventsWritingToEitherSdk() throws Exception {
        var absent = Mockito.mock(ExistsResult.class);
        Mockito.when(collection.exists("reconnect")).thenAnswer(call -> { stall(); return absent; });
        var dialog = showDialog(() -> new OpenDocumentDialog(true, getProject(), null, "bucket", "scope", "collection"));
        dialog.textBox("documentId").enterText("reconnect"); dialog.button(JButtonMatcher.withText("OK")).click();
        assertTrue(started.await(5, TimeUnit.SECONDS)); Cluster replacement = Mockito.mock(Cluster.class);
        Mockito.when(active.get()).thenReturn(replacement); Mockito.when(active.getCluster()).thenReturn(replacement);
        release.countDown();
        await("Connection change is reported without a write", () -> GuiActionRunner.execute(() ->
                dialog.label("documentIdError").text().contains("connection")));
        Mockito.verify(collection, Mockito.never()).insert(Mockito.anyString(), Mockito.any());
        Mockito.verifyNoInteractions(replacement); dialog.button(JButtonMatcher.withText("Cancel")).requireEnabled().click();
    }

    @Test public void missingTemplateSchemaFallsBackToAnEmptyDocument() throws Exception {
        Mockito.when(active.hasQueryService()).thenReturn(true);
        var model = new com.couchbase.intellij.database.entity.CouchbaseCollection(null, CollectionSpec.create("collection", "scope"));
        Mockito.when(active.getCachedCollection("bucket", "scope", "collection")).thenReturn(java.util.Optional.of(model));
        var absent = Mockito.mock(ExistsResult.class); Mockito.when(collection.exists("template")).thenReturn(absent);
        GetResult loaded = result("{}", 1); Mockito.when(collection.get("template")).thenReturn(loaded);
        var dialog = showDialog(() -> new OpenDocumentDialog(true, getProject(), null, "bucket", "scope", "collection"));
        dialog.textBox("documentId").enterText("template"); dialog.checkBox("generateDocumentTemplate").check();
        dialog.button(JButtonMatcher.withText("OK")).click();
        await("Template fallback opens the created document", () -> GuiActionRunner.execute(() -> !dialog.target().isShowing()));
        Mockito.verify(collection).insert("template", com.couchbase.client.java.json.JsonObject.create());
        Mockito.verify(active, Mockito.never()).getChild(Mockito.anyString());
    }

    @Test public void aFailedDocumentWriteRestoresCancelAndAllowsRetry() throws Exception {
        Mockito.when(collection.exists("retry-write")).thenReturn(Mockito.mock(ExistsResult.class));
        Mockito.when(collection.insert(Mockito.eq("retry-write"), Mockito.any()))
                .thenThrow(new IllegalStateException("Server write failed")).thenReturn(Mockito.mock(MutationResult.class));
        GetResult loaded = result("{}", 1);
        Mockito.when(collection.get("retry-write")).thenReturn(loaded);
        var dialog = showDialog(() -> new OpenDocumentDialog(true, getProject(), null, "bucket", "scope", "collection"));
        dialog.textBox("documentId").enterText("retry-write"); dialog.button(JButtonMatcher.withText("OK")).click();
        await("A failed write is reported", () -> GuiActionRunner.execute(() -> dialog.label("documentIdError").text().contains("Server write failed")));
        dialog.button(JButtonMatcher.withText("Cancel")).requireEnabled(); dialog.textBox("documentId").requireEnabled();
        dialog.button(JButtonMatcher.withText("OK")).requireEnabled().click();
        await("Retry opens the document", () -> GuiActionRunner.execute(() -> !dialog.target().isShowing()));
        Mockito.verify(collection, Mockito.times(2)).insert(Mockito.eq("retry-write"), Mockito.any());
    }

    @Test public void cancelIsDisabledOnceTheDocumentWriteHasStarted() throws Exception {
        var absent = Mockito.mock(ExistsResult.class); Mockito.when(collection.exists("writing")).thenReturn(absent);
        Mockito.when(collection.insert(Mockito.eq("writing"), Mockito.any())).thenAnswer(call -> { stall(); return Mockito.mock(MutationResult.class); });
        GetResult loaded = result("{}", 1); Mockito.when(collection.get("writing")).thenReturn(loaded);
        var dialog = showDialog(() -> new OpenDocumentDialog(true, getProject(), null, "bucket", "scope", "collection"));
        dialog.textBox("documentId").enterText("writing"); dialog.button(JButtonMatcher.withText("OK")).click();
        assertTrue(started.await(5, TimeUnit.SECONDS)); dialog.button(JButtonMatcher.withText("Cancel")).requireDisabled();
        robot.pressAndReleaseKeys(java.awt.event.KeyEvent.VK_ESCAPE); dialog.requireVisible();
        release.countDown();
        await("The completed write opens the document", () -> GuiActionRunner.execute(() -> !dialog.target().isShowing()));
        Mockito.verify(collection).insert(Mockito.eq("writing"), Mockito.any());
    }

    @Override protected void tearDown() throws Exception {
        ActiveCluster restore = previous; release.countDown();
        try {
            closeDialogs();
            ApplicationManager.getApplication().invokeAndWait(() -> {
                var manager = FileEditorManager.getInstance(getProject());
                for (var file : manager.getOpenFiles()) manager.closeFile(file);
                com.intellij.openapi.fileEditor.impl.EditorHistoryManager.getInstance(getProject()).removeAllFiles();
                com.intellij.openapi.fileEditor.ex.IdeDocumentHistory.getInstance(getProject()).clearHistory();
            });
            super.tearDown();
        } finally { ActiveCluster.setInstance(restore); }
    }
}
