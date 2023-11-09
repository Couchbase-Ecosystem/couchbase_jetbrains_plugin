package org.intellij.sdk.language.cblite;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import generated.cblite.GeneratedTypes;
import org.intellij.sdk.language.SqlppSyntaxHighlighter;
import org.jetbrains.annotations.NotNull;

public class SqlppLiteSyntaxHighlighter implements SyntaxHighlighter {
    @Override
    public @NotNull Lexer getHighlightingLexer() {
        return new SqlppLiteLexerAdapter();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        if (SqlppLiteTokenSets.KEYWORDS.contains(tokenType)) {
            return SqlppSyntaxHighlighter.KEY_KEYS;
        }
        if (tokenType.equals(GeneratedTypes.BOOLEAN_LITERAL)) {
            return SqlppSyntaxHighlighter.NUMBER_KEYS;
        }

        if (tokenType.equals(GeneratedTypes.FUNCTION_NAME)) {
            return SqlppSyntaxHighlighter.FUNCTION_KEYS;
        }

        if (SqlppLiteTokenSets.PUNCTUATION.contains(tokenType)) {
            return SqlppSyntaxHighlighter.SEPARATOR_KEYS;
        }


        if (SqlppLiteTokenSets.STRING_LITERALS.contains(tokenType)) {
            return SqlppSyntaxHighlighter.STRING_KEYS;
        }

        if (tokenType.equals(GeneratedTypes.NBR)) {
            return SqlppSyntaxHighlighter.NUMBER_KEYS;
        }

        if (SqlppLiteTokenSets.IDENTIFIERS.contains(tokenType)) {
            return SqlppSyntaxHighlighter.ESCAPED_IDENTIFIER_KEYS;
        }

        if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return SqlppSyntaxHighlighter.BAD_CHAR_KEYS;
        }

        return SqlppSyntaxHighlighter.EMPTY_KEYS;
    }
}
