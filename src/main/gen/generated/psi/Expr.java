// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Expr extends PsiElement {

  @Nullable
  ArithmeticTerm getArithmeticTerm();

  @Nullable
  CaseExpr getCaseExpr();

  @Nullable
  CollectionExpr getCollectionExpr();

  @Nullable
  ComparisonTerm getComparisonTerm();

  @Nullable
  ConcatenationTerm getConcatenationTerm();

  @Nullable
  Expr getExpr();

  @Nullable
  FunctionCall getFunctionCall();

  @Nullable
  Identifier getIdentifier();

  @Nullable
  Literal getLiteral();

  @Nullable
  LogicalTerm getLogicalTerm();

  @Nullable
  NestedExpr getNestedExpr();

  @Nullable
  SubqueryExpr getSubqueryExpr();

}
