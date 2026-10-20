package com.couchbase.intellij.tree;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.intellij.VirtualFileKeys;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.persistence.CouchbaseDocumentVirtualFile;
import com.couchbase.intellij.persistence.SavedCluster;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.CodeCompletionHandlerBase;
import com.intellij.codeInsight.lookup.impl.LookupImpl;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import org.assertj.swing.edt.GuiActionRunner;
import org.junit.Test;
import org.mockito.Mockito;

import javax.swing.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.awt.event.KeyEvent;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/** Exercises native typing and the actual JSON completion contributor on remote documents. */
public class JsonDocumentEditingUiTest extends DialogUiTestCase {
    private ActiveCluster previous, active;
    private Cluster sdk;
    private SavedCluster settings;
    private JFrame frame;
    private Editor editor;
    private java.lang.reflect.Field headlessFlag;
    private boolean previousHeadless;
    private java.lang.reflect.Field testModeFlag;
    private boolean previousTestMode;

    @Override protected void setUp() throws Exception {
        super.setUp(); previous = ActiveCluster.getInstance();
        sdk = Mockito.mock(Cluster.class, Mockito.RETURNS_DEEP_STUBS);
        settings = Mockito.mock(SavedCluster.class);
        Mockito.when(settings.isInferCacheValid(Mockito.anyString())).thenReturn(true);
        active = Mockito.spy(new ActiveCluster() {
            @Override public SavedCluster getSavedCluster() { return settings; }
            @Override public String getId() { return "json-edit-ui"; }
            @Override public boolean isReadOnlyMode() { return false; }
        });
        active.setCluster(sdk); active.setServices(List.of("n1ql")); ActiveCluster.setInstance(active);
    }

    private void metadata(JsonObject schema) {
        Mockito.when(settings.getInferCacheValue(Mockito.anyString())).thenReturn(schema);
        Mockito.when(sdk.buckets().getAllBuckets()).thenReturn(Map.of("bucket", Mockito.mock(BucketSettings.class)));
        ScopeSpec scope = Mockito.mock(ScopeSpec.class);
        Mockito.when(scope.name()).thenReturn("scope");
        Mockito.when(scope.collections()).thenReturn(Set.of(CollectionSpec.create("collection", "scope")));
        Mockito.when(sdk.bucket("bucket").collections().getAllScopes()).thenReturn(List.of(scope));
        active.updateSchema();
        assertTrue(active.getCachedCollection("bucket", "scope", "collection").isPresent());
        Mockito.clearInvocations(sdk);
        Mockito.clearInvocations(active);
    }

    private void openDocument() {
        ApplicationManager.getApplication().invokeAndWait(() -> {
            var file = new CouchbaseDocumentVirtualFile(getProject(), JsonFileType.INSTANCE,
                    "bucket", "scope", "collection", "editing");
            file.putUserData(VirtualFileKeys.CONN_ID, active.getId());
            file.putUserData(VirtualFileKeys.CLUSTER, active.getId());
            file.putUserData(VirtualFileKeys.BUCKET, "bucket");
            file.putUserData(VirtualFileKeys.SCOPE, "scope");
            file.putUserData(VirtualFileKeys.COLLECTION, "collection");
            file.acceptServerContent("{\n  \"existing\": 1,\n  \"\"\n}".getBytes(StandardCharsets.UTF_8), 1);
            myFixture.openFileInEditor(file);
            editor = myFixture.getEditor();
            editor.getCaretModel().moveToOffset(editor.getDocument().getText().lastIndexOf("\"\"") + 1);
            frame = new JFrame("Couchbase JSON editing"); frame.setContentPane(editor.getComponent());
            frame.setSize(900, 600);
        });
    }

    private List<String> completeWithoutBlocking() throws Exception {
        FutureTask<List<String>> completion = new FutureTask<>(() -> {
            var items = myFixture.complete(CompletionType.BASIC);
            return items == null ? List.of() : java.util.Arrays.stream(items)
                    .map(com.intellij.codeInsight.lookup.LookupElement::getLookupString).toList();
        });
        ApplicationManager.getApplication().executeOnPooledThread(completion);
        try {
            List<String> items = completion.get(5, TimeUnit.SECONDS);
            Mockito.verify(active, Mockito.atLeastOnce()).getCachedCollection("bucket", "scope", "collection");
            return items;
        }
        finally {
            // A regression must fail within a bound and still release the platform read lock for teardown.
            if (!completion.isDone()) cancelBlockedCompletion();
        }
    }

    private void typeAndCheckResponsiveness() {
        GuiActionRunner.execute(() -> {
            LookupManager.getInstance(getProject()).hideActiveLookup();
            frame.setVisible(true);
            editor.getContentComponent().requestFocusInWindow();
        });
        robot.waitForIdle(); robot.enterText("z");
        await("Native typing updates the document after completion", () -> GuiActionRunner.execute(() ->
                editor.getDocument().getText().contains("\"z\"")));
        Mockito.verifyNoInteractions(sdk);
    }

    @Test public void pendingOrFailedInferenceCannotBlockCompletionOrTyping() throws Exception {
        metadata(null); openDocument();
        completeWithoutBlocking(); typeAndCheckResponsiveness();
    }

    @Test public void emptyCollectionSchemaCannotBlockCompletionOrTyping() throws Exception {
        metadata(JsonObject.create().put("content", JsonArray.create())); openDocument();
        completeWithoutBlocking(); typeAndCheckResponsiveness();
    }

    @Test public void unloadedMetadataDoesNotStartNetworkRequestsDuringCompletion() throws Exception {
        openDocument(); completeWithoutBlocking(); typeAndCheckResponsiveness();
    }

    @Test public void cachedSchemaStillSuggestsFieldsAndExcludesExistingKeys() throws Exception {
        JsonObject fields = JsonObject.create()
                .put("existing", JsonObject.create().put("type", "number"))
                .put("customerName", JsonObject.create().put("type", "string"))
                .put("email", JsonObject.create().put("type", "string"));
        metadata(JsonObject.create().put("content", JsonArray.from(
                JsonObject.create().put("#docs", 1).put("properties", fields))));
        assertEquals(Set.of("existing", "customerName", "email"),
                active.getCachedCollection("bucket", "scope", "collection").orElseThrow().getAllAttributeNames());
        Mockito.clearInvocations(active);
        openDocument();
        // The light test application suppresses native lookup windows independently of
        // java.awt.headless. Enable the real display path on this same application.
        headlessFlag = com.intellij.openapi.application.impl.ApplicationImpl.class.getDeclaredField("myHeadlessMode");
        headlessFlag.setAccessible(true);
        previousHeadless = headlessFlag.getBoolean(ApplicationManager.getApplication());
        headlessFlag.setBoolean(ApplicationManager.getApplication(), false);
        testModeFlag = com.intellij.openapi.application.impl.ApplicationImpl.class.getDeclaredField("myTestModeFlag");
        testModeFlag.setAccessible(true);
        previousTestMode = testModeFlag.getBoolean(ApplicationManager.getApplication());
        ApplicationManager.getApplication().invokeAndWait(() ->
                com.intellij.codeInsight.lookup.impl.LookupDisplayStrategy.EP_NAME.getPoint().registerExtension(
                        new com.intellij.codeInsight.lookup.impl.LookupDisplayStrategy() {
                            public boolean isAvailable(Editor target) { return target == editor; }
                            public java.awt.Color getBackgroundColor() { return editor.getColorsScheme().getDefaultBackground(); }
                            public void updateLocation(LookupImpl popup, Editor target, java.awt.Point point) {
                                popup.setLocation(new com.intellij.ui.awt.RelativePoint(frame.getRootPane(), point));
                            }
                            public void hideLookup(LookupImpl popup, Editor target) { popup.hide(false); }
                            public void showLookup(LookupImpl popup, Editor target, java.awt.Point point) {
                                // HintManager suppresses hints in the light test application.
                                // Mount the real LookupImpl in our native editor frame instead.
                                try { testModeFlag.setBoolean(ApplicationManager.getApplication(), false); }
                                catch (IllegalAccessException failure) { throw new AssertionError(failure); }
                                popup.setForceShowAsPopup(false);
                                popup.setForceLightweightPopup(true);
                                popup.show(frame.getRootPane(), point.x, point.y, editor.getContentComponent(),
                                        new com.intellij.ui.HintHint(frame.getRootPane(), point).setAwtTooltip(false));
                            }
                        }, com.intellij.openapi.extensions.LoadingOrder.FIRST, getTestRootDisposable()));
        GuiActionRunner.execute(() -> {
            frame.setVisible(true);
            editor.getContentComponent().requestFocusInWindow();
        });
        robot.waitForIdle();
        ApplicationManager.getApplication().invokeLater(() ->
                new CodeCompletionHandlerBase(CompletionType.BASIC).invokeCompletion(getProject(), editor, 1));
        await("The actual completion popup contains the cached fields", () -> GuiActionRunner.execute(() -> {
            var lookup = LookupManager.getActiveLookup(editor);
            return lookup instanceof LookupImpl popup && popup.isShown()
                    && popup.getList().isShowing() && popup.getItems().stream()
                    .map(LookupElement::getLookupString).toList().containsAll(List.of("customerName", "email"));
        }));
        List<String> suggestions = GuiActionRunner.execute(() -> ((LookupImpl) LookupManager.getActiveLookup(editor))
                .getItems().stream().map(LookupElement::getLookupString).toList());
        assertFalse(suggestions.toString(), suggestions.contains("existing"));
        String initial = GuiActionRunner.execute(() -> LookupManager.getActiveLookup(editor).getCurrentItem().getLookupString());
        robot.pressAndReleaseKeys(KeyEvent.VK_DOWN);
        await("Down changes the actual lookup selection", () -> GuiActionRunner.execute(() ->
                !LookupManager.getActiveLookup(editor).getCurrentItem().getLookupString().equals(initial)));
        String selected = GuiActionRunner.execute(() -> LookupManager.getActiveLookup(editor).getCurrentItem().getLookupString());
        robot.pressAndReleaseKeys(KeyEvent.VK_ENTER);
        await("Enter inserts the selected field and closes the popup", () -> GuiActionRunner.execute(() ->
                LookupManager.getActiveLookup(editor) == null && editor.getDocument().getText().contains("\"" + selected + "\"")));
        Mockito.verify(active, Mockito.atLeastOnce()).getCachedCollection("bucket", "scope", "collection");
        Mockito.verifyNoInteractions(sdk);
    }

    private static void cancelBlockedCompletion() {
        Thread.getAllStackTraces().forEach((thread, stack) -> {
            boolean contributor = false, waiting = false;
            for (StackTraceElement entry : stack) {
                contributor |= entry.getClassName().startsWith("org.intellij.sdk.language.JsonKeyCompletionContributor");
                waiting |= entry.getClassName().equals("com.couchbase.intellij.utils.Subscribable");
            }
            if (contributor && waiting) thread.interrupt();
        });
    }

    @Override protected void tearDown() throws Exception {
        ActiveCluster restore = previous;
        try {
            try {
                cancelBlockedCompletion();
                GuiActionRunner.execute(() -> {
                    LookupManager.getInstance(getProject()).hideActiveLookup();
                    if (frame != null) frame.dispose();
                });
            } finally {
                if (testModeFlag != null) testModeFlag.setBoolean(ApplicationManager.getApplication(), previousTestMode);
                if (headlessFlag != null) headlessFlag.setBoolean(ApplicationManager.getApplication(), previousHeadless);
            }
        } finally {
            try { super.tearDown(); }
            finally { ActiveCluster.setInstance(restore); }
        }
    }
}
