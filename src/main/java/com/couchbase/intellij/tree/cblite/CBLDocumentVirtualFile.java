package com.couchbase.intellij.tree.cblite;

import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.ObjectMapper;
import com.couchbase.intellij.persistence.CouchbaseFileSystem;
import com.couchbase.intellij.workbench.Log;
import com.couchbase.lite.Document;
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

public class CBLDocumentVirtualFile extends VirtualFile {
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

    public CBLDocumentVirtualFile(Project project, FileType type, String scope, String collection, String id) {
        this.project = project;
        this.type = type;
        this.scope = scope;
        this.collection = collection;
        this.id = id;

        this.path = String.format("/%s/%s", scope, collection);
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
        return true;
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
            try {
                Document doc = ActiveCBLDatabase.getInstance().getDatabase()
                        .getScope(scope)
                        .getCollection(collection)
                        .getDocument(id);

                if (doc == null) {
                    content = "{}".getBytes();
                } else {
                    ObjectMapper mapper = new ObjectMapper();
                    content = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsBytes(mapper.readTree(doc.toJSON().getBytes()));
                }
            } catch (Exception e) {
                Log.error("Could not refresh document " + id, e);
            }
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
        CBLDocumentVirtualFile that = (CBLDocumentVirtualFile) o;
        return Objects.equals(scope, that.scope) && Objects.equals(collection, that.collection) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scope, collection, id);
    }
}
