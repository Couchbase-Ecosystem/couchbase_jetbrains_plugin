package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.ActiveCluster;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.ex.ActionUtil;
import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.UIUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.swing.*;
import java.util.List;

/** The explorer toolbar must always offer "Add New Connection", with or without a connected cluster. */
public class TreeToolBarBuilderTest extends LightPlatformCodeInsightFixture4TestCase {

    static {
        com.couchbase.intellij.testutil.AllowedThreads.allowJdkAwtThreads();
    }

    private ActiveCluster previous;

    @Before
    public void disconnect() {
        previous = ActiveCluster.getInstance();
        ActiveCluster none = Mockito.mock(ActiveCluster.class);
        ActiveCluster.setInstance(none);
    }

    @After
    public void restore() {
        ActiveCluster.setInstance(previous);
    }

    @Test
    public void addConnectionIsOfferedWithoutAConnection() {
        JPanel panel = new TreeToolBarBuilder().build(getProject(), new Tree());
        List<ActionToolbar> toolbars = toolbars(panel);
        assertEquals("explorer toolbar + more-options toolbar", 2, toolbars.size());

        AnAction add = toolbars.get(0).getActions().stream()
                .filter(a -> "Add New Connection".equals(a.getTemplateText())).findFirst().orElse(null);
        assertNotNull("Add New Connection is in the toolbar", add);

        // Updating on the thread the action declares must leave it visible and enabled.
        assertNotNull(add.getActionUpdateThread());
        com.intellij.openapi.actionSystem.Presentation presentation = add.getTemplatePresentation().clone();
        com.intellij.openapi.actionSystem.AnActionEvent event = com.intellij.openapi.actionSystem.AnActionEvent
                .createEvent(com.intellij.openapi.actionSystem.DataContext.EMPTY_CONTEXT, presentation, "Explorer",
                        com.intellij.openapi.actionSystem.ActionUiKind.TOOLBAR, null);
        ActionUtil.performDumbAwareUpdate(add, event, false);
        assertTrue(presentation.isVisible());
        assertTrue(presentation.isEnabled());
    }

    @Test
    public void everyToolbarActionDeclaresItsUpdateThread() {
        JPanel panel = new TreeToolBarBuilder().build(getProject(), new Tree());
        for (ActionToolbar toolbar : toolbars(panel)) {
            assertFalse("toolbar has actions", toolbar.getActions().isEmpty());
            for (AnAction action : toolbar.getActions()) {
                if (action instanceof com.intellij.openapi.actionSystem.Separator) continue;
                boolean declared;
                try {
                    declared = action.getClass().getMethod("getActionUpdateThread").getDeclaringClass() != AnAction.class;
                } catch (NoSuchMethodException e) {
                    declared = false;
                }
                assertTrue(action.getTemplateText() + " must override getActionUpdateThread()", declared);
            }
        }
    }

    private static List<ActionToolbar> toolbars(JPanel panel) {
        List<ActionToolbar> found = UIUtil.findComponentsOfType(panel, JComponent.class).stream()
                .filter(c -> c instanceof ActionToolbar).map(c -> (ActionToolbar) c).toList();
        found.forEach(ActionToolbar::updateActionsImmediately);
        return found;
    }
}
