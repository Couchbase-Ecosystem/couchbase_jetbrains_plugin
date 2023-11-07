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

public class Expr4Impl extends SqlppPSIWrapper implements Expr4 {

  public Expr4Impl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitExpr4(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<Expr3> getExpr3List() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Expr3.class);
  }

  @Override
  @NotNull
  public List<OpPrec4> getOpPrec4List() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, OpPrec4.class);
  }

}
