// This is a generated file. Not intended for manual editing.
package generated.psi.cblite;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Expr6 extends PsiElement {

  @Nullable
  BetweenExpression getBetweenExpression();

  @Nullable
  Expr5 getExpr5();

  @Nullable
  InExpression getInExpression();

  @Nullable
  LikeExpression getLikeExpression();

  @NotNull
  List<OpPrec6> getOpPrec6List();

  @Nullable
  PostOpPrec6 getPostOpPrec6();

}
