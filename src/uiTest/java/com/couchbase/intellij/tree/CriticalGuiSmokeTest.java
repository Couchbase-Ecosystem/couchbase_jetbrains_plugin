package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.collections.CollectionSource;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tree.node.CollectionsNodeDescriptor;
import com.couchbase.intellij.workbench.CustomSqlFileEditor;
import com.couchbase.intellij.workbench.CustomSqlFileEditorProvider;
import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.testFramework.LightVirtualFile;
import org.intellij.sdk.language.SQLPPFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.impl.DialogWrapperPeerImpl;
import com.intellij.openapi.util.Disposer;
import com.intellij.testFramework.common.ThreadLeakTracker;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import com.intellij.ui.treeStructure.Tree;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/** Real native peers, deliberately without Robot: also runs inside a headless Wayland compositor. */
public class CriticalGuiSmokeTest extends LightPlatformCodeInsightFixture4TestCase {
    static {
        ThreadLeakTracker.longRunningThreadCreated(Disposer.newDisposable("Native UI AWT lifetime"),
                "SystemPropertyWatcher");
    }

    @Override protected boolean runInDispatchThread() { return false; }

    @Override protected void setUp() throws Exception {
        super.setUp();
        assertFalse(GraphicsEnvironment.isHeadless());
        String expected = System.getProperty("awt.toolkit.name");
        assertTrue("Must use the requested native toolkit: " + expected + ", got " + Toolkit.getDefaultToolkit(),
                Toolkit.getDefaultToolkit().getClass().getSimpleName().equals(expected));
    }

    @Test public void newConnectionOpensAndValidatesWithNativePeers() throws Exception {
        showAndCheck(() -> new NewConnectionDialog(getProject(), new Tree(), null, null), window -> {
            button(window, "Save").doClick();
            assertTrue(named(window, "connectionError", JLabel.class).getText().contains("Name can't be empty"));
            assertTrue(named(window, "connectionUsername", JTextField.class).isShowing());
        });
    }

    @Test public void certificateModeAndAdvancedSettingsOpenWithNativePeers() throws Exception {
        showAndCheck(() -> new NewConnectionDialog(getProject(), new Tree(), null, null), window -> {
            named(window, "connectionAuthentication", JComboBox.class).setSelectedItem("Client certificate");
            assertFalse(named(window, "connectionUsername", JTextField.class).isShowing());
            assertTrue(named(window, "connectionKeyStore", JTextField.class).isShowing());
            JCheckBox ssl = named(window, "connectionSsl", JCheckBox.class);
            assertTrue(ssl.isSelected()); assertFalse(ssl.isEnabled());
            button(window, "Test Connection").doClick();
            assertTrue(named(window, "connectionError", JLabel.class).getText().contains("Client keystore can't be empty"));
            JLabel advanced = descendants(window).filter(JLabel.class::isInstance).map(JLabel.class::cast)
                    .filter(label -> "Advanced Settings".equals(label.getText())).findFirst().orElseThrow();
            MouseEvent click = new MouseEvent(advanced, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 2, 2, 1, false);
            for (var listener : advanced.getMouseListeners()) listener.mouseClicked(click);
            window.validate();
            JTabbedPane tabs = descendants(window).filter(JTabbedPane.class::isInstance).map(JTabbedPane.class::cast)
                    .findFirst().orElseThrow();
            assertTrue(tabs.isShowing());
            for (int i = 0; i < tabs.getTabCount(); i++) { tabs.setSelectedIndex(i); window.validate(); }
        });
    }

    @Test public void openDocumentOpensAndValidatesWithNativePeers() throws Exception {
        checkDocument(false);
    }

    @Test public void createDocumentOpensAndValidatesWithNativePeers() throws Exception {
        checkDocument(true);
    }

    private void checkDocument(boolean create) throws Exception {
        showAndCheck(() -> new OpenDocumentDialog(create, getProject(), null, "unused", "_default", "_default"), window -> {
            button(window, "OK").doClick();
            assertTrue(named(window, "documentId", JTextField.class).isShowing());
            assertEquals("The id specified contains invalid characters.", named(window, "documentIdError", JLabel.class).getText());
        });
    }

    @Test public void createCollectionOpensAndValidatesWithNativePeers() throws Exception {
        showAndCheck(() -> new NewCollectionDialog(getProject(), "unused", "_default", null, new Tree()), window -> {
            button(window, "OK").doClick();
            assertEquals("Please enter a name for the collection", named(window, "newCollectionError", JLabel.class).getText());
        });
    }

    @Test public void searchCollectionOpensWithoutFetchingBeforeTyping() throws Exception {
        CollectionSource source = Mockito.mock(CollectionSource.class);
        showAndCheck(() -> new CollectionSearchDialog(getProject(), source, ignored -> fail("No selection was made")), window -> {
            assertTrue(named(window, "collectionSearchField", JTextField.class).isShowing());
            assertTrue(named(window, "collectionSearchResults", JList.class).isShowing());
            Mockito.verifyNoInteractions(source);
        });
    }

    @Test public void explorerAndCollectionsContextMenuOpenWithNativePeers() throws Exception {
        ActiveCluster previous = ActiveCluster.getInstance();
        ActiveCluster active = Mockito.mock(ActiveCluster.class);
        ActiveCluster.setInstance(active);
        JFrame[] frame = {null};
        CompletableFuture<Void> finished = new CompletableFuture<>();
        try {
            ApplicationManager.getApplication().invokeLater(() -> {
                try {
                    frame[0] = new JFrame("Couchbase explorer smoke test");
                    frame[0].setContentPane(new CouchbaseWindowContent(getProject()));
                    frame[0].setSize(800, 600); frame[0].setVisible(true);
                    Tree tree = CouchbaseWindowContent.getTree();
                    assertTrue(tree.isShowing());
                    assertNotNull("Native tree speed search is installed", com.intellij.ui.speedSearch.SpeedSearchSupply.getSupply(tree, true));
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(new CollectionsNodeDescriptor("_default", "unused"));
                    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root"); root.add(node);
                    tree.setModel(new DefaultTreeModel(root)); frame[0].validate();
                    var speedSearch = (com.intellij.ui.SpeedSearchBase<?>) com.intellij.ui.speedSearch.SpeedSearchSupply.getSupply(tree, true);
                    speedSearch.showPopup("Collections");
                    assertTrue("The native search field opens inside the explorer", speedSearch.getTextField().isShowing());
                    speedSearch.hidePopup();
                    Rectangle row = tree.getRowBounds(0);
                    MouseEvent event = new MouseEvent(tree, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(),
                            0, row.x + 10, row.y + 5, 1, true, MouseEvent.BUTTON3);
                    TreeRightClickListener.handle(tree, getProject(), null, event, node);
                    Timer check = new Timer(300, ignored -> ApplicationManager.getApplication().invokeLater(() -> {
                        try {
                            assertTrue("Connection action is available", hasAction(frame[0], "Add New Connection"));
                            MenuElement[] selected = MenuSelectionManager.defaultManager().getSelectedPath();
                            assertTrue("A real context menu opens", selected.length > 0);
                            JPopupMenu menu = (JPopupMenu) selected[0];
                            assertTrue(descendants(menu).filter(JMenuItem.class::isInstance).map(JMenuItem.class::cast)
                                    .anyMatch(item -> "Search Collection...".equals(item.getText())));
                            assertTrue(descendants(menu).filter(JMenuItem.class::isInstance).map(JMenuItem.class::cast)
                                    .anyMatch(item -> "Add New Collection".equals(item.getText())));
                            assertFalse("Context menu must not become a search prompt", descendants(menu).anyMatch(JTextField.class::isInstance));
                            finished.complete(null);
                        } catch (Throwable failure) { finished.completeExceptionally(failure); }
                        finally { MenuSelectionManager.defaultManager().clearSelectedPath(); }
                    }));
                    check.setRepeats(false); check.start();
                } catch (Throwable failure) { finished.completeExceptionally(failure); }
            });
            finished.get(20, TimeUnit.SECONDS);
        } finally {
            ApplicationManager.getApplication().invokeAndWait(() -> {
                MenuSelectionManager.defaultManager().clearSelectedPath();
                if (frame[0] != null) frame[0].dispose();
            });
            ActiveCluster.setInstance(previous);
        }
    }

    @Test public void queryWorkbenchEditorOpensWithNativePeers() throws Exception {
        CompletableFuture<Void> finished = new CompletableFuture<>();
        JFrame[] frame = {null}; CustomSqlFileEditor[] editor = {null};
        try {
            ApplicationManager.getApplication().invokeLater(() -> {
                try {
                    editor[0] = (CustomSqlFileEditor) new CustomSqlFileEditorProvider().createEditor(getProject(),
                            new LightVirtualFile("smoke.sqlpp", SQLPPFileType.INSTANCE, "SELECT 1 AS result"));
                    frame[0] = new JFrame("Query workbench smoke test");
                    frame[0].setContentPane(editor[0].getComponent());
                    frame[0].setSize(1200, 700); frame[0].setVisible(true);
                    assertTrue(editor[0].getComponent().isShowing());
                    Timer check = new Timer(300, ignored -> ApplicationManager.getApplication().invokeLater(() -> {
                        try {
                            assertTrue("Execute action is available", hasAction(frame[0], "Execute"));
                            finished.complete(null);
                        } catch (Throwable failure) { finished.completeExceptionally(failure); }
                    }));
                    check.setRepeats(false); check.start();
                } catch (Throwable failure) { finished.completeExceptionally(failure); }
            });
            finished.get(20, TimeUnit.SECONDS);
        } finally {
            ApplicationManager.getApplication().invokeAndWait(() -> {
                if (editor[0] != null) Disposer.dispose(editor[0]);
                if (frame[0] != null) frame[0].dispose();
            });
        }
    }

    private void showAndCheck(Supplier<DialogWrapper> factory, Consumer<JDialog> assertions) throws Exception {
        CompletableFuture<Void> finished = new CompletableFuture<>();
        DialogWrapper[] dialog = {null};
        ApplicationManager.getApplication().invokeLater(() -> {
            try {
                // Only disable the test-mode headless substitution. The toolkit, dialog,
                // native window allocation, layout, and action handlers are production code.
                try (MockedStatic<DialogWrapperPeerImpl> peers = Mockito.mockStatic(
                        DialogWrapperPeerImpl.class, Mockito.CALLS_REAL_METHODS)) {
                    peers.when(DialogWrapperPeerImpl::isHeadlessEnv).thenReturn(false);
                    dialog[0] = factory.get();
                }
                Timer check = new Timer(200, event -> ApplicationManager.getApplication().invokeLater(() -> {
                    try {
                        JDialog window = (JDialog) dialog[0].getPeer().getWindow();
                        assertTrue("Real native peer allocated", window.isDisplayable());
                        assertTrue("Window visible", window.isShowing());
                        assertFalse("AWT ScrollPane has no supported native Wayland peer; use a Swing scroll pane",
                                descendants(window).anyMatch(java.awt.ScrollPane.class::isInstance));
                        assertions.accept(window);
                        finished.complete(null);
                    } catch (Throwable failure) { finished.completeExceptionally(failure); }
                    finally { dialog[0].close(DialogWrapper.CANCEL_EXIT_CODE); }
                }, ModalityState.any()));
                check.setRepeats(false); check.start();
                dialog[0].show();
            } catch (Throwable failure) { finished.completeExceptionally(failure); }
        });
        try { finished.get(20, TimeUnit.SECONDS); }
        finally {
            ApplicationManager.getApplication().invokeAndWait(() -> {
                if (dialog[0] != null && !dialog[0].isDisposed()) dialog[0].close(DialogWrapper.CANCEL_EXIT_CODE);
            }, ModalityState.any());
        }
    }

    private static JButton button(Container root, String text) {
        return descendants(root).filter(JButton.class::isInstance).map(JButton.class::cast)
                .filter(button -> text.equals(button.getText())).findFirst().orElseThrow();
    }

    private static boolean hasAction(Container root, String text) {
        return descendants(root).filter(ActionButton.class::isInstance).map(ActionButton.class::cast)
                .anyMatch(button -> text.equals(button.getAction().getTemplatePresentation().getText()));
    }

    private static <T extends Component> T named(Container root, String name, Class<T> type) {
        return descendants(root).filter(type::isInstance).map(type::cast)
                .filter(component -> name.equals(component.getName())).findFirst().orElseThrow();
    }

    private static Stream<Component> descendants(Component root) {
        return Stream.concat(Stream.of(root), root instanceof Container container
                ? Arrays.stream(container.getComponents()).flatMap(CriticalGuiSmokeTest::descendants) : Stream.empty());
    }
}
