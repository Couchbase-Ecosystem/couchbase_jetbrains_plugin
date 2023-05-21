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

public class FromTermsImpl extends ASTWrapperPsiElement implements FromTerms {

  public FromTermsImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitFromTerms(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<CommaSeparatedJoin> getCommaSeparatedJoinList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CommaSeparatedJoin.class);
  }

  @Override
  @Nullable
  public FromGeneric getFromGeneric() {
    return findChildByClass(FromGeneric.class);
  }

  @Override
  @Nullable
  public FromKeyspace getFromKeyspace() {
    return findChildByClass(FromKeyspace.class);
  }

  @Override
  @Nullable
  public FromSubquery getFromSubquery() {
    return findChildByClass(FromSubquery.class);
  }

  @Override
  @NotNull
  public List<JoinClause> getJoinClauseList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JoinClause.class);
  }

  @Override
  @NotNull
  public List<NestClause> getNestClauseList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, NestClause.class);
  }

  @Override
  @NotNull
  public List<UnnestClause> getUnnestClauseList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, UnnestClause.class);
  }

}
