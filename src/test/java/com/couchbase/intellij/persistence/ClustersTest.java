package com.couchbase.intellij.persistence;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ClustersTest {

    @Test
    public void collectionsAreLazilyInitialized() {
        Clusters c = new Clusters();
        assertNotNull(c.getMap());
        assertTrue(c.getMap().isEmpty());
        assertNotNull(c.getInferCache());
        assertTrue(c.getInferCache().isEmpty());
        assertNotNull(c.getInferCacheUpdateTimes());
        assertTrue(c.getInferCacheUpdateTimes().isEmpty());
    }

    @Test
    public void settersOverrideLazyDefaults() {
        Clusters c = new Clusters();
        Map<String, SavedCluster> map = new HashMap<>();
        c.setMap(map);
        assertSame(map, c.getMap());

        Map<String, Map<String, String>> cache = new HashMap<>();
        c.setInferCache(cache);
        assertSame(cache, c.getInferCache());

        Map<String, Map<String, Long>> times = new HashMap<>();
        c.setInferCacheUpdateTimes(times);
        assertSame(times, c.getInferCacheUpdateTimes());
    }

    @Test
    public void setMapNullIsRecoveredByGetter() {
        Clusters c = new Clusters();
        c.setMap(null);
        assertNotNull(c.getMap());
    }

    @Test
    public void optionsToStringIsLowercaseName() {
        assertEquals("live_polling", Clusters.Options.LIVE_POLLING.toString());
        assertEquals("live_reload", Clusters.Options.LIVE_RELOAD.toString());
    }

    @Test
    public void defaultsContainOnlyLivePolling() {
        assertEquals(java.util.Set.of("live_polling"), Clusters.Options.defaults());
    }
}
