// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface GrantStatement extends PsiElement {

  @NotNull
  List<KeyspaceRef> getKeyspaceRefList();

  @NotNull
  List<Role> getRoleList();

  @NotNull
  List<User> getUserList();

}
