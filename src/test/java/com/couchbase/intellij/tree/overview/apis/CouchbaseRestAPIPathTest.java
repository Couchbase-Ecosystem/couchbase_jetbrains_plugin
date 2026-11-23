package com.couchbase.intellij.tree.overview.apis;

import org.junit.Test;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class CouchbaseRestAPIPathTest {

    private static String query(String path) {
        return URI.create("https://host:18091" + path).getRawQuery();
    }

    @Test
    public void keyRangeFiltersAreQuotedJsonStringsThatSurviveUriParsing() {
        String path = CouchbaseRestAPI.kvDocumentsPath("travel-sample", "inventory", "airline", 0, 10, "airline_1", "airline_9");

        String decoded = URLDecoder.decode(query(path), StandardCharsets.UTF_8);
        assertTrue(decoded, decoded.contains("startkey=\"airline_1\""));
        assertTrue(decoded, decoded.contains("endkey=\"airline_9\""));
        assertTrue(decoded, decoded.startsWith("skip=0&limit=10"));
        assertTrue(decoded, decoded.endsWith("&include_doc=false"));
        assertEquals("/pools/default/buckets/travel-sample/scopes/inventory/collections/airline/docs",
                URI.create("https://host:18091" + path).getPath());
    }

    @Test
    public void keysWithQuotesSpacesAndAmpersandsAreEscapedNotSpliced() {
        String path = CouchbaseRestAPI.kvDocumentsPath("b", "s", "c", 5, 20, "a \"b\" & c=d", null);

        String raw = query(path);
        assertFalse(raw, raw.contains("\""));
        assertEquals("only the fixed parameters may contain a raw ampersand", 3, raw.chars().filter(ch -> ch == '&').count());
        assertTrue(URLDecoder.decode(raw, StandardCharsets.UTF_8).contains("startkey=\"a \\\"b\\\" & c=d\""));
        assertFalse(raw.contains("endkey"));
    }

    @Test
    public void noFilterGivesNoKeyParameters() {
        String raw = query(CouchbaseRestAPI.kvDocumentsPath("b", "s", "c", 0, 10, null, null));
        assertEquals("skip=0&limit=10&include_doc=false", raw);
    }

    @Test
    public void documentKeysAreOnePercentEncodedPathSegment() {
        String path = CouchbaseRestAPI.docPath("travel-sample", "inventory", "airline", "draft[1] a/b?c#d%e\"f");

        URI uri = URI.create("https://host:18091" + path);
        assertNull(uri.getRawQuery());
        assertNull(uri.getRawFragment());
        assertEquals("/pools/default/buckets/travel-sample/scopes/inventory/collections/airline/docs/draft[1] a/b?c#d%e\"f",
                uri.getPath());
        assertTrue(uri.getRawPath(), uri.getRawPath().endsWith("/docs/draft%5B1%5D%20a%2Fb%3Fc%23d%25e%22f"));
    }

    @Test
    public void plainNamesAreUnchanged() {
        assertEquals("/pools/default/buckets/b/scopes/_default/collections/_default/docs/airline_10",
                CouchbaseRestAPI.docPath("b", "_default", "_default", "airline_10"));
    }
}
