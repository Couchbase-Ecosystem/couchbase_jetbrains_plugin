// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface UpdateStatement extends PsiElement {

  @Nullable
  LimitClause getLimitClause();

  @Nullable
  ReturningClause getReturningClause();

  @Nullable
  SetClause getSetClause();

  @NotNull
  TargetKeyspace getTargetKeyspace();

  @Nullable
  UnsetClause getUnsetClause();

  @Nullable
  UseKeys getUseKeys();

  @Nullable
  WhereClause getWhereClause();

}
