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

public class AnsiJoinRhsImpl extends ASTWrapperPsiElement implements AnsiJoinRhs {

  public AnsiJoinRhsImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitAnsiJoinRhs(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public RhsGeneric getRhsGeneric() {
    return findChildByClass(RhsGeneric.class);
  }

  @Override
  @Nullable
  public RhsKeyspace getRhsKeyspace() {
    return findChildByClass(RhsKeyspace.class);
  }

  @Override
  @Nullable
  public RhsSubquery getRhsSubquery() {
    return findChildByClass(RhsSubquery.class);
  }

}
