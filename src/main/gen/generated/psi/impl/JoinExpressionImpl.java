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

public class JoinExpressionImpl extends SqlppPSIWrapper implements JoinExpression {

  public JoinExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull Visitor visitor) {
    visitor.visitJoinExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public Expression getExpression() {
    return findChildByClass(Expression.class);
  }

  @Override
  @NotNull
  public IdentifierRef getIdentifierRef() {
    return findNotNullChildByClass(IdentifierRef.class);
  }

  @Override
  @NotNull
  public JoinOperator getJoinOperator() {
    return findNotNullChildByClass(JoinOperator.class);
  }

}
