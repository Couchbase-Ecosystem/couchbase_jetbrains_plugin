package org.intellij.sdk.language.cblite;

import com.intellij.psi.TokenType;
import generated.cblite.GeneratedTypes;
import org.intellij.sdk.language.SqlppSyntaxHighlighter;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

public class SqlppLiteSyntaxHighlighterTest {

    private final SqlppLiteSyntaxHighlighter highlighter = new SqlppLiteSyntaxHighlighter();

    @Test
    public void testKeywordUsesKeyColor() {
        assertArrayEquals(SqlppSyntaxHighlighter.KEY_KEYS, highlighter.getTokenHighlights(GeneratedTypes.SELECT));
    }

    @Test
    public void testBooleanLiteralUsesNumberColor() {
        assertArrayEquals(SqlppSyntaxHighlighter.NUMBER_KEYS, highlighter.getTokenHighlights(GeneratedTypes.BOOLEAN_LITERAL));
    }

    @Test
    public void testFunctionNameUsesFunctionColor() {
        assertArrayEquals(SqlppSyntaxHighlighter.FUNCTION_KEYS, highlighter.getTokenHighlights(GeneratedTypes.FUNCTION_NAME));
    }

    @Test
    public void testPunctuationUsesSeparatorColor() {
        assertArrayEquals(SqlppSyntaxHighlighter.SEPARATOR_KEYS, highlighter.getTokenHighlights(GeneratedTypes.DOT));
    }

    @Test
    public void testStringLiteralUsesStringColor() {
        assertArrayEquals(SqlppSyntaxHighlighter.STRING_KEYS, highlighter.getTokenHighlights(GeneratedTypes.STRING_CHAR));
    }

    @Test
    public void testNumberUsesNumberColor() {
        assertArrayEquals(SqlppSyntaxHighlighter.NUMBER_KEYS, highlighter.getTokenHighlights(GeneratedTypes.NBR));
    }

    @Test
    public void testIdentifierUsesEscapedIdentifierColor() {
        assertArrayEquals(SqlppSyntaxHighlighter.ESCAPED_IDENTIFIER_KEYS, highlighter.getTokenHighlights(GeneratedTypes.IDENTIFIER));
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
    public void testLexerIsCreated() {
        assertNotNull(highlighter.getHighlightingLexer());
    }
}
