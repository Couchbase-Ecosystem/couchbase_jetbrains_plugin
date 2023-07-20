// This is a generated file. Not intended for manual editing.
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import generated.psi.*;

public class SelectStatementImpl extends ASTWrapperPsiElement implements SelectStatement {

  public SelectStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitSelectStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LimitClause getLimitClause() {
    return findChildByClass(LimitClause.class);
  }

  @Override
  @Nullable
  public OffsetClause getOffsetClause() {
    return findChildByClass(OffsetClause.class);
  }

  @Override
  @Nullable
  public OrderByClause getOrderByClause() {
    return findChildByClass(OrderByClause.class);
  }

  @Override
  @NotNull
  public List<SelectTerm> getSelectTermList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SelectTerm.class);
  }

  @Override
  @NotNull
  public List<SetOp> getSetOpList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SetOp.class);
  }

}
