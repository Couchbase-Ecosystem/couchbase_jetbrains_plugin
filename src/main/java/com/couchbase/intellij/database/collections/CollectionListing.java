package com.couchbase.intellij.database.collections;

import java.util.*;

/**
 * Decides which collections the tree shows and in which order: favorites first (alphabetical), then collections
 * the user pulled in through search, then the alphabetical pages loaded so far.
 */
public final class CollectionListing {

    public static final int PAGE_SIZE = 10;

    /** Alphabetical, case-insensitive, with a case-sensitive tie-break so the order is total. */
    public static final Comparator<String> NAME_ORDER =
            String.CASE_INSENSITIVE_ORDER.thenComparing(Comparator.naturalOrder());

    private final List<String> names;
    private final int pagedLoaded;
    private final int total;

    private CollectionListing(List<String> names, int pagedLoaded, int total) {
        this.names = names;
        this.pagedLoaded = pagedLoaded;
        this.total = total;
    }

    /** Names to show, in display order. */
    public List<String> getNames() {
        return names;
    }

    /** How many collections of the alphabetical sequence have been consumed so far (the next page's offset). */
    public int getPagedLoaded() {
        return pagedLoaded;
    }

    /** Number of collections in the scope. */
    public int getTotal() {
        return total;
    }

    public boolean hasMore() {
        return pagedLoaded < total;
    }

    public static CollectionListing build(CollectionSource source, Collection<String> favorites,
                                          Collection<String> pinned, int pagedToLoad) {
        int total = source.count();
        List<String> page = pagedToLoad > 0 ? source.page(0, pagedToLoad) : new ArrayList<>();

        Set<String> favorite = new TreeSet<>(NAME_ORDER);
        favorite.addAll(source.existing(favorites));

        LinkedHashSet<String> result = new LinkedHashSet<>(favorite);
        if (!pinned.isEmpty()) {
            Set<String> existingPinned = source.existing(pinned);
            for (String name : pinned) {
                if (existingPinned.contains(name)) {
                    result.add(name);
                }
            }
        }
        result.addAll(page);
        return new CollectionListing(new ArrayList<>(result), page.size(), total);
    }
}
