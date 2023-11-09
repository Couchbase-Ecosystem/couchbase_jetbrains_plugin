package com.couchbase.intellij.tree.cblite.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.couchbase.intellij.tree.cblite.ActiveCBLDatabase;
import com.couchbase.lite.Blob;
import com.couchbase.lite.Collection;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Document;
import com.couchbase.lite.MutableDictionary;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;

public class CBLBlobHandler {

    private static final String ATTACHMENT_FIELD = "_attachments";

    private CBLBlobHandler() {
    }

    public static boolean collectionHasBlob(Collection collection) throws CouchbaseLiteException {
        Query query = QueryBuilder.select().from(DataSource.collection(collection));

        ResultSet queryResults = query.execute();
        for (Result queryResult : queryResults) {
            Document doc = collection.getDocument(Objects.requireNonNull(queryResult.getString("_id")));
            if (documentHasBlob(doc)) {
                return true;
            }
        }
        return false;
    }

    public static boolean documentHasBlob(Document document) {
        // Check if ATTACHMENT_FIELD field exists and is a dictionary
        if (document.contains(ATTACHMENT_FIELD) && document.getDictionary(ATTACHMENT_FIELD) != null) {
            return true;
        }

        // Check all fields in the document for a dictionary with "@type" : "blob"
        for (String key : document.getKeys()) {
            if (document.getBlob(key) != null) {
                return true;
            }
        }

        return false;
    }

    public static Map<String, Blob> getDocumentBlobsWithNames(Document document) {

        Map<String, Blob> blobs = new HashMap<>();

        // Check if ATTACHMENT_FIELD field exists and is a dictionary
        if (document.contains(ATTACHMENT_FIELD) && document.getDictionary(ATTACHMENT_FIELD) != null) {
            Dictionary attachments = document.getDictionary(ATTACHMENT_FIELD);
            for (String key : attachments.getKeys()) {
                blobs.put(key, attachments.getBlob(key));
            }
        }

        // Check all fields in the document for a dictionary with "@type" : "blob"
        for (String key : document.getKeys()) {
            if (document.getBlob(key) != null) {
                blobs.put(key, document.getBlob(key));
            }
        }

        return blobs;
    }


    public static void removeBlobFromDocument(Collection collection, MutableDocument document, Blob blob) throws CouchbaseLiteException {
        // Check if ATTACHMENT_FIELD field exists and is a dictionary
        if (document.contains(ATTACHMENT_FIELD) && document.getDictionary(ATTACHMENT_FIELD) != null) {
            MutableDictionary attachments = document.getDictionary(ATTACHMENT_FIELD).toMutable();
            for (String key : attachments.getKeys()) {
                if (attachments.getBlob(key).equals(blob)) {
                    attachments.remove(key);
                    document.setDictionary(ATTACHMENT_FIELD, attachments);
                    ActiveCBLDatabase.getInstance().getDatabase().getCollection(collection.getName()).save(document);
                    return;
                }
            }
        }

        // Check all fields in the document for a dictionary with "@type" : "blob"
        for (String key : document.getKeys()) {
            if (document.getBlob(key) != null && document.getBlob(key).equals(blob)) {
                document.remove(key);
                collection.save(document);
                return;
            }
        }
    }

}
