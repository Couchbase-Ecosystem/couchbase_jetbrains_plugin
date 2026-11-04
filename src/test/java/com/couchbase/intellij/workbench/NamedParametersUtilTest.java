package com.couchbase.intellij.workbench;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NamedParametersUtilTest {

    @Test
    public void booleansAreParsedCaseInsensitively() {
        assertEquals(Boolean.TRUE, NamedParametersUtil.parseValue("true"));
        assertEquals(Boolean.TRUE, NamedParametersUtil.parseValue("TRUE"));
        assertEquals(Boolean.FALSE, NamedParametersUtil.parseValue("False"));
    }

    @Test
    public void integersBecomeLong() {
        Object v = NamedParametersUtil.parseValue("42");
        assertTrue(v instanceof Long);
        assertEquals(42L, v);
        assertEquals(-7L, NamedParametersUtil.parseValue("-7"));
    }

    @Test
    public void decimalsBecomeDouble() {
        Object v = NamedParametersUtil.parseValue("1.0");
        assertTrue(v instanceof Double);
        assertEquals(1.0d, v);
    }

    @Test
    public void jsonArrayIsParsed() {
        Object v = NamedParametersUtil.parseValue("[1,\"a\"]");
        assertTrue(v instanceof JsonArray);
        assertEquals(2, ((JsonArray) v).size());
    }

    @Test
    public void jsonObjectIsParsed() {
        Object v = NamedParametersUtil.parseValue("{\"k\":\"v\"}");
        assertTrue(v instanceof JsonObject);
        assertEquals("v", ((JsonObject) v).getString("k"));
    }

    @Test
    public void quotedStringIsUnquoted() {
        assertEquals("hello world", NamedParametersUtil.parseValue("\"hello world\""));
    }

    @Test
    public void plainStringIsReturnedAsIs() {
        assertEquals("hello", NamedParametersUtil.parseValue("hello"));
    }

    @Test
    public void emptyStringIsReturnedAsEmptyString() {
        assertEquals("", NamedParametersUtil.parseValue(""));
    }

    @Test
    public void leadingWhitespaceNumberIsParsedAsDouble() {
        // Double.parseDouble trims whitespace, so " 5" becomes 5.0 rather than a String
        Object v = NamedParametersUtil.parseValue(" 5");
        assertTrue(v instanceof Double);
        assertEquals(5.0d, v);
    }

    @Test
    public void singleQuoteCharacterThrows() {
        // NOTE: possible bug: a lone double quote satisfies startsWith and endsWith, so substring(1, 0)
        // throws StringIndexOutOfBoundsException from inside the catch block instead of returning the value.
        try {
            NamedParametersUtil.parseValue("\"");
            org.junit.Assert.fail("expected StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            // pinned current behavior
        }
    }
}
