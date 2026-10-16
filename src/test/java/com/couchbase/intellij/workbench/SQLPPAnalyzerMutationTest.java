package com.couchbase.intellij.workbench;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SQLPPAnalyzerMutationTest {

    @Test
    public void dataMutationKeywordsAreDetected() {
        assertTrue(SQLPPAnalyzer.isMutation("INSERT INTO b.s.c (KEY, VALUE) VALUES (\"k\", {})"));
        assertTrue(SQLPPAnalyzer.isMutation("upsert into b (key, value) values ('k', 1)"));
        assertTrue(SQLPPAnalyzer.isMutation("UPDATE b SET a = 1"));
        assertTrue(SQLPPAnalyzer.isMutation("delete from b where x = 1"));
        assertTrue(SQLPPAnalyzer.isMutation("REPLACE INTO b (KEY, VALUE) VALUES (\"k\", {})"));
    }

    @Test
    public void ddlAndGrantKeywordsAreDetected() {
        assertTrue(SQLPPAnalyzer.isMutation("CREATE INDEX idx ON b(a)"));
        assertTrue(SQLPPAnalyzer.isMutation("alter collection b.s.c"));
        assertTrue(SQLPPAnalyzer.isMutation("DROP SCOPE b.s"));
        assertTrue(SQLPPAnalyzer.isMutation("GRANT query_select ON b TO u"));
    }

    @Test
    public void selectIsNotAMutation() {
        assertFalse(SQLPPAnalyzer.isMutation("SELECT * FROM `travel-sample` LIMIT 10"));
        assertFalse(SQLPPAnalyzer.isMutation("select updated_at from b"));
    }

    @Test
    public void keywordInsideStringLiteralIsIgnored() {
        assertFalse(SQLPPAnalyzer.isMutation("SELECT * FROM b WHERE name = 'update'"));
        assertFalse(SQLPPAnalyzer.isMutation("SELECT \"delete\" FROM b"));
    }

    @Test
    public void keywordInsideBacktickIdentifierIsIgnored() {
        assertFalse(SQLPPAnalyzer.isMutation("SELECT `drop` FROM b"));
    }

    @Test
    public void emptyQueryIsNotAMutation() {
        assertFalse(SQLPPAnalyzer.isMutation(""));
    }

    @Test
    public void keywordGluedToSemicolonIsMissed() {
        // NOTE: possible bug: tokenizer splits on whitespace only, so "1;DROP" is one token and the
        // mutation is not detected. Pinned as current behavior.
        assertFalse(SQLPPAnalyzer.isMutation("SELECT 1;DROP INDEX i ON b"));
    }
}
