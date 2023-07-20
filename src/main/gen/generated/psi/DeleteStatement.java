// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface DeleteStatement extends PsiElement {

  @Nullable
  LimitClause getLimitClause();

  @Nullable
  ReturningClause getReturningClause();

  @NotNull
  TargetKeyspace getTargetKeyspace();

  @Nullable
  UseKeysClause getUseKeysClause();

  @Nullable
  WhereClause getWhereClause();

}
