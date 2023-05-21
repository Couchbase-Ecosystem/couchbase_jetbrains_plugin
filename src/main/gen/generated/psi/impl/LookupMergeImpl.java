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

public class LookupMergeImpl extends ASTWrapperPsiElement implements LookupMerge {

  public LookupMergeImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitLookupMerge(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public LookupMergeActions getLookupMergeActions() {
    return findNotNullChildByClass(LookupMergeActions.class);
  }

  @Override
  @NotNull
  public LookupMergePredicate getLookupMergePredicate() {
    return findNotNullChildByClass(LookupMergePredicate.class);
  }

  @Override
  @NotNull
  public LookupMergeSource getLookupMergeSource() {
    return findNotNullChildByClass(LookupMergeSource.class);
  }

  @Override
  @NotNull
  public TargetKeyspace getTargetKeyspace() {
    return findNotNullChildByClass(TargetKeyspace.class);
  }

}
