package org.intellij.sdk.language.cblite;

import generated.cblite.GeneratedTypes;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SqlppLiteTokenSetsTest {

    @Test
    public void testCommentsAreEmpty() {
        assertEquals(0, SqlppLiteTokenSets.COMMENTS.getTypes().length);
    }

    @Test
    public void testKeywordMembership() {
        assertTrue(SqlppLiteTokenSets.KEYWORDS.contains(GeneratedTypes.SELECT));
        assertTrue(SqlppLiteTokenSets.KEYWORDS.contains(GeneratedTypes.WHERE));
        assertFalse(SqlppLiteTokenSets.KEYWORDS.contains(GeneratedTypes.IDENTIFIER));
    }

    @Test
    public void testIdentifiersAreNotKeywords() {
        assertTrue(SqlppLiteTokenSets.IDENTIFIERS.contains(GeneratedTypes.IDENTIFIER));
        assertTrue(SqlppLiteTokenSets.IDENTIFIERS.contains(GeneratedTypes.ESCAPED_IDENTIFIER));
        assertFalse(SqlppLiteTokenSets.KEYWORDS.contains(GeneratedTypes.ESCAPED_IDENTIFIER));
    }

    @Test
    public void testPunctuationAndStrings() {
        assertTrue(SqlppLiteTokenSets.PUNCTUATION.contains(GeneratedTypes.COMMA));
        assertFalse(SqlppLiteTokenSets.PUNCTUATION.contains(GeneratedTypes.SELECT));
        assertTrue(SqlppLiteTokenSets.STRING_LITERALS.contains(GeneratedTypes.ESCAPED_QUOTE));
        assertFalse(SqlppLiteTokenSets.STRING_LITERALS.contains(GeneratedTypes.COMMA));
    }
}
