package org.intellij.sdk.language.completion;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;


public class KeywordsTest extends LightPlatformCodeInsightFixture4TestCase {
    private void assertCompletes(String text) {
        assertCompletes(text, "where");
    }

    public void assertNotCompletes(String text) {
        assertNotCompletes(text, "where");
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
    public void testEmptyFile() {
        assertCompletes("");
    }

    @Test
    public void testPostStatement() {
        assertCompletes("select * from test; ");
    }

    @Test
    public void testExpression() {
        assertNotCompletes("select ");
    }

    @Test
    public void testNotInPath() {
        assertNotCompletes("select * from a.");
    }

    @Test
    public void testNotInExpression() {
        assertNotCompletes("SELECT * from landmark.geo.accuracy where landmark.geo.accuracy = ");
    }

    @Test
    public void testInConditionAfterAnd() {
        assertNotCompletes("SELECT * from landmark.geo.accuracy where landmark.geo.accuracy = 100 + 300 and <caret> locatiom.geo.");
        assertNotCompletes("SELECT * from landmark.geo.accuracy where landmark.geo.accuracy = 100 + 300 and locatiom.geo.");
    }

    @Test
    public void testAliasPathSubfieldInFunction() {
        assertNotCompletes("select * from bucket-entity.scope-entity.collection-entity as test_alias where test_alias.parent-field.child-field  = lower(test_alias.parent-field.");
    }
}