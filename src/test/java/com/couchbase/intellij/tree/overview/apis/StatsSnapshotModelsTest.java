package com.couchbase.intellij.tree.overview.apis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Parsing characterization for the small per-node and per-bucket stat snapshots.
 */
public class StatsSnapshotModelsTest {

    private static final Gson GSON = new GsonBuilder().create();

    @Test
    public void systemStatsParsesRatesAndBigIntegerAllocstall() {
        SystemStats stats = GSON.fromJson(
                "{\"cpu_utilization_rate\":87.25,\"cpu_stolen_rate\":0.5,\"swap_total\":0,\"swap_used\":0," +
                        "\"mem_total\":8589934592,\"mem_free\":1073741824,\"mem_limit\":8000000000," +
                        "\"cpu_cores_available\":4,\"allocstall\":123456789012345678901234567890}",
                SystemStats.class);

        Assert.assertEquals(Double.valueOf(87.25), stats.getCpu_utilization_rate());
        Assert.assertEquals(Double.valueOf(0.5), stats.getCpu_stolen_rate());
        Assert.assertEquals(Long.valueOf(8589934592L), stats.getMem_total());
        Assert.assertEquals(Long.valueOf(4L), stats.getCpu_cores_available());
        Assert.assertEquals(new BigInteger("123456789012345678901234567890"), stats.getAllocstall());
    }

    @Test
    public void systemStatsMissingFieldsAreNull() {
        SystemStats stats = GSON.fromJson("{}", SystemStats.class);

        Assert.assertNull(stats.getCpu_utilization_rate());
        Assert.assertNull(stats.getMem_free());
        Assert.assertNull(stats.getAllocstall());
    }

    @Test
    public void systemStatsQuotedNumberIsCoercedToLong() {
        // Gson coerces a quoted numeric string into a Long field
        SystemStats stats = GSON.fromJson("{\"mem_total\":\"8589934592\"}", SystemStats.class);

        Assert.assertEquals(Long.valueOf(8589934592L), stats.getMem_total());
    }

    @Test
    public void systemStatsNonNumericStringIsRejected() {
        Assert.assertThrows(JsonSyntaxException.class,
                () -> GSON.fromJson("{\"mem_total\":\"lots\"}", SystemStats.class));
    }

    @Test
    public void interestingStatsParsesCountersAndDoubles() {
        InterestingStats stats = GSON.fromJson(
                "{\"cmd_get\":10.0,\"get_hits\":9.0,\"ops\":12.5,\"curr_items\":500,\"curr_items_tot\":1000," +
                        "\"vb_replica_curr_items\":500,\"mem_used\":2048,\"index_disk_size\":0," +
                        "\"couch_docs_actual_disk_size\":4096,\"ep_bg_fetched\":0}",
                InterestingStats.class);

        Assert.assertEquals(Double.valueOf(10.0), stats.getCmd_get());
        Assert.assertEquals(Double.valueOf(9.0), stats.getGet_hits());
        Assert.assertEquals(Double.valueOf(12.5), stats.getOps());
        Assert.assertEquals(Long.valueOf(500L), stats.getCurr_items());
        Assert.assertEquals(Long.valueOf(1000L), stats.getCurr_items_tot());
        Assert.assertEquals(Long.valueOf(0L), stats.getIndex_disk_size());
        Assert.assertEquals(Long.valueOf(4096L), stats.getCouch_docs_actual_disk_size());
    }

    @Test
    public void interestingStatsMissingFieldsAreNull() {
        InterestingStats stats = GSON.fromJson("{\"curr_items\":1}", InterestingStats.class);

        Assert.assertEquals(Long.valueOf(1L), stats.getCurr_items());
        Assert.assertNull(stats.getOps());
        Assert.assertNull(stats.getCouch_views_data_size());
        Assert.assertNull(stats.getVb_active_num_non_resident());
    }

    @Test
    public void basicBucketStatsParsesIntegerNonResidentCount() {
        BasicBucketStats stats = GSON.fromJson(
                "{\"quotaPercentUsed\":0.0,\"opsPerSec\":5,\"diskFetches\":3,\"itemCount\":10," +
                        "\"diskUsed\":1,\"dataUsed\":2,\"memUsed\":3,\"vbActiveNumNonResident\":7}",
                BasicBucketStats.class);

        Assert.assertEquals(Double.valueOf(0.0), stats.getQuotaPercentUsed());
        Assert.assertEquals(Double.valueOf(5.0), stats.getOpsPerSec());
        Assert.assertEquals(Long.valueOf(3L), stats.getDiskFetches());
        Assert.assertEquals(Long.valueOf(10L), stats.getItemCount());
        Assert.assertEquals(Integer.valueOf(7), stats.getVbActiveNumNonResident());
    }

    @Test
    public void basicBucketStatsNullsStayNull() {
        BasicBucketStats stats = GSON.fromJson("{\"itemCount\":null}", BasicBucketStats.class);

        Assert.assertNull(stats.getItemCount());
        Assert.assertNull(stats.getMemUsed());
        Assert.assertNull(stats.getVbActiveNumNonResident());
    }
}
