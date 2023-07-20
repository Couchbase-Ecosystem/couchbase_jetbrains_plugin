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

public class DmlStatementImpl extends ASTWrapperPsiElement implements DmlStatement {

  public DmlStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitDmlStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public DeleteStatement getDeleteStatement() {
    return findChildByClass(DeleteStatement.class);
  }

  @Override
  @Nullable
  public InsertStatement getInsertStatement() {
    return findChildByClass(InsertStatement.class);
  }

  @Override
  @Nullable
  public MergeStatement getMergeStatement() {
    return findChildByClass(MergeStatement.class);
  }

  @Override
  @Nullable
  public UpdateStatement getUpdateStatement() {
    return findChildByClass(UpdateStatement.class);
  }

  @Override
  @Nullable
  public UpsertStatement getUpsertStatement() {
    return findChildByClass(UpsertStatement.class);
  }

}
