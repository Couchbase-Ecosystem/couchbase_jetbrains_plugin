package com.couchbase.intellij.workbench;

import org.junit.Test;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.Assert.*;

public class QueryExecutionWaitTest {
    @Test
    public void returnsCompletedResultAndPreservesFailureCause() throws Exception {
        assertEquals("rows", QueryExecutionWait.await(CompletableFuture.completedFuture("rows"), new LinkedBlockingQueue<>()));
        IllegalStateException failure = new IllegalStateException("query failed");
        try {
            QueryExecutionWait.await(CompletableFuture.failedFuture(failure), new LinkedBlockingQueue<>());
            fail("Expected query failure");
        } catch (ExecutionException e) { assertSame(failure, e.getCause()); }
    }

    @Test
    public void cancelStopsAPendingRequestAndCancelsTheSdkFuture() throws Exception {
        CompletableFuture<String> request = new CompletableFuture<>();
        BlockingQueue<Boolean> cancel = new LinkedBlockingQueue<>();
        ExecutorService worker = Executors.newSingleThreadExecutor();
        try {
            Future<?> work = worker.submit(() -> {
                assertThrows(CancellationException.class, () -> QueryExecutionWait.await(request, cancel));
            });
            cancel.offer(true);
            work.get(2, TimeUnit.SECONDS);
            assertTrue(request.isCancelled());
        } finally { worker.shutdownNow(); assertTrue(worker.awaitTermination(2, TimeUnit.SECONDS)); }
    }

    @Test
    public void interruptionCancelsTheRequestAndPreservesTheInterruptFlag() throws Exception {
        CompletableFuture<String> request = new CompletableFuture<>();
        AtomicBoolean interrupted = new AtomicBoolean();
        Thread worker = new Thread(() -> {
            try {
                Thread.currentThread().interrupt();
                QueryExecutionWait.await(request, new LinkedBlockingQueue<>());
                fail("Expected interruption");
            } catch (InterruptedException e) { interrupted.set(Thread.currentThread().isInterrupted()); }
            catch (ExecutionException e) { throw new AssertionError(e); }
        });
        worker.start(); worker.join(2000);
        assertFalse(worker.isAlive());
        assertTrue(interrupted.get());
        assertTrue(request.isCancelled());
    }
}
