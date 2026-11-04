package com.couchbase.intellij.persistence;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.persistence.storage.ClustersStorage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class SavedClusterTest {

    private ClustersStorage storage;
    private MockedStatic<ClustersStorage> mockedStorage;

    @Before
    public void setUp() {
        storage = new ClustersStorage();
        mockedStorage = Mockito.mockStatic(ClustersStorage.class);
        mockedStorage.when(ClustersStorage::getInstance).thenReturn(storage);
    }

    @After
    public void tearDown() {
        mockedStorage.close();
    }

    private static SavedCluster cluster(String id) {
        SavedCluster sc = new SavedCluster();
        sc.setId(id);
        sc.setName("name-" + id);
        return sc;
    }

    @Test
    public void certificateConfigurationRoundTripsThroughIdeStorage() {
        SavedCluster saved = cluster("certificate:test");
        saved.setAuthenticationType("certificate");
        saved.setClientKeyStorePath("/certificates/client.p12");
        saved.setServerCaCertificatePath("/certificates/ca.pem");
        var xml = com.intellij.util.xmlb.XmlSerializer.serialize(saved);
        SavedCluster restored = com.intellij.util.xmlb.XmlSerializer.deserialize(xml, SavedCluster.class);
        assertTrue(restored.usesCertificateAuthentication());
        assertEquals(saved.getClientKeyStorePath(), restored.getClientKeyStorePath());
        assertEquals(saved.getServerCaCertificatePath(), restored.getServerCaCertificatePath());
        assertFalse(new SavedCluster().usesCertificateAuthentication());
    }

    @Test
    public void ldapDefaultsToFalseAndIsSettable() {
        SavedCluster sc = cluster("c1");
        assertFalse(sc.getLDAP());
        sc.setLDAP(true);
        assertTrue(sc.getLDAP());
        sc.setLDAP(null);
        assertFalse(sc.getLDAP());
    }

    @Test
    public void queryPreferencesHaveDefaultsAndAreCached() {
        SavedCluster sc = cluster("c1");
        QueryPreferences first = sc.getQueryPreferences();
        assertEquals(600, first.getQueryTimeout());
        assertTrue(first.isSaveHistory());
        assertSame(first, sc.getQueryPreferences());
    }

    @Test
    public void optionsStartEmpty() {
        SavedCluster sc = cluster("c1");
        assertNull(sc.getOptions());
        assertEquals(0, sc.options().count());
        assertFalse(sc.hasOption(Clusters.Options.LIVE_POLLING));
    }

    @Test
    public void addOptionAppendsCommaSeparated() {
        SavedCluster sc = cluster("c1");
        sc.addOption(Clusters.Options.LIVE_POLLING);
        assertEquals("live_polling", sc.getOptions());
        assertTrue(sc.hasOption(Clusters.Options.LIVE_POLLING));
        assertFalse(sc.hasOption(Clusters.Options.LIVE_RELOAD));

        sc.addOption("custom");
        assertEquals("live_polling,custom", sc.getOptions());
    }

    @Test
    public void removeOptionDropsAllMatches() {
        SavedCluster sc = cluster("c1");
        sc.setOptions("a,b,a");
        sc.removeOption("a");
        assertEquals("b", sc.getOptions());
    }

    @Test
    public void removeOptionWithNoOptionsIsNoop() {
        SavedCluster sc = cluster("c1");
        sc.removeOption("a");
        assertNull(sc.getOptions());
        sc.removeOption(Clusters.Options.LIVE_RELOAD);
        assertNull(sc.getOptions());
    }

    @Test
    public void optionsSetterJoinsSetWithCommas() {
        SavedCluster sc = cluster("c1");
        Set<String> data = new LinkedHashSet<>();
        data.add("live_polling");
        data.add("live_reload");
        sc.options(data);
        assertEquals("live_polling,live_reload", sc.getOptions());
        assertTrue(sc.hasOption(Clusters.Options.LIVE_RELOAD));
    }

    @Test
    public void emptyOptionStringYieldsSingleEmptyEntry() {
        // NOTE: edge case pinned: an empty options string is split into one empty token, not zero tokens.
        SavedCluster sc = cluster("c1");
        sc.setOptions("");
        assertEquals(1, sc.options().count());
    }

    @Test
    public void inferCacheValueRoundTripsForValidEntry() {
        SavedCluster sc = cluster("c1");
        JsonObject schema = JsonObject.create().put("content", 42);
        sc.setInferCacheValue("b.s.c", schema);
        JsonObject read = sc.getInferCacheValue("b.s.c");
        assertNotNull(read);
        assertEquals(schema.toString(), read.toString());
        assertTrue(sc.isInferCacheValid("b.s.c"));
        assertTrue(sc.getInferCacheValues().containsKey("b.s.c"));
    }

    @Test
    public void nullInferValueIsStoredAsNull() {
        SavedCluster sc = cluster("c1");
        sc.setInferCacheValue("b.s.c", null);
        assertNull(sc.getInferCacheValue("b.s.c"));
        assertTrue(sc.isInferCacheValid("b.s.c"));
    }

    @Test
    public void unknownKeyIsInvalidAndReturnsNull() {
        SavedCluster sc = cluster("c1");
        assertFalse(sc.isInferCacheValid("missing"));
        assertNull(sc.getInferCacheValue("missing"));
    }

    @Test
    public void expiredEntryIsNotReturnedEvenIfPresent() {
        SavedCluster sc = cluster("c1");
        sc.setInferCacheValue("k", JsonObject.create().put("a", 1));
        long fourDaysAgo = System.currentTimeMillis() - java.util.concurrent.TimeUnit.DAYS.toMillis(4);
        sc.getInferValuesUpdateTimes().put("k", fourDaysAgo);
        assertFalse(sc.isInferCacheValid("k"));
        assertNull(sc.getInferCacheValue("k"));
    }

    @Test
    public void recentlyUpdatedEntryIsValidWithinDefaultPeriod() {
        SavedCluster sc = cluster("c1");
        sc.getInferValuesUpdateTimes().put("k", System.currentTimeMillis() - java.util.concurrent.TimeUnit.DAYS.toMillis(1));
        assertTrue(sc.isInferCacheValid("k"));
    }

    @Test
    public void zeroOrUnsetPeriodFallsBackToThreeDays() {
        SavedCluster sc = cluster("c1");
        assertEquals(0L, sc.getInferCachePeriod().longValue());
        sc.isInferCacheValid("k");
        assertEquals(java.util.concurrent.TimeUnit.DAYS.toMillis(3), sc.getInferCachePeriod().longValue());

        sc.setInferCachePeriod(0L);
        sc.isInferCacheValid("k");
        assertEquals(java.util.concurrent.TimeUnit.DAYS.toMillis(3), sc.getInferCachePeriod().longValue());
    }

    @Test
    public void inferCacheIsIsolatedPerClusterId() {
        SavedCluster c1 = cluster("c1");
        SavedCluster c2 = cluster("c2");
        c1.setInferCacheValue("k", JsonObject.create().put("who", "c1"));
        assertNotNull(c1.getInferCacheValue("k"));
        assertNull(c2.getInferCacheValue("k"));
        assertFalse(c2.isInferCacheValid("k"));
    }

    @Test
    public void toStringIncludesIdAndName() {
        String s = cluster("c1").toString();
        assertTrue(s.contains("id='c1'"));
        assertTrue(s.contains("name='name-c1'"));
    }
}
