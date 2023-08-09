// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface UpsertStatement extends PsiElement {

  @Nullable
  InsertSelect getInsertSelect();

  @Nullable
  InsertValues getInsertValues();

  @Nullable
  ReturningClause getReturningClause();

  @NotNull
  TargetKeyspace getTargetKeyspace();

}
