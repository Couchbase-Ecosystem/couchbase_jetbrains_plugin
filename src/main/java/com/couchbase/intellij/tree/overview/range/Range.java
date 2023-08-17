package com.couchbase.intellij.tree.overview.range;

import java.util.List;

public class Range {

    private List<RangeData> data;
    private long startTimestamp;
    private long endTimestamp;

    public List<RangeData> getData() {
        return data;
    }

    public void setData(List<RangeData> data) {
        this.data = data;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }
}
