// This is a generated file. Not intended for manual editing.
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import org.intellij.sdk.language.psi.SqlppPSIWrapper;
import generated.psi.*;

public class IndexJoinClauseImpl extends SqlppPSIWrapper implements IndexJoinClause {

  public IndexJoinClauseImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitIndexJoinClause(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public IndexJoinPredicate getIndexJoinPredicate() {
    return findNotNullChildByClass(IndexJoinPredicate.class);
  }

  @Override
  @NotNull
  public IndexJoinRhs getIndexJoinRhs() {
    return findNotNullChildByClass(IndexJoinRhs.class);
  }

  @Override
  @Nullable
  public IndexJoinType getIndexJoinType() {
    return findChildByClass(IndexJoinType.class);
  }

}
