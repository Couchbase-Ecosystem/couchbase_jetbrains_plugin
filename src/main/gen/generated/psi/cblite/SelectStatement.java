// This is a generated file. Not intended for manual editing.
package generated.psi.cblite;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SelectStatement extends PsiElement {

  @Nullable
  FromClause getFromClause();

  @Nullable
  GroupByClause getGroupByClause();

  @Nullable
  HavingClause getHavingClause();

  @Nullable
  LimitClause getLimitClause();

  @Nullable
  OffsetClause getOffsetClause();

  @Nullable
  OrderByClause getOrderByClause();

  @NotNull
  SelectResults getSelectResults();

  @Nullable
  WhereClause getWhereClause();

}
