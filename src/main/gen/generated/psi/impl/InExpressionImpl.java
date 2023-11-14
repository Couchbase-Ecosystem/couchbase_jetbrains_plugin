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

public class InExpressionImpl extends SqlppPSIWrapper implements InExpression {

  public InExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitInExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ArrayLiteral getArrayLiteral() {
    return findChildByClass(ArrayLiteral.class);
  }

  @Override
  @NotNull
  public Expr5 getExpr5() {
    return findNotNullChildByClass(Expr5.class);
  }

  @Override
  @Nullable
  public ParenExpressions getParenExpressions() {
    return findChildByClass(ParenExpressions.class);
  }

  @Override
  @Nullable
  public SubStatement getSubStatement() {
    return findChildByClass(SubStatement.class);
  }

}
