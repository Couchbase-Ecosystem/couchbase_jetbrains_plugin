package com.couchbase.intellij.database;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.progress.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import com.couchbase.client.java.Cluster;
import java.util.Objects;
import java.util.function.Consumer;

/** Runs SDK work without holding the UI or a platform read/write lock. */
public final class ClusterUiTask {
    private ClusterUiTask() {}

    @FunctionalInterface public interface Work<T> { T run(Connection connection) throws Exception; }

    /** A request keeps its original SDK even when the shared ActiveCluster object is reconnected. */
    public static final class Connection {
        private final ActiveCluster active;
        private final Cluster sdk;
        private final String id;
        private Connection(ActiveCluster active) {
            this.active = active;
            sdk = active == null ? null : active.get();
            id = active == null ? null : active.getId();
        }
        public String id() { return id; }
        public ActiveCluster active() { requireCurrent(); return active; }
        public Cluster sdk() { requireCurrent(); return sdk; }
        public boolean isCurrent() {
            return active != null && sdk != null && ActiveCluster.INSTANCE.getValue() == active
                    && active.get() == sdk && Objects.equals(id, active.getId());
        }
        public void requireCurrent() {
            if (!isCurrent()) throw new IllegalStateException("The original connection is no longer active. Reconnect and try again.");
        }
        public void requireWritable() {
            requireCurrent();
            if (active.isReadOnlyMode()) throw new IllegalStateException("This connection is read-only.");
        }
    }

    public static Connection capture(ActiveCluster active) { return new Connection(active); }

    public static <T> void submit(Project project, ActiveCluster active, String title, Work<T> work,
                                  Consumer<T> success, Consumer<Exception> failure) {
        submit(project, capture(active), title, work, success, failure);
    }

    public static <T> void submit(Project project, Connection connection, String title, Work<T> work,
                                  Consumer<T> success, Consumer<Exception> failure) {
        ModalityState modality = ModalityState.current();
        ProgressManager.getInstance().run(new Task.Backgroundable(project, title, false) {
            @Override public void run(@NotNull ProgressIndicator indicator) {
                try {
                    connection.requireCurrent();
                    T result = work.run(connection);
                    ApplicationManager.getApplication().invokeLater(() -> {
                        if (project != null && project.isDisposed()) return;
                        if (!connection.isCurrent()) {
                            failure.accept(new IllegalStateException("The active connection changed. Select the original connection and try again."));
                        } else success.accept(result);
                    }, modality);
                } catch (Exception error) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        if (project == null || !project.isDisposed()) failure.accept(error);
                    }, modality);
                }
            }
        });
    }

}
