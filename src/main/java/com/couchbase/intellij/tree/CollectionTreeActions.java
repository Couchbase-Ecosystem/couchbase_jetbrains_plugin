package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.DataLoader;
import com.couchbase.intellij.database.collections.CollectionListing;
import com.couchbase.intellij.persistence.storage.FavoriteCollectionsStorage;
import com.couchbase.intellij.tree.node.*;
import com.intellij.ui.treeStructure.Tree;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Objects;

public final class CollectionTreeActions {
    private CollectionTreeActions() {}

    public static void toggleFavorite(Tree tree, DefaultMutableTreeNode node) {
        if (!(node.getUserObject() instanceof CollectionNodeDescriptor collection)
                || !Objects.equals(ActiveCluster.getInstance().getId(), collection.getConnectionId())
                || !(node.getParent() instanceof DefaultMutableTreeNode parent)
                || !(parent.getUserObject() instanceof CollectionsNodeDescriptor collections)) return;
        var favorites = FavoriteCollectionsStorage.getInstance().getValue();
        boolean selected = !favorites.isFavorite(collection.getConnectionId(), collection.getBucket(), collection.getScope(), collection.getText());
        favorites.setFavorite(collection.getConnectionId(), collection.getBucket(), collection.getScope(), collection.getText(), selected);
        collection.setFavorite(selected);
        tree.repaint();
        DataLoader.listCollections(parent, tree, Math.max(collections.getPagedLoaded(), CollectionListing.PAGE_SIZE));
    }

}
