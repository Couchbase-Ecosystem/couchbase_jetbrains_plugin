package com.couchbase.intellij.tree.overview.apis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class IndexStatsTest {

    private static final Gson GSON = new GsonBuilder().create();

    @Test
    public void parsesIndexerStatsPayload() {
        String json = """
                {
                  "travel-sample:def_sourceairport": {
                    "indexer_state": "Active",
                    "memory_quota": 268435456,
                    "memory_used": 1024,
                    "data_size": 2048,
                    "disk_size": 4096,
                    "frag_percent": 12.5,
                    "resident_percent": 100.0,
                    "scan_bytes_read": 512,
                    "items_count": "1000",
                    "num_requests": "7",
                    "avg_scan_latency": "1.5ms",
                    "cache_hit_percent": "99"
                  }
                }
                """;

        Map<String, IndexStats> map = GSON.fromJson(json, new TypeToken<Map<String, IndexStats>>() {
        }.getType());

        IndexStats stats = map.get("travel-sample:def_sourceairport");
        Assert.assertNotNull(stats);
        Assert.assertEquals("Active", stats.getIndexer_state());
        Assert.assertEquals(Long.valueOf(268435456L), stats.getMemory_quota());
        Assert.assertEquals(Long.valueOf(1024L), stats.getMemory_used());
        Assert.assertEquals(Long.valueOf(2048L), stats.getData_size());
        Assert.assertEquals(Long.valueOf(4096L), stats.getDisk_size());
        Assert.assertEquals(Double.valueOf(12.5), stats.getFrag_percent());
        Assert.assertEquals(Double.valueOf(100.0), stats.getResident_percent());
        Assert.assertEquals(Long.valueOf(512L), stats.getScan_bytes_read());
        Assert.assertEquals("1000", stats.getItems_count());
        Assert.assertEquals("7", stats.getNum_requests());
        Assert.assertEquals("1.5ms", stats.getAvg_scan_latency());
        Assert.assertEquals("99", stats.getCache_hit_percent());
    }

    @Test
    public void missingFieldsStayNull() {
        IndexStats stats = GSON.fromJson("{\"indexer_state\":\"Paused\"}", IndexStats.class);

        Assert.assertEquals("Paused", stats.getIndexer_state());
        Assert.assertNull(stats.getMemory_quota());
        Assert.assertNull(stats.getFrag_percent());
        Assert.assertNull(stats.getItems_count());
        Assert.assertNull(stats.getDocid_count());
    }

    @Test
    public void nullJsonValuesStayNull() {
        IndexStats stats = GSON.fromJson("{\"data_size\":null,\"items_count\":null}", IndexStats.class);

        Assert.assertNull(stats.getData_size());
        Assert.assertNull(stats.getItems_count());
    }

    @Test
    public void numericJsonIntoStringFieldIsAcceptedAsText() {
        // Gson coerces JSON numbers into String fields, so "cache_hits": 5 becomes "5"
        IndexStats stats = GSON.fromJson("{\"cache_hits\":5,\"cache_misses\":0}", IndexStats.class);

        Assert.assertEquals("5", stats.getCache_hits());
        Assert.assertEquals("0", stats.getCache_misses());
    }

    @Test
    public void zeroAndNegativeValuesAreKept() {
        IndexStats stats = GSON.fromJson("{\"memory_used\":0,\"data_size\":-1,\"frag_percent\":0.0}", IndexStats.class);

        Assert.assertEquals(Long.valueOf(0L), stats.getMemory_used());
        Assert.assertEquals(Long.valueOf(-1L), stats.getData_size());
        Assert.assertEquals(Double.valueOf(0.0), stats.getFrag_percent());
    }

    @Test
    public void fractionalValueInLongFieldFailsToParse() {
        // NOTE: possible bug: a fractional byte count in a Long field aborts parsing of the whole stats map
        Assert.assertThrows(JsonSyntaxException.class,
                () -> GSON.fromJson("{\"memory_used\":1.5}", IndexStats.class));
    }

    @Test
    public void unknownFieldsAreIgnored() {
        IndexStats stats = GSON.fromJson("{\"brand_new_metric\":42,\"indexer_state\":\"Warmup\"}", IndexStats.class);

        Assert.assertEquals("Warmup", stats.getIndexer_state());
    }

    @Test
    public void toStringRendersSetAndUnsetFields() {
        IndexStats stats = new IndexStats();
        stats.setIndexer_state("Active");
        stats.setMemory_quota(10L);

        String text = stats.toString();

        Assert.assertTrue(text, text.startsWith("IndexStats{"));
        Assert.assertTrue(text, text.contains("indexer_state='Active'"));
        Assert.assertTrue(text, text.contains("memory_quota=10"));
        Assert.assertTrue(text, text.contains("data_size=null"));
    }
}
