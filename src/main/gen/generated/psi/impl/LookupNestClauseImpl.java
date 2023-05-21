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

public class LookupNestClauseImpl extends ASTWrapperPsiElement implements LookupNestClause {

  public LookupNestClauseImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitLookupNestClause(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public LookupNestPredicate getLookupNestPredicate() {
    return findNotNullChildByClass(LookupNestPredicate.class);
  }

  @Override
  @NotNull
  public LookupNestRhs getLookupNestRhs() {
    return findNotNullChildByClass(LookupNestRhs.class);
  }

  @Override
  @Nullable
  public LookupNestType getLookupNestType() {
    return findChildByClass(LookupNestType.class);
  }

}
