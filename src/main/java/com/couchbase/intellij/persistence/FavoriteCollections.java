package com.couchbase.intellij.persistence;

import java.util.ArrayList;
import java.util.List;

/** Collections the user pinned to the top of the tree, per connection. */
public class FavoriteCollections {

    private List<String> keys = new ArrayList<>();

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys == null ? new ArrayList<>() : keys;
    }

    private static String key(String connectionId, String bucket, String scope, String collection) {
        return connectionId + "|" + bucket + "|" + scope + "|" + collection;
    }

    public boolean isFavorite(String connectionId, String bucket, String scope, String collection) {
        return keys.contains(key(connectionId, bucket, scope, collection));
    }

    public void setFavorite(String connectionId, String bucket, String scope, String collection, boolean favorite) {
        String k = key(connectionId, bucket, scope, collection);
        if (favorite) {
            if (!keys.contains(k)) {
                keys.add(k);
            }
        } else {
            keys.remove(k);
        }
    }

    /** Favorite collection names (unsorted) for one scope of one connection. */
    public List<String> getFavorites(String connectionId, String bucket, String scope) {
        String prefix = key(connectionId, bucket, scope, "");
        List<String> out = new ArrayList<>();
        for (String k : keys) {
            if (k.startsWith(prefix)) {
                out.add(k.substring(prefix.length()));
            }
        }
        return out;
    }
}
