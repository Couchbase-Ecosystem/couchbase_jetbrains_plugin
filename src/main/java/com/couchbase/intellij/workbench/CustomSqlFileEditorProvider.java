package com.couchbase.intellij.workbench;

import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class CustomSqlFileEditorProvider implements FileEditorProvider, DumbAware {

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        // Only accept virtual files with a specific file type, name, extension, etc.
        return "sqlpp".equals(file.getExtension());
    }

    @NotNull
    @Override
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        // Here, you can create an instance of your custom file editor
        // You might want to pass the project and the file to your custom file editor's constructor
        return new CustomSqlFileEditor(project, file);
    }

    @NotNull
    @Override
    public String getEditorTypeId() {
        return "custom-sql-editor"; // Unique ID for your editor type
    }

    @NotNull
    @Override
    public FileEditorPolicy getPolicy() {
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR; // Or another policy depending on your needs
    }
}
