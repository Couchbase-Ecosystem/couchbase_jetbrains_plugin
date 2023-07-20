// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Statement extends PsiElement {

  @Nullable
  DclStatement getDclStatement();

  @Nullable
  DdlStatement getDdlStatement();

  @Nullable
  DmlStatement getDmlStatement();

  @Nullable
  DqlStatement getDqlStatement();

  @Nullable
  TclStatement getTclStatement();

  @Nullable
  UtilityStatement getUtilityStatement();

}
