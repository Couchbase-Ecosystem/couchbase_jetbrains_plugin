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

public class CreateStatementImpl extends ASTWrapperPsiElement implements CreateStatement {

  public CreateStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitCreateStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public CreateCollection getCreateCollection() {
    return findChildByClass(CreateCollection.class);
  }

  @Override
  @Nullable
  public CreateFunction getCreateFunction() {
    return findChildByClass(CreateFunction.class);
  }

  @Override
  @Nullable
  public CreateIndex getCreateIndex() {
    return findChildByClass(CreateIndex.class);
  }

  @Override
  @Nullable
  public CreatePrimaryIndex getCreatePrimaryIndex() {
    return findChildByClass(CreatePrimaryIndex.class);
  }

  @Override
  @Nullable
  public CreateScope getCreateScope() {
    return findChildByClass(CreateScope.class);
  }

}
