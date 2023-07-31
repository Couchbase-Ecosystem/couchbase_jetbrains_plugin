// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SelectStatement extends PsiElement {

  @Nullable
  LimitClause getLimitClause();

  @Nullable
  OffsetClause getOffsetClause();

  @Nullable
  OrderByClause getOrderByClause();

  @NotNull
  List<SelectTerm> getSelectTermList();

  @NotNull
  List<SetOp> getSetOpList();

}
