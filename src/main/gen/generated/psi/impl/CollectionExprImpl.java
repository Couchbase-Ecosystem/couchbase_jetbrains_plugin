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

public class CollectionExprImpl extends ASTWrapperPsiElement implements CollectionExpr {

  public CollectionExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitCollectionExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ExistsExpr getExistsExpr() {
    return findChildByClass(ExistsExpr.class);
  }

  @Override
  @Nullable
  public InExpr getInExpr() {
    return findChildByClass(InExpr.class);
  }

  @Override
  @Nullable
  public RangeCond getRangeCond() {
    return findChildByClass(RangeCond.class);
  }

  @Override
  @Nullable
  public WithinExpr getWithinExpr() {
    return findChildByClass(WithinExpr.class);
  }

}
