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

public class LookupMergeActionsImpl extends ASTWrapperPsiElement implements LookupMergeActions {

  public LookupMergeActionsImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitLookupMergeActions(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LookupMergeInsert getLookupMergeInsert() {
    return findChildByClass(LookupMergeInsert.class);
  }

  @Override
  @Nullable
  public MergeDelete getMergeDelete() {
    return findChildByClass(MergeDelete.class);
  }

  @Override
  @Nullable
  public MergeUpdate getMergeUpdate() {
    return findChildByClass(MergeUpdate.class);
  }

}
