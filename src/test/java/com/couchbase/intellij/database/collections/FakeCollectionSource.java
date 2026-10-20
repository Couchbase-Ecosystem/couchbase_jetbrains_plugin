package com.couchbase.intellij.database.collections;

import java.util.*;
import java.util.stream.Collectors;

/** In-memory scope with the same ordering contract as the real sources. */
public class FakeCollectionSource implements CollectionSource {

    public final List<String> names;
    public int pageCalls;
    public int searchCalls;

    public FakeCollectionSource(String... names) {
        this.names = Arrays.stream(names).sorted(CollectionListing.NAME_ORDER).collect(Collectors.toList());
    }

    public static FakeCollectionSource numbered(int count) {
        String[] n = new String[count];
        for (int i = 0; i < count; i++) {
            n[i] = String.format("col%02d", i);
        }
        return new FakeCollectionSource(n);
    }

    @Override
    public int count() {
        return names.size();
    }

    @Override
    public List<String> page(int offset, int limit) {
        pageCalls++;
        if (offset >= names.size()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(names.subList(offset, Math.min(names.size(), offset + limit)));
    }

    @Override
    public List<String> search(String text, int limit) {
        searchCalls++;
        return names.stream().filter(n -> n.toLowerCase().contains(text.toLowerCase())).limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public Set<String> existing(Collection<String> wanted) {
        return wanted.stream().filter(names::contains).collect(Collectors.toSet());
    }
}
