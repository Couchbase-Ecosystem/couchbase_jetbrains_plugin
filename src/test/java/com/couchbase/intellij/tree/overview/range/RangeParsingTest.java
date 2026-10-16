package com.couchbase.intellij.tree.overview.range;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Parses the /pools/default/stats/range response shape the same way CouchbaseRestAPI.getCollectionCounts does.
 */
public class RangeParsingTest {

    private static final Gson GSON = new GsonBuilder().create();
    private static final Type RANGE_LIST = new TypeToken<List<Range>>() {
    }.getType();

    private static final String RESPONSE = """
            [
              {
                "startTimestamp": 1700000000,
                "endTimestamp": 1700000360,
                "data": [
                  {
                    "metric": {
                      "name": "kv_collection_item_count",
                      "bucket": "travel-sample",
                      "scope": "inventory",
                      "collection": "airport",
                      "collection_id": "8",
                      "scope_id": "0"
                    },
                    "values": [[1700000000, 1500], [1700000003, 1502]]
                  },
                  {
                    "metric": {"name": "kv_ops", "bucket": "travel-sample", "nodes": ["10.0.0.5:8091"]},
                    "values": []
                  }
                ]
              }
            ]
            """;

    @Test
    public void parsesRangeEnvelopeAndMetrics() {
        List<Range> ranges = GSON.fromJson(RESPONSE, RANGE_LIST);

        Assert.assertEquals(1, ranges.size());
        Range range = ranges.get(0);
        Assert.assertEquals(1700000000L, range.getStartTimestamp());
        Assert.assertEquals(1700000360L, range.getEndTimestamp());
        Assert.assertEquals(2, range.getData().size());

        RangeMetric metric = range.getData().get(0).getMetric();
        Assert.assertEquals("kv_collection_item_count", metric.getName());
        Assert.assertEquals("travel-sample", metric.getBucket());
        Assert.assertEquals("inventory", metric.getScope());
        Assert.assertEquals("airport", metric.getCollection());
        Assert.assertEquals("8", metric.getCollection_id());
        Assert.assertNull(metric.getNodes());
    }

    @Test
    public void numericSamplesArriveAsDoubles() {
        List<Range> ranges = GSON.fromJson(RESPONSE, RANGE_LIST);

        Object count = ranges.get(0).getData().get(0).getValues().get(0).get(1);

        Assert.assertEquals(Double.class, count.getClass());
        Assert.assertEquals(1500.0, (Double) count, 0.0);
        // NOTE: possible bug: getCollectionCounts() calls Integer.valueOf(value.toString()), which sees "1500.0"
        // and throws NumberFormatException for every sample Gson parsed as a Double.
        Assert.assertEquals("1500.0", count.toString());
        Assert.assertThrows(NumberFormatException.class, () -> Integer.valueOf(count.toString()));
    }

    @Test
    public void metricNodesListIsParsed() {
        List<Range> ranges = GSON.fromJson(RESPONSE, RANGE_LIST);

        RangeMetric metric = ranges.get(0).getData().get(1).getMetric();
        Assert.assertEquals(List.of("10.0.0.5:8091"), metric.getNodes());
        Assert.assertTrue(ranges.get(0).getData().get(1).getValues().isEmpty());
    }

    @Test
    public void missingDataYieldsNullList() {
        Range range = GSON.fromJson("{\"startTimestamp\":1,\"endTimestamp\":2}", Range.class);

        Assert.assertNull(range.getData());
        Assert.assertEquals(1L, range.getStartTimestamp());
    }

    @Test
    public void rangeDataWithoutMetricHasNullMetric() {
        RangeData data = GSON.fromJson("{\"values\":[[1,2]]}", RangeData.class);

        Assert.assertNull(data.getMetric());
        Assert.assertEquals(1, data.getValues().size());
    }
}
