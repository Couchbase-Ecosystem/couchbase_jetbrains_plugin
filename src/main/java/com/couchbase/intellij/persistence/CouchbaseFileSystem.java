package com.couchbase.intellij.persistence;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.openapi.vfs.newvfs.FileSystemInterface;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class CouchbaseFileSystem extends VirtualFileSystem implements FileSystemInterface {
    public static final CouchbaseFileSystem INSTANCE = new CouchbaseFileSystem();
    private List<VirtualFileListener> listeners = new ArrayList<>();

    @Override
    public @NonNls @NotNull String getProtocol() {
        return "couchbase";
    }

    @Override
    public @Nullable VirtualFile findFileByPath(@NotNull @NonNls String path) {
        return null;
    }

    @Override
    public void refresh(boolean asynchronous) {
        Log.info("cbfs refresh requested");
    }

    @Override
    public @Nullable VirtualFile refreshAndFindFileByPath(@NotNull String path) {
        return findFileByPath(path);
    }

    @Override
    public void addVirtualFileListener(@NotNull VirtualFileListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeVirtualFileListener(@NotNull VirtualFileListener listener) {
        listeners.remove(listener);
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
    public boolean isSymLink(@NotNull VirtualFile file) {
        return false;
    }

    @Override
    public @Nullable String resolveSymLink(@NotNull VirtualFile file) {
        return null;
    }

    @Override
    public boolean isReadOnly() {
        return ActiveCluster.getInstance().isReadOnlyMode();
    }

    void notifyContentsChanged(@NotNull VirtualFile file, long oldModificationStamp, long newModificationStamp) {
        VirtualFileEvent event = new VirtualFileEvent(null, file, null, oldModificationStamp, newModificationStamp);
        listeners.forEach(listener -> {
            listener.beforeContentsChange(event);
            listener.contentsChanged(event);
        });

    }
}
