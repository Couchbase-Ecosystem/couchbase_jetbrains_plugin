package com.couchbase.intellij.database.entity;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CouchbaseDocumentFlavorTest {

    private static JsonObject properties() {
        return JsonObject.create()
                .put("name", JsonObject.create().put("type", "string"))
                .put("age", JsonObject.create().put("type", "number"));
    }

    @Test
    public void sampleSizeFromNumericDocsCount() {
        CouchbaseDocumentFlavor f = new CouchbaseDocumentFlavor(null,
                JsonObject.create().put("#docs", 42).put("properties", properties()));
        assertEquals(Long.valueOf(42), f.getSampleSize());
    }

    @Test
    public void sampleSizeFromArrayIsMaxOfIntegers() {
        CouchbaseDocumentFlavor f = new CouchbaseDocumentFlavor(null,
                JsonObject.create().put("#docs", JsonArray.from(1, 5, 3)).put("properties", properties()));
        assertEquals(Long.valueOf(5), f.getSampleSize());
    }

    @Test
    public void emptyDocsArrayGivesZeroSampleSize() {
        CouchbaseDocumentFlavor f = new CouchbaseDocumentFlavor(null,
                JsonObject.create().put("#docs", JsonArray.create()).put("properties", properties()));
        assertEquals(Long.valueOf(0), f.getSampleSize());
    }

    @Test
    public void nameIsNullAndParentIsCollection() {
        CouchbaseCollection parent = mock(CouchbaseCollection.class);
        Cluster cluster = mock(Cluster.class);
        when(parent.getCluster()).thenReturn(cluster);
        CouchbaseDocumentFlavor f = new CouchbaseDocumentFlavor(parent,
                JsonObject.create().put("#docs", 1).put("properties", properties()));
        assertNull(f.getName());
        assertSame(parent, f.getParent());
        assertSame(cluster, f.getCluster());
        assertEquals(2, f.getChildren().size());
    }

    @Test
    public void generateDocumentFillsZeroValuesForEachField() {
        CouchbaseDocumentFlavor f = new CouchbaseDocumentFlavor(null,
                JsonObject.create().put("#docs", 1).put("properties", properties()));
        JsonObject doc = f.generateDocument();
        assertEquals("", doc.getString("name"));
        assertEquals(0, doc.getInt("age").intValue());
        assertEquals(2, doc.size());
    }

    @Test
    public void toJsonDescribesFieldTypes() {
        CouchbaseDocumentFlavor f = new CouchbaseDocumentFlavor(null,
                JsonObject.create().put("#docs", 1).put("properties", JsonObject.create()
                        .put("name", JsonObject.create().put("type", "string"))));
        assertEquals("{\"name\":[\"string\"]}", f.toJson().toString());
    }

    @Test
    public void getPropertiesReturnsOriginalObject() {
        JsonObject raw = JsonObject.create().put("#docs", 1).put("properties", properties());
        CouchbaseDocumentFlavor f = new CouchbaseDocumentFlavor(null, raw);
        assertSame(raw, f.getProperties());
    }
}
