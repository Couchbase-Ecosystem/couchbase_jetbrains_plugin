package com.couchbase.intellij.tree.overview.apis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Assert;
import org.junit.Test;

public class ServerOverviewTest {

    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    private static final String CLUSTER_JSON = """
            {
              "name": "Cluster-A",
              "nodes": [
                {"hostname": "10.0.0.5:8091", "thisNode": true, "services": ["kv"], "status": "healthy"},
                {"hostname": "10.0.0.6:8091", "thisNode": false, "services": ["kv", "index"], "status": "unhealthy"}
              ],
              "bucketNames": [{"bucketName": "travel-sample", "uuid": "f1e2d3"}],
              "rebalanceStatus": "none",
              "autoCompactionSettings": {
                "parallelDBAndViewCompaction": false,
                "magmaFragmentationPercentage": 50,
                "databaseFragmentationThreshold": {"percentage": 30, "size": "auto"},
                "viewFragmentationThreshold": {"percentage": 30, "size": "auto"},
                "indexCompactionMode": "circular",
                "indexFragmentationThreshold": {"percentage": 30, "size": "auto"}
              },
              "balanced": true,
              "memoryQuota": 4096,
              "indexMemoryQuota": 512,
              "ftsMemoryQuota": 256,
              "cbasMemoryQuota": 0,
              "eventingMemoryQuota": 256,
              "storageTotals": {
                "ram": {"total": 17179869184, "quotaTotal": 12884901888, "quotaUsed": 4294967296, "used": 9000000000, "usedByData": 3000000000, "quotaUsedPerNode": 4294967296, "quotaTotalPerNode": 12884901888},
                "hdd": {"total": 500000000000, "quotaTotal": 400000000000, "used": 100000000000, "usedByData": 50000000000, "free": 400000000000}
              }
            }
            """;

    @Test
    public void parsesNodesAndBucketNames() {
        ServerOverview overview = GSON.fromJson(CLUSTER_JSON, ServerOverview.class);

        Assert.assertEquals("Cluster-A", overview.getName());
        Assert.assertEquals(2, overview.getNodes().size());

        CBNode first = overview.getNodes().get(0);
        Assert.assertEquals("10.0.0.5:8091", first.getHostname());
        Assert.assertTrue(first.isThisNode());

        CBNode second = overview.getNodes().get(1);
        Assert.assertFalse(second.isThisNode());
        Assert.assertEquals("unhealthy", second.getStatus());
        Assert.assertEquals(java.util.List.of("kv", "index"), second.getServices());

        Assert.assertEquals("travel-sample", overview.getBucketNames().get(0).getBucketName());
        Assert.assertEquals("f1e2d3", overview.getBucketNames().get(0).getUuid());
    }

    @Test
    public void parsesQuotasAndCompactionSettings() {
        ServerOverview overview = GSON.fromJson(CLUSTER_JSON, ServerOverview.class);

        Assert.assertEquals(Boolean.TRUE, overview.getBalanced());
        Assert.assertEquals(Long.valueOf(4096L), overview.getMemoryQuota());
        Assert.assertEquals(Long.valueOf(0L), overview.getCbasMemoryQuota());

        AutoCompactionSettings compaction = overview.getAutoCompactionSettings();
        Assert.assertEquals(Boolean.FALSE, compaction.getParallelDBAndViewCompaction());
        Assert.assertEquals(Long.valueOf(50L), compaction.getMagmaFragmentationPercentage());
        Assert.assertEquals("circular", compaction.getIndexCompactionMode());
        Assert.assertEquals(Integer.valueOf(30), compaction.getDatabaseFragmentationThreshold().getPercentage());
        Assert.assertEquals("auto", compaction.getViewFragmentationThreshold().getSize());
    }

    @Test
    public void parsesStorageTotalsRamAndHdd() {
        ServerOverview overview = GSON.fromJson(CLUSTER_JSON, ServerOverview.class);

        RAM ram = overview.getStorageTotals().getRam();
        Assert.assertEquals(Long.valueOf(17179869184L), ram.getTotal());
        Assert.assertEquals(Long.valueOf(4294967296L), ram.getQuotaUsed());
        Assert.assertEquals(Long.valueOf(3000000000L), ram.getUsedByData());

        HDD hdd = overview.getStorageTotals().getHdd();
        Assert.assertEquals(Long.valueOf(500000000000L), hdd.getTotal());
        Assert.assertEquals(Long.valueOf(400000000000L), hdd.getFree());
    }

    @Test
    public void emptyObjectHasNoNodesOrQuotas() {
        ServerOverview overview = GSON.fromJson("{}", ServerOverview.class);

        Assert.assertNull(overview.getNodes());
        Assert.assertNull(overview.getBucketNames());
        Assert.assertNull(overview.getBalanced());
        Assert.assertNull(overview.getAutoCompactionSettings());
        Assert.assertNull(overview.getStorageTotals());
        Assert.assertNull(overview.getIndexMemoryQuota());
    }

    @Test
    public void emptyNodeListIsKeptAsEmpty() {
        ServerOverview overview = GSON.fromJson("{\"nodes\":[]}", ServerOverview.class);

        Assert.assertTrue(overview.getNodes().isEmpty());
    }

    @Test
    public void storageTotalsWithOnlyHddLeavesRamNull() {
        StorageTotals totals = GSON.fromJson("{\"hdd\":{\"free\":10}}", StorageTotals.class);

        Assert.assertNull(totals.getRam());
        Assert.assertEquals(Long.valueOf(10L), totals.getHdd().getFree());
        Assert.assertNull(totals.getHdd().getTotal());
    }
}
