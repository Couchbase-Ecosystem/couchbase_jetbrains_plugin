// This is a generated file. Not intended for manual editing.
package generated.psi.cblite;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SelectExpression extends PsiElement {

  @NotNull
  Alias getAlias();

  @NotNull
  Expression getExpression();

  @NotNull
  List<JoinExpression> getJoinExpressionList();

}
