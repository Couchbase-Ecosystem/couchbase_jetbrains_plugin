package org.intellij.sdk.language.cblite;

import com.couchbase.intellij.tree.cblite.sqlppl.SQLPPLiteLanguage;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import generated.cblite.GeneratedParser;
import generated.cblite.GeneratedTypes;
import org.jetbrains.annotations.NotNull;

public class SqlppLiteParserDefinition implements ParserDefinition {
    public static final IFileElementType FILE = new IFileElementType(SQLPPLiteLanguage.INSTANCE);
    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new SqlppLiteLexerAdapter();
    }

    @Override
    public @NotNull PsiParser createParser(Project project) {
        return new GeneratedParser();
    }

    @Override
    public @NotNull IFileElementType getFileNodeType() {
        return FILE;
    }

    @Override
    public @NotNull TokenSet getCommentTokens() {
        return SqlppLiteTokenSets.COMMENTS;
    }

    @Override
    public @NotNull TokenSet getStringLiteralElements() {
        return SqlppLiteTokenSets.STRING_LITERALS;
    }

    @Override
    public @NotNull PsiElement createElement(ASTNode node) {
        return GeneratedTypes.Factory.createElement(node);
    }

    @Override
    public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new SqlppLiteFile(viewProvider);
    }
}