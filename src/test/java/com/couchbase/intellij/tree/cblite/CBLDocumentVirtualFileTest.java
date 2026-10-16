package com.couchbase.intellij.tree.cblite;

import com.intellij.json.JsonFileType;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class CBLDocumentVirtualFileTest {

    private static CBLDocumentVirtualFile jsonFile(String scope, String collection, String id) {
        return new CBLDocumentVirtualFile(mock(Project.class), JsonFileType.INSTANCE, scope, collection, id);
    }

    @Test
    public void jsonTypeNamesFileWithJsonExtension() {
        CBLDocumentVirtualFile file = jsonFile("inventory", "hotel", "doc1");

        Assert.assertEquals("doc1.json", file.getName());
        Assert.assertSame(JsonFileType.INSTANCE, file.getType());
    }

    @Test
    public void nonJsonTypeIsMarkedReadOnlyInName() {
        FileType other = null;
        CBLDocumentVirtualFile file = new CBLDocumentVirtualFile(mock(Project.class), other, "inventory", "hotel", "doc1");

        Assert.assertEquals("(read-only)doc1", file.getName());
    }

    @Test
    public void pathIsScopeAndCollectionUnderRoot() {
        CBLDocumentVirtualFile file = jsonFile("inventory", "hotel", "doc1");

        Assert.assertEquals("/inventory/hotel", file.getPath());
        Assert.assertEquals("inventory", file.scope());
        Assert.assertEquals("hotel", file.collection());
        Assert.assertEquals("doc1", file.id());
    }

    @Test
    public void fixedVirtualFileFlags() {
        CBLDocumentVirtualFile file = jsonFile("s", "c", "i");

        Assert.assertTrue(file.isWritable());
        Assert.assertFalse(file.isDirectory());
        Assert.assertTrue(file.isValid());
        Assert.assertNull(file.getParent());
        Assert.assertEquals(0, file.getChildren().length);
    }

    @Test
    public void equalityIgnoresTypeAndProjectAndUsesScopeCollectionId() {
        CBLDocumentVirtualFile a = jsonFile("s", "c", "i");
        CBLDocumentVirtualFile b = new CBLDocumentVirtualFile(mock(Project.class), null, "s", "c", "i");
        CBLDocumentVirtualFile differentId = jsonFile("s", "c", "other");

        Assert.assertEquals(a, b);
        Assert.assertEquals(a.hashCode(), b.hashCode());
        Assert.assertNotEquals(a, differentId);
        Assert.assertNotEquals(a, null);
        Assert.assertNotEquals(a, "s/c/i");
    }

    @Test
    public void binaryContentRoundTripsWithoutRefresh() throws Exception {
        CBLDocumentVirtualFile file = jsonFile("s", "c", "i");
        byte[] payload = "{\"a\":1}".getBytes();

        file.setBinaryContent(payload, 5L, 6L, null);

        Assert.assertArrayEquals(payload, file.contentsToByteArray());
        Assert.assertEquals(payload.length, file.getLength());
    }

    @Test
    public void outputStreamRecordsModificationAndTimestamps() throws Exception {
        CBLDocumentVirtualFile file = jsonFile("s", "c", "i");

        file.getOutputStream(null, 42L, 43L);

        Assert.assertEquals(42L, file.getModificationStamp());
        Assert.assertEquals(43L, file.getTimeStamp());
    }

    @Test
    public void setTimeStampUpdatesTimestamp() {
        CBLDocumentVirtualFile file = jsonFile("s", "c", "i");

        file.setTimeStamp(99L);

        Assert.assertEquals(99L, file.getTimeStamp());
    }

    @Test
    public void inputStreamReturnsSetContent() throws Exception {
        CBLDocumentVirtualFile file = jsonFile("s", "c", "i");
        file.setBinaryContent("hi".getBytes(), 0L, 0L, null);

        byte[] read = file.getInputStream().readAllBytes();

        Assert.assertEquals("hi", new String(read));
    }

    // NOTE: possible bug: without an active Couchbase Lite connection, refresh() swallows the NPE and leaves
    // content null (the document is not replaced by "{}" as the not-found path does). contentsToByteArray() is
    // annotated @NotNull, so the IntelliJ instrumentation turns that null into an IllegalStateException.
    @Test
    public void refreshWithoutActiveDatabaseLeavesContentNullAndRunsCallback() throws Exception {
        CBLDocumentVirtualFile file = jsonFile("s", "c", "i");
        boolean[] ran = {false};

        file.refresh(false, false, () -> ran[0] = true);

        Assert.assertTrue(ran[0]);
        Assert.assertThrows(IllegalStateException.class, file::contentsToByteArray);
    }

    @Test
    public void getLengthWithoutContentAndDatabasePropagatesIllegalState() {
        Assert.assertThrows(IllegalStateException.class, () -> jsonFile("s", "c", "i").getLength());
    }
}
