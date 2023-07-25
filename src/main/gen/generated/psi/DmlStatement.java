// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface DmlStatement extends PsiElement {

  @Nullable
  DeleteStatement getDeleteStatement();

  @Nullable
  InsertStatement getInsertStatement();

  @Nullable
  MergeStatement getMergeStatement();

  @Nullable
  UpdateStatement getUpdateStatement();

  @Nullable
  UpsertStatement getUpsertStatement();

}
