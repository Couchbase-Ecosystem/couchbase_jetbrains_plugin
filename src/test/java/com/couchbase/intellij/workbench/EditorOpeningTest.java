package com.couchbase.intellij.workbench;

import com.intellij.openapi.vfs.VirtualFile;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class EditorOpeningTest {
    public static final class SynchronousApi {
        VirtualFile opened;
        public void requestOpenFile(VirtualFile file) { opened = file; }
    }

    public static final class AsynchronousApi {
        final CompletableFuture<Void> pending = new CompletableFuture<>();
        VirtualFile opened;
        public CompletableFuture<Void> requestOpenFile(VirtualFile file) {
            opened = file;
            return pending;
        }
    }

    public static final class FailingApi {
        final IllegalStateException error = new IllegalStateException("Could not open editor");
        public void requestOpenFile(VirtualFile file) { throw error; }
    }

    @Test public void supportsTheVoidReturnTypeInOlderIdes() {
        SynchronousApi manager = new SynchronousApi();
        VirtualFile file = mock(VirtualFile.class);
        assertTrue(EditorOpening.invokeRequest(SynchronousApi.class, manager, file).toCompletableFuture().isDone());
        assertSame(file, manager.opened);
    }

    @Test public void waitsForTheFutureBeforeNotifyingTheCaller() {
        AsynchronousApi manager = new AsynchronousApi();
        VirtualFile file = mock(VirtualFile.class);
        AtomicInteger notifications = new AtomicInteger();
        EditorOpening.invokeRequest(AsynchronousApi.class, manager, file).thenRun(notifications::incrementAndGet);
        assertSame(file, manager.opened);
        assertEquals(0, notifications.get());

        manager.pending.complete(null);
        assertEquals(1, notifications.get());
    }

    @Test public void keepsAsynchronousOpenFailures() {
        AsynchronousApi manager = new AsynchronousApi();
        var request = EditorOpening.invokeRequest(AsynchronousApi.class, manager, mock(VirtualFile.class));
        IllegalStateException error = new IllegalStateException("Could not open editor");
        manager.pending.completeExceptionally(error);

        assertSame(error, assertThrows(CompletionException.class, () -> request.toCompletableFuture().join()).getCause());
    }

    @Test public void preservesSynchronousOpenFailuresWithoutReflectionWrappers() {
        FailingApi manager = new FailingApi();
        assertSame(manager.error, assertThrows(IllegalStateException.class,
                () -> EditorOpening.invokeRequest(FailingApi.class, manager, mock(VirtualFile.class))));
    }
}
