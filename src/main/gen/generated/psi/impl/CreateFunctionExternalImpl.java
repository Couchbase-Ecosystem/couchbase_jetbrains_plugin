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

public class CreateFunctionExternalImpl extends ASTWrapperPsiElement implements CreateFunctionExternal {

  public CreateFunctionExternalImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitCreateFunctionExternal(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public FunctionRef getFunctionRef() {
    return findNotNullChildByClass(FunctionRef.class);
  }

  @Override
  @NotNull
  public Obj getObj() {
    return findNotNullChildByClass(Obj.class);
  }

  @Override
  @Nullable
  public Params getParams() {
    return findChildByClass(Params.class);
  }

}
