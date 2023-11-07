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

public class Expr2Impl extends SqlppPSIWrapper implements Expr2 {

  public Expr2Impl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull Visitor visitor) {
    visitor.visitExpr2(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<Expr1> getExpr1List() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Expr1.class);
  }

  @Override
  @NotNull
  public List<OpPrec2> getOpPrec2List() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, OpPrec2.class);
  }

}
