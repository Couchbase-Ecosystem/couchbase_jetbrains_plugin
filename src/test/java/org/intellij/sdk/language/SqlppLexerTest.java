package org.intellij.sdk.language;

import com.intellij.lexer.Lexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import generated.GeneratedTypes;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SqlppLexerTest {

    private static List<IElementType> tokens(String text) {
        Lexer lexer = new SqlppLexerAdapter();
        lexer.start(text);
        List<IElementType> out = new ArrayList<>();
        while (lexer.getTokenType() != null) {
            if (lexer.getTokenType() != TokenType.WHITE_SPACE) {
                out.add(lexer.getTokenType());
            }
            lexer.advance();
        }
        return out;
    }

    @Test
    public void testSelectStatementTokens() {
        assertEquals(
                java.util.Arrays.asList(GeneratedTypes.SELECT, GeneratedTypes.ASTERISK, GeneratedTypes.FROM, GeneratedTypes.IDENTIFIER),
                tokens("select * from t"));
    }

    @Test
    public void testKeywordsAreCaseInsensitive() {
        assertEquals(tokens("select * from t"), tokens("SELECT * FROM t"));
    }

    // NOTE: possible bug: the lexer treats "//" as a line comment but SqlppCommenter uses the "#" prefix.
    @Test
    public void testLineCommentIsToken() {
        List<IElementType> t = tokens("select 1 // note");
        assertTrue(t.contains(GeneratedTypes.LINE_COMMENT));
    }

    @Test
    public void testSemicolonIsPunctuationToken() {
        assertTrue(tokens("select 1;").contains(GeneratedTypes.SEMICOLON));
    }

    @Test
    public void testEmptyInputHasNoTokens() {
        assertEquals(0, tokens("").size());
    }
}
