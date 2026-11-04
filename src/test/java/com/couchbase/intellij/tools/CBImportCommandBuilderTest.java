package com.couchbase.intellij.tools;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class CBImportCommandBuilderTest {

    private static final String TOOL_PATH = "/opt/cbimport";

    @Before
    public void setUp() {
        CBTools.getTool(CBTools.Type.CB_IMPORT).setPath(TOOL_PATH);
    }

    @After
    public void tearDown() {
        CBTools.getTool(CBTools.Type.CB_IMPORT).setPath(null);
    }

    private static CBImportCommandBuilder base(String format) {
        return new CBImportCommandBuilder(format, "couchbase://localhost", "Administrator", "secret");
    }

    @Test
    public void constructorAddsToolPathFormatAndConnectionArgs() {
        List<String> cmd = base("json").build();

        Assert.assertEquals(Arrays.asList(
                TOOL_PATH, "json", "--no-ssl-verify",
                "--cluster", "couchbase://localhost",
                "--username", "Administrator",
                "--password", "secret"), cmd);
    }

    @Test
    public void constructorWithUnsetToolPathAddsNullFirstElement() {
        CBTools.getTool(CBTools.Type.CB_IMPORT).setPath(null);

        List<String> cmd = base("csv").build();

        Assert.assertNull(cmd.get(0));
        Assert.assertEquals("csv", cmd.get(1));
    }

    @Test
    public void setBucketAndDatasetPrefixFileScheme() {
        List<String> cmd = base("json")
                .setBucket("travel")
                .setDataset("/tmp/data.json")
                .build();

        Assert.assertEquals(Arrays.asList("--bucket", "travel", "--dataset", "file:///tmp/data.json"),
                cmd.subList(cmd.size() - 4, cmd.size()));
    }

    @Test
    public void setFormatAndFieldSeparatorSkippedWhenNull() {
        List<String> before = base("csv").build();
        int size = before.size();

        List<String> cmd = base("csv").setFormat(null).setFieldSeparator(null).setGeneratorDelimiter(null).build();

        Assert.assertEquals(size, cmd.size());
    }

    @Test
    public void setFormatAndFieldSeparatorAddedWhenPresent() {
        List<String> cmd = base("csv").setFormat("lines").setFieldSeparator("|").build();

        Assert.assertTrue(cmd.containsAll(Arrays.asList("--format", "lines", "--field-separator", "|")));
        Assert.assertEquals(Arrays.asList("--format", "lines", "--field-separator", "|"),
                cmd.subList(cmd.size() - 4, cmd.size()));
    }

    @Test
    public void setGeneratorDelimiterAddedWhenPresent() {
        List<String> cmd = base("json").setGeneratorDelimiter("::").build();

        Assert.assertEquals(Arrays.asList("--generator-delimiter", "::"), cmd.subList(cmd.size() - 2, cmd.size()));
    }

    @Test
    public void inferTypesOnlyForCsv() {
        Assert.assertTrue(base("csv").setInferTypes(CBImportCommandBuilder.CSV_FILE_FORMAT).build()
                .contains("--infer-types"));
        Assert.assertFalse(base("json").setInferTypes(CBImportCommandBuilder.JSON_FILE_FORMAT).build()
                .contains("--infer-types"));
    }

    // NOTE: possible bug: setInferTypes(null) throws NullPointerException because fileFormat is dereferenced
    // without a null check, unlike the other optional setters.
    @Test(expected = NullPointerException.class)
    public void inferTypesNullThrows() {
        base("csv").setInferTypes(null);
    }

    @Test
    public void scopeCollectionAndGenerateKeyAlwaysAdded() {
        List<String> cmd = base("json")
                .setScopeCollectionExp("inv.hotel")
                .setGenerateKey("#UUID#")
                .build();

        Assert.assertEquals(Arrays.asList("--scope-collection-exp", "inv.hotel", "--generate-key", "#UUID#"),
                cmd.subList(cmd.size() - 4, cmd.size()));
    }

    @Test
    public void skipDocsUsesDocsFlagForJsonAndRowsFlagForCsv() {
        List<String> json = base("json").setSkipDocsOrRows("5", "json").build();
        List<String> csv = base("csv").setSkipDocsOrRows("7", "csv").build();

        Assert.assertEquals(Arrays.asList("--skip-docs", "5"), json.subList(json.size() - 2, json.size()));
        Assert.assertEquals(Arrays.asList("--skip-rows", "7"), csv.subList(csv.size() - 2, csv.size()));
    }

    @Test
    public void skipDocsIgnoredWhenValueNullOrEmpty() {
        int size = base("json").build().size();

        Assert.assertEquals(size, base("json").setSkipDocsOrRows(null, "json").build().size());
        Assert.assertEquals(size, base("json").setSkipDocsOrRows("", "json").build().size());
    }

    // NOTE: possible bug: for an unknown flag (neither json nor csv) the value is still appended without any
    // preceding option name, which produces a dangling positional argument for cbimport.
    @Test
    public void skipDocsUnknownFormatAddsDanglingValue() {
        List<String> cmd = base("xml").setSkipDocsOrRows("3", "xml").build();

        Assert.assertEquals("3", cmd.get(cmd.size() - 1));
        Assert.assertFalse(cmd.contains("--skip-docs"));
        Assert.assertFalse(cmd.contains("--skip-rows"));
    }

    @Test
    public void limitUsesLimitDocsOrLimitRows() {
        List<String> json = base("json").setLimitDocsOrRows("10", "json").build();
        List<String> csv = base("csv").setLimitDocsOrRows("20", "csv").build();

        Assert.assertEquals(Arrays.asList("--limit-docs", "10"), json.subList(json.size() - 2, json.size()));
        Assert.assertEquals(Arrays.asList("--limit-rows", "20"), csv.subList(csv.size() - 2, csv.size()));
    }

    @Test
    public void limitIgnoredWhenValueNullOrEmpty() {
        int size = base("csv").build().size();

        Assert.assertEquals(size, base("csv").setLimitDocsOrRows(null, "csv").build().size());
        Assert.assertEquals(size, base("csv").setLimitDocsOrRows("", "csv").build().size());
    }

    @Test
    public void ignoreFieldsTrimsEachEntry() {
        List<String> cmd = base("json").setIgnoreFields(" a , b,c ").build();

        Assert.assertEquals(Arrays.asList("--ignore-fields", "a,b,c"), cmd.subList(cmd.size() - 2, cmd.size()));
    }

    @Test
    public void ignoreFieldsEmptyStringAddsNothing() {
        int size = base("json").build().size();

        Assert.assertEquals(size, base("json").setIgnoreFields("").build().size());
    }

    @Test
    public void ignoreFieldsEmptyEntriesArePreserved() {
        // Empty middle entries are kept; a trailing comma is dropped by String.split.
        List<String> middle = base("json").setIgnoreFields("a,,b").build();
        List<String> trailing = base("json").setIgnoreFields("a,").build();

        Assert.assertEquals("a,,b", middle.get(middle.size() - 1));
        Assert.assertEquals("a", trailing.get(trailing.size() - 1));
    }

    @Test
    public void ignoreFieldsWhitespaceOnlyBecomesEmptyValue() {
        List<String> cmd = base("json").setIgnoreFields("   ").build();

        Assert.assertEquals(Arrays.asList("--ignore-fields", ""), cmd.subList(cmd.size() - 2, cmd.size()));
    }

    @Test(expected = NullPointerException.class)
    public void ignoreFieldsNullThrows() {
        base("json").setIgnoreFields(null);
    }

    @Test
    public void threadsAddedAsString() {
        List<String> cmd = base("json").setThreads(8).build();

        Assert.assertEquals(Arrays.asList("--threads", "8"), cmd.subList(cmd.size() - 2, cmd.size()));
    }

    @Test
    public void setVerboseAppendsFlag() {
        CBImportCommandBuilder builder = base("json");
        builder.setVerbose();

        List<String> cmd = builder.build();
        Assert.assertEquals("--verbose", cmd.get(cmd.size() - 1));
    }

    @Test
    public void buildReturnsLiveListSoLaterCallsAreVisible() {
        CBImportCommandBuilder builder = base("json");
        List<String> first = builder.build();
        int before = first.size();

        builder.setBucket("b");

        Assert.assertEquals(before + 2, first.size());
    }
}
