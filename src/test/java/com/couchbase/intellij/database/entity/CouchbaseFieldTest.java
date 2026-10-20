package com.couchbase.intellij.database.entity;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class CouchbaseFieldTest {

    private static CouchbaseDocumentFlavor flavor(JsonObject properties) {
        return new CouchbaseDocumentFlavor(null, JsonObject.create().put("#docs", 1).put("properties", properties));
    }

    private static JsonObject type(String t) {
        return JsonObject.create().put("type", t);
    }

    @Test
    public void stringFieldZeroValueIsEmptyString() {
        CouchbaseDocumentFlavor f = flavor(JsonObject.create());
        CouchbaseField field = new CouchbaseField(f, null, "name", type("string"));
        JsonObject target = JsonObject.create();
        field.addZeroValue(target);
        assertEquals("", target.getString("name"));
    }

    @Test
    public void numberBooleanAndUnknownZeroValues() {
        CouchbaseDocumentFlavor f = flavor(JsonObject.create());
        JsonObject target = JsonObject.create();
        new CouchbaseField(f, null, "n", type("number")).addZeroValue(target);
        new CouchbaseField(f, null, "b", type("boolean")).addZeroValue(target);
        new CouchbaseField(f, null, "x", type("binary")).addZeroValue(target);
        assertEquals(0, target.getInt("n").intValue());
        assertFalse(target.getBoolean("b"));
        assertTrue(target.containsKey("x"));
        assertNull(target.get("x"));
    }

    @Test
    public void objectZeroValueRecursesIntoChildren() {
        CouchbaseDocumentFlavor f = flavor(JsonObject.create());
        JsonObject props = JsonObject.create().put("address", type("object")
                .put("properties", JsonObject.create().put("city", type("string"))));
        CouchbaseField obj = new CouchbaseField(f, null, "address", props.getObject("address"));
        assertTrue(obj.hasChildren());
        JsonObject target = JsonObject.create();
        obj.addZeroValue(target);
        assertEquals("", target.getObject("address").getString("city"));
    }

    @Test
    public void arrayZeroValueIsSingleSampleElement() {
        CouchbaseDocumentFlavor f = flavor(JsonObject.create());
        JsonObject arr = type("array").put("items", type("object")
                .put("properties", JsonObject.create().put("x", type("number"))));
        CouchbaseField field = new CouchbaseField(f, null, "list", arr);
        JsonObject target = JsonObject.create();
        field.addZeroValue(target);
        JsonArray list = target.getArray("list");
        assertEquals(1, list.size());
        assertEquals(0, list.getObject(0).getInt("x").intValue());
    }

    @Test
    public void unionTypeIgnoresNullAndUsesFirstConcreteType() {
        CouchbaseDocumentFlavor f = flavor(JsonObject.create());
        JsonObject union = JsonObject.create().put("type", JsonArray.from("null", "number"));
        CouchbaseField field = new CouchbaseField(f, null, "n", union);
        JsonObject target = JsonObject.create();
        field.addZeroValue(target);
        assertEquals(0, target.getInt("n").intValue());
    }

    @Test
    public void leafToJsonIsArrayOfTypes() {
        CouchbaseDocumentFlavor f = flavor(JsonObject.create());
        CouchbaseField field = new CouchbaseField(f, null, "name", type("string"));
        assertFalse(field.hasChildren());
        assertEquals(JsonArray.from("string").toString(), field.toJson().toString());
    }

    @Test
    public void objectToJsonIsNestedObject() {
        CouchbaseDocumentFlavor f = flavor(JsonObject.create());
        JsonObject obj = type("object").put("properties", JsonObject.create().put("city", type("string")));
        CouchbaseField field = new CouchbaseField(f, null, "address", obj);
        assertEquals("{\"city\":[\"string\"]}", field.toJson().toString());
    }

    @Test
    public void parentFallsBackToFlavorWhenNoParentField() {
        CouchbaseDocumentFlavor f = flavor(JsonObject.create());
        CouchbaseField top = new CouchbaseField(f, null, "name", type("string"));
        assertSame(f, top.getParent());
        CouchbaseField child = new CouchbaseField(f, top, "child", type("string"));
        assertSame(top, child.getParent());
        assertEquals("name", top.getName());
    }

    @Test
    public void nullTypesAreFilteredFromUnionAndNoTypeGivesEmptyChildren() {
        CouchbaseDocumentFlavor f = flavor(JsonObject.create());
        CouchbaseField onlyNull = new CouchbaseField(f, null, "z", JsonObject.create().put("type", JsonArray.from("null")));
        assertFalse(onlyNull.hasChildren());
        assertEquals("[]", onlyNull.toJson().toString());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void addZeroValueWithNoTypesThrowsAfterWritingEmptyString() {
        // NOTE: possible bug: when no concrete type is available, addZeroValue puts "" and then still
        // calls types.get(0), so it throws instead of returning after the first branch.
        CouchbaseDocumentFlavor f = flavor(JsonObject.create());
        CouchbaseField onlyNull = new CouchbaseField(f, null, "z", JsonObject.create().put("type", JsonArray.from("null")));
        onlyNull.addZeroValue(JsonObject.create());
    }
}
