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

public class JsonHintImpl extends ASTWrapperPsiElement implements JsonHint {

  public JsonHintImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitJsonHint(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public FtsHintJson getFtsHintJson() {
    return findChildByClass(FtsHintJson.class);
  }

  @Override
  @Nullable
  public GsiHintJson getGsiHintJson() {
    return findChildByClass(GsiHintJson.class);
  }

  @Override
  @Nullable
  public HashHintJson getHashHintJson() {
    return findChildByClass(HashHintJson.class);
  }

  @Override
  @Nullable
  public NlHintJson getNlHintJson() {
    return findChildByClass(NlHintJson.class);
  }

  @Override
  @Nullable
  public OrderedHintJson getOrderedHintJson() {
    return findChildByClass(OrderedHintJson.class);
  }

}
