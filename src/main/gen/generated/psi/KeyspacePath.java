// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface KeyspacePath extends PsiElement {

  @NotNull
  BucketRef getBucketRef();

  @Nullable
  CollectionRef getCollectionRef();

  @Nullable
  NamespaceRef getNamespaceRef();

  @Nullable
  ScopeRef getScopeRef();

}
