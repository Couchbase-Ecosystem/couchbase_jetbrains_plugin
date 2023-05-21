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

public class ExprImpl extends ASTWrapperPsiElement implements Expr {

  public ExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ArithmeticTerm getArithmeticTerm() {
    return findChildByClass(ArithmeticTerm.class);
  }

  @Override
  @Nullable
  public CaseExpr getCaseExpr() {
    return findChildByClass(CaseExpr.class);
  }

  @Override
  @Nullable
  public CollectionExpr getCollectionExpr() {
    return findChildByClass(CollectionExpr.class);
  }

  @Override
  @Nullable
  public ComparisonTerm getComparisonTerm() {
    return findChildByClass(ComparisonTerm.class);
  }

  @Override
  @Nullable
  public ConcatenationTerm getConcatenationTerm() {
    return findChildByClass(ConcatenationTerm.class);
  }

  @Override
  @Nullable
  public Expr getExpr() {
    return findChildByClass(Expr.class);
  }

  @Override
  @Nullable
  public FunctionCall getFunctionCall() {
    return findChildByClass(FunctionCall.class);
  }

  @Override
  @Nullable
  public Identifier getIdentifier() {
    return findChildByClass(Identifier.class);
  }

  @Override
  @Nullable
  public Literal getLiteral() {
    return findChildByClass(Literal.class);
  }

  @Override
  @Nullable
  public LogicalTerm getLogicalTerm() {
    return findChildByClass(LogicalTerm.class);
  }

  @Override
  @Nullable
  public NestedExpr getNestedExpr() {
    return findChildByClass(NestedExpr.class);
  }

  @Override
  @Nullable
  public SubqueryExpr getSubqueryExpr() {
    return findChildByClass(SubqueryExpr.class);
  }

}
