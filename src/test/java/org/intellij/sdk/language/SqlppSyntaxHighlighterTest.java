package org.intellij.sdk.language;

import com.intellij.psi.TokenType;
import generated.GeneratedTypes;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

public class SqlppSyntaxHighlighterTest {

    private final SqlppSyntaxHighlighter highlighter = new SqlppSyntaxHighlighter();

    @Test
    public void testKeywordUsesKeyColor() {
        assertArrayEquals(SqlppSyntaxHighlighter.KEY_KEYS, highlighter.getTokenHighlights(GeneratedTypes.SELECT));
    }

    @Test
    public void testBooleanUsesNumberColor() {
        assertArrayEquals(SqlppSyntaxHighlighter.NUMBER_KEYS, highlighter.getTokenHighlights(GeneratedTypes.BOOL));
    }

    @Test
    public void testNumberUsesNumberColor() {
        assertArrayEquals(SqlppSyntaxHighlighter.NUMBER_KEYS, highlighter.getTokenHighlights(GeneratedTypes.NBR));
    }

    @Test
    public void testFunctionUsesFunctionColor() {
        // NOTE: possible bug: FUNCTION is a member of SqlppTokenSets.KEYWORDS, which is checked first, so the
        // dedicated FUNCTION_KEYS branch in getTokenHighlights is unreachable and functions get the keyword color.
        assertArrayEquals(SqlppSyntaxHighlighter.KEY_KEYS, highlighter.getTokenHighlights(GeneratedTypes.FUNCTION));
    }

    @Test
    public void testPunctuationUsesSeparatorColor() {
        assertArrayEquals(SqlppSyntaxHighlighter.SEPARATOR_KEYS, highlighter.getTokenHighlights(GeneratedTypes.COMMA));
        assertArrayEquals(SqlppSyntaxHighlighter.SEPARATOR_KEYS, highlighter.getTokenHighlights(GeneratedTypes.SEMICOLON));
    }

    @Test
    public void testStringCharUsesStringColor() {
        assertArrayEquals(SqlppSyntaxHighlighter.STRING_KEYS, highlighter.getTokenHighlights(GeneratedTypes.STRING_CHAR));
    }

    @Test
    public void testIdentifiersUseEscapedIdentifierColor() {
        assertArrayEquals(SqlppSyntaxHighlighter.ESCAPED_IDENTIFIER_KEYS, highlighter.getTokenHighlights(GeneratedTypes.IDENTIFIER));
        assertArrayEquals(SqlppSyntaxHighlighter.ESCAPED_IDENTIFIER_KEYS, highlighter.getTokenHighlights(GeneratedTypes.ESCAPED_IDENTIFIER));
    }

    @Test
    public void testCommentUsesCommentColor() {
        assertArrayEquals(SqlppSyntaxHighlighter.COMMENT_KEYS, highlighter.getTokenHighlights(GeneratedTypes.LINE_COMMENT));
        assertArrayEquals(SqlppSyntaxHighlighter.COMMENT_KEYS, highlighter.getTokenHighlights(GeneratedTypes.BLOCK_COMMENT));
    }

    @Test
    public void testBadCharacterUsesBadCharacterColor() {
        assertArrayEquals(SqlppSyntaxHighlighter.BAD_CHAR_KEYS, highlighter.getTokenHighlights(TokenType.BAD_CHARACTER));
    }

    @Test
    public void testUnknownTokenHasNoHighlight() {
        assertArrayEquals(SqlppSyntaxHighlighter.EMPTY_KEYS, highlighter.getTokenHighlights(GeneratedTypes.EQUAL));
    }

    @Test
    public void testStrElementIsNotHighlightedAsString() {
        // NOTE: possible bug: STR is in the parser's LITERALS set but not in STRINGS, so a STR token
        // gets no highlight here. Pinned as current behaviour.
        assertArrayEquals(SqlppSyntaxHighlighter.EMPTY_KEYS, highlighter.getTokenHighlights(GeneratedTypes.STR));
    }

    @Test
    public void testLexerIsCreated() {
        assertNotNull(highlighter.getHighlightingLexer());
    }
}
