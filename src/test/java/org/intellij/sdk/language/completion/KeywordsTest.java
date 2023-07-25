package org.intellij.sdk.language.completion;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import junit.framework.TestCase;
import org.junit.Test;


public class KeywordsTest extends LightPlatformCodeInsightFixture4TestCase {
    @Test
    public void testEmptyFile() {
        myFixture.configureByText("test.sqlpp", "");

        myFixture.type("");
        LookupElement[] items = myFixture.completeBasic();
        assertTrue(items.length > 0);
    }

    @Test
    public void testPostStatement() {
        myFixture.configureByText("test.sqlpp", "");
        myFixture.type("select * from test; ");
        LookupElement[] items = myFixture.completeBasic();
        assertTrue(items.length > 0);
    }

    @Test
    public void testExpression() {
        myFixture.configureByText("test.sqlpp", "");
        myFixture.type("select ");
        LookupElement[] items = myFixture.completeBasic();
        assertTrue(items.length > 0);
    }
}