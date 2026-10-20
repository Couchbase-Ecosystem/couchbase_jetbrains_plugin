package com.couchbase.intellij.workbench;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SQLPPQueryUtilsTest {

    @Test
    public void detectsOrderByCaseInsensitively() {
        assertTrue(SQLPPQueryUtils.hasOrderBy("SELECT * FROM b ORDER BY a"));
        assertTrue(SQLPPQueryUtils.hasOrderBy("select * from b order by a"));
    }

    @Test
    public void queryWithoutOrderByIsFalse() {
        assertFalse(SQLPPQueryUtils.hasOrderBy("SELECT * FROM b"));
    }

    @Test
    public void nullQueryIsFalse() {
        assertFalse(SQLPPQueryUtils.hasOrderBy(null));
    }

    @Test
    public void emptyQueryIsFalse() {
        assertFalse(SQLPPQueryUtils.hasOrderBy(""));
    }

    @Test
    public void orderByAcrossNewlineIsMissed() {
        // NOTE: possible bug: the split is on a single space only, so tabs or newlines between
        // ORDER and BY produce a token "ORDER\nBY" and ORDER is not detected.
        assertFalse(SQLPPQueryUtils.hasOrderBy("SELECT * FROM b ORDER\nBY a"));
    }

    @Test
    public void orderAsColumnNameWithSpacesIsDetected() {
        // Token-based check: any standalone token equal to ORDER counts, even outside an ORDER BY clause.
        assertTrue(SQLPPQueryUtils.hasOrderBy("SELECT order FROM b"));
    }
}
