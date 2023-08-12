package org.intellij.sdk.language.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class SqlppPSIWrapper extends ASTWrapperPsiElement {
    public SqlppPSIWrapper(@NotNull ASTNode node) {
        super(node);
    }
}
