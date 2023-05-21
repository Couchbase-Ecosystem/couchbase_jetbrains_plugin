// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface CreateIndex extends PsiElement {

  @NotNull
  List<IndexKey> getIndexKeyList();

  @NotNull
  IndexName getIndexName();

  @Nullable
  IndexPartition getIndexPartition();

  @Nullable
  IndexUsing getIndexUsing();

  @Nullable
  IndexWith getIndexWith();

  @NotNull
  List<KeyAttribs> getKeyAttribsList();

  @NotNull
  KeyspaceRef getKeyspaceRef();

  @Nullable
  LeadKeyAttribs getLeadKeyAttribs();

  @Nullable
  WhereClause getWhereClause();

}
