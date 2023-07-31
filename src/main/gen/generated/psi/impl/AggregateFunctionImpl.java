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

public class AggregateFunctionImpl extends SqlppPSIWrapper implements AggregateFunction {

  public AggregateFunctionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitAggregateFunction(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public AggregateFunctionName getAggregateFunctionName() {
    return findNotNullChildByClass(AggregateFunctionName.class);
  }

  @Override
  @Nullable
  public AggregateQuantifier getAggregateQuantifier() {
    return findChildByClass(AggregateQuantifier.class);
  }

  @Override
  @Nullable
  public Expr getExpr() {
    return findChildByClass(Expr.class);
  }

  @Override
  @Nullable
  public FilterClause getFilterClause() {
    return findChildByClass(FilterClause.class);
  }

  @Override
  @Nullable
  public OverClause getOverClause() {
    return findChildByClass(OverClause.class);
  }

  @Override
  @Nullable
  public Path getPath() {
    return findChildByClass(Path.class);
  }

}
