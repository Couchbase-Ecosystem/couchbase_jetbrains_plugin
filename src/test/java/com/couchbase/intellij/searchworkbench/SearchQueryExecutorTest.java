package com.couchbase.intellij.searchworkbench;

import com.couchbase.client.java.json.JsonObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.*;

public class SearchQueryExecutorTest {

    /** Records everything the executor reports. */
    private static final class RecordingOutput implements SearchQueryExecutor.Output {
        final List<String> events = new ArrayList<>();
        List<String> metrics;
        List<JsonObject> hits;
        String errorMessage;

        @Override public void loading() { events.add("loading"); }
        @Override public void cancelled() { events.add("cancelled"); }
        @Override public void results(List<String> metrics, List<JsonObject> hits) { events.add("results"); this.metrics = metrics; this.hits = hits; }
        @Override public void error(String elapsed, String message) { events.add("error"); errorMessage = message; }
    }

    /** Records the request and answers with whatever future the test hands it. */
    private static final class RecordingFts implements SearchQueryExecutor.FtsCall {
        final CompletableFuture<String> answer;
        boolean scoped;
        String bucket, scope, index, query;
        int calls;

        RecordingFts(CompletableFuture<String> answer) { this.answer = answer; }

        @Override
        public CompletableFuture<String> run(boolean scoped, String bucket, String scope, String index, String query) {
            calls++;
            this.scoped = scoped; this.bucket = bucket; this.scope = scope; this.index = index; this.query = query;
            return answer;
        }
    }

    private static final String RESPONSE = "{\"took\": 2500000, \"total_hits\": 2, \"hits\": [{\"id\": \"a\"}, {\"id\": \"b\"}]}";

    @Test
    public void aDefaultSizeIsAddedOnlyWhenTheQuerySetsNoSizeOrLimit() {
        // JsonObject does not keep key order, so compare parsed values.
        assertEquals(JsonObject.fromJson("{\"query\":{\"match\":\"x\"},\"size\":200}"),
                JsonObject.fromJson(SearchQueryExecutor.withDefaultSize("{\"query\":{\"match\":\"x\"}}", 200)));
        assertEquals("{\"size\":5}", SearchQueryExecutor.withDefaultSize("{\"size\":5}", 200));
        assertEquals("{\"limit\":5}", SearchQueryExecutor.withDefaultSize("{\"limit\":5}", 200));
        assertEquals("no limit must not send size: null", "{\"query\":{}}", SearchQueryExecutor.withDefaultSize("{\"query\":{}}", null));
    }

    @Test
    public void resultsCarryTookInMillisecondsAndEveryHit() {
        RecordingFts fts = new RecordingFts(CompletableFuture.completedFuture(RESPONSE));
        RecordingOutput out = new RecordingOutput();

        assertTrue(SearchQueryExecutor.run(new LinkedBlockingQueue<>(1), "travel-sample", "hotels", "{\"query\":{\"match\":\"x\"}}", 10, fts, out));

        assertEquals(List.of("loading", "results"), out.events);
        assertFalse(fts.scoped);
        assertEquals("_default", fts.scope);
        assertEquals("hotels", fts.index);
        assertEquals(JsonObject.fromJson("{\"query\":{\"match\":\"x\"},\"size\":10}"), JsonObject.fromJson(fts.query));
        assertEquals("2 MS", out.metrics.get(1));
        assertEquals("2", out.metrics.get(4));
        assertEquals(2, out.hits.size());
        assertEquals("b", out.hits.get(1).getString("id"));
    }

    @Test
    public void scopedIndexNamesAreSplitIntoScopeAndIndex() {
        RecordingFts fts = new RecordingFts(CompletableFuture.completedFuture(RESPONSE));
        SearchQueryExecutor.run(new LinkedBlockingQueue<>(1), "travel-sample", "travel-sample.inventory.hotels", "{}", null, fts, new RecordingOutput());
        assertTrue(fts.scoped);
        assertEquals("inventory", fts.scope);
        assertEquals("hotels", fts.index);
    }

    @Test
    public void invalidJsonIsReportedWithoutCallingTheServer() {
        RecordingFts fts = new RecordingFts(CompletableFuture.completedFuture(RESPONSE));
        RecordingOutput out = new RecordingOutput();
        assertFalse(SearchQueryExecutor.run(new LinkedBlockingQueue<>(1), "b", "i", "{not json", 10, fts, out));
        assertEquals(List.of("error"), out.events);
        assertEquals(SearchQueryExecutor.INVALID_JSON, out.errorMessage);
        assertEquals(0, fts.calls);
    }

    @Test
    public void serverErrorsAreShownAsErrors() {
        RecordingFts fts = new RecordingFts(CompletableFuture.completedFuture("{\"error\": \"index not found\"}"));
        RecordingOutput out = new RecordingOutput();
        assertTrue(SearchQueryExecutor.run(new LinkedBlockingQueue<>(1), "b", "i", "{}", 10, fts, out));
        assertEquals(List.of("loading", "error"), out.events);
        assertEquals("index not found", out.errorMessage);
    }

    @Test
    public void cancellingWhileTheServerIsSlowStopsTheRequest() throws Exception {
        CompletableFuture<String> never = new CompletableFuture<>();
        RecordingFts fts = new RecordingFts(never);
        RecordingOutput out = new RecordingOutput();
        BlockingQueue<Boolean> cancel = new LinkedBlockingQueue<>(1);
        ExecutorService worker = Executors.newSingleThreadExecutor();
        try {
            Future<Boolean> outcome = worker.submit(() -> SearchQueryExecutor.run(cancel, "b", "i", "{}", 10, fts, out));
            Thread.sleep(150);
            assertEquals("still waiting on the server", List.of("loading"), out.events);
            cancel.put(true);
            assertFalse(outcome.get(5, TimeUnit.SECONDS));
        } finally {
            worker.shutdownNow();
        }
        assertEquals(List.of("loading", "cancelled"), out.events);
        assertTrue(never.isCancelled());
    }

    @Test
    public void aFailedRequestIsReportedNotThrown() {
        CompletableFuture<String> failed = new CompletableFuture<>();
        failed.completeExceptionally(new RuntimeException("connection refused"));
        RecordingOutput out = new RecordingOutput();
        assertTrue(SearchQueryExecutor.run(new LinkedBlockingQueue<>(1), "b", "i", "{}", 10, new RecordingFts(failed), out));
        assertEquals(List.of("loading", "error"), out.events);
        assertTrue(out.errorMessage, out.errorMessage.contains("connection refused"));
    }
}
