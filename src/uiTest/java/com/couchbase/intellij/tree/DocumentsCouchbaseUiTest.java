package com.couchbase.intellij.tree;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.collections.CollectionSources;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.ex.IdeDocumentHistory;
import com.intellij.openapi.fileEditor.impl.EditorHistoryManager;
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
        private UiTestCluster() {
        }
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

    @Override
    protected void tearDown() throws Exception {
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
                GuiActionRunner.execute(() -> ActiveCluster.setInstance(previousCluster));
            }
        }
    }
}
