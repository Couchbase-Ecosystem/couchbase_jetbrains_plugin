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

public class UpdateStatementImpl extends ASTWrapperPsiElement implements UpdateStatement {

  public UpdateStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitUpdateStatement(this);
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
  public ReturningClause getReturningClause() {
    return findChildByClass(ReturningClause.class);
  }

  @Override
  @Nullable
  public SetClause getSetClause() {
    return findChildByClass(SetClause.class);
  }

  @Override
  @NotNull
  public TargetKeyspace getTargetKeyspace() {
    return findNotNullChildByClass(TargetKeyspace.class);
  }

  @Override
  @Nullable
  public UnsetClause getUnsetClause() {
    return findChildByClass(UnsetClause.class);
  }

  @Override
  @Nullable
  public UseKeys getUseKeys() {
    return findChildByClass(UseKeys.class);
  }

  @Override
  @Nullable
  public WhereClause getWhereClause() {
    return findChildByClass(WhereClause.class);
  }

}
