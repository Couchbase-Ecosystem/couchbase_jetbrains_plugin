package com.couchbase.intellij.workbench;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

/** Opens registered editors using the public API shared by the supported platforms. */
public final class EditorOpening {
    private EditorOpening() {}

    public static void openFile(Project project, VirtualFile file) {
        openFile(project, file, editors -> {});
    }

    public static void openFile(Project project, VirtualFile file, Consumer<List<FileEditor>> onOpened) {
        FileEditorManager manager = FileEditorManager.getInstance(project);
        invokeRequest(FileEditorManager.class, manager, file).whenComplete((ignored, error) -> {
            if (error != null) {
                Log.error("Could not open Couchbase editor", error);
                return;
            }
            Runnable complete = () -> {
                if (!project.isDisposed()) onOpened.accept(manager.getEditorList(file));
            };
            var application = ApplicationManager.getApplication();
            if (application.isDispatchThread()) complete.run();
            else application.invokeLater(complete, project.getDisposed());
        });
    }

    static CompletionStage<?> invokeRequest(Class<?> api, Object manager, VirtualFile file) {
        try {
            // requestOpenFile returns void in 252 and CompletableFuture in 263.
            // Reflection avoids binding to either incompatible JVM return descriptor.
            Object request = api.getMethod("requestOpenFile", VirtualFile.class).invoke(manager, file);
            return request instanceof CompletionStage<?> pending ? pending : CompletableFuture.completedFuture(null);
        } catch (InvocationTargetException error) {
            Throwable cause = error.getCause();
            if (cause instanceof RuntimeException runtime) throw runtime;
            if (cause instanceof Error fatal) throw fatal;
            throw new IllegalStateException("Could not open Couchbase editor", cause);
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("The IDE does not provide requestOpenFile", error);
        }
    }
}
