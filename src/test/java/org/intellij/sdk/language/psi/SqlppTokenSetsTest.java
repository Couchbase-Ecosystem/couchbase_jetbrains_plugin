package org.intellij.sdk.language.psi;

import generated.GeneratedTypes;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SqlppTokenSetsTest {

    @Test
    public void testSelectIsKeyword() {
        assertTrue(SqlppTokenSets.KEYWORDS.contains(GeneratedTypes.SELECT));
        assertFalse(SqlppTokenSets.KEYWORDS.contains(GeneratedTypes.IDENTIFIER));
    }

    @Test
    public void testIdentifiersAndKeywordsAreDisjoint() {
        assertFalse(SqlppTokenSets.IDENTIFIERS.contains(GeneratedTypes.SELECT));
        assertTrue(SqlppTokenSets.IDENTIFIERS.contains(GeneratedTypes.ESCAPED_IDENTIFIER));
    }

    @Test
    public void testCommentsContainLineAndBlock() {
        assertTrue(SqlppTokenSets.COMMENTS.contains(GeneratedTypes.LINE_COMMENT));
        assertTrue(SqlppTokenSets.COMMENTS.contains(GeneratedTypes.BLOCK_COMMENT));
        assertFalse(SqlppTokenSets.COMMENTS.contains(GeneratedTypes.SELECT));
    }

    @Test
    public void testPunctuationIncludesSemicolonAndBraces() {
        assertTrue(SqlppTokenSets.PUNCTUATION.contains(GeneratedTypes.SEMICOLON));
        assertTrue(SqlppTokenSets.PUNCTUATION.contains(GeneratedTypes.LBRACE));
        assertTrue(SqlppTokenSets.PUNCTUATION.contains(GeneratedTypes.RBRACE));
        assertFalse(SqlppTokenSets.PUNCTUATION.contains(GeneratedTypes.EQUAL));
    }

    @Test
    public void testStringsAndLiterals() {
        assertTrue(SqlppTokenSets.STRINGS.contains(GeneratedTypes.STRING_CHAR));
        assertTrue(SqlppTokenSets.LITERALS.contains(GeneratedTypes.NBR));
        assertTrue(SqlppTokenSets.LITERALS.contains(GeneratedTypes.STR));
        assertFalse(SqlppTokenSets.STRINGS.contains(GeneratedTypes.NBR));
    }

    @Test
    public void testRefsAndOperators() {
        assertTrue(SqlppTokenSets.REFS.contains(GeneratedTypes.IDENTIFIER_REF));
        assertTrue(SqlppTokenSets.REFS.contains(GeneratedTypes.COLLECTION_REF));
        assertFalse(SqlppTokenSets.REFS.contains(GeneratedTypes.SELECT));
        assertTrue(SqlppTokenSets.OPERATORS.contains(GeneratedTypes.EQUAL));
        assertTrue(SqlppTokenSets.OPERATORS.contains(GeneratedTypes.LESSTHAN_OR_MORETHAN));
        assertFalse(SqlppTokenSets.OPERATORS.contains(GeneratedTypes.COMMA));
    }
}
