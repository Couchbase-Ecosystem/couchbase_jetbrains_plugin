package com.couchbase.intellij.tools;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Characterization tests for the private CBImport.partLastLine parser that reads the
 * metadata trailer written by CBExport at the end of a dataset file.
 */
public class CBImportMetadataParserTest {

    private static CBImport.ExportedMetadata parse(String line) throws Exception {
        Method m = CBImport.class.getDeclaredMethod("partLastLine", String.class);
        m.setAccessible(true);
        try {
            return (CBImport.ExportedMetadata) m.invoke(null, line);
        } catch (InvocationTargetException e) {
            throw (Exception) e.getCause();
        }
    }

    @Test
    public void parsesBucketScopeCollectionsAndIndexes() throws Exception {
        CBImport.ExportedMetadata meta = parse("bucket:travel;scope:inventory;col:hotel,airline;idx:hotel|ZW1w#airline|ZmFy");

        Assert.assertEquals("travel", meta.getBucket());
        Assert.assertEquals("inventory", meta.getScope());
        Assert.assertEquals(Arrays.asList("hotel", "airline"), meta.getCollections());

        Map<String, String> expected = new HashMap<>();
        expected.put("hotel", "ZW1w");
        expected.put("airline", "ZmFy");
        Assert.assertEquals(expected, meta.getIndexes());
    }

    @Test
    public void singleCollectionWithoutCommaIsAddedAsOneEntry() throws Exception {
        CBImport.ExportedMetadata meta = parse("bucket:b;scope:s;col:only");

        Assert.assertEquals(Collections.singletonList("only"), meta.getCollections());
        Assert.assertTrue(meta.getIndexes().isEmpty());
    }

    @Test
    public void singleIndexWithoutHashSeparatorIsParsed() throws Exception {
        CBImport.ExportedMetadata meta = parse("bucket:b;scope:s;col:c;idx:c|ZW1w");

        Assert.assertEquals(Collections.singletonMap("c", "ZW1w"), meta.getIndexes());
    }

    // NOTE: possible bug: the single-index branch (no '#') indexes ix[1] without checking the length, so an
    // idx entry without a '|' separator throws ArrayIndexOutOfBoundsException, while the multi-index branch
    // silently skips such entries.
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void singleIndexWithoutPipeThrows() throws Exception {
        parse("bucket:b;scope:s;col:c;idx:brokenentry");
    }

    @Test
    public void multiIndexSkipsEntriesWithoutPipe() throws Exception {
        CBImport.ExportedMetadata meta = parse("bucket:b;scope:s;col:c,d;idx:c|ZW1w#brokenentry#d|ZmFy");

        Map<String, String> expected = new HashMap<>();
        expected.put("c", "ZW1w");
        expected.put("d", "ZmFy");
        Assert.assertEquals(expected, meta.getIndexes());
    }

    @Test
    public void missingFieldsAreNull() throws Exception {
        CBImport.ExportedMetadata meta = parse("col:c");

        Assert.assertNull(meta.getBucket());
        Assert.assertNull(meta.getScope());
        Assert.assertEquals(Collections.singletonList("c"), meta.getCollections());
        Assert.assertTrue(meta.getIndexes().isEmpty());
    }

    @Test
    public void unknownPrefixesAreIgnored() throws Exception {
        CBImport.ExportedMetadata meta = parse("version:9;bucket:b;scope:s;col:c");

        Assert.assertEquals("b", meta.getBucket());
        Assert.assertEquals("s", meta.getScope());
        Assert.assertEquals(Collections.singletonList("c"), meta.getCollections());
    }

    // NOTE: possible bug: values are read with split(":")[1], so any value containing ':' is silently
    // truncated at the second colon (e.g. a scope named "a:b" becomes "a").
    @Test
    public void valueContainingColonIsTruncated() throws Exception {
        CBImport.ExportedMetadata meta = parse("bucket:b;scope:a:b");

        Assert.assertEquals("a", meta.getScope());
    }

    @Test
    public void lastOccurrenceWinsForRepeatedScope() throws Exception {
        CBImport.ExportedMetadata meta = parse("scope:first;scope:second");

        Assert.assertEquals("second", meta.getScope());
    }

    @Test
    public void exportedMetadataGettersReturnConstructorValues() {
        CBImport.ExportedMetadata meta = new CBImport.ExportedMetadata("b", "s",
                Collections.singletonList("c"), Collections.singletonMap("c", "idx"));

        Assert.assertEquals("b", meta.getBucket());
        Assert.assertEquals("s", meta.getScope());
        Assert.assertEquals(Collections.singletonList("c"), meta.getCollections());
        Assert.assertEquals("idx", meta.getIndexes().get("c"));
    }
}
