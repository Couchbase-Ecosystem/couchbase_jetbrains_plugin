package com.couchbase.intellij.database.entity;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.intellij.persistence.SavedCluster;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CouchbaseCollectionTest {

    private static final String PATH = "scope.coll";

    private static JsonObject schemaWithOneFlavor() {
        JsonObject flavor = JsonObject.create()
                .put("#docs", 2)
                .put("properties", JsonObject.create()
                        .put("name", JsonObject.create().put("type", "string")));
        return JsonObject.create().put("content", JsonArray.from(flavor));
    }

    private static CouchbaseCollection collectionWithCachedSchema(JsonObject schema) {
        SavedCluster savedCluster = mock(SavedCluster.class);
        when(savedCluster.isInferCacheValid(anyString())).thenReturn(true);
        when(savedCluster.getInferCacheValue(PATH)).thenReturn(schema);

        CouchbaseScope scope = mock(CouchbaseScope.class);
        when(scope.getName()).thenReturn("scope");
        when(scope.getSavedCluster()).thenReturn(savedCluster);

        CouchbaseCollection collection = new CouchbaseCollection(scope, CollectionSpec.create("coll", "scope"));
        // loads the valid cached schema synchronously (no INFER, no IDE progress task)
        collection.updateSchema();
        return collection;
    }

    @Test
    public void nameAndPathComeFromSpecAndScope() {
        CouchbaseCollection c = collectionWithCachedSchema(schemaWithOneFlavor());
        assertEquals("coll", c.getName());
        assertEquals(PATH, c.path());
    }

    @Test
    public void validCachedSchemaPopulatesFlavors() {
        CouchbaseCollection c = collectionWithCachedSchema(schemaWithOneFlavor());
        Set<CouchbaseDocumentFlavor> flavors = c.getChildren();
        assertEquals(1, flavors.size());
        assertEquals(Long.valueOf(2), flavors.iterator().next().getSampleSize());
    }

    @Test
    public void attributeNamesAreCollectedFromAllFlavors() {
        CouchbaseCollection c = collectionWithCachedSchema(schemaWithOneFlavor());
        assertEquals(Set.of("name"), c.getAllAttributeNames());
    }

    @Test
    public void generateDocumentMergesFlavorZeroValues() {
        CouchbaseCollection c = collectionWithCachedSchema(schemaWithOneFlavor());
        JsonObject doc = c.generateDocument();
        assertEquals("", doc.getString("name"));
    }

    @Test
    public void toJsonHasOneEntryPerFlavor() {
        CouchbaseCollection c = collectionWithCachedSchema(schemaWithOneFlavor());
        assertEquals(1, c.toJson().size());
    }

    @Test
    public void emptyCachedSchemaYieldsNullDocumentAndEmptyJson() {
        CouchbaseCollection c = collectionWithCachedSchema(JsonObject.create().put("content", JsonArray.create()));
        assertTrue(c.getChildren().isEmpty());
        assertNull(c.generateDocument());
        assertEquals(0, c.toJson().size());
    }

    // NOTE: possible bug: toJson() and getAllAttributeNames() call children.get() with no timeout, and
    // Subscribable.get() then does wait(0). On a collection whose schema has not been loaded yet (no
    // updateSchema() call, or a null/empty schema), these methods block the calling thread forever.
    // Not pinned with a test because it would hang; the tests above always load the schema first.

    @Test(timeout = 10_000)
    public void validCacheWithMissingSchemaTimesOutWithNoSuchElement() {
        // NOTE: possible bug: when the cache says "valid" but no schema is stored, updateSchema() never
        // populates children, and getChildren() waits 1000ms then calls Optional.get() on an empty value,
        // which throws NoSuchElementException instead of returning an empty set.
        CouchbaseCollection c = collectionWithCachedSchema(null);
        try {
            c.getChildren();
            fail("expected NoSuchElementException");
        } catch (java.util.NoSuchElementException expected) {
            // pinned current behavior
        }
    }
}
