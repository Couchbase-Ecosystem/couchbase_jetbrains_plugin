package com.couchbase.intellij.database.collections;

import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Works on every cluster (including KV-only ones) but always has to download the whole scope manifest, so it
 * is only used when the query service cannot page for us. The manifest is fetched once per instance.
 */
public class ManifestCollectionSource implements CollectionSource {

    private final Supplier<ScopeSpec> scopeSupplier;
    private List<String> names;

    public ManifestCollectionSource(Supplier<ScopeSpec> scopeSupplier) {
        this.scopeSupplier = scopeSupplier;
    }

    private List<String> names() {
        if (names == null) {
            names = scopeSupplier.get().collections().stream().map(CollectionSpec::name)
                    .sorted(CollectionListing.NAME_ORDER).collect(Collectors.toList());
        }
        return names;
    }

    @Override
    public int count() {
        return names().size();
    }

    @Override
    public List<String> page(int offset, int limit) {
        List<String> all = names();
        if (offset >= all.size() || limit <= 0) {
            return new ArrayList<>();
        }
        return new ArrayList<>(all.subList(offset, Math.min(all.size(), offset + limit)));
    }

    @Override
    public List<String> search(String text, int limit) {
        String needle = text == null ? "" : text.toLowerCase(Locale.ROOT);
        return names().stream().filter(n -> n.toLowerCase(Locale.ROOT).contains(needle)).limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public Set<String> existing(Collection<String> wanted) {
        Set<String> all = new HashSet<>(names());
        return wanted.stream().filter(all::contains).collect(Collectors.toSet());
    }
}
