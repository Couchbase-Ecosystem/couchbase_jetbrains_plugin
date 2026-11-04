package com.couchbase.intellij.tree.overview.apis;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Only the non-Capella path is covered: cloud.couchbase.com URLs trigger a real DNS SRV lookup and are deliberately not used.
 */
public class NSLookupTest {

    @Test
    public void couchbaseSchemeIsStripped() {
        Assert.assertEquals(List.of("10.0.0.5"), NSLookup.getServerURL("couchbase://10.0.0.5"));
    }

    @Test
    public void couchbasesSchemeIsStripped() {
        Assert.assertEquals(List.of("db.example.com"), NSLookup.getServerURL("couchbases://db.example.com"));
    }

    @Test
    public void plainHostIsReturnedUnchanged() {
        Assert.assertEquals(List.of("127.0.0.1:8091"), NSLookup.getServerURL("127.0.0.1:8091"));
    }

    @Test
    public void httpPrefixIsNotStripped() {
        // NOTE: possible bug: only couchbase:// and couchbases:// are stripped, so an http:// URL is passed through
        // and later concatenated into "http://http://host:8091/..." by CouchbaseRestAPI.
        Assert.assertEquals(List.of("http://host"), NSLookup.getServerURL("http://host"));
    }

    @Test
    public void commaSeparatedHostsAreNotSplit() {
        // NOTE: possible bug: multi-host connection strings are returned as a single entry
        Assert.assertEquals(List.of("a.example.com,b.example.com"),
                NSLookup.getServerURL("couchbase://a.example.com,b.example.com"));
    }
}
