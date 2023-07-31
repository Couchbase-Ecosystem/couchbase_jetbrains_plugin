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

public class DropStatementImpl extends SqlppPSIWrapper implements DropStatement {

  public DropStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitDropStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public DropCollection getDropCollection() {
    return findChildByClass(DropCollection.class);
  }

  @Override
  @Nullable
  public DropFunction getDropFunction() {
    return findChildByClass(DropFunction.class);
  }

  @Override
  @Nullable
  public DropIndex getDropIndex() {
    return findChildByClass(DropIndex.class);
  }

  @Override
  @Nullable
  public DropPrimaryIndex getDropPrimaryIndex() {
    return findChildByClass(DropPrimaryIndex.class);
  }

  @Override
  @Nullable
  public DropScope getDropScope() {
    return findChildByClass(DropScope.class);
  }

}
