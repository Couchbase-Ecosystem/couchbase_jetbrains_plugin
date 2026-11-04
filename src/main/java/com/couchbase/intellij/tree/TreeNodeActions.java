package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.DataLoader;
import com.couchbase.intellij.tree.node.*;
import com.couchbase.intellij.workbench.EditorOpening;
import com.couchbase.intellij.workbench.Log;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.ex.ActionUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.Nullable;
import com.couchbase.intellij.DocumentFormatter;

import javax.swing.tree.DefaultMutableTreeNode;

/** What a double-click, Enter, Delete or F5 means on an explorer node; shared by the mouse and key handlers. */
public final class TreeNodeActions {
    private TreeNodeActions() {}

    /** Opens the node (document, index, search index) or loads its next page. @return false when it is neither. */
    public static boolean open(Project project, Tree tree, DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();
        if (userObject instanceof FileNodeDescriptor descriptor) {
            // Always force a load from the server on read-only mode.
            if (ActiveCluster.getInstance().isReadOnlyMode()) {
                descriptor.setVirtualFile(null);
            }
            DataLoader.loadDocument(project, descriptor, tree, file -> EditorOpening.openFile(project, file, editors -> {
                if (!editors.isEmpty()) CouchbaseDocumentEditorRefresher.attach(editors.getFirst());
            }));
            return true;
        }
        if (userObject instanceof IndexNodeDescriptor descriptor) {
            VirtualFile virtualFile = descriptor.getVirtualFile();
            if (virtualFile != null) EditorOpening.openFile(project, virtualFile);
            else Log.debug("virtual file is null");
            return true;
        }
        if (userObject instanceof SearchIndexNodeDescriptor descriptor) {
            VirtualFile virtualFile = descriptor.getVirtualFile();
            if (virtualFile != null) {
                DocumentFormatter.formatFile(project, virtualFile);
                EditorOpening.openFile(project, virtualFile);
            } else {
                Log.debug("virtual file is null");
            }
            return true;
        }
        if (!(node.getParent() instanceof DefaultMutableTreeNode parent)) {
            return false;
        }
        if (userObject instanceof LoadMoreCollectionsNodeDescriptor) {
            DataLoader.loadMoreCollections(parent, tree);
            return true;
        }
        if (userObject instanceof LoadMoreNodeDescriptor loadMore) {
            DataLoader.listDocuments(parent, tree, loadMore.getNewOffset());
            return true;
        }
        return false;
    }

    /** Runs the node's context-menu action whose name starts with one of {@code prefixes}. @return false if none. */
    public static boolean perform(Project project, Tree tree, DefaultMutableTreeNode node, String... prefixes) {
        AnAction action = find(TreeRightClickListener.menuFor(tree, project, null, null, node), prefixes);
        if (action == null) {
            return false;
        }
        ActionUtil.invokeAction(action, DataManager.getInstance().getDataContext(tree), ActionPlaces.KEYBOARD_SHORTCUT, null, null);
        return true;
    }

    public static @Nullable AnAction find(@Nullable DefaultActionGroup menu, String... prefixes) {
        if (menu == null) {
            return null;
        }
        for (AnAction action : menu.getChildren((com.intellij.openapi.actionSystem.AnActionEvent) null)) {
            String text = action.getTemplatePresentation().getText();
            if (text == null) continue;
            for (String prefix : prefixes) {
                if (text.startsWith(prefix)) return action;
            }
        }
        return null;
    }
}
