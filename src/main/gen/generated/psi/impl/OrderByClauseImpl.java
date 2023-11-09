// This is a generated file. Not intended for manual editing.
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.cblite.GeneratedTypes.*;
import org.intellij.sdk.language.psi.SqlppPSIWrapper;
import generated.psi.cblite.*;

public class OrderByClauseImpl extends SqlppPSIWrapper implements OrderByClause {

  public OrderByClauseImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitOrderByClause(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<OrderingClause> getOrderingClauseList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, OrderingClause.class);
  }

}
