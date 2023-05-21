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

public class ComparisonTermImpl extends ASTWrapperPsiElement implements ComparisonTerm {

  public ComparisonTermImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitComparisonTerm(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public BetweenExpr getBetweenExpr() {
    return findChildByClass(BetweenExpr.class);
  }

  @Override
  @Nullable
  public IsExpr getIsExpr() {
    return findChildByClass(IsExpr.class);
  }

  @Override
  @Nullable
  public LikeExpr getLikeExpr() {
    return findChildByClass(LikeExpr.class);
  }

  @Override
  @Nullable
  public RelationalExpr getRelationalExpr() {
    return findChildByClass(RelationalExpr.class);
  }

}
