package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.ClusterUiTask;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicReference;

/** Cancellation remains possible until the UI accepts the start of the server mutation. */
final class DialogOperation {
    private enum State { PREPARING, WRITING, CANCELLED }
    private final AtomicReference<State> state = new AtomicReference<>(State.PREPARING);
    private final ModalityState modality = ModalityState.current();

    boolean cancel() {
        return state.compareAndSet(State.PREPARING, State.CANCELLED) || state.get() == State.CANCELLED;
    }

    void checkCancelled() {
        if (state.get() == State.CANCELLED) throw new CancellationException("Operation cancelled.");
    }

    void beginMutation(ClusterUiTask.Connection connection, Runnable disableCancel) {
        ApplicationManager.getApplication().invokeAndWait(() -> {
            connection.requireWritable();
            if (!state.compareAndSet(State.PREPARING, State.WRITING)) throw new CancellationException("Operation cancelled.");
            disableCancel.run();
        }, modality);
    }
}
