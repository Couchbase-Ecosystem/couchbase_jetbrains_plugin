package com.couchbase.intellij.database.collections;

import com.couchbase.client.core.error.ScopeNotFoundException;
import com.couchbase.client.java.manager.collection.CollectionManager;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CollectionSourcesTest {
    @Test
    public void selectsTheRequestedScopeFromTheBucketManifest() {
        CollectionManager manager = mock(CollectionManager.class);
        ScopeSpec wanted = ScopeSpec.create("inventory", Set.of(CollectionSpec.create("hotel", "inventory")));
        when(manager.getAllScopes()).thenReturn(List.of(ScopeSpec.create("_default", Set.of()), wanted));

        assertSame(wanted, CollectionSources.requireScope(manager, "inventory"));
        verify(manager).getAllScopes();
    }

    @Test
    public void missingScopeStillReportsTheSdkException() {
        CollectionManager manager = mock(CollectionManager.class);
        when(manager.getAllScopes()).thenReturn(List.of(ScopeSpec.create("_default", Set.of())));

        assertThrows(ScopeNotFoundException.class, () -> CollectionSources.requireScope(manager, "missing"));
    }
}
