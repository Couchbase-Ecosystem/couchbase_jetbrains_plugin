package org.intellij.sdk.language.completion;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

public class FunctionsTest extends LightPlatformCodeInsightFixture4TestCase {
    private void assertCompletes(String text) {
        assertCompletes(text, "tostr");
    }

    public void assertNotCompletes(String text) {
        assertNotCompletes(text, "tostr");
    }

    private void assertCompletes(String text, String with) {
        assertCompletion(text, items -> assertTrue(
                Arrays.stream(items)
                        .filter(e -> !e.isCaseSensitive())
                        .map(LookupElement::getAllLookupStrings)
                        .flatMap(Collection::stream)
                        .anyMatch(with::equalsIgnoreCase)));
    }

    private void assertNotCompletes(String text, String with) {
        assertCompletion(text, items -> assertTrue(
                Arrays.stream(items)
                        .filter(e -> !e.isCaseSensitive())
                        .map(LookupElement::getAllLookupStrings)
                        .flatMap(Collection::stream)
                        .noneMatch(with::equalsIgnoreCase)));
    }

    private void assertCompletion(String text, Consumer<LookupElement[]> test) {
        if (!text.contains(CodeInsightTestFixture.CARET_MARKER)) {
            text += CodeInsightTestFixture.CARET_MARKER;
        }
        myFixture.setCaresAboutInjection(true);
        myFixture.configureByText("test.sqlpp", text);

        LookupElement[] items = myFixture.completeBasic();
        test.accept(items);
    }

    @Test
    public void testInExpr() {
        assertCompletes("select name, ");
    }

    @Test
    public void testNotAfterDot() {
        assertNotCompletes("select name.");
    }

    @Test
    public void testInFunction() {
        assertCompletes("select * from test, TOOBJ(");
    }

    @Test
    public void testInCondition() {
        assertCompletes("SELECT * from landmark.geo.accuracy where landmark.geo.accuracy = ");
    }

    @Test
    public void testAfterFailedCondition() {
        assertCompletes("SELECT * from landmark.geo.accuracy where landmark.geo.accuracy = 100 +");
    }

    @Test
    public void testInConditionAfterAnd() {
        assertCompletes("SELECT * from landmark.geo.accuracy where landmark.geo.accuracy = 100 + 300 and <caret> locatiom.geo.");
        assertNotCompletes("SELECT * from landmark.geo.accuracy where landmark.geo.accuracy = 100 + 300 and locatiom.geo.");
    }

    @Test
    public void testAliasPathSubfieldInFunction() {
        assertNotCompletes("select * from bucket-entity.scope-entity.collection-entity as test_alias where test_alias.parent-field.child-field  = lower(test_alias.parent-field.");
    }
}