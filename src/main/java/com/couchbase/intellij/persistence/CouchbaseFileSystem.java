package com.couchbase.intellij.persistence;

import com.couchbase.intellij.database.ActiveCluster;
import com.intellij.openapi.util.io.FileAttributes;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.newvfs.NewVirtualFileSystem;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CouchbaseFileSystem extends NewVirtualFileSystem {
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
    public int getRank() {
        return 0;
    }

    @Override
    public @NotNull VirtualFile copyFile(Object requestor, @NotNull VirtualFile file, @NotNull VirtualFile newParent, @NotNull String copyName) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte @NotNull [] contentsToByteArray(@NotNull VirtualFile file) throws IOException {
        if (file instanceof CouchbaseDocumentVirtualFile) {
            return file.contentsToByteArray();
        }
        return new byte[0];
    }

    @Override
    public @NotNull InputStream getInputStream(@NotNull VirtualFile file) throws IOException {
        return file.getInputStream();
    }

    @Override
    public @NotNull OutputStream getOutputStream(@NotNull VirtualFile file, Object requestor, long modStamp, long timeStamp) throws IOException {
        return file.getOutputStream(requestor, modStamp, timeStamp);
    }

    @Override
    public long getLength(@NotNull VirtualFile file) {
        return file.getLength();
    }

    @Override
    public boolean exists(@NotNull VirtualFile file) {
        return file instanceof CouchbaseDocumentVirtualFile;
    }

    @Override
    public String @NotNull [] list(@NotNull VirtualFile file) {
        return new String[0];
    }

    @Override
    public boolean isDirectory(@NotNull VirtualFile file) {
        return false;
    }

    @Override
    public long getTimeStamp(@NotNull VirtualFile file) {
        if (file instanceof CouchbaseDocumentVirtualFile) {
            return file.getTimeStamp();
        }
        return 0;
    }

    @Override
    public void setTimeStamp(@NotNull VirtualFile file, long timeStamp) throws IOException {
        if (file instanceof CouchbaseDocumentVirtualFile) {
            ((CouchbaseDocumentVirtualFile) file).setTimeStamp(timeStamp);
        }
    }

    @Override
    public boolean isWritable(@NotNull VirtualFile file) {
        return file instanceof CouchbaseDocumentVirtualFile && !isReadOnly();
    }

    @Override
    public void setWritable(@NotNull VirtualFile file, boolean writableFlag) throws IOException {

    }

    @Override
    public @NotNull VirtualFile createChildDirectory(Object requestor, @NotNull VirtualFile parent, @NotNull String dir) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull VirtualFile createChildFile(Object requestor, @NotNull VirtualFile parent, @NotNull String file) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteFile(Object requestor, @NotNull VirtualFile file) throws IOException {

    }

    @Override
    public void moveFile(Object requestor, @NotNull VirtualFile file, @NotNull VirtualFile newParent) throws IOException {

    }

    @Override
    public void renameFile(Object requestor, @NotNull VirtualFile file, @NotNull String newName) throws IOException {

    }

    @Override
    public @Nullable FileAttributes getAttributes(@NotNull VirtualFile file) {
        return null;
    }

    @Override
    protected @NotNull String extractRootPath(@NotNull String normalizedPath) {
        return "";
    }

    @Override
    public @Nullable VirtualFile findFileByPathIfCached(@NonNls @NotNull String path) {
        return null;
    }

    @Override
    public boolean isReadOnly() {
        return ActiveCluster.getInstance().isReadOnlyMode();
    }
}
