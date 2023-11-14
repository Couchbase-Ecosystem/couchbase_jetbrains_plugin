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

public class Expr5Impl extends SqlppPSIWrapper implements Expr5 {

  public Expr5Impl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitExpr5(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<Expr4> getExpr4List() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Expr4.class);
  }

  @Override
  @NotNull
  public List<OpPrec5> getOpPrec5List() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, OpPrec5.class);
  }

}
