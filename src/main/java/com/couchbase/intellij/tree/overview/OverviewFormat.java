package com.couchbase.intellij.tree.overview;

import com.couchbase.intellij.tree.overview.apis.ServerOverview;
import org.jetbrains.annotations.Nullable;

/** Number formatting for the cluster overview; values the server did not report show as a dash. */
public final class OverviewFormat {
    public static final String NOT_REPORTED = "-";

    private OverviewFormat() {}

    /** A memory quota in megabytes; null when the cluster has no such service (Community Edition, for example). */
    public static String mbToGb(@Nullable Long sizeInMb) {
        if (sizeInMb == null) {
            return NOT_REPORTED;
        }
        if (sizeInMb < 1024) {
            return sizeInMb + " Mb";
        }
        return String.format("%.2f Gb", sizeInMb / 1024.0);
    }

    /** Data, Index, Search, Eventing and Analytics quotas, in the order the overview shows them. */
    public static String[] quotaValues(ServerOverview overview) {
        return new String[]{
                mbToGb(overview.getMemoryQuota()), mbToGb(overview.getIndexMemoryQuota()),
                mbToGb(overview.getFtsMemoryQuota()), mbToGb(overview.getEventingMemoryQuota()),
                mbToGb(overview.getCbasMemoryQuota())};
    }
}
