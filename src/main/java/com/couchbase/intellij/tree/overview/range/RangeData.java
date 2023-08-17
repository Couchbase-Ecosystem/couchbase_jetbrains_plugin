package com.couchbase.intellij.tree.overview.range;

import java.util.List;

public class RangeData {

    private RangeMetric metric;
    private List<List<Object>> values;

    public RangeMetric getMetric() {
        return metric;
    }

    public void setMetric(RangeMetric metric) {
        this.metric = metric;
    }

    public List<List<Object>> getValues() {
        return values;
    }

    public void setValues(List<List<Object>> values) {
        this.values = values;
    }
}
