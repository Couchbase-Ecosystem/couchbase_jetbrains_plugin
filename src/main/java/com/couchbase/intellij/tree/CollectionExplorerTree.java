package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.ClusterUiTask;
import com.couchbase.intellij.database.collections.CollectionListing;
import com.couchbase.intellij.database.collections.CollectionSources;
import com.couchbase.intellij.persistence.storage.FavoriteCollectionsStorage;
import com.couchbase.intellij.tree.docfilter.QueryFilterUtil;
import com.couchbase.intellij.tree.node.*;
import com.intellij.openapi.project.Project;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.containers.JBIterable;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/** Native tree speed search, extended with a lazy snapshot of the selected scope's collection names. */
public final class CollectionExplorerTree extends Tree {
    private final CollectionSpeedSearch search;

    public CollectionExplorerTree(Project project, TreeModel model) {
        super(model);
        search = new CollectionSpeedSearch(project);
        search.setupListeners();
        ToolTipManager.sharedInstance().registerComponent(this);
        addTreeWillExpandListener(new TreeWillExpandListener() {
            public void treeWillExpand(TreeExpansionEvent event) {}
            public void treeWillCollapse(TreeExpansionEvent event) {
                if (search.session != null && event.getPath().isDescendant(search.session.parentPath)) search.hidePopup();
            }
        });
    }

    static String text(TreePath path) {
        Object value = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
        return value instanceof NodeDescriptor descriptor ? descriptor.getText() : String.valueOf(value);
    }

    @Override protected void processMouseEvent(MouseEvent event) {
        if (event.getID() == MouseEvent.MOUSE_PRESSED && SwingUtilities.isLeftMouseButton(event)) {
            TreePath path = getPathForLocation(event.getX(), event.getY());
            if (path != null && ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject() instanceof LoadErrorNodeDescriptor error) {
                search.hidePopup();
                setSelectionPath(path);
                error.retry();
                event.consume();
                return;
            }
        }
        if (search != null && event.getID() == MouseEvent.MOUSE_PRESSED && search.isPopupActive()) {
            TreePath path = getPathForLocation(event.getX(), event.getY());
            if (path != null) setSelectionPath(path);
            search.acceptSelection();
            search.hidePopup();
        }
        super.processMouseEvent(event);
    }

    @Override protected void processKeyEvent(KeyEvent event) {
        if (event.getID() == KeyEvent.KEY_PRESSED && event.getKeyCode() == KeyEvent.VK_ENTER && !search.isPopupActive()
                && getLastSelectedPathComponent() instanceof DefaultMutableTreeNode node
                && node.getUserObject() instanceof LoadErrorNodeDescriptor error) {
            error.retry();
            event.consume();
            return;
        }
        super.processKeyEvent(event);
    }

    @Override public String getToolTipText(MouseEvent event) {
        TreePath path = getPathForLocation(event.getX(), event.getY());
        if (path != null) {
            Object value = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
            if (value instanceof CollectionsNodeDescriptor || value instanceof ScopeNodeDescriptor)
                return "Type to search all collections in this scope. Enter keeps the selection; Escape cancels.";
        }
        return super.getToolTipText(event);
    }

    private DefaultMutableTreeNode selectedCollections() {
        if (!(getLastSelectedPathComponent() instanceof DefaultMutableTreeNode selected)) return null;
        for (DefaultMutableTreeNode node = selected; node != null; node = (DefaultMutableTreeNode) node.getParent()) {
            if (node.getUserObject() instanceof CollectionsNodeDescriptor) return node;
            if (node.getUserObject() instanceof ScopeNodeDescriptor) {
                for (int i = 0; i < node.getChildCount(); i++) {
                    var child = (DefaultMutableTreeNode) node.getChildAt(i);
                    if (child.getUserObject() instanceof CollectionsNodeDescriptor) return child;
                }
            }
        }
        return null;
    }

    private final class CollectionSpeedSearch extends TreeSpeedSearch {
        private final Project project;
        private Session session;

        CollectionSpeedSearch(Project project) {
            super(CollectionExplorerTree.this, false, null, CollectionExplorerTree::text);
            this.project = project;
            addChangeListener(event -> {
                String prefix = getEnteredPrefix();
                if (prefix != null && !prefix.isEmpty() && session == null) startSession();
                else if (prefix == null) finishSession();
            });
        }

        private void startSession() {
            DefaultMutableTreeNode parent = selectedCollections();
            ActiveCluster active = ActiveCluster.getInstance();
            if (parent == null || active.get() == null) return;
            for (DefaultMutableTreeNode node = parent; node != null; node = (DefaultMutableTreeNode) node.getParent()) {
                if (node.getUserObject() instanceof ScopeNodeDescriptor scope && !Objects.equals(scope.getConnectionId(), active.getId())) return;
            }
            var descriptor = (CollectionsNodeDescriptor) parent.getUserObject();
            if (descriptor.getTotal() < 0) return; // Let the initial page finish before starting a snapshot.
            Session started = new Session(parent, descriptor, active, getSelectionPath());
            session = started;
            var source = CollectionSources.forScope(descriptor.getBucket(), descriptor.getScope());
            setPaintBusy(true);
            Map<String, DefaultMutableTreeNode> loaded = new HashMap<>();
            for (int i = 0; i < parent.getChildCount(); i++) {
                var child = (DefaultMutableTreeNode) parent.getChildAt(i);
                if (child.getUserObject() instanceof CollectionNodeDescriptor collection) loaded.put(collection.getText(), child);
            }
            ClusterUiTask.submit(project, active, "Searching collection names", connection ->
                    source.page(0, Integer.MAX_VALUE).stream().distinct().sorted(CollectionListing.NAME_ORDER)
                            .map(name -> started.parentPath.pathByAddingChild(loaded.containsKey(name)
                                    ? loaded.get(name) : new DefaultMutableTreeNode(name))).toList(), paths -> {
                        if (session != started) return;
                        setPaintBusy(false);
                        if (!current(started)) { hidePopup(); return; }
                        started.paths = paths;
                        findAndSelectElement(getEnteredPrefix());
                    }, error -> {
                        if (session != started) return;
                        setPaintBusy(false);
                        // Native search still works for loaded nodes. Make snapshot failure visible in its popup.
                        if (getSearchField() != null) getSearchField().setToolTipText("Could not load all collection names: " + error.getMessage());
                    });
        }

        private boolean current(Session expected) {
            return session == expected && ActiveCluster.getInstance() == expected.active
                    && expected.descriptor.isCurrentLoad(expected.generation)
                    && expected.parentPath.getPathComponent(0) == getModel().getRoot()
                    && Arrays.equals(expected.parent.getPath(), expected.parentPath.getPath());
        }

        @Override protected JBIterable<TreePath> allPaths() {
            return session != null && current(session) && session.paths != null ? JBIterable.from(session.paths) : super.allPaths();
        }

        @Override protected int getSelectedIndex() {
            // The native implementation normally uses visible row indices; our candidates include unloaded names.
            return session != null && current(session) && session.paths != null
                    ? session.paths.indexOf(getSelectionPath()) : super.getSelectedIndex();
        }

        @Override protected void selectElement(Object element, String prefix) {
            if (session != null && current(session) && element instanceof TreePath path
                    && path.getParentPath().equals(session.parentPath)) {
                var node = (DefaultMutableTreeNode) path.getLastPathComponent();
                if (session.preview != node) removePreview();
                if (node.getParent() == null) {
                    if (!(node.getUserObject() instanceof CollectionNodeDescriptor)) {
                        String name = String.valueOf(node.getUserObject());
                        var descriptor = session.descriptor;
                        var collection = new CollectionNodeDescriptor(name, session.active.getId(), descriptor.getBucket(), descriptor.getScope(),
                                QueryFilterUtil.getQueryFilter(descriptor.getBucket(), descriptor.getScope(), name));
                        collection.setFavorite(FavoriteCollectionsStorage.getInstance().getValue().isFavorite(session.active.getId(), descriptor.getBucket(), descriptor.getScope(), name));
                        node.setUserObject(collection);
                        node.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
                    }
                    int index = session.parent.getChildCount();
                    if (index > 0 && ((DefaultMutableTreeNode) session.parent.getLastChild()).getUserObject() instanceof LoadMoreCollectionsNodeDescriptor) index--;
                    ((DefaultTreeModel) getModel()).insertNodeInto(node, session.parent, index);
                    session.preview = node;
                    updateCounts();
                }
            }
            super.selectElement(element, prefix);
        }

        @Override protected void processKeyEvent(KeyEvent event) {
            if (event.getID() == KeyEvent.KEY_PRESSED && event.getKeyCode() == KeyEvent.VK_ENTER && isPopupActive()
                    && getSelectionPath() != null && isMatchingElement(getSelectionPath(), getEnteredPrefix())) acceptSelection();
            super.processKeyEvent(event);
        }

        void acceptSelection() {
            if (session != null && current(session) && getLastSelectedPathComponent() instanceof DefaultMutableTreeNode node
                    && node.getParent() == session.parent && node.getUserObject() instanceof CollectionNodeDescriptor collection) {
                session.descriptor.addSearchAdded(collection.getText());
                if (node == session.preview) session.preview = null;
            }
        }

        @Override public void hidePopup() {
            super.hidePopup();
            finishSession();
        }

        private void finishSession() {
            if (session == null) return;
            boolean restoreSelection = session.preview != null && getLastSelectedPathComponent() == session.preview && current(session);
            removePreview();
            if (restoreSelection) setSelectionPath(session.originalSelection);
            session = null;
            setPaintBusy(false);
        }

        private void removePreview() {
            if (session == null || session.preview == null) return;
            DefaultMutableTreeNode preview = session.preview;
            session.preview = null;
            if (preview.getParent() == session.parent) {
                ((DefaultTreeModel) getModel()).removeNodeFromParent(preview);
                updateCounts();
            }
        }

        private void updateCounts() {
            int shown = 0;
            for (int i = 0; i < session.parent.getChildCount(); i++)
                if (((DefaultMutableTreeNode) session.parent.getChildAt(i)).getUserObject() instanceof CollectionNodeDescriptor) shown++;
            session.descriptor.setListingCounts(shown, session.descriptor.getTotal());
            ((DefaultTreeModel) getModel()).nodeChanged(session.parent);
        }
    }

    private static final class Session {
        final DefaultMutableTreeNode parent;
        final CollectionsNodeDescriptor descriptor;
        final TreePath parentPath;
        final TreePath originalSelection;
        final ActiveCluster active;
        final long generation;
        List<TreePath> paths;
        DefaultMutableTreeNode preview;
        Session(DefaultMutableTreeNode parent, CollectionsNodeDescriptor descriptor, ActiveCluster active, TreePath originalSelection) {
            this.parent = parent; this.descriptor = descriptor; this.active = active; this.originalSelection = originalSelection;
            parentPath = new TreePath(parent.getPath()); generation = descriptor.getLoadGeneration();
        }
    }
}
