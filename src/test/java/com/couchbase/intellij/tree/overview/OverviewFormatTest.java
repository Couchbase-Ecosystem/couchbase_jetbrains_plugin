package com.couchbase.intellij.tree.overview;

import com.couchbase.intellij.tree.overview.apis.ServerOverview;
import com.google.gson.Gson;
import org.junit.Test;

import static org.junit.Assert.*;

public class OverviewFormatTest {

    /** What a Community Edition cluster returns: no eventing or analytics service, so no quota for either. */
    private static final String COMMUNITY_EDITION = """
            {
              "nodes": [{"hostname": "10.0.0.5:8091", "services": ["kv", "index", "n1ql"], "status": "healthy"}],
              "memoryQuota": 2048,
              "indexMemoryQuota": 512,
              "ftsMemoryQuota": 256
            }
            """;

    @Test
    public void quotasTheServerDidNotReportShowAsADashInsteadOfCrashing() {
        ServerOverview overview = new Gson().fromJson(COMMUNITY_EDITION, ServerOverview.class);
        assertNull(overview.getEventingMemoryQuota());
        assertNull(overview.getCbasMemoryQuota());

        assertArrayEquals(new String[]{"2.00 Gb", "512 Mb", "256 Mb", "-", "-"}, OverviewFormat.quotaValues(overview));
    }

    @Test
    public void quotasAreShownInMegabytesBelowOneGigabyteAndGigabytesAbove() {
        assertEquals("-", OverviewFormat.mbToGb(null));
        assertEquals("0 Mb", OverviewFormat.mbToGb(0L));
        assertEquals("1023 Mb", OverviewFormat.mbToGb(1023L));
        assertEquals("1.00 Gb", OverviewFormat.mbToGb(1024L));
        assertEquals("4.50 Gb", OverviewFormat.mbToGb(4608L));
        assertEquals("-", ServerOverviewDialog.mbToGb(null));
    }
}
