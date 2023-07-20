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

public class DropCollectionImpl extends ASTWrapperPsiElement implements DropCollection {

  public DropCollectionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitDropCollection(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public BucketRef getBucketRef() {
    return findChildByClass(BucketRef.class);
  }

  @Override
  @NotNull
  public CollectionRef getCollectionRef() {
    return findNotNullChildByClass(CollectionRef.class);
  }

  @Override
  @Nullable
  public NamespaceRef getNamespaceRef() {
    return findChildByClass(NamespaceRef.class);
  }

  @Override
  @Nullable
  public ScopeRef getScopeRef() {
    return findChildByClass(ScopeRef.class);
  }

}
