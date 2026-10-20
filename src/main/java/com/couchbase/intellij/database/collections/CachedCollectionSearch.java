package com.couchbase.intellij.database.collections;

import java.util.List;
import java.util.Locale;

/** A picker-local snapshot, loaded on the first nonempty search and reused as the user types. */
public final class CachedCollectionSearch {
    private final CollectionSource source;
    private List<String> names;

    public CachedCollectionSearch(CollectionSource source) {
        this.source = source;
    }

    // Debounced searches can overlap on background threads. Only one may fetch the snapshot.
    public synchronized List<String> search(String text, int limit) {
        String needle = text == null ? "" : text.trim().toLowerCase(Locale.ROOT);
        if (limit <= 0 || (names == null && needle.isEmpty())) {
            return List.of();
        }
        if (names == null) {
            names = source.page(0, Integer.MAX_VALUE).stream().distinct()
                    .sorted(CollectionListing.NAME_ORDER).toList();
        }
        return names.stream().filter(name -> name.toLowerCase(Locale.ROOT).contains(needle))
                .limit(limit).toList();
    }
}
