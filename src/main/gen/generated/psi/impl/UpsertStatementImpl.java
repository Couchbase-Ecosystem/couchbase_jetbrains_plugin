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

public class UpsertStatementImpl extends ASTWrapperPsiElement implements UpsertStatement {

  public UpsertStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitUpsertStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public InsertSelect getInsertSelect() {
    return findChildByClass(InsertSelect.class);
  }

  @Override
  @Nullable
  public InsertValues getInsertValues() {
    return findChildByClass(InsertValues.class);
  }

  @Override
  @Nullable
  public ReturningClause getReturningClause() {
    return findChildByClass(ReturningClause.class);
  }

  @Override
  @NotNull
  public TargetKeyspace getTargetKeyspace() {
    return findNotNullChildByClass(TargetKeyspace.class);
  }

}
