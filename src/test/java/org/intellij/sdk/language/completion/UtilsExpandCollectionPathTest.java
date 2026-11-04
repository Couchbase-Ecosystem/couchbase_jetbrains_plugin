package org.intellij.sdk.language.completion;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.entity.CouchbaseCollection;
import com.couchbase.intellij.database.entity.CouchbaseBucket;
import com.couchbase.intellij.database.entity.CouchbaseScope;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

/**
 * Pure JUnit4 tests for {@link Utils#expandCollectionPath}: the ActiveCluster singleton is replaced with a Mockito mock
 * and restored after each test.
 */
public class UtilsExpandCollectionPathTest {

    private ActiveCluster originalCluster;
    private ActiveCluster cluster;

    @Before
    public void setUp() {
        originalCluster = ActiveCluster.getInstance();
        cluster = Mockito.mock(ActiveCluster.class);
        ActiveCluster.setInstance(cluster);
    }

    @After
    public void tearDown() {
        ActiveCluster.setInstance(originalCluster);
    }

    private CouchbaseCollection collection(String name, List<String> path) {
        CouchbaseCollection c = Mockito.mock(CouchbaseCollection.class);
        Mockito.when(c.getName()).thenReturn(name);
        Mockito.when(c.pathElements()).thenReturn(path);
        return c;
    }

    private CouchbaseScope scope(CouchbaseCollection... collections) {
        CouchbaseScope s = Mockito.mock(CouchbaseScope.class);
        Mockito.when(s.getChildren()).thenReturn(new HashSet<>(Arrays.asList(collections)));
        return s;
    }

    private CouchbaseBucket bucket(CouchbaseScope... scopes) {
        CouchbaseBucket b = Mockito.mock(CouchbaseBucket.class);
        Mockito.when(b.getChildren()).thenReturn(new HashSet<>(Arrays.asList(scopes)));
        return b;
    }

    @Test
    public void testEmptyContextResolvesUniqueCollectionName() {
        CouchbaseCollection hotels = collection("hotel", Arrays.asList("travel", "inventory", "hotel"));
        CouchbaseBucket builtBucket = bucket(scope(hotels));
        Mockito.when(cluster.getChildren()).thenReturn(new HashSet<>(Collections.singletonList(builtBucket)));

        assertEquals(Arrays.asList("travel", "inventory", "hotel"),
                Utils.expandCollectionPath(Collections.emptyList(), "hotel"));
    }

    @Test
    public void testEmptyContextMatchIsCaseInsensitive() {
        CouchbaseCollection hotels = collection("hotel", Arrays.asList("travel", "inventory", "hotel"));
        CouchbaseBucket builtBucket = bucket(scope(hotels));
        Mockito.when(cluster.getChildren()).thenReturn(new HashSet<>(Collections.singletonList(builtBucket)));

        assertEquals(Arrays.asList("travel", "inventory", "hotel"),
                Utils.expandCollectionPath(Collections.emptyList(), "HOTEL"));
    }

    @Test
    public void testEmptyContextNoMatchReturnsStubWithName() {
        CouchbaseCollection hotels = collection("hotel", Arrays.asList("travel", "inventory", "hotel"));
        CouchbaseBucket builtBucket = bucket(scope(hotels));
        Mockito.when(cluster.getChildren()).thenReturn(new HashSet<>(Collections.singletonList(builtBucket)));

        assertEquals(Collections.singletonList("airport"),
                Utils.expandCollectionPath(Collections.emptyList(), "airport"));
    }

    @Test
    public void testEmptyContextAmbiguousNameReturnsStub() {
        CouchbaseCollection inScopeA = collection("hotel", Arrays.asList("travel", "a", "hotel"));
        CouchbaseCollection inScopeB = collection("hotel", Arrays.asList("travel", "b", "hotel"));
        CouchbaseBucket builtBucket = bucket(scope(inScopeA), scope(inScopeB));
        Mockito.when(cluster.getChildren()).thenReturn(new HashSet<>(Collections.singletonList(builtBucket)));

        assertEquals(Collections.singletonList("hotel"),
                Utils.expandCollectionPath(Collections.emptyList(), "hotel"));
    }

    @Test
    public void testEmptyClusterReturnsStub() {
        Mockito.when(cluster.getChildren()).thenReturn(Collections.emptySet());

        assertEquals(Collections.singletonList("hotel"),
                Utils.expandCollectionPath(Collections.emptyList(), "hotel"));
    }

    @Test
    public void testNonEmptyContextWithSingleCollectionReturnsItsPath() {
        CouchbaseCollection only = collection("hotel", Arrays.asList("travel", "inventory", "hotel"));
        Mockito.when(cluster.navigate(Mockito.anyList())).thenAnswer(inv -> Stream.of(scope(only)));

        assertEquals(Arrays.asList("travel", "inventory", "hotel"),
                Utils.expandCollectionPath(Arrays.asList("travel", "inventory"), "hot"));
    }

    @Test
    public void testNonEmptyContextWithManyCollectionsReturnsContextPlusName() {
        CouchbaseCollection hotels = collection("hotel", Arrays.asList("travel", "inventory", "hotel"));
        CouchbaseCollection airports = collection("airport", Arrays.asList("travel", "inventory", "airport"));
        Mockito.when(cluster.navigate(Mockito.anyList())).thenAnswer(inv -> Stream.of(scope(hotels, airports)));

        assertEquals(Arrays.asList("travel", "inventory", "hotel"),
                Utils.expandCollectionPath(Arrays.asList("travel", "inventory"), "hotel"));
    }

    @Test
    public void testNonEmptyContextIgnoresRequestedNameWhenSingleCollection() {
        // NOTE: possible bug: with a non-empty context the requested name is never used to filter the
        // candidates, so a single collection in the scope is returned even when the typed name does not match it.
        CouchbaseCollection only = collection("hotel", Arrays.asList("travel", "inventory", "hotel"));
        Mockito.when(cluster.navigate(Mockito.anyList())).thenAnswer(inv -> Stream.of(scope(only)));

        assertEquals(Arrays.asList("travel", "inventory", "hotel"),
                Utils.expandCollectionPath(Arrays.asList("travel", "inventory"), "zzz"));
    }
}
