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

public class Expr7Impl extends SqlppPSIWrapper implements Expr7 {

  public Expr7Impl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull Visitor visitor) {
    visitor.visitExpr7(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<Expr6> getExpr6List() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Expr6.class);
  }

  @Override
  @NotNull
  public List<OpPrec7> getOpPrec7List() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, OpPrec7.class);
  }

}
