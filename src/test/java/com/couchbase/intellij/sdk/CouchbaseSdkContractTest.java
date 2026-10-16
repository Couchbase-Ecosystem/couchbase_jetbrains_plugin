package com.couchbase.intellij.sdk;

import com.couchbase.client.core.api.query.CoreQueryMetaData;
import com.couchbase.client.core.api.query.CoreQueryResult;
import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.ObjectMapper;
import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.core.error.IndexExistsException;
import com.couchbase.client.core.error.IndexFailureException;
import com.couchbase.client.core.error.PlanningFailureException;
import com.couchbase.client.core.error.TimeoutException;
import com.couchbase.client.core.msg.query.QueryChunkRow;
import com.couchbase.client.core.node.NodeIdentifier;
import com.couchbase.client.java.codec.JacksonJsonSerializer;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.collection.CollectionManager;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.query.QueryResult;
import com.couchbase.intellij.persistence.QueryFilter;
import org.junit.Test;
import utils.JsonObjectUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * Pins the parts of the Couchbase SDK that this plugin depends on <em>without</em> the compiler being able to
 * notice when they change: reflection into SDK internals, SDK-shaded third-party classes, exception types and
 * the exact wording of an SDK error message. These must keep passing across SDK upgrades.
 */
public class CouchbaseSdkContractTest {

    /** Same prefix that QueryExecutor / InferHelper match on to detect the "INFER returns arrays" SDK quirk. */
    private static final String DESERIALIZATION_FAILURE_PREFIX =
            "Deserialization of content into target class com.couchbase.client.java.json.JsonObject failed";

    private static QueryResult resultWithRows(String... jsonRows) {
        CoreQueryResult core = new CoreQueryResult() {
            @Override
            public Stream<QueryChunkRow> rows() {
                return Stream.of(jsonRows).map(r -> new QueryChunkRow(r.getBytes(StandardCharsets.UTF_8)));
            }

            @Override
            public CoreQueryMetaData metaData() {
                return null;
            }

            @Override
            public NodeIdentifier lastDispatchedTo() {
                return null;
            }
        };
        return new QueryResult(core, JacksonJsonSerializer.create());
    }

    // ---- INFER workaround (QueryExecutor + InferHelper) -------------------------------------------------------

    @Test
    public void queryResultStillExposesPrivateCoreResultFieldNamedInternal() throws Exception {
        Field field = QueryResult.class.getDeclaredField("internal");
        assertTrue(CoreQueryResult.class.isAssignableFrom(field.getType()));
        assertFalse("workaround relies on the field being private & reflectively readable",
                Modifier.isPublic(field.getModifiers()));
        field.setAccessible(true);
        assertNotNull(field.get(resultWithRows("{}")));
    }

    @Test
    public void objectRowsDeserializeNormally() {
        List<JsonObject> rows = resultWithRows("{\"a\":1}", "{\"b\":\"x\"}").rowsAsObject();
        assertEquals(2, rows.size());
        assertEquals(1, (int) rows.get(0).getInt("a"));
        assertEquals("x", rows.get(1).getString("b"));
    }

    @Test
    public void arrayRowsStillFailWithTheMessageTheWorkaroundMatchesOn() {
        QueryResult result = resultWithRows("[{\"flavor\":\"x\"}]");
        try {
            result.rowsAsObject();
            fail("INFER-style array rows must still fail rowsAsObject(); if the SDK now handles them the "
                    + "reflection workaround can be deleted");
        } catch (Exception ex) {
            assertNotNull(ex.getMessage());
            assertTrue("unexpected message: " + ex.getMessage(), ex.getMessage().startsWith(DESERIALIZATION_FAILURE_PREFIX));
        }
    }

    @Test
    public void rawRowsCanBeRecoveredViaReflectionAsTheWorkaroundDoes() throws Exception {
        QueryResult result = resultWithRows("[{\"flavor\":\"x\",\"#docs\":3}]");

        Field field = QueryResult.class.getDeclaredField("internal");
        field.setAccessible(true);
        CoreQueryResult internal = (CoreQueryResult) field.get(result);

        List<JsonObject> out = new ArrayList<>();
        internal.rows().forEach(e -> {
            JsonObject obj = JsonObject.create();
            obj.put("content", JsonArray.fromJson(e.data()));
            out.add(obj);
        });

        assertEquals(1, out.size());
        JsonObject first = out.get(0).getArray("content").getObject(0);
        assertEquals("x", first.getString("flavor"));
        assertEquals(3, (int) first.getInt("#docs"));
    }

    // ---- SDK-shaded Jackson (QueryFilterUtil, CouchbaseDocumentVirtualFile, CapellaApiMethods) -----------------

    @Test
    public void queryFilterRoundTripsThroughSdkShadedObjectMapper() throws Exception {
        QueryFilter filter = new QueryFilter();
        filter.setQuery("type = 'airline'");
        filter.setDocumentStartKey("a");
        filter.setDocumentEndKey("z");
        filter.setOffset(7);

        ObjectMapper mapper = new ObjectMapper();
        QueryFilter back = mapper.readValue(mapper.writeValueAsString(filter), QueryFilter.class);

        assertEquals(filter, back);
    }

    @Test
    public void legacyPlainTextQueryFilterIsRejectedByMapperSoCallersFallBack() {
        // QueryFilterUtil.getQueryFilter relies on this throwing to treat old-format values as a bare query.
        try {
            new ObjectMapper().readValue("type = 'airline'", QueryFilter.class);
            fail("expected a parse failure");
        } catch (Exception expected) {
            // ok
        }
    }

    // ---- Manager API used for collection create/drop ---------------------------------------------------------

    @Test
    public void collectionManagerOffersStringBasedCreateAndDrop() throws Exception {
        assertNotNull(CollectionManager.class.getMethod("createCollection", String.class, String.class));
        assertNotNull(CollectionManager.class.getMethod("dropCollection", String.class, String.class));
    }

    @Test
    public void collectionSpecStillCarriesNameAndScopeForCallersThatBuildOne() {
        CollectionSpec spec = CollectionSpec.create("c", "s");
        assertEquals("c", spec.name());
        assertEquals("s", spec.scopeName());
    }

    // ---- Exception hierarchy the plugin catches --------------------------------------------------------------

    @Test
    public void caughtSdkExceptionsAreStillCouchbaseExceptions() {
        assertTrue(CouchbaseException.class.isAssignableFrom(DocumentNotFoundException.class));
        assertTrue(CouchbaseException.class.isAssignableFrom(IndexExistsException.class));
        assertTrue(CouchbaseException.class.isAssignableFrom(IndexFailureException.class));
        assertTrue(CouchbaseException.class.isAssignableFrom(PlanningFailureException.class));
        assertTrue(CouchbaseException.class.isAssignableFrom(TimeoutException.class));
    }

    // ---- JSON types used all over the plugin -----------------------------------------------------------------

    @Test
    public void generatePathsDescribesNestedObjectsAndArrays() {
        JsonObject doc = JsonObject.fromJson(
                "{\"name\":\"x\",\"age\":3,\"tags\":[\"a\",\"b\"],\"addr\":{\"city\":\"y\"},"
                        + "\"items\":[{\"sku\":\"s\"}],\"nothing\":null}");

        Map<String, String> paths = JsonObjectUtil.generatePaths(doc, "");

        assertEquals("String", paths.get("name"));
        assertEquals("Integer", paths.get("age"));
        assertEquals("String[]", paths.get("tags[*]"));
        assertEquals("String", paths.get("addr.city"));
        assertEquals("String", paths.get("items[*].sku"));
        assertEquals("Unknown", paths.get("nothing"));
    }

    @Test
    public void generatePathsCanSkipArrays() {
        JsonObject doc = JsonObject.fromJson("{\"tags\":[\"a\"],\"n\":1}");
        Map<String, String> paths = JsonObjectUtil.generatePaths(doc, "", true);
        assertEquals(Map.of("n", "Integer"), paths);
    }

    @Test
    public void jsonObjectTextRoundTrips() {
        JsonObject o = JsonObject.create().put("a", 1).put("b", JsonArray.from("x", 2));
        assertEquals(o, JsonObject.fromJson(o.toString()));
    }
}
