// This is a generated file. Not intended for manual editing.
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import org.intellij.sdk.language.psi.SqlppPSIWrapper;
import generated.psi.*;

public class ExprImpl extends SqlppPSIWrapper implements Expr {

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
  @NotNull
  public List<ArithmeticTerm> getArithmeticTermList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ArithmeticTerm.class);
  }

  @Override
  @Nullable
  public CaseExpr getCaseExpr() {
    return findChildByClass(CaseExpr.class);
  }

  @Override
  @NotNull
  public List<CollectionExpr> getCollectionExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CollectionExpr.class);
  }

  @Override
  @NotNull
  public List<ComparisonTerm> getComparisonTermList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ComparisonTerm.class);
  }

  @Override
  @NotNull
  public List<ConcatenationTerm> getConcatenationTermList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ConcatenationTerm.class);
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
  public IdentifierRef getIdentifierRef() {
    return findChildByClass(IdentifierRef.class);
  }

  @Override
  @Nullable
  public JsonValue getJsonValue() {
    return findChildByClass(JsonValue.class);
  }

  @Override
  @Nullable
  public Literal getLiteral() {
    return findChildByClass(Literal.class);
  }

  @Override
  @NotNull
  public List<LogicalTerm> getLogicalTermList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LogicalTerm.class);
  }

  @Override
  @Nullable
  public NamedArg getNamedArg() {
    return findChildByClass(NamedArg.class);
  }

  @Override
  @Nullable
  public NestedExpr getNestedExpr() {
    return findChildByClass(NestedExpr.class);
  }

  @Override
  @Nullable
  public NewArrayExpr getNewArrayExpr() {
    return findChildByClass(NewArrayExpr.class);
  }

  @Override
  @Nullable
  public Path getPath() {
    return findChildByClass(Path.class);
  }

  @Override
  @Nullable
  public SubqueryExpr getSubqueryExpr() {
    return findChildByClass(SubqueryExpr.class);
  }

}
