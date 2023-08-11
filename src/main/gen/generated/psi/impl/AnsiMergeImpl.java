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

public class AnsiMergeImpl extends SqlppPSIWrapper implements AnsiMerge {

  public AnsiMergeImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitAnsiMerge(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public AnsiMergeActions getAnsiMergeActions() {
    return findNotNullChildByClass(AnsiMergeActions.class);
  }

  @Override
  @NotNull
  public AnsiMergePredicate getAnsiMergePredicate() {
    return findNotNullChildByClass(AnsiMergePredicate.class);
  }

  @Override
  @NotNull
  public AnsiMergeSource getAnsiMergeSource() {
    return findNotNullChildByClass(AnsiMergeSource.class);
  }

  @Override
  @NotNull
  public TargetKeyspace getTargetKeyspace() {
    return findNotNullChildByClass(TargetKeyspace.class);
  }

  @Override
  @NotNull
  public UseIndexClause getUseIndexClause() {
    return findNotNullChildByClass(UseIndexClause.class);
  }

}
