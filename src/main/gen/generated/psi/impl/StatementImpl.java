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

public class StatementImpl extends ASTWrapperPsiElement implements Statement {

  public StatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public DclStatement getDclStatement() {
    return findChildByClass(DclStatement.class);
  }

  @Override
  @Nullable
  public DdlStatement getDdlStatement() {
    return findChildByClass(DdlStatement.class);
  }

  @Override
  @Nullable
  public DmlStatement getDmlStatement() {
    return findChildByClass(DmlStatement.class);
  }

  @Override
  @Nullable
  public DqlStatement getDqlStatement() {
    return findChildByClass(DqlStatement.class);
  }

  @Override
  @Nullable
  public TclStatement getTclStatement() {
    return findChildByClass(TclStatement.class);
  }

  @Override
  @Nullable
  public UtilityStatement getUtilityStatement() {
    return findChildByClass(UtilityStatement.class);
  }

}
