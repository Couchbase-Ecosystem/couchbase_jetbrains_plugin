package com.couchbase.intellij.persistence;

import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.ObjectMapper;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.intellij.VirtualFileKeys;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.workbench.Log;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Arrays;
import java.util.Objects;

public class CouchbaseDocumentVirtualFile extends VirtualFile {
    private final String bucket;
    private final String scope;
    private final String collection;
    private final String id;
    private volatile long mtime;

    private volatile long contentHash;
    private volatile byte[] lastReadContent;

    private final String path;
    private final String name;
    private volatile long size;
    private FileType type;
    private boolean desynchronized = false;

    private final Project project;

    public CouchbaseDocumentVirtualFile(Project project, FileType type, String bucket, String scope, String collection, String id) {
        this.project = project;
        this.type = type;
        this.bucket = bucket;
        this.scope = scope;
        this.collection = collection;
        this.id = id;

        if (JsonFileType.INSTANCE.equals(type)) {
            this.name = String.format("%s.json", id);
        } else {
            this.name = String.format("(read-only)%s", id);
        }
        this.path = String.format("couchbase://%s/%s/%s/%s.%s", bucket, scope, collection, id, type.getDefaultExtension());
    }

    @Override
    public @NotNull @NlsSafe String getName() {
        return name;
    }

    @Override
    public @NotNull CouchbaseFileSystem getFileSystem() {
        return CouchbaseFileSystem.INSTANCE;
    }

    @Override
    public @NonNls @NotNull String getPath() {
        return path;
    }

    @Override
    public boolean isWritable() {
        ActiveCluster active = ActiveCluster.getInstance();
        return active != null && !active.isReadOnlyMode() && JsonFileType.INSTANCE.equals(type);
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
    public void setBinaryContent(byte @NotNull [] content, long newModificationStamp, long newTimeStamp, Object requestor) throws IOException {
        super.setBinaryContent(content, newModificationStamp, newTimeStamp, requestor);
        this.mtime = newTimeStamp;
    }

    @Override
    public @NotNull OutputStream getOutputStream(Object requestor, long newModificationStamp, long newTimeStamp) throws IOException {
        Log.info("Created output stream for couchbase document: " + getPath() + "/" + getName());
        return new CouchbaseDocumentOutputStream(this);
    }

    @Override
    public byte @NotNull [] contentsToByteArray() throws IOException {
        if (lastReadContent != null) return lastReadContent.clone();
        if (ApplicationManager.getApplication().isDispatchThread()) throw new IOException("Load the document in the background before opening its editor.");
        Log.info("fetched contents as byte array for couchbase document: " + getPath());
        GetResult result = fetch();
        byte[] contents = fetchContent(result);
        putUserData(VirtualFileKeys.CAS, Long.toString(result.cas()));
        updateFromCluster(contents);
        this.size = contents.length;
        return contents;
    }

    @Override
    public long getModificationStamp() {
        return contentHash;
    }

    @Override
    public long getTimeStamp() {
        return mtime;
    }

    @Override
    public long getLength() {
        return size;
    }

    public void setTimeStamp(long timeStamp) {
        this.mtime = timeStamp;
    }

    @Override
    public @NotNull InputStream getInputStream() throws IOException {
        Log.info("Created input stream for couchbase document: " + getPath());
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

    private GetResult fetch() {
        ActiveCluster active = ActiveCluster.getInstance();
        if (!Objects.equals(active.getId(), getUserData(VirtualFileKeys.CONN_ID)))
            throw new IllegalStateException("The document belongs to a different connection.");
        return active.getCluster()
                            .bucket(bucket)
                            .scope(scope)
                            .collection(collection)
                            .get(id);
    }

    private byte[] fetchContent(GetResult gr) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return new ObjectMapper().writerWithDefaultPrettyPrinter()
                                           .writeValueAsBytes(mapper.readTree(gr.contentAsBytes()));
        } catch (Throwable e) {
            return gr.contentAsBytes();
        }
    }

    @Override
    public void refresh(boolean asynchronous, boolean recursive, @Nullable Runnable postRunnable) {
        Runnable refresh = () -> {
            GetResult gr = fetch();
            Document cached = FileDocumentManager.getInstance().getCachedDocument(this);
            // Polling must not advance the CAS of unsaved edits past a concurrent server update.
            if (!desynchronized && (cached == null || !FileDocumentManager.getInstance().isDocumentUnsaved(cached))) {
                updateFromCluster(fetchContent(gr));
                putUserData(VirtualFileKeys.CAS, String.valueOf(gr.cas()));
            }

            if (postRunnable != null) {
                postRunnable.run();
            }
        };

        if (asynchronous || ApplicationManager.getApplication().isDispatchThread()) {
            ApplicationManager.getApplication().executeOnPooledThread(refresh);
        } else {
            refresh.run();
        }
    }

    private void updateFromCluster(byte[] newContent) {
        lastReadContent = newContent.clone();
        size = newContent.length;
        desynchronized = false;
        int newContentHash = Arrays.hashCode(newContent);
        if (this.contentHash != newContentHash) {
            long newMtime = System.currentTimeMillis();
            Log.info("Couchbase document content changed: " + getPath());
            this.contentHash = newContentHash;
            this.mtime = newMtime;
        }
    }

    public FileType getType() {
        return type;
    }

    public Project getProject() {
        return project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CouchbaseDocumentVirtualFile that = (CouchbaseDocumentVirtualFile) o;
        return Objects.equals(bucket, that.bucket) && Objects.equals(scope, that.scope) && Objects.equals(collection, that.collection) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bucket, scope, collection, id, name);
    }

    @Override
    public void setBinaryContent(byte @NotNull [] content, long newModificationStamp, long newTimeStamp) throws IOException {
        super.setBinaryContent(content, newModificationStamp, newTimeStamp);
    }

    void setContentHash(int hash) {
        this.contentHash = hash;
    }

    void setSize(long length) {
        this.size = length;
    }

    void recordSavedContent(byte[] content) {
        lastReadContent = content.clone();
    }

    public void acceptServerContent(byte[] content, long cas) {
        updateFromCluster(content);
        putUserData(VirtualFileKeys.CAS, Long.toString(cas));
    }

    /** Explicit reload, including when the user chose to discard unsaved edits. */
    public void reloadFromCluster() {
        GetResult result = fetch();
        updateFromCluster(fetchContent(result));
        putUserData(VirtualFileKeys.CAS, Long.toString(result.cas()));
    }

    public boolean checkUpdatedOnCluster() {
        GetResult gr = fetch();
        return contentHash != Arrays.hashCode(fetchContent(gr));
    }

    public void setDesynchronized(boolean desynchronized) {
        this.desynchronized = desynchronized;
    }

    public boolean isDesynchronized() {
        return desynchronized;
    }
}
