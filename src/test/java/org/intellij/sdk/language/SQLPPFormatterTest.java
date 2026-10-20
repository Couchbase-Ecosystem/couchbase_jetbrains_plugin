package org.intellij.sdk.language;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SQLPPFormatterTest {

    @Test
    public void testEmptyInputGivesEmptyOutput() {
        assertEquals("", SQLPPFormatter.format(""));
    }

    @Test
    public void testSelectFromIsUppercasedAndBrokenOnLines() {
        assertEquals("SELECT *\nFROM t", SQLPPFormatter.format("select * from t"));
    }

    @Test
    public void testWhereClauseStartsNewLine() {
        assertEquals("SELECT *\nFROM t\nWHERE x = 1", SQLPPFormatter.format("select * from t where x = 1"));
    }

    @Test
    public void testAndIsIndentedUnderWhere() {
        assertEquals("SELECT *\nFROM t\nWHERE x = 1\n        AND y = 2",
                SQLPPFormatter.format("select * from t where x = 1 and y = 2"));
    }

    @Test
    public void testCommaSeparatedProjectionIsWrapped() {
        assertEquals("SELECT a,\n         b\nFROM t", SQLPPFormatter.format("select a, b from t"));
    }

    @Test
    public void testExistingWhitespaceAndNewlinesAreNormalised() {
        assertEquals(SQLPPFormatter.format("select * from t where x = 1"),
                SQLPPFormatter.format("select   *\n\tfrom    t\nwhere   x = 1"));
    }

    @Test
    public void testFormattingIsIdempotentForSimpleQuery() {
        String once = SQLPPFormatter.format("select * from t where x = 1 and y = 2");
        assertEquals(once, SQLPPFormatter.format(once));
    }

    @Test
    public void testQuotedLiteralWithCommaIsKept() {
        // Quote handling is ad hoc (the formatter splits on quotes); pin only that the literal survives intact.
        assertTrue(SQLPPFormatter.format("select 'a, b' from t").contains("'a, b'"));
    }
}
