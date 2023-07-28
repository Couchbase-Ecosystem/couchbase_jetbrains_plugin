package org.intellij.sdk.language.completion;

import com.couchbase.client.java.Cluster;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.entity.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import org.intellij.sdk.language.psi.SqlppFile;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

public class IdentifiersTest extends LightPlatformCodeInsightFixture4TestCase {

    private void checkCompletes(String text, String complete) {
        checkCompletes(null,text, complete);
    }
    private void checkCompletes(List<String> context, String text, String complete) {
        checkCompletes(context, text, items -> {
            assertTrue(items.length > 0);
            assertTrue(Arrays.stream(items).anyMatch(i -> complete.equals(i.getLookupString())));
        });
    }
    private void checkCompletes(List<String> context, String text, Consumer<LookupElement[]> checker) {
        ActiveCluster activeClusterInstance = Mockito.mock(ActiveCluster.class);
        Cluster cluster = Mockito.mock(Cluster.class);
        CouchbaseBucket bucket = Mockito.mock(CouchbaseBucket.class);
        CouchbaseBucket otherBucket = Mockito.mock(CouchbaseBucket.class);
        CouchbaseScope scope = Mockito.mock(CouchbaseScope.class);
        CouchbaseScope otherScope = Mockito.mock(CouchbaseScope.class);
        CouchbaseCollection collection = Mockito.mock(CouchbaseCollection.class);
        CouchbaseCollection otherCollection = Mockito.mock(CouchbaseCollection.class);
        CouchbaseDocumentFlavor flavor = Mockito.mock(CouchbaseDocumentFlavor.class);
        CouchbaseField parentField = Mockito.mock(CouchbaseField.class);
        CouchbaseField childField = Mockito.mock(CouchbaseField.class);

        Mockito.when(bucket.getName()).thenReturn("bucket-entity");
        Mockito.when(otherBucket.getName()).thenReturn("other-bucket-entity");
        Mockito.when(scope.getName()).thenReturn("scope-entity");
        Mockito.when(otherScope.getName()).thenReturn("other-scope-entity");
        Mockito.when(collection.getName()).thenReturn("collection-entity");
        Mockito.when(otherCollection.getName()).thenReturn("other-collection-entity");
        Mockito.when(parentField.getName()).thenReturn("parent-field");
        Mockito.when(childField.getName()).thenReturn("child-field");

        Mockito.when(activeClusterInstance.getChildren()).thenReturn(new HashSet<>(Arrays.asList(bucket, otherBucket)));
        Mockito.when(bucket.getChildren()).thenReturn(new HashSet<>(Arrays.asList(scope)));
        Mockito.when(otherBucket.getChildren()).thenReturn(new HashSet<>(Arrays.asList(otherScope)));
        Mockito.when(scope.getChildren()).thenReturn(new HashSet<>(Arrays.asList(collection)));
        Mockito.when(otherScope.getChildren()).thenReturn(new HashSet<>(Arrays.asList(otherCollection)));
        Mockito.when(collection.getChildren()).thenReturn(new HashSet<>(Arrays.asList(flavor)));
        Mockito.when(otherCollection.getChildren()).thenReturn(new HashSet<>());
        Mockito.when(flavor.getChildren()).thenReturn(new HashSet<>(Arrays.asList(parentField)));
        Mockito.when(parentField.getChildren()).thenReturn(new HashSet<>(Arrays.asList(childField)));
        Mockito.when(childField.getChildren()).thenReturn(Collections.EMPTY_SET);
        Mockito.when(activeClusterInstance.getCluster()).thenReturn(cluster);
        ActiveCluster.setInstance(activeClusterInstance);

        myFixture.configureByText("test.sqlpp", "");
        myFixture.type(text);
        ((SqlppFile) myFixture.getFile()).setClusterContext(context);
        LookupElement[] items = myFixture.completeBasic();
        checker.accept(items);
    }

    @Test
    public void testInExpr() {
        checkCompletes("select test, ", "bucket-entity");
    }

    @Test
    public void testInFunction() {
        checkCompletes("select * from test, TOOBJ(", "bucket-entity");
    }

    @Test
    public void testEscaped() {
        checkCompletes("select `", "bucket-entity");
    }

    @Test
    public void testDot() {
        checkCompletes("select bucket-entity.", "scope-entity");
    }

    @Test
    public void testField() {
        checkCompletes("select ", "parent-field");
    }

    @Test
    public void testEscapedDot() {
        checkCompletes("select `bucket-entity`.", "scope-entity");
    }

    @Test
    public void testEscapedDotCollection() {
        checkCompletes("select `bucket-entity`.`scope-entity`.`collection-entity`.", "parent-field");
    }

    @Test
    public void testDotEscape() {
        checkCompletes("select bucket-entity.`", "scope-entity");
    }

    @Test
    public void testDoubleDot() {
        checkCompletes("select * from bucket-entity.scope-entity.", "collection-entity");
    }

    @Test
    public void testUpdateSet() {
        checkCompletes("UPDATE test SET ", "parent-field");
    }

    @Test
    public void testUpdateSetExpression() {
        checkCompletes("UPDATE travel-sample.inventory.hotel SET `airlineid` = '100' + ", "parent-field");
    }

    @Test
    public void testUsesEditorContext() {
        checkCompletes(Arrays.asList("other-bucket-entity", "other-scope-entity"), "SELECT ", items -> {
            assertTrue(Arrays.stream(items).anyMatch(item -> "other-collection-entity".equals(item.getLookupString())));
            assertTrue(Arrays.stream(items).noneMatch(item -> "collection-entity".equals(item.getLookupString())));
        });
    }
}