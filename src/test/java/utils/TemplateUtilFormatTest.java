package utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Pure formatting helpers of TemplateUtil. The default locale is pinned so decimal separators are stable.
 */
public class TemplateUtilFormatTest {

    private Locale previous;

    @Before
    public void setUp() {
        previous = Locale.getDefault();
        Locale.setDefault(Locale.US);
    }

    @After
    public void tearDown() {
        Locale.setDefault(previous);
    }

    @Test
    public void fmtByteUsesMbThenGbAtOneKilobyteOfMb() {
        assertEquals("0.00 Mb", TemplateUtil.fmtByte(0L));
        assertEquals("1.00 Mb", TemplateUtil.fmtByte(1024L * 1024));
        assertEquals("1023.00 Mb", TemplateUtil.fmtByte(1023L * 1024 * 1024));
        assertEquals("1.00 Gb", TemplateUtil.fmtByte(1024L * 1024 * 1024));
        assertEquals("1.50 Gb", TemplateUtil.fmtByte(1536L * 1024 * 1024));
    }

    @Test
    public void fmtDoubleHasThreeDecimalsAndNullStaysNull() {
        assertNull(TemplateUtil.fmtDouble(null));
        assertEquals("1.235", TemplateUtil.fmtDouble(1.23456));
        assertEquals("2.000", TemplateUtil.fmtDouble(2.0));
    }

    @Test
    public void formatDurationSplitsSecondsIntoDaysHoursMinutes() {
        assertNull(TemplateUtil.formatDuration(null));
        assertEquals("0 days 0 hours 0 minutes", TemplateUtil.formatDuration(0L));
        // 1 day, 1 hour, 1 minute, 1 second (the seconds are dropped)
        assertEquals("1 days 1 hours 1 minutes", TemplateUtil.formatDuration(90061L));
    }

    @Test
    public void formatDurationDoesNotWrapLargerUnits() {
        // hours wrap at 24 into days, but days are not capped
        assertEquals("400 days 0 hours 0 minutes", TemplateUtil.formatDuration(400L * 86400));
    }

    @Test
    public void formatNumberGroupsThousandsWithSpaces() {
        assertEquals("0", TemplateUtil.formatNumber(0L));
        assertEquals("999", TemplateUtil.formatNumber(999L));
        assertEquals("1 234 567", TemplateUtil.formatNumber(1234567L));
    }
}
