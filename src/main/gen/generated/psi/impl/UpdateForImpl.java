// This is a generated file. Not intended for manual editing.
package generated.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static generated.GeneratedTypes.*;
import org.intellij.sdk.language.psi.SqlppPSIWrapper;
import generated.psi.*;

public class UpdateForImpl extends SqlppPSIWrapper implements UpdateFor {

  public UpdateForImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitUpdateFor(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public Cond getCond() {
    return findChildByClass(Cond.class);
  }

  @Override
  @NotNull
  public List<NameVar> getNameVarList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, NameVar.class);
  }

  @Override
  @NotNull
  public List<Path> getPathList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Path.class);
  }

  @Override
  @NotNull
  public List<Var> getVarList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Var.class);
  }

}
