package com.couchbase.intellij.tree.cblite.storage;

import com.couchbase.lite.Blob;
import com.couchbase.lite.Collection;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Document;
import com.couchbase.lite.MutableDictionary;
import com.couchbase.lite.MutableDocument;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CBLBlobHandlerTest {

    private static Document docWithTopLevelBlob(String field, Blob blob) {
        Document doc = mock(Document.class);
        when(doc.getKeys()).thenReturn(java.util.List.of(field));
        when(doc.getBlob(field)).thenReturn(blob);
        return doc;
    }

    private static Document docWithAttachments(String name, Blob blob) {
        Document doc = mock(Document.class);
        Dictionary attachments = mock(Dictionary.class);
        when(attachments.getKeys()).thenReturn(java.util.List.of(name));
        when(attachments.getBlob(name)).thenReturn(blob);
        when(doc.contains("_attachments")).thenReturn(true);
        when(doc.getDictionary("_attachments")).thenReturn(attachments);
        return doc;
    }

    @Test
    public void documentWithAttachmentsDictionaryHasBlob() {
        Document doc = docWithAttachments("a.txt", mock(Blob.class));

        Assert.assertTrue(CBLBlobHandler.documentHasBlob(doc));
    }

    @Test
    public void documentWithTopLevelBlobFieldHasBlob() {
        Document doc = docWithTopLevelBlob("photo", mock(Blob.class));

        Assert.assertTrue(CBLBlobHandler.documentHasBlob(doc));
    }

    @Test
    public void plainDocumentHasNoBlob() {
        Document doc = mock(Document.class);
        when(doc.getKeys()).thenReturn(java.util.List.of("name"));

        Assert.assertFalse(CBLBlobHandler.documentHasBlob(doc));
    }

    @Test
    public void attachmentsFieldWithoutDictionaryIsNotABlob() {
        // contains() is true but getDictionary() returns null: falls through to the key scan.
        Document doc = mock(Document.class);
        when(doc.contains("_attachments")).thenReturn(true);
        when(doc.getKeys()).thenReturn(java.util.List.of("_attachments"));

        Assert.assertFalse(CBLBlobHandler.documentHasBlob(doc));
    }

    @Test
    public void getBlobsWithNamesCollectsAttachmentsAndTopLevelBlobs() {
        Blob attachmentBlob = mock(Blob.class);
        Blob topLevelBlob = mock(Blob.class);

        Document doc = docWithAttachments("a.txt", attachmentBlob);
        when(doc.getKeys()).thenReturn(java.util.List.of("photo"));
        when(doc.getBlob("photo")).thenReturn(topLevelBlob);

        Map<String, Blob> blobs = CBLBlobHandler.getDocumentBlobsWithNames(doc);

        Assert.assertEquals(2, blobs.size());
        Assert.assertSame(attachmentBlob, blobs.get("a.txt"));
        Assert.assertSame(topLevelBlob, blobs.get("photo"));
    }

    @Test
    public void topLevelBlobOverridesAttachmentWithSameName() {
        // NOTE: possible bug (or at least surprising): when an attachment and a top-level field share a name,
        // the top-level blob silently replaces the attachment entry in the returned map.
        Blob attachmentBlob = mock(Blob.class);
        Blob topLevelBlob = mock(Blob.class);

        Document doc = docWithAttachments("shared", attachmentBlob);
        when(doc.getKeys()).thenReturn(java.util.List.of("shared"));
        when(doc.getBlob("shared")).thenReturn(topLevelBlob);

        Map<String, Blob> blobs = CBLBlobHandler.getDocumentBlobsWithNames(doc);

        Assert.assertEquals(1, blobs.size());
        Assert.assertSame(topLevelBlob, blobs.get("shared"));
    }

    @Test
    public void getBlobsWithNamesEmptyForPlainDocument() {
        Document doc = mock(Document.class);
        when(doc.getKeys()).thenReturn(java.util.List.of("name"));

        Assert.assertTrue(CBLBlobHandler.getDocumentBlobsWithNames(doc).isEmpty());
    }

    @Test
    public void removeTopLevelBlobRemovesFieldAndSavesWithGivenCollection() throws Exception {
        Blob blob = mock(Blob.class);
        MutableDocument doc = mock(MutableDocument.class);
        when(doc.getKeys()).thenReturn(java.util.List.of("photo"));
        when(doc.getBlob("photo")).thenReturn(blob);
        Collection collection = mock(Collection.class);

        CBLBlobHandler.removeBlobFromDocument(collection, doc, blob);

        verify(doc).remove("photo");
        verify(collection).save(doc);
    }

    @Test
    public void removeBlobNotPresentDoesNothing() throws Exception {
        MutableDocument doc = mock(MutableDocument.class);
        when(doc.getKeys()).thenReturn(java.util.List.of("photo"));
        when(doc.getBlob("photo")).thenReturn(mock(Blob.class));
        Collection collection = mock(Collection.class);

        CBLBlobHandler.removeBlobFromDocument(collection, doc, mock(Blob.class));

        verify(doc, never()).remove("photo");
        verify(collection, never()).save(doc);
    }

    // NOTE: possible bug: the attachments branch saves through ActiveCBLDatabase.getInstance().getDatabase()
    // instead of the collection argument, so removing an attachment without an active connection throws NPE.
    @Test(expected = NullPointerException.class)
    public void removeAttachmentWithoutActiveDatabaseThrowsNpe() throws Exception {
        Blob blob = mock(Blob.class);
        MutableDictionary attachments = mock(MutableDictionary.class);
        when(attachments.getKeys()).thenReturn(java.util.List.of("a.txt"));
        when(attachments.getBlob("a.txt")).thenReturn(blob);

        MutableDictionary dict = mock(MutableDictionary.class);
        when(dict.toMutable()).thenReturn(attachments);

        MutableDocument doc = mock(MutableDocument.class);
        when(doc.contains("_attachments")).thenReturn(true);
        when(doc.getDictionary("_attachments")).thenReturn(dict);

        CBLBlobHandler.removeBlobFromDocument(mock(Collection.class), doc, blob);
    }
}
