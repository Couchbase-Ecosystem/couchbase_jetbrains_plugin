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

public class Expr9Impl extends SqlppPSIWrapper implements Expr9 {

  public Expr9Impl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull Visitor visitor) {
    visitor.visitExpr9(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public CollateSuffix getCollateSuffix() {
    return findChildByClass(CollateSuffix.class);
  }

  @Override
  @NotNull
  public Expr8 getExpr8() {
    return findNotNullChildByClass(Expr8.class);
  }

}
