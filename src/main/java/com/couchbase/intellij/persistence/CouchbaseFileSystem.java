package com.couchbase.intellij.persistence;

import com.couchbase.intellij.database.ActiveCluster;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileListener;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class CouchbaseFileSystem extends com.intellij.openapi.vfs.VirtualFileSystem {
    public static final CouchbaseFileSystem INSTANCE = new CouchbaseFileSystem();
    @Override
    public @NonNls @NotNull String getProtocol() {
        return "couchbase";
    }

    @Override
    public @Nullable VirtualFile findFileByPath(@NotNull @NonNls String path) {
        String[] parts = path.split("/");
        if (parts.length != 4) {
            return null;
        }

        return new CouchbaseDocumentVirtualFile(parts[0], parts[1], parts[2], parts[3]);
    }

    @Override
    public void refresh(boolean asynchronous) {

    }

    @Override
    public @Nullable VirtualFile refreshAndFindFileByPath(@NotNull String path) {
        return findFileByPath(path);
    }

    @Override
    public void addVirtualFileListener(@NotNull VirtualFileListener listener) {

    }

    @Override
    public void removeVirtualFileListener(@NotNull VirtualFileListener listener) {

    }

    @Override
    protected void deleteFile(Object requestor, @NotNull VirtualFile vFile) throws IOException {

    }

    @Override
    protected void moveFile(Object requestor, @NotNull VirtualFile vFile, @NotNull VirtualFile newParent) throws IOException {

    }

    @Override
    protected void renameFile(Object requestor, @NotNull VirtualFile vFile, @NotNull String newName) throws IOException {

    }

    @Override
    protected @NotNull VirtualFile createChildFile(Object requestor, @NotNull VirtualFile vDir, @NotNull String fileName) throws IOException {
        throw new IOException("Not supported");
    }

    @Override
    protected @NotNull VirtualFile createChildDirectory(Object requestor, @NotNull VirtualFile vDir, @NotNull String dirName) throws IOException {
        throw new IOException("Not supported");
    }

    @Override
    protected @NotNull VirtualFile copyFile(Object requestor, @NotNull VirtualFile virtualFile, @NotNull VirtualFile newParent, @NotNull String copyName) throws IOException {
        return null;
    }

    @Override
    public boolean isReadOnly() {
        return ActiveCluster.getInstance().isReadOnlyMode();
    }
}
