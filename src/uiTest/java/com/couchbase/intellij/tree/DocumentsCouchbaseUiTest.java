package com.couchbase.intellij.tree;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.collections.CollectionSources;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.ex.IdeDocumentHistory;
import com.intellij.openapi.fileEditor.impl.EditorHistoryManager;
import com.intellij.testFramework.PlatformTestUtil;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DocumentsCouchbaseUiTest extends DialogUiTestCase {
    private static final String BUCKET = "ui_tests";

    @ClassRule
    public static final Database DATABASE = new Database();

    private ActiveCluster previousCluster;

    public static final class Database extends ExternalResource {
        private final CouchbaseContainer container = new CouchbaseContainer("couchbase/server:enterprise-8.0.2")
                .withCredentials("Administrator", "ui-test-password")
                .withBucket(new BucketDefinition(BUCKET).withPrimaryIndex(false));
        private Cluster cluster;

        @Override
        protected void before() throws Throwable {
            try {
                container.start();
                cluster = Cluster.connect(container.getConnectionString(), container.getUsername(), container.getPassword());
                cluster.waitUntilReady(Duration.ofSeconds(60));
                cluster.bucket(BUCKET).waitUntilReady(Duration.ofSeconds(60));
                for (String name : List.of("airline", "airport", "hotel")) {
                    cluster.bucket(BUCKET).collections().createCollection("_default", name);
                }
                cluster.bucket(BUCKET).defaultCollection().upsert("existing-document",
                        JsonObject.create().put("name", "Keep this content"));
            } catch (Throwable failure) {
                try {
                    after();
                } catch (Throwable cleanupFailure) {
                    failure.addSuppressed(cleanupFailure);
                }
                throw failure;
            }
        }

        @Override
        protected void after() {
            try {
                if (cluster != null) {
                    cluster.disconnect();
                }
            } finally {
                container.stop();
            }
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        previousCluster = ActiveCluster.getInstance();
        GuiActionRunner.execute(() -> {
            ActiveCluster active = new UiTestCluster();
            active.setCluster(DATABASE.cluster);
            active.setServices(List.of());
            ActiveCluster.setInstance(active);
        });
    }

    private static final class UiTestCluster extends ActiveCluster {
        private final com.couchbase.intellij.persistence.SavedCluster settings = new com.couchbase.intellij.persistence.SavedCluster();
        private UiTestCluster() { settings.setId("ui-documents"); settings.setName("UI documents"); }
        @Override public String getId() { return settings.getId(); }
        @Override public com.couchbase.intellij.persistence.SavedCluster getSavedCluster() { return settings; }
        @Override public boolean isReadOnlyMode() { return Boolean.TRUE.equals(settings.isReadOnly()); }
    }

    private DialogFixture documentDialog(boolean create) {
        return showDialog(() -> new OpenDocumentDialog(create, getProject(), null,
                BUCKET, "_default", "_default"));
    }

    private void documentIsOpen(String id) {
        await("document editor opens", () -> GuiActionRunner.execute(() ->
                Arrays.stream(FileEditorManager.getInstance(getProject()).getOpenFiles())
                        .anyMatch(file -> file.getName().equals(id + ".json"))));
    }

    @Test
    public void missingDocumentShowsErrorAndAnExistingIdCanBeOpened() {
        DialogFixture dialog = documentDialog(false);
        dialog.textBox("documentId").enterText("missing-document");
        dialog.button(JButtonMatcher.withText("OK")).click();
        await("missing document is reported", () -> GuiActionRunner.execute(() -> "There is no document with the specified id.".equals(dialog.label("documentIdError").text())));
        dialog.requireVisible();
        dialog.textBox("documentId").deleteText().enterText("existing-document");
        dialog.button(JButtonMatcher.withText("OK")).click();
        await("dialog closes", () -> GuiActionRunner.execute(() -> !dialog.target().isShowing()));
        documentIsOpen("existing-document");
    }

    @Test
    public void newDocumentIsPersistedAndOpenedInTheEditor() {
        DialogFixture dialog = documentDialog(true);
        dialog.textBox("documentId").enterText("created-by-ui");
        dialog.button(JButtonMatcher.withText("OK")).click();
        await("dialog closes", () -> GuiActionRunner.execute(() -> !dialog.target().isShowing()));
        assertEquals(JsonObject.create(), DATABASE.cluster.bucket(BUCKET).defaultCollection()
                .get("created-by-ui").contentAsObject());
        documentIsOpen("created-by-ui");
    }

    @Test
    public void creatingAnExistingDocumentPreservesItsContent() {
        DialogFixture dialog = documentDialog(true);
        dialog.textBox("documentId").enterText("existing-document");
        dialog.button(JButtonMatcher.withText("OK")).click();
        await("dialog closes", () -> GuiActionRunner.execute(() -> !dialog.target().isShowing()));
        assertEquals("Keep this content", DATABASE.cluster.bucket(BUCKET).defaultCollection()
                .get("existing-document").contentAsObject().getString("name"));
        documentIsOpen("existing-document");
    }

    @Test
    public void collectionPickerSearchesTheDockerCluster() {
        var source = new com.couchbase.intellij.database.collections.QueryCollectionSource(DATABASE.cluster, BUCKET, "_default");
        assertEquals(List.of("_default", "airline", "airport", "hotel"), source.page(0, 10));
        assertEquals(4, source.count());
        List<String> chosen = new CopyOnWriteArrayList<>();
        DialogFixture dialog = showDialog(() -> new CollectionSearchDialog(getProject(),
                CollectionSources.forScope(BUCKET, "_default"), chosen::add));
        dialog.textBox("collectionSearchField").click().enterText("air");
        await("server collections are listed", () -> GuiActionRunner.execute(() ->
                Arrays.equals(new String[]{"airline", "airport"}, dialog.list("collectionSearchResults").contents())));
        dialog.list("collectionSearchResults").item("airport").doubleClick();
        assertEquals(List.of("airport"), chosen);
        await("dialog closes", () -> GuiActionRunner.execute(() -> !dialog.target().isShowing()));
    }

    @Test
    public void casProtectedSaveRejectsConcurrentChangesAndPreservesExpiration() throws Exception {
        var collection = DATABASE.cluster.bucket(BUCKET).defaultCollection();
        collection.upsert("cas-and-expiry", JsonObject.create().put("value", "original"),
                com.couchbase.client.java.kv.UpsertOptions.upsertOptions().expiry(Duration.ofMinutes(10)));
        var original = collection.get("cas-and-expiry", com.couchbase.client.java.kv.GetOptions.getOptions().withExpiry(true));
        com.couchbase.intellij.persistence.CouchbaseDocumentSaver.save(collection, "cas-and-expiry", "{\"value\":\"saved\"}", original.cas());
        var saved = collection.get("cas-and-expiry", com.couchbase.client.java.kv.GetOptions.getOptions().withExpiry(true));
        assertEquals(original.expiryTime(), saved.expiryTime());
        assertThrows(com.couchbase.client.core.error.CasMismatchException.class, () ->
                com.couchbase.intellij.persistence.CouchbaseDocumentSaver.save(collection, "cas-and-expiry", "{\"value\":\"stale\"}", original.cas()));
        assertEquals("saved", collection.get("cas-and-expiry").contentAsObject().getString("value"));
        assertThrows(com.couchbase.client.core.error.DocumentExistsException.class, () ->
                com.couchbase.intellij.persistence.CouchbaseDocumentSaver.save(collection, "cas-and-expiry", "{}", null));
    }

    @Test public void readOnlySelectScriptsStillRunWithServerSideReadOnlyEnabled() {
        ActiveCluster.getInstance().getSavedCluster().setReadOnly(true);
        var output = org.mockito.Mockito.mock(com.couchbase.intellij.workbench.QueryResultToolWindowFactory.class);
        try (var windows = org.mockito.Mockito.mockStatic(com.couchbase.intellij.workbench.QueryResultUtil.class)) {
            windows.when(() -> com.couchbase.intellij.workbench.QueryResultUtil.getOutputWindow(getProject())).thenReturn(output);
            assertTrue(com.couchbase.intellij.workbench.QueryExecutor.executeScript(new java.util.concurrent.LinkedBlockingQueue<>(),
                    com.couchbase.intellij.workbench.QueryExecutor.QueryType.NORMAL, null,
                    List.of("SELECT 1 AS result", "SELECT 2 AS result"), -1, getProject()));
            org.mockito.Mockito.verify(output).updateQueryStats(org.mockito.Mockito.anyList(),
                    org.mockito.Mockito.argThat(rows -> rows.size() == 2 && rows.stream().allMatch(row -> "success".equals(row.getString("_sequence_query_status")))),
                    org.mockito.Mockito.any(), org.mockito.Mockito.isNull(), org.mockito.Mockito.eq(true));
        } finally { ActiveCluster.getInstance().getSavedCluster().setReadOnly(false); }
    }

    private com.intellij.openapi.editor.Document editorDocument(String id) {
        com.intellij.openapi.editor.Document[] result = {null};
        com.intellij.openapi.application.ApplicationManager.getApplication().invokeAndWait(() -> {
            var file = Arrays.stream(FileEditorManager.getInstance(getProject()).getOpenFiles())
                    .filter(f -> f.getName().equals(id + ".json")).findFirst().orElseThrow();
            result[0] = com.intellij.openapi.fileEditor.FileDocumentManager.getInstance().getDocument(file);
        });
        return result[0];
    }

    private void open(String id) {
        var dialog = documentDialog(false);
        dialog.textBox("documentId").enterText(id);
        dialog.button(JButtonMatcher.withText("OK")).click();
        documentIsOpen(id);
    }

    private void edit(com.intellij.openapi.editor.Document document, String text) {
        com.intellij.openapi.application.ApplicationManager.getApplication().invokeAndWait(() ->
                com.intellij.openapi.application.ApplicationManager.getApplication().runWriteAction(() -> document.setText(text)));
    }

    private void save(com.intellij.openapi.editor.Document document) {
        com.intellij.openapi.application.ApplicationManager.getApplication().invokeAndWait(() ->
                com.intellij.openapi.fileEditor.FileDocumentManager.getInstance().saveDocument(document));
        await("background document save completes", () -> GuiActionRunner.execute(() ->
                !com.couchbase.intellij.persistence.CouchbaseDocumentSaveCoordinator.isSaving(document)));
    }

    private boolean dirty(com.intellij.openapi.editor.Document document) {
        return GuiActionRunner.execute(() -> com.intellij.openapi.fileEditor.FileDocumentManager.getInstance().isDocumentUnsaved(document));
    }

    @Test
    public void editorSaveWritesUtf8AndPreservesDocumentExpiration() {
        var collection = DATABASE.cluster.bucket(BUCKET).defaultCollection();
        collection.upsert("editor-save", JsonObject.create().put("name", "original"),
                com.couchbase.client.java.kv.UpsertOptions.upsertOptions().expiry(Duration.ofMinutes(10)));
        var original = collection.get("editor-save", com.couchbase.client.java.kv.GetOptions.getOptions().withExpiry(true));
        open("editor-save");
        var document = editorDocument("editor-save");
        edit(document, "{\"name\":\"München 東京\"}");
        save(document);
        assertFalse("Successful saves clear the dirty state", dirty(document));
        var saved = collection.get("editor-save", com.couchbase.client.java.kv.GetOptions.getOptions().withExpiry(true));
        assertEquals("München 東京", saved.contentAsObject().getString("name"));
        assertEquals(original.expiryTime(), saved.expiryTime());
    }

    @Test
    public void conflictCancelKeepsEditsAndOverwriteRequiresASecondSave() {
        var collection = DATABASE.cluster.bucket(BUCKET).defaultCollection();
        collection.upsert("editor-conflict", JsonObject.create().put("name", "original"));
        open("editor-conflict");
        var document = editorDocument("editor-conflict");
        edit(document, "{\"name\":\"my edits\"}");
        collection.upsert("editor-conflict", JsonObject.create().put("name", "server edits"));
        var previous = com.intellij.openapi.ui.TestDialogManager.setTestDialog(message -> 2);
        try {
            save(document);
            assertTrue(dirty(document));
            assertEquals("server edits", collection.get("editor-conflict").contentAsObject().getString("name"));
            com.intellij.openapi.ui.TestDialogManager.setTestDialog(message -> 0);
            save(document);
            assertFalse(dirty(document));
            assertEquals("my edits", collection.get("editor-conflict").contentAsObject().getString("name"));
        } finally { com.intellij.openapi.ui.TestDialogManager.setTestDialog(previous); }
    }

    @Test
    public void invalidJsonReadOnlyAndWrongConnectionKeepEditsWithoutWriting() {
        var collection = DATABASE.cluster.bucket(BUCKET).defaultCollection();
        collection.upsert("guarded-save", JsonObject.create().put("name", "original"));
        open("guarded-save");
        var document = editorDocument("guarded-save");
        var file = GuiActionRunner.execute(() -> com.intellij.openapi.fileEditor.FileDocumentManager.getInstance().getFile(document));
        var connection = file.getUserData(com.couchbase.intellij.VirtualFileKeys.CONN_ID);
        try {
            edit(document, "{invalid"); save(document);
            assertTrue("Invalid JSON stays unsaved", dirty(document));
            assertEquals("original", collection.get("guarded-save").contentAsObject().getString("name"));
            edit(document, "{\"name\":\"my edits\"}");
            ActiveCluster.getInstance().getSavedCluster().setReadOnly(true);
            save(document);
            assertTrue("Read-only connection keeps edits", dirty(document));
            assertEquals("original", collection.get("guarded-save").contentAsObject().getString("name"));
            ActiveCluster.getInstance().getSavedCluster().setReadOnly(false);
            file.putUserData(com.couchbase.intellij.VirtualFileKeys.CONN_ID, "another-connection");
            save(document);
            assertTrue("Wrong connection cannot save", dirty(document));
            assertEquals("original", collection.get("guarded-save").contentAsObject().getString("name"));
            file.putUserData(com.couchbase.intellij.VirtualFileKeys.CONN_ID, connection);
            save(document);
            assertFalse(dirty(document));
            assertEquals("my edits", collection.get("guarded-save").contentAsObject().getString("name"));
        } finally {
            ActiveCluster.getInstance().getSavedCluster().setReadOnly(false);
            file.putUserData(com.couchbase.intellij.VirtualFileKeys.CONN_ID, connection);
        }
    }

    @Test
    public void deletedDocumentRequiresExplicitRecreationAndAllowsRetry() {
        var collection = DATABASE.cluster.bucket(BUCKET).defaultCollection();
        collection.upsert("deleted-save", JsonObject.create().put("name", "original"));
        open("deleted-save");
        var document = editorDocument("deleted-save");
        edit(document, "{\"name\":\"recreated\"}");
        collection.remove("deleted-save");
        var previous = com.intellij.openapi.ui.TestDialogManager.setTestDialog(message -> 1);
        try {
            save(document);
            assertTrue(dirty(document)); assertFalse(collection.exists("deleted-save").exists());
            com.intellij.openapi.ui.TestDialogManager.setTestDialog(message -> 0);
            save(document);
            assertFalse(dirty(document));
            assertEquals("recreated", collection.get("deleted-save").contentAsObject().getString("name"));
        } finally { com.intellij.openapi.ui.TestDialogManager.setTestDialog(previous); }
    }

    @Test
    public void conflictReloadReplacesEditorContentWithoutWritingOverTheServer() {
        var collection = DATABASE.cluster.bucket(BUCKET).defaultCollection();
        collection.upsert("reload-save", JsonObject.create().put("name", "original"));
        open("reload-save");
        var document = editorDocument("reload-save");
        edit(document, "{\"name\":\"my edits\"}");
        var server = collection.upsert("reload-save", JsonObject.create().put("name", "server edits"));
        var previous = com.intellij.openapi.ui.TestDialogManager.setTestDialog(message -> 1);
        try {
            save(document);
            assertEquals("server edits", JsonObject.fromJson(document.getText()).getString("name"));
            assertEquals(server.cas(), collection.get("reload-save").cas());
            assertFalse("Reload acknowledges the chosen server version", dirty(document));
        } finally { com.intellij.openapi.ui.TestDialogManager.setTestDialog(previous); }
    }

    @Override
    protected void tearDown() throws Exception {
        // The platform clears test instance fields during super.tearDown().
        ActiveCluster clusterToRestore = previousCluster;
        try {
            GuiActionRunner.execute(() -> {
                FileEditorManager manager = FileEditorManager.getInstance(getProject());
                Arrays.stream(manager.getOpenFiles()).forEach(manager::closeFile);
                EditorHistoryManager.getInstance(getProject()).removeAllFiles();
                IdeDocumentHistory.getInstance(getProject()).clearHistory();
            });
        } finally {
            try {
                // Let the fixture drain editor/background work while its cluster is still available.
                super.tearDown();
            } finally {
                PlatformTestUtil.waitForAllBackgroundActivityToCalmDown();
                GuiActionRunner.execute(() -> ActiveCluster.setInstance(clusterToRestore));
            }
        }
    }
}
