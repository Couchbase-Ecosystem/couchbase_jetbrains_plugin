// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Expr extends PsiElement {

  @NotNull
  List<ArithmeticTerm> getArithmeticTermList();

  @NotNull
  List<CaseExpr> getCaseExprList();

  @NotNull
  List<CollectionExpr> getCollectionExprList();

  @NotNull
  List<ComparisonTerm> getComparisonTermList();

  @NotNull
  List<ConcatenationTerm> getConcatenationTermList();

  @Nullable
  Expr getExpr();

  @NotNull
  List<FunctionCall> getFunctionCallList();

  @Nullable
  IdentifierRef getIdentifierRef();

  @Nullable
  Literal getLiteral();

  @NotNull
  List<LogicalTerm> getLogicalTermList();

  @NotNull
  List<NestedExpr> getNestedExprList();

  @NotNull
  List<SubqueryExpr> getSubqueryExprList();

}
