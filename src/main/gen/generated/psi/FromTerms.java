// This is a generated file. Not intended for manual editing.
package generated.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface FromTerms extends PsiElement {

  @NotNull
  List<CommaSeparatedJoin> getCommaSeparatedJoinList();

  @Nullable
  FromGeneric getFromGeneric();

  @Nullable
  FromKeyspace getFromKeyspace();

  @Nullable
  FromSubquery getFromSubquery();

  @NotNull
  List<JoinClause> getJoinClauseList();

  @NotNull
  List<NestClause> getNestClauseList();

  @NotNull
  List<UnnestClause> getUnnestClauseList();

}
