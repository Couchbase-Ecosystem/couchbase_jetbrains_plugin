package com.couchbase.intellij.database.collections;

import com.couchbase.intellij.database.ActiveCluster;

public final class CollectionSources {

    private CollectionSources() {
    }

    /** Pages through the query service when there is one, and falls back to the scope manifest otherwise. */
    public static CollectionSource forScope(String bucket, String scope) {
        ActiveCluster active = ActiveCluster.getInstance();
        CollectionSource manifest = new ManifestCollectionSource(
                () -> active.get().bucket(bucket).collections().getScope(scope));
        if (!active.hasQueryService()) {
            return manifest;
        }
        return new FallbackCollectionSource(new QueryCollectionSource(active.get(), bucket, scope), manifest);
    }
}
