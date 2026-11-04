package com.couchbase.intellij.database.entity;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.intellij.persistence.SavedCluster;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CouchbaseScopeTest {

    private static JsonObject schemaWithOneFlavor() {
        JsonObject flavor = JsonObject.create()
                .put("#docs", 1)
                .put("properties", JsonObject.create()
                        .put("name", JsonObject.create().put("type", "string")));
        return JsonObject.create().put("content", JsonArray.from(flavor));
    }

    /** A scope whose every collection has a valid cached schema, so inference never needs the IDE or a cluster. */
    private static CouchbaseScope scopeWith(int collections) {
        SavedCluster savedCluster = mock(SavedCluster.class);
        when(savedCluster.isInferCacheValid(anyString())).thenReturn(true);
        when(savedCluster.getInferCacheValue(anyString())).thenReturn(schemaWithOneFlavor());

        CouchbaseBucket bucket = mock(CouchbaseBucket.class);
        when(bucket.getName()).thenReturn("bucket");
        when(bucket.getSavedCluster()).thenReturn(savedCluster);

        Set<CollectionSpec> specs = IntStream.range(0, collections)
                .mapToObj(i -> CollectionSpec.create("c" + i, "scope"))
                .collect(Collectors.toSet());
        return new CouchbaseScope(bucket, ScopeSpec.create("scope", specs));
    }

    private static long inferred(Set<CouchbaseCollection> collections) {
        return collections.stream().filter(c -> !c.getCachedChildren().isEmpty()).count();
    }

    @Test
    public void smallScopesInferEveryCollectionAsSoonAsTheyAreRead() {
        Set<CouchbaseCollection> collections = scopeWith(CouchbaseScope.EAGER_INFER_LIMIT).getChildren();

        assertEquals(CouchbaseScope.EAGER_INFER_LIMIT, collections.size());
        assertEquals(CouchbaseScope.EAGER_INFER_LIMIT, inferred(collections));
    }

    @Test
    public void largeScopesListEveryCollectionButInferOnlyOnFirstUse() {
        int size = CouchbaseScope.EAGER_INFER_LIMIT + 1;
        Set<CouchbaseCollection> collections = scopeWith(size).getChildren();

        assertEquals(size, collections.size());
        assertEquals("no INFER task may start just because the scope was read", 0, inferred(collections));

        CouchbaseCollection one = collections.iterator().next();
        assertEquals(1, one.getChildren().size());
        assertEquals(1, inferred(collections));
    }

    @Test
    public void cachedChildrenNeverStartInference() {
        Set<CouchbaseCollection> collections = scopeWith(CouchbaseScope.EAGER_INFER_LIMIT + 1).getChildren();
        for (CouchbaseCollection c : collections) {
            assertTrue(c.getCachedChildren().isEmpty());
        }
        assertEquals(0, inferred(collections));
    }
}
