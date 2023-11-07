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
import generated.psi.*;

public class SelectStatementImpl extends SqlppPSIWrapper implements SelectStatement {

  public SelectStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
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
  public FromClause getFromClause() {
    return findChildByClass(FromClause.class);
  }

  @Override
  @Nullable
  public GroupByClause getGroupByClause() {
    return findChildByClass(GroupByClause.class);
  }

  @Override
  @Nullable
  public HavingClause getHavingClause() {
    return findChildByClass(HavingClause.class);
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
  public SelectResults getSelectResults() {
    return findNotNullChildByClass(SelectResults.class);
  }

  @Override
  @Nullable
  public WhereClause getWhereClause() {
    return findChildByClass(WhereClause.class);
  }

}
