// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface DmlStatement extends PsiElement {

  @Nullable
  Delete getDelete();

  @Nullable
  Insert getInsert();

  @Nullable
  Merge getMerge();

  @Nullable
  Update getUpdate();

  @Nullable
  Upsert getUpsert();

}
