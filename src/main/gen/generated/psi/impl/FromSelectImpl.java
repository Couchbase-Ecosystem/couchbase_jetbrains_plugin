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

public class FromSelectImpl extends SqlppPSIWrapper implements FromSelect {

  public FromSelectImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitFromSelect(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public FromClause getFromClause() {
    return findNotNullChildByClass(FromClause.class);
  }

  @Override
  @Nullable
  public GroupByClause getGroupByClause() {
    return findChildByClass(GroupByClause.class);
  }

  @Override
  @Nullable
  public LetClause getLetClause() {
    return findChildByClass(LetClause.class);
  }

  @Override
  @NotNull
  public SelectClause getSelectClause() {
    return findNotNullChildByClass(SelectClause.class);
  }

  @Override
  @Nullable
  public WhereClause getWhereClause() {
    return findChildByClass(WhereClause.class);
  }

  @Override
  @Nullable
  public WindowClause getWindowClause() {
    return findChildByClass(WindowClause.class);
  }

  @Override
  @Nullable
  public WithClause getWithClause() {
    return findChildByClass(WithClause.class);
  }

}
