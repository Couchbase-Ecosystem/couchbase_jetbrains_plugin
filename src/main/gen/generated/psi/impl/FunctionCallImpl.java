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

public class FunctionCallImpl extends ASTWrapperPsiElement implements FunctionCall {

  public FunctionCallImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitFunctionCall(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public AggregateFunction getAggregateFunction() {
    return findChildByClass(AggregateFunction.class);
  }

  @Override
  @Nullable
  public BuiltinFunction getBuiltinFunction() {
    return findChildByClass(BuiltinFunction.class);
  }

  @Override
  @Nullable
  public OrdinaryFunction getOrdinaryFunction() {
    return findChildByClass(OrdinaryFunction.class);
  }

  @Override
  @Nullable
  public WindowFunction getWindowFunction() {
    return findChildByClass(WindowFunction.class);
  }

}
