package com.couchbase.intellij.workbench;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import generated.psi.Statement;
import org.jetbrains.annotations.Nullable;

/** Resolves which SQL++ statement Explain and Advise should work on. */
public final class StatementAtCaret {
    private StatementAtCaret() {}

    /**
     * The statement under {@code offset}. On whitespace between statements, or at the end of the file, the closest
     * statement before the caret; before the first statement, the first one. Null only for a file without statements.
     */
    public static @Nullable String find(PsiFile psi, int offset) {
        return ApplicationManager.getApplication().runReadAction((Computable<String>) () -> {
            PsiElement focused = psi.findElementAt(offset);
            Statement statement = focused == null ? null : PsiTreeUtil.getTopmostParentOfType(focused, Statement.class);
            if (statement == null) {
                statement = closestTo(psi, offset);
            }
            return statement == null ? null : statement.getText();
        });
    }

    private static @Nullable Statement closestTo(PsiFile psi, int offset) {
        Statement before = null;
        Statement first = null;
        for (Statement statement : PsiTreeUtil.findChildrenOfType(psi, Statement.class)) {
            if (PsiTreeUtil.getParentOfType(statement, Statement.class) != null) {
                continue; // a subquery; only whole statements run
            }
            int start = statement.getTextRange().getStartOffset();
            if (first == null || start < first.getTextRange().getStartOffset()) {
                first = statement;
            }
            if (start <= offset && (before == null || start > before.getTextRange().getStartOffset())) {
                before = statement;
            }
        }
        return before != null ? before : first;
    }
}
