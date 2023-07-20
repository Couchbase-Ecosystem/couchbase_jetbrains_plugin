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

public class TclStatementImpl extends ASTWrapperPsiElement implements TclStatement {

  public TclStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitTclStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public BeginTransaction getBeginTransaction() {
    return findChildByClass(BeginTransaction.class);
  }

  @Override
  @Nullable
  public CommitTransaction getCommitTransaction() {
    return findChildByClass(CommitTransaction.class);
  }

  @Override
  @Nullable
  public RollbackTransaction getRollbackTransaction() {
    return findChildByClass(RollbackTransaction.class);
  }

  @Override
  @Nullable
  public SavepointStatement getSavepointStatement() {
    return findChildByClass(SavepointStatement.class);
  }

  @Override
  @Nullable
  public SetTransaction getSetTransaction() {
    return findChildByClass(SetTransaction.class);
  }

}
