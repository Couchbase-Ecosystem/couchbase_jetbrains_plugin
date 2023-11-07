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

public class BaseExpressionImpl extends SqlppPSIWrapper implements BaseExpression {

  public BaseExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitBaseExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public AnyEveryExpression getAnyEveryExpression() {
    return findChildByClass(AnyEveryExpression.class);
  }

  @Override
  @Nullable
  public ArrayLiteral getArrayLiteral() {
    return findChildByClass(ArrayLiteral.class);
  }

  @Override
  @Nullable
  public BaseExpression getBaseExpression() {
    return findChildByClass(BaseExpression.class);
  }

  @Override
  @Nullable
  public CaseExpression getCaseExpression() {
    return findChildByClass(CaseExpression.class);
  }

  @Override
  @Nullable
  public DictLiteral getDictLiteral() {
    return findChildByClass(DictLiteral.class);
  }

  @Override
  @Nullable
  public Expression getExpression() {
    return findChildByClass(Expression.class);
  }

  @Override
  @Nullable
  public FunctionCall getFunctionCall() {
    return findChildByClass(FunctionCall.class);
  }

  @Override
  @Nullable
  public IdentifierRef getIdentifierRef() {
    return findChildByClass(IdentifierRef.class);
  }

  @Override
  @Nullable
  public Literal getLiteral() {
    return findChildByClass(Literal.class);
  }

  @Override
  @Nullable
  public OpPrefix getOpPrefix() {
    return findChildByClass(OpPrefix.class);
  }

  @Override
  @Nullable
  public Property getProperty() {
    return findChildByClass(Property.class);
  }

  @Override
  @Nullable
  public SelectExpression getSelectExpression() {
    return findChildByClass(SelectExpression.class);
  }

}
