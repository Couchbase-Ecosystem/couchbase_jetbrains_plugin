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
  public Delete getDelete() {
    return findChildByClass(Delete.class);
  }

  @Override
  @Nullable
  public Insert getInsert() {
    return findChildByClass(Insert.class);
  }

  @Override
  @Nullable
  public Merge getMerge() {
    return findChildByClass(Merge.class);
  }

  @Override
  @Nullable
  public Update getUpdate() {
    return findChildByClass(Update.class);
  }

  @Override
  @Nullable
  public Upsert getUpsert() {
    return findChildByClass(Upsert.class);
  }

}
