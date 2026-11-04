package com.couchbase.intellij.tools.cbmigrate;

import com.couchbase.intellij.tools.CBTools;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class CBMigrateDynamoCommandBuilderTest {

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
        List<String> cmd = new CBMigrateDynamo.CBMigrateDynamoCommandBuilder().build();

        Assert.assertEquals(Arrays.asList(TOOL_PATH, "dynamodb"), cmd);
    }

    @Test
    public void awsOptionsAreEmittedWithExpectedFlags() {
        List<String> cmd = new CBMigrateDynamo.CBMigrateDynamoCommandBuilder()
                .setAwsProfile("dev")
                .setAwsRegion("eu-west-1")
                .setDynamoTable("orders")
                .setAwsAccessKey("AKIA")
                .setAwsAccessKeySecret("SECRET")
                .build();

        Assert.assertEquals(Arrays.asList(
                TOOL_PATH, "dynamodb",
                "--aws-profile", "dev",
                "--aws-region", "eu-west-1",
                "--dynamodb-table-name", "orders",
                "--aws-access-key-id", "AKIA",
                "--aws-secret-access-key", "SECRET"), cmd);
    }

    @Test
    public void couchbaseOptionsAreEmittedInOrder() {
        List<String> cmd = new CBMigrateDynamo.CBMigrateDynamoCommandBuilder()
                .setCBCluster("couchbase://c")
                .setCBUsername("u")
                .setCBPassword("p")
                .setCBBucket("b")
                .setCBScope("s")
                .setCBCollection("c")
                .setCBGenerateKey("#UUID#")
                .build();

        Assert.assertEquals(Arrays.asList(
                TOOL_PATH, "dynamodb",
                "--cb-cluster", "couchbase://c",
                "--cb-username", "u",
                "--cb-password", "p",
                "--cb-bucket", "b",
                "--cb-scope", "s",
                "--cb-collection", "c",
                "--cb-generate-key", "#UUID#"), cmd);
    }

    @Test
    public void copyIndexesTakesExplicitValue() {
        List<String> cmd = new CBMigrateDynamo.CBMigrateDynamoCommandBuilder().setCopyIndexes("true").build();

        Assert.assertEquals(Arrays.asList("--copy-indexes", "true"), cmd.subList(cmd.size() - 2, cmd.size()));
    }

    @Test
    public void verboseUsesDebugFlagNotVerbose() {
        List<String> cmd = new CBMigrateDynamo.CBMigrateDynamoCommandBuilder().setVerbose().build();

        Assert.assertEquals("--debug", cmd.get(cmd.size() - 1));
        Assert.assertFalse(cmd.contains("--verbose"));
    }

    @Test
    public void sslNoVerifyAddsFlagWithLiteralTrueValue() {
        List<String> cmd = new CBMigrateDynamo.CBMigrateDynamoCommandBuilder().setSslNoVerify().build();

        Assert.assertEquals(Arrays.asList("--cb-no-ssl-verify", "true"), cmd.subList(cmd.size() - 2, cmd.size()));
    }

    // NOTE: possible bug: null values are appended as null elements without validation.
    @Test
    public void nullAwsRegionIsAppendedAsNullElement() {
        List<String> cmd = new CBMigrateDynamo.CBMigrateDynamoCommandBuilder().setAwsRegion(null).build();

        Assert.assertEquals("--aws-region", cmd.get(cmd.size() - 2));
        Assert.assertNull(cmd.get(cmd.size() - 1));
    }
}
