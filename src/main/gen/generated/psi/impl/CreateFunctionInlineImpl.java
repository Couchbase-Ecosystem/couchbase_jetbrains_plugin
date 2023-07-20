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

public class CreateFunctionInlineImpl extends ASTWrapperPsiElement implements CreateFunctionInline {

  public CreateFunctionInlineImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitCreateFunctionInline(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public Body getBody() {
    return findNotNullChildByClass(Body.class);
  }

  @Override
  @NotNull
  public FunctionRef getFunctionRef() {
    return findNotNullChildByClass(FunctionRef.class);
  }

  @Override
  @Nullable
  public Params getParams() {
    return findChildByClass(Params.class);
  }

}
