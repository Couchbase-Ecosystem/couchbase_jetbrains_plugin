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

public class CreateIndexImpl extends ASTWrapperPsiElement implements CreateIndex {

  public CreateIndexImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitCreateIndex(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<IndexKey> getIndexKeyList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, IndexKey.class);
  }

  @Override
  @NotNull
  public IndexName getIndexName() {
    return findNotNullChildByClass(IndexName.class);
  }

  @Override
  @Nullable
  public IndexPartition getIndexPartition() {
    return findChildByClass(IndexPartition.class);
  }

  @Override
  @Nullable
  public IndexUsing getIndexUsing() {
    return findChildByClass(IndexUsing.class);
  }

  @Override
  @Nullable
  public IndexWith getIndexWith() {
    return findChildByClass(IndexWith.class);
  }

  @Override
  @NotNull
  public List<KeyAttribs> getKeyAttribsList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, KeyAttribs.class);
  }

  @Override
  @NotNull
  public KeyspaceRef getKeyspaceRef() {
    return findNotNullChildByClass(KeyspaceRef.class);
  }

  @Override
  @Nullable
  public LeadKeyAttribs getLeadKeyAttribs() {
    return findChildByClass(LeadKeyAttribs.class);
  }

  @Override
  @Nullable
  public WhereClause getWhereClause() {
    return findChildByClass(WhereClause.class);
  }

}
