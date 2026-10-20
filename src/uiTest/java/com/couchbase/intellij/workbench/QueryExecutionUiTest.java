package com.couchbase.intellij.workbench;

import com.couchbase.client.java.*;
import com.couchbase.client.java.query.QueryResult;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.QueryContext;
import com.couchbase.intellij.persistence.SavedCluster;
import com.couchbase.intellij.tree.DialogUiTestCase;
import com.couchbase.intellij.utils.Subscribable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.*;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.testFramework.ServiceContainerUtil;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.intellij.sdk.language.SQLPPFileType;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.AdditionalAnswers;
import javax.swing.*;
import java.util.concurrent.*;

public class QueryExecutionUiTest extends DialogUiTestCase {
    @Test public void pendingQueryLeavesTheUiResponsiveAndCancelStopsWaiting() throws Exception {
        ActiveCluster previous = ActiveCluster.getInstance();
        QueryResultToolWindowFactory previousOutput = QueryResultToolWindowFactory.instance;
        var output = Mockito.mock(QueryResultToolWindowFactory.class);
        var active = Mockito.mock(ActiveCluster.class);
        var sdk = Mockito.mock(Cluster.class);
        var async = Mockito.mock(AsyncCluster.class);
        var settings = new SavedCluster();
        settings.setId("query-test"); settings.getQueryPreferences().setSaveHistory(false);
        Mockito.when(active.get()).thenReturn(sdk);
        Mockito.when(active.getCluster()).thenReturn(sdk);
        Mockito.when(active.getSavedCluster()).thenReturn(settings);
        Mockito.when(active.getQueryContext()).thenReturn(new Subscribable<QueryContext>());
        Mockito.when(sdk.async()).thenReturn(async);
        CompletableFuture<QueryResult> request = new CompletableFuture<>();
        CountDownLatch dispatched = new CountDownLatch(1);
        Mockito.when(async.query(Mockito.anyString(), Mockito.any())).thenAnswer(call -> {
            assertFalse("SDK calls must run outside the UI thread", SwingUtilities.isEventDispatchThread());
            dispatched.countDown(); return request;
        });
        ToolWindowManager real = ToolWindowManager.getInstance(getProject());
        ToolWindowManager manager = Mockito.mock(ToolWindowManager.class, AdditionalAnswers.delegatesTo(real));
        ToolWindow window = Mockito.mock(ToolWindow.class);
        Mockito.doReturn(window).when(manager).getToolWindow("Couchbase Output");
        ServiceContainerUtil.replaceService(getProject(), ToolWindowManager.class, manager, getTestRootDisposable());
        QueryResultToolWindowFactory.instance = output;
        ActiveCluster.setInstance(active);
        JFrame[] frame = {null};
        CustomSqlFileEditor[] editor = {null};
        Timer[] heartbeat = {null};
        // Several timer ticks must be dispatched while the SDK request is pending.
        CountDownLatch ticks = new CountDownLatch(8);
        try {
            ApplicationManager.getApplication().invokeAndWait(() -> {
                editor[0] = new CustomSqlFileEditor(getProject(), new LightVirtualFile("responsive.sqlpp", SQLPPFileType.INSTANCE, "SELECT 1 AS result"));
                frame[0] = new JFrame("Query responsiveness");
                frame[0].setContentPane(editor[0].getComponent());
                frame[0].setSize(1200, 600); frame[0].setVisible(true);
                heartbeat[0] = new Timer(100, event -> ticks.countDown());
            });
            robot.click(button(frame[0], "Execute"));
            assertTrue("Query dispatched", dispatched.await(5, TimeUnit.SECONDS));
            GuiActionRunner.execute(() -> heartbeat[0].start());
            assertTrue("UI events continue while the server response is pending", ticks.await(2, TimeUnit.SECONDS));
            assertFalse(request.isDone());
            await("Cancel is available while the query is running", () -> GuiActionRunner.execute(() -> {
                try { return button(frame[0], "Cancel").isShowing(); } catch (Exception ignored) { return false; }
            }));
            // Invoke the production ActionButton on the EDT after toolbar replacement.
            GuiActionRunner.execute(() -> button(frame[0], "Cancel").click());
            await("SDK request is cancelled", request::isCancelled);
            Mockito.verify(output, Mockito.timeout(2000)).setStatusAsCanceled();
            await("Execute is restored", () -> GuiActionRunner.execute(() -> {
                try { return button(frame[0], "Execute").isShowing(); } catch (Exception ignored) { return false; }
            }));
        } finally {
            request.cancel(true);
            ApplicationManager.getApplication().invokeAndWait(() -> {
                if (heartbeat[0] != null) heartbeat[0].stop();
                if (editor[0] != null) Disposer.dispose(editor[0]);
                if (frame[0] != null) frame[0].dispose();
            });
            QueryResultToolWindowFactory.instance = previousOutput;
            ActiveCluster.setInstance(previous);
        }
    }

    @Test public void executeRejectsAMutationInAReadOnlyScriptBeforeCallingTheSdk() {
        ActiveCluster previous = ActiveCluster.getInstance();
        var previousOutput = QueryResultToolWindowFactory.instance;
        var output = Mockito.mock(QueryResultToolWindowFactory.class);
        var active = Mockito.mock(ActiveCluster.class);
        var sdk = Mockito.mock(Cluster.class);
        var settings = new SavedCluster(); settings.setId("readonly-script-ui");
        Mockito.when(active.get()).thenReturn(sdk); Mockito.when(active.getCluster()).thenReturn(sdk);
        Mockito.when(active.getSavedCluster()).thenReturn(settings);
        Mockito.when(active.isReadOnlyMode()).thenReturn(true);
        Mockito.when(active.getQueryContext()).thenReturn(new Subscribable<QueryContext>());
        ToolWindowManager real = ToolWindowManager.getInstance(getProject());
        ToolWindowManager manager = Mockito.mock(ToolWindowManager.class, AdditionalAnswers.delegatesTo(real));
        Mockito.doReturn(Mockito.mock(ToolWindow.class)).when(manager).getToolWindow("Couchbase Output");
        ServiceContainerUtil.replaceService(getProject(), ToolWindowManager.class, manager, getTestRootDisposable());
        QueryResultToolWindowFactory.instance = output; ActiveCluster.setInstance(active);
        JFrame[] frame = {null}; CustomSqlFileEditor[] editor = {null};
        try {
            ApplicationManager.getApplication().invokeAndWait(() -> {
                editor[0] = new CustomSqlFileEditor(getProject(), new LightVirtualFile("readonly.sqlpp", SQLPPFileType.INSTANCE,
                        "SELECT 1 AS result;\nUPDATE `bucket`.`scope`.`collection` SET value = 1;"));
                frame[0] = new JFrame("Read-only script"); frame[0].setContentPane(editor[0].getComponent());
                frame[0].setSize(1200, 600); frame[0].setVisible(true);
            });
            robot.click(button(frame[0], "Execute"));
            Mockito.verify(output, Mockito.timeout(5000)).updateQueryStats(Mockito.anyList(), Mockito.isNull(),
                    Mockito.argThat(error -> error.getErrors().getFirst().getMessage().contains("read-only")), Mockito.isNull(), Mockito.eq(false));
            Mockito.verifyNoInteractions(sdk);
        } finally {
            ApplicationManager.getApplication().invokeAndWait(() -> {
                if (editor[0] != null) Disposer.dispose(editor[0]);
                if (frame[0] != null) frame[0].dispose();
            });
            QueryResultToolWindowFactory.instance = previousOutput; ActiveCluster.setInstance(previous);
        }
    }

    private ActionButton button(JFrame frame, String text) {
        return robot.finder().find(frame, new GenericTypeMatcher<>(ActionButton.class) {
            @Override protected boolean isMatching(ActionButton component) {
                return component.isShowing() && text.equals(component.getAction().getTemplatePresentation().getText());
            }
        });
    }
}
