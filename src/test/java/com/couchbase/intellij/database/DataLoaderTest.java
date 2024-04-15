package com.couchbase.intellij.database;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.intellij.tree.node.CollectionNodeDescriptor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class DataLoaderTest {

    private static Cluster cluster;
    private static Bucket bucket;

    private static Scope scope;

    private static final String BUCKET_NAME = "testBucket";
    private static final String USERNAME = "Administrator";
    private static final String PASSWORD = "password";

    private static String noIndexCollection = "noIndexCollection";
    private static String primaryIndexCollection = "primaryIndexCollection";

    private static String partialAndSecondaryIndexCollection = "partialAndSecondaryIndexCollection";

    private static String primaryAndSecondaryIndexCollection = "primaryAndSecondaryIndexCollection";

    private static String secondaryWithFirstInvalidFieldCollection = "secondaryWithFirstInvalidFieldCollection";

    private static String secondaryWithFirstInvalidFieldCollectionAndAnotherSecondary = "secondaryWithFirstInvalidFieldCollectionAndAnotherSecondary";

    private static String secondaryWithSingleFirstInvalid = "secondaryWithSingleFirstInvalid";

    private static String secondaryWithDesc = "secondaryWithDesc";


    @Container
    public static CouchbaseContainer couchbaseContainer = new CouchbaseContainer("couchbase/server:latest")
            .withBucket(new BucketDefinition(BUCKET_NAME))
            .withCredentials(USERNAME, PASSWORD);


    @BeforeAll
    public static void setup() throws Exception {
        // Start the container and get the connection string
        couchbaseContainer.start();

        // Connect to the cluster
        cluster = Cluster.connect(couchbaseContainer.getConnectionString(), USERNAME, PASSWORD);
        bucket = cluster.bucket(BUCKET_NAME);
        scope = bucket.defaultScope();

        ActiveCluster activeCluster = new ActiveCluster();
        activeCluster.setCluster(cluster);
        ActiveCluster.setInstance(activeCluster);

        bucket.collections()
                .createCollection(CollectionSpec.create(noIndexCollection, scope.name()));
        bucket.collections()
                .createCollection(CollectionSpec.create(primaryIndexCollection, scope.name()));
        bucket.collections()
                .createCollection(CollectionSpec.create(primaryAndSecondaryIndexCollection, scope.name()));
        bucket.collections()
                .createCollection(CollectionSpec.create(partialAndSecondaryIndexCollection, scope.name()));
        bucket.collections()
                .createCollection(CollectionSpec.create(secondaryWithFirstInvalidFieldCollection, scope.name()));
        bucket.collections()
                .createCollection(CollectionSpec.create(secondaryWithFirstInvalidFieldCollectionAndAnotherSecondary, scope.name()));
        bucket.collections()
                .createCollection(CollectionSpec.create(secondaryWithSingleFirstInvalid, scope.name()));
        bucket.collections()
                .createCollection(CollectionSpec.create(secondaryWithDesc, scope.name()));

        Thread.sleep(2000);

        scope.query("CREATE PRIMARY INDEX ON " + primaryIndexCollection);

        scope.query("CREATE INDEX test2 ON " + primaryAndSecondaryIndexCollection + "(att1,att2)");
        scope.query("CREATE PRIMARY INDEX ON " + primaryAndSecondaryIndexCollection);
        scope.query("CREATE INDEX test3 ON " + primaryAndSecondaryIndexCollection + "(att1,att2)");

        scope.query("CREATE INDEX test4 ON " + partialAndSecondaryIndexCollection + "(att1,att2) where att1 < att2");
        scope.query("CREATE INDEX test5 ON " + partialAndSecondaryIndexCollection + "(att3,att4)");
        scope.query("CREATE INDEX test6 ON " + partialAndSecondaryIndexCollection + "(att5,att6) where att1 > 100");

        scope.query("CREATE INDEX test7 ON " + secondaryWithFirstInvalidFieldCollection + "((distinct (array (`v`.`day`) for `v` in `schedule` end)),att2)");

        scope.query("CREATE INDEX test8 ON " + secondaryWithFirstInvalidFieldCollectionAndAnotherSecondary + "((distinct (array (`v`.`day`) for `v` in `schedule` end)),att2)");
        scope.query("CREATE INDEX test9 ON " + secondaryWithFirstInvalidFieldCollectionAndAnotherSecondary + "(att1,att3)");

        scope.query("CREATE INDEX test10 ON " + secondaryWithSingleFirstInvalid + "((distinct (array (`v`.`day`) for `v` in `schedule` end)))");

        scope.query("CREATE INDEX test11 ON " + secondaryWithDesc + "(att1   desc)");

    }

    @AfterAll
    public static void tearDown() {
        // Disconnect and stop the container
        if (cluster != null) {
            cluster.disconnect();
        }
        couchbaseContainer.stop();
    }

    @Test
    public void testNoIndexCollection() {
        CollectionNodeDescriptor desc = new CollectionNodeDescriptor(
                noIndexCollection, null, bucket.name(), scope.name(), null);
        String result = DataLoader.getIndexedField(desc);
        Assertions.assertNull(result);
    }

    @Test
    public void testPrimaryIndexCollection() {
        CollectionNodeDescriptor desc = new CollectionNodeDescriptor(
                primaryIndexCollection, null, bucket.name(), scope.name(), null);
        String result = DataLoader.getIndexedField(desc);
        Assertions.assertEquals("meta(couchbaseAlias).id", result);
    }

    @Test
    public void testPrimaryAndSecondaryIndexCollection() {
        CollectionNodeDescriptor desc = new CollectionNodeDescriptor(
                primaryAndSecondaryIndexCollection, null, bucket.name(), scope.name(), null);
        String result = DataLoader.getIndexedField(desc);
        Assertions.assertEquals("meta(couchbaseAlias).id", result);
    }

    @Test
    public void testPartialAndSecondaryIndexCollection() {
        CollectionNodeDescriptor desc = new CollectionNodeDescriptor(
                partialAndSecondaryIndexCollection, null, bucket.name(), scope.name(), null);
        String result = DataLoader.getIndexedField(desc);
        Assertions.assertEquals("att3", result);
    }

    @Test
    public void testSecondaryWithFirstInvalidFieldCollection() {
        CollectionNodeDescriptor desc = new CollectionNodeDescriptor(
                secondaryWithFirstInvalidFieldCollection, null, bucket.name(), scope.name(), null);
        String result = DataLoader.getIndexedField(desc);
        Assertions.assertEquals("att2", result);
    }

    @Test
    public void testSecondaryWithFirstInvalidFieldCollectionAndAnotherSecondary() {
        CollectionNodeDescriptor desc = new CollectionNodeDescriptor(
                secondaryWithFirstInvalidFieldCollectionAndAnotherSecondary, null, bucket.name(), scope.name(), null);
        String result = DataLoader.getIndexedField(desc);
        Assertions.assertEquals("att1", result);
    }

    @Test
    public void testSecondaryWithSingleFirstInvalid() {
        CollectionNodeDescriptor desc = new CollectionNodeDescriptor(
                secondaryWithSingleFirstInvalid, null, bucket.name(), scope.name(), null);
        String result = DataLoader.getIndexedField(desc);
        Assertions.assertNull(result);
    }

    @Test
    public void testSecondaryWithDesc() {
        CollectionNodeDescriptor desc = new CollectionNodeDescriptor(
                secondaryWithDesc, null, bucket.name(), scope.name(), null);
        String result = DataLoader.getIndexedField(desc);
        Assertions.assertEquals("att1", result);
    }


}
