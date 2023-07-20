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

public class RevokeStatementImpl extends ASTWrapperPsiElement implements RevokeStatement {

  public RevokeStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitRevokeStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<KeyspaceRef> getKeyspaceRefList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, KeyspaceRef.class);
  }

  @Override
  @NotNull
  public List<Role> getRoleList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Role.class);
  }

  @Override
  @NotNull
  public List<User> getUserList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, User.class);
  }

}
