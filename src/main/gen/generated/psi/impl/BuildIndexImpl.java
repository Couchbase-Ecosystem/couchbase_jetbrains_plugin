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

public class BuildIndexImpl extends ASTWrapperPsiElement implements BuildIndex {

  public BuildIndexImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitBuildIndex(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<IndexTerm> getIndexTermList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, IndexTerm.class);
  }

  @Override
  @Nullable
  public IndexUsing getIndexUsing() {
    return findChildByClass(IndexUsing.class);
  }

  @Override
  @NotNull
  public KeyspaceRef getKeyspaceRef() {
    return findNotNullChildByClass(KeyspaceRef.class);
  }

}
