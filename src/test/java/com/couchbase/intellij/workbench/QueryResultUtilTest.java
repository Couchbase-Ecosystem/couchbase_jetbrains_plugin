package com.couchbase.intellij.workbench;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class QueryResultUtilTest {

    // DecimalFormat is locale-sensitive, so pin the locale for deterministic decimal separators.
    private Locale previous;

    @Before
    public void pinLocale() {
        previous = Locale.getDefault();
        Locale.setDefault(Locale.US);
    }

    @After
    public void restoreLocale() {
        Locale.setDefault(previous);
    }

    @Test
    public void zeroBytes() {
        assertEquals("0 Bytes", QueryResultUtil.getSizeText(0));
    }

    @Test
    public void bytesBelowOneKilobyteStayInBytes() {
        assertEquals("1023 Bytes", QueryResultUtil.getSizeText(1023));
    }

    @Test
    public void exactlyOneKilobyte() {
        assertEquals("1.00 KB", QueryResultUtil.getSizeText(1024));
    }

    @Test
    public void fractionalKilobytesUseTwoDecimals() {
        assertEquals("1.50 KB", QueryResultUtil.getSizeText(1536));
    }

    @Test
    public void largeValuesStayInKilobytes() {
        // no MB unit exists: 1 MiB is shown as 1024.00 KB
        assertEquals("1024.00 KB", QueryResultUtil.getSizeText(1024L * 1024L));
    }
}
