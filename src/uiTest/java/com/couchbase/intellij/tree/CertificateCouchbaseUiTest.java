package com.couchbase.intellij.tree;

import com.couchbase.client.java.Cluster;
import com.couchbase.intellij.database.*;
import com.couchbase.intellij.persistence.SavedCluster;
import com.couchbase.intellij.persistence.QueryFilter;
import com.couchbase.intellij.tree.overview.apis.CouchbaseRestAPI;
import com.couchbase.intellij.tree.node.*;
import com.intellij.ui.treeStructure.Tree;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.junit.*;
import java.nio.file.*;
import java.time.Duration;
import java.util.List;
import javax.swing.*;
import javax.swing.tree.*;
import javax.imageio.ImageIO;
import java.awt.*;

/** Opt-in live certificate fixture: tools/start-certificate-test-cluster.py, -PcertificateDemoTests=true. */
public class CertificateCouchbaseUiTest extends DialogUiTestCase {
    private static final Path CERTIFICATES = Path.of("build/certificate-demo").toAbsolutePath();

    @Override protected void setUp() throws Exception {
        Assume.assumeTrue("Enable with -PcertificateDemoTests=true after starting the certificate demo", Boolean.getBoolean("cb.certificate.demo"));
        super.setUp();
    }

    private SavedCluster settings() {
        SavedCluster settings = new SavedCluster();
        settings.setId("certificate:TLS test"); settings.setName("TLS test"); settings.setUsername("");
        settings.setUrl("couchbases://localhost"); settings.setSslEnable(true);
        settings.setAuthenticationType("certificate");
        settings.setClientKeyStorePath(CERTIFICATES.resolve("client.p12").toString());
        settings.setServerCaCertificatePath(CERTIFICATES.resolve("ca.pem").toString());
        return settings;
    }

    @Test public void testConnectionUsesCertificateAndKeepsTheDialogResponsive() throws Exception {
        var dialog = showDialog(() -> new NewConnectionDialog(getProject(), new Tree(), null, null));
        screenshot("connection-password.png");
        dialog.comboBox("connectionAuthentication").selectItem("Client certificate");
        dialog.textBox("connectionName").enterText("Certificate demo");
        dialog.textBox("connectionHost").enterText("couchbases://localhost");
        dialog.textBox("connectionKeyStore").enterText(CERTIFICATES.resolve("client.p12").toString());
        dialog.textBox("connectionKeyStorePassword").enterText("wrong-password");
        dialog.textBox("connectionServerCa").enterText(CERTIFICATES.resolve("ca.pem").toString());
        dialog.button(JButtonMatcher.withText("Test Connection")).click();
        await("incorrect keystore password is reported", () -> GuiActionRunner.execute(() ->
                dialog.label("connectionError").text().contains("Could not open the client keystore")));
        dialog.button(JButtonMatcher.withText("Test Connection")).requireEnabled();
        dialog.textBox("connectionKeyStorePassword").deleteText().enterText("changeit");
        dialog.button(JButtonMatcher.withText("Test Connection")).click();
        await("certificate connection succeeds", () -> GuiActionRunner.execute(() ->
                "Connection was successful.".equals(dialog.label("connectionStatus").text())));
        dialog.checkBox("connectionSsl").requireSelected().requireDisabled();
        screenshot("connection-certificate.png");
    }

    @Test public void explorerRestAndSdkOperationsUseTheSameCertificateIdentity() throws Exception {
        SavedCluster settings = settings();
        ConnectionAuthentication auth = ConnectionAuthentication.load(settings, "changeit");
        Cluster sdk = Cluster.connect(settings.getUrl(), auth.clusterOptions());
        ActiveCluster previous = ActiveCluster.getInstance();
        try {
            sdk.waitUntilReady(Duration.ofSeconds(15));
            ActiveCluster active = new ActiveCluster() {
                @Override public SavedCluster getSavedCluster() { return settings; }
                @Override public String getId() { return settings.getId(); }
                @Override public String getClusterURL() { return "localhost"; }
                @Override public boolean isSSLEnabled() { return true; }
                @Override public ConnectionAuthentication getAuthentication() { return auth; }
                @Override public String getPassword() { throw new AssertionError("Certificate connections must not use a database password"); }
            };
            active.setCluster(sdk);
            ActiveCluster.setInstance(active);
            assertFalse(CouchbaseRestAPI.getOverview().getNodes().isEmpty());
            assertNotNull(CouchbaseRestAPI.callWhoAmIEndpoint());
            var bucket = sdk.bucket("certificate_test"); bucket.waitUntilReady(Duration.ofSeconds(15));
            bucket.defaultCollection().upsert("certificate-ui-test", com.couchbase.client.java.json.JsonObject.create().put("certificate", true));
            assertTrue(bucket.defaultCollection().get("certificate-ui-test").contentAsObject().getBoolean("certificate"));
            assertEquals(1, sdk.query("SELECT 1 AS result").rowsAsObject().getFirst().getInt("result").intValue());
            var collections = new com.couchbase.intellij.database.collections.QueryCollectionSource(sdk, "certificate_test", "_default");
            assertTrue("Default collection must be visible in the explorer", collections.page(0, 10).contains("_default"));
        } finally { ActiveCluster.setInstance(previous); sdk.disconnect(); }
    }

    @Test public void emptyCollectionsHideSchemaAndRefreshRestoresItWithoutConfusingFiltersOrPaging() throws Exception {
        SavedCluster settings = settings();
        ConnectionAuthentication auth = ConnectionAuthentication.load(settings, "changeit");
        Cluster sdk = Cluster.connect(settings.getUrl(), auth.clusterOptions());
        ActiveCluster previous = ActiveCluster.getInstance();
        String name = "schema_ui_" + Long.toUnsignedString(System.nanoTime());
        JFrame frame = null;
        boolean created = false;
        try {
            sdk.waitUntilReady(Duration.ofSeconds(15));
            var bucket = sdk.bucket("certificate_test"); bucket.waitUntilReady(Duration.ofSeconds(15));
            bucket.collections().createCollection("_default", name); created = true;
            ActiveCluster active = new ActiveCluster() {
                @Override public SavedCluster getSavedCluster() { return settings; }
                @Override public String getId() { return settings.getId(); }
                @Override public String getClusterURL() { return "localhost"; }
                @Override public boolean isSSLEnabled() { return true; }
                @Override public ConnectionAuthentication getAuthentication() { return auth; }
            };
            active.setCluster(sdk); active.setServices(List.of("n1ql")); ActiveCluster.setInstance(active);
            var descriptor = new CollectionNodeDescriptor(name, settings.getId(), "certificate_test", "_default", null);
            var parent = new DefaultMutableTreeNode(descriptor);
            var root = new DefaultMutableTreeNode("root"); root.add(parent);
            Tree tree = GuiActionRunner.execute(() -> new CollectionExplorerTree(getProject(), new DefaultTreeModel(root)));
            frame = GuiActionRunner.execute(() -> {
                tree.setCellRenderer(new CouchbaseWindowContent.NodeDescriptorRenderer());
                JFrame window = new JFrame("Schema visibility"); window.setContentPane(new JScrollPane(tree));
                window.setSize(700, 500); window.setVisible(true); return window;
            });
            reloadCollection(parent, tree);
            assertFalse("Empty collections have no Schema row", hasChild(parent, SchemaNodeDescriptor.class));
            assertTrue("Indexes remain available on empty collections", hasChild(parent, IndexesNodeDescriptor.class));

            var collection = bucket.scope("_default").collection(name);
            collection.upsert("document", com.couchbase.client.java.json.JsonObject.create().put("type", "present"));
            await("REST document listing observes the inserted document", () -> collectionHasDocuments(name));
            reloadCollection(parent, tree);
            assertTrue("Refreshing after inserting a document restores Schema", hasChild(parent, SchemaNodeDescriptor.class));
            assertTrue(hasChild(parent, FileNodeDescriptor.class));

            collection.queryIndexes().createIndex("by_missing_field", List.of("missing_field"));
            collection.queryIndexes().watchIndexes(List.of("by_missing_field"), Duration.ofSeconds(15));
            reloadCollection(parent, tree);
            assertFalse(hasChild(parent, FileNodeDescriptor.class));
            assertTrue("An index that excludes the document does not hide Schema", hasChild(parent, SchemaNodeDescriptor.class));
            QueryFilter filter = new QueryFilter(); filter.setQuery("type = 'missing'");
            GuiActionRunner.execute(() -> descriptor.setQueryFilter(filter));
            reloadCollection(parent, tree);
            assertFalse(hasChild(parent, FileNodeDescriptor.class));
            assertTrue("Zero filter matches do not hide Schema on a populated collection", hasChild(parent, SchemaNodeDescriptor.class));

            QueryFilter offset = new QueryFilter(); offset.setOffset(100);
            GuiActionRunner.execute(() -> descriptor.setQueryFilter(offset));
            reloadCollection(parent, tree);
            assertTrue("An offset beyond the last document does not hide Schema", hasChild(parent, SchemaNodeDescriptor.class));

            collection.remove("document");
            await("REST document listing observes the removal", () -> !collectionHasDocuments(name));
            GuiActionRunner.execute(() -> descriptor.setQueryFilter(filter));
            reloadCollection(parent, tree);
            assertFalse("An indexed collection emptied while filtered also hides Schema", hasChild(parent, SchemaNodeDescriptor.class));
        } finally {
            JFrame window = frame;
            if (window != null) GuiActionRunner.execute(window::dispose);
            ActiveCluster.setInstance(previous);
            try { if (created) sdk.bucket("certificate_test").collections().dropCollection("_default", name); }
            finally { sdk.disconnect(); }
        }
    }

    private void reloadCollection(DefaultMutableTreeNode parent, Tree tree) {
        GuiActionRunner.execute(() -> DataLoader.listDocuments(parent, tree, 0));
        await("Collection documents finish loading", () -> !hasChild(parent, LoadingNodeDescriptor.class));
        assertFalse("Loading succeeds", hasChild(parent, LoadErrorNodeDescriptor.class));
    }

    private boolean collectionHasDocuments(String name) {
        try { return !CouchbaseRestAPI.listKVDocuments("certificate_test", "_default", name, 0, 1, null, null).isEmpty(); }
        catch (Exception error) { throw new AssertionError("Could not read the test collection", error); }
    }

    private boolean hasChild(DefaultMutableTreeNode parent, Class<?> type) {
        return GuiActionRunner.execute(() -> java.util.Collections.list(parent.children()).stream()
                .anyMatch(child -> type.isInstance(((DefaultMutableTreeNode) child).getUserObject())));
    }

    private void screenshot(String name) throws Exception {
        Path artifacts = Path.of(System.getProperty("ui.test.artifacts")); Files.createDirectories(artifacts);
        ImageIO.write(new java.awt.Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())),
                "png", artifacts.resolve(name).toFile());
    }
}
