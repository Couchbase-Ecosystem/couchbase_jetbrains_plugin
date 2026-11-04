package com.couchbase.intellij.tree;

import com.couchbase.client.java.Cluster;
import com.couchbase.intellij.database.*;
import com.intellij.openapi.application.ApplicationManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;

public class AsyncConnectionUiTest extends DialogUiTestCase {
    private ActiveCluster previous, active;
    private final AtomicReference<Cluster> sdk = new AtomicReference<>();
    private final AtomicReference<String> id = new AtomicReference<>("A");
    private final CountDownLatch started = new CountDownLatch(1), release = new CountDownLatch(1);

    @Override protected void setUp() throws Exception {
        super.setUp(); previous = ActiveCluster.INSTANCE.getValue();
        sdk.set(Mockito.mock(Cluster.class)); active = Mockito.mock(ActiveCluster.class);
        Mockito.when(active.get()).thenAnswer(call -> sdk.get());
        Mockito.when(active.getId()).thenAnswer(call -> id.get()); ActiveCluster.setInstance(active);
    }

    @Test public void switchingTheSdkOnTheSameActiveObjectRejectsPendingWork() throws Exception {
        AtomicReference<Exception> error = new AtomicReference<>();
        AtomicReference<Object> success = new AtomicReference<>();
        ApplicationManager.getApplication().invokeLater(() -> ClusterUiTask.submit(getProject(), active, "Connection regression", connection -> {
            started.countDown(); assertTrue(release.await(10, TimeUnit.SECONDS));
            connection.requireWritable(); return connection.sdk();
        }, success::set, error::set));
        assertTrue(started.await(5, TimeUnit.SECONDS)); Cluster replacement = Mockito.mock(Cluster.class);
        GuiActionRunner.execute(() -> sdk.set(replacement)); release.countDown();
        await("Reconnecting invalidates pending work", () -> error.get() != null);
        assertNull(success.get()); Mockito.verifyNoInteractions(replacement);
    }

    @Test public void reconnectingWithTheSameIdRejectsAnAlreadyFetchedResult() throws Exception {
        AtomicReference<Exception> error = new AtomicReference<>();
        AtomicReference<String> success = new AtomicReference<>();
        ApplicationManager.getApplication().invokeLater(() -> ClusterUiTask.submit(getProject(), active, "Callback regression", connection -> {
            connection.sdk(); started.countDown(); assertTrue(release.await(10, TimeUnit.SECONDS)); return "old result";
        }, success::set, error::set));
        assertTrue(started.await(5, TimeUnit.SECONDS));
        GuiActionRunner.execute(() -> sdk.set(Mockito.mock(Cluster.class))); release.countDown();
        await("Stale results cannot reach the UI", () -> error.get() != null);
        assertNull(success.get());
    }

    @Test public void switchingIdsOnTheSameObjectRejectsAnAlreadyFetchedResult() throws Exception {
        AtomicReference<Exception> error = new AtomicReference<>();
        AtomicReference<String> success = new AtomicReference<>();
        ApplicationManager.getApplication().invokeLater(() -> ClusterUiTask.submit(getProject(), active, "Identity regression", connection -> {
            started.countDown(); assertTrue(release.await(10, TimeUnit.SECONDS)); return "old result";
        }, success::set, error::set));
        assertTrue(started.await(5, TimeUnit.SECONDS));
        GuiActionRunner.execute(() -> id.set("B")); release.countDown();
        await("A changed connection ID rejects the callback", () -> error.get() != null);
        assertNull(success.get());
    }

    @Test public void aConnectionCapturedBeforeConfirmationCannotSubmitToAReplacementSdk() throws Exception {
        var original = ClusterUiTask.capture(active);
        Cluster replacement = Mockito.mock(Cluster.class); sdk.set(replacement);
        AtomicReference<Exception> error = new AtomicReference<>();
        AtomicReference<Object> success = new AtomicReference<>(); AtomicBoolean attempted = new AtomicBoolean();
        ApplicationManager.getApplication().invokeLater(() -> ClusterUiTask.submit(getProject(), original, "Delayed confirmation", connection -> {
            attempted.set(true); connection.requireWritable(); return connection.sdk();
        }, success::set, error::set));
        await("A confirmed stale action is rejected before any SDK work", () -> error.get() != null);
        assertFalse(attempted.get()); assertNull(success.get()); Mockito.verifyNoInteractions(replacement);
    }

    @Test public void documentDeleteActionKeepsTheConnectionFromBeforeItsConfirmation() throws Exception {
        Cluster original = sdk.get(), replacement = Mockito.mock(Cluster.class);
        AtomicReference<String> error = new AtomicReference<>(); AtomicBoolean confirmed = new AtomicBoolean();
        var previousDialog = com.intellij.openapi.ui.TestDialogManager.setTestDialog(message -> {
            if (message.contains("Are you sure")) { confirmed.set(true); sdk.set(replacement); }
            else error.set(message);
            return com.intellij.openapi.ui.Messages.YES;
        });
        try {
            ApplicationManager.getApplication().invokeAndWait(() -> {
                var node = new javax.swing.tree.DefaultMutableTreeNode(new com.couchbase.intellij.tree.node.FileNodeDescriptor(
                        "doc", "bucket", "scope", "collection", "doc", com.couchbase.intellij.tree.node.FileNodeDescriptor.FileType.JSON, null));
                var root = new javax.swing.tree.DefaultMutableTreeNode("root"); root.add(node);
                var tree = new com.intellij.ui.treeStructure.Tree(new javax.swing.tree.DefaultTreeModel(root));
                var manager = Mockito.mock(com.intellij.openapi.actionSystem.ActionManager.class);
                var popup = Mockito.mock(com.intellij.openapi.actionSystem.ActionPopupMenu.class);
                Mockito.when(popup.getComponent()).thenReturn(Mockito.mock(javax.swing.JPopupMenu.class));
                AtomicReference<com.intellij.openapi.actionSystem.ActionGroup> menu = new AtomicReference<>();
                Mockito.when(manager.createActionPopupMenu(Mockito.anyString(), Mockito.any())).thenAnswer(call -> {
                    menu.set(call.getArgument(1)); return popup;
                });
                try (var actions = Mockito.mockStatic(com.intellij.openapi.actionSystem.ActionManager.class)) {
                    actions.when(com.intellij.openapi.actionSystem.ActionManager::getInstance).thenReturn(manager);
                    TreeRightClickListener.handle(tree, getProject(), null, new java.awt.event.MouseEvent(tree,
                            java.awt.event.MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, 1, 1, 1, true), node);
                }
                var delete = java.util.Arrays.stream(menu.get().getChildren(null))
                        .filter(action -> "Delete Document".equals(action.getTemplatePresentation().getText())).findFirst().orElseThrow();
                delete.actionPerformed(Mockito.mock(com.intellij.openapi.actionSystem.AnActionEvent.class));
            });
            await("The actual delete action reports a stale confirmation", () -> error.get() != null);
            assertTrue(confirmed.get()); assertTrue(error.get(), error.get().toLowerCase().contains("connection"));
            Mockito.verifyNoInteractions(original, replacement);
        } finally { com.intellij.openapi.ui.TestDialogManager.setTestDialog(previousDialog); }
    }

    @Override protected void tearDown() throws Exception {
        ActiveCluster restore = previous; release.countDown();
        try { super.tearDown(); } finally { ActiveCluster.setInstance(restore); }
    }
}
