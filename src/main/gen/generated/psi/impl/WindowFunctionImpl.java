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

public class WindowFunctionImpl extends SqlppPSIWrapper implements WindowFunction {

  public WindowFunctionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitWindowFunction(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public OverClause getOverClause() {
    return findNotNullChildByClass(OverClause.class);
  }

  @Override
  @NotNull
  public WindowFunctionArguments getWindowFunctionArguments() {
    return findNotNullChildByClass(WindowFunctionArguments.class);
  }

  @Override
  @NotNull
  public WindowFunctionName getWindowFunctionName() {
    return findNotNullChildByClass(WindowFunctionName.class);
  }

  @Override
  @Nullable
  public WindowFunctionOptions getWindowFunctionOptions() {
    return findChildByClass(WindowFunctionOptions.class);
  }

}
