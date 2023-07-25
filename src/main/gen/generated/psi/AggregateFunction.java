// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface AggregateFunction extends PsiElement {

  @NotNull
  AggregateFunctionName getAggregateFunctionName();

  @Nullable
  AggregateQuantifier getAggregateQuantifier();

  @Nullable
  Expr getExpr();

  @Nullable
  FilterClause getFilterClause();

  @Nullable
  OverClause getOverClause();

  @Nullable
  Path getPath();

}
