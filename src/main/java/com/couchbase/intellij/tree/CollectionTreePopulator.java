package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.collections.CollectionListing;
import com.couchbase.intellij.database.collections.CollectionSource;
import com.couchbase.intellij.persistence.FavoriteCollections;
import com.couchbase.intellij.persistence.QueryFilter;
import com.couchbase.intellij.tree.node.*;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Map;
import java.util.function.Function;

/** Fills a Collections tree node with (at most) the loaded pages, favorites on top. Does no UI work itself. */
public final class CollectionTreePopulator {

    private CollectionTreePopulator() {
    }

    public static CollectionListing populate(DefaultMutableTreeNode parent, CollectionsNodeDescriptor desc,
                                             CollectionSource source, FavoriteCollections favorites,
                                             String connectionId, int pagedToLoad,
                                             Map<String, Integer> counts,
                                             Function<String, QueryFilter> filters) {
        String bucket = desc.getBucket();
        String scope = desc.getScope();

        CollectionListing listing = CollectionListing.build(source,
                favorites.getFavorites(connectionId, bucket, scope), desc.getSearchAdded(), pagedToLoad);

        parent.removeAllChildren();
        desc.setPagedLoaded(listing.getPagedLoaded());

        if (listing.getNames().isEmpty()) {
            parent.add(new DefaultMutableTreeNode(new NoResultsNodeDescriptor()));
            return listing;
        }

        for (String name : listing.getNames()) {
            CollectionNodeDescriptor colDesc = new CollectionNodeDescriptor(name, connectionId, bucket, scope,
                    filters.apply(name));
            colDesc.setFavorite(favorites.isFavorite(connectionId, bucket, scope, name));
            colDesc.setCounter(com.couchbase.intellij.database.DataLoader.formatCount(
                    counts.get(bucket + "." + scope + "." + name)));
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(colDesc);
            child.add(new DefaultMutableTreeNode(new LoadingNodeDescriptor()));
            parent.add(child);
        }

        if (listing.hasMore()) {
            parent.add(new DefaultMutableTreeNode(new LoadMoreCollectionsNodeDescriptor(bucket, scope,
                    listing.getPagedLoaded() + CollectionListing.PAGE_SIZE)));
        }
        return listing;
    }
}
