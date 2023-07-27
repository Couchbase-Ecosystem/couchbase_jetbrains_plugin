package org.intellij.sdk.language.completion;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.Callable;

public class FunctionsTest extends LightPlatformCodeInsightFixture4TestCase {
    @Test
    public void testInExpr() {
        myFixture.configureByText("test.sqlpp", "");
        myFixture.type("select name, ");
        LookupElement[] items = myFixture.completeBasic();
        assertTrue(items.length > 0);
        assertTrue(Arrays.stream(items).anyMatch(i -> "TOSTR".equals(i.getLookupString())));
    }

    @Test
    public void testInFunction() {
        myFixture.configureByText("test.sqlpp", "");
        myFixture.type("select * from test, TOOBJ(");
        LookupElement[] items = myFixture.completeBasic();
        assertTrue(items.length > 0);
        assertTrue(Arrays.stream(items).anyMatch(i -> "TOSTR".equals(i.getLookupString())));
    }
}