package utils;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class JsonObjectUtilTest {

    private static JsonObject sample() {
        return JsonObject.create()
                .put("a", JsonObject.create().put("b", 1))
                .put("c", "x")
                .put("arr", JsonArray.from(JsonObject.create().put("k", true)))
                .put("nums", JsonArray.from(1, 2))
                .put("empty", JsonArray.create())
                .putNull("n");
    }

    @Test
    public void generatesDottedPathsWithJavaTypeNames() {
        Map<String, String> paths = JsonObjectUtil.generatePaths(sample(), "");
        assertEquals("Integer", paths.get("a.b"));
        assertEquals("String", paths.get("c"));
        assertEquals("Boolean", paths.get("arr[*].k"));
        assertEquals("Integer[]", paths.get("nums[*]"));
        assertEquals("Unknown", paths.get("n"));
        assertFalse(paths.containsKey("empty"));
        assertFalse(paths.containsKey("empty[*]"));
    }

    @Test
    public void prefixIsPrependedToEveryPath() {
        Map<String, String> paths = JsonObjectUtil.generatePaths(sample(), "root");
        assertEquals("Integer", paths.get("root.a.b"));
        assertEquals("String", paths.get("root.c"));
        assertFalse(paths.containsKey("a.b"));
    }

    @Test
    public void skipArraysOmitsArrayEntries() {
        Map<String, String> paths = JsonObjectUtil.generatePaths(sample(), "", true);
        assertEquals("Integer", paths.get("a.b"));
        assertFalse(paths.containsKey("arr[*].k"));
        assertFalse(paths.containsKey("nums[*]"));
    }

    @Test
    public void emptyObjectGivesNoPaths() {
        assertTrue(JsonObjectUtil.generatePaths(JsonObject.create(), "p").isEmpty());
    }

    @Test
    public void pathsAreSortedBecauseResultIsTreeMap() {
        Map<String, String> paths = JsonObjectUtil.generatePaths(sample(), "");
        assertEquals(java.util.List.of("a.b", "arr[*].k", "c", "n", "nums[*]"), java.util.List.copyOf(paths.keySet()));
    }

    @Test
    public void arrayWithNullFirstElementThrowsNullPointerException() {
        // NOTE: possible bug: an array whose first element is null dereferences firstElement.getClass()
        // without a null check.
        JsonObject withNullFirst = JsonObject.create().put("list", JsonArray.create().addNull().add(1));
        try {
            JsonObjectUtil.generatePaths(withNullFirst, "");
            fail("expected NullPointerException");
        } catch (NullPointerException expected) {
            // pinned current behavior
        }
    }
}
