package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tree.node.*;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.ui.treeStructure.Tree;
import javax.swing.tree.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

/** Fetch off the EDT, publish tree changes on the EDT, and ignore stale connection/expansion results. */
public final class TreeLoadTask {
    private static final Map<DefaultMutableTreeNode, Long> requests = new WeakHashMap<>();
    private static final Map<Tree, Set<Long>> pending = new WeakHashMap<>();
    private static long sequence;

    private TreeLoadTask() {}

    public static void invalidate(DefaultMutableTreeNode parent, Tree tree) {
        Long previous = requests.remove(parent);
        if (previous != null) finish(tree, previous);
    }

    public static <T> CompletableFuture<Void> load(DefaultMutableTreeNode parent, Tree tree, ActiveCluster active, String subject,
                               BooleanSupplier valid, Callable<T> fetch, Consumer<T> apply, Runnable retry) {
        List<NodeKey> selected = selectedPath(parent, tree);
        invalidate(parent, tree);
        long request = ++sequence;
        requests.put(parent, request);
        pending.computeIfAbsent(tree, ignored -> new HashSet<>()).add(request);
        Object root = tree.getModel().getRoot();
        String connection = active.getId();
        var sdk = active.get();
        ModalityState modality = ModalityState.current();
        BooleanSupplier current = () -> Objects.equals(requests.get(parent), request) && valid.getAsBoolean()
                && ActiveCluster.getInstance() == active && Objects.equals(connection, active.getId()) && sdk == active.get()
                && parent.getRoot() == root && tree.getModel().getRoot() == root;
        for (int i = parent.getChildCount() - 1; i >= 0; i--) {
            Object value = ((DefaultMutableTreeNode) parent.getChildAt(i)).getUserObject();
            if (value instanceof LoadingNodeDescriptor || value instanceof LoadErrorNodeDescriptor
                    || value instanceof NoResultsNodeDescriptor || value instanceof LoadMoreNodeDescriptor) parent.remove(i);
        }
        parent.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor("Loading " + subject + "…")));
        ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parent);
        restoreSelection(parent, tree, selected);
        tree.setPaintBusy(true);
        CompletableFuture<Void> completion = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            T result = null;
            Exception failure = null;
            try {
                if (sdk == null) throw new IllegalStateException("Connect to the cluster and retry.");
                result = fetch.call();
            } catch (Exception error) { failure = error; Log.error(error); }
            T loaded = result;
            Exception error = failure;
            ApplicationManager.getApplication().invokeLater(() -> {
                try {
                    if (!current.getAsBoolean()) return;
                    List<NodeKey> selection = selectedPath(parent, tree);
                    removeStatusRows(parent);
                    if (error == null) {
                        try { apply.accept(loaded); }
                        catch (Exception renderingError) { showError(parent, subject, renderingError, current, retry); }
                    } else showError(parent, subject, error, current, retry);
                    ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parent);
                    restoreSelection(parent, tree, selection);
                } finally { finish(tree, request); completion.complete(null); }
            }, modality);
        });
        return completion;
    }

    private record NodeKey(Class<?> type, String text) {
        static NodeKey of(DefaultMutableTreeNode node) {
            Object value = node.getUserObject();
            return new NodeKey(value.getClass(), value instanceof NodeDescriptor descriptor ? descriptor.getText() : String.valueOf(value));
        }
    }

    private static List<NodeKey> selectedPath(DefaultMutableTreeNode parent, Tree tree) {
        TreePath selection = tree.getSelectionPath();
        TreePath base = new TreePath(parent.getPath());
        if (selection == null || !base.isDescendant(selection)) return null;
        List<NodeKey> keys = new ArrayList<>();
        for (int i = base.getPathCount(); i < selection.getPathCount(); i++) keys.add(NodeKey.of((DefaultMutableTreeNode) selection.getPathComponent(i)));
        return keys;
    }

    private static void restoreSelection(DefaultMutableTreeNode parent, Tree tree, List<NodeKey> keys) {
        if (keys == null) return;
        DefaultMutableTreeNode selected = parent;
        for (NodeKey key : keys) {
            DefaultMutableTreeNode found = null;
            for (int i = 0; i < selected.getChildCount(); i++) {
                var child = (DefaultMutableTreeNode) selected.getChildAt(i);
                if (key.equals(NodeKey.of(child))) { found = child; break; }
            }
            if (found == null) break;
            selected = found;
        }
        tree.setSelectionPath(new TreePath(selected.getPath()));
    }

    private static void removeStatusRows(DefaultMutableTreeNode parent) {
        for (int i = parent.getChildCount() - 1; i >= 0; i--) {
            Object value = ((DefaultMutableTreeNode) parent.getChildAt(i)).getUserObject();
            if (value instanceof LoadingNodeDescriptor || value instanceof LoadErrorNodeDescriptor) parent.remove(i);
        }
    }

    private static void showError(DefaultMutableTreeNode parent, String subject, Exception failure, BooleanSupplier current, Runnable retry) {
        parent.add(new DefaultMutableTreeNode(new LoadErrorNodeDescriptor(subject, failure, () -> {
            if (current.getAsBoolean()) retry.run();
        })));
    }

    private static void finish(Tree tree, long request) {
        Set<Long> work = pending.get(tree);
        if (work != null) work.remove(request);
        tree.setPaintBusy(work != null && !work.isEmpty());
        if (work != null && work.isEmpty()) pending.remove(tree);
    }
}
