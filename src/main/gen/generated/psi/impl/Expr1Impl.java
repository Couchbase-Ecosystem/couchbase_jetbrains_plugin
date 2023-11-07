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
import generated.psi.cblite.*;

public class Expr1Impl extends SqlppPSIWrapper implements Expr1 {

  public Expr1Impl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitExpr1(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<Expr0> getExpr0List() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Expr0.class);
  }

  @Override
  @NotNull
  public List<OpPrec1> getOpPrec1List() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, OpPrec1.class);
  }

}
