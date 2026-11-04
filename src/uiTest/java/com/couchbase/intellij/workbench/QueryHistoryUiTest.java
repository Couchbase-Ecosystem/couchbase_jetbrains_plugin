package com.couchbase.intellij.workbench;

import com.couchbase.intellij.database.*;
import com.couchbase.intellij.persistence.storage.QueryHistoryStorage;
import com.couchbase.intellij.tree.DialogUiTestCase;
import com.couchbase.intellij.utils.Subscribable;
import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.testFramework.LightVirtualFile;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.intellij.sdk.language.SQLPPFileType;
import org.junit.Test;
import org.mockito.Mockito;
import javax.swing.*;
import java.util.*;
import java.util.List;

public class QueryHistoryUiTest extends DialogUiTestCase {
    private ActiveCluster previous;
    private List<String> savedHistory;
    private JFrame frame;
    private CustomSqlFileEditor editor;

    @Override protected void setUp() throws Exception {
        super.setUp();
        previous = ActiveCluster.getInstance();
        savedHistory = new ArrayList<>(QueryHistoryStorage.getInstance().getValue().getHistory());
        var active = Mockito.mock(ActiveCluster.class);
        Mockito.when(active.getQueryContext()).thenReturn(new Subscribable<QueryContext>());
        ActiveCluster.setInstance(active);
    }

    private FrameFixture open(List<String> history) {
        ApplicationManager.getApplication().invokeAndWait(() -> {
            QueryHistoryStorage.getInstance().getValue().setHistory(new ArrayList<>(history));
            editor = new CustomSqlFileEditor(getProject(), new LightVirtualFile("history.sqlpp", SQLPPFileType.INSTANCE, "SELECT 'draft'"));
            frame = new JFrame("Query history"); frame.setContentPane(editor.getComponent());
            frame.setSize(1100, 600); frame.setVisible(true);
        });
        return new FrameFixture(robot, frame);
    }

    @Test public void emptyHistoryHasDisabledNavigationAndAnAccurateLabel() {
        var window = open(List.of());
        window.label("queryHistoryPosition").requireText("No history");
        robot.click(button("Previous History")); robot.click(button("Next History"));
        assertEquals("SELECT 'draft'", GuiActionRunner.execute(() -> editor.getEditor().getDocument().getText()));
        window.label("queryHistoryPosition").requireText("No history");
        assertTrue("Primary execution remains visible", GuiActionRunner.execute(() -> button("Execute").isShowing()));
    }

    @Test public void actualHistoryButtonsRestoreDraftAndRecalledEdits() {
        var window = open(List.of("SELECT 'first'", "SELECT 'second'"));
        robot.click(button("Previous History"));
        window.label("queryHistoryPosition").requireText("History (2/2)");
        ApplicationManager.getApplication().invokeAndWait(() -> ApplicationManager.getApplication().runWriteAction(() -> editor.getEditor().getDocument().setText("SELECT 'edited second'")));
        robot.click(button("Previous History"));
        assertEquals("SELECT 'first'", GuiActionRunner.execute(() -> editor.getEditor().getDocument().getText()));
        robot.click(button("Next History"));
        assertEquals("SELECT 'edited second'", GuiActionRunner.execute(() -> editor.getEditor().getDocument().getText()));
        robot.click(button("Next History"));
        assertEquals("SELECT 'draft'", GuiActionRunner.execute(() -> editor.getEditor().getDocument().getText()));
        window.label("queryHistoryPosition").requireText("History (2)");
        robot.click(button("Previous History"));
        assertEquals("SELECT 'edited second'", GuiActionRunner.execute(() -> editor.getEditor().getDocument().getText()));
        robot.click(button("Next History"));
        robot.click(button("Next History"));
        assertEquals("SELECT 'draft'", GuiActionRunner.execute(() -> editor.getEditor().getDocument().getText()));
        assertEquals(List.of("SELECT 'first'", "SELECT 'second'"), QueryHistoryStorage.getInstance().getValue().getHistory());
        robot.click(button("More Query Actions"));
        await("Occasional actions remain available in the menu", () -> GuiActionRunner.execute(() ->
                Arrays.stream(java.awt.Window.getWindows()).filter(java.awt.Window::isShowing).anyMatch(popupWindow -> menuContains(popupWindow, "Saved Queries"))));
        robot.pressAndReleaseKey(java.awt.event.KeyEvent.VK_ESCAPE);
    }

    private static boolean menuContains(java.awt.Component component, String text) {
        if (component instanceof JList<?> list && list.isShowing()) {
            for (int i = 0; i < list.getModel().getSize(); i++) {
                Object value = list.getModel().getElementAt(i);
                if (text.equals(String.valueOf(value))) return true;
            }
        }
        if (component instanceof java.awt.Container container)
            for (java.awt.Component child : container.getComponents()) if (menuContains(child, text)) return true;
        return false;
    }

    private ActionButton button(String text) {
        return robot.finder().find(frame, new GenericTypeMatcher<>(ActionButton.class) {
            @Override protected boolean isMatching(ActionButton button) {
                return button.isShowing() && text.equals(button.getAction().getTemplatePresentation().getText());
            }
        });
    }

    @Override protected void tearDown() throws Exception {
        ActiveCluster restore = previous;
        try {
            ApplicationManager.getApplication().invokeAndWait(() -> {
                MenuSelectionManager.defaultManager().clearSelectedPath();
                if (editor != null) Disposer.dispose(editor);
                if (frame != null) frame.dispose();
                QueryHistoryStorage.getInstance().getValue().setHistory(savedHistory);
            });
            super.tearDown();
        } finally { ActiveCluster.setInstance(restore); }
    }
}
