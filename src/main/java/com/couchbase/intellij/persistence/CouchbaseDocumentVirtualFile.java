package com.couchbase.intellij.persistence;

import com.couchbase.intellij.database.ActiveCluster;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CouchbaseDocumentVirtualFile extends VirtualFile {
    private final String bucket;
    private final String scope;
    private final String collection;
    private final String id;

    private final String path;
    private final String name;

    public CouchbaseDocumentVirtualFile(String bucket, String scope, String collection, String id) {
        this.bucket = bucket;
        this.scope = scope;
        this.collection = collection;
        this.id = id;

        this.path = String.format("%s/%s/%s", bucket, scope, collection);
        this.name = String.format("%s_%s.json", collection, id);
    }
    @Override
    public @NotNull @NlsSafe String getName() {
        return name;
    }

    @Override
    public @NotNull VirtualFileSystem getFileSystem() {
        return CouchbaseFileSystem.INSTANCE;
    }

    @Override
    public @NonNls @NotNull String getPath() {
        return path;
    }

    @Override
    public boolean isWritable() {
        return !ActiveCluster.getInstance().isReadOnlyMode();
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public VirtualFile getParent() {
        return null;
    }

    @Override
    public VirtualFile[] getChildren() {
        return new VirtualFile[0];
    }

    @Override
    public @NotNull OutputStream getOutputStream(Object requestor, long newModificationStamp, long newTimeStamp) throws IOException {
        return new CouchbaseDocumentOutputStream(this);
    }

    @Override
    public byte @NotNull [] contentsToByteArray() throws IOException {
        return ActiveCluster.getInstance().getCluster()
                .bucket(bucket)
                .scope(scope)
                .collection(collection)
                .get(id).contentAsBytes();
    }

    @Override
    public long getTimeStamp() {
        return 0;
    }

    @Override
    public long getLength() {
        try {
            return contentsToByteArray().length;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void refresh(boolean asynchronous, boolean recursive, @Nullable Runnable postRunnable) {

    }

    @Override
    public @NotNull InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(contentsToByteArray());
    }

    public String bucket() {
        return bucket;
    }

    public String scope() {
        return scope;
    }

    public String collection() {
        return collection;
    }
    public String id() {
        return id;
    }
}
