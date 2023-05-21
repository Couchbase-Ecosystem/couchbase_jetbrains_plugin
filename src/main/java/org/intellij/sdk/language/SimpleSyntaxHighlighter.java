// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.language;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import generated.GeneratedTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class SimpleSyntaxHighlighter extends SyntaxHighlighterBase {

    public static final TextAttributesKey SEPARATOR =
            createTextAttributesKey("SIMPLE_SEPARATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey KEY =
            createTextAttributesKey("SIMPLE_KEY", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey VALUE =
            createTextAttributesKey("SIMPLE_VALUE", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey COMMENT =
            createTextAttributesKey("SIMPLE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);

    public static final TextAttributesKey NUMBER =
            createTextAttributesKey("SIMPLE_NUMBER", DefaultLanguageHighlighterColors.NUMBER);

    public static final TextAttributesKey ESCAPED_IDENTIFIER =
            createTextAttributesKey("SIMPLE_ESCAPED_IDENTIFIER", DefaultLanguageHighlighterColors.LABEL);

    public static final TextAttributesKey FUNCTION =
            createTextAttributesKey("SIMPLE_FUNCTION", DefaultLanguageHighlighterColors.GLOBAL_VARIABLE);

    public static final TextAttributesKey STRING =
            createTextAttributesKey("SIMPLE_STRING", DefaultLanguageHighlighterColors.STRING);

    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("SIMPLE_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);


    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] SEPARATOR_KEYS = new TextAttributesKey[]{SEPARATOR};
    private static final TextAttributesKey[] KEY_KEYS = new TextAttributesKey[]{KEY};
    private static final TextAttributesKey[] VALUE_KEYS = new TextAttributesKey[]{VALUE};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};

    private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{NUMBER};
    private static final TextAttributesKey[] ESCAPED_IDENTIFIER_KEYS = new TextAttributesKey[]{ESCAPED_IDENTIFIER};

    private static final TextAttributesKey[] FUNCTION_KEYS = new TextAttributesKey[]{FUNCTION};

    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new SimpleLexerAdapter();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        System.out.println(tokenType.toString());
        if (tokenType.equals(GeneratedTypes.SQL_KEYWORDS)) {
            return KEY_KEYS;
        }
        if (tokenType.equals(GeneratedTypes.BOOL)) {
            return NUMBER_KEYS;
        }

        if (tokenType.equals(GeneratedTypes.FUNCTION)) {
            return FUNCTION_KEYS;
        }

        if (tokenType.equals(GeneratedTypes.SIGNALS)
                || tokenType.equals(GeneratedTypes.IDENTIFIER)
                || tokenType.equals(ESCAPED_IDENTIFIER)) {
            return SEPARATOR_KEYS;
        }


        if (tokenType.equals(GeneratedTypes.STR)) {
            return STRING_KEYS;
        }

        if (tokenType.equals(GeneratedTypes.NBR)) {
            return NUMBER_KEYS;
        }

        if (tokenType.equals(GeneratedTypes.BACKTICKED_STRING)) {
            return ESCAPED_IDENTIFIER_KEYS;
        }

        if (tokenType.equals(GeneratedTypes.COMMENT)) {
            return COMMENT_KEYS;
        }

        if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        }
        return EMPTY_KEYS;
    }

}
