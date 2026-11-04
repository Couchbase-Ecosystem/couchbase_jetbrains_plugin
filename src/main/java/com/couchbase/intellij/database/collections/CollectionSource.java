package com.couchbase.intellij.database.collections;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Read-only view over the collections of a single scope. Implementations must return names in
 * {@link CollectionListing#NAME_ORDER} so that paging is stable.
 */
public interface CollectionSource {

    /** Total number of collections in the scope. */
    int count();

    /** Up to {@code limit} collection names, in name order, starting at {@code offset}. */
    List<String> page(int offset, int limit);

    /** Up to {@code limit} names containing {@code text} (case-insensitive), in name order. */
    List<String> search(String text, int limit);

    /** The subset of {@code names} that exist in the scope. */
    Set<String> existing(Collection<String> names);
}
