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
        dialog.label("documentIdError").requireText("There is no document with the specified id.");
        dialog.requireVisible();
        dialog.textBox("documentId").deleteText().enterText("existing-document");
        dialog.button(JButtonMatcher.withText("OK")).click();
        dialog.requireNotVisible();
        documentIsOpen("existing-document");
    }

    @Test
    public void newDocumentIsPersistedAndOpenedInTheEditor() {
        DialogFixture dialog = documentDialog(true);
        dialog.textBox("documentId").enterText("created-by-ui");
        dialog.button(JButtonMatcher.withText("OK")).click();
        dialog.requireNotVisible();
        assertEquals(JsonObject.create(), DATABASE.cluster.bucket(BUCKET).defaultCollection()
                .get("created-by-ui").contentAsObject());
        documentIsOpen("created-by-ui");
    }

    @Test
    public void creatingAnExistingDocumentPreservesItsContent() {
        DialogFixture dialog = documentDialog(true);
        dialog.textBox("documentId").enterText("existing-document");
        dialog.button(JButtonMatcher.withText("OK")).click();
        dialog.requireNotVisible();
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
        dialog.requireNotVisible();
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

    @Test
    public void editorSaveWritesUtf8AndPreservesDocumentExpiration() {
        var collection = DATABASE.cluster.bucket(BUCKET).defaultCollection();
        collection.upsert("editor-save", JsonObject.create().put("name", "original"),
                com.couchbase.client.java.kv.UpsertOptions.upsertOptions().expiry(Duration.ofMinutes(10)));
        var original = collection.get("editor-save", com.couchbase.client.java.kv.GetOptions.getOptions().withExpiry(true));
        DialogFixture dialog = documentDialog(false);
        dialog.textBox("documentId").enterText("editor-save");
        dialog.button(JButtonMatcher.withText("OK")).click();
        documentIsOpen("editor-save");
        com.intellij.openapi.application.ApplicationManager.getApplication().invokeAndWait(() -> {
            var file = Arrays.stream(FileEditorManager.getInstance(getProject()).getOpenFiles())
                    .filter(f -> f.getName().equals("editor-save.json")).findFirst().orElseThrow();
            var manager = com.intellij.openapi.fileEditor.FileDocumentManager.getInstance();
            var document = manager.getDocument(file);
            com.intellij.openapi.application.ApplicationManager.getApplication().runWriteAction(
                    () -> document.setText("{\"name\":\"München 東京\"}"));
            manager.saveDocument(document);
            assertFalse("Successful saves clear the dirty state", manager.isDocumentUnsaved(document));
        });
        var saved = collection.get("editor-save", com.couchbase.client.java.kv.GetOptions.getOptions().withExpiry(true));
        assertEquals("München 東京", saved.contentAsObject().getString("name"));
        assertEquals(original.expiryTime(), saved.expiryTime());
    }

    @Test
    public void conflictCancelKeepsEditsAndOverwriteRequiresASecondSave() {
        var collection = DATABASE.cluster.bucket(BUCKET).defaultCollection();
        collection.upsert("editor-conflict", JsonObject.create().put("name", "original"));
        DialogFixture dialog = documentDialog(false);
        dialog.textBox("documentId").enterText("editor-conflict");
        dialog.button(JButtonMatcher.withText("OK")).click();
        documentIsOpen("editor-conflict");
        com.intellij.openapi.application.ApplicationManager.getApplication().invokeAndWait(() -> {
            var file = Arrays.stream(FileEditorManager.getInstance(getProject()).getOpenFiles())
                    .filter(f -> f.getName().equals("editor-conflict.json")).findFirst().orElseThrow();
            var manager = com.intellij.openapi.fileEditor.FileDocumentManager.getInstance();
            var document = manager.getDocument(file);
            com.intellij.openapi.application.ApplicationManager.getApplication().runWriteAction(
                    () -> document.setText("{\"name\":\"my edits\"}"));
            collection.upsert("editor-conflict", JsonObject.create().put("name", "server edits"));
            // Refresh must not silently advance the CAS protecting unsaved editor content.
            file.refresh(false, false);
            var previous = com.intellij.openapi.ui.TestDialogManager.setTestDialog(message -> 2);
            try {
                manager.saveDocument(document);
                assertTrue(manager.isDocumentUnsaved(document));
                assertEquals("server edits", collection.get("editor-conflict").contentAsObject().getString("name"));
                com.intellij.openapi.ui.TestDialogManager.setTestDialog(message -> 0);
                manager.saveDocument(document);
                assertFalse(manager.isDocumentUnsaved(document));
                assertEquals("my edits", collection.get("editor-conflict").contentAsObject().getString("name"));
            } finally { com.intellij.openapi.ui.TestDialogManager.setTestDialog(previous); }
        });
    }

    @Test
    public void invalidJsonReadOnlyAndWrongConnectionKeepEditsWithoutWriting() {
        var collection = DATABASE.cluster.bucket(BUCKET).defaultCollection();
        collection.upsert("guarded-save", JsonObject.create().put("name", "original"));
        var dialog = documentDialog(false);
        dialog.textBox("documentId").enterText("guarded-save");
        dialog.button(JButtonMatcher.withText("OK")).click();
        documentIsOpen("guarded-save");
        com.intellij.openapi.application.ApplicationManager.getApplication().invokeAndWait(() -> {
            var file = Arrays.stream(FileEditorManager.getInstance(getProject()).getOpenFiles())
                    .filter(f -> f.getName().equals("guarded-save.json")).findFirst().orElseThrow();
            var manager = com.intellij.openapi.fileEditor.FileDocumentManager.getInstance();
            var document = manager.getDocument(file);
            var connection = file.getUserData(com.couchbase.intellij.VirtualFileKeys.CONN_ID);
            try {
                com.intellij.openapi.application.ApplicationManager.getApplication().runWriteAction(() -> document.setText("{invalid"));
                manager.saveDocument(document);
                assertTrue("Invalid JSON must stay unsaved", manager.isDocumentUnsaved(document));
                assertEquals("original", collection.get("guarded-save").contentAsObject().getString("name"));
                com.intellij.openapi.application.ApplicationManager.getApplication().runWriteAction(() -> document.setText("{\"name\":\"my edits\"}"));
                ActiveCluster.getInstance().getSavedCluster().setReadOnly(true);
                manager.saveDocument(document);
                assertTrue("Read-only connection must keep edits", manager.isDocumentUnsaved(document));
                assertEquals("original", collection.get("guarded-save").contentAsObject().getString("name"));
                ActiveCluster.getInstance().getSavedCluster().setReadOnly(false);
                file.putUserData(com.couchbase.intellij.VirtualFileKeys.CONN_ID, "another-connection");
                manager.saveDocument(document);
                assertTrue("Switching connections must not save into another cluster", manager.isDocumentUnsaved(document));
                assertEquals("original", collection.get("guarded-save").contentAsObject().getString("name"));
                file.putUserData(com.couchbase.intellij.VirtualFileKeys.CONN_ID, connection);
                manager.saveDocument(document);
                assertFalse(manager.isDocumentUnsaved(document));
                assertEquals("my edits", collection.get("guarded-save").contentAsObject().getString("name"));
            } finally {
                ActiveCluster.getInstance().getSavedCluster().setReadOnly(false);
                file.putUserData(com.couchbase.intellij.VirtualFileKeys.CONN_ID, connection);
            }
        });
    }

    @Test
    public void deletedDocumentRequiresExplicitRecreationAndAllowsRetry() {
        var collection = DATABASE.cluster.bucket(BUCKET).defaultCollection();
        collection.upsert("deleted-save", JsonObject.create().put("name", "original"));
        var dialog = documentDialog(false);
        dialog.textBox("documentId").enterText("deleted-save");
        dialog.button(JButtonMatcher.withText("OK")).click();
        documentIsOpen("deleted-save");
        com.intellij.openapi.application.ApplicationManager.getApplication().invokeAndWait(() -> {
            var file = Arrays.stream(FileEditorManager.getInstance(getProject()).getOpenFiles())
                    .filter(f -> f.getName().equals("deleted-save.json")).findFirst().orElseThrow();
            var manager = com.intellij.openapi.fileEditor.FileDocumentManager.getInstance();
            var document = manager.getDocument(file);
            com.intellij.openapi.application.ApplicationManager.getApplication().runWriteAction(() -> document.setText("{\"name\":\"recreated\"}"));
            collection.remove("deleted-save");
            var previous = com.intellij.openapi.ui.TestDialogManager.setTestDialog(message -> 1);
            try {
                manager.saveDocument(document);
                assertTrue(manager.isDocumentUnsaved(document));
                assertFalse(collection.exists("deleted-save").exists());
                com.intellij.openapi.ui.TestDialogManager.setTestDialog(message -> 0);
                manager.saveDocument(document);
                assertFalse(manager.isDocumentUnsaved(document));
                assertEquals("recreated", collection.get("deleted-save").contentAsObject().getString("name"));
            } finally { com.intellij.openapi.ui.TestDialogManager.setTestDialog(previous); }
        });
    }

    @Test
    public void conflictReloadReplacesEditorContentWithoutWritingOverTheServer() {
        var collection = DATABASE.cluster.bucket(BUCKET).defaultCollection();
        collection.upsert("reload-save", JsonObject.create().put("name", "original"));
        var dialog = documentDialog(false);
        dialog.textBox("documentId").enterText("reload-save");
        dialog.button(JButtonMatcher.withText("OK")).click();
        documentIsOpen("reload-save");
        com.intellij.openapi.application.ApplicationManager.getApplication().invokeAndWait(() -> {
            var file = Arrays.stream(FileEditorManager.getInstance(getProject()).getOpenFiles())
                    .filter(f -> f.getName().equals("reload-save.json")).findFirst().orElseThrow();
            var manager = com.intellij.openapi.fileEditor.FileDocumentManager.getInstance();
            var document = manager.getDocument(file);
            com.intellij.openapi.application.ApplicationManager.getApplication().runWriteAction(() -> document.setText("{\"name\":\"my edits\"}"));
            var server = collection.upsert("reload-save", JsonObject.create().put("name", "server edits"));
            var previous = com.intellij.openapi.ui.TestDialogManager.setTestDialog(message -> 1);
            try {
                manager.saveDocument(document);
                assertEquals("server edits", JsonObject.fromJson(document.getText()).getString("name"));
                assertEquals(server.cas(), collection.get("reload-save").cas());
                assertEquals(Long.toString(server.cas()), file.getUserData(com.couchbase.intellij.VirtualFileKeys.CAS));
            } finally { com.intellij.openapi.ui.TestDialogManager.setTestDialog(previous); }
        });
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
