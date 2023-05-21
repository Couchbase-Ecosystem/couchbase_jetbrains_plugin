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

public class CreateCollectionImpl extends ASTWrapperPsiElement implements CreateCollection {

  public CreateCollectionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitCreateCollection(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public Bucket getBucket() {
    return findChildByClass(Bucket.class);
  }

  @Override
  @NotNull
  public Collection getCollection() {
    return findNotNullChildByClass(Collection.class);
  }

  @Override
  @Nullable
  public Namespace getNamespace() {
    return findChildByClass(Namespace.class);
  }

  @Override
  @Nullable
  public Scope getScope() {
    return findChildByClass(Scope.class);
  }

}
