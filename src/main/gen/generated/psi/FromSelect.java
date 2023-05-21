// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface FromSelect extends PsiElement {

  @NotNull
  FromClause getFromClause();

  @Nullable
  GroupByClause getGroupByClause();

  @Nullable
  LetClause getLetClause();

  @NotNull
  SelectClause getSelectClause();

  @Nullable
  WhereClause getWhereClause();

  @Nullable
  WindowClause getWindowClause();

  @Nullable
  WithClause getWithClause();

}
