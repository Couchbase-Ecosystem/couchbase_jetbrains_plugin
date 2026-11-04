package com.couchbase.intellij.persistence.storage;

import com.couchbase.intellij.persistence.Clusters;
import com.couchbase.intellij.persistence.CollectionRelationships;
import com.couchbase.intellij.persistence.FavoriteQueries;
import com.couchbase.intellij.persistence.QueryHistory;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * The storages are IntelliJ PersistentStateComponents. Only their in-memory accessors are exercised here
 * (getInstance() needs the IDE application and is not called).
 */
public class StorageDefaultsTest {

    @Test
    public void clustersStorageLazilyCreatesValueAndAcceptsReplacement() {
        ClustersStorage storage = new ClustersStorage();
        assertNotNull(storage.getState());
        Clusters value = storage.getValue();
        assertNotNull(value);
        assertSame(value, storage.getValue());

        Clusters replacement = new Clusters();
        storage.setValue(replacement);
        assertSame(replacement, storage.getValue());
    }

    @Test
    public void favoriteQueryStorageLazilyCreatesEmptyList() {
        FavoriteQueryStorage storage = new FavoriteQueryStorage();
        FavoriteQueries value = storage.getValue();
        assertNotNull(value.getList());
        assertTrue(value.getList().isEmpty());

        FavoriteQueries replacement = new FavoriteQueries();
        storage.setValue(replacement);
        assertSame(replacement, storage.getValue());
    }

    @Test
    public void queryHistoryStorageLazilyCreatesEmptyHistory() {
        QueryHistoryStorage storage = new QueryHistoryStorage();
        QueryHistory value = storage.getValue();
        assertNotNull(value.getHistory());
        assertTrue(value.getHistory().isEmpty());

        QueryHistory replacement = new QueryHistory();
        storage.setValue(replacement);
        assertSame(replacement, storage.getValue());
    }

    @Test
    public void relationshipStorageLazilyCreatesEmptyRelationshipMap() {
        RelationshipStorage storage = new RelationshipStorage();
        CollectionRelationships value = storage.getValue();
        assertNotNull(value.getRelationships());
        assertTrue(value.getRelationships().isEmpty());

        CollectionRelationships replacement = new CollectionRelationships();
        replacement.setRelationships(new HashMap<>());
        storage.setValue(replacement);
        assertSame(replacement, storage.getValue());
    }
}
