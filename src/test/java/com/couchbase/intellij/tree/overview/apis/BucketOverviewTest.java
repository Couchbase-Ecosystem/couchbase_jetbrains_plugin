package com.couchbase.intellij.tree.overview.apis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class BucketOverviewTest {

    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    private static final String BUCKET_JSON = """
            {
              "name": "travel-sample",
              "nodeLocator": "vbucket",
              "bucketType": "membase",
              "storageBackend": "couchstore",
              "uuid": "f1e2d3",
              "uri": "/pools/default/buckets/travel-sample",
              "bucketCapabilitiesVer": "dcp",
              "bucketCapabilities": ["xattr", "dcp", "couchapi"],
              "autoCompactionSettings": true,
              "replicaIndex": true,
              "replicaNumber": 1,
              "threadsNumber": 3,
              "evictionPolicy": "valueOnly",
              "durabilityMinLevel": "none",
              "pitrEnabled": false,
              "pitrGranularity": 600,
              "pitrMaxHistoryAge": 86400,
              "conflictResolutionType": "seqno",
              "maxTTL": 0,
              "compressionMode": "passive",
              "quota": {"ram": 104857600, "rawRAM": 104857600},
              "basicStats": {
                "quotaPercentUsed": 45.5,
                "opsPerSec": 0.0,
                "diskFetches": 0,
                "itemCount": 31591,
                "diskUsed": 2000000,
                "dataUsed": 1500000,
                "memUsed": 47000000,
                "vbActiveNumNonResident": 0
              }
            }
            """;

    @Test
    public void parsesBucketSettingsAndCapabilities() {
        BucketOverview bucket = GSON.fromJson(BUCKET_JSON, BucketOverview.class);

        Assert.assertEquals("travel-sample", bucket.getName());
        Assert.assertEquals("membase", bucket.getBucketType());
        Assert.assertEquals("couchstore", bucket.getStorageBackend());
        Assert.assertEquals(List.of("xattr", "dcp", "couchapi"), bucket.getBucketCapabilities());
        Assert.assertEquals(Boolean.TRUE, bucket.getReplicaIndex());
        Assert.assertEquals(Integer.valueOf(1), bucket.getReplicaNumber());
        Assert.assertEquals(Boolean.FALSE, bucket.getPitrEnabled());
        Assert.assertEquals(Long.valueOf(600L), bucket.getPitrGranularity());
        Assert.assertEquals(Long.valueOf(0L), bucket.getMaxTTL());
    }

    @Test
    public void parsesQuotaAndBasicStats() {
        BucketOverview bucket = GSON.fromJson(BUCKET_JSON, BucketOverview.class);

        Assert.assertEquals(Long.valueOf(104857600L), bucket.getQuota().getRam());
        Assert.assertEquals(Long.valueOf(104857600L), bucket.getQuota().getRawRAM());

        BasicBucketStats stats = bucket.getBasicStats();
        Assert.assertEquals(Double.valueOf(45.5), stats.getQuotaPercentUsed());
        Assert.assertEquals(Double.valueOf(0.0), stats.getOpsPerSec());
        Assert.assertEquals(Long.valueOf(31591L), stats.getItemCount());
        Assert.assertEquals(Integer.valueOf(0), stats.getVbActiveNumNonResident());
    }

    @Test
    public void missingObjectsAndBooleansStayNull() {
        BucketOverview bucket = GSON.fromJson("{\"name\":\"empty\"}", BucketOverview.class);

        Assert.assertEquals("empty", bucket.getName());
        Assert.assertNull(bucket.getQuota());
        Assert.assertNull(bucket.getBasicStats());
        Assert.assertNull(bucket.getBucketCapabilities());
        Assert.assertNull(bucket.getReplicaIndex());
        Assert.assertNull(bucket.getPitrEnabled());
        Assert.assertNull(bucket.getMaxTTL());
    }

    @Test
    public void explicitNullQuotaIsNull() {
        BucketOverview bucket = GSON.fromJson("{\"name\":\"b\",\"quota\":null,\"basicStats\":null}", BucketOverview.class);

        Assert.assertNull(bucket.getQuota());
        Assert.assertNull(bucket.getBasicStats());
    }

    @Test
    public void zeroItemCountIsKept() {
        BucketOverview bucket = GSON.fromJson("{\"basicStats\":{\"itemCount\":0,\"quotaPercentUsed\":0}}", BucketOverview.class);

        Assert.assertEquals(Long.valueOf(0L), bucket.getBasicStats().getItemCount());
        Assert.assertEquals(Double.valueOf(0.0), bucket.getBasicStats().getQuotaPercentUsed());
    }
}
