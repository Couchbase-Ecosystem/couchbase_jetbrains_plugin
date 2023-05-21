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

public class UnsetClauseImpl extends ASTWrapperPsiElement implements UnsetClause {

  public UnsetClauseImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitUnsetClause(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<Path> getPathList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Path.class);
  }

  @Override
  @NotNull
  public List<UpdateFor> getUpdateForList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, UpdateFor.class);
  }

}
