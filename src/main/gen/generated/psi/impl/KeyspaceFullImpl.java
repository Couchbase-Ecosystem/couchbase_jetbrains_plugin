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

public class KeyspaceFullImpl extends ASTWrapperPsiElement implements KeyspaceFull {

  public KeyspaceFullImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitKeyspaceFull(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public BucketRef getBucketRef() {
    return findNotNullChildByClass(BucketRef.class);
  }

  @Override
  @NotNull
  public CollectionRef getCollectionRef() {
    return findNotNullChildByClass(CollectionRef.class);
  }

  @Override
  @NotNull
  public NamespaceRef getNamespaceRef() {
    return findNotNullChildByClass(NamespaceRef.class);
  }

  @Override
  @NotNull
  public ScopeRef getScopeRef() {
    return findNotNullChildByClass(ScopeRef.class);
  }

}
