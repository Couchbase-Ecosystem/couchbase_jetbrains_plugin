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
    private final List<String> pagedNames;
    private final int total;

    private CollectionListing(List<String> names, List<String> pagedNames, int total) {
        this.names = names;
        this.pagedNames = pagedNames;
        this.total = total;
    }

    /** Names to show, in display order. */
    public List<String> getNames() {
        return names;
    }

    /** The alphabetical sequence consumed so far, in source order; {@link #extend} continues after it. */
    public List<String> getPagedNames() {
        return pagedNames;
    }

    /** How many collections of the alphabetical sequence have been consumed so far (the next page's offset). */
    public int getPagedLoaded() {
        return pagedNames.size();
    }

    /** Number of collections in the scope. */
    public int getTotal() {
        return total;
    }

    public boolean hasMore() {
        return pagedNames.size() < total;
    }

    /**
     * Fetches the first {@code pagedToLoad} collections afresh. Used for the first page and whenever the
     * loaded prefix must be re-read (a collection was created, deleted, or favorites changed).
     */
    public static CollectionListing build(CollectionSource source, Collection<String> favorites,
                                          Collection<String> pinned, int pagedToLoad) {
        int total = source.count();
        List<String> page = pagedToLoad > 0 ? source.page(0, pagedToLoad) : new ArrayList<>();
        return assemble(source, favorites, pinned, page, total);
    }

    /**
     * Continues an existing listing: only the {@code more} collections after {@code loaded} travel over the
     * wire, so loading page 100 of a large scope costs the same as loading page 2.
     */
    public static CollectionListing extend(CollectionSource source, Collection<String> favorites,
                                           Collection<String> pinned, List<String> loaded, int more) {
        int total = source.count();
        List<String> page = new ArrayList<>(loaded);
        if (more > 0) {
            page.addAll(source.page(loaded.size(), more));
        }
        return assemble(source, favorites, pinned, page, total);
    }

    private static CollectionListing assemble(CollectionSource source, Collection<String> favorites,
                                              Collection<String> pinned, List<String> page, int total) {
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
        return new CollectionListing(new ArrayList<>(result), List.copyOf(page), total);
    }
}
