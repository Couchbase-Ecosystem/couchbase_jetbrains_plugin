package com.couchbase.intellij.workbench;

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import org.intellij.sdk.language.SQLPPFileType;
import org.junit.Test;

public class StatementAtCaretTest extends LightPlatformCodeInsightFixture4TestCase {

    static {
        com.couchbase.intellij.testutil.AllowedThreads.allowJdkAwtThreads();
    }

    private String at(String textWithCaret) {
        myFixture.configureByText(SQLPPFileType.INSTANCE, textWithCaret);
        String statement = StatementAtCaret.find(myFixture.getFile(), myFixture.getCaretOffset());
        return statement == null ? null : statement.trim();
    }

    @Test
    public void caretInsideAStatementPicksThatStatement() {
        assertTrue(at("SELECT 1;\nSELECT <caret>2;\nSELECT 3;").startsWith("SELECT 2"));
    }

    @Test
    public void caretAtTheEndOfTheFilePicksTheLastStatement() {
        assertTrue(at("SELECT 1;\nSELECT 2;<caret>").startsWith("SELECT 2"));
        assertTrue(at("SELECT 1;\nSELECT 2;\n\n<caret>").startsWith("SELECT 2"));
    }

    @Test
    public void caretOnWhitespaceBetweenStatementsPicksThePrecedingOne() {
        assertTrue(at("SELECT 1;\n<caret>\nSELECT 2;").startsWith("SELECT 1"));
    }

    @Test
    public void caretBeforeTheFirstStatementPicksTheFirstOne() {
        assertTrue(at("<caret>\n\nSELECT 1;\nSELECT 2;").startsWith("SELECT 1"));
    }

    @Test
    public void fileWithoutStatementsGivesNothingInsteadOfCrashing() {
        assertNull(at("   <caret>  "));
    }
}
