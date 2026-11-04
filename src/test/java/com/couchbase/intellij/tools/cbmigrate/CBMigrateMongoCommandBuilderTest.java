package com.couchbase.intellij.tools.cbmigrate;

import com.couchbase.intellij.tools.CBTools;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class CBMigrateMongoCommandBuilderTest {

    private static final String TOOL_PATH = "/opt/cbmigrate";

    @Before
    public void setUp() {
        CBTools.getTool(CBTools.Type.CBMIGRATE).setPath(TOOL_PATH);
    }

    @After
    public void tearDown() {
        CBTools.getTool(CBTools.Type.CBMIGRATE).setPath(null);
    }

    @Test
    public void emptyBuilderHasOnlyToolAndSubcommand() {
        List<String> cmd = new CBMigrateMongo.CBMigrateCommandBuilder().build();

        Assert.assertEquals(Arrays.asList(TOOL_PATH, "mongo"), cmd);
    }

    @Test
    public void fullCommandKeepsOptionOrder() {
        List<String> cmd = new CBMigrateMongo.CBMigrateCommandBuilder()
                .setMongoDBURI("mongodb://h:27017")
                .setMongoDBDatabase("shop")
                .setMongoDBCollection("orders")
                .setCBCluster("couchbase://c")
                .setCBUsername("u")
                .setCBPassword("p")
                .setCBBucket("b")
                .setCBScope("_default")
                .setCBCollection("orders")
                .setCBGenerateKey("#UUID#")
                .setVerbose()
                .build();

        Assert.assertEquals(Arrays.asList(
                TOOL_PATH, "mongo",
                "--mongodb-uri", "mongodb://h:27017",
                "--mongodb-database", "shop",
                "--mongodb-collection", "orders",
                "--cb-cluster", "couchbase://c",
                "--cb-username", "u",
                "--cb-password", "p",
                "--cb-bucket", "b",
                "--cb-scope", "_default",
                "--cb-collection", "orders",
                "--cb-generate-key", "#UUID#",
                "--verbose"), cmd);
    }

    @Test
    public void valuesWithSpacesAndQuotesAreNotQuotedOrSplit() {
        // Values are handed to ProcessBuilder as-is; no shell quoting is applied.
        List<String> cmd = new CBMigrateMongo.CBMigrateCommandBuilder()
                .setCBPassword("pa ss\"w'rd")
                .build();

        Assert.assertEquals(Arrays.asList("--cb-password", "pa ss\"w'rd"), cmd.subList(cmd.size() - 2, cmd.size()));
    }

    // NOTE: possible bug: null values are appended as null list elements (no guard), which makes
    // ProcessBuilder fail at start time instead of giving a clear validation error.
    @Test
    public void nullValueIsAppendedAsNullElement() {
        List<String> cmd = new CBMigrateMongo.CBMigrateCommandBuilder().setCBBucket(null).build();

        Assert.assertEquals("--cb-bucket", cmd.get(cmd.size() - 2));
        Assert.assertNull(cmd.get(cmd.size() - 1));
    }

    @Test
    public void unsetToolPathLeavesNullFirstElement() {
        CBTools.getTool(CBTools.Type.CBMIGRATE).setPath(null);

        List<String> cmd = new CBMigrateMongo.CBMigrateCommandBuilder().build();

        Assert.assertNull(cmd.get(0));
        Assert.assertEquals("mongo", cmd.get(1));
    }
}
