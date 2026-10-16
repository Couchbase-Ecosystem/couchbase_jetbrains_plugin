package utils;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class CBConfigUtilTest {

    @Test
    public void versionsAboveSixPointSixAreSupported() {
        assertTrue(CBConfigUtil.isSupported("7.0"));
        assertTrue(CBConfigUtil.isSupported("10.0"));
        assertTrue(CBConfigUtil.isSupported("6.10"));
        assertTrue(CBConfigUtil.isSupported("6.6.1"));
    }

    @Test
    public void sixPointSixAndOlderAreNotSupported() {
        assertFalse(CBConfigUtil.isSupported("6.6"));
        assertFalse(CBConfigUtil.isSupported("6.5.9"));
        assertFalse(CBConfigUtil.isSupported("5.9"));
        assertFalse(CBConfigUtil.isSupported("6"));
    }

    @Test(expected = NumberFormatException.class)
    public void nonNumericVersionThrows() {
        CBConfigUtil.isSupported("abc");
    }

    @Test
    public void queryServiceDetection() {
        assertFalse(CBConfigUtil.hasQueryService(null));
        assertFalse(CBConfigUtil.hasQueryService(Collections.emptyList()));
        assertTrue(CBConfigUtil.hasQueryService(Arrays.asList("kv", "n1ql")));
        assertFalse(CBConfigUtil.hasQueryService(Arrays.asList("kv", "fts")));
    }

    @Test
    public void searchServiceDetection() {
        assertFalse(CBConfigUtil.hasSearchService(null));
        assertTrue(CBConfigUtil.hasSearchService(Arrays.asList("kv", "fts")));
        assertFalse(CBConfigUtil.hasSearchService(Arrays.asList("kv", "n1ql")));
    }
}
