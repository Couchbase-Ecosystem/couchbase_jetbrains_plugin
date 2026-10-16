package com.couchbase.intellij.database;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.persistence.CollectionRelationships;
import com.couchbase.intellij.persistence.SavedCluster;
import com.couchbase.intellij.persistence.storage.RelationshipStorage;
import com.couchbase.intellij.tree.node.SchemaDataArrayNodeDescriptor;
import com.couchbase.intellij.tree.node.SchemaDataNodeDescriptor;
import com.couchbase.intellij.tree.node.SchemaDataObjectNodeDescriptor;
import com.couchbase.intellij.tree.node.SchemaFlavorNodeDescriptor;
import utils.ColorHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.tree.DefaultMutableTreeNode;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InferHelperTest {

    private ActiveCluster activeCluster;
    private SavedCluster savedCluster;
    private Map<String, Long> updateTimes;
    private MockedStatic<ActiveCluster> mockedActive;
    private MockedStatic<RelationshipStorage> mockedRelationships;
    private MockedStatic<ColorHelper> mockedColors;

    @Before
    public void setUp() {
        activeCluster = mock(ActiveCluster.class);
        when(activeCluster.getId()).thenReturn("c1");

        savedCluster = mock(SavedCluster.class);
        updateTimes = new HashMap<>();
        when(savedCluster.getInferValuesUpdateTimes()).thenReturn(updateTimes);
        when(activeCluster.getSavedCluster()).thenReturn(savedCluster);

        mockedActive = Mockito.mockStatic(ActiveCluster.class);
        mockedActive.when(ActiveCluster::getInstance).thenReturn(activeCluster);

        RelationshipStorage storage = new RelationshipStorage();
        CollectionRelationships rel = new CollectionRelationships();
        Map<String, Map<String, String>> all = new HashMap<>();
        Map<String, String> c1 = new HashMap<>();
        c1.put("b.s.c.tags[*]", "bucket.scope.other.id");
        all.put("c1", c1);
        rel.setRelationships(all);
        storage.setValue(rel);

        mockedRelationships = Mockito.mockStatic(RelationshipStorage.class);
        mockedRelationships.when(RelationshipStorage::getInstance).thenReturn(storage);

        // leaf descriptors build HTML labels through IDE editor colours; stub them out
        mockedColors = Mockito.mockStatic(ColorHelper.class);
        mockedColors.when(ColorHelper::getKeywordColor).thenReturn("#0000ff");
    }

    @After
    public void tearDown() {
        mockedColors.close();
        mockedRelationships.close();
        mockedActive.close();
    }

    private static String field(Object target, String name) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return (String) f.get(target);
    }

    // ---- invalidation ----

    @Test
    public void invalidateCacheResetsUpdateTimeToZeroForLowercasedPath() {
        updateTimes.put("bucket.scope.coll", 123L);
        InferHelper.invalidateCache("Bucket", "Scope", "Coll");
        assertEquals(Long.valueOf(0L), updateTimes.get("bucket.scope.coll"));
    }

    @Test
    public void invalidateCacheWithoutActiveClusterThrows() {
        when(activeCluster.getSavedCluster()).thenReturn(null);
        try {
            InferHelper.invalidateCache("b", "s", "c");
            fail("expected RuntimeException");
        } catch (RuntimeException e) {
            assertEquals("No active cluster", e.getMessage());
        }
    }

    @Test
    public void invalidateIfOlderInvalidatesMissingOrStaleEntries() {
        InferHelper.invalidateInferCacheIfOlder("b", "s", "c", 1000L);
        assertEquals(Long.valueOf(0L), updateTimes.get("b.s.c"));

        updateTimes.put("b.s.c", System.currentTimeMillis() - 60_000L);
        InferHelper.invalidateInferCacheIfOlder("b", "s", "c", 1000L);
        assertEquals(Long.valueOf(0L), updateTimes.get("b.s.c"));
    }

    @Test
    public void invalidateIfOlderKeepsRecentEntries() {
        long recent = System.currentTimeMillis();
        updateTimes.put("b.s.c", recent);
        InferHelper.invalidateInferCacheIfOlder("b", "s", "c", 60_000L);
        assertEquals(Long.valueOf(recent), updateTimes.get("b.s.c"));
    }

    // ---- inferSchema ----

    @Test
    public void inferSchemaReturnsCachedValueWithoutQuerying() {
        JsonObject cached = JsonObject.create().put("content", JsonArray.create());
        when(savedCluster.isInferCacheValid("b.s.c")).thenReturn(true);
        when(savedCluster.getInferCacheValue("b.s.c")).thenReturn(cached);

        assertSame(cached, InferHelper.inferSchema("c", "s", "b"));
        verify(activeCluster, never()).get();
    }

    @Test
    public void inferSchemaReturnsNullWhenQueryFails() {
        when(savedCluster.isInferCacheValid(anyString())).thenReturn(false);
        Cluster cluster = mock(Cluster.class);
        when(cluster.query(anyString())).thenThrow(new RuntimeException("boom"));
        when(activeCluster.get()).thenReturn(cluster);

        assertNull(InferHelper.inferSchema("c", "s", "b"));
    }

    // ---- tree building ----

    @Test
    public void extractTypesBuildsLeafObjectAndArrayNodes() throws Exception {
        JsonObject properties = JsonObject.fromJson("{"
                + "\"name\":{\"type\":\"string\",\"samples\":[\"a\",\"b\"]},"
                + "\"address\":{\"type\":\"object\",\"properties\":{\"city\":{\"type\":\"string\"}}},"
                + "\"tags\":{\"type\":\"array\",\"items\":{\"type\":\"string\"}},"
                + "\"scores\":{\"type\":\"array\",\"items\":{\"type\":\"object\",\"properties\":{\"v\":{\"type\":\"number\"}}}}"
                + "}");
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");

        InferHelper.extractTypes(root, properties, "b.s.c");

        // child order follows JsonObject key order, which is not specified, so look nodes up by path
        assertEquals(4, root.getChildCount());

        DefaultMutableTreeNode name = findByPath(root, "b.s.c.name");
        assertTrue(name.getUserObject() instanceof SchemaDataNodeDescriptor);
        assertEquals("string", field(name.getUserObject(), "value"));

        DefaultMutableTreeNode address = findByPath(root, "b.s.c.address");
        assertTrue(address.getUserObject() instanceof SchemaDataObjectNodeDescriptor);
        assertEquals(1, address.getChildCount());

        DefaultMutableTreeNode tags = findByPath(root, "b.s.c.tags[*]");
        assertTrue(tags.getUserObject() instanceof SchemaDataNodeDescriptor);
        assertEquals("array of strings", field(tags.getUserObject(), "value"));

        DefaultMutableTreeNode scores = findByPath(root, "b.s.c.scores[*]");
        assertTrue(scores.getUserObject() instanceof SchemaDataArrayNodeDescriptor);
        assertEquals(1, scores.getChildCount());
    }

    private static DefaultMutableTreeNode findByPath(DefaultMutableTreeNode parent, String path) throws Exception {
        for (int i = 0; i < parent.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent.getChildAt(i);
            if (path.equals(field(child.getUserObject(), "path"))) {
                return child;
            }
        }
        throw new AssertionError("no node with path " + path);
    }

    @Test
    public void extractTypesJoinsUnionTypesAndPicksUpRelationshipReference() throws Exception {
        JsonObject properties = JsonObject.fromJson("{"
                + "\"maybe\":{\"type\":[\"string\",\"null\"]},"
                + "\"nested\":{\"type\":\"string\"}"
                + "}");
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        InferHelper.extractTypes(root, properties, "b.s.c");

        DefaultMutableTreeNode maybe = findByPath(root, "b.s.c.maybe");
        assertEquals("string | null", field(maybe.getUserObject(), "value"));
    }

    @Test
    public void extractTypesReferenceLookupUsesClusterIdKeyedRelationships() throws Exception {
        // A relationship is registered for "b.s.c.tags[*]" under cluster c1 (see setUp), so an array of
        // scalars at that path should carry the reference through to the node descriptor.
        JsonObject properties = JsonObject.fromJson("{\"tags\":{\"type\":\"array\",\"items\":{\"type\":\"string\"}}}");
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        InferHelper.extractTypes(root, properties, "b.s.c");
        SchemaDataNodeDescriptor d = (SchemaDataNodeDescriptor) findByPath(root, "b.s.c.tags[*]").getUserObject();
        assertEquals("bucket.scope.other.id", field(d, "reference"));
    }

    @Test
    public void extractArrayAddsOneFlavorNodePerPattern() {
        JsonArray flavors = JsonArray.from(
                JsonObject.create().put("#docs", 3).put("Flavor", "shape A")
                        .put("properties", JsonObject.create().put("x", JsonObject.create().put("type", "number"))),
                JsonObject.create().put("#docs", 1).put("Flavor", "shape B"));
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");

        InferHelper.extractArray(root, flavors, "b.s.c");

        assertEquals(2, root.getChildCount());
        DefaultMutableTreeNode first = (DefaultMutableTreeNode) root.getChildAt(0);
        assertTrue(first.getUserObject() instanceof SchemaFlavorNodeDescriptor);
        assertEquals(1, first.getChildCount());
        assertEquals(0, ((DefaultMutableTreeNode) root.getChildAt(1)).getChildCount());
    }
}
