package com.couchbase.intellij.database.collections;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.client.core.error.ScopeNotFoundException;
import com.couchbase.client.java.manager.collection.CollectionManager;
import com.couchbase.client.java.manager.collection.ScopeSpec;

public final class CollectionSources {

    private CollectionSources() {
    }

    public static ScopeSpec requireScope(CollectionManager manager, String scope) {
        return manager.getAllScopes().stream().filter(candidate -> candidate.name().equals(scope))
                .findFirst().orElseThrow(() -> ScopeNotFoundException.forScope(scope));
    }

    /** Pages through the query service when there is one, and falls back to the scope manifest otherwise. */
    public static CollectionSource forScope(String bucket, String scope) {
        ActiveCluster active = ActiveCluster.getInstance();
        CollectionSource manifest = new ManifestCollectionSource(
                () -> requireScope(active.get().bucket(bucket).collections(), scope));
        if (!active.hasQueryService()) {
            return manifest;
        }
        return new FallbackCollectionSource(new QueryCollectionSource(active.get(), bucket, scope), manifest);
    }
}
