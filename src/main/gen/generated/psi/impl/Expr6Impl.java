// This is a generated file. Not intended for manual editing.
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.cblite.GeneratedTypes.*;
import org.intellij.sdk.language.psi.SqlppPSIWrapper;
import generated.psi.*;

public class Expr6Impl extends SqlppPSIWrapper implements Expr6 {

  public Expr6Impl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull Visitor visitor) {
    visitor.visitExpr6(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public BetweenExpression getBetweenExpression() {
    return findChildByClass(BetweenExpression.class);
  }

  @Override
  @Nullable
  public Expr5 getExpr5() {
    return findChildByClass(Expr5.class);
  }

  @Override
  @Nullable
  public InExpression getInExpression() {
    return findChildByClass(InExpression.class);
  }

  @Override
  @Nullable
  public LikeExpression getLikeExpression() {
    return findChildByClass(LikeExpression.class);
  }

  @Override
  @NotNull
  public List<OpPrec6> getOpPrec6List() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, OpPrec6.class);
  }

  @Override
  @Nullable
  public PostOpPrec6 getPostOpPrec6() {
    return findChildByClass(PostOpPrec6.class);
  }

}
