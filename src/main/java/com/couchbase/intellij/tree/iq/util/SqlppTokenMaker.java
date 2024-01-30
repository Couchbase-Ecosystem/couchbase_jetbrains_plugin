package com.couchbase.intellij.tree.iq.util;

import com.intellij.lexer.FlexAdapter;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import generated.GeneratedTypes;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMaker;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;
import org.intellij.sdk.language.SqlppLexerAdapter;
import org.intellij.sdk.language.psi.SqlppTokenSets;

import javax.swing.text.Segment;
import java.util.Arrays;

public class SqlppTokenMaker extends AbstractTokenMaker {
    private static final TokenMap KEYWORDS = new TokenMap();

    static {
        Arrays.stream(SqlppTokenSets.KEYWORDS.getTypes()).forEach(tokenType -> KEYWORDS.put(tokenType.toString(), Token.RESERVED_WORD));
    }

    @Override
    public TokenMap getWordsToHighlight() {
        return KEYWORDS;
    }

    @Override
    public Token getTokenList(Segment text, int initialTokenType, int startOffset) {
        resetTokenList();
        char[] textToLex = text.toString().toCharArray();
        FlexAdapter lexer = new SqlppLexerAdapter();
        lexer.start(text);

        IElementType nextToken;
        while (null != (nextToken = lexer.getTokenType())) {
            int tokenType = Token.NULL;
            if (nextToken == TokenType.WHITE_SPACE) {
                tokenType = Token.WHITESPACE;
            } else if (nextToken == GeneratedTypes.ASTERISK) {
                tokenType = Token.IDENTIFIER;
            } else if (SqlppTokenSets.KEYWORDS.contains(nextToken)) {
                tokenType = Token.RESERVED_WORD;
            } else if (SqlppTokenSets.IDENTIFIERS.contains(nextToken)) {
                tokenType = Token.IDENTIFIER;
            } else if (SqlppTokenSets.LITERALS.contains(nextToken)) {
                if (nextToken == GeneratedTypes.NBR) {
                    tokenType = Token.LITERAL_NUMBER_FLOAT;
                } else if (nextToken == GeneratedTypes.STR) {
                    tokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
                } else if (nextToken == GeneratedTypes.BOOL) {
                    tokenType = Token.LITERAL_BOOLEAN;
                }
            } else if (SqlppTokenSets.COMMENTS.contains(nextToken)) {
                tokenType = Token.COMMENT_DOCUMENTATION;
            } else if (SqlppTokenSets.PUNCTUATION.contains(nextToken)) {
                tokenType = Token.SEPARATOR;
            } else if (nextToken == GeneratedTypes.BACKTICK) {
                tokenType = Token.IDENTIFIER;
            } else if (SqlppTokenSets.OPERATORS.contains(nextToken)) {
                tokenType = Token.OPERATOR;
            } else if (nextToken == GeneratedTypes.DQUOTE || nextToken == GeneratedTypes.QUOTE) {
                tokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
            } else if (nextToken == GeneratedTypes.STRING_CHAR) {
                tokenType = Token.LITERAL_CHAR;
            } else if (nextToken == GeneratedTypes.FUNCS || nextToken == GeneratedTypes.FUNCTION_NAME || nextToken == GeneratedTypes.FUNCTION_REF) {
                tokenType = Token.FUNCTION;
            } else {
                throw new RuntimeException("handle me: " + nextToken);
            }

            if (tokenType > 0) {
                addToken(textToLex, lexer.getTokenStart(), lexer.getTokenEnd() - 1, tokenType, lexer.getTokenStart() + startOffset);
            }
            lexer.advance();
        }
        if (firstToken == null) {
            addNullToken();
        }
        return firstToken;
    }
}
