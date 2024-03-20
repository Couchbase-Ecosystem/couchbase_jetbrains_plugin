package com.couchbase.intellij.persistence;

import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.ObjectMapper;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.intellij.database.ActiveCluster;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Objects;

public class CouchbaseDocumentVirtualFile extends VirtualFile {
    private final String bucket;
    private final String scope;
    private final String collection;
    private final String id;

    private final String path;
    private final String name;
    private FileType type;

    private Long timestamp = System.currentTimeMillis();
    private Long modified = -1L;

    private byte[] content;
    private final Project project;

    public CouchbaseDocumentVirtualFile(Project project, FileType type, String bucket, String scope, String collection, String id) {
        this.project = project;
        this.type = type;
        this.bucket = bucket;
        this.scope = scope;
        this.collection = collection;
        this.id = id;

        this.path = String.format("%s/%s/%s", bucket, scope, collection);
        if (JsonFileType.INSTANCE.equals(type)) {
            this.name = String.format("%s.json", id);
        } else {
            this.name = String.format("(read-only)%s", id);
        }
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
        return !ActiveCluster.getInstance().isReadOnlyMode() && JsonFileType.INSTANCE.equals(type);
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
        this.content = content;
        super.setBinaryContent(content, newModificationStamp, newTimeStamp, requestor);
    }

    @Override
    public @NotNull OutputStream getOutputStream(Object requestor, long newModificationStamp, long newTimeStamp) throws IOException {
        modified = newModificationStamp;
        timestamp = newTimeStamp;
        return new ByteArrayOutputStream();
    }

    @Override
    public byte @NotNull [] contentsToByteArray() throws IOException {
        if (content == null) {
            refresh(false, false, null);
        }
        return content;
    }

    @Override
    public long getModificationStamp() {
        return modified;
    }

    @Override
    public long getTimeStamp() {
        return timestamp;
    }

    @Override
    public long getLength() {
        try {
            return contentsToByteArray().length;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setTimeStamp(long timeStamp) {
        this.timestamp = timeStamp;
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

    @Override
    public void refresh(boolean asynchronous, boolean recursive, @Nullable Runnable postRunnable) {
        Runnable refresh = () -> {
            GetResult gr = ActiveCluster.getInstance().getCluster()
                    .bucket(bucket)
                    .scope(scope)
                    .collection(collection)
                    .get(id);

            byte[] newContent;
            try {
                ObjectMapper mapper = new ObjectMapper();
                newContent = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsBytes(mapper.readTree(gr.contentAsBytes()));
            } catch (Throwable e) {
                newContent = gr.contentAsBytes();
            }

            content = newContent;

            if (postRunnable != null) {
                postRunnable.run();
            }
        };

        if (asynchronous) {
            ApplicationManager.getApplication().invokeLater(refresh);
        } else {
            refresh.run();
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
        return Objects.equals(bucket, that.bucket) && Objects.equals(scope, that.scope) && Objects.equals(collection, that.collection) && Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bucket, scope, collection, id, name);
    }
}
