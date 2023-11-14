package org.intellij.sdk.language.cblite;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.FlexLexer;
import com.intellij.lexer.Lexer;
import org.jetbrains.annotations.NotNull;

public class SqlppLiteLexerAdapter extends FlexAdapter {
    public SqlppLiteLexerAdapter() {
         super(new SqlppLiteLexer(null));
    }
}