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

public class OtherStatementImpl extends ASTWrapperPsiElement implements OtherStatement {

  public OtherStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitOtherStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public AlterIndex getAlterIndex() {
    return findChildByClass(AlterIndex.class);
  }

  @Override
  @Nullable
  public BuildIndex getBuildIndex() {
    return findChildByClass(BuildIndex.class);
  }

  @Override
  @Nullable
  public ExecuteFunction getExecuteFunction() {
    return findChildByClass(ExecuteFunction.class);
  }

}
