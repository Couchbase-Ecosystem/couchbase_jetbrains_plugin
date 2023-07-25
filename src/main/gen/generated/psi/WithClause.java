// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface WithClause extends PsiElement {

  @NotNull
  List<Alias> getAliasList();

  @NotNull
  List<Expr> getExprList();

  @NotNull
  List<SelectStatement> getSelectStatementList();

}
