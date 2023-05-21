// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface KeyspacePath extends PsiElement {

  @NotNull
  Bucket getBucket();

  @Nullable
  Collection getCollection();

  @Nullable
  Namespace getNamespace();

  @Nullable
  Scope getScope();

}
