package com.couchbase.intellij.database.collections;

import com.couchbase.intellij.workbench.Log;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Uses the preferred source and transparently switches to the fallback if it fails (no query service, missing
 * permission on system:keyspaces, older server...). Once it has failed, the fallback is used for good.
 */
public class FallbackCollectionSource implements CollectionSource {

    private final CollectionSource preferred;
    private final CollectionSource fallback;
    private boolean preferredFailed;

    public FallbackCollectionSource(CollectionSource preferred, CollectionSource fallback) {
        this.preferred = preferred;
        this.fallback = fallback;
    }

    public boolean isUsingFallback() {
        return preferredFailed;
    }

    private <T> T call(Function<CollectionSource, T> op) {
        if (!preferredFailed) {
            try {
                return op.apply(preferred);
            } catch (RuntimeException e) {
                Log.debug("Paged collection listing failed, using the scope manifest instead: " + e.getMessage());
                preferredFailed = true;
            }
        }
        return op.apply(fallback);
    }

    @Override
    public int count() {
        return call(CollectionSource::count);
    }

    @Override
    public List<String> page(int offset, int limit) {
        return call(s -> s.page(offset, limit));
    }

    @Override
    public List<String> search(String text, int limit) {
        return call(s -> s.search(text, limit));
    }

    @Override
    public Set<String> existing(Collection<String> names) {
        return call(s -> s.existing(names));
    }
}
