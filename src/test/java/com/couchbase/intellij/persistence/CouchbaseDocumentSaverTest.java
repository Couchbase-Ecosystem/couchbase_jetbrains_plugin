package com.couchbase.intellij.persistence;

import com.couchbase.client.java.Collection;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CouchbaseDocumentSaverTest {

    @Test
    public void anySingleJsonValueIsAccepted() throws IOException {
        assertTrue(CouchbaseDocumentSaver.requireJson("[1,2,3]").isArray());
        assertTrue(CouchbaseDocumentSaver.requireJson("{\"a\":1}").isObject());
        assertTrue(CouchbaseDocumentSaver.requireJson("\"text\"").isTextual());
        assertTrue(CouchbaseDocumentSaver.requireJson("  {\"a\":[{}]}\n").isObject());
    }

    @Test
    public void trailingTokensEmptyAndMalformedContentAreRejected() {
        for (String invalid : new String[]{"{\"a\":1}}", "{\"a\":1} trailing", "[1] [2]", "", "   \n", "{\"a\":", "{a:1}"}) {
            assertThrows(invalid, IOException.class, () -> CouchbaseDocumentSaver.requireJson(invalid));
        }
    }

    @Test
    public void invalidJsonNeverReachesTheServer() {
        Collection collection = mock(Collection.class);
        assertThrows(IOException.class, () -> CouchbaseDocumentSaver.save(collection, "id", "{\"a\":1}}", 1L));
        assertThrows(IOException.class, () -> CouchbaseDocumentSaver.save(collection, "id", "", null));
        verifyNoInteractions(collection);
    }

    @Test
    public void replayComparisonIgnoresLineSeparatorsAndFormatting() {
        String saved = "{\n  \"a\" : [ 1, 2 ]\n}";
        assertTrue(CouchbaseDocumentSaver.sameJson(saved, saved.replace("\n", "\r\n")));
        assertTrue(CouchbaseDocumentSaver.sameJson(saved, saved + "\n"));
        assertTrue(CouchbaseDocumentSaver.sameJson("{\n  \"a\" : 1   \n}", "{\n  \"a\" : 1\n}"));
        assertFalse(CouchbaseDocumentSaver.sameJson(saved, "{\"a\":[1,3]}"));
        assertFalse(CouchbaseDocumentSaver.sameJson(saved, "{\"a\":[1,2]}}"));
        assertFalse(CouchbaseDocumentSaver.sameJson(null, saved));
    }

    @Test
    public void prettyPrintAlwaysUsesLineFeeds() throws IOException {
        String pretty = new String(CouchbaseDocumentSaver.prettyPrint("{\"a\":{\"b\":[1,2]}}".getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        assertTrue(pretty.contains("\n"));
        assertFalse(pretty.contains("\r"));
        assertEquals(CouchbaseDocumentSaver.requireJson("{\"a\":{\"b\":[1,2]}}"), CouchbaseDocumentSaver.requireJson(pretty));
        assertTrue(new String(CouchbaseDocumentSaver.prettyPrint("[1,2]".getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8).startsWith("["));
    }

    @Test
    public void prettyPrintRejectsContentThatIsNotOneJsonValue() {
        // DataLoader opens these read-only instead of as editable JSON.
        assertThrows(com.fasterxml.jackson.core.JsonProcessingException.class,
                () -> CouchbaseDocumentSaver.prettyPrint("{\"a\":1}}".getBytes(StandardCharsets.UTF_8)));
        assertThrows(com.fasterxml.jackson.core.JsonProcessingException.class,
                () -> CouchbaseDocumentSaver.prettyPrint(new byte[0]));
    }
}
