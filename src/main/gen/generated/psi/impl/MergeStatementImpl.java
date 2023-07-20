// This is a generated file. Not intended for manual editing.
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class MergeStatementImpl extends ASTWrapperPsiElement implements MergeStatement {

  public MergeStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitMergeStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public AnsiMerge getAnsiMerge() {
    return findChildByClass(AnsiMerge.class);
  }

  @Override
  @Nullable
  public LimitClause getLimitClause() {
    return findChildByClass(LimitClause.class);
  }

  @Override
  @Nullable
  public LookupMerge getLookupMerge() {
    return findChildByClass(LookupMerge.class);
  }

  @Override
  @Nullable
  public ReturningClause getReturningClause() {
    return findChildByClass(ReturningClause.class);
  }

}
