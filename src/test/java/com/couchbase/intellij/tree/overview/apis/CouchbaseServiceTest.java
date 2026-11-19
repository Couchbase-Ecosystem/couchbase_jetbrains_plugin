package com.couchbase.intellij.tree.overview.apis;

import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class CouchbaseServiceTest {

    @Test
    public void eachServiceHasItsPlainAndTlsPort() {
        assertEquals(8091, CouchbaseService.MANAGEMENT.port(false));
        assertEquals(18091, CouchbaseService.MANAGEMENT.port(true));
        assertEquals(8094, CouchbaseService.SEARCH.port(false));
        assertEquals(18094, CouchbaseService.SEARCH.port(true));
        assertEquals(9102, CouchbaseService.INDEX.port(false));
        assertEquals(19102, CouchbaseService.INDEX.port(true));
    }

    @Test
    public void endpointIsPortThenPathAndParsesAsAUrlAuthority() {
        String endpoint = CouchbaseService.MANAGEMENT.endpoint(true, "/pools/default/buckets/travel-sample");
        assertEquals("18091/pools/default/buckets/travel-sample", endpoint);

        URI uri = URI.create("https://node1:" + endpoint);
        assertEquals(18091, uri.getPort());
        assertEquals("/pools/default/buckets/travel-sample", uri.getPath());
    }

    @Test
    public void keyRangeListingKeepsItsEncodedQueryBehindTheServicePort() {
        String path = CouchbaseRestAPI.kvDocumentsPath("b", "s", "c", 0, 10, "a", null);
        URI uri = URI.create("http://node1:" + CouchbaseService.MANAGEMENT.endpoint(false, path));
        assertEquals(8091, uri.getPort());
        assertEquals("/pools/default/buckets/b/scopes/s/collections/c/docs", uri.getPath());
    }
}
