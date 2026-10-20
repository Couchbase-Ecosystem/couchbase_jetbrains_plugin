package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.persistence.storage.RelationshipStorage;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import java.util.Map;

public class RelationshipSettingsManagerTest extends BasePlatformTestCase {

    static {
        com.couchbase.intellij.testutil.AllowedThreads.allowJdkAwtThreads();
    }

    private ActiveCluster previousCluster;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        previousCluster = ActiveCluster.getInstance();
        ActiveCluster.setInstance(new ActiveCluster() {
        });
        RelationshipStorage.getInstance().loadState(new RelationshipStorage.State());
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            RelationshipStorage.getInstance().loadState(new RelationshipStorage.State());
            ActiveCluster.setInstance(previousCluster);
        } finally {
            super.tearDown();
        }
    }

    public void testPopulateTravelSampleAddsFourRelationships() {
        RelationshipSettingsManager.populateTravelSample();

        Map<String, String> map = currentRelationships();

        assertEquals(4, map.size());
        assertEquals("travel-sample.inventory.airline.meta().id",
                map.get("travel-sample.inventory.route.airlineid"));
        assertEquals("travel-sample.inventory.airline.iata",
                map.get("travel-sample.inventory.route.airline"));
        assertEquals("travel-sample.inventory.airport.faa",
                map.get("travel-sample.inventory.route.sourceairport"));
        assertEquals("travel-sample.inventory.airport.faa",
                map.get("travel-sample.inventory.route.destinationairport"));
    }

    public void testPopulateTravelSampleIsIdempotent() {
        RelationshipSettingsManager.populateTravelSample();
        RelationshipSettingsManager.populateTravelSample();

        assertEquals(4, currentRelationships().size());
    }

    public void testPopulateTravelSampleKeepsExistingMappings() {
        currentRelationships().put("custom.a.b.c", "custom.x.y.z");

        RelationshipSettingsManager.populateTravelSample();

        assertEquals("custom.x.y.z", currentRelationships().get("custom.a.b.c"));
        assertEquals(5, currentRelationships().size());
    }

    /** Relationships are keyed by the active connection id, which is null for the bare test cluster. */
    private Map<String, String> currentRelationships() {
        return RelationshipStorage.getInstance().getValue().getRelationships()
                .computeIfAbsent(ActiveCluster.getInstance().getId(), k -> new java.util.HashMap<>());
    }
}
