package com.couchbase.intellij.workbench.chart;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.workbench.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ChartUtilTest {

    @Before
    public void captureLog() {
        // loadResourceAsString logs on failure; keep that away from the IDE console
        Log.setPrinter((message, type) -> { });
    }

    @After
    public void resetLog() {
        Log.setPrinter(null);
    }

    @Test
    public void extractsTopLevelAttribute() {
        List<JsonObject> docs = Arrays.asList(
                JsonObject.create().put("name", "a"),
                JsonObject.create().put("name", "b"));

        JsonArray values = ChartUtil.getAttributeValues(docs, "name");

        assertEquals(2, values.size());
        assertEquals("a", values.getString(0));
        assertEquals("b", values.getString(1));
    }

    @Test
    public void extractsDottedNestedAttribute() {
        List<JsonObject> docs = Collections.singletonList(
                JsonObject.create().put("address", JsonObject.create().put("city", "Lisbon")));

        JsonArray values = ChartUtil.getAttributeValues(docs, "address.city");

        assertEquals("Lisbon", values.getString(0));
    }

    @Test
    public void missingPathYieldsNullPerDocument() {
        List<JsonObject> docs = Arrays.asList(
                JsonObject.create().put("a", JsonObject.create().put("b", 1)),
                JsonObject.create().put("a", JsonObject.create()),
                JsonObject.create());

        JsonArray values = ChartUtil.getAttributeValues(docs, "a.b");

        assertEquals(3, values.size());
        assertEquals(1, values.get(0));
        assertNull(values.get(1));
        assertNull(values.get(2));
    }

    @Test
    public void pathThroughScalarYieldsNull() {
        List<JsonObject> docs = Collections.singletonList(JsonObject.create().put("a", "text"));

        JsonArray values = ChartUtil.getAttributeValues(docs, "a.b");

        assertNull(values.get(0));
    }

    @Test
    public void emptyInputYieldsEmptyArray() {
        JsonArray values = ChartUtil.getAttributeValues(new ArrayList<>(), "x");

        assertEquals(0, values.size());
    }

    @Test
    public void extractsArrayValueAsIs() {
        List<JsonObject> docs = Collections.singletonList(JsonObject.create().put("tags", JsonArray.from("x", "y")));

        Object value = ChartUtil.getAttributeValues(docs, "tags").get(0);

        assertTrue(value instanceof JsonArray);
        assertEquals(2, ((JsonArray) value).size());
    }

    @Test
    public void filterOutKeepsKeysWhoseValueIsNotInFilterList() {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("Label", "label_col");
        fields.put("Value", "value_col");
        fields.put("Color", "color_col");

        List<String> options = ChartUtil.filterOutAttributes(fields, Collections.singletonList("value_col"));

        assertEquals(Arrays.asList("Label", "Color"), options);
    }

    @Test
    public void filterOutWithEmptyListKeepsEverything() {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("A", "a");

        assertEquals(Collections.singletonList("A"), ChartUtil.filterOutAttributes(fields, Collections.emptyList()));
    }

    @Test
    public void loadMissingResourceReturnsNull() {
        assertNull(ChartUtil.loadResourceAsString("/does/not/exist.html"));
    }

    @Test
    public void loadExistingResourceReturnsContent() {
        String content = ChartUtil.loadResourceAsString("/tools/README.md");

        assertTrue(content != null && !content.isEmpty());
    }
}
