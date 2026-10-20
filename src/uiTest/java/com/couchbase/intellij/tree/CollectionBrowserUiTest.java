package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.*;
import com.couchbase.intellij.database.collections.*;
import com.couchbase.intellij.persistence.storage.FavoriteCollectionsStorage;
import com.couchbase.intellij.tree.node.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.treeStructure.Tree;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.*;
import org.junit.Test;
import org.mockito.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class CollectionBrowserUiTest extends DialogUiTestCase {
    @Test public void nativeTreeSearchAndContextMenuFavoritesWorkAcrossCollapseAndPaging() throws Exception {
        ActiveCluster previous = ActiveCluster.getInstance();
        var active = Mockito.mock(ActiveCluster.class);
        Mockito.when(active.get()).thenReturn(Mockito.mock(com.couchbase.client.java.Cluster.class));
        Mockito.when(active.getId()).thenReturn("browser-ui"); ActiveCluster.setInstance(active);
        var favorites = FavoriteCollectionsStorage.getInstance().getValue();
        List<String> oldFavorites = new ArrayList<>(favorites.getKeys());
        List<String> names = IntStream.range(0, 100).mapToObj(i -> String.format("collection_%02d", i)).toList();
        AtomicInteger snapshots = new AtomicInteger();
        CollectionSource source = new CollectionSource() {
            public int count() { return names.size(); }
            public List<String> page(int offset, int limit) {
                if (limit == Integer.MAX_VALUE) {
                    assertFalse("Fetching all collection names must stay off the UI thread", SwingUtilities.isEventDispatchThread());
                    snapshots.incrementAndGet();
                }
                return names.subList(Math.min(offset, names.size()), (int) Math.min(names.size(), (long) offset + limit));
            }
            public List<String> search(String text, int limit) { return names.stream().filter(name -> name.contains(text)).limit(limit).toList(); }
            public Set<String> existing(Collection<String> requested) { Set<String> found = new HashSet<>(requested); found.retainAll(names); return found; }
        };
        JFrame[] frame = {null}; Tree[] tree = {null}; DefaultMutableTreeNode[] collections = {null};
        MockedStatic<DataLoader>[] loaders = new MockedStatic[1];
        MockedStatic<CollectionSources>[] sources = new MockedStatic[1];
        try {
            ApplicationManager.getApplication().invokeAndWait(() -> {
                var root = new DefaultMutableTreeNode("Root");
                var connection = new DefaultMutableTreeNode("Connection"); root.add(connection);
                var bucketNode = new DefaultMutableTreeNode("bucket"); connection.add(bucketNode);
                var scope = new DefaultMutableTreeNode(new ScopeNodeDescriptor("scope", "browser-ui", "bucket")); bucketNode.add(scope);
                collections[0] = new DefaultMutableTreeNode(new CollectionsNodeDescriptor("scope", "bucket")); scope.add(collections[0]);
                tree[0] = new CollectionExplorerTree(getProject(), new DefaultTreeModel(root)); tree[0].setRootVisible(false);
                tree[0].setCellRenderer(new CouchbaseWindowContent.NodeDescriptorRenderer());
                tree[0].addMouseListener(new com.intellij.ui.PopupHandler() {
                    @Override public void invokePopup(Component component, int x, int y) {
                        TreePath path = tree[0].getPathForLocation(x, y);
                        if (path != null) TreeRightClickListener.handle(tree[0], getProject(), null,
                                new java.awt.event.MouseEvent(tree[0], java.awt.event.MouseEvent.MOUSE_RELEASED,
                                        System.currentTimeMillis(), 0, x, y, 1, true, java.awt.event.MouseEvent.BUTTON3),
                                (DefaultMutableTreeNode) path.getLastPathComponent());
                    }
                });
                loaders[0] = Mockito.mockStatic(DataLoader.class, Mockito.CALLS_REAL_METHODS);
                loaders[0].when(() -> DataLoader.listCollections(Mockito.eq(collections[0]), Mockito.eq(tree[0]), Mockito.anyInt())).thenAnswer(call -> {
                    String selected = tree[0].getLastSelectedPathComponent() instanceof DefaultMutableTreeNode node
                            && node.getUserObject() instanceof CollectionNodeDescriptor collection ? collection.getText() : null;
                    CollectionTreePopulator.populate(collections[0], (CollectionsNodeDescriptor) collections[0].getUserObject(),
                            source, favorites, "browser-ui", call.getArgument(2), Map.of(), name -> null);
                    ((DefaultTreeModel) tree[0].getModel()).nodeStructureChanged(collections[0]);
                    if (selected != null) select(tree[0], collections[0], selected);
                    return null;
                });
                sources[0] = Mockito.mockStatic(CollectionSources.class);
                sources[0].when(() -> CollectionSources.forScope("bucket", "scope")).thenReturn(source);
                DataLoader.listCollections(collections[0], tree[0], 10);
                tree[0].addTreeExpansionListener(new TreeExpandListener(tree[0]));
                JPanel panel = new JPanel(new BorderLayout());
                panel.add(new JScrollPane(tree[0]), BorderLayout.CENTER);
                frame[0] = new JFrame("Collection browser"); frame[0].setContentPane(panel);
                frame[0].setSize(700, 650); frame[0].setVisible(true);
                tree[0].expandPath(new TreePath(scope.getPath())); tree[0].expandPath(new TreePath(collections[0].getPath()));
                tree[0].setSelectionPath(new TreePath(collections[0].getPath()));
            });
            FrameFixture browser = new FrameFixture(robot, frame[0]);
            assertEquals(10, GuiActionRunner.execute(() -> descriptor(collections[0]).getShown()).intValue());
            browser.tree().focus();
            robot.settings().delayBetweenEvents(100);
            robot.enterText("c");
            await("native popup opens and fetches the snapshot", () -> GuiActionRunner.execute(() -> snapshots.get() == 1
                    && "c".equals(com.intellij.ui.speedSearch.SpeedSearchSupply.getSupply(tree[0]).getEnteredPrefix())));
            robot.enterText("ollection_9");
            await("native search selects a match", () -> GuiActionRunner.execute(() -> selectedName(tree[0]).matches("collection_[0-9]9")));
            String firstMatch = GuiActionRunner.execute(() -> selectedName(tree[0]));
            browser.tree().pressAndReleaseKeys(java.awt.event.KeyEvent.VK_DOWN);
            await("native arrow navigation finds another unloaded match", () -> GuiActionRunner.execute(() -> !selectedName(tree[0]).equals(firstMatch)));
            robot.enterText("9");
            await("typing reuses the same snapshot", () -> GuiActionRunner.execute(() -> selectedName(tree[0]).equals("collection_99")));
            assertEquals("Only one full name snapshot per search", 1, snapshots.get());
            assertEquals("Preview replaces the previous preview instead of accumulating rows", 11,
                    GuiActionRunner.execute(() -> descriptor(collections[0]).getShown()).intValue());
            browser.tree().pressAndReleaseKeys(java.awt.event.KeyEvent.VK_ENTER);
            await("Enter keeps the selected collection", () -> GuiActionRunner.execute(() -> descriptor(collections[0]).getSearchAdded().equals(List.of("collection_99"))));
            assertFavoriteIndicator(tree[0], false);
            chooseFavoriteAction(browser, tree[0], "Add to Favorites");
            assertFavoriteIndicator(tree[0], true);
            assertTrue(favorites.isFavorite("browser-ui", "bucket", "scope", "collection_99"));
            GuiActionRunner.execute(() -> {
                TreePath scope = new TreePath(((DefaultMutableTreeNode) collections[0].getParent()).getPath());
                assertTrue("Scope is expanded before collapsing", tree[0].isExpanded(scope));
                tree[0].collapsePath(scope);
                assertTrue("Scope collapse forgets temporary search selections", ((CollectionsNodeDescriptor) collections[0].getUserObject()).getSearchAdded().isEmpty());
                tree[0].expandPath(scope);
            });
            await("favorite remains first after reopening", () -> GuiActionRunner.execute(() ->
                    ((CollectionsNodeDescriptor) collections[0].getUserObject()).getSearchAdded().isEmpty()
                            && ((CollectionsNodeDescriptor) collections[0].getUserObject()).getTotal() == 100
                            && ((DefaultMutableTreeNode) collections[0].getFirstChild()).getUserObject() instanceof CollectionNodeDescriptor first
                            && first.getText().equals("collection_99") && first.isFavorite()));
            GuiActionRunner.execute(() -> select(tree[0], collections[0], "collection_99"));
            assertFavoriteIndicator(tree[0], true);
            chooseFavoriteAction(browser, tree[0], "Remove from Favorites");
            assertFalse(favorites.isFavorite("browser-ui", "bucket", "scope", "collection_99"));
            GuiActionRunner.execute(() -> select(tree[0], collections[0], "collection_00"));
            assertFavoriteIndicator(tree[0], false);
            chooseFavoriteAction(browser, tree[0], "Add to Favorites");
            assertFavoriteIndicator(tree[0], true);
            chooseFavoriteAction(browser, tree[0], "Remove from Favorites");
            assertFavoriteIndicator(tree[0], false);
            GuiActionRunner.execute(() -> {
                DataLoader.listCollections(collections[0], tree[0], 20);
                tree[0].setSelectionPath(new TreePath(collections[0].getPath()));
            });
            assertEquals(20, GuiActionRunner.execute(() -> descriptor(collections[0]).getShown()).intValue());
            browser.tree().focus();
            robot.enterText("collection_88");
            await("second search previews a new collection", () -> GuiActionRunner.execute(() -> selectedName(tree[0]).equals("collection_88")));
            robot.enterText("_missing");
            browser.tree().pressAndReleaseKeys(java.awt.event.KeyEvent.VK_ENTER);
            await("Enter with no matches discards the old preview", () -> GuiActionRunner.execute(() -> descriptor(collections[0]).getShown() == 20));
            assertFalse(GuiActionRunner.execute(() -> descriptor(collections[0]).getSearchAdded().contains("collection_88")));
            browser.tree().focus(); robot.enterText("collection_88");
            await("another search can start after no matches", () -> GuiActionRunner.execute(() -> selectedName(tree[0]).equals("collection_88")));
            browser.tree().pressAndReleaseKeys(java.awt.event.KeyEvent.VK_ESCAPE);
            await("Escape removes the preview", () -> GuiActionRunner.execute(() -> descriptor(collections[0]).getShown() == 20));
            assertFalse(GuiActionRunner.execute(() -> descriptor(collections[0]).getSearchAdded().contains("collection_88")));
            assertSame("Escape restores the previous selection", collections[0], GuiActionRunner.execute(() -> tree[0].getLastSelectedPathComponent()));
            browser.tree().focus(); robot.enterText("collection_77");
            await("native popup is ready for screenshot", () -> GuiActionRunner.execute(() -> selectedName(tree[0]).equals("collection_77")));

            java.nio.file.Path picture = java.nio.file.Path.of(System.getProperty("ui.test.artifacts"), "collection-browser.png");
            java.nio.file.Files.createDirectories(picture.getParent());
            javax.imageio.ImageIO.write(new java.awt.Robot().createScreenCapture(GuiActionRunner.execute(() -> { return frame[0].getBounds(); })), "png", picture.toFile());
            browser.tree().pressAndReleaseKeys(java.awt.event.KeyEvent.VK_ESCAPE);
            GuiActionRunner.execute(() -> tree[0].setSelectionPath(new TreePath(collections[0].getPath())));
            browser.tree().focus(); robot.enterText("collection_77");
            await("unfavorited search selection is available", () -> GuiActionRunner.execute(() -> selectedName(tree[0]).equals("collection_77")));
            browser.tree().pressAndReleaseKeys(java.awt.event.KeyEvent.VK_ENTER);
            assertTrue(GuiActionRunner.execute(() -> descriptor(collections[0]).getSearchAdded().contains("collection_77")));
            GuiActionRunner.execute(() -> {
                TreePath scope = new TreePath(((DefaultMutableTreeNode) collections[0].getParent()).getPath());
                tree[0].collapsePath(scope); tree[0].expandPath(scope);
            });
            await("reopening drops the unfavorited search choice", () -> GuiActionRunner.execute(() -> descriptor(collections[0]).getShown() == 10
                    && descriptor(collections[0]).getSearchAdded().isEmpty()));
        } finally {
            ApplicationManager.getApplication().invokeAndWait(() -> {
                if (sources[0] != null) sources[0].close(); if (loaders[0] != null) loaders[0].close();
                if (frame[0] != null) frame[0].dispose();
            });
            favorites.setKeys(oldFavorites); ActiveCluster.setInstance(previous);
        }
    }

    private static void assertFavoriteIndicator(Tree tree, boolean expected) {
        GuiActionRunner.execute(() -> {
            var node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            assertNotNull("A collection is selected", node);
            var descriptor = (CollectionNodeDescriptor) node.getUserObject();
            Component row = tree.getCellRenderer().getTreeCellRendererComponent(tree, node, false, false, false,
                    tree.getLeadSelectionRow(), false);
            JLabel marker = Arrays.stream(((Container) row).getComponents())
                    .filter(component -> "collectionFavoriteIndicator".equals(component.getName()))
                    .map(JLabel.class::cast).findFirst().orElse(null);
            assertEquals("Only favorited collections display a star", expected, marker != null);
            if (marker != null) {
                assertEquals("Favorite star matches the collection icon's neutral color", iconColor(descriptor.getIcon(), tree), iconColor(marker.getIcon(), tree));
                assertEquals("Favorite collection", marker.getAccessibleContext().getAccessibleName());
            }
        });
    }

    private static int iconColor(Icon icon, Component component) {
        var picture = new java.awt.image.BufferedImage(icon.getIconWidth(), icon.getIconHeight(), java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = picture.createGraphics();
        try { icon.paintIcon(component, graphics, 0, 0); } finally { graphics.dispose(); }
        Map<Integer, Integer> colors = new HashMap<>();
        for (int y = 0; y < picture.getHeight(); y++) for (int x = 0; x < picture.getWidth(); x++) {
            int color = picture.getRGB(x, y);
            if ((color >>> 24) == 255) colors.merge(color & 0xffffff, 1, Integer::sum);
        }
        return colors.entrySet().stream().max(Map.Entry.comparingByValue()).orElseThrow().getKey();
    }

    private void chooseFavoriteAction(FrameFixture browser, Tree tree, String action) {
        int row = GuiActionRunner.execute(() -> tree.getLeadSelectionRow());
        browser.tree().showPopupMenuAt(row).menuItemWithPath(action).click();
        robot.waitForIdle();
    }

    private static CollectionsNodeDescriptor descriptor(DefaultMutableTreeNode parent) {
        return (CollectionsNodeDescriptor) parent.getUserObject();
    }

    private static String selectedName(Tree tree) {
        return tree.getLastSelectedPathComponent() instanceof DefaultMutableTreeNode node
                && node.getUserObject() instanceof CollectionNodeDescriptor collection ? collection.getText() : "";
    }

    private static void select(Tree tree, DefaultMutableTreeNode parent, String name) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            var child = (DefaultMutableTreeNode) parent.getChildAt(i);
            if (child.getUserObject() instanceof CollectionNodeDescriptor collection && name.equals(collection.getText())) {
                tree.setSelectionPath(new TreePath(child.getPath())); tree.scrollPathToVisible(new TreePath(child.getPath())); return;
            }
        }
    }
}
