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

public class AnsiJoinHintsImpl extends ASTWrapperPsiElement implements AnsiJoinHints {

  public AnsiJoinHintsImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitAnsiJoinHints(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public MultipleHints getMultipleHints() {
    return findChildByClass(MultipleHints.class);
  }

  @Override
  @Nullable
  public UseHashHint getUseHashHint() {
    return findChildByClass(UseHashHint.class);
  }

  @Override
  @Nullable
  public UseNlHint getUseNlHint() {
    return findChildByClass(UseNlHint.class);
  }

}
