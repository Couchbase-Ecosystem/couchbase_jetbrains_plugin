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

public class RelationalExprImpl extends SqlppPSIWrapper implements RelationalExpr {

  public RelationalExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitRelationalExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public Expr getExpr() {
    return findChildByClass(Expr.class);
  }

  @Override
  @NotNull
  public List<Literal> getLiteralList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Literal.class);
  }

  @Override
  @NotNull
  public List<NestedExpr> getNestedExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, NestedExpr.class);
  }

}
