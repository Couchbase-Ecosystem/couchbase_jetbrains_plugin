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

public class SimpleHintImpl extends ASTWrapperPsiElement implements SimpleHint {

  public SimpleHintImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitSimpleHint(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public FtsHintSimple getFtsHintSimple() {
    return findChildByClass(FtsHintSimple.class);
  }

  @Override
  @Nullable
  public GsiHintSimple getGsiHintSimple() {
    return findChildByClass(GsiHintSimple.class);
  }

  @Override
  @Nullable
  public HashHintSimple getHashHintSimple() {
    return findChildByClass(HashHintSimple.class);
  }

  @Override
  @Nullable
  public NlHintSimple getNlHintSimple() {
    return findChildByClass(NlHintSimple.class);
  }

  @Override
  @Nullable
  public OrderedHintSimple getOrderedHintSimple() {
    return findChildByClass(OrderedHintSimple.class);
  }

}
