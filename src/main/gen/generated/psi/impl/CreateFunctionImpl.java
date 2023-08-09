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

public class CreateFunctionImpl extends SqlppPSIWrapper implements CreateFunction {

  public CreateFunctionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitCreateFunction(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public CreateFunctionExternal getCreateFunctionExternal() {
    return findChildByClass(CreateFunctionExternal.class);
  }

  @Override
  @Nullable
  public CreateFunctionInline getCreateFunctionInline() {
    return findChildByClass(CreateFunctionInline.class);
  }

}
