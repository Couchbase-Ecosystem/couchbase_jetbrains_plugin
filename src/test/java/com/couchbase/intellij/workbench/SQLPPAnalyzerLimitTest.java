package com.couchbase.intellij.workbench;

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SQLPPAnalyzerLimitTest extends LightPlatformCodeInsightFixture4TestCase {

    static {
        com.couchbase.intellij.testutil.AllowedThreads.allowJdkAwtThreads();
    }

    @Test
    public void nonSelectStatementIsTreatedAsLimited() {
        assertTrue(SQLPPAnalyzer.isLimited(getProject(), "INSERT INTO b (KEY, VALUE) VALUES (\"k\", {})"));
    }

    @Test
    public void selectWithLimitIsReportedAsNotLimited() {
        // NOTE: possible bug: a SELECT that really has LIMIT 10 returns false, so the LIMIT clause is not
        // detected (the lookup only checks direct children of the SelectStatement). Pinned as current behavior.
        assertFalse(SQLPPAnalyzer.isLimited(getProject(), "SELECT * FROM b LIMIT 10"));
    }

    @Test
    public void selectWithoutLimitIsNotLimited() {
        assertFalse(SQLPPAnalyzer.isLimited(getProject(), "SELECT * FROM b"));
    }
}
