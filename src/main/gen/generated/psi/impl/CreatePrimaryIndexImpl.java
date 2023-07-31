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

public class CreatePrimaryIndexImpl extends SqlppPSIWrapper implements CreatePrimaryIndex {

  public CreatePrimaryIndexImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitCreatePrimaryIndex(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public IndexName getIndexName() {
    return findChildByClass(IndexName.class);
  }

  @Override
  @Nullable
  public IndexUsing getIndexUsing() {
    return findChildByClass(IndexUsing.class);
  }

  @Override
  @Nullable
  public IndexWith getIndexWith() {
    return findChildByClass(IndexWith.class);
  }

  @Override
  @NotNull
  public KeyspaceRef getKeyspaceRef() {
    return findNotNullChildByClass(KeyspaceRef.class);
  }

}
