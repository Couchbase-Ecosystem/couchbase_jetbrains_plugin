package com.couchbase.intellij.workbench;

import com.intellij.openapi.progress.ProgressManager;
import java.util.concurrent.*;

/** Waits on a worker thread without spinning, while honoring both Cancel and task cancellation. */
final class QueryExecutionWait {
    private QueryExecutionWait() {}

    static <T> T await(CompletableFuture<T> future, BlockingQueue<Boolean> cancel) throws ExecutionException, InterruptedException {
        try {
            while (true) {
                ProgressManager.checkCanceled();
                if (cancel.poll() != null) throw new CancellationException();
                try {
                    return future.get(100, TimeUnit.MILLISECONDS);
                } catch (TimeoutException pending) {
                    // Sleeping on the future leaves the UI and CPU available while the server works.
                }
            }
        } catch (InterruptedException e) {
            future.cancel(true);
            Thread.currentThread().interrupt();
            throw e;
        } catch (RuntimeException e) {
            future.cancel(true);
            throw e;
        }
    }
}
