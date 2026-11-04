package com.couchbase.intellij.database.collections;

import com.couchbase.intellij.workbench.Log;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.LongSupplier;

/**
 * Uses the preferred source and transparently switches to the fallback if it fails (no query service, missing
 * permission on system:keyspaces, older server...). The preferred source is tried again once
 * {@link #RETRY_AFTER_MS} has passed, so a transient failure such as a timeout does not pin the slower manifest
 * path for the rest of the session.
 */
public class FallbackCollectionSource implements CollectionSource {

    /** How long the fallback is used after the preferred source failed, before the preferred one is retried. */
    public static final long RETRY_AFTER_MS = 5 * 60 * 1000L;

    private final CollectionSource preferred;
    private final CollectionSource fallback;
    private final LongSupplier clock;
    /** Epoch millis of the last preferred failure, or 0 when it has not failed (shared across worker threads). */
    private volatile long preferredFailedAt;

    public FallbackCollectionSource(CollectionSource preferred, CollectionSource fallback) {
        this(preferred, fallback, System::currentTimeMillis);
    }

    FallbackCollectionSource(CollectionSource preferred, CollectionSource fallback, LongSupplier clock) {
        this.preferred = preferred;
        this.fallback = fallback;
        this.clock = clock;
    }

    public boolean isUsingFallback() {
        long failedAt = preferredFailedAt;
        return failedAt != 0 && clock.getAsLong() - failedAt < RETRY_AFTER_MS;
    }

    private <T> T call(Function<CollectionSource, T> op) {
        if (!isUsingFallback()) {
            try {
                T result = op.apply(preferred);
                preferredFailedAt = 0;
                return result;
            } catch (RuntimeException e) {
                Log.debug("Paged collection listing failed, using the scope manifest instead: " + e.getMessage());
                preferredFailedAt = clock.getAsLong();
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
